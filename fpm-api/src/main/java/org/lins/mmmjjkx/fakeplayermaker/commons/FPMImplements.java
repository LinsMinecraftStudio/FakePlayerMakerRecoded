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

    static {
        current = findCurrent();
    }

    public FPMImplements() {}

    private static FPMImplements findCurrent() {
        Server server = Bukkit.getServer();
        String serverClassName = server.getClass().getName();
        String nmsVer = serverClassName
                .replaceAll("org.bukkit.craftbukkit.", "")
                .replaceAll(".CraftServer", "");
        String packageName = "org.lins.mmmjjkx.fakeplayermaker.impls." + nmsVer + ".FPMImpl";
        try {
            Class<?> impl = Class.forName(packageName);

            return (FPMImplements) impl.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unsupported server version: " + nmsVer);
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