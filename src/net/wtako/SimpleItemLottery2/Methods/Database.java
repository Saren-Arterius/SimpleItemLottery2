package net.wtako.SimpleItemLottery2.Methods;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

import net.wtako.SimpleItemLottery2.Main;
import net.wtako.SimpleItemLottery2.Utils.Lang;

public class Database {

    private static Database instance;
    public Connection       conn;

    public Database() throws SQLException {
        Database.instance = this;
        final String path = MessageFormat.format("jdbc:sqlite:{0}/{1}", Main.getInstance().getDataFolder()
                .getAbsolutePath(), "SimpleItemLottery2.db");
        conn = DriverManager.getConnection(path);
    }

    private void addConfig(String config, String value) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("INSERT INTO `configs` (`config`, `value`) VALUES (?, ?)");
        stmt.setString(1, config);
        stmt.setString(2, value);
        stmt.execute();
        stmt.close();
    }

    public void createTables() throws SQLException {
        final Statement cur = conn.createStatement();
        cur.execute("CREATE TABLE `prizes` (`rowid` INTEGER PRIMARY KEY AUTOINCREMENT, `added_by` VARCHAR(20) NOT NULL, `prize_class` INT NOT NULL, `prob` INT NOT NULL, `item_type` VARCHAR(32) NOT NULL, `enchantment` TEXT NULL, `display_name` TEXT NULL, `lore` TEXT NULL, `amount` INT NOT NULL, `timestamp` INT NOT NULL)");
        cur.execute("CREATE TABLE `configs` (`config` VARCHAR(128) PRIMARY KEY, `value` VARCHAR(128) NULL)");
        cur.close();
        addConfig("database_version", "1");
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

    public void check() throws SQLException {
        Main.log.info(Lang.TITLE.toString() + "Checking databases...");
        if (!areTablesExist()) {
            Main.log.info(Lang.TITLE.toString() + "Creating databases...");
            createTables();
            Main.log.info(Lang.TITLE.toString() + "Done.");
        }
    }

    public static Database getInstance() {
        return Database.instance;
    }

}