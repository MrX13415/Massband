package net.icelane.massband.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import net.icelane.massband.core.Massband;

public class EntityEvents implements Listener {

	private static EntityEvents eventHandler = new EntityEvents();

	public static EntityEvents getListener(){
		return eventHandler;
	}
	
	@EventHandler
	public void onEntityPickupItem(EntityPickupItemEvent event){
		if (!(event.getEntity() instanceof Player)) return;
		if (!Massband.canUse((Player) event.getEntity())) return; // no permission

		Massband obj = Massband.get((Player) event.getEntity());
		if (obj != null) obj.getInteract().itemPickup((Player) event.getEntity(), event.getItem().getItemStack());
	}

}
