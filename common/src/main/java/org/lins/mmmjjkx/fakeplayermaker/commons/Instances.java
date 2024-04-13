package org.lins.mmmjjkx.fakeplayermaker.commons;

import com.google.common.base.Preconditions;

public class Instances {
    private static FPMPlugin fpmPlugin;

    public static FPMPlugin getFPMPlugin() {
        return fpmPlugin;
    }

    public static void setFPMPlugin(FPMPlugin fpmPlugin) {
        Preconditions.checkNotNull(fpmPlugin, "fpmPlugin cannot be null");

        Instances.fpmPlugin = fpmPlugin;
    }
}
