package net.icelane.massband.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.command.CommandBase;
import net.icelane.massband.core.Marker;
import net.icelane.massband.core.Marker.MeasureMode;
import net.icelane.massband.core.Massband;

public class Massband_ModeCommand extends CommandBase{

	@Override
	public String name() {
		return "mode";
	}
	
	@Override
	public void initialize() {
		setAliases("md", "m");
		setDescription("Set measuring mode to be used.");
		setHelp("§7Set measuring mode to be used. Available modes:"
				+ "\n   §71: §cblocks    §7Measurs block distance in X or Y direction or 45 degrees diagonal."
				+ "\n   §72: §cvectors   §7Measures the vector distance between the center points of blocks.");
		setPermission("massband.command.mode", true);
		setUsage("[blocks|vectors]");
		setInGameOnly(true);
		setTabList("blocks", "vectors");
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
				MeasureMode value = null;
				int valueInt = -1;
				try{valueInt = Integer.valueOf(args[0]);}catch (NumberFormatException ex){}
				
				int index = 0; 
				for (MeasureMode mode : Marker.MeasureMode.values()){
					if (index == valueInt) valueInt = index;
					if (mode.toString().toLowerCase().contains(args[0].toLowerCase().trim())){
						value = mode;
					}
					index++;
				}
				
				if (value == null) value = Marker.MeasureMode.values()[valueInt];  
				
				obj.getMarkers(player.getWorld()).setMode(value);
				obj.getMarkers(player.getWorld()).recalculate();
				
				player.sendMessage("§7Measuring mode set to: §c" + value.toString().toLowerCase());
			}catch (Exception ex){
				player.sendMessage("§cError: incorrect argument!");
			}
		}else{
			player.sendMessage("§7Measuring mode: §c" + obj.getMarkers(player.getWorld()).getMode().toString().toLowerCase());		
		}
		
		return true;
	}

}
