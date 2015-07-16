package de.MrX13415.Massband.CommandExecuter;

import java.util.ArrayList;

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
			
			if (Massband.permissionsEnabled()) {					
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
		    	player.sendMessage(Massband.language.MB_ENABLED);
		    	
			}else if (args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_MassbandDisable)) {
		    	tmpVars.setEnabled(false);
		    	player.sendMessage(Massband.language.MB_ENABLED);
		    	
			}else if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_clear)) {
		    	onCommandClear(tmpVars, player);
		    	
			}else if (args[0].equalsIgnoreCase("length") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_lenght)) {
	        	onCommandLastLength(tmpVars, player);
	        	
			}else if (args[0].equalsIgnoreCase("ígnoreaxes") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_ignoreaxes)) {
				onCommandSwitchMode(tmpVars, player, args);
			
			}else if (args[0].equalsIgnoreCase("dimensions") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_dimensions)) {
				onCommandDimensions(tmpVars, player);
					
			}else if (args[0].equalsIgnoreCase("countblocks") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_countblocks)) {
				if (hasPermission_countblocks){
					onCommandCountBlocks(tmpVars, player);
				}else{
					player.sendMessage(String.format(Massband.language.PERMISSION_NOT, Massband.PERMISSION_NODE_Massband_countblocks));
				}
			}else if (args[0].equalsIgnoreCase("lengthmode") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_lengthmode)) {
				onCommandMode(tmpVars, player, PlayerVars.MODE_LENGTH);
			
			}else if (args[0].equalsIgnoreCase("fixedmode") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_fixedmode)) {
				
				if (args.length == 2){
					try{
						double flength = Double.valueOf(args[1]);
						tmpVars.setFixedLenght(flength);
						onCommandMode(tmpVars, player, PlayerVars.MODE_FIXED, String.valueOf(flength));
					}catch(Exception e){
						player.sendMessage(Massband.getLanguage().FIXED_MODE_ERROR);
						printHelpMsg(command, player);
					}					
				}else{
					printHelpMsg(command, player);
				}
				
			}else if (args[0].equalsIgnoreCase("surfacemode") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_surfacemode)) {
				onCommandMode(tmpVars, player, PlayerVars.MODE_SURFACE);
			
			}else if (args[0].equalsIgnoreCase("simplemode") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_simplemode)) {
				onCommandMode(tmpVars, player, PlayerVars.MODE_SIMPLE);
			
			}else if (args[0].equalsIgnoreCase("expand") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_expand)) {
				if (args.length >= 2 ){
					try {
						int expandSize = 0;
						String direction = args[1];
						
						try{
							expandSize = Integer.valueOf(args[1]);
						}catch(Exception e){}
						
						if (args.length == 3) {
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
					player.sendMessage(Massband.language.THREADS_INTERUPT_NOTHING);
				}
			}else if (args[0].equalsIgnoreCase("stopall") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_stopall)) {
				//stopall Permission
				if (hasPermission_stopall) {
					CountBlocks.interuptAll(sender);
				}else{
					player.sendMessage(String.format(Massband.language.PERMISSION_NOT, Massband.PERMISSION_NODE_Massband_stop_all));
				}
			}else if (args[0].equalsIgnoreCase("blocklist") || args[0].equalsIgnoreCase(Massband.configFile.commandShortForm_blockList)) {				
				if (hasPermission_blocklist){
					if (args.length >= 3 && args[1].equalsIgnoreCase("book")){
						onCommandBlocklistBook(tmpVars, args);
					}else{
						onCommandBlockList(tmpVars, args);
					}
				}else{
					player.sendMessage(String.format(Massband.language.PERMISSION_NOT, Massband.PERMISSION_NODE_Massband_blocklist));
				}
			}else{
				printHelpMsg(command, player);
			}
		}
	}
	
	private void onCommandBlockList(PlayerVars tmpVars, String[] args){
		int page = 1;
		
		ArrayList<String> mats = new ArrayList<String>();
		
		if (args.length == 2) {
			try {
				page = Integer.valueOf(args[1]);
			} catch (Exception e){
				mats.add(args[1]);
			};
		}else{
			for (int i = 1; i < args.length; i++) {
				mats.add(args[i]);				
			}
		}
		
		if (page >= 1 && mats.size() == 0) tmpVars.printBlockListPage(page);
		else tmpVars.findMaterial(mats);
	}
	
	public void onCommandBlocklistBook(PlayerVars tmpVars, String[] args){

		String projectName = args[2];
		String objName = projectName;
		
		String author = tmpVars.getPlayer().getName();
		
		if (args.length >= 4) objName = args[3];
		if (args.length >= 5) author = args[4];

		tmpVars.createBlocklistBook(projectName, objName, author);
	}
	
	private void onCommandExpand(PlayerVars tmpVars, Player player, int modeSurface, int expandsize, String direction ) {
		if (tmpVars.getWayPointListSize() == 2) {
			
			org.bukkit.util.Vector point1 = tmpVars.getWayPointList().get(0);
			org.bukkit.util.Vector point2 = tmpVars.getWayPointList().get(1);
			
			if (direction.equalsIgnoreCase("up")) {
				point1.setY(point1.getY() + expandsize);
			
				if (point1.getY() > player.getWorld().getMaxHeight()) point1.setY(player.getWorld().getMaxHeight());
				if (point1.getY() < 0) point1.setY(0);
				
				player.sendMessage(String.format(Massband.language.EXPANDED_UP, expandsize));
				
			}else if (direction.equalsIgnoreCase("down")) {
				point2.setY(point2.getY() - expandsize);
				
				if (point2.getY() > player.getWorld().getMaxHeight()) point2.setY(player.getWorld().getMaxHeight());
				if (point2.getY() < 0) point2.setY(0);
				
				player.sendMessage(String.format(Massband.language.EXPANDED_DOWN, expandsize));
				
				
			}else if (direction.equalsIgnoreCase("vert")) {
				point1.setY(player.getWorld().getMaxHeight());
				point2.setY(0);
			
				if (point1.getY() > (player.getWorld().getMaxHeight())) point1.setY(player.getWorld().getMaxHeight());
				if (point2.getY() > (player.getWorld().getMaxHeight())) point2.setY(player.getWorld().getMaxHeight());
				if (point1.getY() < 0) point1.setY(0);
				if (point2.getY() < 0) point2.setY(0);
				
				player.sendMessage(Massband.language.EXPANDED_VERT);
			}else{
				if (expandsize > 0){
					direction = getCardinalDirection(player);
					
					double p1z = point1.getZ();
					double p2z = point2.getZ();
					double p1x = point1.getX();
					double p2x = point2.getX();

					if (direction.equals("N")){
	
						if (p1z < p2z) point1.setZ(p1z - expandsize);	
						else if (p1z > p2z) point2.setZ(p2z - expandsize);
						else point1.setZ(p1z - expandsize);

					}else if (direction.equals("S")){
						
						if (p1z > p2z) point1.setZ(p1z + expandsize);	
						else if (p1z < p2z) point2.setZ(p2z + expandsize);
						else point1.setZ(p1z + expandsize);
						
					}else if (direction.equals("E")){
						point1.setX(point1.getX() + expandsize);
						
						if (p1x > p2x) point1.setX(p1x + expandsize);	
						else if (p1x < p2x) point2.setX(p2x + expandsize);
						else point1.setX(p1x + expandsize);
						
					}else if (direction.equals("W")){
						
						if (p1x < p2x) point1.setX(p1x - expandsize);	
						else if (p1x > p2x) point2.setX(p2x - expandsize);
						else point1.setX(p1x - expandsize);
						
					}
				
					player.sendMessage(String.format(Massband.language.EXPANDED_DIRECTION, expandsize, direction));
									
				}
			}
			

			tmpVars.computingVectors();
			tmpVars.calculateDiminsions();
			MassbandCommandExecuter.onCommandDimensions(tmpVars, player);	//output
		}else{
			player.sendMessage(String.format(Massband.language.SELECTION_FIRST, Massband.configFile.itemName));
		}
	}
	
	public static String getCardinalDirection(Player player) {
		double rotation = (player.getLocation().getYaw() -180) % 360;
		if (rotation < 0) {
			rotation += 360.0;
		}
		if (0 <= rotation && rotation < 22.5) {
			return "N";
		} else if (22.5 <= rotation && rotation < 67.5) {
			return "NE";
		} else if (67.5 <= rotation && rotation < 112.5) {
			return "E";
		} else if (112.5 <= rotation && rotation < 157.5) {
			return "SE";
		} else if (157.5 <= rotation && rotation < 202.5) {
			return "S";
		} else if (202.5 <= rotation && rotation < 247.5) {
			return "SW";
		} else if (247.5 <= rotation && rotation < 292.5) {
			return "W";
		} else if (292.5 <= rotation && rotation < 337.5) {
			return "NW";
		} else if (337.5 <= rotation && rotation < 360.0) {
			return "N";
		} else {
			return null;
		}
	}
	
	public void onCommandMode(PlayerVars tmpVars, Player player, int mode){
		onCommandMode(tmpVars, player, mode, "");
	}
	
	public void onCommandMode(PlayerVars tmpVars, Player player, int mode, String value){
		tmpVars.setMode(mode);
		tmpVars.removeAllWayPoints();
		
		if (mode == PlayerVars.MODE_SIMPLE) {
			player.sendMessage(Massband.language.MODE_SIMPLE2);
		}else if (mode == PlayerVars.MODE_LENGTH) {
			player.sendMessage(Massband.language.MODE_LENGTH2);
		}else if(mode == PlayerVars.MODE_SURFACE){
			player.sendMessage(Massband.language.MODE_SURFACE2);
		}else if(mode == PlayerVars.MODE_FIXED){
			player.sendMessage(String.format(Massband.language.MODE_FIXED2, value));
		}
	}
	
	public static void onCommandClear(PlayerVars tmpVars, Player player){
	 	tmpVars.removeAllWayPoints();
    	player.sendMessage(Massband.language.POINT_CLR);
	}
	
	public static void onCommandLastLength(PlayerVars tmpVars, Player player){
		if (tmpVars.getMode() == PlayerVars.MODE_FIXED)
			player.sendMessage(String.format(Massband.language.DIFF, tmpVars.getLenght() * -1 + tmpVars.getFixedLenght(), tmpVars.getFixedLenght(), tmpVars.getLenght()));
		else
			player.sendMessage(String.format(Massband.language.LENGTH, tmpVars.getLenght()));
	}
	
	public static void onCommandSwitchMode(PlayerVars tmpVars, Player player, String[] args){
		tmpVars.getIgnoredAxes().clear();
		for (String arg : args) {
			if (arg.equalsIgnoreCase(PlayerVars.AXIS.X.name())) tmpVars.getIgnoredAxes().add(PlayerVars.AXIS.X);
			if (arg.equalsIgnoreCase(PlayerVars.AXIS.Y.name())) tmpVars.getIgnoredAxes().add(PlayerVars.AXIS.Y);
			if (arg.equalsIgnoreCase(PlayerVars.AXIS.Z.name())) tmpVars.getIgnoredAxes().add(PlayerVars.AXIS.Z);
			if (PlayerVars.AXIS.NONE.name().startsWith(arg.toUpperCase())) tmpVars.getIgnoredAxes().clear();
		}
			
		player.sendMessage(String.format(Massband.getLanguage().IGNORE_AXIS, tmpVars.getIgnoredAxesAsString()));
	}
	
	
	public static void onCommandCountBlocks(PlayerVars tmpVars, Player player){
		if (tmpVars.getMode() == PlayerVars.MODE_SURFACE) {
			if (tmpVars.getBlockCountingThread() == null) {
				if (tmpVars.getWayPointListSize() >= 2) {
					player.sendMessage(Massband.language.COUNTBLOCK);
					player.sendMessage(String.format(Massband.language.COUNTBLOCK_VOL, (int)(tmpVars.getDimensionHieght() * tmpVars.getDimensionWith() * tmpVars.getDimensionLength())));
					
					tmpVars.countBlocks(player.getWorld());	
					Massband.log.warning(Massband.consoleOutputHeader + " " + tmpVars.getBlockCountingThread().getOwner().getName() + " starts a Block-counting Thread.");
					
	//				player.sendMessage(ChatColor.WHITE + "Content: " + ChatColor.GOLD + count + ChatColor.WHITE + " Blocks" + ChatColor.GRAY + " (exept air)");
					
				}else{
					player.sendMessage(Massband.language.SELECTION_FIRST);	
				}
			}else{
				player.sendMessage(Massband.language.COUNTBLOCK_ONCE);
			}
		}else{
			player.sendMessage(Massband.language.SFM_ONLY);
		}
	}
	
	public static void onCommandDimensions(PlayerVars tmpVars, Player player){
		if (tmpVars.getMode() == PlayerVars.MODE_SURFACE) {
			player.sendMessage(String.format(Massband.language.D_WIDTH, tmpVars.getDimensionWith()));
			player.sendMessage(String.format(Massband.language.D_LENGTH, tmpVars.getDimensionLength()));
			player.sendMessage(String.format(Massband.language.D_HEIGHT, tmpVars.getDimensionHieght()));
		}else{
			player.sendMessage(Massband.language.SFM_ONLY);
		}
	}
	
	public static void printHelpMsg(Command command, CommandSender sender){
		sender.sendMessage(Massband.getLanguage().COMMAND_MASSBAND_USAGE.split("\\\\n"));
	}

}
