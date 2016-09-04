package net.icelane.massband;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.icelane.massband.config.configs.Config;
import net.icelane.massband.core.Massband;

public class Plugin extends JavaPlugin{

	private static Plugin plugin;
	
	private boolean permissionsEnabled = true;
		
	public static Plugin get(){
		return plugin;
	}
	
	public static FileConfiguration config(){
		return get().getConfig();
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		
		plugin = this;
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		Config.initialize(Config.class);
		Config.save();
		
		Server.registerEvents();
		Server.registerCommands();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		
		Massband.cleanAll();
	}

	public void disable(){
		Bukkit.getPluginManager().disablePlugin(this);
	}
	
	public boolean isPermissionsEnabled() {
		return permissionsEnabled;
	}

	public void setPermissionsEnabled(boolean usePermissions) {
		this.permissionsEnabled = usePermissions;
	}
	
}
