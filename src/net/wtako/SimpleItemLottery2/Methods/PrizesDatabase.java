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
import org.json.simple.JSONObject;

public class PrizesDatabase extends Database {

    public PrizesDatabase() throws SQLException {
        super();
    }

    public static void addItem(Player player, int prizeClass, int prob) throws SQLException {
        final ItemStack item = player.getItemInHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(Lang.CANNOT_ADD_AIR.toString());
            return;
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
    }

    public static void addItem(int moneyAmount, int prizeClass, int prob, Player player) throws SQLException {
        if (moneyAmount < 1) {
            player.sendMessage(Lang.MONEY_ERROR.toString());
            return;
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
    }

    public static boolean deleteItem(int rowID) throws SQLException {
        final PreparedStatement selStmt = Database.getInstance().conn
                .prepareStatement("SELECT * FROM `prizes` WHERE rowid = ? LIMIT 1");
        selStmt.setInt(1, rowID);
        final ResultSet result = selStmt.executeQuery();
        if (!result.next()) {
            result.close();
            selStmt.close();
            return false;
        }
        result.close();
        selStmt.close();
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
        Integer rowsLimit = Main.getInstance().getConfig().getInt("variable.list.RowsPerPage");

        if (prizeClass != null) {
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
            Main.log.info("pc is null");
            Main.log.info(page.toString());
            selStmt2 = Database.getInstance().conn.prepareStatement("SELECT * FROM `prizes` ORDER BY rowid DESC LIMIT "
                    + rowsLimit.toString() + " OFFSET ?");
            selStmt2.setInt(1, (page - 1) * rowsLimit);
            result = selStmt2.executeQuery();
        }
        final List<String> tableArrayList = new ArrayList<String>();
        tableArrayList.add("List:");
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
                .prepareStatement("SELECT * FROM `prizes` WHERE prize_class = ?");
        selStmt1.setInt(1, prizeClass);
        ResultSet result1 = selStmt1.executeQuery();
        while (result1.next()) {
            probSum += result1.getInt(4);
        }
        result1.close();
        selStmt1.close();

        if (probSum == 0) {
            return null;
        }
        
        double rand = Math.random() * probSum;

        final PreparedStatement selStmt2 = Database.getInstance().conn
                .prepareStatement("SELECT * FROM `prizes` WHERE prize_class = ?");
        selStmt2.setInt(1, prizeClass);
        ResultSet result2 = selStmt2.executeQuery();
        while (result2.next()) {
            rand -= result2.getInt(4);
            if (rand <= 0) {
                int rowID = result2.getInt(1);
                selStmt2.close();
                result2.close();
                return rowID;
            }
        }
        selStmt2.close();
        result2.close();
        return null;
    }

    public static ItemStack getItem(int rowID) throws SQLException {
        return null;
    }
}
