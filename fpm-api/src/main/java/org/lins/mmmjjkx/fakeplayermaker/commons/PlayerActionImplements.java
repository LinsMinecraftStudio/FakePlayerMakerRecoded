package org.lins.mmmjjkx.fakeplayermaker.commons;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Entity;

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

        if (Instances.isVersionAtLeast1206()) {
           VersionedImplementation implementation = VersionedImplementation.get();
            nmsVer = implementation.getPackageName();
        }

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

    public abstract void mountNearest(IFPMPlayer player, int radius);

    public abstract void dismount(IFPMPlayer player);

    public abstract void lookAt(IFPMPlayer player, double x, double y, double z);

    public abstract void sneak(IFPMPlayer player, boolean sneak);

    public abstract void setupValues(IFPMPlayer player, SetupValueCollection values);

    public abstract void interact(IFPMPlayer player, InteractHand hand);

    public abstract void attack(IFPMPlayer player, Entity bukkitEntity);
}
