package net.wtako.SILOT2.Methods;

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

import net.wtako.SILOT2.Main;
import net.wtako.SILOT2.Utils.ItemUtils;
import net.wtako.SILOT2.Utils.Lang;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class PrizesDatabase extends Database {

    public PrizesDatabase() throws SQLException {
        super();
    }

    @SuppressWarnings("unchecked")
    public static boolean addItem(Player player, int prizeClass, int prob) throws SQLException {
        final ItemStack newPrize = player.getItemInHand();
        if (newPrize.getType() == Material.AIR) {
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
        final JSONObject metaJson = new JSONObject();
        metaJson.putAll(ItemUtils.serialize(newPrize.getItemMeta()));
        final PreparedStatement insStmt = Database.getInstance().conn
                .prepareStatement("INSERT INTO `prizes` (`added_by`, `prize_class`, `prob`, `item_type`, `enchantment`, `display_name`, `meta`, `amount`, `timestamp`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        insStmt.setString(1, player.getName());
        insStmt.setInt(2, prizeClass);
        insStmt.setInt(3, prob);
        insStmt.setString(4, newPrize.getType().name());

        if (newPrize.getType() == Material.ENCHANTED_BOOK) {
            final EnchantmentStorageMeta esm = (EnchantmentStorageMeta) newPrize.getItemMeta();
            for (final Entry<Enchantment, Integer> entry: esm.getStoredEnchants().entrySet()) {
                tempEnchantmentMap.put(entry.getKey().getName(), entry.getValue());
            }
        } else {
            for (final Entry<Enchantment, Integer> entry: newPrize.getEnchantments().entrySet()) {
                tempEnchantmentMap.put(entry.getKey().getName(), entry.getValue());
            }
        }

        insStmt.setString(5, JSONObject.toJSONString(tempEnchantmentMap));
        insStmt.setString(6, newPrize.getItemMeta().getDisplayName());
        insStmt.setString(7, metaJson.toJSONString());
        insStmt.setInt(8, newPrize.getAmount());
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
        final PreparedStatement selStmt1 = Database.getInstance().conn
                .prepareStatement("SELECT SUM(prob) FROM `prizes` WHERE prize_class = ?");
        selStmt1.setInt(1, prizeClass);
        final int probSum = selStmt1.executeQuery().getInt(1);
        selStmt1.close();

        if (probSum == 0) {
            return null;
        }

        double rand = Math.random() * probSum;

        final PreparedStatement selStmt2 = Database.getInstance().conn
                .prepareStatement("SELECT rowid, prob FROM `prizes` WHERE prize_class = ?");
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

    @SuppressWarnings("unchecked")
    public static ItemStack getPrizeItem(int rowID) throws SQLException {
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

        final Map<String, Long> enchantmentMap = (Map<String, Long>) JSONValue.parse(result.getString(6));
        final Map<String, Object> prizeMetaMap = (Map<String, Object>) JSONValue.parse(result.getString(8));

        if (prize.getType() == Material.ENCHANTED_BOOK) {
            final EnchantmentStorageMeta esm = (EnchantmentStorageMeta) ItemUtils.deserialize(prizeMetaMap); // This
                                                                                                             // doesn't
                                                                                                             // give
                                                                                                             // shit
            for (final Entry<String, Long> entry: enchantmentMap.entrySet()) {
                esm.addStoredEnchant(Enchantment.getByName(entry.getKey()), entry.getValue().intValue(), false);
            }
            prize.setItemMeta(esm);
        } else {
            prize.setItemMeta((ItemMeta) ItemUtils.deserialize(prizeMetaMap));
            for (final Entry<String, Long> entry: enchantmentMap.entrySet()) {
                prize.addUnsafeEnchantment(Enchantment.getByName(entry.getKey()), entry.getValue().intValue());
            }
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
