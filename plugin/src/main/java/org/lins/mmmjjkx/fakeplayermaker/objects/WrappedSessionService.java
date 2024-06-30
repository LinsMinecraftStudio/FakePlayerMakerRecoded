package org.lins.mmmjjkx.fakeplayermaker.objects;

import lombok.RequiredArgsConstructor;
import org.lins.mmmjjkx.fakeplayermaker.util.CommonUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.Proxy;

@RequiredArgsConstructor
public class WrappedSessionService {
    private static final Class<?> sessionServiceClass = CommonUtils.getClass(
            "com.github.steveice10.mc.auth.service.SessionService"
    );

    private static final Constructor<?> sessionServiceConstructor;
    private static final Method setProxyMethod;

    static {
        try {
            assert sessionServiceClass != null;

            sessionServiceConstructor = sessionServiceClass.getDeclaredConstructor();
            setProxyMethod = sessionServiceClass.getDeclaredMethod("setProxy", Proxy.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final Object sessionService;

    public void setProxy(Proxy proxy) {
        try {
            setProxyMethod.invoke(sessionService, proxy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static WrappedSessionService create() {
        try {
            return new WrappedSessionService(sessionServiceConstructor.newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object getHandle() {
        return sessionService;
    }
}
