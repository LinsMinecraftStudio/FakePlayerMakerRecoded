package org.lins.mmmjjkx.fakeplayermaker.impls.v1_20_R1;

import com.mojang.authlib.GameProfile;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.papermc.paper.threadedregions.EntityScheduler;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;
import org.lins.mmmjjkx.fakeplayermaker.commons.FakeChannel;
import org.lins.mmmjjkx.fakeplayermaker.commons.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.commons.PlayerSettingsValueCollection;

import java.lang.reflect.Field;
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
    public void setupConnection(@NotNull IFPMPlayer player, @NotNull PlayerSettingsValueCollection settings) {
        ServerPlayer serverPlayer = (ServerPlayer) player;

        FakeChannel channel = new FakeChannel();

        LOOP.register(channel);

        FPMNetworkManager connection = new FPMNetworkManager(PacketFlow.SERVERBOUND, channel);

        serverPlayer.connection = new FPMConnection(MinecraftServer.getServer(), connection, serverPlayer, settings);
    }

    @Override
    public void addPlayer(@NotNull IFPMPlayer player) {
        PlayerList playerList = MinecraftServer.getServer().getPlayerList();
        ServerPlayer serverPlayer = (ServerPlayer) player;

        playerList.placeNewPlayer(serverPlayer.connection.connection, serverPlayer);

        serverPlayer.getBukkitEntity().taskScheduler.schedule(e -> {}, e -> {
            try {
                Field field = EntityScheduler.class.getDeclaredField("tickCount");
                field.setAccessible(true);
                field.set(serverPlayer.getBukkitEntity().taskScheduler, 10L);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }, 1000L);
    }

    @Override
    public void removePlayer(@NotNull IFPMPlayer player) {
        PlayerList playerList = MinecraftServer.getServer().getPlayerList();
        ServerPlayer serverPlayer = (ServerPlayer) player;

        setupConnection(player, PlayerSettingsValueCollection.EMPTY);

        playerList.remove(serverPlayer);

        ServerLevel world = serverPlayer.serverLevel();
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
