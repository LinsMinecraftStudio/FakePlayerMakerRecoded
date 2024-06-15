package org.lins.mmmjjkx.fakeplayermaker.listeners;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;

import java.util.Random;

public class Scheduling implements Listener {
    public Scheduling() {
        Bukkit.getPluginManager().registerEvents(this, FPMRecoded.INSTANCE);
    }

    @EventHandler
    public void onFakePlayerJoin(PlayerJoinEvent e) {
        Pair<Boolean, IFPMPlayer> playerPair = FPMRecoded.fakePlayerManager.getExactly(e.getPlayer().getName());
        if (playerPair.getRight() != null) {
            if (FPMRecoded.INSTANCE.getConfig().getBoolean("schedule.randomQuit.enabled", false)) {
                Random rand = new Random();
                int min = FPMRecoded.INSTANCE.getConfig().getInt("schedule.randomQuit.timeInterval.min");
                int max = FPMRecoded.INSTANCE.getConfig().getInt("schedule.randomQuit.timeInterval.max");
                int delay = rand.nextInt((max - min) + 1) + min;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                       FPMRecoded.fakePlayerManager.leave(playerPair.getRight().getFakePlayerProfile().name());
                    }
                }.runTaskLaterAsynchronously(FPMRecoded.INSTANCE, delay * 20L);
            }
        }
    }

    @EventHandler
    public void onFakePlayerQuit(PlayerQuitEvent e) {
        Pair<Boolean, IFPMPlayer> playerPair = FPMRecoded.fakePlayerManager.getExactly(e.getPlayer().getName());
        if (playerPair.getRight() != null) {
            if (FPMRecoded.INSTANCE.getConfig().getBoolean("schedule.randomJoin.enabled", false)) {
                Random rand = new Random();
                int min = FPMRecoded.INSTANCE.getConfig().getInt("schedule.randomJoin.timeInterval.min");
                int max = FPMRecoded.INSTANCE.getConfig().getInt("schedule.randomJoin.timeInterval.max");
                int delay = rand.nextInt((max - min) + 1) + min;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        FPMRecoded.fakePlayerManager.join(playerPair.getRight().getFakePlayerProfile().getName());
                    }
                }.runTaskLaterAsynchronously(FPMRecoded.INSTANCE, delay * 20L);
            }
        }
    }
}
