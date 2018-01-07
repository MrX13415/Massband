package net.icelane.massband.io.commands.massband.settings;

import org.bukkit.command.CommandSender;

import net.icelane.massband.config.ConfigBase;
import net.icelane.massband.config.configs.Config;
import net.icelane.massband.io.commands.massband.Massband_Settings;

public class Settings_Config extends Massband_Settings{

	public static final String Default = "default";
	
	@Override
	public String name() {
		return "config";
	}
	
	@Override
	public void initialize() {
		setAliases("config", "cfg");
		setDescription("Allows changes to the Massband configuration.");
		setPermission("massband.command.settings.config", false);
		setUsage("<config entry> [value]");
	}

	@Override
	protected ConfigBase<?> getConfig(CommandSender sender) {
		return Config.get();
	}

	@Override
	public String getSettingsHeaderText(ConfigBase<?> config) {
		return "§cMassband §aConfiguration";
	}
	
	@Override
	protected boolean IsOtherPlayersEnabled(CommandSender sender) {
		return false;
	}

}
