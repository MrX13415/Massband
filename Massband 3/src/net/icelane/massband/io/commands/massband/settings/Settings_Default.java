package net.icelane.massband.io.commands.massband.settings;

import org.bukkit.command.CommandSender;

import net.icelane.massband.config.ConfigBase;
import net.icelane.massband.config.configs.PlayerConfig;
import net.icelane.massband.io.commands.massband.Massband_Settings;

public class Settings_Default extends Massband_Settings{

//	public static final String Default = "default";
	
	@Override
	public String name() {
		return "default";
	}
	
	@Override
	public void initialize() {
		setAliases("defaults", "def");
		setDescription("Allows changes to default settings for all players.");
		setPermission("massband.command.settings.default", false);
		setUsage("<config entry> [value]");
	}

	@Override
	protected ConfigBase<?> getConfig(CommandSender sender) {
		return PlayerConfig.getDefault();
	}

	@Override
	public String getSettingsHeaderText(ConfigBase<?> config) {
		return "§cDefault §aPlayer Settings";
	}
	
	@Override
	protected boolean IsOtherPlayersEnabled(CommandSender sender) {
		return false;
	}

}
