package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;

import java.util.List;
import java.util.Map;

public class SpawnCommand extends SubCommand {
    public SpawnCommand() {
        super("spawn");

        addArgument("name", PolymerCommand.ArgumentType.OPTIONAL);
        addArgument("location", PolymerCommand.ArgumentType.OPTIONAL);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(1, List.of("nameOrLocation"), 2, List.of("location"));
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.spawn");
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        String name = getArg(0);
        String location = getArg(1);

    }
}
