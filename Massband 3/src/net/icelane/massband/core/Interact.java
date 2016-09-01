package net.icelane.massband.core;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import net.icelane.massband.Config;

public class Interact {

	private Massband massband;
	
	private Material material     = Material.valueOf(Config.interact_material.valueStr());
	private boolean preventAction = Boolean.valueOf(Config.interact_preventAction.valueStr());
	private String mousebutton    = "";
	
	public Interact(Massband obj) {
		this.massband  = obj;
	}
	
	public void interact(PlayerInteractEvent event){
		Markers markers = massband.getMarker(event.getPlayer().getWorld());
		Block block    = event.getClickedBlock();
		BlockFace face = event.getBlockFace();
		ItemStack item = event.getItem();
	
		if (block == null) return;
		if (face == null) return;
		if (item == null) return;
		if (item.getType() != this.material) return;
		
		int index = markers.indexOf(block.getLocation());
		if (index > -1){
			markers.remove(index);
			markers.recalculate();
			return;
		}

		markers.add(block, face);

		if (preventAction) event.setCancelled(true);
	}
	
	public void itemChange(PlayerItemHeldEvent event) {
		Markers markers = massband.getMarker(event.getPlayer().getWorld());
		
		ItemStack newItem = event.getPlayer().getInventory().getItem(event.getNewSlot());
		
		if (newItem == null || newItem.getType() != Material.STICK){
			markers.hideAll();
		}else{
			markers.showAll();
		}
	}

}
