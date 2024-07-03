package org.lins.mmmjjkx.fakeplayermaker.listeners;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;

import java.util.List;

public class CommandListeners implements Listener {
    public CommandListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(this, FPMRecoded.INSTANCE);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String command = e.getMessage().replaceFirst("/", "");

        //run using fake players
        if (FPMRecoded.fakePlayerManager.get(player.getName()) != null) {
            List<String> bannedCommands = FPMRecoded.getSettingValues().bannedCommands();
            boolean find = bannedCommands.stream().anyMatch(command::matches);
            if (find) {
                e.setCancelled(true);
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(player, "command.not_allowed_command");
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Pair<Boolean, IFPMPlayer> nmsPlayer = FPMRecoded.fakePlayerManager.getExactly(player.getName());
        if (nmsPlayer.getRight() != null) {
            FPMRecoded.INSTANCE.getConfig().getStringList("runCommands.onQuit").forEach(c -> {
                String[] split = c.split(" ");
                String command = split[0];
                String head = split.length > 1 ? split[1] : "";
                IFPMPlayer IFPMPlayer = nmsPlayer.getRight();
                OfflinePlayer owner = Bukkit.getOfflinePlayer(IFPMPlayer.getOwnerUUID());
                command = command.replaceAll("%fakePlayer%", player.getName());
                if (owner.getName() != null) {
                    command = command.replaceAll("%owner%", owner.getName());
                }
                switch (head) {
                    default -> player.performCommand(c);
                    case "console" -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    case "chat" -> player.chat(command);
                }
            });
        }
    }
}
