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

	boolean hasPermission_use = false;
	boolean hasPermission_stopall = false;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {		
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (Massband.permissionHandler != null) {	
								
				//use Permission
				if (Massband.permissionHandler.permission(player, Massband.PERMISSION_NODE_Massband_use)) hasPermission_use = true;
				if (Massband.permissionHandler.permission(player, Massband.PERMISSION_NODE_Massband_stop_all)) hasPermission_stopall = true;
				
				if (hasPermission_use) command(sender, command, label, args);
			}else{
				
				if (player.isOp()) {
					hasPermission_stopall = true;
				}
				
				//no Permission installed ! (everyone has access)
				command(sender, command, label, args);
			}
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
			
			if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_clear)) {
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
				onCommandCountBlocks(tmpVars, player);
			
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
				}
			}else{
				printHelpMsg(command, player);
			}
		}
	}
	
	private void onCommandExpand(PlayerVars tmpVars, Player player, int modeSurface, int expandsize, String direction ) {
		if (tmpVars.getWayPointListSize() == 2) {
			
			org.bukkit.util.Vector point1 = tmpVars.getWayPointList().get(0);
			org.bukkit.util.Vector point2 = tmpVars.getWayPointList().get(1);
			
			if (direction.equalsIgnoreCase("up")) {
				point1.setY(point1.getY() + expandsize);
			
				if (point1.getY() > 128) point1.setY(128);
				if (point1.getY() < 0) point1.setY(0);
				
				player.sendMessage(ChatColor.GRAY + "Selection expanded ... (" + expandsize + " Blocks up)");
				
			}else if (direction.equalsIgnoreCase("down")) {
				point2.setY(point2.getY() - expandsize);
				
				if (point2.getY() > 127) point2.setY(127);
				if (point2.getY() < 0) point2.setY(0);
				
				player.sendMessage(ChatColor.GRAY + "Selection expanded ... (" + expandsize + " Block down)");
				
				
			}else if (direction.equalsIgnoreCase("vert")) {
				point1.setY(128);
				point2.setY(0);
			
				if (point1.getY() > 127) point1.setY(127);
				if (point2.getY() > 127) point2.setY(127);
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
		String[] usage = command.getUsage().split("" + (char) 10);
		
		for (String line : usage) {
			if (line.contains("<%item>")) line = line.replaceAll("<%item>", Massband.configFile.itemName);	
			sender.sendMessage(ChatColor.GRAY + line);
		}
	}

}
