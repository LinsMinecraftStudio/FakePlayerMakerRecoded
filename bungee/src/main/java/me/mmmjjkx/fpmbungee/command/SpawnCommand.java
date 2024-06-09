package me.mmmjjkx.fpmbungee.command;

import io.github.linsminecraftstudio.bungee.command.SubBungeeCommand;
import net.md_5.bungee.api.CommandSender;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnCommand extends SubBungeeCommand {
    public SpawnCommand(FPMBCommand command) {
        super(command, "spawn");
    }

    @Override
    protected void run(CommandSender commandSender, String[] args) {
        if (hasPermission(commandSender)) {

        }
    }

    @Override
    protected Map<Integer, List<String>> tabCompletions(CommandSender commandSender, String[] strings) {
        Map<Integer, List<String>> completions = new HashMap<>();
        completions.put(0, Collections.singletonList("name"));
        return completions;
    }
}
