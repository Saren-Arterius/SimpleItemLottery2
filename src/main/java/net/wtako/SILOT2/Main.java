package net.wtako.SILOT2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.wtako.SILOT2.Commands.CommandSILOT2;
import net.wtako.SILOT2.EventHandlers.ItemUseListener;
import net.wtako.SILOT2.Methods.Database;
import net.wtako.SILOT2.Utils.Config;
import net.wtako.SILOT2.Utils.Lang;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main             instance;
    public static Economy           econ;
    public static String            artifactId;
    public static YamlConfiguration LANG;
    public static File              LANG_FILE;
    public static Logger            log = Logger.getLogger("SILOT2");

    @Override
    public void onEnable() {
        Main.instance = this;
        Main.artifactId = getProperty("artifactId");
        getCommand(getProperty("mainCommand")).setExecutor(new CommandSILOT2());
        Config.saveAll();
        loadLang();
        setupEcon();
        if (Database.getInstance() == null) {
            try {
                new Database();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        getServer().getPluginManager().registerEvents(new ItemUseListener(), this);
    }

    @Override
    public void onDisable() {
        if (Database.getInstance() != null) {
            try {
                Database.reset();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void setupEcon() {
        final RegisteredServiceProvider<Economy> economyProvider = Main.getInstance().getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            Main.econ = economyProvider.getProvider();
        }
    }

    public void loadLang() {
        final File lang = new File(getDataFolder(), "messages.yml");
        if (!lang.exists()) {
            try {
                getDataFolder().mkdir();
                lang.createNewFile();
                final InputStream defConfigStream = getResource("messages.yml");
                if (defConfigStream != null) {
                    final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                    defConfig.save(lang);
                    Lang.setFile(defConfig);
                    return;
                }
            } catch (final IOException e) {
                e.printStackTrace(); // So they notice
                Main.log.severe("[" + Main.getInstance().getName() + "] Couldn't create language file.");
                Main.log.severe("[" + Main.getInstance().getName() + "] This is a fatal error. Now disabling");
                setEnabled(false); // Without it loaded, we can't send them
                // messages
            }
        }
        final YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
        for (final Lang item: Lang.values()) {
            if (conf.getString(item.getPath()) == null) {
                conf.set(item.getPath(), item.getDefault());
            }
        }
        Lang.setFile(conf);
        Main.LANG = conf;
        Main.LANG_FILE = lang;
        try {
            conf.save(getLangFile());
        } catch (final IOException e) {
            Main.log.log(Level.WARNING, "[" + Main.getInstance().getName() + "] Failed to save messages.yml.");
            Main.log.log(Level.WARNING, "[" + Main.getInstance().getName() + "] Report this stack trace to "
                    + getProperty("author") + ".");
            e.printStackTrace();
        }
    }

    /**
     * Gets the messages.yml config.
     * 
     * @return The messages.yml config.
     */
    public YamlConfiguration getLang() {
        return Main.LANG;
    }

    /**
     * Get the messages.yml file.
     * 
     * @return The messages.yml file.
     */
    public File getLangFile() {
        return Main.LANG_FILE;
    }

    public String getProperty(String key) {
        final YamlConfiguration spawnConfig = YamlConfiguration.loadConfiguration(getResource("plugin.yml"));
        return spawnConfig.getString(key);
    }

    public static Main getInstance() {
        return Main.instance;
    }

}
