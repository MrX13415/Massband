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
import net.icelane.massband.config.configs.Config;
import net.icelane.massband.config.configs.PlayerConfig;
import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;

public class Massband_Config extends CommandBase{

	public static final String Default = "default";
	
	@Override
	public String name() {
		return "config";
	}
	
	@Override
	public void initialize() {
		setAliases("settings", "cfg", "set");
		setDescription("Allows changes to any Massband settings.");
		setPermission("massband.command.config", true);
		setUsage("[<playerName>|default] <config entry> [value]");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> tabList = super.onTabComplete(sender, command, alias, args);

		while (args.length > 0) {
			String arg = args[args.length - 1];
			String pre = args.length > 1 ? args[args.length - 2] : "";
			ConfigBase<?> config = getConfig(sender, args[0]);
			
			if (args.length == 1) {
				if (Default.contains(arg.toLowerCase().trim())) 
					tabList.add(Default);
				
				tabList = addConfigEntries(tabList, config, arg);
				break;
			}

			if (args.length == 2) {
				tabList = addConfigEntries(tabList, config, arg);	
			}
						
			if (args.length > 1 && args.length <= 3) {				
				tabList = addConfigEntryValues(tabList, config, pre);	
			}
			
			break; //only once!
		}
		
		tabList.sort(Comparator.naturalOrder());
		return tabList; 		
	}
	
	private ConfigBase<?> getConfig(CommandSender sender, String arg) {

		if (arg.equals(Default)) {
			//default
			return PlayerConfig.getDefault();
		}
		
		Player player = Server.get().getPlayer(arg);
		if (player != null) {
			//player
			return Massband.get(player).config();
		}
		
		if (sender instanceof Player) {
			//player
			return  Massband.get((Player)sender).config();
		}
			
		//console
		return Config.get();
	}
	
	private List<String> addConfigEntries(List<String> tabList, ConfigBase<?> config, String arg) {
		if (config == null) return tabList;
		ArrayList<Entry<?>> configEntries = config.getEntryList();

		if (configEntries != null) {
			for (Entry<?> entry : configEntries) {
				String path = entry.getPath();
				
				if (path.contains(arg)) {
					tabList.add(path);
				}
			}
		}
		return tabList;
	}
	
	private List<String> addConfigEntryValues(List<String> tabList, ConfigBase<?> config, String pre) {
		if (config == null) return tabList;
		Entry<?> entry = config.getEntry(pre);
				
		if (entry != null) {
			tabList.add(entry.getDefault().toString());
			if (!entry.getDefault().toString().equals(entry.get().toString())) {
				tabList.add(entry.get().toString());
			}
		}
		
		return tabList;
	}
	
	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
		return true;
	}

	@Override
	public boolean command(Player player, Command cmd, String label, String[] args) {
		Massband obj = Massband.get(player);

		
		//mb config <setting> <value>
//		
//		if (args.length == 1){
//			try{
//				int value = Integer.valueOf(args[0]);
//				if (value < 1 && value > -1 ) value = 1;  // min marker count is 1!
//				if (value < 0) value = -1;  // no "limit"
//
//				obj.getMarkers(player.getWorld()).setMaxCount(value);
//				
//				player.sendMessage("§7Marker count set to: §c" +  (value == -1 ? "No Limit" : value));
//			}catch (NumberFormatException ex){
//				player.sendMessage("§cError: incorrect argument!");
//			}
//		}else{
//			int count = obj.getMarkers(player.getWorld()).getMaxCount();
//			player.sendMessage("§7Marker count: §c" + (count == -1 ? "No Limit" : count));		
//		}
		
		return true;
	}

}
