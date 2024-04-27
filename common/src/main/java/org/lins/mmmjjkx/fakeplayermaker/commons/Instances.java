package org.lins.mmmjjkx.fakeplayermaker.commons;

import com.google.common.base.Preconditions;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import lombok.Getter;

public class Instances {
    @Getter
    private static PolymerPlugin FPM;

    public static void setFPM(PolymerPlugin FPM) {
        Preconditions.checkNotNull(FPM);
        Instances.FPM = FPM;
    }
}
