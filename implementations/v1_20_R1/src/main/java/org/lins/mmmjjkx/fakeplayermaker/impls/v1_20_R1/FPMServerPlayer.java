package org.lins.mmmjjkx.fakeplayermaker.impls.v1_20_R1;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.lins.mmmjjkx.fakeplayermaker.commons.IFPMPlayer;

import java.util.UUID;

public class FPMServerPlayer extends ServerPlayer implements IFPMPlayer {
    private final UUID ownerUUID;

    public FPMServerPlayer(MinecraftServer server, ServerLevel world, GameProfile profile, UUID owner) {
        super(server, world, profile);

        this.ownerUUID = owner;
    }

    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }
}
