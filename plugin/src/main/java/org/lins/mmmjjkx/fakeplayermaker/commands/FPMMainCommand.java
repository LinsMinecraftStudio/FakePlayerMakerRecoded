package org.lins.mmmjjkx.fakeplayermaker.commands;

import io.github.linsminecraftstudio.polymer.command.presets.HelpMessager;
import io.github.linsminecraftstudio.polymer.command.presets.sub.SubHelpMessager;
import io.github.linsminecraftstudio.polymer.command.presets.sub.SubReloadCommand;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
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
        registerSubCommand(new SubReloadCommand(FPMRecoded.INSTANCE));
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
            return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command..help");
        }
    }
}
