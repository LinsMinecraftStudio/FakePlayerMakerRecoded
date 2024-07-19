package org.lins.mmmjjkx.fakeplayermaker.nmsservice;

import com.destroystokyo.paper.profile.PaperMinecraftSessionService;
import com.mojang.authlib.Environment;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import org.lins.mmmjjkx.fakeplayermaker.commons.Instances;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.FakePlayerProfile;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;

import java.net.InetAddress;
import java.net.Proxy;
import java.util.List;
import java.util.UUID;

public class FPMSessionService extends PaperMinecraftSessionService {
    protected FPMSessionService(ServicesKeySet servicesKeySet, Proxy proxy, Environment env) {
        super(servicesKeySet, proxy, env);
    }

    @Override
    public void joinServer(UUID profileId, String authenticationToken, String serverId) throws AuthenticationException {
        List<String> names = Instances.getFakePlayerManager().getFakePlayerNames();

        String name = names.stream().filter(n -> UUID.nameUUIDFromBytes(n.getBytes()).equals(profileId)).findFirst().orElse(null);

        if (Instances.getFakePlayerManager().get(name) != null) {
            return;
        }

        super.joinServer(profileId, authenticationToken, serverId);
    }

    public ProfileResult hasJoinedServer(String profileName, String serverId, InetAddress address) throws AuthenticationUnavailableException {
        IFPMPlayer fakePlayer = Instances.getFakePlayerManager().get(profileName);

        if (fakePlayer != null) {
            FakePlayerProfile profile = fakePlayer.getFakePlayerProfile();
            return new ProfileResult(new GameProfile(profile.getUUID(), profile.getName()));
        }

        return super.hasJoinedServer(profileName, serverId, address);
    }
}
