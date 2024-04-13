package org.lins.mmmjjkx.fakeplayermaker.commons;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public interface FPMPlugin {
    FileConfiguration getConfig();

    Logger getLogger();
}
