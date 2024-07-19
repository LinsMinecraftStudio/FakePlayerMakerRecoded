package org.lins.mmmjjkx.fakeplayermaker.nmsservice;

import com.destroystokyo.paper.profile.PaperGameProfileRepository;
import com.mojang.authlib.Environment;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import org.lins.mmmjjkx.fakeplayermaker.commons.Instances;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;

import java.net.Proxy;
import java.util.UUID;

public class FPMGameProfileRepository extends PaperGameProfileRepository {
    public FPMGameProfileRepository(Proxy proxy, Environment environment) {
        super(proxy, environment);
    }

    @Override
    public void findProfilesByNames(String[] names, ProfileLookupCallback callback) {
        for (String name : names) {
            IFPMPlayer fakePlayer = Instances.getFakePlayerManager().get(name);
            if (fakePlayer != null) {
                UUID uuid = fakePlayer.getFakePlayerProfile().uuid();
                callback.onProfileLookupSucceeded(new GameProfile(uuid, name));
                return;
            }
        }

        super.findProfilesByNames(names, callback);
    }
}
