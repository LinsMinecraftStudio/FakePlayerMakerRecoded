package org.lins.mmmjjkx.fakeplayermaker.objects;

import lombok.RequiredArgsConstructor;
import org.lins.mmmjjkx.fakeplayermaker.util.CommonUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiredArgsConstructor
public class WrappedSession {
    private static final Class<?> sessionClass = CommonUtils.getClass(
            "com.github.steveice10.packetlib.tcp.TcpClientSession",
            "org.geysermc.mcprotocollib.network.tcp.TcpClientSession"
    );

    private static final Class<?> packetClass = CommonUtils.getClass(
            "org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket",
            "com.github.steveice10.mc.protocol.codec.MinecraftPacket"
    );

    private static final Class<?> sessionAdapterClass = CommonUtils.getClass(
            "com.github.steveice10.packetlib.event.session.SessionAdapter",
            "org.geysermc.mcprotocollib.network.event.session.SessionAdapter"
    );

    private static final Class<?> minecraftProtocolClass = CommonUtils.getClass(
            "com.github.steveice10.mc.protocol.MinecraftProtocol",
            "org.geysermc.mcprotocollib.protocol.MinecraftProtocol"
    );

    private static final Method connectMethod;
    private static final Method disconnectMethod;
    private static final Method sendMethod;
    private static final Method setFlagMethod;
    private static final Method isConnectedMethod;
    private static final Method addListenerMethod;
    private static final Constructor<?> protocolConstructor;
    private static final Constructor<?> sessionConstructor;

    static {
        try {
            assert sessionClass != null && packetClass != null && sessionAdapterClass != null && minecraftProtocolClass != null;
            connectMethod = sessionClass.getDeclaredMethod("connect");
            sendMethod = sessionClass.getDeclaredMethod("send", packetClass);
            disconnectMethod = sessionClass.getDeclaredMethod("disconnect", String.class);
            setFlagMethod = sessionClass.getDeclaredMethod("setFlag", String.class, Object.class);
            isConnectedMethod = sessionClass.getDeclaredMethod("isConnected");
            addListenerMethod = sessionClass.getDeclaredMethod("addListener", sessionAdapterClass);

            sessionConstructor = sessionClass.getConstructor(String.class, int.class, String.class, int.class, Object.class);
            protocolConstructor = minecraftProtocolClass.getConstructor(WrappedGameProfile.GAME_PROFILE_CLASS, String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final Object session;

    public void connect() {
        try {
            connectMethod.invoke(session);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(Object packet) {
        try {
            sendMethod.invoke(session, packet);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect(String reason) {
        try {
            disconnectMethod.invoke(session, reason);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFlag(String key, Object value) {
        try {
            setFlagMethod.invoke(session, key, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isConnected() {
        try {
            return (boolean) isConnectedMethod.invoke(session);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void addListener(Object listener) {
        try {
            addListenerMethod.invoke(session, listener);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object newProtocol(Object gameProfile, String accessToken) {
        try {
            return protocolConstructor.newInstance(gameProfile, accessToken);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static WrappedSession newSession(String host, int port, String bindAddress, int bindPort, Object protocol) {
        try {
            return new WrappedSession(sessionConstructor.newInstance(host, port, bindAddress, bindPort, protocol));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
