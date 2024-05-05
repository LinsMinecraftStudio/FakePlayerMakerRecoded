package me.mmmjjkx.fpmbungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public final class FakePlayerMakerBungee extends Plugin {
    public static final String CHANNEL_NAME = "fpmbungee";

    public static FakePlayerManager manager;

    private Configuration configuration;

    @Override
    public void onEnable() {
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
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
