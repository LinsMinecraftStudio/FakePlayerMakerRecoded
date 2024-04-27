package org.lins.mmmjjkx.fakeplayermaker.impls.v1_20_R1;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.protocol.PacketFlow;

public class EmptyPacketEncoder extends PacketEncoder {
    public EmptyPacketEncoder(PacketFlow side) {
        super(side);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
    }
}
