package org.lins.mmmjjkx.fakeplayermaker.util;

import com.mojang.authlib.GameProfile;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;
import org.lins.mmmjjkx.fakeplayermaker.commons.PlayerActionImplements;
import org.lins.mmmjjkx.fakeplayermaker.commons.ValueCollection;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FakePlayerManager {
    private final FPMImplements IMPL = FPMImplements.getCurrent();
    private Map<String, Object> fakePlayers;

    public FakePlayerManager() {
        fakePlayers = FPMRecoded.fakePlayerSaver.getFakePlayers();

        for (String playerName : fakePlayers.keySet()) {
            join(playerName);
        }
    }

    public void reload() {
        fakePlayers = FPMRecoded.fakePlayerSaver.getFakePlayers();

        for (String playerName : fakePlayers.keySet()) {
            IMPL.removePlayer(fakePlayers.get(playerName));
        }

        for (String playerName : fakePlayers.keySet()) {
            join(playerName);
        }
    }

    public void join(String playerName) {
        Object player = fakePlayers.get(playerName);
        if (player == null) {
            return;
        }
        IMPL.setupConnection(player);
        IMPL.addPlayer(player);

        ValueCollection collection = new ValueCollection(
                FPMRecoded.INSTANCE.getConfig().getBoolean("invulnerable", true),
                FPMRecoded.INSTANCE.getConfig().getDouble("maxHealth", 20)
        );

        GameProfile profile = IMPL.getGameProfile(player);
        Location location = FPMRecoded.fakePlayerSaver.getReadyToTeleport().get(profile);

        if (location != null) {
            Player bk = IMPL.toBukkit(player);
            bk.teleport(location);
        }

        PlayerActionImplements.getCurrent().setupValues(player, collection);
    }

    public void leave(String playerName) {
        Object player = fakePlayers.get(playerName);
        if (player == null) {
            return;
        }
        IMPL.removePlayer(player);
    }

    public void remove(String playerName) {
        leave(playerName);
        fakePlayers.remove(playerName);
        FPMRecoded.fakePlayerSaver.removeFakePlayer(playerName);
    }

    public Object create(String playerName, UUID owner, String levelName) {
        UUID uuid = UUID.nameUUIDFromBytes(playerName.getBytes());
        GameProfile profile = new GameProfile(uuid, playerName);
        Object player = IMPL.createPlayer(profile, levelName, owner);
        fakePlayers.put(playerName, player);
        FPMRecoded.fakePlayerSaver.saveFakePlayer(player);

        return player;
    }

    public List<String> getFakePlayerNames() {
        return fakePlayers.keySet().stream().toList();
    }

    /**
     * Get a fake player by name.
     * @param playerName The name of the fake player.
     * @return A pair of a boolean and an object.<br>
     *         The boolean indicates whether the fake player is online or not.<br>
     *         The object is the fake player object.
     */
    public Pair<Boolean, Object> getFakePlayer(String playerName) {
        Object player = fakePlayers.get(playerName);

        if (player == null) {
            return Pair.of(false, null);
        }

        Player bk = IMPL.toBukkit(player);
        if (Bukkit.getPlayer(bk.getUniqueId()) == null) {
            return Pair.of(false, player);
        }

        return Pair.of(true, player);
    }
}
