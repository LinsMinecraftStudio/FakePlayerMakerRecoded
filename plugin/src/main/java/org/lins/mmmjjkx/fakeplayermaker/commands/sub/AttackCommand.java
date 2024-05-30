package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;
import org.lins.mmmjjkx.fakeplayermaker.commons.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.commons.PlayerActionImplements;

import java.util.List;
import java.util.Map;

public class AttackCommand extends FPMSubCmd {
    public AttackCommand() {
        super("attack");
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, FPMRecoded.fakePlayerManager.getFakePlayerNames());
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        if (hasPermission()) {
            String playerName = getArg(0);
            if (playerName == null) {
                return;
            }

            IFPMPlayer player = getFakePlayer(commandSender, playerName);
            if (player == null) {
                return;
            }

            Player bk = FPMImplements.getCurrent().toBukkit(player);

            int entityAttackRadius = FPMRecoded.INSTANCE.getConfig().getInt("fakePlayer.entityAttackRadius", 3);

            List<Entity> targets = bk.getNearbyEntities(entityAttackRadius, entityAttackRadius, entityAttackRadius);

            PlayerActionImplements.getCurrent().attack(player, targets.get(0));
        }
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.attack");
    }
}
