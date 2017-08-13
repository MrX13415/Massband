package net.icelane.massband.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.command.CommandBase;
import net.icelane.massband.core.Massband;

public class Massband_CountCommand extends CommandBase{

	@Override
	public String name() {
		return "count";
	}
	
	@Override
	public void initialize() {
		setAliases("markers", "cnt", "c");
		setDescription("Set the number of markers to be placed. Set to -1 for no Limit.");
		setPermissionNode("count");
		setUsage("<marker count>");
		setInGameOnly(true);
		setTabList("10", "1", "-1");
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
		return true;
	}

	@Override
	public boolean command(Player player, Command cmd, String label, String[] args) {
		Massband obj = Massband.get(player);
		
		if (args.length == 1){
			try{
				int value = Integer.valueOf(args[0]);
				if (value < 1 && value > -1 ) value = 1;  // min marker count is 1!
				if (value < 0) value = -1;  // no "limit"

				obj.getMarkers(player.getWorld()).setMaxCount(value);
				
				player.sendMessage("§7Marker count set to: §c" +  (value == -1 ? "No Limit" : value));
			}catch (NumberFormatException ex){
				player.sendMessage("§cError: incorrect argument!");
			}
		}else{
			int count = obj.getMarkers(player.getWorld()).getMaxCount();
			player.sendMessage("§7Marker count: §c" + (count == -1 ? "No Limit" : count));		
		}
		
		return true;
	}

}
