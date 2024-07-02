package org.lins.mmmjjkx.fakeplayermaker.util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;

import java.util.*;
import java.util.logging.Level;

public class CommonUtils {
    private static final Map<String, BitSet> allocatedIP = new HashMap<>();
    private static final int portRangeStart = 0;
    private static final int portRangeEnd = 65535;

    private static final char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = new Random().nextInt(chars.length);
            sb.append(chars[index]);
        }
        return sb.toString();
    }

    public static String deobfString(String obfString) {
        if (obfString == null) {
            return null;
        }

        return obfString.replaceAll("%", "");
    }

    public static String[] deobfStrings(String... obfStrings) {
        if (obfStrings == null) {
            return null;
        }

        String[] deobfStrings = new String[obfStrings.length];
        for (int i = 0; i < obfStrings.length; i++) {
            deobfStrings[i] = deobfString(obfStrings[i]);
        }
        return deobfStrings;
    }

    @Nullable
    public static Class<?> getClass(String... classPath) {
        for (String path : classPath) {
            try {
                return Class.forName(path);
            } catch (ClassNotFoundException ignore) {
            }
        }
        return null;
    }

    @Nullable
    public static Pair<String, Integer> getUnAllocatedIPPort() {
        List<String> usableIP = FPMRecoded.INSTANCE.getConfig().getStringList("usable_ip");
        if (usableIP.isEmpty()) {
            FPMRecoded.INSTANCE.getLogger().log(Level.SEVERE, """
                    No usable IP found in config.yml.
                    Please add at least one usable IP to config.yml.
                    OTHERWISE, FAKE PLAYERS WILL NOT BE ABLE TO CONNECT.
                    """);
            return null;
        }

        List<Pair<String, Integer>> unallocatedIPPorts = findUnallocatedIPPorts();
        if (unallocatedIPPorts.isEmpty()) {
            FPMRecoded.INSTANCE.getLogger().log(Level.SEVERE, """
                    No unallocated IP and port found.
                    You may need to increase the number of usable IP or release some IP and port(kicking useless fake players).
                    """);
            return null;
        }

        Pair<String, Integer> allocatedIPPort = unallocatedIPPorts.get(0);

        allocatedIP.putIfAbsent(allocatedIPPort.getKey(), new BitSet(portRangeEnd + 1));
        allocatedIP.get(allocatedIPPort.getKey()).set(allocatedIPPort.getValue(), true);
        return allocatedIPPort;
    }

    public static void releaseIPPort(String ip, int port) {
        allocatedIP.getOrDefault(ip, new BitSet()).set(port, true);
    }

    public static List<Pair<String, Integer>> findUnallocatedIPPorts() {
        List<Pair<String, Integer>> unallocatedIPPorts = new ArrayList<>();
        List<String> usableIP = FPMRecoded.INSTANCE.getConfig().getStringList("usable_ip");

        for (String ip : usableIP) {
            BitSet bitSet = allocatedIP.getOrDefault(ip, new BitSet(portRangeEnd + 1));
            for (int port = portRangeStart; port <= portRangeEnd; port++) {
                if (!bitSet.get(port)) {
                    unallocatedIPPorts.add(new ImmutablePair<>(ip, port));
                }
            }
        }

        return unallocatedIPPorts;
    }

    public static boolean isOnMinecraftVersion(int major, int minor, int patch) {
        String version = Bukkit.getMinecraftVersion();
        String[] versionParts = version.split("-")[0].split("\\.");
        int majorVersion, minorVersion, patchVersion;
        if (versionParts.length == 2) {
            patchVersion = 0;
        } else if (versionParts.length == 3) {
            patchVersion = Integer.parseInt(versionParts[2]);
        } else {
            return false;
        }
        majorVersion = Integer.parseInt(versionParts[0]);
        minorVersion = Integer.parseInt(versionParts[1]);
        return majorVersion > major || (majorVersion == major && minorVersion > minor) || (majorVersion == major && minorVersion == minor && patchVersion >= patch);
    }
}
