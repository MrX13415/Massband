package net.icelane.massband;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import net.icelane.massband.event.EntityEvents;
import net.icelane.massband.event.InventoryEvents;
import net.icelane.massband.event.PlayerEvents;
import net.icelane.massband.event.WorldEvents;
import net.icelane.massband.io.CommandBase;
import net.icelane.massband.io.commands.MassbandCommand;

public abstract class Server {

	public static org.bukkit.Server get(){
		return Plugin.get().getServer();
	}
	
	public static Logger logger(){
		return Plugin.logger;
	}
	
	public static void registerEvents(){
		JavaPlugin plugin = Plugin.get();

		get().getPluginManager().registerEvents(PlayerEvents.getListener(), plugin);
		get().getPluginManager().registerEvents(EntityEvents.getListener(), plugin);
		get().getPluginManager().registerEvents(InventoryEvents.getListener(), plugin);
		get().getPluginManager().registerEvents(WorldEvents.getListener(), plugin);
	}
	
	public static boolean registerCommands(){
		// register commands
		try {
			//register the main command (/massband)
			CommandBase.register(MassbandCommand.class);
			
			return true;
		} catch (Exception ex) {
			logger().severe("Error: Commands not definated in 'plugin.yml'");
			logger().log(Level.SEVERE, "BOOM!", ex);
		}
		return false;
	}
}
