package org.lins.mmmjjkx.fakeplayermaker.util;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.fakeplayermaker.objects.CodecHelperMethod;
import org.lins.mmmjjkx.fakeplayermaker.objects.providers.IObjectProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;

public class Reflections {
    private Reflections() {}

    public static IObjectProvider objectProvider;

    public static Class<?> RESOURCE_PACK_PACKET_CLASS;
    public static Class<?> SERVERBOUND_RESOURCE_PACK_PACKET_CLASS;
    private static Class<?> RESOURCE_PACK_STATUS_CLASS;
    private static Class<?> BASE_PACKET_CODEC_HELPER_CLASS;
    private static Class<?> MINECRAFT_CODEC_HELPER_CLASS;

    public static Object MINECRAFT_CODEC;
    public static Object MINECRAFT_CODEC_HELPER;

    public static String[] PKG_NAMES_CLIENTBOUND;
    public static String[] PKG_NAMES_SERVERBOUND;

    public static Method RESOURCE_PACK_GET_URL;
    public static Method RESOURCE_PACK_GET_ID;

    public static void init() {
        RESOURCE_PACK_STATUS_CLASS = CommonUtils.getClass(
                "com.github.steveice10.mc.protocol.data.game.ResourcePackStatus",
                "org.geysermc.mcprotocollib.protocol.data.game.ResourcePackStatus"
        );

        SERVERBOUND_RESOURCE_PACK_PACKET_CLASS = getServerboundPacketClass("ServerboundResourcePackPacket");
        RESOURCE_PACK_PACKET_CLASS = getClientboundPacketClass("ClientboundResourcePackPacket", "ClientboundResourcePackPushPacket");

        BASE_PACKET_CODEC_HELPER_CLASS = CommonUtils.getClass(
                "com.github.steveice10.packetlib.codec.BasePacketCodecHelper",
                "org.geysermc.mcprotocollib.network.codec.BasePacketCodecHelper"
        );

        MINECRAFT_CODEC_HELPER_CLASS = CommonUtils.getClass(
                "com.github.steveice10.mc.protocol.codec.MinecraftCodecHelper",
                "org.geysermc.mcprotocollib.protocol.codec.MinecraftCodecHelper"
        );

        Class<?> packetCodecClass = CommonUtils.getClass(
                "com.github.steveice10.mc.protocol.codec.PacketCodec",
                "org.geysermc.mcprotocollib.protocol.codec.PacketCodec"
        );

        Objects.requireNonNull(RESOURCE_PACK_PACKET_CLASS, "Failed to find ResourcePackPacket class");
        Objects.requireNonNull(packetCodecClass, "Failed to find PacketCodec class");

        try {
            RESOURCE_PACK_GET_URL = RESOURCE_PACK_PACKET_CLASS.getDeclaredMethod("getUrl");

            try {
                RESOURCE_PACK_GET_ID = RESOURCE_PACK_PACKET_CLASS.getDeclaredMethod("getId");
            } catch (NoSuchMethodException e) {
                RESOURCE_PACK_GET_ID = null;
            }

            MINECRAFT_CODEC = objectProvider.minecraftCodec();
            MINECRAFT_CODEC_HELPER = objectProvider.codecHelper();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static Object createServerBoundResourcePackPacket(UUID uuid, int status) {
        if (SERVERBOUND_RESOURCE_PACK_PACKET_CLASS == null) {
            SERVERBOUND_RESOURCE_PACK_PACKET_CLASS = getServerboundPacketClass("ServerboundResourcePackPacket");
        }

        Constructor<?> constructor;
        boolean newConstructor = false;
        try {
            constructor = SERVERBOUND_RESOURCE_PACK_PACKET_CLASS.getDeclaredConstructor(UUID.class, RESOURCE_PACK_STATUS_CLASS);
            newConstructor = true;
        } catch (NoSuchMethodException e) {
            try {
                constructor = SERVERBOUND_RESOURCE_PACK_PACKET_CLASS.getDeclaredConstructor(RESOURCE_PACK_STATUS_CLASS);
            } catch (NoSuchMethodException e1) {
                throw new RuntimeException(e1);
            }
        }

        Method resourcePackStatusFromId = RESOURCE_PACK_STATUS_CLASS.getDeclaredMethod("from", int.class);
        Object theStatus = resourcePackStatusFromId.invoke(null, status);

        if (newConstructor) {
            try {
                return constructor.newInstance(uuid, theStatus);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return constructor.newInstance(theStatus);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nullable
    public static Class<?> getClientboundPacketClass(String... classNames) {
        if (PKG_NAMES_CLIENTBOUND == null) {
            PKG_NAMES_CLIENTBOUND = new String[]{
                    "com.github.steveice10.mc.protocol.packet.ingame.clientbound",
                    "com.github.steveice10.mc.protocol.packet.common.clientbound",
                    "org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound",
                    "org.geysermc.mcprotocollib.protocol.packet.common.clientbound"
            };
        }
        for (String pkgName : PKG_NAMES_CLIENTBOUND) {
            try {
                for (String className : classNames) {
                    return Class.forName(pkgName + "." + className);
                }
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        return null;
    }

    @Nullable
    public static Class<?> getServerboundPacketClass(String... className) {
        if (PKG_NAMES_SERVERBOUND == null) {
            PKG_NAMES_SERVERBOUND = new String[]{
                    "org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound",
                    "com.github.steveice10.mc.protocol.packet.ingame.serverbound",
                    "com.github.steveice10.mc.protocol.packet.common.serverbound"
            };
        }
        for (String pkgName : PKG_NAMES_SERVERBOUND) {
            try {
                for (String classNames : className) {
                    return Class.forName(pkgName + "." + classNames);
                }
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        return null;
    }

    public static Object createPacket(Class<?> packetClass, ByteBuf buf) {
        try {
            Constructor<?> constructor = packetClass.getDeclaredConstructor(ByteBuf.class, MINECRAFT_CODEC_HELPER_CLASS);
            return constructor.newInstance(buf, MINECRAFT_CODEC_HELPER);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void codecHelperOperation(ByteBuf buf, CodecHelperMethod method, Object... args) {
        try {
            Method helperMethod;
            if (method.isMinecraftCodec()) {
                helperMethod = MINECRAFT_CODEC_HELPER_CLASS.getDeclaredMethod(method.getMethodName(), method.getParameterTypes());
            } else {
                helperMethod = BASE_PACKET_CODEC_HELPER_CLASS.getDeclaredMethod(method.getMethodName(), method.getParameterTypes());
            }
            Object[] parameters = new Object[args.length + 1];

            parameters[0] = buf;
            System.arraycopy(args, 0, parameters, 1, args.length);

            helperMethod.invoke(MINECRAFT_CODEC_HELPER, parameters);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getEnumOrdinal(Object e) {
        try {
            Method ordinalMethod = e.getClass().getDeclaredMethod("ordinal");
            return (int) ordinalMethod.invoke(e);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e1) {
            throw new RuntimeException(e1);
        }
    }

    public static void setObjectProvider(IObjectProvider defObjectProvider) {
        Preconditions.checkNotNull(defObjectProvider);
        objectProvider = defObjectProvider;
    }
}
