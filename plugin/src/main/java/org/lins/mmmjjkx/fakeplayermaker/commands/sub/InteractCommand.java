package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.entity.player.InteractAction;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundInteractPacket;
import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.InteractHand;
import org.lins.mmmjjkx.fakeplayermaker.objects.MCClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InteractCommand extends FPMSubCmd {
    public InteractCommand() {
        super("interact");

        addArgument("player", CommandArgumentType.REQUIRED);
        addArgument("hand", CommandArgumentType.OPTIONAL);
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.interact");
    }


    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, FPMRecoded.fakePlayerManager.getFakePlayerNames(),
                1, Arrays.stream(InteractHand.values()).map(Enum::toString).toList());
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        if (hasPermission()) {
            String playerName = getArg(0);
            String handName = getArg(1);
            if (playerName == null) {
                return;
            }

            IFPMPlayer player = getFakePlayer(commandSender, playerName);
            if (player == null) {
                return;
            }

            InteractHand hand;
            try {
                hand = handName == null ? InteractHand.MAIN_HAND : InteractHand.valueOf(handName);
            } catch (IllegalArgumentException e) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.interact_invalid_hand");
                return;
            }

            run(player, p -> {
                MCClient client = (MCClient) player;
                client.send(new ServerboundInteractPacket(p.getEntityId(), InteractAction.INTERACT, hand == InteractHand.MAIN_HAND ? Hand.MAIN_HAND : Hand.OFF_HAND, p.isSneaking()));
            });
        }
    }
}
