package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.PlayerActionImplements;

import java.util.List;
import java.util.Map;

public class DismountCommand extends FPMSubCmd {
    public DismountCommand() {
        super("dismount");
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.dismount");
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

            Object player = getFakePlayer(commandSender, playerName);
            if (player == null) {
                return;
            }

            PlayerActionImplements.getCurrent().dismount(player);
        }
    }
}
