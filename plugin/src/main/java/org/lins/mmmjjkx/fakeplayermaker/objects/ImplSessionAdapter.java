package org.lins.mmmjjkx.fakeplayermaker.objects;

import com.github.steveice10.mc.protocol.codec.MinecraftCodec;
import com.github.steveice10.mc.protocol.codec.MinecraftCodecHelper;
import com.github.steveice10.mc.protocol.data.game.ResourcePackStatus;
import com.github.steveice10.mc.protocol.packet.common.serverbound.ServerboundResourcePackPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import com.github.steveice10.mc.protocol.packet.login.serverbound.ServerboundHelloPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketSendingEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.SneakyThrows;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.util.CommonUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

public class ImplSessionAdapter extends SessionAdapter {
    private static final Class<? extends Packet> RESOURCE_PACK_PACKET_CLASS = (Class<? extends Packet>) CommonUtils.getClass(
            "com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundResourcePackPacket",
            "com.github.steveice10.mc.protocol.packet.common.clientbound.ClientboundResourcePackPushPacket",
            "org.geysermc.mcprotocollib.protocol.packet.common.clientbound.ClientboundResourcePackPushPacket");
    private static final Class<? extends Packet> SERVERBOUND_RESOURCE_PACK_PACKET_CLASS = (Class<? extends Packet>) CommonUtils.getClass(
            "com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundResourcePackPacket",
            "com.github.steveice10.mc.protocol.packet.common.serverbound.ServerboundResourcePackPacket",
            "org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundResourcePackPacket"
    );
    private static final Method RESOURCE_PACK_GET_URL;
    private static final Method RESOURCE_PACK_GET_ID;

    static {
        assert RESOURCE_PACK_PACKET_CLASS != null;
        try {
            RESOURCE_PACK_GET_URL = RESOURCE_PACK_PACKET_CLASS.getDeclaredMethod("getUrl");
            RESOURCE_PACK_GET_ID = RESOURCE_PACK_PACKET_CLASS.getDeclaredMethod("getId");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final UUID uuid;
    private final Consumer<Session> callback;
    private final Supplier<Map<Pair<Packet, Class<? extends Packet>>, BiConsumer<Session, Packet>>> callbackMap;

    private boolean loggedIn = false;

    public ImplSessionAdapter(UUID uuid, Consumer<Session> callback, Supplier<Map<Pair<Packet, Class<? extends Packet>>, BiConsumer<Session, Packet>>> callbackMap) {
        this.uuid = uuid;
        this.callback = callback;
        this.callbackMap = callbackMap;
    }

    @SneakyThrows
    @Override
    public void packetReceived(Session session, Packet packet) {
        if (packet instanceof ClientboundLoginPacket) {
            if (!loggedIn) {
                callback.accept(session);
            }
            loggedIn = true;
        }

        if (RESOURCE_PACK_PACKET_CLASS.isInstance(packet)) {
            Object rp = RESOURCE_PACK_PACKET_CLASS.cast(packet);
            String url = (String) RESOURCE_PACK_GET_URL.invoke(rp);
            UUID id = (UUID) RESOURCE_PACK_GET_ID.invoke(rp);
            if (isValidResourcePackUrl(url)) {
                session.send(newResourcePackPacket(id, ResourcePackStatus.ACCEPTED));
                if (isOnMinecraftVersion(1,20,5)) {
                    ByteBuf buf = new CompositeByteBuf(new PooledByteBufAllocator(), false, 16);
                    MinecraftCodecHelper helper = MinecraftCodec.CODEC.getHelperFactory().get();
                    helper.writeVarInt(buf, 5);
                    try {session.send(new ServerboundResourcePackPacket(buf, helper));
                    } catch (IOException ignored) {}
                }
                session.send(newResourcePackPacket(id, ResourcePackStatus.SUCCESSFULLY_LOADED));
            } else {
                if (isOnMinecraftVersion(1,20,5)) {
                    ByteBuf buf = new CompositeByteBuf(new PooledByteBufAllocator(), false, 16);
                    MinecraftCodecHelper helper = MinecraftCodec.CODEC.getHelperFactory().get();
                    helper.writeVarInt(buf, 6);
                    try {session.send(new ServerboundResourcePackPacket(buf, helper));
                    } catch (IOException ignored) {}
                } else {
                    session.send(newResourcePackPacket(id, ResourcePackStatus.FAILED_DOWNLOAD));
                }
            }
        }

        var callbackMap = this.callbackMap.get();
        Set<Pair<Packet, Class<? extends Packet>>> keys = callbackMap.keySet();
        keys.stream().map(Pair::getRight).filter(c -> c.isAssignableFrom(packet.getClass())).forEach(c -> {
            if (c.isAssignableFrom(packet.getClass())) {
                callbackMap.getOrDefault(Pair.of(packet, c), (s, p) -> {}).accept(session, packet);
                keys.stream().filter(p -> p.getRight().isAssignableFrom(packet.getClass())).findFirst().ifPresent(callbackMap::remove);
            }
        });
    }

    private boolean isValidResourcePackUrl(String url) {
        try {
            var protocol = URI.create(url).toURL().getProtocol();
            return "http".equals(protocol) || "https".equals(protocol);
        } catch (MalformedURLException var3) {
            return false;
        }
    }

    @Override
    public void packetSending(PacketSendingEvent event) {
        var packet = event.getPacket();
        if (packet instanceof ServerboundHelloPacket helloPacket) {
            event.setPacket(helloPacket.withProfileId(uuid));
        }
    }

    @Override
    public void disconnected(DisconnectedEvent event) {
        loggedIn = false;
        Bukkit.getConsoleSender().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cDisconnected from server: ").append(event.getReason()));
        FPMRecoded.INSTANCE.getLogger().log(Level.WARNING, "Disconnected cause: ", event.getCause());
    }

    private boolean isOnMinecraftVersion(int major, int minor, int patch) {
        String version = Bukkit.getMinecraftVersion();
        String[] versionParts = version.split("-")[0].split("\\.");
        int majorVersion, minorVersion, patchVersion;
        if (versionParts.length == 2) {
            patchVersion = 0;
        } else if (versionParts.length == 3) {
            patchVersion = Integer.parseInt(versionParts[2]);
        } else {
            return false;
        }
        majorVersion = Integer.parseInt(versionParts[0]);
        minorVersion = Integer.parseInt(versionParts[1]);
        return majorVersion > major || (majorVersion == major && minorVersion > minor) || (majorVersion == major && minorVersion == minor && patchVersion >= patch);
    }

    private Packet newResourcePackPacket(UUID uuid, ResourcePackStatus status) {
        assert SERVERBOUND_RESOURCE_PACK_PACKET_CLASS != null;
        Constructor<? extends Packet> constructor;
        boolean newConstructor = false;
        try {
            constructor = SERVERBOUND_RESOURCE_PACK_PACKET_CLASS.getDeclaredConstructor(UUID.class, ResourcePackStatus.class);
            newConstructor = true;
        } catch (NoSuchMethodException e) {
            try {
                constructor = SERVERBOUND_RESOURCE_PACK_PACKET_CLASS.getDeclaredConstructor(ResourcePackStatus.class);
            } catch (NoSuchMethodException e1) {
                throw new RuntimeException(e1);
            }
        }
        if (newConstructor) {
            try {
                return constructor.newInstance(uuid, status);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return constructor.newInstance(status);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
