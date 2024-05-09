package org.lins.mmmjjkx.fakeplayermaker.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class MotdPlayerFaker implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onMotdPing(ServerListPingEvent e) {

    }
}
