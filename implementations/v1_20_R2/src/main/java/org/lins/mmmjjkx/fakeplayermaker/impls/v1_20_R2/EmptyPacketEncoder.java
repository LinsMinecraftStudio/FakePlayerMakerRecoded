package org.lins.mmmjjkx.fakeplayermaker.impls.v1_20_R2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketEncoder;

public class EmptyPacketEncoder extends PacketEncoder {
    public EmptyPacketEncoder() {
        super(Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
    }
}
