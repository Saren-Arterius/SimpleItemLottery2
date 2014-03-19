package net.wtako.SimpleItemLottery2.EventHandlers;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.wtako.SimpleItemLottery2.Methods.PrizesDatabase;
import net.wtako.SimpleItemLottery2.Utils.Lang;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemUseListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return;
        }
        final String IDRow = item.getItemMeta().getLore().get(1);
        final String regex = "^Class: (\\d+)$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(IDRow);
        matcher.find();
        try {
            Integer prizeClass = Integer.parseInt(matcher.group(1));
            Integer randomRow = PrizesDatabase.getRandomRowID(prizeClass);
            if (randomRow == null) {
                player.sendMessage(Lang.NO_PRIZE_FROM_TICKET.toString());
                return;
            }
            player.sendMessage(randomRow.toString());
        } catch (SQLException e) {
            player.sendMessage(Lang.DB_EXCEPTION.toString());
            e.printStackTrace();
            return;
        } catch (IllegalStateException e) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}
