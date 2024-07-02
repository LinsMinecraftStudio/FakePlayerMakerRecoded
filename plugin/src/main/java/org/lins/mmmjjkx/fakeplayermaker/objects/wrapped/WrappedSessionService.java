package org.lins.mmmjjkx.fakeplayermaker.objects.wrapped;

import com.github.steveice10.mc.auth.service.SessionService;
import lombok.RequiredArgsConstructor;

import java.net.Proxy;

@RequiredArgsConstructor
public class WrappedSessionService {
    private final SessionService sessionService;

    public void setProxy(Proxy proxy) {
        try {
            sessionService.setProxy(proxy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static WrappedSessionService create() {
        try {
            return new WrappedSessionService(new SessionService());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SessionService getHandle() {
        return sessionService;
    }
}
