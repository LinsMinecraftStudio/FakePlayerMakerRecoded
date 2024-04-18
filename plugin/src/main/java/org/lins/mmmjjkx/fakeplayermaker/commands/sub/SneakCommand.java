package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.PlayerActionImplements;

import java.util.List;
import java.util.Map;

public class SneakCommand extends SubCommand {
    private final PlayerActionImplements IMPL = PlayerActionImplements.getCurrent();

    public SneakCommand(@NotNull String name) {
        super(name);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(1, List.of("player"));
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        String playerName = getArg(0);
        if (playerName == null) {
            return;
        }

        Object player = FPMRecoded.fakePlayerSaver.getFakePlayer(playerName);
        if (player == null) {
            FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "player_not_found");
            return;
        }

        
    }
}
