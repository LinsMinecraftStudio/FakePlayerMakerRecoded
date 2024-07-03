package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.objects.CodecHelperMethod;
import org.lins.mmmjjkx.fakeplayermaker.objects.MCClient;
import org.lins.mmmjjkx.fakeplayermaker.util.FakePlayerManager;
import org.lins.mmmjjkx.fakeplayermaker.util.Reflections;

import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

public class CmdCommand extends FPMSubCmd {
    private final Class<?> destPacketClass;

    public CmdCommand() {
        super("command", "cmd");

        addArgument("player", CommandArgumentType.REQUIRED);
        addArgument("command", CommandArgumentType.REQUIRED);

        destPacketClass = Reflections.getClientboundPacketClass("ClientboundDisguisedChatPacket");
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

                MCClient client = (MCClient) fakePlayer;
                ByteBuf byteBuf = FakePlayerManager.writeBase(command);
                byteBuf.writeBoolean(false);
                byteBuf.writeBytes(new byte[256]);
                Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_VAR_INT, 0);
                Reflections.codecHelperOperation(byteBuf, CodecHelperMethod.WRITE_FIXED_BITSET, new BitSet(20), 20);

                Object packet = Reflections.createPacket(FakePlayerManager.chatPacketClass, byteBuf);

                client.send(packet, destPacketClass, (session, p) -> {
                    try {
                        if (destPacketClass != null) {
                            Object chatPacket = destPacketClass.cast(packet);
                            Method getMessageMethod = destPacketClass.getMethod("getMessage");
                            Component msg = (Component) getMessageMethod.invoke(chatPacket);
                            commandSender.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }
}
