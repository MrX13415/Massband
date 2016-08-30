package net.icelane.massband;

import org.bukkit.plugin.java.JavaPlugin;

import net.icelane.massband.core.Massband;

public class Plugin extends JavaPlugin{

	private static JavaPlugin plugin;

	public static JavaPlugin get(){
		return plugin;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		
		plugin = this;
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		Server.registerEvents();
		Server.registerCommands();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		
		Massband.cleanAll();
	}
	
}
