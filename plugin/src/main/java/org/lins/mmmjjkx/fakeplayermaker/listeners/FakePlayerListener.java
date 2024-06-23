package org.lins.mmmjjkx.fakeplayermaker.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
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
        if (FPMRecoded.getSettingValues().autoRespawn()) {
            Player p = e.getEntity();
            IFPMPlayer player = FPMRecoded.fakePlayerManager.get(p.getName());
            if (player != null) {
                p.spigot().respawn();
            }
        }
    }

    @EventHandler
    public void onPlayerBeDamaged(EntityDamageEvent e) {
        if (FPMRecoded.getSettingValues().invulnerable() && e.getCause() != EntityDamageEvent.DamageCause.VOID) {
            Entity dest = e.getEntity();
            if (dest instanceof Player p) {
                if (FPMRecoded.fakePlayerManager.get(p.getName()) != null) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerAttemptPickupItemEvent e) {
        if (!FPMRecoded.getSettingValues().pickupItems()) {
            if (FPMRecoded.fakePlayerManager.get(e.getPlayer().getName()) != null) {
                e.setCancelled(true);
            }
        }
    }
}
