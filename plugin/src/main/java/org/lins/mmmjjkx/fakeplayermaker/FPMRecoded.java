package org.lins.mmmjjkx.fakeplayermaker;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.objects.PolymerConstants;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import io.github.linsminecraftstudio.polymer.utils.FileUtil;
import io.github.linsminecraftstudio.polymer.utils.ObjectConverter;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMMainCommand;
import org.lins.mmmjjkx.fakeplayermaker.commons.Instances;
import org.lins.mmmjjkx.fakeplayermaker.listeners.AutoRespawn;
import org.lins.mmmjjkx.fakeplayermaker.listeners.CommandListeners;
import org.lins.mmmjjkx.fakeplayermaker.listeners.Scheduling;
import org.lins.mmmjjkx.fakeplayermaker.util.FakePlayerManager;
import org.lins.mmmjjkx.fakeplayermaker.util.FakePlayerSaver;

import java.util.List;

public final class FPMRecoded extends PolymerPlugin {
    public static FPMRecoded INSTANCE;

    public static FakePlayerSaver fakePlayerSaver;
    public static FakePlayerManager fakePlayerManager;

    public void onLoad() {
        Instances.setFPM(this);
    }

    @Override
    public void onPlEnable() {
        INSTANCE = this;

        startMetrics(21829);

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

        Instances.setFakePlayerManager(fakePlayerManager);

        FileUtil.completeFile("config.yml");

        // Register listeners
        new AutoRespawn();
        new CommandListeners();
        new Scheduling();
        //end of register listeners

        if (getConfig().getBoolean("checkUpdate")) {
            new OtherUtils.Updater(111767, (ver, success) -> {
                if (success) {
                    if (ver.equals(getPluginVersion())) {
                        getComponentLogger().info(ObjectConverter.toComponent("&aYou are using the latest version!"));
                    } else {
                        getLogger().warning("There is a new version available! New version: " + ver + " | Your version: " + getPluginVersion());
                    }
                } else {
                    getComponentLogger().warn(ObjectConverter.toComponent("&4Failed to check for updates!"));
                }
            });
        }
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
        return "1.4.7";
    }

    @Override
    public int requireApiVersion() {
        return PolymerConstants.API_VERSION;
    }
}
