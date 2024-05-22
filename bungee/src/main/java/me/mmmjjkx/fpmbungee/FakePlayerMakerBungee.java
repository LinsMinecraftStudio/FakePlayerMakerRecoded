package me.mmmjjkx.fpmbungee;

import me.mmmjjkx.fpmbungee.fakers.TablistFaker;
import me.mmmjjkx.fpmbungee.utils.FakePlayerManager;
import me.mmmjjkx.fpmbungee.utils.Updater;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public final class FakePlayerMakerBungee extends Plugin {
    public static final String CHANNEL_NAME = "fpmbungee";

    public static FakePlayerManager manager;

    private static TablistFaker tablistFaker;
    private static FakePlayerMakerBungee instance;

    private Configuration configuration;

    @Override
    public void onEnable() {
        instance = this;

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
                ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
                configuration = provider.load(configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        manager = new FakePlayerManager(this);

        if (configuration.getBoolean("fakers.tablist.enabled", false)) {
            tablistFaker = new TablistFaker(configuration);
        }

        getLogger().info("  _____     _        ____  _                       __  __       _             ____                              \n" +
                " |  ___|_ _| | _____|  _ \\| | __ _ _   _  ___ _ __|  \\/  | __ _| | _____ _ __| __ ) _   _ _ __   __ _  ___  ___ \n" +
                " | |_ / _` | |/ / _ \\ |_) | |/ _` | | | |/ _ \\ '__| |\\/| |/ _` | |/ / _ \\ '__|  _ \\| | | | '_ \\ / _` |/ _ \\/ _ \\\n" +
                " |  _| (_| |   <  __/  __/| | (_| | |_| |  __/ |  | |  | | (_| |   <  __/ |  | |_) | |_| | | | | (_| |  __/  __/\n" +
                " |_|  \\__,_|_|\\_\\___|_|   |_|\\__,_|\\__, |\\___|_|  |_|  |_|\\__,_|_|\\_\\___|_|  |____/ \\__,_|_| |_|\\__, |\\___|\\___|\n" +
                "                                   |___/                                                        |___/           " +
                "\n\n Made by mmmjjkx(lijingong11)");

        if (configuration.getBoolean("checkUpdates")) {
            new Updater(0, (s, b) -> {
                if (b) {
                    if (s.equals(getDescription().getVersion())) {
                        getLogger().info("&aYou are using the latest version!");
                    } else {
                        getLogger().warning("There is a new version available! New version: " + s + " | Your version: " + getDescription().getVersion());
                    }
                } else {
                    getLogger().warning("&ailed to check for updates!");
                }
            });
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Nullable
    public static TablistFaker getTablistFaker() {
        return tablistFaker;
    }

    @Nonnull
    public static FakePlayerMakerBungee getInstance() {
        return instance;
    }
}
