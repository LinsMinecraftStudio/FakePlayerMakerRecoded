package org.lins.mmmjjkx.fakeplayermaker.objects;

import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.FakePlayerProfile;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.objects.wrapped.WrappedGameProfile;
import org.lins.mmmjjkx.fakeplayermaker.objects.wrapped.WrappedSession;
import org.lins.mmmjjkx.fakeplayermaker.util.CommonUtils;
import org.lins.mmmjjkx.fakeplayermaker.util.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

public class MCClient implements IFPMPlayer {
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    private final String ip;
    private final int serverPort;
    private final UUID owner;
    @Getter
    private final Pair<String, Integer> bindAddress;
    private final Pair<String, UUID> gameProfilePair;
    @Getter
    private final WrappedGameProfile gameProfile;
    private final Map<Pair<Object, Class<?>>, BiConsumer<WrappedSession, Object>> callbacks = new HashMap<>();

    private WrappedSession session;

    public MCClient(String ip, int serverPort, UUID owner, Pair<String, Integer> bindAddress, Pair<String, UUID> gameProfilePair) {
        this.ip = ip;
        this.serverPort = serverPort;
        this.owner = owner;
        this.bindAddress = bindAddress;
        this.gameProfilePair = gameProfilePair;

        this.gameProfile = WrappedGameProfile.create(gameProfilePair.getRight(), gameProfilePair.getLeft());
    }

    public void connect(Consumer<WrappedSession> callback) {
        String accessToken = "Bareer" + gameProfilePair.getRight();

        Object protocol = Reflections.objectProvider.createProtocol(gameProfile, accessToken);
        WrappedSession session = WrappedSession.newSession(ip, serverPort, bindAddress.getLeft(), bindAddress.getRight(), protocol);

        this.session = session;

        session.setFlag("print-packetlib-debug", FPMRecoded.INSTANCE.getConfig().getBoolean("print-connection-debug"));
        session.setFlag("compression-threshold", 256);
        session.setFlag("verify-users", Bukkit.getOnlineMode());
        session.setFlag("profile", gameProfile);
        session.setFlag("access-token", accessToken);
        session.setFlag("session-service", Reflections.objectProvider.sessionService().getHandle());

        if (!CommonUtils.isOnMinecraftVersion(1,20,5)) {
            session.addListener(new ImplSessionAdapter(getFakePlayerProfile().getId(), callback, () -> callbacks));
        } else {
            session.addListener(new ImplSessionAdapterN2(getFakePlayerProfile().getId(), callback, () -> callbacks));
        }

        session.connect();

        Future<?> waiter = pool.submit(() -> {
            do {
                FPMRecoded.INSTANCE.getLogger().info("Waiting for connection from " + bindAddress.getLeft() + ":" + bindAddress.getRight() + "...");
            } while (!session.isConnected());
        });

        try {
            waiter.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            FPMRecoded.INSTANCE.getLogger().log(Level.SEVERE, "Failed to connect (address: " + bindAddress.getLeft() + ":" + bindAddress.getRight() + ")", e);
        }
    }

    public void disconnect(Consumer<WrappedSession> callback) {
        if (session != null) {
            callback.accept(session);
            session.disconnect("");
        }
    }

    public void send(Object packet) {
        if (session != null && session.isConnected()) {
            session.send(packet);
        }
    }

    public void send(Object packet, Class<?> responseType, BiConsumer<WrappedSession, Object> callback) {
        if (session != null && session.isConnected()) {
            session.send(packet);
            callbacks.put(new ImmutablePair<>(packet, responseType), callback);
        }
    }

    @Override
    public UUID getOwnerUUID() {
        return owner;
    }

    @Override
    public FakePlayerProfile getFakePlayerProfile() {
        return new FakePlayerProfile(gameProfilePair.getLeft(), gameProfilePair.getRight());
    }
}
