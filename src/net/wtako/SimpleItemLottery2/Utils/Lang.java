package net.wtako.SimpleItemLottery2.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * An enum for requesting strings from the language file.
 * 
 * @author gomeow
 */
public enum Lang {

    TITLE("title", "[SILOT2]"),

    LIST_FORMAT1("list-format1", "{0}. {1} - {2} x <{3}>({4})"),
    LIST_FORMAT2("list-format2", "Class: {1}, Prob: {0}, Time: {2}"),
    PAGE_NUMBER("page-number", "Page {0}/{1}"),
    MONEY_SIGN("money-sign", "$"),
    LOTTERY_TICKET("lottery-ticket", "Lottery ticket"),
    LOTTERY_TICKET_DISPLAY_NAME("lottery-ticket-display-name", "Class {0} lottery ticket"),
    LOTTERY_TICKET_CLASS("lottery-ticket-class", "This is a class {0} lottery ticket."),
    LOTTERY_TICKET_USAGE("lottery-ticket-usage", "Right click on it to draw."),
    
    YOU_WON_THIS("you-won-this", "You won {0} x <{1}>({2})!"),

    ADD_SUCCESS("add-success", "Successfully added prize."),
    MAKE_SUCCESS("make-success", "Successfully made ticket(s)."),
    DELETE_SUCCESS("delete-success", "Successfully deleted row {0}."),
    MULTIPLE_DELETE_SUCCESS("multiple-delete-success", "Successfully deleted all valid rows."),

    NO_PRIZE("no-prize", "&eCurrently no prize is added to database."),
    NO_PRIZE_FROM_TICKET("no-prize-from-ticket", "There is still no prize for this class of ticket yet."),
    CANNOT_FIND_ROW("cannot-find-row", "&cCould not find row ID {0}."),
    CANNOT_ADD_AIR("cannot-add-air", "&cYou cant add air as prize!"),
    MONEY_ERROR("money-error", "&cCash prize's amount must be greater than 1."),

    HELP_ADD(
            "help-add",
            "Hold an item on hand, and type /silot2 add <prize class> <probabilty> [money amount] to add it into database. If money amount is givin, cash prize will be added instead."),
    HELP_LIST("help-list", "Type /silot2 list [prize class] [page] to view prize list."),
    HELP_MAKE("help-make", "Type /silot2 make <prize class> [amount] to make lottery tickets."),
    HELP_DELETE("help-delete", "Type /silot2 delete (id 1, id 2, id 3...) to delete prize rows."),
    HELP_RELOAD("help-reload", "Type /silot2 reload to reload this plugin."),

    PLUGIN_RELOADED("plugin-reloaded", "&aPlugin reloaded."),
    DB_EXCEPTION("db-exception", "&4A database error occured! Please contact server administrators."),
    NO_PERMISSION_COMMAND("no-permission-command", "&cYou are not allowed to use this command.");

    private String                   path;
    private String                   def;
    private static YamlConfiguration LANG;

    /**
     * Lang enum constructor.
     * 
     * @param path
     *            The string path.
     * @param start
     *            The default string.
     */
    Lang(String path, String start) {
        this.path = path;
        def = start;
    }

    /**
     * Set the {@code YamlConfiguration} to use.
     * 
     * @param config
     *            The config to set.
     */
    public static void setFile(YamlConfiguration config) {
        Lang.LANG = config;
    }

    @Override
    public String toString() {
        if (this == TITLE) {
            return ChatColor.translateAlternateColorCodes('&', Lang.LANG.getString(path, def)) + " ";
        }
        return ChatColor.translateAlternateColorCodes('&', Lang.LANG.getString(path, def));
    }

    /**
     * Get the default value of the path.
     * 
     * @return The default value of the path.
     */
    public String getDefault() {
        return def;
    }

    /**
     * Get the path to the string.
     * 
     * @return The path to the string.
     */
    public String getPath() {
        return path;
    }
}