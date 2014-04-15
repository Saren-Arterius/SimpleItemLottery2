package net.wtako.SILOT2.Commands.silot2;

import java.sql.SQLException;

import net.wtako.SILOT2.Methods.PrizesDatabase;
import net.wtako.SILOT2.Utils.Lang;

import org.bukkit.command.CommandSender;

public class ArgList {

    public ArgList(CommandSender sender, String[] args) {
        if (!sender.hasPermission("SILOT2.list")) {
            sender.sendMessage(Lang.NO_PERMISSION_COMMAND.toString());
            return;
        }
        if (args.length >= 3) {
            Integer page;
            try {
                page = Integer.parseInt(args[2]);
                if (page <= 0) {
                    page = 1;
                }
                sender.sendMessage(PrizesDatabase.listAllItems(Integer.parseInt(args[1]), page));
            } catch (final NumberFormatException e) {
                sender.sendMessage(Lang.HELP_LIST.toString());
            } catch (final SQLException e) {
                sender.sendMessage(Lang.DB_EXCEPTION.toString());
                e.printStackTrace();
            }
        } else if (args.length == 2) {
            try {
                sender.sendMessage(PrizesDatabase.listAllItems(Integer.parseInt(args[1]), 1));
            } catch (final NumberFormatException e) {
                sender.sendMessage(Lang.HELP_LIST.toString());
            } catch (final SQLException e) {
                sender.sendMessage(Lang.DB_EXCEPTION.toString());
                e.printStackTrace();
            }
        } else {
            try {
                sender.sendMessage(PrizesDatabase.listAllItems(null, 1));
            } catch (final SQLException e) {
                sender.sendMessage(Lang.DB_EXCEPTION.toString());
                e.printStackTrace();
            }
        }
    }

}
