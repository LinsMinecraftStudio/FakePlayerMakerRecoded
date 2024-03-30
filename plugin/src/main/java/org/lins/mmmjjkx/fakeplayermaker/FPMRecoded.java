package org.lins.mmmjjkx.fakeplayermaker;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMMainCommand;

import java.util.List;

public final class FPMRecoded extends PolymerPlugin {
    public static FPMRecoded INSTANCE;

    @Override
    public void onPlEnable() {
        INSTANCE = this;
    }

    @Override
    public void onPlDisable() {

    }

    @Override
    public List<PolymerCommand> registerCommands() {
        return List.of(new FPMMainCommand());
    }

    @Override
    public String requireVersion() {
        return "1.4.2";
    }

    @Override
    public int requireApiVersion() {
        return 2;
    }
}
