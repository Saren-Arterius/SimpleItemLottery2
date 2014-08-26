package net.wtako.SILOT2.Commands.silot2;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.wtako.SILOT2.Main;
import net.wtako.SILOT2.Methods.Prizes;
import net.wtako.SILOT2.Utils.CommandHelper;
import net.wtako.SILOT2.Utils.Lang;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class ArgDel {

    public ArgDel(final CommandSender sender, final String[] args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (args.length >= 2) {
                    final List<String> rowsToRemove = new ArrayList<String>(Arrays.asList(args));
                    rowsToRemove.remove(0);
                    for (final String row: rowsToRemove) {
                        try {
                            Prizes.deleteItem(Integer.parseInt(row));
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
                    sender.sendMessage(MessageFormat.format(Lang.HELP_DELETE.toString(),
                            CommandHelper.joinArgsInUse(args, 1)));
                }
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

}
