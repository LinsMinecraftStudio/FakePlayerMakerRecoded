package org.lins.mmmjjkx.fakeplayermaker.commands;

import io.github.linsminecraftstudio.polymer.command.SubCommand;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class FPMSubCmd extends SubCommand {
    public FPMSubCmd(@NotNull String name) {
        super(name);
    }

    public FPMSubCmd(@NotNull String name, @NotNull String... aliases) {
        super(name, aliases);
    }

    protected IFPMPlayer getFakePlayer(CommandSender sender, String playerName) {
        Pair<Boolean, IFPMPlayer> fakePlayerPair = FPMRecoded.fakePlayerManager.getExactly(playerName);

        if (fakePlayerPair.getRight() == null) {
            FPMRecoded.INSTANCE.getMessageHandler().sendMessage(sender, "player_not_found");
            return null;
        }

        IFPMPlayer fakePlayer;

        if (fakePlayerPair.getLeft()) {
            fakePlayer = fakePlayerPair.getRight();
        } else if (!fakePlayerPair.getLeft() && fakePlayerPair.getRight() != null) {
            FPMRecoded.INSTANCE.getMessageHandler().sendMessage(sender, "player_not_joined");
            fakePlayer = null;
        } else {
            FPMRecoded.INSTANCE.getMessageHandler().sendMessage(sender, "player_not_found");
            fakePlayer = null;
        }

        if (fakePlayer != null) {
            return isHisOwn(sender, fakePlayer.getOwnerUUID()) ? fakePlayer : null;
        }

        return null;
    }

    protected final void run(IFPMPlayer fakePlayer, Consumer<Player> consumer) {
        Player player1 = fakePlayer.getFakePlayerProfile().getPlayer();
        if (player1 != null) {
            consumer.accept(player1);
        } else {
            sendMessage("command.player_not_in_server");
        }
    }

    protected boolean isHisOwn(CommandSender sender, UUID ownerUUID) {
        if (sender instanceof Player p) {
            if (p.hasPermission("fakeplayermaker.bypassowner")) {
                return true;
            } else {
                if (p.getUniqueId().equals(ownerUUID)) {
                    return true;
                }
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(sender, "player_not_yours");
                return false;
            }
        }
        return true;
    }
}
