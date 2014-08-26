package net.wtako.SILOT2.Methods;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.wtako.SILOT2.Main;
import net.wtako.SILOT2.Utils.Config;
import net.wtako.SILOT2.Utils.Lang;
import net.wtako.SILOT2.Utils.StringUtils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Tickets {

    public static void ticketUse(final Inventory inv, final ItemStack ticket) {
        new BukkitRunnable() {
            @Override
            public void run() {
                final int num = inv.first(ticket);
                if (ticket.getAmount() == 1) {
                    inv.remove(ticket);
                    return;
                }
                ticket.setAmount(ticket.getAmount() - 1);
                inv.setItem(num, ticket);
            }
        }.runTask(Main.getInstance());
    }

    public static ItemStack newTicket(int prizeClass, int amount) {
        final String itemTypeRequiredString = Config.TICKET_ITEM_TYPE.getString();
        final Material itemTypeRequired = Material.getMaterial(itemTypeRequiredString.toUpperCase());
        final ItemStack ticket = new ItemStack(itemTypeRequired, 1);
        final ItemMeta lotteryTicketMeta = ticket.getItemMeta();
        final ArrayList<String> lores = new ArrayList<String>();
        lores.add(Lang.LOTTERY_TICKET.toString());
        lores.add(StringUtils.toInvisible(String.valueOf(prizeClass)));
        lores.add(MessageFormat.format(Lang.LOTTERY_TICKET_CLASS.toString(), prizeClass));
        lores.add(Lang.LOTTERY_TICKET_USAGE.toString());
        lotteryTicketMeta.setLore(lores);
        lotteryTicketMeta.setDisplayName(MessageFormat.format(Lang.LOTTERY_TICKET_DISPLAY_NAME.toString(), prizeClass));
        ticket.setAmount(amount);
        ticket.setItemMeta(lotteryTicketMeta);
        if (Config.ENCHANT.getBoolean()) {
            ticket.addUnsafeEnchantment(Enchantment.LUCK, 1);
        }
        return ticket;
    }

    public static Integer getClassID(ItemStack ticket) {
        if (ticket == null || !ticket.hasItemMeta() || !ticket.getItemMeta().hasLore()) {
            return null;
        }
        final List<String> lore = ticket.getItemMeta().getLore();
        if (lore.size() < 4) {
            return null;
        }
        try {
            return Integer.parseInt(StringUtils.fromInvisible(lore.get(1)));
        } catch (final NumberFormatException e) {
            try {
                final String IDRow = lore.get(1);
                final String regex = "^Class: (\\d+)$";
                final Pattern pattern = Pattern.compile(regex);
                final Matcher matcher = pattern.matcher(IDRow);
                matcher.find();
                return Integer.parseInt(matcher.group(1));
            } catch (final IllegalStateException e1) {
                return null;
            }
        }
    }

}
