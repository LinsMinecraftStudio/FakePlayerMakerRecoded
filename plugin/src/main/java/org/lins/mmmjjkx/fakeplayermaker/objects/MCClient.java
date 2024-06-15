package org.lins.mmmjjkx.fakeplayermaker.objects;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.service.SessionService;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.codec.MinecraftCodec;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.FakePlayerProfile;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;

import java.net.Proxy;
import java.util.UUID;
import java.util.function.Consumer;

public class MCClient extends MinecraftProtocol implements IFPMPlayer {
    private final String ip;
    private final int serverPort;
    private final UUID owner;
    private final SessionService sessionService;
    @Getter
    private final Pair<String, Integer> bindAddress;
    private Session session;

    public MCClient(GameProfile profile, String ip, int serverPort, UUID owner, Pair<String, Integer> bindAddress) {
        super(MinecraftCodec.CODEC, profile, null);

        this.ip = ip;
        this.serverPort = serverPort;
        this.owner = owner;
        this.sessionService = new SessionService();
        this.bindAddress = bindAddress;
    }

    public void connect(Consumer<Session> callback) {
        sessionService.setProxy(Proxy.NO_PROXY);

        Session session = new TcpClientSession(ip, serverPort, bindAddress.getLeft(), bindAddress.getRight(), this);

        this.session = session;

        session.setFlag("print-packetlib-debug", FPMRecoded.INSTANCE.getConfig().getBoolean("print-connection-debug"));
        session.setFlag(MinecraftConstants.SESSION_SERVICE_KEY, sessionService);
        session.setFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 256);
        session.setFlag(MinecraftConstants.VERIFY_USERS_KEY, Bukkit.getOnlineMode());

        session.addListener(new ImplSessionAdapter(getProfile().getId()));

        session.connect();
        callback.accept(session);
    }

    public void disconnect() {
        if (session != null) {
            session.disconnect("");
        }
    }

    @Override
    public UUID getOwnerUUID() {
        return owner;
    }

    @Override
    public FakePlayerProfile getFakePlayerProfile() {
        return new FakePlayerProfile(getProfile().getName(), getProfile().getId());
    }
}
