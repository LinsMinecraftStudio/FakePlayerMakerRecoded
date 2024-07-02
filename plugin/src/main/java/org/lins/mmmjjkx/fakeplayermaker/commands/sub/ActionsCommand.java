package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.bukkit.command.CommandSender;
import org.cloudburstmc.math.vector.Vector3i;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.objects.CodecHelperMethod;
import org.lins.mmmjjkx.fakeplayermaker.objects.MCClient;
import org.lins.mmmjjkx.fakeplayermaker.util.CommonUtils;
import org.lins.mmmjjkx.fakeplayermaker.util.Reflections;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ActionsCommand extends FPMSubCmd {
    private final Class<?> playerActionclass;
    private final Class<?> directionclass;
    private final Class<?> packetClass;

    private static final List<String> directions = List.of("DOWN", "UP", "NORTH", "SOUTH", "WEST", "EAST");
    private static final List<String> actions = List.of("START_DIGGING", "CANCEL_DIGGING", "FINISH_DIGGING", "DROP_ITEM_STACK", "DROP_ITEM", "RELEASE_USE_ITEM", "SWAP_HANDS");

    public ActionsCommand() {
        super("actions");

        playerActionclass = CommonUtils.getClass(
                "com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction",
                "com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction"
        );

        directionclass = CommonUtils.getClass(
                "com.github.steveice10.mc.protocol.data.game.entity.object.Direction",
                "org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction"
        );

        packetClass = Reflections.getServerboundPacketClass(
                "player.ServerboundPlayerActionPacket"
        );
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.actions");
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, FPMRecoded.fakePlayerManager.getFakePlayerNames(), 1, actions, 2, List.of("pos"), 3, directions, 4, List.of("sequence"));
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

            String action = getArg(1);
            if (action == null) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.missing_action");
                return;
            }

            action = action.toUpperCase();

            if (!actions.contains(action)) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.invalid_action");
                return;
            }

            String v3i = getArg(2);
            if (v3i == null) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.missing_coordinates");
                return;
            }

            if (!v3i.matches("\\d+,\\d+,\\d+")) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.invalid_coordinates");
                return;
            }

            String direction = getArg(3);
            if (direction == null) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.missing_direction");
                return;
            }

            direction = direction.toUpperCase();

            if (!directions.contains(direction)) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.invalid_direction");
                return;
            }

            String sequenceStr = getArg(4);
            int sequence;
            if (sequenceStr == null) {
                sequence = 0;
            } else {
                try {
                    sequence = Integer.parseInt(sequenceStr);
                } catch (NumberFormatException e) {
                    sendPolymerMessage(commandSender, "Value.NotInt", 5);
                    return;
                }
            }

            AtomicReference<Object> playerAction = new AtomicReference<>();
            AtomicReference<Object> directionObj = new AtomicReference<>();

            Object[] actionConstants = playerActionclass.getEnumConstants();
            String finalAction = action;
            Arrays.stream(actionConstants).filter(c -> c.toString().equalsIgnoreCase(finalAction)).findFirst().ifPresent(playerAction::set);

            Object[] directionConstants = directionclass.getEnumConstants();
            String finalDirection = direction;
            Arrays.stream(directionConstants).filter(c -> c.toString().equalsIgnoreCase(finalDirection)).findFirst().ifPresent(directionObj::set);

            int x, y, z;
            try {
                String[] coords = v3i.split(",");
                x = Integer.parseInt(coords[0]);
                y = Integer.parseInt(coords[1]);
                z = Integer.parseInt(coords[2]);
            } catch (NumberFormatException e) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "command.invalid_coordinates");
                return;
            }

            Vector3i vec = Vector3i.from(x, y, z);

            run(player, p -> {
                ByteBuf byteBuf = new CompositeByteBuf(new PooledByteBufAllocator(), false , 16);
                Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_VAR_INT, Reflections.getEnumOrdinal(playerAction.get()));
                Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_POSITION, vec);
                Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_VAR_INT, Reflections.getEnumOrdinal(directionObj.get()));
                Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_VAR_INT, sequence);
                Object packet = Reflections.createPacket(packetClass, byteBuf);
                MCClient mcClient = (MCClient) player;
                mcClient.send(packet);
            });
        }
    }
}
