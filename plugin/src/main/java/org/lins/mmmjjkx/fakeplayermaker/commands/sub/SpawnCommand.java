package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.utils.ObjectConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;
import org.lins.mmmjjkx.fakeplayermaker.util.CommonUtils;
import org.lins.mmmjjkx.fakeplayermaker.util.FakePlayerSaver;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SpawnCommand extends FPMSubCmd {
    public SpawnCommand() {
        super("spawn");

        addArgument("name", PolymerCommand.ArgumentType.OPTIONAL);
        addArgument("location", PolymerCommand.ArgumentType.OPTIONAL);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, List.of("nameOrLocation"), 1, List.of("location"));
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.spawn");
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        if (hasPermission()) {
            String name = getArg(0);
            String location = getArg(1);

            if (name == null) {
                name = FPMRecoded.INSTANCE.getConfig().getString("namePrefix", "fak_");
                name += CommonUtils.generateRandomString(FPMRecoded.INSTANCE.getConfig().getInt("randomNameLength", 8));
            }

            Location loc;
            if (location == null) {
                if (commandSender instanceof Player p) {
                    loc = p.getLocation();
                } else {
                    loc = FPMRecoded.INSTANCE.getConfig().getLocation("default-spawn-location", Bukkit.getWorlds().get(0).getSpawnLocation());
                }
            } else {
                Location rawLoc = ObjectConverter.toLocation(location);
                if (rawLoc == null) {
                    FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.invalid-location-redirected");
                    loc = FPMRecoded.INSTANCE.getConfig().getLocation("default-spawn-location", Bukkit.getWorlds().get(0).getSpawnLocation());
                } else {
                    loc = rawLoc;
                }
            }

            World world = loc.getWorld();
            UUID owner = FakePlayerSaver.NO_OWNER_UUID;

            if (commandSender instanceof Player p) {
                owner = p.getUniqueId();
            }

            Object player = FPMRecoded.fakePlayerManager.create(name, owner, world.getName());
            FPMRecoded.fakePlayerManager.join(name);

            FPMRecoded.fakePlayerSaver.saveFakePlayer(player);

            Player bk = FPMImplements.getCurrent().toBukkit(player);
            bk.teleport(loc);
        }
    }
}
