package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import com.github.steveice10.mc.protocol.codec.MinecraftCodec;
import com.github.steveice10.mc.protocol.codec.MinecraftCodecHelper;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundDisguisedChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.objects.MCClient;

import java.time.Instant;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

public class CmdCommand extends FPMSubCmd {
    public CmdCommand() {
        super("command");

        addArgument("player", CommandArgumentType.REQUIRED);
        addArgument("command", CommandArgumentType.REQUIRED);
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
                ByteBuf byteBuf = new CompositeByteBuf(new PooledByteBufAllocator(), false, 16);
                MinecraftCodecHelper helper = MinecraftCodec.CODEC.getHelperFactory().get();
                helper.writeString(byteBuf, command);
                byteBuf.writeLong(Instant.now().toEpochMilli());
                byteBuf.writeLong(0L);
                byteBuf.writeBoolean(false);
                byteBuf.writeBytes(new byte[256]);
                byteBuf.writeInt(0);
                int length = command.length();
                helper.writeFixedBitSet(byteBuf, new BitSet(length), length);
                client.send(new ServerboundChatPacket(byteBuf, helper), ClientboundDisguisedChatPacket.class, (session, packet) -> {
                    ClientboundDisguisedChatPacket chatPacket = (ClientboundDisguisedChatPacket) packet;
                    Component msg = chatPacket.getMessage();
                    commandSender.sendMessage(msg);
                });
            }
        }
    }
}
