package de.MrX13415.Massband;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Massband extends JavaPlugin {
	
	static String pluginName = null;
	static String consoleOutputHeader = null;
	
	public static Config configFile = null;
	
	private final PlayerListener pListener = new de.MrX13415.Massband.PlayerListener();
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	
	
	//holds informations for all Players.
	public static ArrayList<PlayerVars> playerlist = new ArrayList<PlayerVars>();
		
	@Override
	public void onDisable() {
	}

	@Override
	public void onEnable() {
	
		PluginDescriptionFile pdfFile = this.getDescription();
		pluginName = pdfFile.getName();
		consoleOutputHeader = "[" + pluginName + "]";

        System.out.println(consoleOutputHeader + " version " + pdfFile.getVersion() + " " + pdfFile.getAuthors() + " is enabled.");
  
        configFile = new Config();
        configFile.read();
        
        //register events ...
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, pListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_ITEM_HELD, pListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, pListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, pListener, Priority.Normal, this);

		//register commands ...
		try {
			this.getCommand("massband").setExecutor(new MassbandCommandExecuter());
		} catch (Exception e) {
			System.err.println("[" + pdfFile.getName() + "] Error: Commands not definated in 'plugin.yaml'");
		}
		
		
	}
	
    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }
    
    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
    
	public static void addPlayer(PlayerVars player) {
		playerlist.add(player);
	}
	
	public static void removePlayer(int index) {
		playerlist.remove(index);
	}
	
	public static PlayerVars getPlayer(int index) {
		return playerlist.get(index);
	}
	
	public static int getPlayerListSize() {
		return playerlist.size();
	}
}
