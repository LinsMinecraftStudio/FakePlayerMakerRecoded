package org.lins.mmmjjkx.fakeplayermaker.commons.objects;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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
     * Create a new fake player with the given UUID and name, and save it to the plugin data file.<br>
     * <b>Note: the player isn't automatically join the server, you need to do it manually.</b>
     * @param owner The UUID of the owner of the fake player.
     * @param name The name of the fake player.
     * @return a new fake player
     *
     * @see #join(IFPMPlayer)
     */
    @NotNull
    IFPMPlayer createAndSave(UUID owner, String name);

    /**
     * Get a fake player by its name.
     * @param name The name or UUID of the fake player.
     * @return the fake player or null if not found.
     */
    @Nullable
    IFPMPlayer get(String name);

    /**
     * Get a fake player by its UUID.
     * @param name the name of the fake player.
     * @return a pair of boolean and the fake player object. The boolean is true if the player is <b>found and online, false otherwise.</b>
     */
    @NotNull
    Pair<Boolean, IFPMPlayer> getExactly(String name);

    /**
     * Join a fake player to the server.
     * @param player The fake player to join.
     *
     * @see #create(UUID, String)
     */
    void join(IFPMPlayer player);

    /**
     * Join a fake player to the server.
     * @param name The name of the fake player to join.
     */
    void join(String name);

    /**
     * Join a fake player to the server and run a callback when it's done.
     * @param name The name of the fake player to join.
     * @param callback The callback to run when the player is joined.
     */
    void join(String name, Runnable callback);

    /**
     * Join a fake player to the server and run a callback when it's done.
     * @param player The fake player to join.
     * @param callback The callback to run when the player is joined.
     */
    void join(IFPMPlayer player, Runnable callback);

    /**
     * Get all fake players owned by the given UUID.
     * @param owner The UUID of the owner.
     * @return a list of fake players owned by the given UUID.
     */
    List<IFPMPlayer> getFakePlayers(UUID owner);

    /**
     * Get all fake player names.<br>
     * <b>Note: some of the players may not be online.</b>
     * @return a list of fake player names.
     */
    List<String> getFakePlayerNames();

    /**
     * Remove a fake player.
     * @param name the name of the fake player to remove.
     */
    void remove(String name);

    /**
     * Let a fake player leave the server.
     * @param name the name of the fake player to leave.
     */
    void leave(String name);
}
