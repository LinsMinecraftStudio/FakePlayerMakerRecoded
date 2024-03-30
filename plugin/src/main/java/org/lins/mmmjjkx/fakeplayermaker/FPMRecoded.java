package org.lins.mmmjjkx.fakeplayermaker;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;

import java.util.List;

public final class FPMRecoded extends PolymerPlugin {
    @Override
    public void onPlEnable() {

    }

    @Override
    public void onPlDisable() {

    }

    @Override
    public List<PolymerCommand> registerCommands() {
        return List.of(

        );
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
