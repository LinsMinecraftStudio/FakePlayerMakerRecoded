package org.lins.mmmjjkx.fakeplayermaker.objects;

import lombok.RequiredArgsConstructor;
import org.lins.mmmjjkx.fakeplayermaker.util.CommonUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class WrappedGameProfile {
    public static final Class<?> GAME_PROFILE_CLASS = CommonUtils.getClass(
            "org.geysermc.mcprotocollib.auth.GameProfile",
            "com.github.steveice10.mc.auth.data.GameProfile"
    );

    public static final Class<?> GAME_PROFILE_PROPERTIES_CLASS = CommonUtils.getClass(
            "org.geysermc.mcprotocollib.auth.GameProfile$Property",
            "com.github.steveice10.mc.auth.data.GameProfile$Property"
    );

    private static final Method getNameMethod;
    private static final Method getUUIDMethod;
    private static final Constructor<?> gameProfileConstructor;
    private static final Method getPropertiesMethod;
    private static final Constructor<?> gameProfilePropertyConstructor;

    static {
        assert GAME_PROFILE_CLASS != null && GAME_PROFILE_PROPERTIES_CLASS != null;
        try {
            getNameMethod = GAME_PROFILE_CLASS.getMethod("getName");
            getUUIDMethod = GAME_PROFILE_CLASS.getMethod("getId");
            gameProfileConstructor = GAME_PROFILE_CLASS.getDeclaredConstructor(UUID.class, String.class);
            getPropertiesMethod = GAME_PROFILE_CLASS.getMethod("getProperties");
            gameProfilePropertyConstructor = GAME_PROFILE_PROPERTIES_CLASS.getDeclaredConstructor(String.class, String.class, String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final Object gameProfile;

    public String getName() {
        try {
            return (String) getNameMethod.invoke(gameProfile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UUID getId() {
        try {
            return (UUID) getUUIDMethod.invoke(gameProfile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void putProperty(String name, String value, String signature) {
        try {
            List<Object> properties = (List<Object>) getPropertiesMethod.invoke(gameProfile);
            properties.add(gameProfilePropertyConstructor.newInstance(name, value, signature));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static WrappedGameProfile create(UUID id, String name) {
        try {
            return new WrappedGameProfile(gameProfileConstructor.newInstance(id, name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object getHandle() {
        return gameProfile;
    }
}
