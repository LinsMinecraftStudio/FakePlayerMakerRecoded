package org.lins.mmmjjkx.fakeplayermaker;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMMainCommand;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMPlugin;
import org.lins.mmmjjkx.fakeplayermaker.commons.Instances;
import org.lins.mmmjjkx.fakeplayermaker.listeners.AutoRespawn;
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

        getLogger().info("""
                
                 _____     _        ____  _                       __  __       _            \s
                |  ___|_ _| | _____|  _ \\| | __ _ _   _  ___ _ __|  \\/  | __ _| | _____ _ __\s
                | |_ / _` | |/ / _ \\ |_) | |/ _` | | | |/ _ \\ '__| |\\/| |/ _` | |/ / _ \\ '__|
                |  _| (_| |   <  __/  __/| | (_| | |_| |  __/ |  | |  | | (_| |   <  __/ |  \s
                |_|  \\__,_|_|\\_\\___|_|   |_|\\__,_|\\__, |\\___|_|  |_|  |_|\\__,_|_|\\_\\___|_|  \s
                                                  |___/                                     \s
                
                Made by mmmjjkx(lijinhong11).""");

        fakePlayerSaver = new FakePlayerSaver(this);
        fakePlayerManager = new FakePlayerManager();

        new AutoRespawn();
    }

    @Override
    public void onPlDisable() {
    }

    @Override
    public List<PolymerCommand> registerCommands() {
        return List.of(FPMMainCommand.INSTANCE);
    }

    @Override
    public void reload() {
        super.reload();

        fakePlayerSaver.reload();
        fakePlayerManager.reload();
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
