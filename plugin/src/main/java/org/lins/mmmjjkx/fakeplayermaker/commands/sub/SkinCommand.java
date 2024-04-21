package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;
import org.lins.mmmjjkx.fakeplayermaker.commons.SkinUtils;

import java.util.List;
import java.util.Map;

public class SkinCommand extends FPMSubCmd {
    public SkinCommand() {
        super("skin");

        addArgument("player", PolymerCommand.ArgumentType.REQUIRED);
        addArgument("skinName", PolymerCommand.ArgumentType.REQUIRED);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, FPMRecoded.fakePlayerManager.getFakePlayerNames(), 1, List.of("skinName"));
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.skin");
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        if (hasPermission()) {
            String playerName = getArg(0);
            String skinName = getArg(1);

            if (playerName == null) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.no_player");
                return;
            }

            if (skinName == null) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.no_skin_name");
                return;
            }

            Object fakePlayer = getFakePlayer(commandSender, playerName);
            if (fakePlayer == null) {
                return;
            }

            Player bk = FPMImplements.getCurrent().toBukkit(fakePlayer);
            boolean success = SkinUtils.changeSkin(bk, skinName);
            if (success) {
                FPMRecoded.fakePlayerSaver.saveFakePlayer(fakePlayer);
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.skin_changed");
            } else {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.skin_error");
            }
        }
    }
}