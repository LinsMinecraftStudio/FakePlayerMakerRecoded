package org.lins.mmmjjkx.fakeplayermaker.commons;

import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Server;

public abstract class FPMImplements {
    private static final FPMImplements current;

    static {
        current = findCurrent();
    }

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

    public static FPMImplements getCurrent() {
        return current;
    }

    public abstract Object createPlayer(GameProfile profile, String levelName);

    public abstract void removePlayer(Object player);
}
