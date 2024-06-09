package me.mmmjjkx.fpmbungee.command;

import io.github.linsminecraftstudio.bungee.command.PolymerBungeeCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class FPMBCommand extends PolymerBungeeCommand {
    public FPMBCommand() {
        super("fakeplayermakerbungee", "fakeplayermakerbungee.admin", "fpmb", "fakeplayerbungee");

        new SpawnCommand(this);
    }

    @Override
    public void defaultExecute(CommandSender commandSender, String[] strings) {
    }

    private BaseComponent parse(String message) {
        return new ComponentBuilder(message).build();
    }
}
