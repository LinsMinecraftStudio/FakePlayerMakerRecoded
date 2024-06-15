package org.lins.mmmjjkx.fakeplayermaker.objects;

import com.github.steveice10.mc.protocol.packet.login.serverbound.ServerboundHelloPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.PacketSendingEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import lombok.RequiredArgsConstructor;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
public class ImplSessionAdapter extends SessionAdapter {
    private final UUID uuid;

    @Override
    public void packetReceived(Session session, Packet packet) {
        /*
        if (packet instanceof ClientboundResourcePackPacket rpPacket) {
            if (!isValidResourcePackUrl(rpPacket.getUrl())) {
                session.send(new ServerboundResourcePackPacket());
            }

            session.send(new ServerboundResourcePackPacket(rpPacket.getId(),
                    ResourcePackStatus.ACCEPTED));
            session.send(new ServerboundResourcePackPacket(rpPacket.getId(),
                    ResourcePackStatus.DOWNLOADED));
            session.send(new ServerboundResourcePackPacket(rpPacket.getId(),
                    ResourcePackStatus.SUCCESSFULLY_LOADED));
        }

         */
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
}
