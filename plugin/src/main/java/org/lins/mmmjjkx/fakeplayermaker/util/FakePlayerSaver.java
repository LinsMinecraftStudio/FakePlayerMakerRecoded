package org.lins.mmmjjkx.fakeplayermaker.util;

import com.mojang.authlib.GameProfile;
import io.github.linsminecraftstudio.polymer.objects.plugin.file.SingleFileStorage;
import io.github.linsminecraftstudio.polymer.utils.ObjectConverter;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;
import org.lins.mmmjjkx.fakeplayermaker.commons.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.commons.SkinUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class FakePlayerSaver extends SingleFileStorage {
    public static final UUID NO_OWNER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private static final FPMImplements IMPL = FPMImplements.getCurrent();
    private final Map<String, IFPMPlayer> fakePlayers;
    private final Map<GameProfile, Location> readyToTeleport;

    public FakePlayerSaver(FPMRecoded fpm) {
        super(fpm, new File(fpm.getDataFolder(), "fakeplayers.yml"));

        fakePlayers = new HashMap<>();
        readyToTeleport = new HashMap<>();
        setup();
    }

    @SneakyThrows
    public void saveFakePlayer(IFPMPlayer player) {
        GameProfile profile = IMPL.getGameProfile(player);
        ConfigurationSection section = createSection(profile.getName());
        section.set("uuid", profile.getId().toString());
        section.set("owner", player.getOwnerUUID().toString());
        Player bk = IMPL.toBukkit(player);
        Pair<String, String> skinInfo = SkinUtils.getSkinInfo(bk);
        String url = skinInfo.getLeft();
        String signature = skinInfo.getRight();
        if (!url.isBlank() && !signature.isBlank()) {
            section.set("skin", url);
            section.set("skin_signature", signature);
        }
        section.set("location", ObjectConverter.toLocationString(bk.getLocation()));
    }

    private void setup() {
        for (String name : getKeys(false)) {
            ConfigurationSection section = getConfigurationSection(name);
            if (section == null) {
                continue;
            }
            String uuid_str = section.getString("uuid");
            UUID uuid = uuid_str == null ? UUID.randomUUID() : UUID.fromString(uuid_str);

            section.set("uuid", uuid.toString());

            String url = section.getString("skin");
            String signature = section.getString("skin_signature");

            String location_str = section.getString("location");
            String owner_str = section.getString("owner", "");

            UUID owner = owner_str.isBlank() ? NO_OWNER_UUID : UUID.fromString(owner_str);

            World first = Bukkit.getWorlds().get(0);

            GameProfile profile = new GameProfile(uuid, name);

            IFPMPlayer player = IMPL.createPlayer(profile, first.getName(), owner);
            Player bk = IMPL.toBukkit(player);
            if (location_str != null) {
                readyToTeleport.put(profile, ObjectConverter.toLocation(location_str));
            }
            if (url != null && !url.isBlank() && (signature != null && !signature.isBlank())) {
                SkinUtils.setSkin(bk, url, signature);
            }
            fakePlayers.put(name, player);
        }
    }

    public void removeFakePlayer(String name) {
        set(name, null);
        fakePlayers.remove(name);
    }

    public void reload() {
        super.reload();

        fakePlayers.clear();
        setup();
    }
}
