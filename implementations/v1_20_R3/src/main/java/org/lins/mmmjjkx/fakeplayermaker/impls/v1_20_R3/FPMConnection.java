package org.lins.mmmjjkx.fakeplayermaker.impls.v1_20_R3;

import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.RelativeMovement;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FPMConnection extends ServerGamePacketListenerImpl {
    public FPMConnection(MinecraftServer server, Connection connection, ServerPlayer player) {
        super(server, connection, player, CommonListenerCookie.createInitial(player.gameProfile));
    }

    @Override
    public void internalTeleport(double d0, double d1, double d2, float f, float f1, @NotNull Set<RelativeMovement> set) {
        super.internalTeleport(d0, d1, d2, f, f1, set);

        ServerLevel level = player.serverLevel();

        if (level.getPlayerByUUID(player.getUUID()) == null) {
            ServerChunkCache source = level.getChunkSource();
            source.move(player);

            player.teleportTo(d0, d1, d2);
        }
    }
}
