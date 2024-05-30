package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;
import org.lins.mmmjjkx.fakeplayermaker.commons.IFPMPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ChatCommand extends FPMSubCmd {
    public ChatCommand() {
        super("chat");

        addArgument("player", PolymerCommand.ArgumentType.REQUIRED);
        addArgument("message", PolymerCommand.ArgumentType.REQUIRED);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, FPMRecoded.fakePlayerManager.getFakePlayerNames(),
                1, List.of("message(you can space directly)"),
                getArgs().size() - 1, List.of("message(you can space directly)")
        );
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.chat");
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        if (hasPermission()) {
            String player = getArg(0);
            String message = getArg(1);

            if (player == null) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.no_player");
                return;
            }

            //head content
            if (message == null) {
                return;
            }

            IFPMPlayer fakePlayer = getFakePlayer(commandSender, player);
            if (fakePlayer != null) {
                String[] msg = Arrays.copyOfRange(getArgs().args(), 1, getArgs().size());
                String msgStr = String.join(" ", msg);
                FPMImplements.getCurrent().toBukkit(fakePlayer).chat(msgStr);
            }
        }
    }
}
