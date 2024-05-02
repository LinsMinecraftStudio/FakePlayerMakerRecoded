package org.lins.mmmjjkx.fakeplayermaker.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IFakePlayerManager {
    /**
     * Create a new fake player with the given UUID and name.<br>
     * <b>Note: the player isn't automatically join the server, you need to do it manually.</b>
     * @param owner The UUID of the owner of the fake player.
     * @param name The name of the fake player.
     * @return a new fake player
     * 
     * @see #join(IFPMPlayer) 
     */
    @NotNull
    IFPMPlayer create(UUID owner, String name);

    /**
     * Get a fake player by its name.
     * @param name The name or UUID of the fake player.
     * @return the fake player or null if not found.
     */
    @Nullable
    IFPMPlayer get(String name);

    /**
     * Join a fake player to the server.
     * @param player The fake player to join.
     *
     * @see #create(UUID, String)
     */
    void join(IFPMPlayer player);
}
