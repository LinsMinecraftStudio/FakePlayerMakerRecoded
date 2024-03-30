package org.lins.mmmjjkx.fakeplayermaker.commons;

import org.bukkit.Bukkit;
import org.bukkit.Server;

public abstract class PlayerActionImplements {
    private static final PlayerActionImplements current;

    static {
        current = findCurrent();
    }

    private static PlayerActionImplements findCurrent() {
        Server server = Bukkit.getServer();
        String serverClassName = server.getClass().getName();
        String nmsVer = serverClassName
                .replaceAll("org.bukkit.craftbukkit.", "")
                .replaceAll(".CraftServer", "");
        String packageName = "org.lins.mmmjjkx.fakeplayermaker.impls." + nmsVer + ".ActionImpl";
        try {
            Class<?> impl = Class.forName(packageName);

            return (PlayerActionImplements) impl.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unsupported server version: " + nmsVer);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static PlayerActionImplements getCurrent() {
        return current;
    }

    public abstract void mountNearest(Object player, int radius);

    public abstract void dismount(Object player);
}
