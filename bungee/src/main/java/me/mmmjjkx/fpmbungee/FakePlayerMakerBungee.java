package me.mmmjjkx.fpmbungee;

import me.mmmjjkx.fpmbungee.fakers.MotdPlayerFaker;
import me.mmmjjkx.fpmbungee.fakers.TablistFaker;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public final class FakePlayerMakerBungee extends Plugin {
    public static final String CHANNEL_NAME = "fpmbungee";

    public static FakePlayerManager manager;

    private static TablistFaker tablistFaker;

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

        if (configuration.getBoolean("fakers.motd.enabled", false)) {
            MotdPlayerFaker faker = new MotdPlayerFaker(configuration.getSection("fakers.motd"));
            getProxy().getPluginManager().registerListener(this, faker);
        }

        if (configuration.getBoolean("fakers.tablist.enabled", false)) {
            tablistFaker = new TablistFaker(configuration);
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
}
