package net.icelane.massband.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import net.icelane.massband.core.Massband;

public class PlayerEvents implements Listener {

	private static PlayerEvents eventHandler = new PlayerEvents();

	private static boolean playerInteract_Handled;
	
	public static PlayerEvents getListener(){
		return eventHandler;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		
		// fire the interact event only once ...
		if (playerInteract_Handled){
			playerInteract_Handled = false;
			return;
		}
		
		ItemStack mainHand = event.getPlayer().getInventory().getItemInMainHand();
		ItemStack offHand  = event.getPlayer().getInventory().getItemInOffHand();
		
		if (mainHand == null || mainHand.getType() == offHand.getType()){
			playerInteract_Handled = true;
		}
		
		Massband obj = Massband.get(event.getPlayer());
		if (obj != null) obj.interact(event);
		//TODO: else write error
	}
	
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event){
		Massband obj = Massband.get(event.getPlayer());
		if (obj != null) obj.getInteract().itemChange(event);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		Massband obj = Massband.get(event.getPlayer());
		if (obj != null) obj.join(event);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Massband obj = Massband.get(event.getPlayer());
		if (obj != null) obj.quit(event);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		Massband obj = Massband.get(event.getPlayer());
		if (obj != null) obj.move(event);
	}
	
}
