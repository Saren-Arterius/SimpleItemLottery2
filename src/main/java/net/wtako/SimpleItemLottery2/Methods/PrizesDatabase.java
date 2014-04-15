package net.wtako.SimpleItemLottery2.Methods;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.wtako.SimpleItemLottery2.Main;
import net.wtako.SimpleItemLottery2.Utils.Lang;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class PrizesDatabase extends Database {

    public PrizesDatabase() throws SQLException {
        super();
    }

    public static boolean addItem(Player player, int prizeClass, int prob) throws SQLException {
        final ItemStack item = player.getItemInHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(Lang.CANNOT_ADD_AIR.toString());
            return false;
        }
        if (prizeClass < 0 || prizeClass > Main.getInstance().getConfig().getInt("variable.add.MaxClassCount")) {
            player.sendMessage(MessageFormat.format(Lang.VALUE_ERROR.toString(), "prize class", Main.getInstance()
                    .getConfig().getInt("variable.add.MaxClassCount")));
            return false;
        }
        if (prob < 0 || prob > Main.getInstance().getConfig().getInt("variable.add.MaxProb")) {
            player.sendMessage(MessageFormat.format(Lang.VALUE_ERROR.toString(), "prob", Main.getInstance().getConfig()
                    .getInt("variable.add.MaxProb")));
            return false;
        }
        final Map<String, Integer> tempEnchantmentMap = new HashMap<String, Integer>();
        final Map<Integer, String> tempLoreMap = new HashMap<Integer, String>();
        final PreparedStatement insStmt = Database.getInstance().conn
                .prepareStatement("INSERT INTO `prizes` (`added_by`, `prize_class`, `prob`, `item_type`, `enchantment`, `display_name`, `lore`, `amount`, `timestamp`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        insStmt.setString(1, player.getName());
        insStmt.setInt(2, prizeClass);
        insStmt.setInt(3, prob);
        insStmt.setString(4, item.getType().name());
        for (final Entry<Enchantment, Integer> entry: item.getEnchantments().entrySet()) {
            tempEnchantmentMap.put(entry.getKey().getName(), entry.getValue());
        }
        insStmt.setString(5, JSONObject.toJSONString(tempEnchantmentMap));
        insStmt.setString(6, item.getItemMeta().getDisplayName());
        if (item.getItemMeta().getLore() != null) {
            for (final String entry: item.getItemMeta().getLore()) {
                tempLoreMap.put(item.getItemMeta().getLore().indexOf(entry), entry);
            }
        }
        insStmt.setString(7, JSONObject.toJSONString(tempLoreMap));
        insStmt.setInt(8, item.getAmount());
        insStmt.setInt(9, (int) (System.currentTimeMillis() / 1000L));
        insStmt.execute();
        insStmt.close();
        return true;
    }

    public static boolean addCashPrize(int moneyAmount, int prizeClass, int prob, Player player) throws SQLException {
        if (!Main.getInstance().getConfig().getBoolean("system.VaultSupport")) {
            player.sendMessage(Lang.ECON_NOT_SUPPORTED.toString());
            return false;
        }
        if (moneyAmount < 1) {
            player.sendMessage(Lang.MONEY_ERROR.toString());
            return false;
        }
        if (prizeClass < 0 || prizeClass > Main.getInstance().getConfig().getInt("variable.add.MaxClassCount")) {
            player.sendMessage(MessageFormat.format(Lang.VALUE_ERROR.toString(), "prize class", Main.getInstance()
                    .getConfig().getInt("variable.add.MaxClassCount")));
            return false;
        }
        if (prob < 0 || prob > Main.getInstance().getConfig().getInt("variable.add.MaxProb")) {
            player.sendMessage(MessageFormat.format(Lang.VALUE_ERROR.toString(), "prob", Main.getInstance().getConfig()
                    .getInt("variable.add.MaxProb")));
            return false;
        }
        final PreparedStatement insStmt = Database.getInstance().conn
                .prepareStatement("INSERT INTO `prizes` (`added_by`, `prize_class`, `prob`, `item_type`, `display_name`, `amount`, `timestamp`) VALUES (?, ?, ?, ?, ?, ?, ?)");
        insStmt.setString(1, player.getName());
        insStmt.setInt(2, prizeClass);
        insStmt.setInt(3, prob);
        insStmt.setString(4, "MONEY");
        insStmt.setString(5, Lang.MONEY_SIGN.toString());
        insStmt.setInt(6, moneyAmount);
        insStmt.setInt(7, (int) (System.currentTimeMillis() / 1000L));
        insStmt.execute();
        insStmt.close();
        return true;
    }

    public static boolean deleteItem(int rowID) throws SQLException {
        final PreparedStatement delStmt = Database.getInstance().conn
                .prepareStatement("DELETE FROM `prizes` WHERE rowid = ?");
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
        final Integer rowsLimit = Main.getInstance().getConfig().getInt("variable.list.RowsPerPage");

        if (prizeClass != null) {
            if (prizeClass < 0 || prizeClass > Main.getInstance().getConfig().getInt("variable.add.MaxClassCount")) {
                final String msg[] = {MessageFormat.format(Lang.VALUE_ERROR.toString(), "prize class", Main
                        .getInstance().getConfig().getInt("variable.add.MaxClassCount"))};
                return msg;
            }
            final PreparedStatement selStmt1 = Database.getInstance().conn
                    .prepareStatement("SELECT count(*) FROM `prizes` WHERE prize_class = ?");
            selStmt1.setInt(1, prizeClass);
            pagesCount = (int) Math.floor(selStmt1.executeQuery().getInt(1) / rowsLimit) + 1;
            selStmt1.close();
            if (page >= pagesCount) {
                page = pagesCount;
            }
            selStmt2 = Database.getInstance().conn
                    .prepareStatement("SELECT * FROM `prizes` WHERE prize_class = ? ORDER BY rowid DESC LIMIT "
                            + rowsLimit.toString() + " OFFSET ?");
            selStmt2.setInt(1, prizeClass);
            selStmt2.setInt(2, (page - 1) * rowsLimit);
            result = selStmt2.executeQuery();
        } else {
            final PreparedStatement selStmt1 = Database.getInstance().conn
                    .prepareStatement("SELECT count(*) FROM `prizes`");
            pagesCount = (int) Math.floor(selStmt1.executeQuery().getInt(1) / rowsLimit) + 1;
            selStmt1.close();
            if (page >= pagesCount) {
                page = pagesCount;
            }
            selStmt2 = Database.getInstance().conn.prepareStatement("SELECT * FROM `prizes` ORDER BY rowid DESC LIMIT "
                    + rowsLimit.toString() + " OFFSET ?");
            selStmt2.setInt(1, (page - 1) * rowsLimit);
            result = selStmt2.executeQuery();
        }
        final List<String> tableArrayList = new ArrayList<String>();
        tableArrayList.add(Lang.TABLE_LIST.toString());
        while (result.next()) {
            hasNoPrize = false;
            tableArrayList.add(MessageFormat.format(Lang.LIST_FORMAT1.toString(), result.getInt(1),
                    result.getString(2), result.getInt(9), result.getString(7), result.getString(5)));
            tableArrayList.add(MessageFormat.format(Lang.LIST_FORMAT2.toString(), result.getInt(4), result.getInt(3),
                    new Date((long) result.getInt(10) * 1000)));
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
        int probSum = 0;

        final PreparedStatement selStmt1 = Database.getInstance().conn
                .prepareStatement("SELECT rowid, prob FROM `prizes` WHERE prize_class = ?");
        selStmt1.setInt(1, prizeClass);
        final ResultSet result1 = selStmt1.executeQuery();
        while (result1.next()) {
            probSum += result1.getInt(2);
        }
        result1.close();
        selStmt1.close();

        if (probSum == 0) {
            return null;
        }

        double rand = Math.random() * probSum;

        final PreparedStatement selStmt2 = Database.getInstance().conn
                .prepareStatement("SELECT rowid, prob FROM `prizes` WHERE prize_class = ?");
        selStmt2.setInt(1, prizeClass);
        final ResultSet result2 = selStmt2.executeQuery();
        while (result2.next()) {
            rand -= result2.getInt(2);
            if (rand <= 0) {
                final int rowID = result2.getInt(1);
                selStmt2.close();
                result2.close();
                return rowID;
            }
        }
        selStmt2.close();
        result2.close();
        return null;
    }

    @SuppressWarnings("unchecked")
    public static ItemStack getItem(int rowID) throws SQLException {
        final PreparedStatement selStmt = Database.getInstance().conn
                .prepareStatement("SELECT * FROM `prizes` WHERE rowid = ?");
        selStmt.setInt(1, rowID);
        final ResultSet result = selStmt.executeQuery();
        if (!result.next()) {
            result.close();
            selStmt.close();
            return null;
        }
        if (result.getString(5).equalsIgnoreCase("money")) {
            result.close();
            selStmt.close();
            return null;
        }
        final ItemStack prize = new ItemStack(Material.getMaterial(result.getString(5)), result.getInt(9));
        final ItemMeta prizeMeta = prize.getItemMeta();
        final List<String> prizeLores = new ArrayList<String>();
        prizeMeta.setDisplayName(result.getString(7));
        /* WTF? */
        final Map<String, String> prizeLoresMap = (Map<String, String>) JSONValue.parse(result.getString(8));
        /* What is this shit? */
        for (int i = 0; i < prizeLoresMap.size(); i++) {
            prizeLores.add(prizeLoresMap.get(String.valueOf(i)));
        }
        prizeMeta.setLore(prizeLores);
        prize.setItemMeta(prizeMeta);
        /* What is this shit? */
        final Map<String, Long> enchantmentMap = (Map<String, Long>) JSONValue.parse(result.getString(6));
        /* Why is it Long instead of Integer or int? */
        for (final Entry<String, Long> entry: enchantmentMap.entrySet()) {
            prize.addUnsafeEnchantment(Enchantment.getByName(entry.getKey()), (int) (long) entry.getValue());
        }
        result.close();
        selStmt.close();
        return prize;
    }

    public static Integer getCashAmount(int rowID) throws SQLException {
        final PreparedStatement selStmt = Database.getInstance().conn
                .prepareStatement("SELECT amount FROM `prizes` WHERE rowid = ? AND amount >= 1 AND item_type = 'MONEY'");
        selStmt.setInt(1, rowID);
        final ResultSet result = selStmt.executeQuery();
        if (!result.next()) {
            result.close();
            selStmt.close();
            return null;
        }
        final Integer amount = result.getInt(1);
        Main.log.info(amount.toString());
        result.close();
        selStmt.close();
        return amount;
    }
}
