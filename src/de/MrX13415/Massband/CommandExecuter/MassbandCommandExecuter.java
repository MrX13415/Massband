package de.MrX13415.Massband.CommandExecuter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.MrX13415.Massband.CountBlocks;
import de.MrX13415.Massband.Massband;
import de.MrX13415.Massband.PlayerVars;

/**
* Handler for the 'massband' command.
* @author MrX13415
*/
public class MassbandCommandExecuter implements CommandExecutor{

	boolean hasPermission_use = false;
	boolean hasPermission_stopall = false;
	boolean hasPermission_countblocks = false;
	boolean hasPermission_blocklist = false;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {		
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (Massband.permissions()) {					
    			//use Permission
				if (Massband.hasPermission(player, Massband.PERMISSION_NODE_Massband_use)) hasPermission_use = true;
				if (Massband.hasPermission(player, Massband.PERMISSION_NODE_Massband_stop_all)) hasPermission_stopall = true;
				if (Massband.hasPermission(player, Massband.PERMISSION_NODE_Massband_countblocks)) hasPermission_countblocks = true;
				if (Massband.hasPermission(player, Massband.PERMISSION_NODE_Massband_blocklist)) hasPermission_blocklist = true;
				
				if (hasPermission_use) command(sender, command, label, args);			
			
//			if (Massband.permissionHandler != null) {	
//								
//				//use Permission
//				if (Massband.permissionHandler.permission(player, Massband.PERMISSION_NODE_Massband_use)) hasPermission_use = true;
//				if (Massband.permissionHandler.permission(player, Massband.PERMISSION_NODE_Massband_stop_all)) hasPermission_stopall = true;
//				if (Massband.permissionHandler.permission(player, Massband.PERMISSION_NODE_Massband_blocklist)) hasPermission_blocklist = true;
//				
//				if (hasPermission_use) command(sender, command, label, args);
			}else{

				if (player.isOp()) {
					hasPermission_countblocks = true;
					hasPermission_blocklist = true;
					hasPermission_use = true;
					hasPermission_stopall = true;
				}
				
				//no Permission installed ! (everyone has access)
				command(sender, command, label, args);
			}
			hasPermission_countblocks = false;
			hasPermission_blocklist = false;
			hasPermission_use = false;
			hasPermission_stopall = false;
			return true;
						
	    }else{
	    	if (args.length > 0) {
		    	if (args[0].equalsIgnoreCase("stopall") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_stopall)) {
		    		CountBlocks.interuptAll(sender);
		    		return true;
		    	}
	    	}	
	    }
		
		return false;
	}
	
	private void command(CommandSender sender, Command command, String label, String[] args){
		Player player = (Player) sender;
        PlayerVars tmpVars = null;
        
        //get current Players PlayerVars ...
        tmpVars = Massband.getPlayerVars(player);
		
		if (args.length <= 0) {
			printHelpMsg(command, sender);

		}else if (tmpVars != null){
			
			if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_MassbandEnable)) {
		    	tmpVars.setEnabled(true);
		    	player.sendMessage(ChatColor.GRAY + "Massband is now " + ChatColor.GOLD + "Enabled" + ChatColor.GRAY + " for you");
		    	
			}else if (args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_MassbandDisable)) {
		    	tmpVars.setEnabled(false);
		    	player.sendMessage(ChatColor.GRAY + "Massband is now " + ChatColor.GOLD + "Disabled"+ ChatColor.GRAY + " for you");
		    	
			}else if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_clear)) {
		    	onCommandClear(tmpVars, player);
		    	
			}else if (args[0].equalsIgnoreCase("length") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_lenght)) {
	        	onCommandLength(tmpVars, player);
	        	
			}else if (args[0].equalsIgnoreCase("3d")) {
				onCommandSwitchMode(tmpVars, player, true);
			
			}else if (args[0].equalsIgnoreCase("2d")) {
				onCommandSwitchMode(tmpVars, player, false);
				
			}else if (args[0].equalsIgnoreCase("dimensions") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_dimensions)) {
				onCommandDimensions(tmpVars, player);
					
			}else if (args[0].equalsIgnoreCase("countblocks") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_countblocks)) {
				if (hasPermission_countblocks){
					onCommandCountBlocks(tmpVars, player);
				}else{
					player.sendMessage(String.format(ChatColor.RED + "You don't have the required Permission (" + ChatColor.GOLD + "%s" + ChatColor.RED + ")", Massband.PERMISSION_NODE_Massband_countblocks));
				}
			}else if (args[0].equalsIgnoreCase("lengthmode") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_lengthmode)) {
				onCommandMode(tmpVars, player, PlayerVars.MODE_LENGTH);
				
			}else if (args[0].equalsIgnoreCase("surfacemode") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_surfacemode)) {
				onCommandMode(tmpVars, player, PlayerVars.MODE_SURFACE);
			
			}else if (args[0].equalsIgnoreCase("simplemode") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_simplemode)) {
				onCommandMode(tmpVars, player, PlayerVars.MODE_SIMPLE);
			
			}else if (args[0].equalsIgnoreCase("expand") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_expand)) {
				if (args.length >= 2 ){
					try {
						int expandSize = 0;
						String direction = args[1];
						
						if (args.length == 3) {
							expandSize = Integer.valueOf(args[1]);
							direction = args[2];
						}
						
						onCommandExpand(tmpVars, player, PlayerVars.MODE_SURFACE, expandSize, direction);
						
					} catch (Exception e) {
						printHelpMsg(command, player);
					}
				}else{
					printHelpMsg(command, player);
				}
			}else if (args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_stop)) {
				if (tmpVars.getBlockCountingThread() != null) {
					tmpVars.getBlockCountingThread().interrupt();
				}else{
					player.sendMessage(ChatColor.RED + " Nothing to Interrupt ...");
				}
			}else if (args[0].equalsIgnoreCase("stopall") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_stopall)) {
				//stopall Permission
				if (hasPermission_stopall) {
					CountBlocks.interuptAll(sender);
				}else{
					player.sendMessage(String.format(ChatColor.RED + "You don't have the required Permission (" + ChatColor.GOLD + "%s" + ChatColor.RED + ")", Massband.PERMISSION_NODE_Massband_stop_all));
				}
			}else if (args[0].equalsIgnoreCase("blocklist") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_blockList)) {
				if (hasPermission_blocklist){
					onCommandBlockList(tmpVars, args);
				}else{
					player.sendMessage(String.format(ChatColor.RED + "You don't have the required Permission (" + ChatColor.GOLD + "%s" + ChatColor.RED + ")", Massband.PERMISSION_NODE_Massband_blocklist));
				}
			}else{
				printHelpMsg(command, player);
			}
		}
	}
	
	private void onCommandBlockList(PlayerVars tmpVars, String[] args){
		int page = 1;
		
		if (args.length == 2) {
			try {
				page = Integer.valueOf(args[1]);
			} catch (Exception e){};
		}
		
		if (page >= 1) tmpVars.printArray(page);
	}
	
	private void onCommandExpand(PlayerVars tmpVars, Player player, int modeSurface, int expandsize, String direction ) {
		if (tmpVars.getWayPointListSize() == 2) {
			
			org.bukkit.util.Vector point1 = tmpVars.getWayPointList().get(0);
			org.bukkit.util.Vector point2 = tmpVars.getWayPointList().get(1);
			
			if (direction.equalsIgnoreCase("up")) {
				point1.setY(point1.getY() + expandsize);
			
				if (point1.getY() > player.getWorld().getMaxHeight()) point1.setY(player.getWorld().getMaxHeight());
				if (point1.getY() < 0) point1.setY(0);
				
				player.sendMessage(ChatColor.GRAY + "Selection expanded ... (" + expandsize + " Blocks up)");
				
			}else if (direction.equalsIgnoreCase("down")) {
				point2.setY(point2.getY() - expandsize);
				
				if (point2.getY() > player.getWorld().getMaxHeight() - 1) point2.setY(player.getWorld().getMaxHeight() - 1);
				if (point2.getY() < 0) point2.setY(0);
				
				player.sendMessage(ChatColor.GRAY + "Selection expanded ... (" + expandsize + " Block down)");
				
				
			}else if (direction.equalsIgnoreCase("vert")) {
				point1.setY(player.getWorld().getMaxHeight());
				point2.setY(0);
			
				if (point1.getY() > (player.getWorld().getMaxHeight() - 1)) point1.setY(player.getWorld().getMaxHeight() - 1);
				if (point2.getY() > (player.getWorld().getMaxHeight() - 1)) point2.setY(player.getWorld().getMaxHeight() - 1);
				if (point1.getY() < 0) point1.setY(0);
				if (point2.getY() < 0) point2.setY(0);
				
				player.sendMessage(ChatColor.GRAY + "Selection expanded ... (bottom - top)");
			}
			

			tmpVars.computingVectors();
			tmpVars.calculateDiminsions();
			MassbandCommandExecuter.onCommandDimensions(tmpVars, player);	//output
		}else{
			player.sendMessage(ChatColor.RED + "Make a Selection first ... (use " + Massband.configFile.itemName + ")");
		}
	}

	public void onCommandMode(PlayerVars tmpVars, Player player, int mode){
		tmpVars.setMode(mode);
		tmpVars.removeAllWayPoints();
		
		if (mode == PlayerVars.MODE_SIMPLE) {
			player.sendMessage(ChatColor.GRAY + "Simple-mode selected ...");
		}else if (mode == PlayerVars.MODE_LENGTH) {
			player.sendMessage(ChatColor.GRAY + "Length-mode selected ...");
		}else if(mode == PlayerVars.MODE_SURFACE){
			player.sendMessage(ChatColor.GRAY + "Surface-mode selected ...");
		}
	}
	
	public static void onCommandClear(PlayerVars tmpVars, Player player){
	 	tmpVars.removeAllWayPoints();
    	player.sendMessage(ChatColor.RED + "Points-list cleared.");
	}
	
	public static void onCommandLength(PlayerVars tmpVars, Player player){
		player.sendMessage(ChatColor.WHITE + "Length: " + ChatColor.GOLD + tmpVars.getLenght() + ChatColor.WHITE + " Blocks");
	}
	
	public static void onCommandSwitchMode(PlayerVars tmpVars, Player player, boolean threeD){
    	tmpVars.setignoreHeight(! threeD);
    	
		if(tmpVars.getignoreHeight()){
        	player.sendMessage(ChatColor.GRAY + "switch to 2D-Mode (ignores the height)");
    	}else{
        	player.sendMessage(ChatColor.GRAY + "switch to 3D-Mode (does't ignores the height)");
    	}
	}
	
	public static void onCommandCountBlocks(PlayerVars tmpVars, Player player){
		if (tmpVars.getMode() == PlayerVars.MODE_SURFACE) {
			if (tmpVars.getBlockCountingThread() == null) {
				if (tmpVars.getWayPointListSize() >= 2) {
					player.sendMessage(ChatColor.GRAY + "Counting Blocks ...  (could take some time)");
					player.sendMessage(ChatColor.WHITE + "Cuboid-Volume: " + ChatColor.GOLD + (int)(tmpVars.getDimensionHieght() * tmpVars.getDimensionWith() * tmpVars.getDimensionLength()) + ChatColor.WHITE + " Blocks");
					
					tmpVars.countBlocks(player.getWorld());	
					Massband.log.warning(Massband.consoleOutputHeader + " " + tmpVars.getBlockCountingThread().getOwner().getName() + " starts a Block-counting Thread.");
					
	//				player.sendMessage(ChatColor.WHITE + "Content: " + ChatColor.GOLD + count + ChatColor.WHITE + " Blocks" + ChatColor.GRAY + " (exept air)");
					
				}else{
					player.sendMessage(ChatColor.RED + "Make a Selection first. see help (/massband)");	
				}
			}else{
				player.sendMessage(ChatColor.RED + "You can count Blocks once at the same time only ! Wait until it's ready or interrupt it");
			}
		}else{
			player.sendMessage(ChatColor.RED + "This command is only in the 'surface-mode' available - see help (/massband)");
		}
	}
	
	public static void onCommandDimensions(PlayerVars tmpVars, Player player){
		if (tmpVars.getMode() == PlayerVars.MODE_SURFACE) {
			player.sendMessage(ChatColor.WHITE +  "Width: " + ChatColor.GOLD + tmpVars.getDimensionWith() + ChatColor.WHITE + " Blocks");
			player.sendMessage(ChatColor.WHITE +  "Length: " + ChatColor.GOLD + tmpVars.getDimensionLength() + ChatColor.WHITE + " Blocks");
			player.sendMessage(ChatColor.WHITE +  "Height: " + ChatColor.GOLD + tmpVars.getDimensionHieght() + ChatColor.WHITE + " Blocks");
		}else{
			player.sendMessage(ChatColor.RED + "This command is only in the 'surface-mode' available - see help (/massband)");
		}
	}
	
	public static void printHelpMsg(Command command, CommandSender sender){
//		String[] usage = command.getUsage().split("" + (char) 10);
//		
//		for (String line : usage) {
//			if (line.contains("<%item>")) line = line.replaceAll("<%item>", Massband.configFile.itemName);	
//			sender.sendMessage(ChatColor.GRAY + line);
//		}
		
		String[] helpMsg =  {ChatColor.GREEN + "Massband 2.7.1 - A Measuring Tape - Command: " + ChatColor.RED + "/massband" + ChatColor.GREEN + " or " + ChatColor.RED + "/mb ",
							   ChatColor.RED + "/mb "						+ ChatColor.GOLD + "<enable|disable> "						+ ChatColor.GRAY + "Enables/Disables Massband for your self",
							   ChatColor.RED + "/mb "						+ ChatColor.GOLD + "<2D|3D> "								+ ChatColor.GRAY + "Switchs between the 2D/3D mode",
							   ChatColor.RED + "/mb " 						+ ChatColor.GOLD + "<simplemode|lengthmode|surfacemode> "	+ ChatColor.GRAY + "Switchs between the different measure mods",
							   ChatColor.RED + "/mb clear " 				+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Clears all measuring points",
							   ChatColor.RED + "/mb stop " 					+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Interrupt your current Block-counting",
							   ChatColor.RED + "/mb stopall " 				+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Interrupts all Block-countings of the server",
							   ChatColor.RED + "/mb length "				+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Returns the last measured length",
							   ChatColor.RED + "/mb dimensions "			+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Returns the dimensions of the selection",
							   ChatColor.RED + "/mb countBlocks "			+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Returns the count of Blocks in the selection (exept air)",
							   ChatColor.RED + "/mb expand "				+ ChatColor.GOLD + "<<amount> <up|down>|vert> "				+ ChatColor.GRAY + "expands the selection in the given direction. (vert = from bottom to the top)",
							   ChatColor.RED + "/mb blockList "				+ ChatColor.GOLD + "[page] "								+ ChatColor.GRAY + "Returns a List of all Blocks in the selection"};
		
		sender.sendMessage(helpMsg);
	}

}
