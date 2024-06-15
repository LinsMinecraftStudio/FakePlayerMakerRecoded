package org.lins.mmmjjkx.fakeplayermaker.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;

public class FakePlayerListener implements Listener {
    public FakePlayerListener() {
        Bukkit.getPluginManager().registerEvents(this, FPMRecoded.INSTANCE);
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent e) {
        if (FPMRecoded.INSTANCE.getConfig().getBoolean("auto-respawn")) {
            Player p = e.getEntity();
            IFPMPlayer player = FPMRecoded.fakePlayerManager.get(p.getName());
            if (player != null) {
                p.spigot().respawn();
            }
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerAttemptPickupItemEvent e) {
        if (!FPMRecoded.INSTANCE.getConfig().getBoolean("pickupItems")) {
            if (FPMRecoded.fakePlayerManager.get(e.getPlayer().getName()) != null) {
                e.setCancelled(true);
            }
        }
    }
}
