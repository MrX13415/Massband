package net.icelane.massband.core;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.icelane.massband.config.configs.Config;
import net.icelane.massband.core.Markers.BlockAxis;
import net.icelane.massband.core.Markers.MarkerSettings;
import net.icelane.massband.core.Markers.MeasureMode;

public class Interact {

	private Massband massband;
	
	private Material material     = (Material) Config.interact_material.get();
	private boolean preventAction = Config.interact_preventAction.get();
	private boolean switchButtons = Config.interact_switchbuttons.get();
	
	private long lastInteractTime = System.nanoTime();
	private long doubleClickDelta = Config.interact_doubleClickTimeFrame.get(); //ms 
	
	
	public Interact(Massband obj) {
		this.massband = obj;
	}
	
	public void interact(PlayerInteractEvent event){
		Markers markers = massband.getMarkers(event.getPlayer().getWorld());
		Block block    = event.getClickedBlock();
		BlockFace face = event.getBlockFace();
		ItemStack item = event.getItem();
		Action action  = event.getAction();
		
		long time  = System.nanoTime();
		long delta = (time - lastInteractTime) / 1000000; //ms
		lastInteractTime = time;
		boolean doubleclick = (delta > 0 && delta <= doubleClickDelta);
		
		if (block == null) return;
		if (face == null) return;
		if (item == null) return;
		if (item.getType() != this.material) return;
		
		// is there already a marker?
		int index = markers.indexOf(block.getLocation());
		
		Action right = Action.RIGHT_CLICK_BLOCK;
		Action left = Action.LEFT_CLICK_BLOCK;
		
		if (switchButtons){
			left = Action.RIGHT_CLICK_BLOCK;
			right = Action.LEFT_CLICK_BLOCK;
		}
		
		if (action == right && doubleclick){
			//remove all markers 
			markers.removeAll();
			index = -1;
		}
		
		if (action == right){
			//remove marker 
			if (index > -1){
				markers.remove(index);
				markers.recalculate();
				return;
			}
	
			// add new marker
			markers.add(block, face);
		}
		
		if (action == left && doubleclick){
			//switch measuring mode on start marker
			if (index == -1){
				switch (markers.getMode()) {
				case BLOCKS: markers.setMode(MeasureMode.VECTORS); break;
				case VECTORS: markers.setMode(MeasureMode.BLOCKS); break;
				default: break;
				}
				markers.recalculate();
			}
		}
		
		if (action == left && !doubleclick){
			if (index == 0){
				switch (markers.getIgnoredAxis()) {
				case None: markers.setIgnoredAxis(BlockAxis.X);  break;
				case X: markers.setIgnoredAxis(BlockAxis.Y); break;
				case Y: markers.setIgnoredAxis(BlockAxis.Z); break;
				case Z: markers.setIgnoredAxis(BlockAxis.None); break;
				default: break;
				}
				markers.recalculate();
			}
			
			//switch states on markers
			if (index > 0 && markers.getMode() == MeasureMode.BLOCKS){
				MarkerSettings settings = markers.getSettings(index);
				boolean auto = settings.isAutoAxis();

				if (auto) settings.setAutoAxis(false); 
				
				switch (settings.getAxis()) {
				case X: settings.setAxis(BlockAxis.Y);  break;
				case Y: settings.setAxis(BlockAxis.Z);  break;
				case Z: settings.setAxis(BlockAxis.X); break;
				default: break;
				}

				settings.setAutoAxis(settings.getLastAutoaxis() == settings.getAxis());
				markers.recalculate();
			}
		}

		if (preventAction) event.setCancelled(true);
	}
	
	public void blockBreak(BlockBreakEvent event){
		PlayerInventory inventory = event.getPlayer().getInventory();
		
		if (inventory.getItemInMainHand().getType() == this.material
				|| inventory.getItemInOffHand().getType() == this.material){
			event.setCancelled(true);
		}
	}
	
	public void itemChange(PlayerItemHeldEvent event) {
		Markers markers = massband.getMarkers(event.getPlayer().getWorld());
		
		ItemStack newItem = event.getPlayer().getInventory().getItem(event.getNewSlot());
		
		if (newItem == null || newItem.getType() != Material.STICK){
			markers.hideAll();
		}else{
			markers.showAll();
		}
	}

	public Material getMaterial() {
		return material;
	}

}
