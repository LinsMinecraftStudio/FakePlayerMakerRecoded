package org.lins.mmmjjkx.fakeplayermaker.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;

public class AutoRespawn implements Listener {
    public AutoRespawn() {
        Bukkit.getPluginManager().registerEvents(this, FPMRecoded.INSTANCE);
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Object player = FPMRecoded.fakePlayerManager.getFakePlayer(p.getName()).getRight();
        if (player != null) {
            p.spigot().respawn();
        }
    }
}
