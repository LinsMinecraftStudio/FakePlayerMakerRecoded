package org.lins.mmmjjkx.fakeplayermaker.commands;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.presets.sub.SubHelpMessager;
import io.github.linsminecraftstudio.polymer.command.presets.sub.SubReloadCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.sub.*;

import java.util.List;

public class FPMMainCommand extends PolymerCommand {
    public static final FPMMainCommand INSTANCE = new FPMMainCommand();

    protected FPMMainCommand() {
        super("fakeplayermaker", FPMRecoded.INSTANCE, List.of("fpm", "fakeplayer"));

        registerSubCommand(new FPMHelpSubCommand(this));
        registerSubCommand(new RemoveCommand());
        registerSubCommand(new SpawnCommand());
        registerSubCommand(new FPMReloadSubCommand());
        registerSubCommand(new ChatCommand());
        registerSubCommand(new SneakCommand());
        registerSubCommand(new LeaveCommand());
        registerSubCommand(new JoinCommand());
        registerSubCommand(new SkinCommand());
        registerSubCommand(new TeleportCommand());
        registerSubCommand(new TeleportHereCommand());
        registerSubCommand(new MountCommand());
        registerSubCommand(new DismountCommand());
        registerSubCommand(new RemoveAllCommand());
        registerSubCommand(new RespawnCommand());
        registerSubCommand(new CmdCommand());
        registerSubCommand(new InteractCommand());
        registerSubCommand(new LookAtCommand());
    }

    @Override
    public String requirePlugin() {
        return "";
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.info", FPMRecoded.INSTANCE.getPluginVersion(), "mmmjjkx(lijinhong11)");
    }

    private static class FPMHelpSubCommand extends SubHelpMessager {
        public FPMHelpSubCommand(@NotNull PolymerCommand command) {
            super(command);
        }

        public String getHelpDescription() {
            return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.help");
        }
    }

    private static class FPMReloadSubCommand extends SubReloadCommand {
        public FPMReloadSubCommand() {
            super(FPMRecoded.INSTANCE);
        }

        @Override
        public void execute(CommandSender sender, String alias) {
            if (this.hasPermission()) {
                FPMRecoded.INSTANCE.reload();
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(sender, "command.reload-success");
            }
        }

        @Override
        public String getHelpDescription() {
            return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.reload");
        }
    }
}
