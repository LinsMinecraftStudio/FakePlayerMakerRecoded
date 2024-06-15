package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;

import java.util.List;
import java.util.Map;

public class LookAtCommand extends FPMSubCmd {
    public LookAtCommand() {
        super("lookat");

        addArgument("player", CommandArgumentType.REQUIRED);
        addArgument("x", CommandArgumentType.REQUIRED);
        addArgument("y", CommandArgumentType.REQUIRED);
        addArgument("z", CommandArgumentType.REQUIRED);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, FPMRecoded.fakePlayerManager.getFakePlayerNames());
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.lookat");
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        if (hasPermission()) {
            String playerName = getArg(0);
            if (playerName == null) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.no_player");
                return;
            }
            String x, y, z;

            IFPMPlayer player = getFakePlayer(commandSender, playerName);

            if (player != null) {
                x = getArg(1);
                y = getArg(2);
                z = getArg(3);

                if (x == null || y == null || z == null) {
                    FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.missing_coordinates");
                    return;
                }

                double x1, y1, z1;

                try {
                    x1 = Double.parseDouble(x);
                    y1 = Double.parseDouble(y);
                    z1 = Double.parseDouble(z);
                } catch (NumberFormatException e) {
                    FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.invalid_coordinates");
                    return;
                }

                //.lookAt(player, x1, y1, z1);
            }
        }
    }
}
