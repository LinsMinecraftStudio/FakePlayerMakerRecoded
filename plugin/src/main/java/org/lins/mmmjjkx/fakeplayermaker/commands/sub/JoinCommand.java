package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;
import org.lins.mmmjjkx.fakeplayermaker.commons.IFPMPlayer;

import java.util.List;
import java.util.Map;

public class JoinCommand extends FPMSubCmd {
    public JoinCommand() {
        super("join");

        addArgument("player", PolymerCommand.ArgumentType.REQUIRED);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, FPMRecoded.fakePlayerManager.getFakePlayerNames());
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.join");
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        if (hasPermission()) {
            String playerName = getArg(0);
            if (playerName == null) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.no_player");
                return;
            }

            IFPMPlayer fakePlayer = getFakePlayer(commandSender, playerName);
            if (fakePlayer == null) {
                return;
            }

            Player player = FPMImplements.getCurrent().toBukkit(fakePlayer);
            if (Bukkit.getPlayer(player.getUniqueId()) != null) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "player_already_joined");
                return;
            }

            FPMRecoded.fakePlayerManager.join(playerName);
        }
    }
}
