package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;

import java.util.List;
import java.util.Map;

public class RemoveAllCommand extends FPMSubCmd {
    public RemoveAllCommand() {
        super("removeall", "ra");
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.removeall");
    }


    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of();
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        if (hasPermission()) {
            FPMRecoded.fakePlayerManager.getFakePlayerNames().forEach(FPMRecoded.fakePlayerManager::remove);
        }
    }
}
