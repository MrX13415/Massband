package net.icelane.massband.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.command.CommandBase;
import net.icelane.massband.core.Massband;

public class Massband_CountCommand extends CommandBase{

	@Override
	public String name() {
		return "markercount";
	}
	
	@Override
	public void initialize() {
		setAliases("count", "markers", "cnt", "c");
		setDescription("Set the number of markers to be placed");
		setPermissionNode("count");
		setUsage("/<command> <marker count>");
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
		sender.sendMessage("Only for ingame usage!");
		return true;
	}

	@Override
	public boolean command(Player player, Command cmd, String label, String[] args) {
		Massband obj = Massband.get(player);
		
		if (args.length == 1){
			try{
				int value = Integer.valueOf(args[0]);
				if (value < 2) value = 2;  // min marker count is 2!
				
				obj.getMarker(player.getWorld()).setMaxCount(value);
				
				player.sendMessage("§7Marker count set to: §c" + value);
			}catch (NumberFormatException ex){
				player.sendMessage("Error: incorrect argument!");
			}
		}else{
			player.sendMessage("§7Marker count: §c" + obj.getMarker(player.getWorld()).getMaxCount());		
		}
		
		return true;
	}

}
