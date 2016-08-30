package net.icelane.massband;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.icelane.massband.command.CommandBase;
import net.icelane.massband.command.commands.MassbandCommand;
import net.icelane.massband.event.PlayerEvents;

public class Server {

	public static org.bukkit.Server get(){
		return Plugin.get().getServer();
	}
	
	public static Logger getLogger(){
		return get().getLogger();
	}
	
	public static void registerEvents(){
		JavaPlugin plugin = Plugin.get();

		get().getPluginManager().registerEvents(PlayerEvents.getListener(), plugin);
	}
	
	public static boolean registerCommands(){
		JavaPlugin plugin = Plugin.get();
		
		// register commands
		try {
			CommandBase.SetPermissionRootNode("massband");
			CommandBase.Initialize();
			
			//register the main commands 
			CommandBase.register(MassbandCommand.class);
			
			return true;
		} catch (Exception e) {
			getLogger().warning("[" + plugin.getDescription().getName() + "] Error: Commands not definated in 'plugin.yml'");
		}
		return false;
	}
}
