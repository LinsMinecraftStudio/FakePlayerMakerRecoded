package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;

import java.util.List;
import java.util.Map;

public class LookAtCommand extends FPMSubCmd {
    public LookAtCommand() {
        super("lookat");
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, FPMRecoded.fakePlayerManager.getFakePlayerNames());
    }

    @Override
    public void execute(CommandSender commandSender, String s) {

    }
}
