package net.icelane.massband.io.commands.massband;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.Server;
import net.icelane.massband.config.ConfigBase;
import net.icelane.massband.config.Entry;
import net.icelane.massband.config.configs.PlayerConfig;
import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;

public class Massband_Settings extends CommandBase{

	public static final String Default = "default";
	
	@Override
	public String name() {
		return "settings";
	}
	
	@Override
	public void initialize() {
		setAliases("cfg", "set");
		setDescription("Allows changes to any Massband settings.");
		setPermission("massband.command.settings", true);
		setUsage("[player] <config entry> [value]");
		
		
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> tabList = super.onTabComplete(sender, command, alias, args);
	
		String arg0 = args.length > 0 ? args[0].trim().toLowerCase() : "";

		ConfigBase<?> config = getConfig(sender);
		int argOffset = 0;
		
		if (args.length <= 1) {
			tabList.addAll(getTabListOfflinePlayers(arg0));
		}
					
		if (args.length >= 2) {
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

		// Try to find the target player 
		Player targetPlayer = null;
		if (args.length > 0) targetPlayer = Server.get().getPlayer(args[0].trim());
		
		boolean targetSelf = targetPlayer == null;
		
		if (targetSelf && !(sender instanceof Player)) {
			sender.sendMessage("§cError: player not found!");	
			return true;
		}
		
		// Retrieve config ...
		ConfigBase<?> config = null;
		if (targetSelf) config = getConfig(sender);  // self
		else config = getConfig((CommandSender)targetPlayer);  // other player

		if (config == null) {
			sender.sendMessage("§cError: config null!");			
			return true;
		}
		
		// A: List all settings of the config ...
		if ((targetSelf && args.length == 0) || (!targetSelf && args.length == 1)) {
			sender.sendMessage("§aSettings for Player: §c" + ((PlayerConfig)config).getPlayer().getName());
			
			List<Entry<?>> entryList = config.getEntryList();
			for (Entry<?> entry : entryList) {
				
				sender.sendMessage(String.format("§7 - §6%s§7: §c%s §7(default: §9%s§7)", 
						entry.getPath().replaceAll("\\.", "§c.§6"),
						entry.get().toString(),
						entry.getDefault().toString()));
			}
			return true;
		}
		
		// Try to find an entry ...
		Entry<?> entry = config.getEntry(targetSelf ? args[0].trim() : args[1].trim());
		
		if (entry == null) {
			sender.sendMessage("§cError: entry null!");
			return true;
		}

		// B: Return the value of a specific entry ...
		if ((targetSelf && args.length == 1) || (!targetSelf && args.length == 2)) {
			sender.sendMessage("§aSettings for Player: §c" + ((PlayerConfig)config).getPlayer().getName());
			sender.sendMessage(String.format("§7 - §6%s§7: §c%s   §7(default: §9%s§7)", 
					entry.getPath().replaceAll("\\.", "§c.§6"),
					entry.get().toString(),
					entry.getDefault().toString()));
			return true;
		}

		// C: Change the value of a specific entry ...
		if ((targetSelf && args.length == 2) || args.length == 3) {
			String value = targetSelf ? args[1].trim() : args[2].trim();
			return true;		
		}

		return false;
	}

	@Override
	public boolean command(Player player, Command cmd, String label, String[] args) {
		return command((CommandSender)player, cmd, label, args);
	}

}
