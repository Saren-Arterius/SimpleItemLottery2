package net.wtako.SimpleItemLottery2.EventHandlers;

import java.io.File;
import java.io.FileWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.milkbowl.vault.economy.Economy;
import net.wtako.SimpleItemLottery2.Main;
import net.wtako.SimpleItemLottery2.Methods.PrizesDatabase;
import net.wtako.SimpleItemLottery2.Utils.Lang;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

public class ItemUseListener implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack lotteryTicketItem = event.getItem();
        if (!player.hasPermission("SILOT2.use")) {
            player.sendMessage(Lang.NO_PERMISSION_DO.toString());
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (lotteryTicketItem == null || !lotteryTicketItem.hasItemMeta() || !lotteryTicketItem.getItemMeta().hasLore()) {
            return;
        }
        if (lotteryTicketItem.getType() != Material.getMaterial(Main.getInstance().getConfig()
                .getString("variable.make.TicketItemType").toUpperCase())) {
            return;
        }
        final String IDRow = lotteryTicketItem.getItemMeta().getLore().get(1);
        final String regex = "^Class: (\\d+)$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(IDRow);
        matcher.find();
        try {
            final Integer prizeClass = Integer.parseInt(matcher.group(1));
            if (!player.hasPermission(MessageFormat.format("SILOT2.class.{0}", prizeClass))) {
                player.sendMessage(Lang.NO_PERMISSION_CLASS.toString());
                return;
            }
            final Integer randomRow = PrizesDatabase.getRandomRowID(prizeClass);
            if (randomRow == null) {
                player.sendMessage(Lang.NO_PRIZE_FROM_TICKET.toString());
                return;
            }
            final ItemStack prize = PrizesDatabase.getItem(randomRow);
            if (prize != null) {
                player.getInventory().addItem(prize);
                final FileWriter writer = new FileWriter(new File(Main.getInstance().getDataFolder(), "log.log"), true);
                writer.append(MessageFormat.format(Lang.LOG_FORMAT.toString() + "\r\n",
                        new Date(System.currentTimeMillis()), player.getName(), prizeClass, prize.getAmount() + " x "
                                + prize.getItemMeta().getDisplayName() + "(" + prize.getType().name() + ")"));
                writer.close();
                player.updateInventory();
                if (prize.getItemMeta().getDisplayName() != null) {
                    player.sendMessage(MessageFormat.format(Lang.YOU_WON_THIS_DISPLAYNAME.toString(),
                            prize.getAmount(), prize.getItemMeta().getDisplayName(), prize.getType().name()));
                } else {
                    player.sendMessage(MessageFormat.format(Lang.YOU_WON_THIS.toString(), prize.getAmount(), prize
                            .getType().name()));
                }
            } else {
                if (Main.getInstance().getConfig().getBoolean("system.VaultSupport")) {
                    try {
                        final RegisteredServiceProvider<Economy> provider = Main.getInstance().getServer()
                                .getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
                        final Economy economy = provider.getProvider();
                        Main.log.info(String.valueOf(randomRow));
                        final Integer cashPrizeAmount = PrizesDatabase.getCashAmount(randomRow);
                        if (cashPrizeAmount == null) {
                            player.sendMessage(Lang.UNKNOWN_ERROR.toString());
                            throw new Exception(
                                    "Cash amount is null! Probably somebody has screwed up some table data.");
                        }
                        economy.depositPlayer(player.getName(), cashPrizeAmount);
                        final FileWriter writer = new FileWriter(
                                new File(Main.getInstance().getDataFolder(), "log.log"), true);
                        writer.append(MessageFormat.format(Lang.LOG_FORMAT.toString() + "\r\n",
                                new Date(System.currentTimeMillis()), player.getName(), prizeClass,
                                Lang.MONEY_SIGN.toString() + cashPrizeAmount.toString()));
                        writer.close();
                        player.sendMessage(MessageFormat.format(Lang.YOU_WON_MONEY.toString(), cashPrizeAmount));
                    } catch (final Error e) {
                        player.sendMessage(MessageFormat.format(Lang.ERROR_HOOKING.toString(), "Vault"));
                        e.printStackTrace();
                        return;
                    }
                } else {
                    player.sendMessage(Lang.ECON_NOT_SUPPORTED.toString());
                    return;
                }
            }
            player.getInventory().remove(lotteryTicketItem);
            final int remaining = lotteryTicketItem.getAmount() - 1;
            if (remaining > 0) {
                lotteryTicketItem.setAmount(remaining);
                player.getInventory().addItem(lotteryTicketItem);
            }
        } catch (final SQLException e) {
            player.sendMessage(Lang.DB_EXCEPTION.toString());
            e.printStackTrace();
            return;
        } catch (final IllegalStateException e) {
            return; // Not a prize ticket
        } catch (final Exception e) {
            e.printStackTrace();
            return;
        }
    }
}
