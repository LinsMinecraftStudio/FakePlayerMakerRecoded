package org.lins.mmmjjkx.fakeplayermaker.commons;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Server;

public abstract class PlayerActionImplements {
    @Getter
    private static final PlayerActionImplements current;

    static {
        current = findCurrent();
    }

    public PlayerActionImplements() {}

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

    public abstract void mountNearest(Object player, int radius);

    public abstract void dismount(Object player);

    public abstract void lookAt(Object player, double x, double y, double z);

    public abstract void chat(Object player, String message);

    public abstract void sneak(Object player, boolean sneak);

    public abstract void setupValues(Object player, SetupValueCollection values);

    public abstract void interact(Object player, InteractHand hand);
}
