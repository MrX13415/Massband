package net.icelane.massband.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
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
		
		// only fire once
		if (mainHand.getType() == offHand.getType()){
			playerInteract_Handled = true;
		}
		
		Massband obj = Massband.get(event.getPlayer());
		if (obj != null) obj.interact(event);
		//TODO: else error 
	}
	
	@EventHandler 
	public void onPlayerChangedMainHand(PlayerChangedMainHandEvent event) {
		
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Massband obj = Massband.get(event.getPlayer());
		if (obj != null) obj.getInteract().itemDrop(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerItemBreak(PlayerItemBreakEvent event) {
		Massband obj = Massband.get(event.getPlayer());
		if (obj != null) obj.getInteract().itemBreak(event.getPlayer());
	}
		
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		Massband obj = Massband.get(event.getPlayer());
		if (obj != null) obj.getInteract().itemConsume(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
		Massband obj = Massband.get(event.getPlayer());
		if (obj != null) obj.getInteract().swapHandItem(event);
	}

	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event){
		Massband obj = Massband.get(event.getPlayer());
		if (obj != null) obj.getInteract().itemHeld(event);
	}
	
	@EventHandler
	public void onPlayerBlockBreak(BlockBreakEvent event){
		Massband obj = Massband.get(event.getPlayer());
		if (obj != null) obj.getInteract().blockBreak(event);
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
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event){
		Massband obj = Massband.get(event.getPlayer());
		if (obj != null) obj.worldChange(event.getFrom());
	}
	
}
