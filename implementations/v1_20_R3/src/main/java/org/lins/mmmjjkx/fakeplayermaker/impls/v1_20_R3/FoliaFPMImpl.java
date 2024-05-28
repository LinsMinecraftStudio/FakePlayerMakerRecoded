package org.lins.mmmjjkx.fakeplayermaker.impls.v1_20_R3;

import ca.spottedleaf.concurrentutil.completable.Completable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.fakeplayermaker.commons.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.commons.Instances;
import org.lins.mmmjjkx.fakeplayermaker.commons.PlayerSettingsValueCollection;

import java.util.logging.Level;

public class FoliaFPMImpl extends FPMImpl {
    @Override
    public void addPlayer(@NotNull IFPMPlayer player) {
        PlayerList playerList = MinecraftServer.getServer().getPlayerList();
        ServerPlayer serverPlayer = (ServerPlayer) player;

        CompoundTag tag = playerList.load(serverPlayer);

        if (tag == null) {
            tag = new CompoundTag();
        }

        boolean joinAble = playerList.pushPendingJoin(
                serverPlayer.getName().getString(),
                serverPlayer.getUUID(),
                serverPlayer.connection.connection
        );

        if (!joinAble) {
            Instances.getFPM().getLogger().log(Level.WARNING, """
                    Failed to add player %s to the server.
                    There are some reasons for this:
                    1. The player(using the same name and the same UUID) is already in the server.
                    2. The tick per max join limit is reached.
                    """.formatted(serverPlayer.getName().getString()));
            return;
        }

        playerList.loadSpawnForNewPlayer(
                serverPlayer.connection.connection,
                serverPlayer,
                CommonListenerCookie.createInitial(serverPlayer.gameProfile),
                new MutableObject<>(tag),
                new MutableObject<>(serverPlayer.gameProfile.getName()),
                new Completable<>()
        );

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

        setupConnection(player, PlayerSettingsValueCollection.EMPTY);

        ServerLevel world = serverPlayer.serverLevel();
        world.playerChunkLoader.removePlayer(serverPlayer);

        playerList.remove(serverPlayer);
    }
}
