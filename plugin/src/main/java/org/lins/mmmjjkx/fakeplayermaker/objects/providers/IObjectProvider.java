package org.lins.mmmjjkx.fakeplayermaker.objects.providers;

import org.lins.mmmjjkx.fakeplayermaker.objects.wrapped.WrappedGameProfile;
import org.lins.mmmjjkx.fakeplayermaker.objects.wrapped.WrappedSessionService;

public interface IObjectProvider {
    Object minecraftCodec();

    Object codecHelper();

    WrappedSessionService sessionService();

    Object createProtocol(WrappedGameProfile profile, String accessToken);
}
