package org.lins.mmmjjkx.fakeplayermaker.impls.v1206;

import net.minecraft.network.BandwidthDebugMonitor;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.util.debugchart.LocalSampleLogger;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMChannel;

public class FPMNetworkManager extends Connection {
    public FPMNetworkManager(PacketFlow side, FPMChannel channel) {
        super(side);

        configureSerialization(channel.pipeline(), PacketFlow.SERVERBOUND, false, new BandwidthDebugMonitor(new LocalSampleLogger(240)));

        channel.pipeline().addLast("encoder", new EmptyPacketEncoder());
        channel.pipeline().addLast("packet_handler", this);

        this.channel = channel;
    }
}
