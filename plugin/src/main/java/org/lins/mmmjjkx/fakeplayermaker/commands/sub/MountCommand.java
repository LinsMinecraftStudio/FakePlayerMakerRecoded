package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.PlayerActionImplements;

import java.util.List;
import java.util.Map;

public class MountCommand extends FPMSubCmd {
    public MountCommand() {
        super("mount");

        addArgument("player", PolymerCommand.ArgumentType.REQUIRED);
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.mount");
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

            int distance = FPMRecoded.INSTANCE.getConfig().getInt("mount-distance", 3);
            PlayerActionImplements.getCurrent().mountNearest(player, distance);
        }
    }
}
