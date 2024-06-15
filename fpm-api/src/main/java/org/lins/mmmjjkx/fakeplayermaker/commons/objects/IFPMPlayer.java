package org.lins.mmmjjkx.fakeplayermaker.commons.objects;

import java.util.UUID;

/**
 * An interface for tagging FPM players.
 */
public interface IFPMPlayer {
    UUID getOwnerUUID();

    FakePlayerProfile getFakePlayerProfile();
}
