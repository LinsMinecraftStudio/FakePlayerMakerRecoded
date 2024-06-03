package org.lins.mmmjjkx.fakeplayermaker.impls.v1_20_R3;

import lombok.SneakyThrows;
import net.minecraft.Util;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.RelativeMovement;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.fakeplayermaker.commons.Instances;
import org.lins.mmmjjkx.fakeplayermaker.commons.PlayerSettingsValueCollection;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.Set;

public class FPMConnection extends ServerGamePacketListenerImpl {
    private static final Field KEEP_ALIVE_PENDING;
    private static final Field KEEP_ALIVE_CHALLENGE;
    private static final Field KEEP_ALIVE_TIME;

    private final PlayerSettingsValueCollection settings;
    private final Random RNG = new Random();

    static {
        try {
            KEEP_ALIVE_PENDING = ServerCommonPacketListenerImpl.class.getDeclaredField("g");
            KEEP_ALIVE_CHALLENGE = ServerCommonPacketListenerImpl.class.getDeclaredField("h");
            KEEP_ALIVE_TIME = ServerCommonPacketListenerImpl.class.getDeclaredField("f");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        KEEP_ALIVE_TIME.setAccessible(true);
        KEEP_ALIVE_CHALLENGE.setAccessible(true);
        KEEP_ALIVE_PENDING.setAccessible(true);
    }

    @SneakyThrows
    public FPMConnection(MinecraftServer server, Connection connection, ServerPlayer player, PlayerSettingsValueCollection settings) {
        super(server, connection, player, CommonListenerCookie.createInitial(player.gameProfile));

        KEEP_ALIVE_PENDING.set(this, false);

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
    protected void keepConnectionAlive() {
        this.server.getProfiler().push("keepAlive");
        long currentTime = Util.getMillis();

        try {
            KEEP_ALIVE_PENDING.set(this, false);
            KEEP_ALIVE_CHALLENGE.set(this, currentTime);
            KEEP_ALIVE_TIME.set(this, currentTime);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        this.server.getProfiler().pop();
    }

    @Override
    public int latency() {
        int latency = RNG.nextInt(settings.latencyMax() - settings.latencyMin() + 1) + settings.latencyMin();
        Instances.getFPM().getLogger().info("Fake player in nms connection latency: " + latency);
        return latency;
    }
}
