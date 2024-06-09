package org.lins.mmmjjkx.fakeplayermaker.listeners;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;

import java.util.concurrent.CompletableFuture;

public class FakePlayerListener implements Listener {
    public FakePlayerListener() {
        Bukkit.getPluginManager().registerEvents(this, FPMRecoded.INSTANCE);
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent e) {
        if (FPMRecoded.INSTANCE.getConfig().getBoolean("auto-respawn")) {
            Player p = e.getEntity();
            Object player = FPMRecoded.fakePlayerManager.getFakePlayer(p.getName()).getRight();
            if (player != null) {
                p.spigot().respawn();
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Object player = FPMRecoded.fakePlayerManager.getFakePlayer(p.getName()).getRight();
        if (player != null) {
            if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
                LuckPerms luckPerms = LuckPermsProvider.get();
                luckPerms.getUserManager().savePlayerData(p.getUniqueId(), p.getName());
                CompletableFuture<User> user = luckPerms.getUserManager().loadUser(p.getUniqueId(), p.getName());
                user.thenAccept(user1 -> {luckPerms.getUserManager().saveUser(user1);}).join();
            }
        }
    }
}
