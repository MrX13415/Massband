package net.icelane.massband.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.command.CommandBase;
import net.icelane.massband.core.Massband;

public class MassbandCommand extends CommandBase{

	@Override
	public void initialize() {
		setLabel("massband");
		setAliases("mb");
		setDescription("Manage Massband");
		setPermissionNode("massband");
		
		addCommand(InfoCommand.class);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
	
		//**** DEBUG ****
		if (args.length == 2 && args[0].equalsIgnoreCase("count")){
			if (sender instanceof Player){
				int value = 2;
				try{
					value = Integer.valueOf(args[1]);
					if (value < 2) value = 2;
					
					Massband.get((Player) sender).markerCount = value;
					sender.sendMessage("Waypoint count set to: " + value);
				}catch (NumberFormatException ex){
					sender.sendMessage("Error: incorrect argument!");
				}
			}else{
				sender.sendMessage("Only for ingame usage!");
			}
			
			return true;
		}
		//*******
		
		if (args.length == 2 && args[0].equalsIgnoreCase("spm")){
			if (sender instanceof Player){
				if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("1")){
					Massband.get((Player) sender).specialMode = true;
				}else{
					Massband.get((Player) sender).specialMode = false;
				}
			}else{
				sender.sendMessage("Only for ingame usage!");
			}
			
			
			return true;
		}
		
		
		
		//**** DEBUG ****
		if (args.length == 2 && args[0].equalsIgnoreCase("debug")){
			if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("1")){
				//UpdateCraft.get().setDebug(true);
				//UpdateCraft.get().getServer().broadcastMessage(Text.get(Text.DEBUG_TRUE, UpdateCraft.get().getDescription().getName()));
			}else{
				//UpdateCraft.get().setDebug(false);
				//UpdateCraft.get().getServer().broadcastMessage(Text.get(Text.DEBUG_FALSE, UpdateCraft.get().getDescription().getName()));
			}
			return true;
		}
		//***************
		
		//**** EASTEREGG ****
		if (args.length == 1 && (args[0].equalsIgnoreCase("cat") || args[0].equalsIgnoreCase("meow") || args[0].equalsIgnoreCase(":3"))){
			sender.sendMessage("§aMeow §c:3");
			return true;
		}
		//*******************

		return false;
	}

}
