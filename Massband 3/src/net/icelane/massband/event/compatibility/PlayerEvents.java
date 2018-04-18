package net.icelane.massband.event.compatibility;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import net.icelane.massband.core.Massband;

/**
 * For compatibility with 1.10 - 1.11
 */
public class PlayerEvents extends net.icelane.massband.event.PlayerEvents {

	/**
	 * Called when a player picks-up an item.
	 * <p>
	 * For compatibility with 1.10 - 1.11
	 * @param event
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerPickupItem(org.bukkit.event.player.PlayerPickupItemEvent event){
		if (!Massband.canUse(event.getPlayer())) return; // no permission
		
		Massband obj = Massband.get(event.getPlayer());
		if (obj != null) obj.getInteract().itemPickup((Player) event.getPlayer(), event.getItem().getItemStack());
	}
}
