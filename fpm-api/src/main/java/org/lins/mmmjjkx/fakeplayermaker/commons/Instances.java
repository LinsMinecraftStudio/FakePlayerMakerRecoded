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
}
