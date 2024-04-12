package org.lins.mmmjjkx.fakeplayermaker.impls.common;

import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class FPMChannel extends AbstractChannel {
    private final ChannelConfig config;
    private final ChannelMetadata metadata;

    public FPMChannel() {
        super(null);
        config = new DefaultChannelConfig(this);
        metadata = new ChannelMetadata(false);
    }

    @Override
    protected AbstractUnsafe newUnsafe() {
        return new AbstractUnsafe() {
            @Override
            public void connect(SocketAddress socketAddress, SocketAddress socketAddress1, ChannelPromise channelPromise) {
                channelPromise.setSuccess();
            }
        };
    }

    @Override
    protected boolean isCompatible(EventLoop eventLoop) {
        return true;
    }

    @Override
    protected SocketAddress localAddress0() {
        return new InetSocketAddress(25565);
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return new InetSocketAddress(25565);
    }

    @Override
    protected void doBind(SocketAddress socketAddress) {
    }

    @Override
    protected void doDisconnect() {
    }

    @Override
    protected void doClose() {
    }

    @Override
    protected void doBeginRead() {
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer channelOutboundBuffer) {
    }

    @Override
    public ChannelConfig config() {
        return config;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public ChannelMetadata metadata() {
        return metadata;
    }

    @Override
    public ChannelPipeline pipeline() {
        return super.pipeline();
    }
}
