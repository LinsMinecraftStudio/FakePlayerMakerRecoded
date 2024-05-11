package org.lins.mmmjjkx.fakeplayermaker.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.linsminecraftstudio.polymer.utils.IterableUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.Set;

public class SkinUtils {
    public static boolean changeSkin(CommandSender requester, Player player, String targetName) {
        PlayerProfile playerProfile = player.getPlayerProfile();
        try {
            URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + targetName);

            InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());

            String uuid = JsonParser.parseReader(reader_0).getAsJsonObject().get("id").getAsString();

            URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");

            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());

            JsonObject properties = JsonParser.parseReader(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

            String value = properties.get("value").getAsString();
            String signature = properties.get("signature").getAsString();

            playerProfile.setProperty(new ProfileProperty("textures", value, signature));
            player.setPlayerProfile(playerProfile);

            return true;
        } catch (IllegalStateException | NullPointerException exception) {
            return false;
        } catch (IOException exception) {
            FPMRecoded.INSTANCE.getMessageHandler().sendMessage(requester, "command.skin_not_found");
            return false;
        }
    }

    @NotNull
    public static Pair<String, String> getSkinInfo(Player player) {
        PlayerProfile playerProfile = player.getPlayerProfile();
        Set<ProfileProperty> properties = playerProfile.getProperties();
        Optional<ProfileProperty> profileProperty = IterableUtil.getIf(properties, p -> p.getName().equals("textures"));
        if (profileProperty.isPresent()) {
            String value = profileProperty.get().getValue();
            String signature = profileProperty.get().getSignature();
            return Pair.of(value, signature);
        }
        return Pair.of("", "");
    }

    public static void setSkin(Player bk, String url, String signature) {
        PlayerProfile playerProfile = bk.getPlayerProfile();
        playerProfile.setProperty(new ProfileProperty("textures", url, signature));
        bk.setPlayerProfile(playerProfile);
    }
}
