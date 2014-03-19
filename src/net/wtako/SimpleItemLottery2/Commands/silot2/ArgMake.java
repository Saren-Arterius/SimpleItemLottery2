package net.wtako.SimpleItemLottery2.Commands.silot2;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import net.wtako.SimpleItemLottery2.Main;
import net.wtako.SimpleItemLottery2.Utils.Lang;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ArgMake {

    public ArgMake(CommandSender sender, String[] args) {
        try {
            if (args.length >= 2) {
                int prizeClass = Integer.parseInt(args[1]);
                final String itemTypeRequiredString = Main.getInstance().getConfig()
                        .getString("variable.make.TicketItemType");
                final Material itemTypeRequired = Material.getMaterial(itemTypeRequiredString.toUpperCase());
                final ItemStack lotteryTickets = new ItemStack(itemTypeRequired, 1);
                ItemMeta lotteryTicketMeta = lotteryTickets.getItemMeta();
                List<String> lores = new ArrayList<String>();
                lores.add(Lang.LOTTERY_TICKET.toString());
                lores.add(MessageFormat.format("Class: {0}", prizeClass));
                lores.add(MessageFormat.format(Lang.LOTTERY_TICKET_CLASS.toString(), prizeClass));
                lores.add(Lang.LOTTERY_TICKET_USAGE.toString());
                lotteryTicketMeta.setLore(lores);
                lotteryTicketMeta.setDisplayName(MessageFormat.format(Lang.LOTTERY_TICKET_DISPLAY_NAME.toString(), prizeClass));
                lotteryTickets.setItemMeta(lotteryTicketMeta);
                if (args.length >= 3) {
                    Integer amount;
                    try {
                        amount = Integer.parseInt(args[2]);
                        if (amount <= 1) {
                            amount = 1;
                        }
                    } catch (final NumberFormatException e) {
                        amount = 1;
                    }
                    lotteryTickets.setAmount(amount);
                }
                if (Main.getInstance().getConfig().getBoolean("variable.make.Enchant")) {
                    lotteryTickets.addUnsafeEnchantment(Enchantment.LUCK, 1);
                }
                ((Player) sender).getInventory().addItem(lotteryTickets);
                sender.sendMessage(Lang.MAKE_SUCCESS.toString());
            } else {
                sender.sendMessage(Lang.HELP_MAKE.toString());
            }
        } catch (final NumberFormatException e) {
            sender.sendMessage(Lang.HELP_MAKE.toString());
        }
    }

}
