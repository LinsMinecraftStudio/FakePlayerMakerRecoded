package org.lins.mmmjjkx.fakeplayermaker.commands;

import io.github.linsminecraftstudio.polymer.command.presets.HelpMessager;
import io.github.linsminecraftstudio.polymer.command.presets.sub.SubHelpMessager;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;

import java.util.List;

public class FPMMainCommand extends HelpMessager {
    public FPMMainCommand() {
        super(FPMRecoded.INSTANCE, "fakeplayermaker");

        setAliases(List.of("fpm", "fakeplayer", "fakeplayermaker"));

        registerSubCommand(new FPMHelpSubCommand());
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
            return FPMRecoded.INSTANCE.getMessageHandler().get(null, "description.help");
        }
    }
}
