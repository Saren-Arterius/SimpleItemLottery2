package net.wtako.SimpleItemLottery2.Commands.silot2;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.wtako.SimpleItemLottery2.Methods.PrizesDatabase;
import net.wtako.SimpleItemLottery2.Utils.Lang;

import org.bukkit.command.CommandSender;

public class ArgDelete {

    public ArgDelete(CommandSender sender, String[] args) {
        if (args.length > 2) {
            final List<String> rowsToRemove = new ArrayList<String>(Arrays.asList(args));
            rowsToRemove.remove(0);
            for (String row: rowsToRemove) {
                try {
                    PrizesDatabase.deleteItem(Integer.parseInt(row));
                } catch (final NumberFormatException e) {
                    // Nobody really cares.
                } catch (final SQLException e) {
                    sender.sendMessage(Lang.DB_EXCEPTION.toString());
                    e.printStackTrace();
                    return;
                }
            }
            sender.sendMessage(Lang.MULTIPLE_DELETE_SUCCESS.toString());
        } else if (args.length == 2) {
            try {
                if (PrizesDatabase.deleteItem(Integer.parseInt(args[1]))) {
                    sender.sendMessage(MessageFormat.format(Lang.DELETE_SUCCESS.toString(), args[1]));
                } else {
                    sender.sendMessage(MessageFormat.format(Lang.CANNOT_FIND_ROW.toString(), args[1]));
                }
            } catch (final NumberFormatException e) {
                sender.sendMessage(Lang.HELP_DELETE.toString());
            } catch (final SQLException e) {
                sender.sendMessage(Lang.DB_EXCEPTION.toString());
                e.printStackTrace();
            }
        } else {
            sender.sendMessage(Lang.HELP_DELETE.toString());
        }
    }

}
