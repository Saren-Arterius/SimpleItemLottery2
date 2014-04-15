package net.wtako.SILOT2.Commands.silot2;

import net.wtako.SILOT2.Utils.Lang;

import org.bukkit.command.CommandSender;

public class ArgHelp {

    public ArgHelp(CommandSender sender) {
        sender.sendMessage("SimpleItemLottery2 v0.0.1");
        sender.sendMessage("Author: Saren");
        sender.sendMessage(Lang.HELP_ADD.toString());
        sender.sendMessage(Lang.HELP_LIST.toString());
        sender.sendMessage(Lang.HELP_MAKE.toString());
        sender.sendMessage(Lang.HELP_DELETE.toString());
        sender.sendMessage(Lang.HELP_RELOAD.toString());
    }

}
