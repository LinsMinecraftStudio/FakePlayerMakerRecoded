package org.lins.mmmjjkx.fakeplayermaker.objects.providers;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.SessionService;
import org.bukkit.Bukkit;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftCodec;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.objects.wrapped.WrappedGameProfile;
import org.lins.mmmjjkx.fakeplayermaker.objects.wrapped.WrappedSessionService;

public class GeyserObjectProvider implements IObjectProvider {
    @Override
    public Object minecraftCodec() {
        return MinecraftCodec.CODEC;
    }

    @Override
    public Object codecHelper() {
        return MinecraftCodec.CODEC.getHelperFactory().get();
    }

    @Override
    public WrappedSessionService sessionService() {
        return new WrappedSessionService(new SelfVerifyedSessionService());
    }

    @Override
    public Object createProtocol(WrappedGameProfile profile, String accessToken) {
        return new MinecraftProtocol((GameProfile) profile.getHandle(), accessToken);
    }

    private static class SelfVerifyedSessionService extends SessionService {
        @Override
        public void joinServer(GameProfile profile, String authenticationToken, String serverId) throws RequestException {
            if (Bukkit.getOnlineMode()) {
                return;
            }

            super.joinServer(profile, authenticationToken, serverId);
        }

        @Override
        public GameProfile getProfileByServer(String name, String serverId) throws RequestException {
            if (Bukkit.getOnlineMode()) {
                IFPMPlayer player = FPMRecoded.fakePlayerManager.get(name);
                if (player != null) {
                    return new GameProfile(player.getFakePlayerProfile().getId(), player.getFakePlayerProfile().getName());
                }
            }

            return super.getProfileByServer(name, serverId);
        }
    }
}
