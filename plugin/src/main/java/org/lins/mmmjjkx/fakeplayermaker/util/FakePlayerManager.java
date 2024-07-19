package org.lins.mmmjjkx.fakeplayermaker.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFakePlayerManager;
import org.lins.mmmjjkx.fakeplayermaker.objects.CodecHelperMethod;
import org.lins.mmmjjkx.fakeplayermaker.objects.MCClient;
import org.lins.mmmjjkx.fakeplayermaker.objects.wrapped.WrappedSession;

import java.time.Instant;
import java.util.*;

public class FakePlayerManager implements IFakePlayerManager {
    public static final Class<?> chatPacketClass = CommonUtils.getClass(
            "com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket",
            "org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatPacket"
    );

    private static final Class<?> chatCommandPacketClass = CommonUtils.getClass(
            "com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket",
            "org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket"
    );

    private final Map<String, MCClient> clients;

    public FakePlayerManager() {
        clients = new HashMap<>();

        clients.putAll(FPMRecoded.fakePlayerSaver.getFakePlayers());

        tryAutoJoin();
    }

    private void tryAutoJoin() {
        if (!FPMRecoded.getSettingValues().joinIfOwnerJoin()) {
            for (MCClient client : clients.values()) {
                join(client);
            }
        }
    }

    @Override
    public @NotNull IFPMPlayer create(UUID owner, String name) {
        String ip = FPMRecoded.INSTANCE.getConfig().getString("entrance.ip", "127.0.0.1");
        int port = FPMRecoded.INSTANCE.getConfig().getInt("entrance.port", 25565);
        UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());
        MCClient client = new MCClient(ip, port, owner, CommonUtils.getUnAllocatedIPPort(), new ImmutablePair<>(name, uuid));
        clients.put(name, client);
        return client;
    }

    @Override
    public @NotNull IFPMPlayer createAndSave(UUID owner, String name) {
        IFPMPlayer player = create(owner, name);
        FPMRecoded.fakePlayerSaver.saveFakePlayer(player);
        return player;
    }

    @Override
    public @Nullable IFPMPlayer get(String name) {
        if (name == null) {
            return null;
        }

        return clients.get(name);
    }

    @Override
    public @NotNull Pair<Boolean, IFPMPlayer> getExactly(String name) {
        return new ImmutablePair<>(Bukkit.getPlayer(name) != null, clients.get(name));
    }

    @Override
    public void join(IFPMPlayer player) {
        join(player, () -> {});
    }

    @Override
    public void join(String name) {
        join(name, () -> {});
    }

    @Override
    public void join(String name, Runnable callback) {
        MCClient client = clients.get(name);
        if (client != null) {
            join(client, callback);
        }
    }

    @Override
    public void join(IFPMPlayer player, Runnable callback) {
        MCClient client = (MCClient) player;
        client.connect(session -> {
            actions("Join", client, session);
            callback.run();
        });
    }

    @Override
    public List<IFPMPlayer> getFakePlayers(UUID ownerUUID) {
        return new ObjectImmutableList<>(clients.values().stream().filter(client -> client.getOwnerUUID().equals(ownerUUID)).toArray(IFPMPlayer[]::new));
    }

    @Override
    public List<String> getFakePlayerNames() {
        return new ObjectImmutableList<>(clients.keySet());
    }

    @Override
    public void remove(String name) {
        leave(name);
        MCClient client = clients.remove(name);
        CommonUtils.releaseIPPort(client.getBindAddress().getKey(), client.getBindAddress().getValue());
        FPMRecoded.fakePlayerSaver.removeFakePlayer(name);
    }

    @Override
    public void leave(String name) {
        MCClient client = clients.get(name);
        if (client != null) {
            client.disconnect(s -> actions("Quit", client, s));
        }
    }

    private void actions(String key, MCClient client, WrappedSession session) {
        List<Runnable> temp = new ArrayList<>();
        FPMRecoded.INSTANCE.getConfig().getStringList("runCommands.on" + key).forEach(c -> {
            Runnable runnable = () -> {
                String[] split = c.split(" ");
                String command = split.length > 1 ? c.replace(split[0] + " ", "") : split[0];
                String head = split[0];
                OfflinePlayer owner = Bukkit.getOfflinePlayer(client.getOwnerUUID());
                command = command.replaceAll("%fakePlayer%", client.getFakePlayerProfile().name());
                if (owner.getName() != null) {
                    command = command.replaceAll("%owner%", owner.getName());
                }
                String finalCommand = command;
                switch (head) {
                    case "chat" -> {
                        ByteBuf byteBuf = writeBase(finalCommand);
                        byteBuf.writeBoolean(false);
                        byteBuf.writeBytes(new byte[256]);
                        Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_VAR_INT, 0);
                        Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_FIXED_BITSET, new BitSet(20), 20);
                        session.send(Reflections.createPacket(chatPacketClass, byteBuf));
                    }
                    case "console" -> Bukkit.getScheduler().runTask(FPMRecoded.INSTANCE, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand));
                    default -> {
                        ByteBuf byteBuf = writeBase(finalCommand);
                        Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_VAR_INT, 0);
                        Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_VAR_INT, 0);
                        Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_FIXED_BITSET, new BitSet(20), 20);
                        session.send(Reflections.createPacket(chatCommandPacketClass, byteBuf));
                    }
                }
            };
            temp.add(runnable);
        });

        for (int i = 0; i < temp.size(); i++) {
            Runnable future = temp.get(i);
            Bukkit.getScheduler().runTaskLater(FPMRecoded.INSTANCE, future, i * 20L);
        }
    }

    public static ByteBuf writeBase(String str) {
        UnpooledByteBufAllocator allocator = new UnpooledByteBufAllocator(true);
        ByteBuf byteBuf = allocator.compositeDirectBuffer(25);
        Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_STRING, str);
        byteBuf.writeLong(Instant.now().toEpochMilli());
        byteBuf.writeLong(0L);
        return byteBuf;
    }
}
