package org.lins.mmmjjkx.fakeplayermaker.commons;

import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class FakeChannel extends AbstractChannel {
    private final ChannelConfig config;
    private final ChannelMetadata metadata;
    private State state = State.OPEN;

    public FakeChannel() {
        super(null);
        metadata = new ChannelMetadata(false);
        config = new DefaultChannelConfig(this);
    }

    @Override
    protected AbstractUnsafe newUnsafe() {
        return new AbstractUnsafe() {
            @Override
            public void connect(SocketAddress socketAddress, SocketAddress socketAddress1, ChannelPromise channelPromise) {
                safeSetSuccess(channelPromise);
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
        state = State.CLOSED;
    }

    @Override
    protected void doBeginRead() {
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer channelOutboundBuffer) {
        channelOutboundBuffer.remove();
    }

    @Override
    public ChannelConfig config() {
        return config;
    }

    @Override
    public boolean isOpen() {
        return state == State.OPEN;
    }

    @Override
    public boolean isActive() {
        return state == State.OPEN;
    }

    @Override
    public ChannelMetadata metadata() {
        return metadata;
    }

    @Override
    public ChannelFuture write(Object msg) {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return newSucceededFuture();
    }

    private enum State {
        OPEN,
        CLOSED
    }
}
