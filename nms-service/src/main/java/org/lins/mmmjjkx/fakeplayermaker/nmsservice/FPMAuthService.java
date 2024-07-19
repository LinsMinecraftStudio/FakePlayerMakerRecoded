package org.lins.mmmjjkx.fakeplayermaker.nmsservice;

import com.destroystokyo.paper.profile.PaperAuthenticationService;
import com.mojang.authlib.Environment;
import com.mojang.authlib.EnvironmentParser;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Proxy;
import java.util.Arrays;

public class FPMAuthService extends PaperAuthenticationService {
    private final Environment environment;

    public FPMAuthService() {
        super(Proxy.NO_PROXY);
        this.environment = EnvironmentParser.getEnvironmentFromProperties().orElse(YggdrasilEnvironment.PROD.getEnvironment());
    }

    @Override
    public MinecraftSessionService createMinecraftSessionService() {
        return new FPMSessionService(this.getServicesKeySet(), this.getProxy(), environment);
    }

    @Override
    public GameProfileRepository createProfileRepository() {
        return new FPMGameProfileRepository(this.getProxy(), environment);
    }

    public static void setup() {
        try {
            bypassFieldFilterMap();

            Field services = MinecraftServer.class.getDeclaredField("l");
            services.setAccessible(true);

            Services service = (Services) services.get(MinecraftServer.getServer());
            FPMAuthService authService = new FPMAuthService();
            MinecraftSessionService sessionService = authService.createMinecraftSessionService();
            GameProfileRepository profileRepository = authService.createProfileRepository();
            Services newService = new Services(sessionService, authService.getServicesKeySet(), profileRepository, service.e());

            Method getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
            getDeclaredFields0.setAccessible(true);

            Field[] fields = (Field[]) getDeclaredFields0.invoke(Field.class, false);
            Field modifiersField = Arrays.stream(fields).filter(f -> f.getName().equals("modifiers")).findFirst().orElse(null);
            modifiersField.setAccessible(true);
            modifiersField.setInt(services, services.getModifiers() & ~Modifier.FINAL);
            services.set(MinecraftServer.getServer(), newService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void bypassFieldFilterMap() throws Exception {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe) field.get(null);
        long addr = unsafe.objectFieldOffset(Class.class.getDeclaredField("module"));
        unsafe.getAndSetObject(FPMAuthService.class, addr, Object.class.getModule());
    }
}
