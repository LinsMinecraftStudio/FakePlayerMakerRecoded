package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.util.CommonUtils;
import org.lins.mmmjjkx.fakeplayermaker.util.FakePlayerSaver;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SpawnCommand extends FPMSubCmd {
    public SpawnCommand() {
        super("spawn", "create");

        addArgument("name", CommandArgumentType.OPTIONAL);
        addArgument("location", CommandArgumentType.OPTIONAL);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, List.of("type the fake player's name here"));
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.spawn");
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        if (hasPermission()) {
            String name = getArg(0);

            if (name == null) {
                name = FPMRecoded.INSTANCE.getConfig().getString("namePrefix", "fak_");
                name += CommonUtils.generateRandomString(FPMRecoded.INSTANCE.getConfig().getInt("randomNameLength", 8));
            }

            if (FPMRecoded.fakePlayerManager.get(name) != null) {
                sendMessage(commandSender, "command.name_taken");
                return;
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
            String offlinePlayerName = offlinePlayer.getName();
            if (offlinePlayerName != null && (offlinePlayer.hasPlayedBefore() && FPMRecoded.fakePlayerManager.get(offlinePlayerName) == null)) {
                sendMessage(commandSender, "command.real_player_named");
                sendMessage(commandSender, "command.name_taken");
                return;
            }

            UUID owner = FakePlayerSaver.NO_OWNER_UUID;

            if (commandSender instanceof Player p) {
                owner = p.getUniqueId();
            }

            IFPMPlayer player = FPMRecoded.fakePlayerManager.createAndSave(owner, name);
            FPMRecoded.fakePlayerManager.join(player);
        }
    }
}
