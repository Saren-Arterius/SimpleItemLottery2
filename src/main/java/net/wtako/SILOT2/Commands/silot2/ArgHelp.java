package net.wtako.SILOT2.Commands.silot2;

import net.wtako.SILOT2.Utils.CommandHelper;
import net.wtako.SILOT2.Utils.CommandsSILOT2;

import org.bukkit.command.CommandSender;

public class ArgHelp {

    public ArgHelp(final CommandSender sender, String[] args) {
        CommandHelper.sendHelp(sender, CommandsSILOT2.values(), "");
    }

}
