package org.lins.mmmjjkx.fakeplayermaker.util;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.game.command.CommandNode;
import com.github.steveice10.mc.protocol.data.game.command.CommandParser;
import com.github.steveice10.mc.protocol.data.game.command.CommandType;
import com.github.steveice10.mc.protocol.data.game.command.properties.StringProperties;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundCommandsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import com.github.steveice10.packetlib.Session;
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
        MCClient client = new MCClient(profile, ip, port, owner, CommonUtils.getUnAllocatedIPPort());
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
            joinActions(client, session);
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
            client.disconnect();
        }
    }

    private void joinActions(MCClient client, Session session) {
        FPMRecoded.INSTANCE.getConfig().getStringList("runCommands.onJoin").forEach(c -> {
            String[] split = c.split(" ");
            String command = split.length > 1 ? c.replace(split[0] + " ", "") : split[0];
            String head = split[0];
            OfflinePlayer owner = Bukkit.getOfflinePlayer(client.getOwnerUUID());
            command = command.replaceAll("%fakePlayer%", client.getFakePlayerProfile().name());
            if (owner.getName() != null) {
                command = command.replaceAll("%owner%", owner.getName());
            }
            switch (head) {
                default -> {
                    CommandNode commandNode = new CommandNode(CommandType.ROOT, true, new int[]{1,2}, OptionalInt.empty(), head, CommandParser.STRING,
                            StringProperties.SINGLE_WORD, null);
                    List<CommandNode> children = new ArrayList<>();
                    for (int i = 1; i < split.length - 1; i++) {
                        String arg = split[i];
                        children.add(new CommandNode(CommandType.ARGUMENT, false, new int[]{0}, OptionalInt.empty(), arg, CommandParser.STRING,
                                StringProperties.SINGLE_WORD, null));
                    }
                    children.add(0, commandNode);
                    session.send(new ClientboundCommandsPacket(children.toArray(new CommandNode[0]), 0));
                }
                case "self" -> {
                    String[] split2 = command.split(" ");
                    String head1 = split2[0];
                    CommandNode commandNode = new CommandNode(CommandType.ROOT, true, new int[]{1,2}, OptionalInt.empty(), head1, CommandParser.STRING,
                            StringProperties.SINGLE_WORD, null);
                    List<CommandNode> children = new ArrayList<>();
                    for (int i = 1; i < split.length - 1; i++) {
                        String arg = split2[i];
                        children.add(new CommandNode(CommandType.ARGUMENT, false, new int[]{0}, OptionalInt.empty(), arg, CommandParser.STRING,
                                StringProperties.SINGLE_WORD, null));
                    }
                    children.add(0, commandNode);
                    session.send(new ClientboundCommandsPacket(children.toArray(new CommandNode[0]), 0));
                }
                case "console" -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                case "chat" -> session.send(new ServerboundChatPacket(command, System.currentTimeMillis(), 1, generateSignature(command), 0, new BitSet()));
            }
        });
    }

    private byte[] generateSignature(String message) {
        byte[] signature = new byte[256];

        for (int i = 0; i < 256; i++) {
            signature[i] = (byte) (i ^ message.charAt(i % message.length()));
        }

        return signature;
    }
}
