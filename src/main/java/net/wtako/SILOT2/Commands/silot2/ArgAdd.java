package net.wtako.SILOT2.Commands.silot2;

import java.sql.SQLException;
import java.text.MessageFormat;

import net.wtako.SILOT2.Main;
import net.wtako.SILOT2.Methods.Prizes;
import net.wtako.SILOT2.Utils.CommandHelper;
import net.wtako.SILOT2.Utils.Lang;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ArgAdd {

    public ArgAdd(final CommandSender sender, final String[] args) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (args.length >= 4) {
                    try {
                        if (Prizes.addCashPrize(Integer.parseInt(args[3]), Integer.parseInt(args[1]),
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
                        if (Prizes.addItem(((Player) sender), Integer.parseInt(args[1]), Integer.parseInt(args[2]))) {
                            sender.sendMessage(Lang.ADD_SUCCESS.toString());
                        }
                    } catch (final NumberFormatException e) {
                        sender.sendMessage(Lang.HELP_ADD.toString());
                    } catch (final SQLException e) {
                        sender.sendMessage(Lang.DB_EXCEPTION.toString());
                        e.printStackTrace();
                    }
                } else {
                    sender.sendMessage(MessageFormat.format(Lang.HELP_ADD.toString(),
                            CommandHelper.joinArgsInUse(args, 1)));
                }
            }

        }.runTaskAsynchronously(Main.getInstance());

    }

}
