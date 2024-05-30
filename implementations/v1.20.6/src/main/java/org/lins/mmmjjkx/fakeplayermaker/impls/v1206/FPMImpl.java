package org.lins.mmmjjkx.fakeplayermaker.impls.v1206;

import com.mojang.authlib.GameProfile;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.SneakyThrows;
import net.minecraft.network.*;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;
import org.lins.mmmjjkx.fakeplayermaker.commons.FakeChannel;
import org.lins.mmmjjkx.fakeplayermaker.commons.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.commons.PlayerSettingsValueCollection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Consumer;

public class FPMImpl extends FPMImplements {
    private final Method PLACE_NEW_PLAYER_ORIGINAL;

    public FPMImpl() {
        try {
            PLACE_NEW_PLAYER_ORIGINAL = PlayerList.class.getMethod("placeNewPlayer", Connection.class, ServerPlayer.class, CommonListenerCookie.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final EventLoop LOOP = new DefaultEventLoop();

    @Override
    public @NotNull IFPMPlayer createPlayer(@NotNull GameProfile profile, @NotNull String levelName, @NotNull UUID owner) {
        World bk = Bukkit.getWorld(levelName);
        ServerLevel world;
        if (bk == null) {
            world = MinecraftServer.getServer().overworld();
        } else {
            world = ((CraftWorld) bk).getHandle();
        }
        return new FPMServerPlayer(MinecraftServer.getServer(), world, profile, owner);
    }

    @Override
    public void setupConnection(@NotNull IFPMPlayer player, @NotNull PlayerSettingsValueCollection settings) {
        ServerPlayer serverPlayer = (ServerPlayer) player;

        FakeChannel channel = new FakeChannel();

        LOOP.register(channel);

        FPMNetworkManager connection = new FPMNetworkManager(PacketFlow.SERVERBOUND, channel);

        FPMConnection connection1 = new FPMConnection(MinecraftServer.getServer(), connection, serverPlayer, settings);

        setupNetworkManager(connection);

        serverPlayer.connection = connection1;
    }

    @lombok.SneakyThrows
    @Override
    public void addPlayer(@NotNull IFPMPlayer player) {
        PlayerList playerList = MinecraftServer.getServer().getPlayerList();
        ServerPlayer serverPlayer = (ServerPlayer) player;

        PLACE_NEW_PLAYER_ORIGINAL.invoke(
                playerList,
                serverPlayer.connection.connection,
                serverPlayer,
                new CommonListenerCookie(serverPlayer.gameProfile, 5, ClientInformation.createDefault(), true)
        );
    }

    @SneakyThrows
    @Override
    public void removePlayer(@NotNull IFPMPlayer player) {
        PlayerList playerList = MinecraftServer.getServer().getPlayerList();
        ServerPlayer serverPlayer = (ServerPlayer) player;

        setupConnection(player, PlayerSettingsValueCollection.EMPTY);

        playerList.remove(serverPlayer);

        ServerLevel world = serverPlayer.serverLevel();
        ChunkMap chunkMap = world.chunkSource.chunkMap;

        Int2ObjectMap<ChunkMap.TrackedEntity> entityMap = (Int2ObjectMap<ChunkMap.TrackedEntity>)
                ChunkMap.class.getDeclaredField("entityMap").get(chunkMap);
        entityMap.remove(serverPlayer.getId());
    }

    @Override
    public @NotNull GameProfile getGameProfile(@NotNull IFPMPlayer player) {
        return ((ServerPlayer) player).gameProfile;
    }

    @Override
    public Player toBukkit(@NotNull IFPMPlayer nmsPlayer) {
        return ((ServerPlayer) nmsPlayer).getBukkitEntity();
    }

    @SneakyThrows
    private void setupNetworkManager(FPMNetworkManager networkManager) {
        ProtocolInfo<?> protocolInfo = EmptyPacketEncoder.PROTOCOL_INFO;
        UnconfiguredPipelineHandler.OutboundConfigurationTask unconfiguredpipelinehandler_d = UnconfiguredPipelineHandler.setupOutboundProtocol(protocolInfo);

        BundlerInfo EMPTY = new BundlerInfo() {
            @Override
            public void unbundlePacket(@NotNull Packet<?> packet, Consumer<Packet<?>> consumer) {
                consumer.accept(packet);
            }

            @Nullable
            @Override
            public Bundler startPacketBundling(@NotNull Packet<?> splitter) {
                return null;
            }
        };

        PacketBundleUnpacker packetbundleunpacker = new PacketBundleUnpacker(EMPTY);

        Field loginDisconnect = Connection.class.getDeclaredField("sendLoginDisconnect");
        loginDisconnect.setAccessible(true);
        loginDisconnect.set(networkManager, false);

        UnconfiguredPipelineHandler.InboundConfigurationTask unconfiguredpipelinehandler_b = UnconfiguredPipelineHandler.setupInboundProtocol(protocolInfo);
        PacketBundlePacker packetbundlepacker = new PacketBundlePacker(EMPTY);
        unconfiguredpipelinehandler_b = unconfiguredpipelinehandler_b.andThen((channelhandlercontext) ->  {
                    channelhandlercontext.pipeline().addAfter("decoder", "bundler", packetbundlepacker);
        });

        unconfiguredpipelinehandler_d = unconfiguredpipelinehandler_d.andThen((channelhandlercontext) -> {
                    channelhandlercontext.pipeline().addAfter("encoder", "unbundler", packetbundleunpacker);
        });

        networkManager.channel.writeAndFlush(unconfiguredpipelinehandler_d);
        networkManager.channel.writeAndFlush(unconfiguredpipelinehandler_b);
    }
}
