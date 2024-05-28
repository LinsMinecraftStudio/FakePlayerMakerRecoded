package me.mmmjjkx.fpmbungee.netty;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.CompleteFuture;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;

import java.net.SocketAddress;

public class FakeChannelHandlerContext implements ChannelHandlerContext {
    
    private static final EventLoop EVENT_LOOP;
    
    static {
        EVENT_LOOP = new DefaultEventLoop();
    }
    
    private final Channel channel;
    
    public FakeChannelHandlerContext(Channel channel) {
        this.channel = channel;
        
        if (!channel.isRegistered()) {
            EVENT_LOOP.register(channel);
        }
    }
    
    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public EventExecutor executor() {
        return EVENT_LOOP;
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public ChannelHandler handler() {
        return new ChannelDuplexHandler();
    }

    @Override
    public boolean isRemoved() {
        return false;
    }

    @Override
    public ChannelHandlerContext fireChannelRegistered() {
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelUnregistered() {
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelActive() {
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelInactive() {
        return this;
    }

    @Override
    public ChannelHandlerContext fireExceptionCaught(Throwable throwable) {
        return this;
    }

    @Override
    public ChannelHandlerContext fireUserEventTriggered(Object o) {
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelRead(Object o) {
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelReadComplete() {
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelWritabilityChanged() {
        return this;
    }

    @Override
    public ChannelFuture bind(SocketAddress socketAddress) {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress) {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress1) {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture disconnect() {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture close() {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture deregister() {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture bind(SocketAddress socketAddress, ChannelPromise channelPromise) {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, ChannelPromise channelPromise) {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress1, ChannelPromise channelPromise) {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise channelPromise) {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture close(ChannelPromise channelPromise) {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture deregister(ChannelPromise channelPromise) {
        return newSucceededFuture();
    }

    @Override
    public ChannelHandlerContext read() {
        return this;
    }

    @Override
    public ChannelFuture write(Object o) {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture write(Object o, ChannelPromise channelPromise) {
        return newSucceededFuture();
    }

    @Override
    public ChannelHandlerContext flush() {
        return this;
    }

    @Override
    public ChannelFuture writeAndFlush(Object o, ChannelPromise channelPromise) {
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture writeAndFlush(Object o) {
        return newSucceededFuture();
    }

    @Override
    public ChannelPromise newPromise() {
        return new VoidChannelPromise(channel, false);
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return new DefaultChannelProgressivePromise(channel, EVENT_LOOP);
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        return new CompleteChannelFuture(channel);
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable throwable) {
        return newSucceededFuture();
    }

    @Override
    public ChannelPromise voidPromise() {
        return newPromise();
    }

    @Override
    public ChannelPipeline pipeline() {
        return channel.pipeline();
    }

    @Override
    public ByteBufAllocator alloc() {
        return new PooledByteBufAllocator();
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> attributeKey) {
        return channel.attr(attributeKey);
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> attributeKey) {
        return channel.hasAttr(attributeKey);
    }

    static class CompleteChannelFuture extends CompleteFuture<Void> implements ChannelFuture {
        private final Channel channel;

        protected CompleteChannelFuture(Channel channel) {
            super(EVENT_LOOP);
            this.channel = ObjectUtil.checkNotNull(channel, "channel");
        }

        protected EventExecutor executor() {
            try (EventExecutor e = super.executor()) {
                return e == null ? this.channel().eventLoop() : e;
            }
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public Throwable cause() {
            return null;
        }

        public ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
            super.addListener(listener);
            return this;
        }

        @SafeVarargs
        public final ChannelFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
            super.addListeners(listeners);
            return this;
        }

        public ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
            super.removeListener(listener);
            return this;
        }

        @SafeVarargs
        public final ChannelFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
            super.removeListeners(listeners);
            return this;
        }

        public ChannelFuture syncUninterruptibly() {
            return this;
        }

        public ChannelFuture sync() {
            return this;
        }

        public ChannelFuture await() {
            return this;
        }

        public ChannelFuture awaitUninterruptibly() {
            return this;
        }

        public Channel channel() {
            return this.channel;
        }

        public Void getNow() {
            return null;
        }

        public boolean isVoid() {
            return true;
        }
    }
}
