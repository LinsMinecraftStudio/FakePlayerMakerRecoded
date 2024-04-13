package org.lins.mmmjjkx.fakeplayermaker.util;

import com.mojang.authlib.GameProfile;
import io.github.linsminecraftstudio.polymer.objects.plugin.file.SingleFileStorage;
import io.github.linsminecraftstudio.polymer.utils.ObjectConverter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;
import org.lins.mmmjjkx.fakeplayermaker.commons.Ownable;
import org.lins.mmmjjkx.fakeplayermaker.commons.SkinUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FakePlayerSaver extends SingleFileStorage {
    public static final UUID NO_OWNER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private static final FPMImplements IMPL = FPMImplements.getCurrent();
    private final Map<String, Object> fakePlayers;

    public FakePlayerSaver(FPMRecoded fpm) {
        super(fpm, new File(fpm.getDataFolder(), "fakeplayers.yml"));

        fakePlayers = new HashMap<>();
    }

    public void saveFakePlayer(Object player) {
        GameProfile profile = IMPL.getGameProfile(player);
        ConfigurationSection section = createSection(profile.getName());
        section.set("uuid", profile.getId().toString());
        if (player instanceof Ownable o) {
            section.set("owner", o.getOwnerUUID().toString());
        }
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
                section = createSection(name);
            }
            String uuid_str = section.getString("uuid");
            UUID uuid = uuid_str == null? UUID.randomUUID() : UUID.fromString(uuid_str);
            String url = section.getString("skin");
            String signature = section.getString("skin_signature");

            String location_str = section.getString("location");
            String owner_str = section.getString("owner");

            //who is the owner?
            UUID owner = owner_str == null ? NO_OWNER_UUID : UUID.fromString(owner_str);

            Object player = IMPL.createPlayer(new GameProfile(uuid, name), "", owner);
        }
    }

    public void removeFakePlayer(String name) {
        set(name, null);
    }

    public Object getOnlineFakePlayer(String name) {
        Player bk = Bukkit.getPlayer(name);
        if (bk != null) {
            return IMPL.toNms(bk);
        }
        return null;
    }

    public Map<String, Object> getFakePlayers() {
        return fakePlayers;
    }

    @Override
    protected void reload(YamlConfiguration refresh) {
        super.reload(refresh);

        fakePlayers.clear();
    }
}
