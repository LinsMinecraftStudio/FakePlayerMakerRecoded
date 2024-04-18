package org.lins.mmmjjkx.fakeplayermaker;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMMainCommand;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMPlugin;
import org.lins.mmmjjkx.fakeplayermaker.commons.Instances;
import org.lins.mmmjjkx.fakeplayermaker.util.FakePlayerManager;
import org.lins.mmmjjkx.fakeplayermaker.util.FakePlayerSaver;

import java.util.List;

public final class FPMRecoded extends PolymerPlugin implements FPMPlugin {
    public static FPMRecoded INSTANCE;

    public static FakePlayerSaver fakePlayerSaver;
    public static FakePlayerManager fakePlayerManager;


    @Override
    public void onLoad() {
        Instances.setFPMPlugin(this);
    }

    @Override
    public void onPlEnable() {
        INSTANCE = this;
        fakePlayerSaver = new FakePlayerSaver(this);
        fakePlayerManager = new FakePlayerManager();
    }

    @Override
    public void onPlDisable() {

    }

    @Override
    public List<PolymerCommand> registerCommands() {
        return List.of(new FPMMainCommand());
    }

    @Override
    public void reload() {
        super.reload();

        fakePlayerSaver.reload();
    }

    @Override
    public String requireVersion() {
        return "1.4.4";
    }

    @Override
    public int requireApiVersion() {
        return 2;
    }
}
