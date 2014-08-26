package net.wtako.SILOT2.Commands.silot2;

import java.text.MessageFormat;

import net.wtako.SILOT2.Main;
import net.wtako.SILOT2.Methods.Tickets;
import net.wtako.SILOT2.Utils.CommandHelper;
import net.wtako.SILOT2.Utils.Config;
import net.wtako.SILOT2.Utils.Lang;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArgMake {

    @SuppressWarnings("deprecation")
    public ArgMake(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(MessageFormat.format(Lang.HELP_MAKE.toString(), CommandHelper.joinArgsInUse(args, 1)));
            return;
        }

        int prizeClass;
        try {
            prizeClass = Integer.parseInt(args[1]);
        } catch (final NumberFormatException e) {
            sender.sendMessage(MessageFormat.format(Lang.HELP_MAKE.toString(), CommandHelper.joinArgsInUse(args, 1)));
            return;
        }
        if (prizeClass < 0 || prizeClass > Config.MAX_CLASS.getInt()) {
            sender.sendMessage(MessageFormat.format(Lang.VALUE_ERROR.toString(), "prize class",
                    Config.MAX_CLASS.getInt()));
            return;
        }

        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (final NumberFormatException e) {
                sender.sendMessage(MessageFormat.format(Lang.HELP_MAKE.toString(), CommandHelper.joinArgsInUse(args, 1)));
                return;
            }
        }
        amount = amount < 1 ? 1 : amount > 64 ? 64 : amount;

        Player reciever;
        if (args.length >= 4) {
            reciever = Main.getInstance().getServer().getPlayer(args[3]);
            if (reciever == null) {
                sender.sendMessage(MessageFormat.format(Lang.PLAYER_NOT_FOUND.toString(), args[3]));
                return;
            }
            sender.sendMessage(MessageFormat.format(Lang.MAKE_SUCCESS_TO_PLAYER.toString(), reciever.getName()));
        } else {
            reciever = ((Player) sender);
            sender.sendMessage(Lang.MAKE_SUCCESS.toString());
        }

        final ItemStack ticket = Tickets.newTicket(prizeClass, prizeClass);
        if (ticket != null) {
            reciever.getInventory().addItem(ticket);
        } else {
            sender.sendMessage("Ticket item is null.");
        }

    }

}
