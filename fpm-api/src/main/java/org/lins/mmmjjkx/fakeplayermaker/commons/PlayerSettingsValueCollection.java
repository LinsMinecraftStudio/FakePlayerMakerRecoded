package org.lins.mmmjjkx.fakeplayermaker.commons;

public record PlayerSettingsValueCollection(int latencyMin, int latencyMax) {
    public static final PlayerSettingsValueCollection EMPTY = new PlayerSettingsValueCollection(0, 0);
}
