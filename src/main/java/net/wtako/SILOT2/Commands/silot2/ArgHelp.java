package net.wtako.SILOT2.Commands.silot2;

import net.wtako.SILOT2.Main;
import net.wtako.SILOT2.Utils.Lang;

import org.bukkit.command.CommandSender;

public class ArgHelp {

    public ArgHelp(CommandSender sender) {
        sender.sendMessage(Main.getInstance().getName() + " v"
                + Main.getInstance().getProperty("version"));
        sender.sendMessage("Author: " + Main.getInstance().getProperty("author"));
        sender.sendMessage(Lang.HELP_ADD.toString());
        sender.sendMessage(Lang.HELP_LIST.toString());
        sender.sendMessage(Lang.HELP_MAKE.toString());
        sender.sendMessage(Lang.HELP_DELETE.toString());
        sender.sendMessage(Lang.HELP_RELOAD.toString());
    }

}
