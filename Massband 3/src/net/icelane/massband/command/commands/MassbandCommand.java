package net.icelane.massband.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.icelane.massband.Plugin;
import net.icelane.massband.command.CommandBase;

public class MassbandCommand extends CommandBase{

	@Override
	public String name() {
		return "massband";
	}
	
	@Override
	public void initialize() {
		//setAliases("mb");
		//setDescription("Manage Massband");
		//setPermissionNode("massband");
		//setUsage("Uhm ... idk?");
		
		addCommand(Massband_ClearCommand.class);
		addCommand(Massband_CountCommand.class);
		addCommand(Massband_InfoCommand.class);
		addCommand(Massband_ModeCommand.class);
		addCommand(Massband_ResetCommand.class);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
	
//		if (args.length == 2 && args[0].equalsIgnoreCase("spm")){
//			if (sender instanceof Player){
//				if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("1")){
//					Massband.get((Player) sender).specialMode = true;
//				}else{
//					Massband.get((Player) sender).specialMode = false;
//				}
//			}else{
//				sender.sendMessage("Only for ingame usage!");
//			}
//			
//			
//			return true;
//		}
				
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

		//**** Hidden features ****
		if (args.length == 1 && args[0].equalsIgnoreCase("_disable")){
			sender.sendMessage("§c/!\\ §6Massband will be disabled!");
			Plugin.get().disable();
			return true;
		}
		
		if (args.length == 1 && args[0].equalsIgnoreCase("_enable")){
			//sender.sendMessage("§c/!\\ §6Massband will be disabled!");
			//Plugin.get().disable();
			return true;
		}
		
		if (args.length == 1 && (args[0].equalsIgnoreCase("_colors") || args[0].equalsIgnoreCase("_color"))){
			sender.sendMessage("§7Colors: §11 §22 §33 §44 §55 §66 §77 §88 §99 §aa §bb §cc §dd §ee §ff");
			return true;
		}
		//*******************
		
		return false;
	}
	
}
