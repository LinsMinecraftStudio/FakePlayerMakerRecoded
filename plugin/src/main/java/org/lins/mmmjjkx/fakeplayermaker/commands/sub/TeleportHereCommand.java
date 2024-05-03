package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;
import org.lins.mmmjjkx.fakeplayermaker.commons.IFPMPlayer;

import java.util.List;
import java.util.Map;

public class TeleportHereCommand extends FPMSubCmd {
    public TeleportHereCommand() {
        super("teleporthere", "tphere");

        addArgument("player", PolymerCommand.ArgumentType.REQUIRED);
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.teleporthere");
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, FPMRecoded.fakePlayerManager.getFakePlayerNames());
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        if (hasPermission()) {
            String playerName = getArg(0);
            if (playerName == null) {
                return;
            }

            IFPMPlayer player = getFakePlayer(commandSender, playerName);
            if (player == null) {
                return;
            }

            if (!(commandSender instanceof Player p)) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.player_only");
                return;
            }

            Player p2 = FPMImplements.getCurrent().toBukkit(player);
            if (FPMImplements.isFolia()) {
                p2.teleportAsync(p.getLocation());
            } else {
                p2.teleport(p);
            }
        }
    }
}
