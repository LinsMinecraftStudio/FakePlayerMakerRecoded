package org.lins.mmmjjkx.fakeplayermaker.impls.v1_19_R3;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.lins.mmmjjkx.fakeplayermaker.commons.Ownable;

import java.util.UUID;

public class FPMServerPlayer extends ServerPlayer implements Ownable {
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
