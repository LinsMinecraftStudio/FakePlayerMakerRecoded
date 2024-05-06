package me.mmmjjkx.fpmbungee.fakers;

import me.mmmjjkx.fpmbungee.FakePlayerMakerBungee;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Arrays;
import java.util.List;

public class MotdPlayerFaker implements Listener {
    private final Configuration config;

    public MotdPlayerFaker(Configuration config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPing(ProxyPingEvent e) {
        ServerPing ping = e.getResponse();
        ServerPing.Players players = ping.getPlayers();
        int count = 0;
        if (config.getBoolean("countRealPlayerAmount", true)) {
            count += players.getOnline();
        }
        if (config.getBoolean("countFakePlayerAmount", false)) {
            count += FakePlayerMakerBungee.manager.getFakePlayerCount();
        }
        if (config.getBoolean("countSimpleFakePlayerAmount", false)) {
            count += config.getInt("simpleFakePlayerAmount", 0);
        }

        players.setOnline(count);

        if (config.getBoolean("listFakePlayers")) {
            List<ServerPing.PlayerInfo> list = Arrays.asList(players.getSample());
            for (UserConnection conn : FakePlayerMakerBungee.manager.getFakePlayers()) {
                ServerPing.PlayerInfo player = new ServerPing.PlayerInfo(conn.getName(), conn.getUniqueId());
                list.add(player);
            }

            players.setSample(list.toArray(new ServerPing.PlayerInfo[0]));
        }
    }
}
