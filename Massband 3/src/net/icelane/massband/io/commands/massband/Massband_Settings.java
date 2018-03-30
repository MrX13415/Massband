package net.icelane.massband.io.commands.massband;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import net.icelane.massband.Server;
import net.icelane.massband.config.ConfigBase;
import net.icelane.massband.config.Entry;
import net.icelane.massband.config.configs.Config;
import net.icelane.massband.config.configs.PlayerConfig;
import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;
import net.icelane.massband.io.CommandText;
import net.icelane.massband.io.commands.massband.settings.Settings_Config;
import net.icelane.massband.io.commands.massband.settings.Settings_Default;

public class Massband_Settings extends CommandBase{

	public static final String ResetCommand = "reset";
//	public static final String Default = "default";
	public static final Permission otherPermission = new Permission("massband.command.settings.other", PermissionDefault.OP);
	
	@Override
	public String name() {
		return "settings";
	}
	
	
	//TODO: FLOAT value detection is broken!
	
	@Override
	public void initialize() {
		setAliases("cfg", "set");
		setDescription("Allows changes to your Massband settings.");
		setPermission("massband.command.settings", true);
		setUsage("<config entry> [value]");
		
		addCommand(Settings_Default.class);
		addCommand(Settings_Config.class);
		
		addSubPermission(otherPermission);
		setDescription("Allows changes to any Massband settings.", otherPermission);
		setUsage("[player] <config entry> [value]", otherPermission);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> tabList = super.onTabComplete(sender, command, alias, args);
	
		String arg0 = args.length > 0 ? args[0].trim().toLowerCase() : "";
		boolean other = IsOtherPlayersEnabled(sender);
		
		ConfigBase<?> config = getConfig(sender);
		int argOffset = 0;
		
		if (other && args.length <= 1) {
			tabList.addAll(getTabListOfflinePlayers(arg0));
		}
					
		if (other && args.length >= 2) {
			Player targetPlayer = Server.get().getPlayer(arg0);
			if (targetPlayer != null) {
				config = getConfig((CommandSender)targetPlayer);
				argOffset = 1;
			}
		}
		
		if (config != null) {
			if (args.length <= (1 + argOffset))
				tabList = addConfigEntries(tabList, config, args[0 + argOffset]);	
		}
		
		if (args.length == (2 + argOffset)) {
			tabList = addConfigEntryValues(tabList, config, args[0 + argOffset], args[1 + argOffset]);				
		}
	
		tabList.sort(Comparator.naturalOrder());
		return tabList; 		
	}
	
	protected ConfigBase<?> getConfig(CommandSender sender) {
		if (sender instanceof Player) {
			return  Massband.get((Player)sender).config();
		}
		return null;
	}
	
	protected List<String> addConfigEntries(List<String> tabList, ConfigBase<?> config, String arg) {
		if (config == null) return tabList;
		ArrayList<Entry<?>> configEntries = config.getEntryList();
		arg = arg.trim().toLowerCase();
		
		if (configEntries != null) {
			for (Entry<?> entry : configEntries) {
				String path = entry.getPath().trim().toLowerCase();
				
				if (path.contains(arg)) {
					tabList.add(path);
				}
			}
		}
		return tabList;
	}
	
	protected List<String> addConfigEntryValues(List<String> tabList, ConfigBase<?> config, String entrystr, String arg) {
		if (config == null) return tabList;
		Entry<?> entry = config.getEntry(entrystr);
		arg = arg.trim().toLowerCase();
		
		if (entry != null) {
			for (String value : entry.getValues()) {
				if (value.toLowerCase().trim().contains(arg))
					tabList.add(value);
			}
		}		
		return tabList;
	}
	
	/** 
	 * Enables or Disables the ability to target other players.
	 * Needs to return "false" if the config to edit isn't a "PlayerConfig".
	 *  
	 * @param sender
	 * @return
	 */
	protected boolean IsOtherPlayersEnabled(CommandSender sender) {
		return true;
	}
	
	public String getSettingsHeaderText(ConfigBase<?> config) {
		return "§aSettings for Player: §c" + ((PlayerConfig)config).getPlayer().getName();
	}
	
	public String getSettingEntryText(ConfigBase<?> config, Entry<?> entry) {
		return getSettingEntryText(config, entry, null);	
	}
	
	public String getSettingEntryText(ConfigBase<?> config, Entry<?> entry, String oldValue) {
		String oldValPart = "";
		String defStrPart = "";		
		String defaultVal = "";
		
		if (config instanceof PlayerConfig && !((PlayerConfig)config).isDefault()){
			Entry<?> entryDefault = PlayerConfig.getDefault().getEntry(entry.getPath());
			if (entryDefault != null) defaultVal = entryDefault.get().toString();
		}else {
			defaultVal = entry.getDefault().toString();
		}
			
		if (!defaultVal.isEmpty()) {
			defStrPart = String.format("   §7(default: §9%s§7)", defaultVal);
		}
		
		if (oldValue != null) {
			oldValPart = String.format("§f%s §7-> ", oldValue.trim());
		}

		return String.format("§f - §6%s §7=  %s§c%s%s", 
				entry.getPath().replaceAll("\\.", "§c.§6"),
				oldValPart,
				entry.get().toString(),
				defStrPart);	
	}
	
	
	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
		// Usage: [player] <entry> [value]
		//      [0]      [1]      [2]      
		// A 1  -        -        -         -> A: list whole config
		// A 2  player   -        -         -> A: list whole config
		// B 1  entry    -        -         -> B: get entry value
		// B 2  player   entry    -         -> B: get entry value
		// C 1  entry    value    -         -> C: set entry value
		// C 2  player   entry    value     -> C: set entry value

