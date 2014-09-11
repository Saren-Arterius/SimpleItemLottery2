package net.wtako.SILOT2.EventHandlers;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;

import net.wtako.SILOT2.Main;
import net.wtako.SILOT2.Methods.Prizes;
import net.wtako.SILOT2.Methods.Tickets;
import net.wtako.SILOT2.Utils.Lang;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemUseListener implements Listener {

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                final Integer prizeClass = Tickets.getClassID(event.getItem());
                if (prizeClass == null) {
                    return;
                }
                if (!event.getPlayer().hasPermission(Main.artifactId + ".use")) {
                    event.getPlayer().sendMessage(Lang.NO_PERMISSION_DO.toString());
                    return;
                }
                if (!event.getPlayer().hasPermission(MessageFormat.format(Main.artifactId + ".class.{0}", prizeClass))) {
                    event.getPlayer().sendMessage(Lang.NO_PERMISSION_CLASS.toString());
                    return;
                }
                if (event.getPlayer().getInventory().firstEmpty() == -1) {
                    event.getPlayer().sendMessage("Your bag was full!");
                    return;
                }
                try {
                    final Integer randomRow = Prizes.getRandomRowID(prizeClass);
                    if (randomRow == null) {
                        event.getPlayer().sendMessage(Lang.NO_PRIZE_FROM_TICKET.toString());
                        return;
                    }
                    Tickets.ticketUse(event.getPlayer().getInventory(), event.getItem());
                    if (!Prizes.giveOutItem(event.getPlayer(), randomRow, prizeClass)) {
                        Prizes.giveOutCash(event.getPlayer(), randomRow, prizeClass);
                    }
                } catch (SQLException | IOException e) {
                    event.getPlayer().sendMessage(Lang.DB_EXCEPTION.toString());
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

}
