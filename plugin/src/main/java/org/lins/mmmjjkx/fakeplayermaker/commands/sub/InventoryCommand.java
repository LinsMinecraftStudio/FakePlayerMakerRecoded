package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;

import java.util.List;
import java.util.Map;

public class InventoryCommand extends FPMSubCmd {
    public InventoryCommand() {
        super("inventory", "inv");

        addArgument("player", CommandArgumentType.REQUIRED);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of();
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

            if (!(commandSender instanceof Player p)) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.player_only");
                return;
            }

            Player fake = player.getFakePlayerProfile().getPlayer();
            p.openInventory(fake.getInventory());
        }
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.inventory");
    }
}
