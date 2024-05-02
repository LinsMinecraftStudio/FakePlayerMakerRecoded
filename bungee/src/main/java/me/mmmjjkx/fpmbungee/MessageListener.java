package me.mmmjjkx.fpmbungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MessageListener implements Listener {
    @EventHandler
    public void onMessage(PluginMessageEvent e) {
        ByteArrayDataInput is = ByteStreams.newDataInput(e.getData());
        String channel = is.readUTF();
        if (channel.equals(FakePlayerMakerBungee.CHANNEL_NAME)) {
            String action = is.readUTF();
            if (action.equals("spawn")) {
                String name = is.readUTF();
                String server = getServerName(e.getSender());


            } else if (action.equals("remove")) {
                String name = is.readUTF();
                ProxyServer.getInstance().getPlayer(name).disconnect();
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
