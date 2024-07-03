package org.lins.mmmjjkx.fakeplayermaker.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.FakePlayerProfile;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;

import java.util.List;

public class FakePlayerListener implements Listener {
    private final boolean quitIfOwnerLeave;
    private final boolean joinIfOwnerJoin;

    public FakePlayerListener() {
        Bukkit.getPluginManager().registerEvents(this, FPMRecoded.INSTANCE);

        quitIfOwnerLeave = FPMRecoded.getSettingValues().quitIfOwnerQuit();
        joinIfOwnerJoin = FPMRecoded.getSettingValues().joinIfOwnerJoin();
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

    @EventHandler
    public void onOwnerQuit(PlayerQuitEvent e) {
        if (quitIfOwnerLeave) {
            Player p = e.getPlayer();
            List<IFPMPlayer> players = FPMRecoded.fakePlayerManager.getFakePlayers(p.getUniqueId());
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (IFPMPlayer player : players) {
                        FakePlayerProfile profile = player.getFakePlayerProfile();
                        Player bk = profile.getPlayer();
                        if (bk != null) {
                            FPMRecoded.fakePlayerManager.leave(bk.getName());
                        }
                    }
                }
            }.runTaskLaterAsynchronously(FPMRecoded.INSTANCE, 20L);
        }
    }

    @EventHandler
    public void onOwnerJoin(PlayerJoinEvent e) {
        if (joinIfOwnerJoin) {
            Player p = e.getPlayer();
            List<IFPMPlayer> players = FPMRecoded.fakePlayerManager.getFakePlayers(p.getUniqueId());
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (IFPMPlayer player : players) {
                        FakePlayerProfile profile = player.getFakePlayerProfile();
                        Player bk = profile.getPlayer();
                        if (bk == null) {
                            FPMRecoded.fakePlayerManager.join(profile.getName());
                        }
                    }
                }
            }.runTaskLaterAsynchronously(FPMRecoded.INSTANCE, 20L);
        }
    }
}
