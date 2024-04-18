package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;

import java.util.List;
import java.util.Map;

public class ChatCommand extends SubCommand {
    public ChatCommand() {
        super("chat");

        addArgument("message", PolymerCommand.ArgumentType.REQUIRED);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(1, FPMRecoded.fakePlayerManager.getFakePlayerNames(), 2, List.of("message(use %sp% instead of spaces)"));
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.chat");
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        String player = getArg(0);
        String message = getArg(1);

        if (player == null || message == null) {

        }
    }
}
