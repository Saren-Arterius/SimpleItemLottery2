package net.wtako.SILOT2.Methods;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.wtako.SILOT2.Main;
import net.wtako.SILOT2.Utils.ItemUtils;
import net.wtako.SILOT2.Utils.Lang;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Database {

    private static Database instance;
    private static int      latestVersion = 2;
    private int             currentVersion;
    public Connection       conn;

    public Database() throws SQLException {
        Database.instance = this;
        final String path = MessageFormat.format("jdbc:sqlite:{0}/{1}", Main.getInstance().getDataFolder()
                .getAbsolutePath(), Main.getInstance().getName() + ".db");
        conn = DriverManager.getConnection(path);
    }

    private void addConfig(String config, String value) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("INSERT INTO `configs` (`config`, `value`) VALUES (?, ?)");
        stmt.setString(1, config);
        stmt.setString(2, value);
        stmt.execute();
        stmt.close();
    }

    private void changeConfig(String config, String value) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("UPDATE `configs` SET value = ? WHERE config = ?");
        stmt.setString(1, value);
        stmt.setString(2, config);
        stmt.execute();
        stmt.close();
    }

    public void createTables() throws SQLException {
        final Statement cur = conn.createStatement();
        cur.execute("CREATE TABLE `prizes` (`rowid` INTEGER PRIMARY KEY AUTOINCREMENT, `added_by` VARCHAR(20) NOT NULL, `prize_class` INT NOT NULL, `prob` INT NOT NULL, `item_type` VARCHAR(32) NOT NULL, `enchantment` TEXT NULL, `display_name` TEXT NULL, `meta` TEXT NULL, `amount` INT NOT NULL, `timestamp` INT NOT NULL)");
        cur.execute("CREATE TABLE `configs` (`config` VARCHAR(128) PRIMARY KEY, `value` VARCHAR(128) NULL)");
        cur.close();
        addConfig("database_version", String.valueOf(Database.latestVersion));
    }

    private boolean areTablesExist() {
        try {
            final Statement cur = conn.createStatement();
            cur.execute("SELECT * FROM `prizes` LIMIT 0");
            cur.execute("SELECT * FROM `configs` LIMIT 0");
            cur.close();
            return true;
        } catch (final SQLException ex) {
            return false;
        }
    }

    private int getCurrentVersion() throws SQLException {
        final PreparedStatement stmt = conn
                .prepareStatement("SELECT value FROM `configs` WHERE config = 'database_version'");
        final int dbVersion = stmt.executeQuery().getInt(1);
        stmt.close();
        return dbVersion;
    }

    @SuppressWarnings("unchecked")
    private void databaseMigrateFrom(int version) throws SQLException {
        switch (version) {
            case 1:
                final PreparedStatement altStmt = conn.prepareStatement("ALTER TABLE `prizes` RENAME TO `prizes_old`");
                altStmt.execute();
                altStmt.close();

                final Statement cur = conn.createStatement();
                cur.execute("CREATE TABLE `prizes` (`rowid` INTEGER PRIMARY KEY AUTOINCREMENT, `added_by` VARCHAR(20) NOT NULL, `prize_class` INT NOT NULL, `prob` INT NOT NULL, `item_type` VARCHAR(32) NOT NULL, `enchantment` TEXT NULL, `display_name` TEXT NULL, `meta` TEXT NULL, `amount` INT NOT NULL, `timestamp` INT NOT NULL)");
                cur.close();

                final PreparedStatement selStmt = Database.getInstance().conn
                        .prepareStatement("SELECT * FROM `prizes_old`");
                ResultSet result;
                result = selStmt.executeQuery();
                PreparedStatement insStmt = null;
                while (result.next()) {
                    try {
                        final Map<String, String> prizeLoresMap = (Map<String, String>) JSONValue.parse(result
                                .getString("lore"));
                        final ItemStack tempItem = new ItemStack(Material.SAND);
                        final ItemMeta itemMeta = tempItem.getItemMeta();
                        final List<String> itemLores = new ArrayList<String>();

                        for (int i = 0; i < prizeLoresMap.size(); i++) {
                            itemLores.add(prizeLoresMap.get(String.valueOf(i)));
                        }

                        itemMeta.setLore(itemLores);
                        final JSONObject metaJson = new JSONObject();
                        metaJson.putAll(ItemUtils.serialize(itemMeta));
                        insStmt = Database.getInstance().conn
                                .prepareStatement("INSERT INTO `prizes` (`added_by`, `prize_class`, `prob`, `item_type`, `enchantment`, `display_name`, `meta`, `amount`, `timestamp`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        insStmt.setString(1, result.getString(2));
                        insStmt.setInt(2, result.getInt(3));
                        insStmt.setInt(3, result.getInt(4));
                        insStmt.setString(4, result.getString(5));
                        insStmt.setString(5, result.getString(6));
                        insStmt.setString(6, result.getString(7));
                        insStmt.setString(7, metaJson.toJSONString());
                        insStmt.setInt(8, result.getInt(9));
                        insStmt.setInt(9, result.getInt(10));
                        insStmt.execute();
                    } catch (final NullPointerException ex) {
                        continue;
                    }
                }
                if (insStmt != null) {
                    insStmt.close();
                }
                result.close();
                selStmt.close();
                final PreparedStatement dropStmt = conn.prepareStatement("DROP TABLE `prizes_old`");
                dropStmt.execute();
                dropStmt.close();
                changeConfig("database_version", "2");
        }
    }

    public void check() throws SQLException {
        Main.log.info(Lang.TITLE.toString() + "Checking database...");
        if (!areTablesExist()) {
            Main.log.info(Lang.TITLE.toString() + "Creating tables...");
            createTables();
            Main.log.info(Lang.TITLE.toString() + "Done.");
        } else if ((currentVersion = getCurrentVersion()) < Database.latestVersion) {
            Main.log.info(Lang.TITLE.toString() + "Migrating database...");
            databaseMigrateFrom(currentVersion);
            Main.log.info(Lang.TITLE.toString() + "Done.");
        }
    }

    public static Database getInstance() {
        return Database.instance;
    }

}