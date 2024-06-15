package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;

import java.util.List;
import java.util.Map;

public class LeaveCommand extends FPMSubCmd {
    public LeaveCommand() {
        super("leave");

        addArgument("player", CommandArgumentType.REQUIRED);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, FPMRecoded.fakePlayerManager.getFakePlayerNames());
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.leave");
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        if (hasPermission()) {
            String playerName = getArg(0);

            if (playerName == null) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.no_player");
                return;
            }

            IFPMPlayer player = getFakePlayer(commandSender, playerName);

            if (player != null) {
                FPMRecoded.fakePlayerManager.leave(playerName);
            }
        }
    }
}
