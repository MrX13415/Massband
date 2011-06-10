package de.MrX13415.Massband;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class MassbandPlayerListener extends org.bukkit.event.player.PlayerListener {
        
    public void onPlayerInteract(PlayerInteractEvent event){
    	if ((event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
    		Player player = event.getPlayer();
    		Block block = event.getClickedBlock();
    	
    		if (Massband.permissionHandler != null) {
    			//use Permission
    			if (Massband.permissionHandler.has(player, Massband.PERMISSION_NODE_Massband_use)) {
        			playerInteract(player, block);
    			}
			}else{
				//no Permission installed ! (everyone has access)
				playerInteract(player, block);
			}
    	}
    }
    
    private void playerInteract(Player player, Block block){
    	//define tool that is used for Massband from the ID in the config-file.
    	ItemStack item = new ItemStack(Massband.configFile.itemID, player.getItemInHand().getAmount());
    	
    	//Player holds Item ...
    	if (player.getItemInHand().equals(item)) {
    		//get Vars for the current Player or create it.
			PlayerVars tmpVars = Massband.getPlayerVars(player);
		
			//mode ?
			if (tmpVars.getMode() == PlayerVars.MODE_LENGTH) {
				onModeLength(tmpVars, player, block);
			}else if(tmpVars.getMode() == PlayerVars.MODE_SURFACE){
				onModeSurface(tmpVars, player, block);
			}
		}
    }
    
    public void onModeLength(PlayerVars tmpVars, Player player, Block block){
		tmpVars.addPoint(block.getX(), block.getY(), block.getZ());
		
		printPoints(tmpVars, player, block);
		
		if (tmpVars.getWayPointListSize() >= 2) {
			tmpVars.computingVectors();
			MassbandCommandExecuter.onCommandLength(tmpVars, player); //output
		}
    }
    
    public void onModeSurface(PlayerVars tmpVars, Player player, Block block){
		tmpVars.addPoint(block.getX(), block.getY(), block.getZ());
		if (tmpVars.getWayPointListSize() >= 3) {
			tmpVars.removeAllWayPoints();	//clear all Points
			tmpVars.addPoint(block.getX(), block.getY(), block.getZ());
		}
		
		printPoints(tmpVars, player, block);
		
		if (tmpVars.getWayPointListSize() == 2) {
			tmpVars.calculateDiminsion();
			MassbandCommandExecuter.onCommandDimensions(tmpVars, player);	//output
		}
    }
    
    /** send a Message to the Player, which conntains the current Points
     * 
     * @param tmpVars
     * @param player
     * @param block
     */
    private void printPoints(PlayerVars tmpVars, Player player,  Block block){
    	int size = tmpVars.getWayPointListSize();
    	
    	if (size == 1){ 
    		if (tmpVars.getMode() == PlayerVars.MODE_LENGTH) {
    			player.sendMessage(ChatColor.GRAY + "- Length-mode --------------------------------------");
			}else if(tmpVars.getMode() == PlayerVars.MODE_SURFACE){
				player.sendMessage(ChatColor.GRAY + "- Surface-mode -------------------------------------");
			}
    	}
    	
    	if (tmpVars.getignoreHeight()) {
			player.sendMessage("Point #" + ChatColor.GRAY + size + ChatColor.WHITE + ": " +
					ChatColor.RED + block.getX() + ChatColor.WHITE + "," + ChatColor.RED + block.getY() + ChatColor.WHITE + "," + 
					ChatColor.RED + block.getZ() + ChatColor.WHITE + " mode: " + ChatColor.GRAY + " 2D");
		}else{
			player.sendMessage("Point #" + ChatColor.GRAY + size + ChatColor.WHITE + ": " +
					ChatColor.RED + block.getX() + ChatColor.WHITE + "," + ChatColor.RED + block.getY() + ChatColor.WHITE + "," + 
					ChatColor.RED + block.getZ() + ChatColor.WHITE + " mode: " + ChatColor.GRAY + " 3D");
		}
    }
   
    public void onPlayerQuit(PlayerQuitEvent event) {
    	Massband.removePlayer(event.getPlayer());
	}

}