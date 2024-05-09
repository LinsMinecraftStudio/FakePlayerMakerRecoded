package org.lins.mmmjjkx.fakeplayermaker.impls.v1206;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.ProtocolInfoBuilder;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;

public class EmptyPacketEncoder extends PacketEncoder<ServerCommonPacketListener> {
    public static final ProtocolInfo<ServerCommonPacketListener> PROTOCOL_INFO =
            new ProtocolInfoBuilder<ServerCommonPacketListener, ByteBuf>(ConnectionProtocol.PLAY, PacketFlow.SERVERBOUND)
                    .build(bf -> bf);

    public EmptyPacketEncoder() {
        super(PROTOCOL_INFO);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
    }
}
