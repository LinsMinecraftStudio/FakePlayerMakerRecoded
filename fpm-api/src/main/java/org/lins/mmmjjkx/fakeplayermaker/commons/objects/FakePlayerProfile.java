package org.lins.mmmjjkx.fakeplayermaker.commons.objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record FakePlayerProfile(String name, UUID uuid) {
    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public UUID getUUID() {
        return uuid;
    }

    @NotNull
    public UUID getId() {
        return uuid;
    }
}
