package net.icelane.massband.core;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import net.icelane.massband.config.configs.Defaults;
import net.icelane.massband.core.Marker.BlockAxis;
import net.icelane.massband.core.Marker.MarkerSettings;
import net.icelane.massband.core.Marker.MeasureMode;

public class Interact {

	private Massband massband;
	
	private Material material     = (Material) Defaults.interact_material.get();
	private boolean preventAction = Defaults.interact_preventAction.get();
	private boolean switchButtons = Defaults.interact_switchbuttons.get();
	private long doubleClickDelta = Defaults.interact_doubleClickTimeFrame.get(); //ms 

	private long lastInteractTime = System.nanoTime();
	private Action lastInteractAction;
	private EquipmentSlot lastInteractSlot;
	private Block lastInteractBlock;
	private BlockFace lastInteractBlockFace;
	
	public Interact(Massband obj) {
		this.massband = obj;
	}
	
	public void interact(PlayerInteractEvent event){
		Marker markers = getMassband().getMarkers(event.getPlayer().getWorld());
		Block block    = event.getClickedBlock();
		BlockFace face = event.getBlockFace();
		ItemStack item = event.getItem();
		Action action  = event.getAction();
		EquipmentSlot slot = event.getHand();
		
		if (block == null) return;
		if (face == null) return;
		if (item == null) return;
		if (item.getType() != this.material) return;
		
		// handle double click ...
		long time  = System.nanoTime();
		long delta = (time - lastInteractTime) / 1000000; //ms
		boolean doubleclick = (delta > 0 && delta <= doubleClickDelta);
		if (lastInteractAction != action) doubleclick = false;
		if (lastInteractSlot != slot) doubleclick = false;
		// must be the same block ...
		if (lastInteractBlock != null &&
				(lastInteractBlock.getX() != block.getX()
				|| lastInteractBlock.getY() != block.getY()
				|| lastInteractBlock.getZ() != block.getZ()))
			doubleclick = false;
		// must be the same face on the block ...
		if (lastInteractBlockFace != null &&
				lastInteractBlockFace.compareTo(face) != 0)
			doubleclick = false;

		lastInteractTime = time;
		lastInteractAction = action;
		lastInteractSlot = slot;
		lastInteractBlock = block;
		
		//DEBUG:
		//Server.logger().info("doubleclick: " + doubleclick + " delta: " + delta);
		//Server.logger().info("item: " + item);
		//Server.logger().info("item: " + event.getHand());
		//Server.logger().info("--------------" );

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
		if (hasItemInMainHand(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
	}
	
	public void inventoryClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player)) return;

		if (hasItemInHand((Player)event.getPlayer()))
			massband.showMarkers(event.getPlayer().getWorld());
		else
			massband.hideMarkers(event.getPlayer().getWorld());
	}
	
	public void swapHandItem(PlayerSwapHandItemsEvent event) {
		if (event.getMainHandItem().getType() == material
			|| event.getOffHandItem().getType() == material) {
			
			massband.showMarkers(event.getPlayer().getWorld());
		}else{
			massband.hideMarkers(event.getPlayer().getWorld());
		}
	}
	
	public void itemHeld(PlayerItemHeldEvent event) {
		ItemStack newHand = event.getPlayer().getInventory().getItem(event.getNewSlot());
		ItemStack offHand = event.getPlayer().getInventory().getItemInOffHand();

		if (isItemHeld(newHand) || isItemHeld(offHand)){
			massband.showMarkers(event.getPlayer().getWorld());
		}else{
			massband.hideMarkers(event.getPlayer().getWorld());
		}
	}

	public void itemPickup(Player player, ItemStack pickupItem) {
		if (player == null) return;
		
		// cancel if item to be picked up is not our "material" ...
		if (pickupItem == null || pickupItem.getType() != material) return;
		
		// cancel if main hand slot is not empty ...
		if (!isItemHeld(player.getInventory().getItemInMainHand(), Material.AIR)) return;
					
		// cancel if there is an empty slot before the current selected slot
		for (int index = 0; index < player.getInventory().getHeldItemSlot(); index++) {
			ItemStack slot = player.getInventory().getItem(index);
			if (slot == null || slot.getType() == Material.AIR) return;
		}
		
		massband.showMarkers(player.getWorld());
	}
	
	public void itemDrop(Player player) {
		if (!hasItemInHand(player))
			massband.hideMarkers(player.getWorld());
	}
	
	public void itemBreak(Player player) {
		if (!hasItemInHand(player))
			massband.hideMarkers(player.getWorld());
	}
		
	public void itemConsume(Player player) {
		if (!hasItemInHand(player))
			massband.hideMarkers(player.getWorld());
	}
	
	public boolean hasItemInMainHand(Player player) {
		return isItemHeld(player.getInventory().getItemInMainHand());
	}
	
	public boolean hasItemInOffHand(Player player) {
		return isItemHeld(player.getInventory().getItemInOffHand());
	}
	
	public boolean hasItemInHand(Player player) {
		return hasItemInMainHand(player) || hasItemInOffHand(player);
	}
		
	public boolean isItemHeld(ItemStack hand) {
		return isItemHeld(hand, material);
	}
	
	public boolean isItemHeld(ItemStack hand, Material material) {
		return hand != null && hand.getType() == material;
	}
	
	public Material getMaterial() {
		return material;
	}

	public Massband getMassband() {
		return massband;
	}

	public boolean isPreventAction() {
		return preventAction;
	}

	public boolean isSwitchButtons() {
		return switchButtons;
	}

	public long getLastInteractTime() {
		return lastInteractTime;
	}

	public long getDoubleClickDelta() {
		return doubleClickDelta;
	}

}
