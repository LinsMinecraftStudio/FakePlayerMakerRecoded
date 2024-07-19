package org.lins.mmmjjkx.fakeplayermaker.objects;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public record SettingValuesCollection(
        boolean invulnerable,
        boolean pickupItems,
        int mountDistance,
        boolean autoRespawn,
        List<String> bannedCommands,
        boolean joinIfOwnerJoin,
        boolean quitIfOwnerQuit
) implements ConfigurationSerializable {

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of(
                "invulnerable", invulnerable,
                "pickupItems", pickupItems,
                "mount-distance", mountDistance,
                "auto-respawn", autoRespawn,
                "bannedCommands", bannedCommands,
                "joinIfOwnerJoin", joinIfOwnerJoin,
                "quitIfOwnerQuit", quitIfOwnerQuit
        );
    }

    public static @NotNull SettingValuesCollection deserialize(@NotNull Map<String, Object> args) {
        boolean invulnerable = (boolean) args.get("invulnerable");
        boolean pickupItems = (boolean) args.get("pickupItems");
        int mountDistance = NumberConversions.toInt(args.get("mount-distance"));
        boolean autoRespawn = (boolean) args.get("auto-respawn");
        Object bannedCommandsObj = args.get("bannedCommands");
        boolean joinIfOwnerJoin = (boolean) args.get("joinIfOwnerJoin");
        boolean quitIfOwnerQuit = (boolean) args.get("quitIfOwnerQuit");
        if (bannedCommandsObj instanceof List<?> o) {
            List<String> bannedCommands = (List<String>) o;
            return new SettingValuesCollection(invulnerable, pickupItems, mountDistance, autoRespawn, bannedCommands, joinIfOwnerJoin, quitIfOwnerQuit);
        } else {
            return new SettingValuesCollection(invulnerable, pickupItems, mountDistance, autoRespawn, List.of(), joinIfOwnerJoin, quitIfOwnerQuit);
        }
    }
}
