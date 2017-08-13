package net.icelane.massband;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import net.icelane.massband.command.CommandBase;
import net.icelane.massband.command.commands.MassbandCommand;
import net.icelane.massband.event.EntityEvents;
import net.icelane.massband.event.InventoryEvents;
import net.icelane.massband.event.PlayerEvents;
import net.icelane.massband.event.WorldEvents;

public class Server {

	public static org.bukkit.Server get(){
		return Plugin.get().getServer();
	}
	
	public static Logger logger(){
		return get().getLogger();
	}
	
	public static void registerEvents(){
		JavaPlugin plugin = Plugin.get();

		get().getPluginManager().registerEvents(PlayerEvents.getListener(), plugin);
		get().getPluginManager().registerEvents(EntityEvents.getListener(), plugin);
		get().getPluginManager().registerEvents(InventoryEvents.getListener(), plugin);
		get().getPluginManager().registerEvents(WorldEvents.getListener(), plugin);
	}
	
	public static boolean registerCommands(){
		JavaPlugin plugin = Plugin.get();
		
		// register commands
		try {
//			CommandBase.SetPermissionRootNode("massband");
//			CommandBase.Initialize();

			//register the main commands 
			CommandBase.register(MassbandCommand.class);
			
			return true;
		} catch (Exception ex) {
			logger().severe("[" + plugin.getDescription().getName() + "] Error: Commands not definated in 'plugin.yml'");
			logger().log(Level.SEVERE, "BOOM!", ex);
		}
		return false;
	}
}
