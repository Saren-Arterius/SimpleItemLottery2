package net.wtako.SimpleItemLottery2.Commands.silot2;

import java.sql.SQLException;

import net.wtako.SimpleItemLottery2.Methods.PrizesDatabase;
import net.wtako.SimpleItemLottery2.Utils.Lang;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgAdd {

    public ArgAdd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("SILOT2.admin")) {
            sender.sendMessage(Lang.NO_PERMISSION_COMMAND.toString());
            return;
        }
        if (args.length >= 4) {
            try {
                if (PrizesDatabase.addCashPrize(Integer.parseInt(args[3]), Integer.parseInt(args[1]),
                        Integer.parseInt(args[2]), (Player) sender)) {
                    sender.sendMessage(Lang.ADD_SUCCESS.toString());
                }
            } catch (final NumberFormatException e) {
                sender.sendMessage(Lang.HELP_ADD.toString());
            } catch (final SQLException e) {
                sender.sendMessage(Lang.DB_EXCEPTION.toString());
                e.printStackTrace();
            }
        } else if (args.length == 3) {
            try {
                if (PrizesDatabase.addItem(((Player) sender), Integer.parseInt(args[1]), Integer.parseInt(args[2]))) {
                    sender.sendMessage(Lang.ADD_SUCCESS.toString());
                }
            } catch (final NumberFormatException e) {
                sender.sendMessage(Lang.HELP_ADD.toString());
            } catch (final SQLException e) {
                sender.sendMessage(Lang.DB_EXCEPTION.toString());
                e.printStackTrace();
            }
        } else {
            sender.sendMessage(Lang.HELP_ADD.toString());
        }
    }

}
