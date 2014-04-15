package net.wtako.SILOT2.Commands.silot2;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.wtako.SILOT2.Main;
import net.wtako.SILOT2.Methods.PrizesDatabase;
import net.wtako.SILOT2.Utils.Lang;

import org.bukkit.command.CommandSender;

public class ArgDelete {

    public ArgDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Main.getInstance().getProperty("artifactId")+".admin")) {
            sender.sendMessage(Lang.NO_PERMISSION_COMMAND.toString());
            return;
        }
        if (args.length >= 2) {
            final List<String> rowsToRemove = new ArrayList<String>(Arrays.asList(args));
            rowsToRemove.remove(0);
            for (final String row: rowsToRemove) {
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
        } else {
            sender.sendMessage(Lang.HELP_DELETE.toString());
        }
    }

}
