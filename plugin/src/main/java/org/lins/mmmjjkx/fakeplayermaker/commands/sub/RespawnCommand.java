package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;

import java.util.List;
import java.util.Map;

public class RespawnCommand extends FPMSubCmd {
    public RespawnCommand() {
        super("respawn");
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
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.no_player");
                return;
            }

            Object player = getFakePlayer(commandSender, playerName);
            if (player == null) {
                return;
            }

            Player bk = FPMImplements.getCurrent().toBukkit(player);
            bk.spigot().respawn();
        }
    }
}
