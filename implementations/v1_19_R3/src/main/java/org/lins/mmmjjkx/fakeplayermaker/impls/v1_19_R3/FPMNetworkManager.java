package org.lins.mmmjjkx.fakeplayermaker.impls.v1_19_R3;

import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.protocol.PacketFlow;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMChannel;

public class FPMNetworkManager extends Connection {
    public FPMNetworkManager(PacketFlow side, FPMChannel channel) {
        super(side);

        configureSerialization(channel.pipeline(), PacketFlow.SERVERBOUND);

        channel.pipeline().addLast("packet_handler", this);
        channel.pipeline().replace(PacketEncoder.class,"encoder", new EmptyPacketEncoder(PacketFlow.SERVERBOUND));

        this.channel = channel;
        this.address = channel.remoteAddress();

        setProtocol(ConnectionProtocol.PLAY);
    }
}
