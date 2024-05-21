package org.lins.mmmjjkx.fakeplayermaker.impls.v1_20_R3;

import net.minecraft.network.BandwidthDebugMonitor;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.util.SampleLogger;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMChannel;

public class FPMNetworkManager extends Connection {
    public FPMNetworkManager(PacketFlow side, FPMChannel channel) {
        super(side);

        channel.attr(Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL).set(ConnectionProtocol.PLAY.codec(PacketFlow.SERVERBOUND));
        channel.attr(Connection.ATTRIBUTE_CLIENTBOUND_PROTOCOL).set(ConnectionProtocol.PLAY.codec(PacketFlow.CLIENTBOUND));

        configureSerialization(channel.pipeline(), PacketFlow.SERVERBOUND, new BandwidthDebugMonitor(new SampleLogger()));

        channel.pipeline().addLast("packet_handler", this);
        channel.pipeline().replace(PacketEncoder.class,"encoder", new EmptyPacketEncoder());

        this.channel = channel;
        this.address = channel.remoteAddress();
    }
}
