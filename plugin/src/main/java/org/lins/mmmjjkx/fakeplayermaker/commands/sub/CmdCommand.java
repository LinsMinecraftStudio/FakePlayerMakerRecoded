package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

public class CmdCommand extends FPMSubCmd {
    public CmdCommand() {
        super("command");

        addArgument("player", CommandArgumentType.REQUIRED);
        addArgument("command", CommandArgumentType.REQUIRED);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, FPMRecoded.fakePlayerManager.getFakePlayerNames(), 1, List.of("command(use %sp% instead of spaces)"));
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.cmd");
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        if (hasPermission()) {
            String player = getArg(0);
            String command = getArg(1);

            if (player == null) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.no_player");
                return;
            }

            if (command == null) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.no_command");
                return;
            }

            IFPMPlayer fakePlayer = getFakePlayer(commandSender, player);
            if (fakePlayer != null) {
                if (command.startsWith("fakeplayermaker") || command.startsWith("fpm") || command.startsWith("fakeplayer")) {
                    FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.not_allowed_command");
                    return;
                }

                Player bk = fakePlayer.getFakePlayerProfile().getPlayer();
                Player bkProxied = (Player) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Player.class}, (proxy, method, args) -> {
                    if (method.getName().equals("sendMessage")) {
                        method.invoke(commandSender, args);
                        return null;
                    }

                    return method.invoke(bk, args);
                });

                bkProxied.performCommand(command.replaceAll("%sp%", " "));
            }
        }
    }
}
