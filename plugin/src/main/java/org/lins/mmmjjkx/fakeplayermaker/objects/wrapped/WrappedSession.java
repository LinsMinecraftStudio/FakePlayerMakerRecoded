package org.lins.mmmjjkx.fakeplayermaker.objects.wrapped;

import lombok.RequiredArgsConstructor;
import org.lins.mmmjjkx.fakeplayermaker.util.CommonUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class WrappedSession {
    private static Class<?> sessionClass;
    private static Method connectMethod;
    private static Method disconnectMethod;
    private static Method sendMethod;
    private static Method setFlagMethod;
    private static Method isConnectedMethod;
    private static Method addListenerMethod;
    private static Constructor<?> sessionConstructor;

    public static void init() {
        sessionClass = CommonUtils.getClass(
                "com.github.steveice10.packetlib.Session",
                "org.geysermc.mcprotocollib.network.Session"
        );
        Class<?> tcpSessionClass = CommonUtils.getClass(
                "com.github.steveice10.packetlib.tcp.TcpClientSession",
                "org.geysermc.mcprotocollib.network.tcp.TcpClientSession"
        );
        Class<?> packetClass = CommonUtils.getClass(
                "com.github.steveice10.packetlib.packet.Packet",
                "org.geysermc.mcprotocollib.network.packet.Packet"
        );
        Class<?> sessionAdapterClass = CommonUtils.getClass(
                "com.github.steveice10.packetlib.event.session.SessionListener",
                "org.geysermc.mcprotocollib.network.event.session.SessionListener"
        );
        Class<?> packetProtocolClass = CommonUtils.getClass(
                "com.github.steveice10.packetlib.packet.PacketProtocol",
                "org.geysermc.mcprotocollib.network.packet.PacketProtocol"
        );

        try {
            assert sessionClass != null && packetClass != null && sessionAdapterClass != null && tcpSessionClass != null;
            connectMethod = sessionClass.getDeclaredMethod("connect");
            sendMethod = sessionClass.getDeclaredMethod("send", packetClass);
            disconnectMethod = sessionClass.getDeclaredMethod("disconnect", String.class);
            setFlagMethod = Arrays.stream(sessionClass.getDeclaredMethods()).filter(m -> m.getName().equalsIgnoreCase("setFlag") && m.getParameterTypes()[0] == String.class).findFirst().orElse(null);
            isConnectedMethod = sessionClass.getDeclaredMethod("isConnected");
            addListenerMethod = sessionClass.getDeclaredMethod("addListener", sessionAdapterClass);

            sessionConstructor = tcpSessionClass.getConstructor(String.class, int.class, String.class, int.class, packetProtocolClass);
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
            if (setFlagMethod != null) {
                setFlagMethod.invoke(session, key, value);
            } else {
                Method setFlagsMethod = sessionClass.getDeclaredMethod("setFlags", Map.class);
                Map<String, Object> flags = new HashMap<>();
                flags.put(key, value);
                setFlagsMethod.invoke(session, flags);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
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

    public static WrappedSession newSession(String host, int port, String bindAddress, int bindPort, Object protocol) {
        try {
            return new WrappedSession(sessionConstructor.newInstance(host, port, bindAddress, bindPort, protocol));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
