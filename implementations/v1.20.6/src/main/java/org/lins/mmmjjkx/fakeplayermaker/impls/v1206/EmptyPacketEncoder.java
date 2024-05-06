package org.lins.mmmjjkx.fakeplayermaker.impls.v1206;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.ProtocolInfoBuilder;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;

public class EmptyPacketEncoder extends PacketEncoder<ServerCommonPacketListenerImpl> {
    public static final ProtocolInfo<ServerCommonPacketListenerImpl> PROTOCOL_INFO =
            new ProtocolInfoBuilder<ServerCommonPacketListenerImpl, ByteBuf>(ConnectionProtocol.PLAY, PacketFlow.SERVERBOUND)
                    .build(bf -> bf);

    public EmptyPacketEncoder() {
        super(PROTOCOL_INFO);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
    }
}
