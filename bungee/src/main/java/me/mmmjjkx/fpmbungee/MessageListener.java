package me.mmmjjkx.fpmbungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.linsminecraftstudio.bungee.objects.MessageChannel;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;

public class MessageListener extends MessageChannel {
    public MessageListener() {
        super(FakePlayerMakerBungee.CHANNEL_NAME);
    }

    @EventHandler
    public void onMessageReceived(PluginMessageEvent e, ByteArrayDataInput in) {
        ByteArrayDataInput is = ByteStreams.newDataInput(e.getData());
        String action = is.readUTF();
        switch (action) {
            case "spawn": {
                String name = is.readUTF();
                //We need console force to write the server name.
                String server = getServerName(e.getSender());
                if (server.isEmpty()) {
                    server = is.readUTF();
                }

                ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
                if (serverInfo == null) {
                    return;
                }

                ProxiedPlayer player = FakePlayerMakerBungee.manager.createFakePlayer(name);
                FakePlayerMakerBungee.manager.joinFakePlayer(name);

                player.connect(serverInfo);

                ByteArrayDataOutput os = ByteStreams.newDataOutput();
                os.writeBoolean(true);

                if (e.getSender() instanceof ProxiedPlayer) {
                    ProxiedPlayer sender = (ProxiedPlayer) e.getSender();
                    sender.sendData(FakePlayerMakerBungee.CHANNEL_NAME, os.toByteArray());
                }
                break;
            }
            case "remove": {
                String name = is.readUTF();
                ProxyServer.getInstance().getPlayer(name).disconnect();
                FakePlayerMakerBungee.manager.removeFakePlayer(name);
                break;
            }
            case "leave": {
                String name = is.readUTF();
                ProxyServer.getInstance().getPlayer(name).disconnect();
                break;
            }
        }
    }

    private String getServerName(Connection connection) {
        if (connection instanceof ProxiedPlayer) {
            return ((ProxiedPlayer) connection).getServer().getInfo().getName();
        }
        return "";
    }
}
