package org.lins.mmmjjkx.fakeplayermaker.impls.v1_19_R3;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;
import org.lins.mmmjjkx.fakeplayermaker.impls.common.FPMChannel;

public final class FPMImpl extends FPMImplements {

    @Override
    public @NotNull Object createPlayer(@NotNull GameProfile profile, @NotNull String levelName) {
        World bk = Bukkit.getWorld(levelName);
        WorldServer world;
        if (bk == null) {
            world = MinecraftServer.getServer().D();
        } else {
            world = ((CraftWorld) bk).getHandle();
        }
        return new EntityPlayer(MinecraftServer.getServer(), world, profile);
    }

    @Override
    public void setupConnection(@NotNull Object player) {
        NetworkManager connection = new NetworkManager(EnumProtocolDirection.a);
        FPMChannel channel = new FPMChannel();
        EntityPlayer player1 = (EntityPlayer) player;

        NetworkManager.a(channel.pipeline() , EnumProtocolDirection.a);

        connection.m = channel;

        player1.b = new PlayerConnection(MinecraftServer.getServer(), connection, player1);
    }

    @Override
    public void addPlayer(@NotNull Object player) {
        PlayerList playerList = MinecraftServer.getServer().ac();
        playerList.a((EntityPlayer) player);
    }

    @Override
    public void removePlayer(@NotNull Object player) {
        PlayerList playerList = MinecraftServer.getServer().ac();
        playerList.remove((EntityPlayer) player);
    }
}
