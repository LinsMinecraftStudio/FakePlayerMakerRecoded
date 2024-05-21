package org.lins.mmmjjkx.fakeplayermaker.commons;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
            VersionedImplementation implementation = VersionedImplementation.get();
            nmsVer = implementation.getPackageName();
        }

        String className = "org.lins.mmmjjkx.fakeplayermaker.impls." + nmsVer + (folia ? ".FoliaFPMImpl" :".FPMImpl");

        try {
            Class<?> impl = Class.forName(className);
            return (FPMImplements) impl.newInstance();
        } catch (ClassNotFoundException e) {
            if (className.contains("FoliaFPMImpl")) {
                throw new UnsupportedOperationException("This version of the server does not support fpm using its Folia implementation.");
            } else {
                throw new UnsupportedOperationException("Unsupported server version: " + nmsVer);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handlePluginCompatability(IFPMPlayer player) {
        Player p = getCurrent().toBukkit(player);
        //luckperms compatibility
        {
            //inject permissible base and load User
            Object lpBukkitPlugin = Bukkit.getPluginManager().getPlugin("LuckPerms");

            if (lpBukkitPlugin != null) {
                if (!LuckPermsProvider.get().getUserManager().isLoaded(p.getUniqueId())) {
                    try {
                        //LuckPerms using different class loader
                        ClassLoader loader = lpBukkitPlugin.getClass().getClassLoader();

                        Object lpBootStrap;
                        try {
                            lpBootStrap = lpBukkitPlugin.getClass().getMethod("getBootstrap").invoke(lpBukkitPlugin);
                        } catch (NoSuchMethodException e) {
                            Field lpBootStrapField = lpBukkitPlugin.getClass().getDeclaredField("plugin");
                            lpBootStrapField.setAccessible(true);
                            lpBootStrap = lpBootStrapField.get(lpBukkitPlugin);
                        }
                        Object storage = lpBukkitPlugin.getClass().getMethod("getStorage").invoke(lpBukkitPlugin);
                        Class<?> userClass = loader.loadClass("me.lucko.luckperms.common.model.User");
                        Method method = storage.getClass().getDeclaredMethod("saveUser", userClass);

                        //saveUser
                        Object user = userClass.getDeclaredConstructors()[0].newInstance(p.getUniqueId(), lpBukkitPlugin);
                        method.invoke(storage, user);

                        Field humanEntityPermissibleField = p.getClass().getDeclaredField("perm");
                        humanEntityPermissibleField.setAccessible(true);
                        Object currentPermissible = humanEntityPermissibleField.get(p);
                        Class<?> LuckPermsPermissible = loader.loadClass("me.lucko.luckperms.bukkit.inject.permissible.LuckPermsPermissible");

                        if (currentPermissible.getClass().equals(LuckPermsPermissible)) return;

                        Object permissible = LuckPermsPermissible.getDeclaredConstructors()[0].newInstance(p, user, lpBukkitPlugin);
                        Object pluginLogger = lpBootStrap.getClass().getMethod("getPluginLogger").invoke(lpBootStrap);

                        Class<?> PermissibleInjector = loader.loadClass("me.lucko.luckperms.bukkit.inject.permissible.PermissibleInjector");
                        Class<?> PluginLogger = loader.loadClass("me.lucko.luckperms.common.plugin.logging.PluginLogger");

                        PermissibleInjector.getMethod("inject", Player.class, LuckPermsPermissible, PluginLogger).invoke(null, p, permissible, pluginLogger);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public abstract @NotNull IFPMPlayer createPlayer(@NotNull GameProfile profile, @NotNull String levelName, @NotNull UUID owner);

    public abstract void setupConnection(@NotNull IFPMPlayer player);

    public abstract void addPlayer(@NotNull IFPMPlayer player);

    public abstract void removePlayer(@NotNull IFPMPlayer player);

    public abstract @NotNull GameProfile getGameProfile(@NotNull IFPMPlayer player);

    public abstract Player toBukkit(@NotNull IFPMPlayer nmsPlayer);
}
