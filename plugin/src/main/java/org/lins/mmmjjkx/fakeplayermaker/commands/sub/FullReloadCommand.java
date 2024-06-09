package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.command.interfaces.ICommand;
import io.github.linsminecraftstudio.polymer.objects.other.CooldownMap;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class FullReloadCommand extends FPMSubCmd implements ICommand.INeedsCooldownCommand<CommandSender> {
    private final CooldownMap<CommandSender> cooldownMap;

    public FullReloadCommand() {
        super("full-reload");

        cooldownMap = new CooldownMap<>();
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.full-reload");
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of();
    }

    @Override
    public void execute(CommandSender sender, String alias) {
        if (this.hasPermission()) {
            if (hasCooldown(sender)) {
                FPMRecoded.INSTANCE.reload();
                FPMRecoded.fakePlayerManager.reload();
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(sender, "command.reload-success");
                cooldownMap.remove(sender);
            } else {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(sender, "command.reload-confirm");
                cooldownMap.set(sender, Duration.ofSeconds(10));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (hasCooldown(sender)) {
                            FPMRecoded.INSTANCE.getMessageHandler().sendMessage(sender, "command.reload-expired");
                            cooldownMap.remove(sender);
                        }
                    }
                }.runTaskLaterAsynchronously(FPMRecoded.INSTANCE, 20 * 10);
            }
        }
    }

    @Override
    public CooldownMap<CommandSender> getCooldownMap() {
        return cooldownMap;
    }
}
