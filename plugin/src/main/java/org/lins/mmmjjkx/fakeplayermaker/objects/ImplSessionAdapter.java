package org.lins.mmmjjkx.fakeplayermaker.objects;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import com.github.steveice10.mc.protocol.packet.login.serverbound.ServerboundHelloPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketSendingEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import org.apache.commons.lang3.tuple.Pair;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

public class ImplSessionAdapter extends SessionAdapter {
    private final UUID uuid;
    private final Consumer<Session> callback;
    private final Supplier<Map<Pair<Packet, Class<? extends Packet>>, BiConsumer<Session, Packet>>> callbackMap;

    private boolean loggedIn = false;

    public ImplSessionAdapter(UUID uuid, Consumer<Session> callback, Supplier<Map<Pair<Packet, Class<? extends Packet>>, BiConsumer<Session, Packet>>> callbackMap) {
        this.uuid = uuid;
        this.callback = callback;
        this.callbackMap = callbackMap;
    }

    @Override
    public void packetReceived(Session session, Packet packet) {
        if (packet instanceof ClientboundLoginPacket) {
            if (!loggedIn) {
                callback.accept(session);
            }
            loggedIn = true;
        }

        var callbackMap = this.callbackMap.get();
        Set<Pair<Packet, Class<? extends Packet>>> keys = callbackMap.keySet();
        keys.stream().map(Pair::getRight).filter(c -> c.isAssignableFrom(packet.getClass())).forEach(c -> {
            if (c.isAssignableFrom(packet.getClass())) {
                callbackMap.getOrDefault(Pair.of(packet, c), (s, p) -> {}).accept(session, packet);
                keys.stream().filter(p -> p.getRight().isAssignableFrom(packet.getClass())).findFirst().ifPresent(callbackMap::remove);
            }
        });
    }

    private boolean isValidResourcePackUrl(String url) {
        try {
            var protocol = URI.create(url).toURL().getProtocol();
            return "http".equals(protocol) || "https".equals(protocol);
        } catch (MalformedURLException var3) {
            return false;
        }
    }

    @Override
    public void packetSending(PacketSendingEvent event) {
        var packet = event.getPacket();
        if (packet instanceof ServerboundHelloPacket helloPacket) {
            event.setPacket(helloPacket.withProfileId(uuid));
        }
    }

    @Override
    public void disconnected(DisconnectedEvent event) {
        loggedIn = false;
        FPMRecoded.INSTANCE.getLogger().log(Level.WARNING, "Disconnected from server: " + event.getReason(), event.getCause());
    }
}
