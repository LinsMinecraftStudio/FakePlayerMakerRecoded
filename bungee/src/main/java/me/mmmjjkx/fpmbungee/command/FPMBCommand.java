package me.mmmjjkx.fpmbungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class FPMBCommand extends Command {
    public FPMBCommand() {
        super("fakeplayermakerbungee", "fakeplayermakerbungee.admin", "fpmb", "fakeplayerbungee");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage();
        } else if (strings.length == 1) {

        }
    }

    private BaseComponent parse(String message) {
        return new ComponentBuilder(message).build();
    }
}