		boolean other = IsOtherPlayersEnabled(sender);
		boolean console = !(sender instanceof Player);
		
		// Try to find the target player 
		Player targetPlayer = null;
		if (other && args.length > 0) targetPlayer = Server.get().getPlayer(args[0].trim());
		
		// Determine if the settings are yours or from another player. 
		boolean targetSelf = targetPlayer == null;
		if (!targetSelf && !console){
			targetSelf = targetPlayer.getUniqueId().equals(((Player)sender).getUniqueId());
			if (targetSelf) args = getArgsOnly(args); // remove own name from args ...
		}
		
		// Not allowed to change other players settings!
		if (other && !targetSelf && !sender.hasPermission(otherPermission)) {
			setFailReason(FailReason.Permissions);
			sender.sendMessage(CommandText.getPermissionDenied(this, otherPermission));
			return false;
		}

		// You need to specify a player on console!
		if (other && console && targetPlayer == null) {
			if (args.length > 0) {
				setFailReason(FailReason.None);
				sender.sendMessage("§cError: §7Player not found: §6" + args[0].trim());
				return true; // syntax was correct.
			}else {
				setFailReason(FailReason.Invalid);
				return false;
			}
		}
		
		// Retrieve config ...
		ConfigBase<?> config = null;
		if (targetSelf) config = getConfig(sender);  // self
		else config = getConfig((CommandSender)targetPlayer);  // other player

		if (config == null) {
			sender.sendMessage("§cError: §7No configuration was found!");			
			return true; // syntax was correct.
		}
		
		// A: List all settings of the config ...
		if ((targetSelf && args.length == 0) || (!targetSelf && args.length == 1)) {
			sender.sendMessage(getSettingsHeaderText(config));
			
			List<Entry<?>> entryList = config.getEntryList();
			for (Entry<?> entry : entryList) {
				sender.sendMessage(getSettingEntryText(config, entry));
			}
			return true; // syntax was correct.
		}
		
		// Entry text 
		String entryStr = targetSelf ? args[0].trim() : args[1].trim();
		
		// Reset ?
		if (resetRequestConfig(entryStr, config, sender, targetPlayer, targetSelf))
			return true; // syntax was correct.
		
		// Try to find an entry matching our entry text ...
		Entry<?> entry = config.getEntry(entryStr);

		if (entry == null) {
			sender.sendMessage("§cError: §7Unkown entry: §6" + entryStr);
			return true; // syntax was correct.
		}

		// B: Return the value of a specific entry ...
		if ((targetSelf && args.length == 1) || (!targetSelf && args.length == 2)) {
			entryGetValue(config, sender, entry);
			return true; // syntax was correct.
		}

		// C: Change the value of a specific entry ...
		if ((targetSelf && args.length == 2) || args.length == 3) {					
			String value = targetSelf ? args[1].trim() : args[2].trim();
			String oldValue = entry.get().toString();
			
			// Reset ?
			if (resetRequestEntry(value, config, entry, sender, targetPlayer, targetSelf))
				return true; // syntax was correct.
			
			entrySetValue(config, sender, entry, value, oldValue);	
			return true; // syntax was correct.
		}

		return false; // wrong syntax!
	}

	/**
	 * B: Return the value of a specific entry.
	 * @param config
	 * @param sender
	 * @param entry
	 */
	private void entryGetValue(ConfigBase<?> config, CommandSender sender, Entry<?> entry) {
		sender.sendMessage(getSettingsHeaderText(config));
		sender.sendMessage(getSettingEntryText(config, entry));
	}
	
	/**
	 * C: Change the value of a specific entry.
	 * @param config
	 * @param sender
	 * @param entry
	 * @param value
	 * @param oldValue
	 */
	private void entrySetValue(ConfigBase<?> config, CommandSender sender, Entry<?> entry, String value, String oldValue) {
		boolean ok = entry.setValueOf(value);					
		if (ok) {
			sender.sendMessage(getSettingsHeaderText(config));
			sender.sendMessage(getSettingEntryText(config, entry, oldValue));
			config.save();
			sender.sendMessage("§aValue changed to: §c" + value);
		}else{
			sender.sendMessage("§cError: §7Invalid value: §6" + value);
		}
	}
	
	private boolean resetRequestConfig(String argument, ConfigBase<?> config, CommandSender sender, Player targetPlayer, boolean targetSelf) {
		boolean resetRequest = argument.trim().equalsIgnoreCase(ResetCommand.trim());
		if (!resetRequest) return false;
			
		List<Entry<?>> entryList = config.getEntryList();
		for (Entry<?> entry : entryList) {
			entry.resetToDefault();
			sender.sendMessage(getSettingEntryText(config, entry));
		}
		
		String tps = targetPlayer != null ? targetPlayer.getDisplayName() : "<None>";
		sender.sendMessage("(Player: " + tps + "; TargetSelf: " + targetSelf + ")");
		sender.sendMessage("§9Config reset to default: §6" + this.getName());
		return true;
	}
	
	private boolean resetRequestEntry(String argument, ConfigBase<?> config, Entry<?> entry, CommandSender sender, Player targetPlayer, boolean targetSelf) {
		boolean resetRequest = argument.trim().equalsIgnoreCase(ResetCommand.trim());
		if (!resetRequest) return false;
		
		entry.resetToDefault();
		String tps = targetPlayer != null ? targetPlayer.getDisplayName() : "<None>";
		sender.sendMessage("(Player: " + tps + "; TargetSelf: " + targetSelf + ")");
		sender.sendMessage("§9Entry reset to default!");
		sender.sendMessage(getSettingEntryText(config, entry));
		return true;
	}
}
