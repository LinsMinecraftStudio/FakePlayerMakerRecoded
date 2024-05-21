package org.lins.mmmjjkx.fakeplayermaker.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;

import java.util.Random;

public class Scheduling implements Listener {
    @EventHandler
    public void onFakePlayerJoin(PlayerJoinEvent e) {
        if (FPMRecoded.fakePlayerManager.getFakePlayer(e.getPlayer().getName()) != null) {
            if (FPMRecoded.INSTANCE.getConfig().getBoolean("schedule.randomQuit.enabled")) {
                Random rand = new Random();
                int min = FPMRecoded.INSTANCE.getConfig().getInt("schedule.randomQuit.timeInterval.min");
                int max = FPMRecoded.INSTANCE.getConfig().getInt("schedule.randomQuit.timeInterval.max");
                int delay = rand.nextInt((max - min) + 1) + min;
                FPMRecoded.INSTANCE.getScheduler().scheduleDelay(() ->
                        e.getPlayer().kick(Component.text("FPM: random quit"))
                        , delay * 20L);
            }
        }
    }

    @EventHandler
    public void onFakePlayerQuit(PlayerJoinEvent e) {
        if (FPMRecoded.fakePlayerManager.getFakePlayer(e.getPlayer().getName()) != null) {
            if (FPMRecoded.INSTANCE.getConfig().getBoolean("schedule.randomJoin.enabled")) {
                Random rand = new Random();
                int min = FPMRecoded.INSTANCE.getConfig().getInt("schedule.randomJoin.timeInterval.min");
                int max = FPMRecoded.INSTANCE.getConfig().getInt("schedule.randomJoin.timeInterval.max");
                int delay = rand.nextInt((max - min) + 1) + min;
                FPMRecoded.INSTANCE.getScheduler().scheduleDelay(() ->
                        FPMRecoded.fakePlayerManager.join(e.getPlayer().getName())
                        , delay * 20L);
            }
        }
    }
}
