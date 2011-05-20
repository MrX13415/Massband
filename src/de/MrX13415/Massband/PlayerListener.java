package de.MrX13415.Massband;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener extends org.bukkit.event.player.PlayerListener {
        
    public void onPlayerInteract(PlayerInteractEvent event){
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		Player player = event.getPlayer();
    		Block block = event.getClickedBlock();
    		
    		if (player.getItemInHand().equals(new ItemStack(Massband.configFile.itemID, player.getItemInHand().getAmount()))) {
    	    	
    			for (int playerIndex = 0; playerIndex < Massband.getPlayerListSize(); playerIndex++) {
    	    		PlayerVars tmpVars = Massband.getPlayer(playerIndex);
    	
    				if (tmpVars.getPlayer().equals(event.getPlayer())) {	//player found
//    					player.sendMessage("MB: PLAYER-FOUND: " + player.getName());
    					
    					tmpVars.addVector(block.getX(), block.getY(), block.getZ());
    					
    					if (tmpVars.getignoreHeight()) {
    						player.sendMessage("Point #" + ChatColor.GRAY + tmpVars.getWayPointListSize() + ChatColor.WHITE + ": " +
    								ChatColor.RED + block.getX() + ChatColor.WHITE + "," + ChatColor.RED + block.getY() + ChatColor.WHITE + "," + 
    								ChatColor.RED + block.getZ() + ChatColor.WHITE + " mode: " + ChatColor.GRAY + " 2D");
    					}else{
    						player.sendMessage("Point #" + ChatColor.GRAY + tmpVars.getWayPointListSize() + ChatColor.WHITE + ": " +
    								ChatColor.RED + block.getX() + ChatColor.WHITE + "," + ChatColor.RED + block.getY() + ChatColor.WHITE + "," + 
    								ChatColor.RED + block.getZ() + ChatColor.WHITE + " mode: " + ChatColor.GRAY + " 3D");
    					}
    					
    					if (tmpVars.getWayPointListSize() >= 2) {
    						player.sendMessage(ChatColor.WHITE +  "Length: " + ChatColor.GOLD + tmpVars.computingVectors() + ChatColor.WHITE + " Blocks");
    					}
    					
    					break;
    				}
    	    	}	
    		}
		}
    }
    
    public void onPlayerJoin(PlayerJoinEvent event) {
		Massband.addPlayer(new PlayerVars(event.getPlayer()));
//		player.sendMessage("MB: ADD: " + player.getName() + " COUNT: " + Massband.getPlayerListSize());
	}
    
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
    	for (int playerIndex = 0; playerIndex < Massband.getPlayerListSize(); playerIndex++) {
			if (Massband.getPlayer(playerIndex).getPlayer().equals(player)) {
				Massband.removePlayer(playerIndex);
//				player.sendMessage("MB: REMOVE: " + player.getName() + " COUNT: " +Massband.getPlayerListSize());
				break;
			}
    	}
	}
    
}