package org.lins.mmmjjkx.fakeplayermaker.objects;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.service.SessionService;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.codec.MinecraftCodec;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.FakePlayerProfile;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

@RequiredArgsConstructor
public class MCClient implements IFPMPlayer {
    private final String ip;
    private final int serverPort;
    private final UUID owner;
    private final SessionService sessionService = new SessionService();
    @Getter
    private final Pair<String, Integer> bindAddress;
    private final GameProfile gameProfile;

    private final Map<Pair<Packet, Class<? extends Packet>>, BiConsumer<Session, Packet>> callbacks = new HashMap<>();

    private Session session;

    public void connect(Consumer<Session> callback) {
        sessionService.setProxy(Proxy.NO_PROXY);

        MinecraftProtocol protocol = new MinecraftProtocol(MinecraftCodec.CODEC, gameProfile, gameProfile.getIdAsString());

        /*
        if (Bukkit.getOnlineMode()) {
            FPMRecoded.INSTANCE.getLogger().severe("The plugin is not compatible with online mode servers.");
            return;
        }

         */
        Session session = new TcpClientSession(ip, serverPort, bindAddress.getLeft(), bindAddress.getRight(), protocol);

        this.session = session;

        session.setFlag("print-packetlib-debug", FPMRecoded.INSTANCE.getConfig().getBoolean("print-connection-debug"));
        session.setFlag(MinecraftConstants.SESSION_SERVICE_KEY, sessionService);
        session.setFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 256);
        session.setFlag(MinecraftConstants.VERIFY_USERS_KEY, Bukkit.getOnlineMode());

        session.addListener(new ImplSessionAdapter(getFakePlayerProfile().getId(), callback, () -> callbacks));

        session.connect();

        CompletableFuture<?> waiter = CompletableFuture.runAsync(() -> {
            do {
                FPMRecoded.INSTANCE.getLogger().info("Waiting for connection from " + bindAddress.getLeft() + ":" + bindAddress.getRight() + "...");
            } while (!session.isConnected());
        }).completeOnTimeout(null, 5, TimeUnit.SECONDS);

        try {
            waiter.get();
        } catch (Exception e) {
            FPMRecoded.INSTANCE.getLogger().log(Level.SEVERE, "Failed to connect (address: " + bindAddress.getLeft() + ":" + bindAddress.getRight() + ")", e);
        }
    }

    public void disconnect(Consumer<Session> callback) {
        if (session != null) {
            callback.accept(session);
            session.disconnect("");
        }
    }

    public void send(Packet packet) {
        if (session != null && session.isConnected()) {
            session.send(packet);
        }
    }

    public void send(Packet packet, Class<? extends Packet> responseType, BiConsumer<Session, Packet> callback) {
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
        return new FakePlayerProfile(gameProfile.getName(), gameProfile.getId());
    }
}
