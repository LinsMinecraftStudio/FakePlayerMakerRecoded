package org.lins.mmmjjkx.fakeplayermaker.util;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.codec.MinecraftCodec;
import com.github.steveice10.mc.protocol.codec.MinecraftCodecHelper;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import com.github.steveice10.packetlib.Session;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
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
import org.lins.mmmjjkx.fakeplayermaker.objects.MCClient;

import java.time.Instant;
import java.util.*;

public class NewFakePlayerManager implements IFakePlayerManager {
    private final Map<String, MCClient> clients;

    public NewFakePlayerManager() {
        clients = new HashMap<>();
    }

    @Override
    public @NotNull IFPMPlayer create(UUID owner, String name) {
        GameProfile profile = new GameProfile(owner, name);
        String ip = FPMRecoded.INSTANCE.getConfig().getString("entrance.ip", "127.0.0.1");
        int port = FPMRecoded.INSTANCE.getConfig().getInt("entrance.port", 60000);
        MCClient client = new MCClient(ip, port, owner, CommonUtils.getUnAllocatedIPPort(), profile);
        clients.put(name, client);
        return client;
    }

    @Override
    public @Nullable IFPMPlayer get(String name) {
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

    private void actions(String key, MCClient client, Session session) {
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
                        ByteBuf byteBuf = new CompositeByteBuf(new PooledByteBufAllocator(), false, 16);
                        MinecraftCodecHelper helper = MinecraftCodec.CODEC.getHelperFactory().get();
                        helper.writeString(byteBuf, finalCommand);
                        byteBuf.writeLong(Instant.now().toEpochMilli());
                        byteBuf.writeLong(0L);
                        byteBuf.writeBoolean(false);
                        byteBuf.writeBytes(new byte[256]);
                        byteBuf.writeInt(0);
                        helper.writeFixedBitSet(byteBuf, new BitSet(finalCommand.length()), finalCommand.length());
                        session.send(new ServerboundChatPacket(byteBuf, helper));
                    }
                    case "console" -> Bukkit.getScheduler().runTask(FPMRecoded.INSTANCE, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand));
                    default -> {
                        ByteBuf byteBuf = new CompositeByteBuf(new PooledByteBufAllocator(), false, 16);
                        MinecraftCodecHelper helper = MinecraftCodec.CODEC.getHelperFactory().get();
                        helper.writeString(byteBuf, finalCommand);
                        byteBuf.writeLong(Instant.now().toEpochMilli());
                        byteBuf.writeLong(0L);
                        byteBuf.writeInt(0);
                        byteBuf.writeInt(0);
                        helper.writeFixedBitSet(byteBuf, new BitSet(finalCommand.length()), finalCommand.length());
                        session.send(new ServerboundChatCommandPacket(byteBuf, helper));
                    }
                }
            };
            temp.add(runnable);
        });

        for (int i = 0; i < temp.size(); i++) {
            Runnable future = temp.get(i);
            //avoid sending too many packets at once
            Bukkit.getScheduler().runTaskLater(FPMRecoded.INSTANCE, future, i * 20L);
        }
    }
}
