package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import com.google.common.collect.Lists;
import io.github.linsminecraftstudio.polymer.TempPolymer;
import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFakePlayerManager;
import org.lins.mmmjjkx.fakeplayermaker.util.FakePlayerSaver;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ListCommand extends FPMSubCmd {
    public ListCommand() {
        super("list");

        addArgument("ownerOrPage", CommandArgumentType.OPTIONAL);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        List<String> players = Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> FPMRecoded.fakePlayerManager.get(name) == null).toList();
        return Map.of(0, players);
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        if (hasPermission()) {
            String owner = getArg(0);
            String page = getArg(1);

            int pageN;

            CommandSender cs;

            if (page != null) {
                try {
                    pageN = Integer.parseInt(page);
                } catch (NumberFormatException e) {
                    TempPolymer.getInstance().getMessageHandler().sendMessage(commandSender, "Value.NotInt", 3);
                    return;
                }
            } else {
                pageN = 1;
            }

            if (owner != null) {
                cs = Bukkit.getPlayerExact(owner);
                if (cs == null) {
                    try {
                        pageN = Integer.parseInt(owner);
                    } catch (NumberFormatException e) {
                        TempPolymer.getInstance().getMessageHandler().sendMessage(commandSender, "Command.PlayerNotFound");
                        return;
                    }
                }
            } else {
                cs = commandSender;
            }

            UUID ownerUUID;

            if (cs instanceof Player p) {
                ownerUUID = p.getUniqueId();
            } else {
                ownerUUID = FakePlayerSaver.NO_OWNER_UUID;
            }

            IFakePlayerManager manager = FPMRecoded.fakePlayerManager;
            List<IFPMPlayer> fakePlayers = manager.getFakePlayers(ownerUUID);
            if (fakePlayers.isEmpty()) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "no_fake_players_u_have");
                return;
            }

            List<List<IFPMPlayer>> parted_fakePlayers = Lists.partition(fakePlayers, 10);
            int maxPage = parted_fakePlayers.size();

            if (pageN > maxPage || pageN < 1) {
                FPMRecoded.INSTANCE.getMessageHandler().sendMessage(commandSender, "page_not_found", maxPage);
                return;
            }

            List<IFPMPlayer> fakePlayersOnPage = parted_fakePlayers.get(pageN - 1);

            int head = pageN == 1 ? 1 : 10 * pageN + 1;
            for (IFPMPlayer fakePlayer : fakePlayersOnPage) {
                TempPolymer.getInstance().getMessageHandler().sendMessage(commandSender, "Info.List.Head", pageN);



                head++;
            }

            TempPolymer.getInstance().getMessageHandler().sendMessage(commandSender, "Info.List.Tail");
            commandSender.sendMessage(this.buildClickEvent(commandSender, parted_fakePlayers, pageN));
        }
    }

    private Component buildClickEvent(CommandSender sender, List<List<IFPMPlayer>> partition, int page) {
        Component prev = TempPolymer.getInstance().getMessageHandler().getColored(sender, "Info.List.Prev");
        Component next = TempPolymer.getInstance().getMessageHandler().getColored(sender, "Info.List.Next");
        ClickEvent.Action var10000 = ClickEvent.Action.RUN_COMMAND;
        String var10001 = this.getName();
        ClickEvent prevClick = ClickEvent.clickEvent(var10000, "/" + var10001 + " " + (page - 1));
        ClickEvent nextClick = ClickEvent.clickEvent(var10000, "/" + var10001 + " " + (page + 1));
        if (page == 1) {
            prev = TempPolymer.getInstance().getMessageHandler().getColored(sender, "Info.List.PrevUnavailable");
            prevClick = null;
        }

        if (page >= partition.size()) {
            next = TempPolymer.getInstance().getMessageHandler().getColored(sender, "Info.List.NextUnavailable");
            nextClick = null;
        }

        Component component = Component.empty();
        if (prevClick != null) {
            prev = prev.clickEvent(prevClick);
        }

        if (nextClick != null) {
            next = next.clickEvent(nextClick);
        }

        Component space = Component.space();
        return component.append(prev).append(space).append(space).append(next);
    }
}
