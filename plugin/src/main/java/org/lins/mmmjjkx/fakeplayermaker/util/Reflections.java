package org.lins.mmmjjkx.fakeplayermaker.util;

import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.fakeplayermaker.objects.CodecHelperMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class Reflections {
    private Reflections() {}

    public static final Class<?> RESOURCE_PACK_PACKET_CLASS = getClientboundPacketClass("ClientboundResourcePackPacket", "ClientboundResourcePackPushPacket");

    public static final Class<?> SERVERBOUND_RESOURCE_PACK_PACKET_CLASS = getServerboundPacketClass("ServerboundResourcePackPacket");

    private static final Class<?> RESOURCE_PACK_STATUS_CLASS = CommonUtils.getClass(
            "com.github.steveice10.mc.protocol.data.game.ResourcePackStatus",
            "org.geysermc.mcprotocollib.protocol.data.game.ResourcePackStatus"
    );

    private static final Class<?> MINECRAFT_CODEC_CLASS = CommonUtils.getClass(
            "org.geysermc.mcprotocollib.protocol.codec.MinecraftCodec",
            "com.github.steveice10.mc.protocol.codec.MinecraftCodec"
    );

    public static final Object MINECRAFT_CODEC;
    public static final Object MINECRAFT_CODEC_HELPER;

    public static final String[] PKG_NAMES_CLIENTBOUND;
    public static final String[] PKG_NAMES_SERVERBOUND;

    public static final Method RESOURCE_PACK_GET_URL;
    public static final Method RESOURCE_PACK_GET_ID;

    static {
        PKG_NAMES_CLIENTBOUND = new String[]{
                "org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound",
                "com.github.steveice10.mc.protocol.packet.ingame.serverbound",
                "com.github.steveice10.mc.protocol.packet.common.serverbound"
        };

        PKG_NAMES_SERVERBOUND = new String[]{
                "org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound",
                "com.github.steveice10.mc.protocol.packet.ingame.serverbound",
                "com.github.steveice10.mc.protocol.packet.common.serverbound"
        };

        assert RESOURCE_PACK_PACKET_CLASS != null;
        assert SERVERBOUND_RESOURCE_PACK_PACKET_CLASS != null;
        assert MINECRAFT_CODEC_CLASS != null;
        assert RESOURCE_PACK_STATUS_CLASS != null;

        try {
            RESOURCE_PACK_GET_URL = RESOURCE_PACK_PACKET_CLASS.getDeclaredMethod("getUrl");
            RESOURCE_PACK_GET_ID = RESOURCE_PACK_PACKET_CLASS.getDeclaredMethod("getId");

            MINECRAFT_CODEC = MINECRAFT_CODEC_CLASS.getDeclaredField("CODEC").get(null);

            Class<?> codecClass = MINECRAFT_CODEC.getClass();
            Method getHelperFactoryMethod = codecClass.getDeclaredMethod("getHelperFactory");
            Object helperFactory = getHelperFactoryMethod.invoke(MINECRAFT_CODEC);
            MINECRAFT_CODEC_HELPER = helperFactory.getClass().getMethod("get").invoke(helperFactory);
        } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static Object createServerBoundResourcePackPacket(UUID uuid, int status) {
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
    public static Class<?> getClientboundPacketClass(String... className) {
        for (String pkgName : PKG_NAMES_CLIENTBOUND) {
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

    @Nullable
    public static Class<?> getServerboundPacketClass(String... className) {
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
            Constructor<?> constructor = packetClass.getDeclaredConstructor(ByteBuf.class, MINECRAFT_CODEC_CLASS);
            return constructor.newInstance(buf, MINECRAFT_CODEC);
        } catch (Exception e) {
            //that isn't a valid packet class
            return null;
        }
    }

    public static void codecHelperOperation(ByteBuf buf, CodecHelperMethod method, Object... args) {
        try {
            Method helperMethod = MINECRAFT_CODEC_HELPER.getClass().getDeclaredMethod(method.name(), method.getParameterTypes());
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
}
