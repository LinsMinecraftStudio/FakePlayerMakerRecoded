package org.lins.mmmjjkx.fakeplayermaker.util;

import com.mojang.authlib.GameProfile;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FakePlayerManager {
    private final FPMImplements IMPL = FPMImplements.getCurrent();
    private Map<String, Object> fakePlayers;

    public FakePlayerManager() {
        fakePlayers = FPMRecoded.fakePlayerSaver.getFakePlayers();
    }

    public void reload() {
        fakePlayers = FPMRecoded.fakePlayerSaver.getFakePlayers();

        for (String playerName : fakePlayers.keySet()) {
            IMPL.removePlayer(fakePlayers.get(playerName));
        }

        for (String playerName : fakePlayers.keySet()) {
            Object player = fakePlayers.get(playerName);
            IMPL.addPlayer(player);
        }
    }

    public void join(String playerName) {
        Object player = fakePlayers.get(playerName);
        if (player == null) {
            return;
        }
        IMPL.setupConnection(player);
        IMPL.addPlayer(player);
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

    public void create(String playerName, UUID owner, String levelName) {
        UUID uuid = UUID.nameUUIDFromBytes(playerName.getBytes());
        GameProfile profile = new GameProfile(uuid, playerName);
        Object player = IMPL.createPlayer(profile, levelName, owner);
        fakePlayers.put(playerName, player);
        FPMRecoded.fakePlayerSaver.saveFakePlayer(player);
    }

    public List<String> getFakePlayerNames() {
        return fakePlayers.keySet().stream().toList();
    }
}
