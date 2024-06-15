package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;

import java.util.List;
import java.util.Map;

public class AttackCommand extends FPMSubCmd {
    public AttackCommand() {
        super("attack");

        addArgument("player", CommandArgumentType.REQUIRED);
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

            Player bk = player.getFakePlayerProfile().getPlayer();

            int entityAttackRadius = FPMRecoded.INSTANCE.getConfig().getInt("fakePlayer.entityAttackRadius", 3);

            List<Entity> targets = bk.getNearbyEntities(entityAttackRadius, entityAttackRadius, entityAttackRadius);

            //.attack(player, targets.get(0));
        }
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.attack");
    }
}
