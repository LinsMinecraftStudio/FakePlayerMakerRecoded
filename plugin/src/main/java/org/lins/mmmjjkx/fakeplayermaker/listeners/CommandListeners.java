package org.lins.mmmjjkx.fakeplayermaker.listeners;

import io.github.linsminecraftstudio.polymer.utils.ObjectConverter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.IFPMPlayer;

import java.util.List;

public class CommandListeners implements Listener {
    public CommandListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(this, FPMRecoded.INSTANCE);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String[] command = e.getMessage().split(" ");
        String commandHead = command[0];

        String argPlayer = command.length > 1 ? command[1] : "";

        if (commandHead.equalsIgnoreCase("tpa") || commandHead.equalsIgnoreCase("tpahere")) {
            if (FPMRecoded.fakePlayerManager.getFakePlayer(argPlayer) != null) {
                if (FPMRecoded.INSTANCE.getConfig().getBoolean("checkTpaRequests.enabled")) {
                    if (FPMRecoded.INSTANCE.getConfig().getBoolean("checkTpaRequests.sendMsgToRequester")) {
                        String msg = FPMRecoded.INSTANCE.getConfig().getString("checkTpaRequests.msgToRequester", "");
                        msg = msg.replaceAll("%fakePlayer%", argPlayer);
                        player.sendMessage(ObjectConverter.toComponent(msg));
                    }
                    e.setCancelled(true);
                }
            }
        }

        //run using fake players
        if (FPMRecoded.fakePlayerManager.getFakePlayer(player.getName()) != null) {
            List<String> bannedCommandsPrefix = FPMRecoded.INSTANCE.getConfig().getStringList("bannedCommandsPrefix");
            boolean find = bannedCommandsPrefix.stream().anyMatch(commandHead::equalsIgnoreCase);
            if (find) {
                e.setCancelled(true);
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(player, "command.not_allowed_command");
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Pair<Boolean, IFPMPlayer> nmsPlayer = FPMRecoded.fakePlayerManager.getFakePlayer(player.getName());
        if (nmsPlayer.getRight() != null) {
            FPMRecoded.INSTANCE.getConfig().getStringList("runCommands.onJoin").forEach(c -> {
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

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Pair<Boolean, IFPMPlayer> nmsPlayer = FPMRecoded.fakePlayerManager.getFakePlayer(player.getName());
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
