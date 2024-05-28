package org.lins.mmmjjkx.fakeplayermaker.impls.v1_20_R1;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.RelativeMovement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.fakeplayermaker.commons.PlayerSettingsValueCollection;

import java.util.Random;
import java.util.Set;

public class FPMConnection extends ServerGamePacketListenerImpl {
    private final PlayerSettingsValueCollection settings;

    public FPMConnection(MinecraftServer server, Connection connection, ServerPlayer player, PlayerSettingsValueCollection settings) {
        super(server, connection, player);

        this.settings = settings;
    }

    @Override
    public void internalTeleport(double d0, double d1, double d2, float f, float f1, @NotNull Set<RelativeMovement> set) {
        super.internalTeleport(d0, d1, d2, f, f1, set);

        ServerLevel level = player.serverLevel();

        if (level.getPlayerByUUID(player.getUUID()) == null) {
            ServerChunkCache source = level.getChunkSource();
            source.move(player);

            player.teleportTo(d0, d1, d2);
            resetPosition();
        }
    }

    @Override
    public void send(@NotNull Packet<?> packet, @Nullable PacketSendListener callbacks) {
        super.send(packet, callbacks);

        resetLatency();
    }

    @Override
    public void send(@NotNull Packet<?> packet) {
        super.send(packet);

        resetLatency();
    }

    private void resetLatency() {
        Random random = new Random();

        player.latency = random.nextInt(settings.latencyMax() - settings.latencyMin() + 1) + settings.latencyMin();
    }
}
