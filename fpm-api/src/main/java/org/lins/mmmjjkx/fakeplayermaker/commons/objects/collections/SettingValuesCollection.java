package org.lins.mmmjjkx.fakeplayermaker.commons.objects.collections;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public record SettingValuesCollection(boolean invulnerable, boolean pickupItems, int mountDistance, boolean autoRespawn, List<String> bannedCommandsPrefix) implements ConfigurationSerializable {
    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of("invulnerable", invulnerable, "pickupItems", pickupItems, "mount-distance", mountDistance, "auto-respawn", autoRespawn, "bannedCommandsPrefix", bannedCommandsPrefix);
    }

    public static @NotNull SettingValuesCollection deserialize(@NotNull Map<String, Object> args) {
        boolean invulnerable = (boolean) args.get("invulnerable");
        boolean pickupItems = (boolean) args.get("pickupItems");
        int mountDistance = NumberConversions.toInt(args.get("mount-distance"));
        boolean autoRespawn = (boolean) args.get("auto-respawn");
        Object bannedCommandsPrefixObj = args.get("bannedCommandsPrefix");
        if (bannedCommandsPrefixObj instanceof List<?> o) {
            List<String> bannedCommandsPrefix = (List<String>) o;
            return new SettingValuesCollection(invulnerable, pickupItems, mountDistance, autoRespawn, bannedCommandsPrefix);
        } else {
            return new SettingValuesCollection(invulnerable, pickupItems, mountDistance, autoRespawn, List.of());
        }
    }
}
