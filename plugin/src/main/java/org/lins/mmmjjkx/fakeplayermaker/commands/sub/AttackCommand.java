package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.entity.player.InteractAction;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundInteractPacket;
import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.objects.MCClient;

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

            //Player bk = player.getFakePlayerProfile().getPlayer();

            //int entityAttackRadius = FPMRecoded.INSTANCE.getConfig().getInt("fakePlayer.entityAttackRadius", 3);

            run(player, p -> {
                MCClient client = (MCClient) player;
                client.send(new ServerboundInteractPacket(p.getEntityId(), InteractAction.ATTACK, Hand.MAIN_HAND, p.isSneaking()));
            });
        }
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.attack");
    }
}
