package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatCommand extends FPMSubCmd {
    public ChatCommand() {
        super("chat");

        addArgument("player", CommandArgumentType.REQUIRED);
        addArgument("message", CommandArgumentType.REQUIRED);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, FPMRecoded.fakePlayerManager.getFakePlayerNames(),
                1, List.of("message(you can space directly)")
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
                Objects.requireNonNull(fakePlayer.getFakePlayerProfile().getPlayer()).chat(msgStr);
            }
        }
    }
}
