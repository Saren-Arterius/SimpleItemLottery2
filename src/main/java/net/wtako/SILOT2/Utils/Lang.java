package net.wtako.SILOT2.Utils;

import net.wtako.SILOT2.Main;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * An enum for requesting strings from the language file.
 * 
 * @author gomeow
 */
public enum Lang {

    TITLE("[" + Main.getInstance().getProperty("artifactId") + "]"),

    TABLE_LIST("&aPrize list:"),
    LIST_FORMAT1("{0}. &e{1} &f- &c{2}&f x <&c{3}&f>({4})"),
    LIST_FORMAT2("&cClass:&f {1}, &eProb:&f {0}, &aTime:&f {2}"),
    PAGE_NUMBER("&aPage: &c{0}&a/{1}"),
    MONEY_SIGN("$"),
    LOTTERY_TICKET("Lottery ticket"),
    LOTTERY_TICKET_DISPLAY_NAME("Class {0} lottery ticket"),
    LOTTERY_TICKET_CLASS("This is a class {0} lottery ticket."),
    LOTTERY_TICKET_USAGE("Right click on it to draw."),
    LOG_FORMAT("[{0}] {1} used a class {2} ticket and won {3}."),

    YOU_WON_THIS("&aYou won &e{0}&a x &e{1}&a!"),
    YOU_WON_THIS_DISPLAYNAME("&aYou won &e{0}&a x <&e{1}&a>({2})!"),
    YOU_WON_MONEY("&aYou won &e${0}&e!"),

    ADD_SUCCESS("&aSuccessfully added prize."),
    MAKE_SUCCESS("&aSuccessfully made ticket(s)."),
    MAKE_SUCCESS_TO_PLAYER("&aSuccessfully made ticket(s) and gave them to {0}."),
    MULTIPLE_DELETE_SUCCESS("&aSuccessfully deleted all valid rows."),

    NO_PRIZE("&eCurrently no prize is added to database."),
    NO_PRIZE_FROM_TICKET("&eThere is still no prize for this class of ticket yet."),
    CANNOT_ADD_AIR("&cYou cant add air as prize!"),
    MONEY_ERROR("&cCash prize's amount must be greater than 1."),
    VALUE_ERROR("&cValue {0} must be less or equal to {1}."),
    PLAYER_NOT_FOUND("&cCannot find player {0}: does not exist or offline."),

    COMMAND_HELP_SEPERATOR("&6 | &a"),
    COMMAND_ARG_IN_USE("&e{0}&a"),
    SUB_COMMAND("Sub-command: &e{0}"),
    HELP_HELP("Type &b/" + Main.getInstance().getProperty("mainCommand") + " &a{0}&f to show help (this message)."),
    HELP_RELOAD("Type &b/" + Main.getInstance().getProperty("mainCommand") + " &a{0}&f to reload the plugin."),
    HELP_ADD("&6Hold an item&f on hand, and type &b/" + Main.getInstance().getProperty("mainCommand")
            + " &a{0}&f <&cprize class&f> <&cprobability&f> [&cmoney amount&f] to add it into database. "
            + "&eIf money amount is given, &ccash prize&e will be added instead."),
    HELP_LIST("Type &b/" + Main.getInstance().getProperty("artifactId").toLowerCase()
            + " &a{0}&f [&cprize class&f] [&cpage&f] to view prize list."),
    HELP_MAKE("Type &b/" + Main.getInstance().getProperty("mainCommand")
            + " &a{0}&f <&cprize class&f> [&camount&f] [&cplayer&f] "
            + "to make lottery tickets [&cand give them to a player&f]."),
    HELP_DELETE("Type &b/" + Main.getInstance().getProperty("artifactId").toLowerCase()
            + " &a{0}&f &f(&cid 1&f, &cid 2&f, &cid 3&f...) to delete prize rows."),
    PLUGIN_RELOADED("&aPlugin reloaded."),
    DB_EXCEPTION("&4A database error occured! Please contact server administrators."),
    ERROR_HOOKING("&4Error in hooking into {0}! Please contact server administrators."),
    UNKNOWN_ERROR("&4Unknown Error! Please contact server administrators."),
    ECON_NOT_SUPPORTED("&eEconomy system is currently not supported set in config.yml"),
    NO_PERMISSION_HELP(" (&cno permission&f)"),
    NO_PERMISSION_COMMAND("&cYou are not allowed to use this command."),
    NO_PERMISSION_DO("&cYou are not allowed to do this."),
    NO_PERMISSION_CLASS("&cYou are not allowed to use lottery tickets of this class.");

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
    Lang(String start) {
        path = name().toLowerCase().replace("_", "-");
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