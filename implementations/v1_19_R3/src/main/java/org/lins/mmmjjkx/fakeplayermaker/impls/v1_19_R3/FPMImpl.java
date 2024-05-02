package org.lins.mmmjjkx.fakeplayermaker.impls.v1_19_R3;

import com.mojang.authlib.GameProfile;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMChannel;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;
import org.lins.mmmjjkx.fakeplayermaker.commons.IFPMPlayer;

import java.util.UUID;

public final class FPMImpl extends FPMImplements {
    private final EventLoop LOOP = new DefaultEventLoop();

    @Override
    public @NotNull IFPMPlayer createPlayer(@NotNull GameProfile profile, @NotNull String levelName, @NotNull UUID owner) {
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
    public void setupConnection(@NotNull IFPMPlayer player) {
        ServerPlayer serverPlayer = (ServerPlayer) player;

        FPMChannel channel = new FPMChannel();

        LOOP.register(channel);

        FPMNetworkManager connection = new FPMNetworkManager(PacketFlow.SERVERBOUND, channel);

        serverPlayer.connection = new FPMConnection(MinecraftServer.getServer(), connection, serverPlayer);
    }

    @Override
    public void addPlayer(@NotNull IFPMPlayer player) {
        PlayerList playerList = MinecraftServer.getServer().getPlayerList();
        ServerPlayer serverPlayer = (ServerPlayer) player;

        playerList.placeNewPlayer(serverPlayer.connection.connection, serverPlayer);
    }

    @Override
    public void removePlayer(@NotNull IFPMPlayer player) {
        PlayerList playerList = MinecraftServer.getServer().getPlayerList();
        ServerPlayer serverPlayer = (ServerPlayer) player;

        setupConnection(player);

        playerList.remove(serverPlayer);

        ServerLevel world = serverPlayer.getLevel();
        ChunkMap chunkMap = world.chunkSource.chunkMap;

        Int2ObjectMap<ChunkMap.TrackedEntity> entityMap = chunkMap.entityMap;
        entityMap.remove(serverPlayer.getId());
    }

    @Override
    public @NotNull GameProfile getGameProfile(@NotNull IFPMPlayer player) {
        return ((ServerPlayer) player).gameProfile;
    }

    @Override
    public Player toBukkit(@NotNull IFPMPlayer nmsPlayer) {
        return ((ServerPlayer) nmsPlayer).getBukkitEntity();
    }

}
