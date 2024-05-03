package org.lins.mmmjjkx.fakeplayermaker.util;

import com.mojang.authlib.GameProfile;
import io.github.linsminecraftstudio.polymer.utils.ObjectConverter;
import me.neznamy.tab.shared.TAB;
import me.neznamy.tab.shared.TabConstants;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FakePlayerManager implements IFakePlayerManager {
    private final FPMImplements IMPL = FPMImplements.getCurrent();
    private Map<String, IFPMPlayer> fakePlayers;

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
        IFPMPlayer player = fakePlayers.get(playerName);
        if (player == null) {
            return;
        }

        join(player);
    }

    public void leave(String playerName) {
        IFPMPlayer player = fakePlayers.get(playerName);
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

    public IFPMPlayer create(String playerName, UUID owner, String levelName) {
        UUID uuid = UUID.nameUUIDFromBytes(playerName.getBytes());
        GameProfile profile = new GameProfile(uuid, playerName);
        IFPMPlayer player = IMPL.createPlayer(profile, levelName, owner);
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
    public Pair<Boolean, IFPMPlayer> getFakePlayer(String playerName) {
        IFPMPlayer player = fakePlayers.get(playerName);

        if (player == null) {
            return Pair.of(false, null);
        }

        Player bk = IMPL.toBukkit(player);
        if (Bukkit.getPlayer(bk.getUniqueId()) == null) {
            return Pair.of(false, player);
        }

        return Pair.of(true, player);
    }

    private void setupDisplayName(IFPMPlayer player) {
        Player player1 = IMPL.toBukkit(player);
        String displayNamePrefix = FPMRecoded.INSTANCE.getConfig().getString("displayNamePrefix", "");

        if (!displayNamePrefix.isEmpty()) {
            Component component = ObjectConverter.toComponent(displayNamePrefix);
            component = component.append(Component.text(player1.getName()));

            player1.displayName(component);
            player1.playerListName(component);

            String finalDisplayName = displayNamePrefix + player1.getName();
            if (Bukkit.getPluginManager().isPluginEnabled("TAB")) {
                Objects.requireNonNull(TAB.getInstance().getPlayer(player1.getUniqueId())).getProperty(TabConstants.Property.TABPREFIX).setTemporaryValue(finalDisplayName);
            }
        }
    }

    @Override
    public @NotNull IFPMPlayer create(UUID owner, String name) {
        Location loc = FPMRecoded.INSTANCE.getConfig().getLocation("default-spawn-location", Bukkit.getWorlds().get(0).getSpawnLocation());
        return create(name, owner, loc.getWorld().getName());
    }

    @Override
    public @Nullable IFPMPlayer get(String name) {
        return getFakePlayer(name).getRight();
    }

    @Override
    public void join(IFPMPlayer player) {
        GameProfile profile = IMPL.getGameProfile(player);
        if (!fakePlayers.containsKey(profile.getName())) {
            fakePlayers.put(profile.getName(), player);
        }

        IMPL.setupConnection(player);
        IMPL.addPlayer(player);

        SetupValueCollection collection = new SetupValueCollection(
                FPMRecoded.INSTANCE.getConfig().getBoolean("invulnerable", true),
                FPMRecoded.INSTANCE.getConfig().getDouble("maxHealth", 20)
        );

        Location location = FPMRecoded.fakePlayerSaver.getReadyToTeleport().get(profile);

        if (location != null) {
            Player bk = IMPL.toBukkit(player);

            if (FPMImplements.isFolia()) {
                bk.teleportAsync(location).join();
            } else {
                bk.teleport(location);
            }
        }
        FPMRecoded.fakePlayerSaver.getReadyToTeleport().remove(profile);

        setupDisplayName(player);

        PlayerActionImplements.getCurrent().setupValues(player, collection);
    }
}
