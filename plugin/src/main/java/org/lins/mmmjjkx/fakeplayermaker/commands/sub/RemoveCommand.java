package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;

import java.util.List;
import java.util.Map;

public class RemoveCommand extends FPMSubCmd {
    public RemoveCommand() {
        super("remove");

        addArgument("player", PolymerCommand.ArgumentType.REQUIRED);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, List.of("player"));
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.remove");
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        String playerName = getArg(0);

        if (playerName == null) {
            FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.no_player");
            return;
        }

        Pair<Boolean, Object> player = FPMRecoded.fakePlayerManager.getFakePlayer(playerName);
        if (player.getRight() == null) {
            FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "player_not_found");
            return;
        }

        FPMRecoded.fakePlayerManager.remove(playerName);
    }
}
