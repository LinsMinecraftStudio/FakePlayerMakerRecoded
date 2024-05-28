package org.lins.mmmjjkx.fakeplayermaker.util;

import com.mojang.authlib.GameProfile;
import io.github.linsminecraftstudio.polymer.utils.ObjectConverter;
import me.neznamy.tab.api.tablist.SortingManager;
import me.neznamy.tab.shared.TAB;
import me.neznamy.tab.shared.TabConstants;
import me.neznamy.tab.shared.features.sorting.Sorting;
import me.neznamy.tab.shared.platform.TabPlayer;
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
        IMPL.toBukkit(player).kick(Component.text("FPM: leaved from server"));
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

    public List<IFPMPlayer> getFakePlayers(UUID owner) {
        return fakePlayers.values().stream().filter(p -> p.getOwnerUUID().equals(owner)).toList();
    }

    private void setupNames(IFPMPlayer player) {
        Player player1 = IMPL.toBukkit(player);
        String displayNamePrefix = FPMRecoded.INSTANCE.getConfig().getString("fakePlayer.displayNamePrefix", "");

        if (!displayNamePrefix.isEmpty()) {
            Component component = ObjectConverter.toComponent(displayNamePrefix);
            component = component.append(Component.text(player1.getName()));

            player1.displayName(component);
            player1.playerListName(component);

            String finalDisplayName = displayNamePrefix + player1.getName();
            if (Bukkit.getPluginManager().isPluginEnabled("TAB")) {
                TabPlayer tabPlayer = TAB.getInstance().getPlayer(player1.getUniqueId());
                if (tabPlayer != null) {
                    tabPlayer.getProperty(TabConstants.Property.TABPREFIX).setTemporaryValue(finalDisplayName);

                    String groupName = FPMRecoded.INSTANCE.getConfig().getString("fakePlayer.defaultTabGroup", "");
                    if (!groupName.isBlank()) {
                        if (!tabPlayer.getGroup().equals(groupName)) {
                            return;
                        }

                        tabPlayer.setGroup(groupName);

                        SortingManager sortingManager = TAB.getInstance().getSortingManager();
                        if (sortingManager != null) {
                            Sorting sorting = (Sorting) sortingManager;
                            sorting.refresh(tabPlayer,true);
                        }
                    }
                }
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

        PlayerSettingsValueCollection settings = FPMRecoded.INSTANCE.getConfig().getBoolean("fakePlayer.randomizePing.enabled", true)
                ? new PlayerSettingsValueCollection(
                        FPMRecoded.INSTANCE.getConfig().getInt("fakePlayer.randomizePing.min", 20),
                        FPMRecoded.INSTANCE.getConfig().getInt("fakePlayer.randomizePing.max", 200)
                ) : PlayerSettingsValueCollection.EMPTY;

        IMPL.setupConnection(player, settings);
        IMPL.addPlayer(player);

        FPMImplements.handlePluginCompatability(player);

        SetupValueCollection collection = new SetupValueCollection(
                FPMRecoded.INSTANCE.getConfig().getBoolean("fakePlayer.invulnerable", true),
                FPMRecoded.INSTANCE.getConfig().getDouble("fakePlayer.maxHealth", 20)
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

        setupNames(player);

        PlayerActionImplements.getCurrent().setupValues(player, collection);
    }
}
