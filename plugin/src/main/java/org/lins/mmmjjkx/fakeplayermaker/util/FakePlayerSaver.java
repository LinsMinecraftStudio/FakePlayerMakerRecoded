package org.lins.mmmjjkx.fakeplayermaker.util;

import com.github.steveice10.mc.auth.data.GameProfile;
import io.github.linsminecraftstudio.polymer.objects.plugin.file.SingleFileStorage;
import io.github.linsminecraftstudio.polymer.utils.IterableUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.configuration.ConfigurationSection;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.FakePlayerProfile;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.objects.MCClient;
import org.lins.mmmjjkx.fakeplayermaker.objects.wrapped.WrappedGameProfile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public class FakePlayerSaver extends SingleFileStorage {
    public static final UUID NO_OWNER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final Map<String, MCClient> fakePlayers;

    public FakePlayerSaver(FPMRecoded fpm) {
        super(fpm, new File(fpm.getDataFolder(), "fakeplayers.yml"));

        fakePlayers = new HashMap<>();
        setup();
    }

    @SneakyThrows
    public void saveFakePlayer(IFPMPlayer player) {
        FakePlayerProfile profile = player.getFakePlayerProfile();
        ConfigurationSection section = createSection(profile.getName());
        section.set("uuid", profile.getId().toString());
        section.set("owner", player.getOwnerUUID().toString());

        MCClient client = (MCClient) player;
        WrappedGameProfile wrappedProfile = client.getGameProfile();
        GameProfile gameProfile = (GameProfile) wrappedProfile.getHandle();

        Optional<GameProfile.Property> skinInfo = IterableUtil.getIf(gameProfile.getProperties(), prop -> prop.getName().equals("textures"));
        skinInfo.ifPresent(s -> {
            String url = s.getValue();
            String signature = s.getSignature();
            if (!url.isBlank() && !signature.isBlank()) {
                section.set("skin", url);
                section.set("skin_signature", signature);
            }
        });
    }

    private void setup() {
        for (String name : getKeys(false)) {
            ConfigurationSection section = getConfigurationSection(name);
            if (section == null) {
                continue;
            }

            String uuid_str = section.getString("uuid");
            UUID uuid = uuid_str == null ? UUID.nameUUIDFromBytes(name.getBytes()) : UUID.fromString(uuid_str);

            section.set("uuid", uuid.toString());

            String url = section.getString("skin");
            String signature = section.getString("skin_signature");

            String owner_str = section.getString("owner", "");

            UUID owner = owner_str.isBlank() ? NO_OWNER_UUID : UUID.fromString(owner_str);

            WrappedGameProfile profile = WrappedGameProfile.create(uuid, name);

            if (url != null && !url.isBlank() && (signature != null && !signature.isBlank())) {
                profile.putProperty("textures", url, signature);
            }

            String ip = FPMRecoded.INSTANCE.getConfig().getString("entrance.ip", "127.0.0.1");
            int port = FPMRecoded.INSTANCE.getConfig().getInt("entrance.port", 25565);
            fakePlayers.put(name, new MCClient(ip, port, owner, CommonUtils.getUnAllocatedIPPort(), new ImmutablePair<>(name, uuid)));
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
