package org.lins.mmmjjkx.fakeplayermaker.commons;

import com.google.common.base.Preconditions;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import lombok.Getter;

public class Instances {
    @Getter
    private static PolymerPlugin FPM;

    @Getter
    private static IFakePlayerManager fakePlayerManager;

    public static void setFPM(PolymerPlugin FPM) {
        Preconditions.checkNotNull(FPM);
        Instances.FPM = FPM;
    }

    public static void setFakePlayerManager(IFakePlayerManager fakePlayerManager) {
        Preconditions.checkNotNull(fakePlayerManager);
        Instances.fakePlayerManager = fakePlayerManager;
    }

    public static boolean isVersionAtLeast1206() {
        int version = versionToCode(FPM.getServer().getMinecraftVersion());
        return version >= 1206;
    }

    public static int versionToCode(String s) {
        String[] ver = s.split("\\.");
        String ver2 = "";
        for (String v : ver) {
            ver2 = ver2.concat(v);
        }

        if (ver.length == 2) {
            ver2 = ver2.concat("0");
        }

        return Integer.parseInt(ver2);
    }
}
