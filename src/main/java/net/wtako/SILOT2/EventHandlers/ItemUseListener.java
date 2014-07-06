package net.wtako.SILOT2.EventHandlers;

import java.io.File;
import java.io.FileWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.milkbowl.vault.economy.Economy;
import net.wtako.SILOT2.Main;
import net.wtako.SILOT2.Methods.PrizesDatabase;
import net.wtako.SILOT2.Utils.Lang;
import net.wtako.SILOT2.Utils.StringUtils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

public class ItemUseListener implements Listener {

    @SuppressWarnings({"deprecation"})
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Integer prizeClass;
        final Player player = event.getPlayer();
        final ItemStack lotteryTicketItem = event.getItem();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (lotteryTicketItem == null || !lotteryTicketItem.hasItemMeta() || !lotteryTicketItem.getItemMeta().hasLore()) {
            return;
        }
        final List<String> lore = lotteryTicketItem.getItemMeta().getLore();
        if (lore.size() < 4) {
            return;
        }
        try {
            try {
                prizeClass = Integer.parseInt(StringUtils.fromInvisible(lore.get(1)));
            } catch (final Exception e) {
                final String IDRow = lore.get(1);
                final String regex = "^Class: (\\d+)$";
                final Pattern pattern = Pattern.compile(regex);
                final Matcher matcher = pattern.matcher(IDRow);
                matcher.find();
                prizeClass = Integer.parseInt(matcher.group(1));
            }
            if (!player.hasPermission(Main.getInstance().getProperty("artifactId") + ".use")) {
                player.sendMessage(Lang.NO_PERMISSION_DO.toString());
                return;
            }
            if (!player.hasPermission(MessageFormat.format(Main.getInstance().getProperty("artifactId") + ".class.{0}",
                    prizeClass))) {
                player.sendMessage(Lang.NO_PERMISSION_CLASS.toString());
                return;
            }
            final Integer randomRow = PrizesDatabase.getRandomRowID(prizeClass);
            if (randomRow == null) {
                player.sendMessage(Lang.NO_PRIZE_FROM_TICKET.toString());
                return;
            }
            final ItemStack prize = PrizesDatabase.getPrizeItem(randomRow);
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
            final int remaining = (player.getInventory().all(lotteryTicketItem).size() * lotteryTicketItem.getAmount()) - 1;
            player.getInventory().remove(lotteryTicketItem);
            if (remaining > 0) {
                lotteryTicketItem.setAmount(remaining);
                player.getInventory().addItem(lotteryTicketItem);
            }
            player.updateInventory();
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
