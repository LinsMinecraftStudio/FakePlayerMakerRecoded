package me.mmmjjkx.fpmbungee;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.mmmjjkx.fpmbungee.netty.FPMChannel;
import me.mmmjjkx.fpmbungee.netty.FakeChannelHandlerContext;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.netty.ChannelWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FakePlayerManager {
    private final Map<String, UserConnection> fakePlayerMap = new HashMap<>();
    private Configuration fakePlayers;

    public FakePlayerManager(FakePlayerMakerBungee plugin) {
        ConfigurationProvider cp = ConfigurationProvider.getProvider(YamlConfiguration.class);
        File file = new File(plugin.getDataFolder(), "fakeplayers.yml");
        if (!file.exists()) {
            try {
                Files.createFile(file.toPath());
                fakePlayers = cp.load(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //We only need to store its name and the uuid
    @CanIgnoreReturnValue
    public ProxiedPlayer createFakePlayer(String name) {
        UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());

        FPMChannel channel = new FPMChannel();

        ListenerInfo info = BungeeCord.getInstance().getConfig().getListeners().toArray(new ListenerInfo[0])[0];

        UserConnection connection = new UserConnection(
                ProxyServer.getInstance(),
                new ChannelWrapper(new FakeChannelHandlerContext(channel)),
                name,
                new InitialHandler(BungeeCord.getInstance(), info)
        );

        fakePlayers.set(name, uuid.toString());
        fakePlayerMap.put(name, connection);
        return connection;
    }

    public void joinFakePlayer(String name) {
        UserConnection connection = fakePlayerMap.get(name);
        if (connection == null) {
            return;
        }
        BungeeCord.getInstance().addConnection(connection);
    }

    public void removeFakePlayer(String name) {
        fakePlayers.set(name, null);
        fakePlayerMap.remove(name);
    }

    public int getFakePlayerCount() {
        return fakePlayerMap.size();
    }
}
