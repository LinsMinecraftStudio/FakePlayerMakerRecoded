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
import org.lins.mmmjjkx.fakeplayermaker.util.CommonUtils;
import org.lins.mmmjjkx.fakeplayermaker.util.Reflections;

import java.util.List;
import java.util.Map;

public class AttackCommand extends FPMSubCmd {
    private final Class<?> packetClass;
    private final Class<? extends Enum> interactActionClass;

    public AttackCommand() {
        super("attack");

        addArgument("player", CommandArgumentType.REQUIRED);

        packetClass = Reflections.getServerboundPacketClass(
                "player.ServerboundInteractPacket"
        );

        interactActionClass = (Class<? extends Enum>) CommonUtils.getClass(
                "com.github.steveice10.mc.protocol.data.game.entity.player.InteractAction",
                "org.geysermc.mcprotocollib.protocol.data.game.entity.player.InteractAction"
        );
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, FPMRecoded.fakePlayerManager.getFakePlayerNames());
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

            Object interactAction = Enum.valueOf(interactActionClass, "ATTACK");

            run(player, p -> {
                MCClient client = (MCClient) player;
                ByteBuf byteBuf = new CompositeByteBuf(new PooledByteBufAllocator(), false, 16);
                Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_VAR_INT, p.getEntityId());
                Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_VAR_INT, Reflections.getEnumOrdinal(interactAction));
                byteBuf.writeBoolean(p.isSneaking());
                Object packet = Reflections.createPacket(packetClass, byteBuf);
                client.send(packet);
            });
        }
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.attack");
    }
}
