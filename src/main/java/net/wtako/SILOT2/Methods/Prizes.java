package net.wtako.SILOT2.Methods;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;

import net.wtako.SILOT2.Main;
import net.wtako.SILOT2.Utils.Config;
import net.wtako.SILOT2.Utils.ItemUtils;
import net.wtako.SILOT2.Utils.Lang;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Prizes {

    public static boolean addItem(Player player, int prizeClass, int prob) throws SQLException {
        final ItemStack newPrize = player.getItemInHand();
        if (newPrize.getType() == Material.AIR) {
            player.sendMessage(Lang.CANNOT_ADD_AIR.toString());
            return false;
        }
        if (prizeClass < 0 || prizeClass > Config.MAX_CLASS.getInt()) {
            player.sendMessage(MessageFormat.format(Lang.VALUE_ERROR.toString(), "prize class",
                    Config.MAX_CLASS.getInt()));
            return false;
        }
        if (prob < 0 || prob > Config.MAX_PROB.getInt()) {
            player.sendMessage(MessageFormat.format(Lang.VALUE_ERROR.toString(), "prob", Config.MAX_PROB.getInt()));
            return false;
        }
        final PreparedStatement insStmt = Database.getConn().prepareStatement(
                "INSERT INTO `prizes` (" + "`added_by`, " + "`prize_class`, " + "`prob`, " + "`item_json`, "
                        + "`timestamp`" + ") " + "VALUES " + "(?, ?, ?, ?, ?)");
        insStmt.setString(1, player.getName());
        insStmt.setInt(2, prizeClass);
        insStmt.setInt(3, prob);
        insStmt.setString(4, ItemUtils.encodeItem(newPrize).toJSONString());
        insStmt.setLong(5, System.currentTimeMillis());
        insStmt.execute();
        insStmt.close();
        return true;
    }

    public static boolean addCashPrize(int moneyAmount, int prizeClass, int prob, Player player) throws SQLException {
        if (moneyAmount < 1) {
            player.sendMessage(Lang.MONEY_ERROR.toString());
            return false;
        }
        if (prizeClass < 0 || prizeClass > Config.MAX_CLASS.getInt()) {
            player.sendMessage(MessageFormat.format(Lang.VALUE_ERROR.toString(), "prize class",
                    Config.MAX_CLASS.getInt()));
            return false;
        }
        if (prob < 0 || prob > Config.MAX_PROB.getInt()) {
            player.sendMessage(MessageFormat.format(Lang.VALUE_ERROR.toString(), "prob", Config.MAX_PROB.getInt()));
            return false;
        }
        final PreparedStatement insStmt = Database.getConn().prepareStatement(
                "INSERT INTO `prizes` (`added_by`, `prize_class`, `prob`, `cash`, `timestamp`) VALUES (?, ?, ?, ?, ?)");
        insStmt.setString(1, player.getName());
        insStmt.setInt(2, prizeClass);
        insStmt.setInt(3, prob);
        insStmt.setInt(4, moneyAmount);
        insStmt.setLong(5, System.currentTimeMillis());
        insStmt.execute();
        insStmt.close();
        return true;
    }

    public static boolean deleteItem(int rowID) throws SQLException {
        final PreparedStatement delStmt = Database.getConn().prepareStatement("DELETE FROM `prizes` WHERE rowid = ?");
        delStmt.setInt(1, rowID);
        delStmt.execute();
        delStmt.close();
        return true;
    }

    public static String[] listAllItems(Integer prizeClass, Integer page) throws SQLException {
        boolean hasNoPrize = true;
        PreparedStatement selStmt2;
        ResultSet result;
        Integer pagesCount;
        final Integer rowsLimit = Config.ROWS_PER_PAGE.getInt();

        if (prizeClass != null) {
            if (prizeClass < 0 || prizeClass > Config.MAX_CLASS.getInt()) {
                final String msg[] = {MessageFormat.format(Lang.VALUE_ERROR.toString(), "prize class",
                        Config.MAX_CLASS.getInt())};
                return msg;
            }
            final PreparedStatement selStmt1 = Database.getConn().prepareStatement(
                    "SELECT count(*) FROM `prizes` WHERE prize_class = ?");
            selStmt1.setInt(1, prizeClass);
            pagesCount = (int) Math.floor(selStmt1.executeQuery().getInt(1) / rowsLimit) + 1;
            selStmt1.close();
            if (page >= pagesCount) {
                page = pagesCount;
            }
            selStmt2 = Database.getConn().prepareStatement(
                    "SELECT * FROM `prizes` WHERE prize_class = ? ORDER BY row_id DESC LIMIT " + rowsLimit.toString()
                            + " OFFSET ?");
            selStmt2.setInt(1, prizeClass);
            selStmt2.setInt(2, (page - 1) * rowsLimit);
            result = selStmt2.executeQuery();
        } else {
            final PreparedStatement selStmt1 = Database.getConn().prepareStatement("SELECT count(*) FROM `prizes`");
            pagesCount = (int) Math.floor(selStmt1.executeQuery().getInt(1) / rowsLimit) + 1;
            selStmt1.close();
            if (page >= pagesCount) {
                page = pagesCount;
            }
            selStmt2 = Database.getConn().prepareStatement(
                    "SELECT * FROM `prizes` ORDER BY row_id DESC LIMIT " + rowsLimit.toString() + " OFFSET ?");
            selStmt2.setInt(1, (page - 1) * rowsLimit);
            result = selStmt2.executeQuery();
        }
        final ArrayList<String> tableArrayList = new ArrayList<String>();
        tableArrayList.add(Lang.TABLE_LIST.toString());
        while (result.next()) {
            hasNoPrize = false;
            final String itemJson = result.getString("item_json");
            if (itemJson != null) {
                final ItemStack item = ItemUtils.restoreItem(itemJson);
                tableArrayList.add(MessageFormat.format(Lang.LIST_FORMAT1.toString(), result.getInt("row_id"), result
                        .getString("added_by"), item.getAmount(), item.getItemMeta().hasDisplayName() ? item
                        .getItemMeta().getDisplayName() : item.getType().name(), item.getType().name()));
            } else {
                tableArrayList.add(MessageFormat.format(Lang.LIST_FORMAT1.toString(), result.getInt("row_id"),
                        result.getString("added_by"), result.getInt("cash"), Lang.MONEY_SIGN.toString(), "MONEY"));
            }
            tableArrayList.add(MessageFormat.format(Lang.LIST_FORMAT2.toString(), result.getInt("prize_class"),
                    result.getInt("prob"), new Date(result.getLong("timestamp"))));
        }
        result.close();
        selStmt2.close();
        if (hasNoPrize) {
            tableArrayList.add(Lang.NO_PRIZE.toString());
            tableArrayList.add(Lang.HELP_ADD.toString());
            tableArrayList.remove(0);
        } else {
            tableArrayList.add(MessageFormat.format(Lang.PAGE_NUMBER.toString(), page, pagesCount));
        }
        final String[] finalMessage = new String[tableArrayList.size()];
        return tableArrayList.toArray(finalMessage);
    }

    public static Integer getRandomRowID(int prizeClass) throws SQLException {
        final PreparedStatement selStmt1 = Database.getConn().prepareStatement(
                "SELECT SUM(prob) FROM `prizes` WHERE prize_class = ?");
        selStmt1.setInt(1, prizeClass);
        final int probSum = selStmt1.executeQuery().getInt(1);
        selStmt1.close();

        if (probSum == 0) {
            return null;
        }

        double rand = Math.random() * probSum;

        final PreparedStatement selStmt2 = Database.getConn().prepareStatement(
                "SELECT row_id, prob FROM `prizes` WHERE prize_class = ?");
        selStmt2.setInt(1, prizeClass);
        final ResultSet result2 = selStmt2.executeQuery();
        int rowID = -1;
        while (result2.next()) {
            rand -= result2.getInt(2);
            if (rand <= 0) {
                rowID = result2.getInt(1);
                break;
            }
        }
        selStmt2.close();
        result2.close();
        if (rowID == -1) {
            return null;
        }
        return rowID;
    }

    public static ItemStack getPrizeItem(int rowID) throws SQLException {
        final PreparedStatement selStmt = Database.getConn().prepareStatement(
                "SELECT item_json FROM `prizes` WHERE row_id = ?");
        selStmt.setInt(1, rowID);
        final ResultSet result = selStmt.executeQuery();
        if (!result.next()) {
            result.close();
            selStmt.close();
            return null;
        }
        final ItemStack prize = ItemUtils.restoreItem(result.getString(1));
        result.close();
        selStmt.close();
        return prize;
    }

    public static Integer getCashAmount(int rowID) throws SQLException {
        final PreparedStatement selStmt = Database.getConn().prepareStatement(
                "SELECT cash FROM `prizes` WHERE row_id = ? AND cash >= 0");
        selStmt.setInt(1, rowID);
        final ResultSet result = selStmt.executeQuery();
        if (!result.next()) {
            result.close();
            selStmt.close();
            return null;
        }
        final Integer cash = result.getInt(1);
        result.close();
        selStmt.close();
        return cash;
    }

    public static void giveOutCash(Player player, int rowID, int prizeClass) throws SQLException, IOException {
        if (Main.econ != null) {
            final Integer cashPrizeAmount = Prizes.getCashAmount(rowID);
            if (cashPrizeAmount == null) {
                player.sendMessage(Lang.UNKNOWN_ERROR.toString());
                throw new IllegalStateException("Cash amount is null! "
                        + "Probably somebody has screwed up some table data.");
            }
            Main.econ.depositPlayer(player, cashPrizeAmount);
            final FileWriter writer = new FileWriter(new File(Main.getInstance().getDataFolder(), "log.log"), true);
            writer.append(MessageFormat.format(Lang.LOG_FORMAT.toString() + "\r\n",
                    new Date(System.currentTimeMillis()), player.getName(), prizeClass, Lang.MONEY_SIGN.toString()
                            + cashPrizeAmount.toString()));
            writer.close();
            player.sendMessage(MessageFormat.format(Lang.YOU_WON_MONEY.toString(), cashPrizeAmount));
        } else {
            player.sendMessage(Lang.ECON_NOT_SUPPORTED.toString());
            return;
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean giveOutItem(final Player player, int rowID, int prizeClass) throws SQLException, IOException {
        final ItemStack prize = Prizes.getPrizeItem(rowID);
        if (prize == null) {
            return false;
        }
        final FileWriter writer = new FileWriter(new File(Main.getInstance().getDataFolder(), "log.log"), true);
        writer.append(MessageFormat.format(Lang.LOG_FORMAT.toString() + "\r\n", new Date(System.currentTimeMillis()),
                player.getName(), prizeClass, prize.getAmount() + " x " + prize.getItemMeta().getDisplayName() + "("
                        + prize.getType().name() + ")"));
        writer.close();
        if (prize.getItemMeta().getDisplayName() != null) {
            player.sendMessage(MessageFormat.format(Lang.YOU_WON_THIS_DISPLAYNAME.toString(), prize.getAmount(), prize
                    .getItemMeta().getDisplayName(), prize.getType().name()));
        } else {
            player.sendMessage(MessageFormat.format(Lang.YOU_WON_THIS.toString(), prize.getAmount(), prize.getType()
                    .name()));
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                player.getInventory().addItem(prize);
                player.updateInventory();
            }
        }.runTask(Main.getInstance());
        return true;
    }
}
