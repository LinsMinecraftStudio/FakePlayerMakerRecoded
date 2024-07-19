package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.util.FakePlayerSaver;

import java.util.List;
import java.util.Map;

public class JoinCommand extends FPMSubCmd {
    public JoinCommand() {
        super("join");

        addArgument("player", CommandArgumentType.REQUIRED);
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

            int amount;
            if (commandSender instanceof Player p) {
                amount = FPMRecoded.fakePlayerManager.getFakePlayers(p.getUniqueId()).size();
            } else {
                amount = FPMRecoded.fakePlayerManager.getFakePlayers(FakePlayerSaver.NO_OWNER_UUID).size();
            }

            if (amount == 0) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.tip_of_first_create");
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "player_not_found");
                return;
            }

            Pair<Boolean, IFPMPlayer> fakePlayerPair = FPMRecoded.fakePlayerManager.getExactly(playerName);
            IFPMPlayer fakePlayer = fakePlayerPair.getRight();
            if (fakePlayer == null) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "player_not_found");
                return;
            }

            if (!isHisOwn(commandSender, fakePlayer.getOwnerUUID())) {
                return;
            }

            Player bk = Bukkit.getPlayer(fakePlayer.getFakePlayerProfile().getId());
            if (bk != null) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "player_already_joined");
                return;
            }

            FPMRecoded.fakePlayerManager.join(playerName);
        }
    }
}
