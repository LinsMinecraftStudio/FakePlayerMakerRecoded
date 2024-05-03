package org.lins.mmmjjkx.fakeplayermaker.impls.v1_20_R3;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.fakeplayermaker.commons.IFPMPlayer;

public class FoliaFPMImpl extends FPMImpl {
    @Override
    public void addPlayer(@NotNull IFPMPlayer player) {
        PlayerList playerList = MinecraftServer.getServer().getPlayerList();
        ServerPlayer serverPlayer = (ServerPlayer) player;

        CompoundTag tag = playerList.load(serverPlayer);

        if (tag == null) {
            tag = new CompoundTag();
        }

        ServerLevel level = serverPlayer.serverLevel();
        level.getCurrentWorldData();

        playerList.placeNewPlayer(
                serverPlayer.connection.connection,
                serverPlayer,
                CommonListenerCookie.createInitial(serverPlayer.gameProfile),
                tag,
                serverPlayer.gameProfile.getName(),
                Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
    }

    @Override
    public void removePlayer(@NotNull IFPMPlayer player) {
        PlayerList playerList = MinecraftServer.getServer().getPlayerList();
        ServerPlayer serverPlayer = (ServerPlayer) player;

        setupConnection(player);

        ServerLevel world = serverPlayer.serverLevel();
        world.playerChunkLoader.removePlayer(serverPlayer);

        playerList.remove(serverPlayer);
    }
}
