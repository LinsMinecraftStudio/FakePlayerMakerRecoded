package me.mmmjjkx.fpmbungee.fakers;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class MotdPlayerFaker implements Listener {
    private final Configuration config;

    public MotdPlayerFaker(Configuration config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPing(ProxyPingEvent e) {
        ServerPing ping = e.getResponse();

    }
}
