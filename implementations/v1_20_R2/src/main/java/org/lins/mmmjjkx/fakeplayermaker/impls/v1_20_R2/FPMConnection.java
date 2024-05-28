package org.lins.mmmjjkx.fakeplayermaker.impls.v1_20_R2;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.RelativeMovement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.fakeplayermaker.commons.PlayerSettingsValueCollection;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.Set;

public class FPMConnection extends ServerGamePacketListenerImpl {
    private static final Field LATENCY;

    static {
        try {
            LATENCY = ServerCommonPacketListenerImpl.class.getDeclaredField("i");
            LATENCY.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final PlayerSettingsValueCollection settings;

    public FPMConnection(MinecraftServer server, Connection connection, ServerPlayer player, PlayerSettingsValueCollection settings) {
        super(server, connection, player, CommonListenerCookie.createInitial(player.gameProfile));

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
        int latency = random.nextInt(settings.latencyMax() - settings.latencyMin() + 1) + settings.latencyMin();

        try {
            LATENCY.set(this, latency);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
