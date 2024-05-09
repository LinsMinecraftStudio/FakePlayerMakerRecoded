package org.lins.mmmjjkx.fakeplayermaker.commons;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

@Getter
class VersionedImplementation {

    private static final Map<String, VersionedImplementation> versionedImplMap = new HashMap<>();

    private final String packageName;

    static {
        new VersionedImplementation("1.20.6", "1.21");
    }

    private VersionedImplementation(String version, String... otherSupports) {

        versionedImplMap.put(version, this);
        for (String s : otherSupports) {
            versionedImplMap.put(s, this);
        }

        packageName = "v" + Instances.versionToCode(version);
    }

    static VersionedImplementation get() {
        return versionedImplMap.get(Bukkit.getMinecraftVersion());
    }
}
