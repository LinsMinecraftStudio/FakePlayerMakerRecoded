package org.lins.mmmjjkx.fakeplayermaker.commands;

import io.github.linsminecraftstudio.polymer.command.presets.HelpMessager;
import io.github.linsminecraftstudio.polymer.command.presets.sub.SubHelpMessager;
import io.github.linsminecraftstudio.polymer.command.presets.sub.SubReloadCommand;
import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.sub.ChatCommand;
import org.lins.mmmjjkx.fakeplayermaker.commands.sub.RemoveCommand;
import org.lins.mmmjjkx.fakeplayermaker.commands.sub.SpawnCommand;

import java.util.List;

public class FPMMainCommand extends HelpMessager {
    public FPMMainCommand() {
        super(FPMRecoded.INSTANCE, "fakeplayermaker");

        setAliases(List.of("fpm", "fakeplayer", "fakeplayermaker"));

        registerSubCommand(new FPMHelpSubCommand());
        registerSubCommand(new RemoveCommand());
        registerSubCommand(new SpawnCommand());
        registerSubCommand(new FPMReloadSubCommand());
        registerSubCommand(new ChatCommand());
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "description.help");
    }

    private static class FPMHelpSubCommand extends SubHelpMessager {
        public FPMHelpSubCommand() {
            super(FPMRecoded.INSTANCE);
        }

        public String getHelpDescription() {
            return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help");
        }
    }

    private static class FPMReloadSubCommand extends SubReloadCommand {
        public FPMReloadSubCommand() {
            super(FPMRecoded.INSTANCE);
        }

        @Override
        public void execute(CommandSender sender, String alias) {
            if (this.hasPermission()) {
                FPMRecoded.INSTANCE.reload();
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(sender, "command.reload-success");
            }
        }
    }
}
