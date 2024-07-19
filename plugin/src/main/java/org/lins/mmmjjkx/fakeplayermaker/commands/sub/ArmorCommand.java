package org.lins.mmmjjkx.fakeplayermaker.commands.sub;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commands.FPMSubCmd;
import org.lins.mmmjjkx.fakeplayermaker.commons.objects.IFPMPlayer;

import java.util.List;
import java.util.Map;

public class ArmorCommand extends FPMSubCmd {
    private final List<String> armorSlots = List.of("helmet", "chestplate", "leggings", "boots");

    public ArmorCommand(@NotNull String name) {
        super(name);
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender commandSender) {
        return Map.of(0, List.of("wear", "remove"), 1, FPMRecoded.fakePlayerManager.getFakePlayerNames(), 2, armorSlots);
    }

    @Override
    public String getHelpDescription() {
        return FPMRecoded.INSTANCE.getMessageHandler().get(null, "command.help.armor");
    }

    @Override
    public void execute(CommandSender commandSender, String s) {
        String mode = getArg(0);
        String playerName = getArg(1);
        String armorSlot = getArg(2);

        if (mode == null || playerName == null || armorSlot == null) {
            sendMessage("command.armor_usage");
            return;
        }

        IFPMPlayer player = getFakePlayer(commandSender, playerName);
        if (player == null) {
            return;
        }

        if (!(commandSender instanceof Player p)) {
            sendMessage("command.no_console");
            return;
        }

        if (mode.equals("wear")) {
            ItemStack item = p.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) {
                sendMessage("command.armor_no_item");
                return;
            } else if (!isArmor(item.getType())) {
                sendMessage("command.armor_not_armor");
                return;
            }

            if (!armorSlots.contains(armorSlot)) {
                sendMessage("command.invalid_armor_slot");
                return;
            }

            run(player, bk -> {
                PlayerInventory inv = bk.getInventory();
                switch (armorSlot) {
                    case "helmet" -> {
                        if (inv.getHelmet() == null) {
                            inv.setHelmet(item);
                        } else {
                            ItemStack invHelmet = inv.getHelmet();
                            ItemStack newHelmet = p.getInventory().getItemInMainHand();
                            p.getInventory().setItemInMainHand(invHelmet);

                            inv.setHelmet(newHelmet);
                        }
                    }
                    case "chestplate" -> {
                        if (inv.getChestplate() == null) {
                            inv.setChestplate(item);
                        } else {
                            ItemStack invChestplate = inv.getChestplate();
                            ItemStack newChestplate = p.getInventory().getItemInMainHand();
                            p.getInventory().setItemInMainHand(invChestplate);

                            inv.setChestplate(newChestplate);
                        }
                    }
                    case "leggings" -> {
                        if (inv.getLeggings() == null) {
                            inv.setLeggings(item);
                        } else {
                            ItemStack invLeggings = inv.getLeggings();
                            ItemStack newLeggings = p.getInventory().getItemInMainHand();
                            p.getInventory().setItemInMainHand(invLeggings);

                            inv.setLeggings(newLeggings);
                        }
                    }
                    case "boots" -> {
                        if (inv.getBoots() == null) {
                            inv.setBoots(item);
                        } else {
                            ItemStack invBoots = inv.getBoots();
                            ItemStack newBoots = p.getInventory().getItemInMainHand();
                            p.getInventory().setItemInMainHand(invBoots);

                            inv.setBoots(newBoots);
                        }
                    }
                }
            });
        } else if (mode.equals("remove")) {
            run(player, bk -> {
                PlayerInventory inv = bk.getInventory();
                switch (armorSlot) {
                    case "helmet" -> {
                        if (inv.getHelmet() != null) {
                            ItemStack helmet = inv.getHelmet();
                            inv.setHelmet(null);
                            p.getInventory().addItem(helmet);
                        }
                    }
                    case "chestplate" -> {
                        if (inv.getChestplate() != null) {
                            ItemStack chestplate = inv.getChestplate();
                            inv.setChestplate(null);
                            p.getInventory().addItem(chestplate);
                        }
                    }
                    case "leggings" -> {
                        if (inv.getLeggings() != null) {
                            ItemStack leggings = inv.getLeggings();
                            inv.setLeggings(null);
                            p.getInventory().addItem(leggings);
                        }
                    }
                    case "boots" -> {
                        if (inv.getBoots() != null) {
                            ItemStack boots = inv.getBoots();
                            inv.setBoots(null);
                            p.getInventory().addItem(boots);
                        }
                    }
                }
            });
        }
    }

    private boolean isArmor(Material material) {
        String materialName = material.name();
        return materialName.endsWith("HELMET") ||
                materialName.endsWith("CHESTPLATE") ||
                materialName.endsWith("LEGGINGS") ||
                materialName.endsWith("BOOTS");
    }
}
