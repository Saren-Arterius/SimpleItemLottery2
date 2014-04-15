package net.wtako.SILOT2.Commands;

import net.wtako.SILOT2.Commands.silot2.ArgAdd;
import net.wtako.SILOT2.Commands.silot2.ArgDelete;
import net.wtako.SILOT2.Commands.silot2.ArgHelp;
import net.wtako.SILOT2.Commands.silot2.ArgList;
import net.wtako.SILOT2.Commands.silot2.ArgMake;
import net.wtako.SILOT2.Commands.silot2.ArgReload;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandSilot2 implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                new ArgReload(sender);
                return true;
            } else if (args[0].equalsIgnoreCase("help")) {
                new ArgHelp(sender);
                return true;
            } else if (args[0].equalsIgnoreCase("add")) {
                new ArgAdd(sender, args);
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                new ArgList(sender, args);
                return true;
            } else if (args[0].equalsIgnoreCase("delete")) {
                new ArgDelete(sender, args);
                return true;
            } else if (args[0].equalsIgnoreCase("make")) {
                new ArgMake(sender, args);
                return true;
            }
        }
        return false;
    }
}
