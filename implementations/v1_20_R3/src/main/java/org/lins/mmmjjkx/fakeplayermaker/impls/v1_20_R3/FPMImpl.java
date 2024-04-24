package org.lins.mmmjjkx.fakeplayermaker.impls.v1_20_R3;

import com.mojang.authlib.GameProfile;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.BandwidthDebugMonitor;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.SampleLogger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMChannel;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;

import java.util.UUID;

public final class FPMImpl extends FPMImplements {
    private final EventLoop LOOP = new DefaultEventLoop();

    @Override
    public @NotNull Object createPlayer(@NotNull GameProfile profile, @NotNull String levelName, @NotNull UUID owner) {
        World bk = Bukkit.getWorld(levelName);
        ServerLevel world;
        if (bk == null) {
            world = MinecraftServer.getServer().overworld();
        } else {
            world = ((CraftWorld) bk).getHandle();
        }
        return new FPMServerPlayer(MinecraftServer.getServer(), world, profile, owner);
    }

    @Override
    public void setupConnection(@NotNull Object player) {
        FPMChannel channel = new FPMChannel();
        ServerPlayer serverPlayer = (ServerPlayer) player;

        LOOP.register(channel);

        Connection connection = new Connection(PacketFlow.SERVERBOUND);

        channel.attr(Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL).set(ConnectionProtocol.PLAY.codec(PacketFlow.SERVERBOUND));
        channel.attr(Connection.ATTRIBUTE_CLIENTBOUND_PROTOCOL).set(ConnectionProtocol.PLAY.codec(PacketFlow.CLIENTBOUND));

        Connection.configureSerialization(channel.pipeline(), PacketFlow.SERVERBOUND, new BandwidthDebugMonitor(new SampleLogger()));

        connection.channel = channel;

        serverPlayer.connection = new FPMConnection(MinecraftServer.getServer(), connection, serverPlayer);
    }

    @Override
    public void addPlayer(@NotNull Object player) {
        PlayerList playerList = MinecraftServer.getServer().getPlayerList();
        ServerPlayer serverPlayer = (ServerPlayer) player;

        playerList.placeNewPlayer(serverPlayer.connection.connection, serverPlayer, CommonListenerCookie.createInitial(serverPlayer.gameProfile));
    }

    @Override
    public void removePlayer(@NotNull Object player) {
        PlayerList playerList = MinecraftServer.getServer().getPlayerList();
        ServerPlayer serverPlayer = (ServerPlayer) player;

        setupConnection(player);

        playerList.remove(serverPlayer);

        ServerLevel world = serverPlayer.serverLevel();
        ChunkMap chunkMap = world.chunkSource.chunkMap;

        Int2ObjectMap<ChunkMap.TrackedEntity> entityMap = chunkMap.entityMap;
        entityMap.remove(serverPlayer.getId());
    }

    @Override
    public @NotNull GameProfile getGameProfile(@NotNull Object player) {
        return ((ServerPlayer) player).gameProfile;
    }

    @Override
    public Object toNms(@NotNull Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    @Override
    public Player toBukkit(@NotNull Object nmsPlayer) {
        return ((ServerPlayer) nmsPlayer).getBukkitEntity();
    }
}
