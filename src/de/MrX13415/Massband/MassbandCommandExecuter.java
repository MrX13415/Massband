package de.MrX13415.Massband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
* Handler for the 'massband' command.
* @author MrX13415
*/
public class MassbandCommandExecuter implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	    if (sender instanceof Player) {
	        Player player = (Player) sender;
	        PlayerVars tmpVars = null;
	        
			for (int playerIndex = 0; playerIndex < Massband.getPlayerListSize(); playerIndex++) {
	    		tmpVars = Massband.getPlayer(playerIndex);
				
	    		if (tmpVars.getPlayer().equals(player)) {	//player found
//	    			player.sendMessage("MB: PLAYER-FOUND: " + player.getName());
					break;
				}
	    	}	
			
			if (args.length <= 0) {
				player.sendMessage(ChatColor.RED + command.getUsage());
			}else{
				
				if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("clr")) {
			    	tmpVars.removeAllWayPoints();
		        	player.sendMessage(ChatColor.RED + "Points-list cleared.");
		        	
				}else if (args[0].equalsIgnoreCase("length") || args[0].equalsIgnoreCase("l")) {
		        	player.sendMessage(ChatColor.WHITE + "Length: " + ChatColor.GOLD + tmpVars.getLenght() + ChatColor.WHITE + " Blocks");

				}else if (args[0].equalsIgnoreCase("switchmode") || args[0].equalsIgnoreCase("sm")) {
					if(tmpVars.getignoreHeight()){
		            	tmpVars.setignoreHeight(false);
		            	player.sendMessage(ChatColor.GRAY + "switch to 3D-Mode (does't ignores the height)");
		        	}else{
		            	tmpVars.setignoreHeight(true);
		            	player.sendMessage(ChatColor.GRAY + "switch to 2D-Mode (ignores the height)");
		        	}
						
				}else{
					player.sendMessage(ChatColor.RED + command.getUsage());
				}
			}
			
			return true;
	    } else {
	        return false;
	    }
	}

}
