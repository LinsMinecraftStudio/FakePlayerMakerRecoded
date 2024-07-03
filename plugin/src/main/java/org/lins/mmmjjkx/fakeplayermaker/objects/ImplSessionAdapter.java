package org.lins.mmmjjkx.fakeplayermaker.objects;

import com.github.steveice10.mc.protocol.codec.MinecraftCodec;
import com.github.steveice10.mc.protocol.codec.MinecraftCodecHelper;
import com.github.steveice10.mc.protocol.packet.common.serverbound.ServerboundResourcePackPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import com.github.steveice10.mc.protocol.packet.login.clientbound.ClientboundHelloPacket;
import com.github.steveice10.mc.protocol.packet.login.serverbound.ServerboundHelloPacket;
import com.github.steveice10.mc.protocol.packet.login.serverbound.ServerboundKeyPacket;
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
import org.lins.mmmjjkx.fakeplayermaker.objects.wrapped.WrappedSession;
import org.lins.mmmjjkx.fakeplayermaker.util.CommonUtils;
import org.lins.mmmjjkx.fakeplayermaker.util.Reflections;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.PublicKey;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

public class ImplSessionAdapter extends SessionAdapter {
    private final UUID uuid;
    private final Consumer<WrappedSession> callback;
    private final Supplier<Map<Pair<Object, Class<?>>, BiConsumer<WrappedSession, Object>>> callbackMap;

    private boolean loggedIn = false;

    public ImplSessionAdapter(UUID uuid, Consumer<WrappedSession> callback, Supplier<Map<Pair<Object, Class<?>>, BiConsumer<WrappedSession, Object>>> callbacks) {
        this.uuid = uuid;
        this.callback = callback;
        this.callbackMap = callbacks;
    }

    @SneakyThrows
    @Override
    public void packetReceived(Session session, Packet packet) {
        if (packet instanceof ClientboundHelloPacket helloPacket) {
            PublicKey key = helloPacket.getPublicKey();
            byte[] challenge = helloPacket.getChallenge();
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();
            ServerboundKeyPacket keyPacket = new ServerboundKeyPacket(key, secretKey, challenge);
            session.send(keyPacket);
        }
        
        if (packet instanceof ClientboundLoginPacket) {
            if (!loggedIn) {
                callback.accept(new WrappedSession(session));
            }
            loggedIn = true;
        }

        boolean is1205 = CommonUtils.isOnMinecraftVersion(1,20,5);

        if (Reflections.RESOURCE_PACK_PACKET_CLASS.isInstance(packet)) {
            Object rp = Reflections.RESOURCE_PACK_PACKET_CLASS.cast(packet);
            String url = (String) Reflections.RESOURCE_PACK_GET_URL.invoke(rp);
            UUID id = Reflections.RESOURCE_PACK_GET_ID == null ? null : (UUID) Reflections.RESOURCE_PACK_GET_ID.invoke(rp);
            if (isValidResourcePackUrl(url)) {
                session.send((Packet) Reflections.createServerBoundResourcePackPacket(id, 3));
                if (is1205) {
                    ByteBuf buf = new CompositeByteBuf(new PooledByteBufAllocator(), false, 16);
                    MinecraftCodecHelper helper = MinecraftCodec.CODEC.getHelperFactory().get();
                    helper.writeVarInt(buf, 4);
                    try {session.send(new ServerboundResourcePackPacket(buf, helper));
                    } catch (IOException ignored) {}
                }
                session.send((Packet) Reflections.createServerBoundResourcePackPacket(id, 0));
            } else {
                if (is1205) {
                    ByteBuf buf = new CompositeByteBuf(new PooledByteBufAllocator(), false, 16);
                    MinecraftCodecHelper helper = MinecraftCodec.CODEC.getHelperFactory().get();
                    helper.writeVarInt(buf, 5);
                    try {session.send(new ServerboundResourcePackPacket(buf, helper));
                    } catch (IOException ignored) {}
                } else {
                    session.send((Packet) Reflections.createServerBoundResourcePackPacket(id, 2));
                }
            }
        }

        var callbackMap = this.callbackMap.get();
        Set<Pair<Object, Class<?>>> keys = callbackMap.keySet();
        List<Class<?>> list = new ArrayList<>(keys.stream().map(Pair::getRight).filter(c -> c.equals(packet.getClass())).distinct().toList());
        if (!list.isEmpty()) {
            if (list.contains(packet.getClass())) {
                for (Pair<Object, Class<?>> pair : keys) {
                    if (pair.getRight().equals(packet.getClass())) {
                        BiConsumer<WrappedSession, Object> consumer = callbackMap.get(pair);
                        consumer.accept(new WrappedSession(session), packet);
                        callbackMap.remove(pair);
                    }
                }
                list.remove(packet.getClass());
            }
        }
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
}
