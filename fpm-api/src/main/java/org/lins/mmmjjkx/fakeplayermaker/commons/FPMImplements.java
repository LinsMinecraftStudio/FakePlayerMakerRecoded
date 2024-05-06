package org.lins.mmmjjkx.fakeplayermaker.commons;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class FPMImplements {

    @Getter
    private static final FPMImplements current;
    @Getter
    private static final boolean folia;

    static {
        boolean folia1;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia1 = true;
        } catch (ClassNotFoundException ignored) {
            folia1 = false;
        }
        folia = folia1;

        current = findCurrent();
    }

    public FPMImplements() {}

    private static FPMImplements findCurrent() {
        Server server = Bukkit.getServer();
        String serverClassName = server.getClass().getName();
        String nmsVer = serverClassName
                .replaceAll("org.bukkit.craftbukkit.", "")
                .replaceAll(".CraftServer", "");

        if (Instances.isVersionAtLeast1206()) {
            nmsVer = "v" + Instances.versionToCode(Bukkit.getMinecraftVersion());
        }

        String className = "org.lins.mmmjjkx.fakeplayermaker.impls." + nmsVer + (folia ? ".FoliaFPMImpl" :".FPMImpl");

        try {
            Class<?> impl = Class.forName(className);
            return (FPMImplements) impl.newInstance();
        } catch (ClassNotFoundException e) {
            if (className.contains("FoliaFPMImpl")) {
                throw new UnsupportedOperationException("This version of the server does not support fpm using Folia implementation.");
            } else {
                throw new UnsupportedOperationException("Unsupported server version: " + nmsVer);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract @NotNull IFPMPlayer createPlayer(@NotNull GameProfile profile, @NotNull String levelName, @NotNull UUID owner);

    public abstract void setupConnection(@NotNull IFPMPlayer player);

    public abstract void addPlayer(@NotNull IFPMPlayer player);

    public abstract void removePlayer(@NotNull IFPMPlayer player);

    public abstract @NotNull GameProfile getGameProfile(@NotNull IFPMPlayer player);

    public abstract Player toBukkit(@NotNull IFPMPlayer nmsPlayer);
}
