package net.icelane.massband.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import net.icelane.massband.core.Massband;

public class InventoryEvents implements Listener {

	private static InventoryEvents eventHandler = new InventoryEvents();

	public static InventoryEvents getListener(){
		return eventHandler;
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		if (!(event.getPlayer() instanceof Player)) return;
		
		Massband obj = Massband.get((Player) event.getPlayer());
		if (obj != null) obj.getInteract().inventoryClose(event);
	}
}
