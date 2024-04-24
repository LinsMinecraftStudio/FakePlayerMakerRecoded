package org.lins.mmmjjkx.fakeplayermaker.impls.v1_20_R3;

import io.github.linsminecraftstudio.polymer.utils.IterableUtil;
import io.papermc.paper.adventure.ChatProcessor;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.fakeplayermaker.commons.InteractHand;
import org.lins.mmmjjkx.fakeplayermaker.commons.PlayerActionImplements;
import org.lins.mmmjjkx.fakeplayermaker.commons.SetupValueCollection;

import java.util.List;

public final class ActionImpl extends PlayerActionImplements {
    @Override
    public void mountNearest(Object player, int radius) {
        ServerPlayer serverPlayer = (ServerPlayer) player;
        ServerLevel level = serverPlayer.serverLevel();
        AABB boundingBox = serverPlayer.getBoundingBox();
        List<Entity> rideable = IterableUtil.getAllMatches(level.getEntities(serverPlayer, boundingBox.inflate(3, 2, 3)), e ->
                e instanceof Minecart || e instanceof AbstractHorse || e instanceof Boat);
        if (!rideable.isEmpty()) {
            Entity entity = rideable.get(0);
            serverPlayer.startRiding(entity, true);
        }
    }

    @Override
    public void dismount(Object player) {
        ServerPlayer serverPlayer = (ServerPlayer) player;
        serverPlayer.stopRiding();
    }

    @Override
    public void lookAt(Object player, double x, double y, double z) {
        ServerPlayer serverPlayer = (ServerPlayer) player;
        serverPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(x, y, z));
    }

    @Override
    public void chat(Object player, String message) {
        ServerPlayer serverPlayer = (ServerPlayer) player;
        PlayerChatMessage playerChatMessage = PlayerChatMessage.unsigned(serverPlayer.getUUID(), message);
        ChatProcessor chatProcessor = new ChatProcessor(
                MinecraftServer.getServer(), serverPlayer, playerChatMessage, true
        );
        chatProcessor.process();
    }

    @Override
    public void sneak(Object player, boolean sneak) {
        ServerPlayer serverPlayer = (ServerPlayer) player;
        serverPlayer.setShiftKeyDown(sneak);
        serverPlayer.setPose(sneak ? Pose.CROUCHING : Pose.STANDING);
    }

    @Override
    public void setupValues(Object player, SetupValueCollection values) {
        ServerPlayer serverPlayer = (ServerPlayer) player;

        Player bk = serverPlayer.getBukkitEntity();
        bk.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(values.maxHealth());

        serverPlayer.setInvulnerable(values.invulnerable());
    }

    @Override
    public void interact(Object player, InteractHand hand) {
        ServerPlayer serverPlayer = (ServerPlayer) player;

        serverPlayer.interact(serverPlayer, hand == InteractHand.MAIN_HAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }
}
