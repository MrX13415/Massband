package de.MrX13415.Massband;

import java.util.ArrayList;
//import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;


import org.bukkit.plugin.Plugin;

/**
 * Massband (bukkit plugin)
 * A mesuring tape
 *
 * @version 2.6.6 r52
 * @author MrX13415
 * 
 * Copyright (C) 2011 MrX13415
 *
 *  This program is free software; you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation;
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program;
 *  if not, see <http://www.gnu.org/licenses/>.
 */

public class Massband extends JavaPlugin {
	
	public static Server server = null;
	public static Logger log = null;
	public static String pluginName = null;
	public static String consoleOutputHeader = null;
	public static Config configFile = null;

	//permissions
	public static PermissionHandler permissionHandler;
	public static final String PERMISSION_NODE_Massband_use = "Massband.use";
	public static final String PERMISSION_NODE_Massband_stop_all = "Massband.stopall";
	
	//holds information for all Players.
	public static ArrayList<PlayerVars> playerlist = new ArrayList<PlayerVars>();
	
	//holds all Counting Threads ..
	public static ArrayList<CountBlocks> threads = new ArrayList<CountBlocks>();
	
	private final PlayerListener pListener = new MassbandPlayerListener();
//	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();


	@Override
	public void onDisable() {
//		if (configFile.write()) { //create new File
//			log.info(consoleOutputHeader + " Settings in config saved");
//		}
		
		CountBlocks.interuptAll(null);
	}

	@Override
	public void onEnable() {
		//set static vars ...
		server = this.getServer();
		log = server.getLogger();
		
		PluginDescriptionFile pdfFile = this.getDescription();
		pluginName = pdfFile.getName();
		consoleOutputHeader = "[" + pluginName + "]";

        log.info(consoleOutputHeader + " v" + pdfFile.getVersion() + " " + pdfFile.getAuthors() + " is enabled.");
  
        configFile = new Config();
        configFile.read();
        //---------------------
                
        //register events ...
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, pListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, pListener, Priority.Normal, this);
		
		//register commands ...
		try {
			this.getCommand("massband").setExecutor(new MassbandCommandExecuter());
		} catch (Exception e) {
			log.warning("[" + pdfFile.getName() + "] Error: Commands not definated in 'plugin.yaml'");
		}
		
		setupPermissions();
	}
	
//    public boolean isDebugging(final Player player) {
//        if (debugees.containsKey(player)) {
//            return debugees.get(player);
//        } else {
//            return false;
//        }
//    }
//    
//    public void setDebugging(final Player player, final boolean value) {
//        debugees.put(player, value);
//    }
    
    private void setupPermissions() {
	      Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

	      if (Massband.permissionHandler == null) {
	          if (permissionsPlugin != null) {
	        	  Massband.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	        	  log.info(consoleOutputHeader + " Permission system detected: " + permissionsPlugin.getDescription().getFullName());
	          } else {
	        	  log.warning(consoleOutputHeader + " Permission system NOT detected! (everyone will have permissions to use it.)");
	          }
	      }
	  }
  	
	/** remove PlayerVars from the current Player, to keep
	 *  the ArrayList short.
	 * 
	 * @param player
	 */
	public static void removePlayer(Player player) {
		PlayerVars playerVars = getPlayerVars(player);
		if (playerVars != null) playerlist.remove(playerVars);
	}

	/** returns the PlayerVars for the given player
	 *  or create it.
	 *   
	 * @param player
	 * @return
	 */
	public static PlayerVars getPlayerVars(Player player) {
		PlayerVars playerVars = null;
		
		for (int playerIndex = 0; playerIndex < playerlist.size(); playerIndex++) {
    		if (playerlist.get(playerIndex).getPlayer().equals(player)) {
    			playerVars = playerlist.get(playerIndex); 
    		}
    	}
    	
		if (playerVars == null){
			//create PlayerVars for the current Player.
			playerVars = new PlayerVars(player);
			playerlist.add(playerVars);
		}
		
		return playerVars;
	}
}
