package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.objects.CodecHelperMethod;
import org.lins.mmmjjkx.fakeplayermaker.objects.MCClient;
import org.lins.mmmjjkx.fakeplayermaker.util.Reflections;

import java.util.List;
import java.util.Map;

public class SneakCommand extends FPMSubCmd {
    private static final Class<?> playerCommandPacketClass = Reflections.getServerboundPacketClass("player.ServerboundPlayerCommandPacket");

    public SneakCommand() {
        super("sneak");

        addArgument("player", CommandArgumentType.REQUIRED);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, FPMRecoded.fakePlayerManager.getFakePlayerNames());
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.sneak");
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        if (hasPermission()) {
            String playerName = getArg(0);
            if (playerName == null) {
                return;
            }

            IFPMPlayer player = getFakePlayer(commandSender, playerName);
            if (player == null) {
                return;
            }

            run(player, bk -> {
                MCClient client = (MCClient) player;
                ByteBuf byteBuf = new CompositeByteBuf(new PooledByteBufAllocator(), false, 16);
                Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_VAR_INT, bk.getEntityId());
                Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_VAR_INT, bk.isSneaking() ? 1 : 0);
                Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_VAR_INT, 0);
                client.send(Reflections.createPacket(playerCommandPacketClass, byteBuf));
            });
        }
    }
}
