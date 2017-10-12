package net.icelane.massband.io.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.icelane.massband.Plugin;
import net.icelane.massband.config.configs.Config;
import net.icelane.massband.io.CommandBase;
import net.icelane.massband.io.commands.massband.Massband_Clear;
import net.icelane.massband.io.commands.massband.Massband_Count;
import net.icelane.massband.io.commands.massband.Massband_Debug;
import net.icelane.massband.io.commands.massband.Massband_Info;
import net.icelane.massband.io.commands.massband.Massband_Limit;
import net.icelane.massband.io.commands.massband.Massband_Mode;
import net.icelane.massband.io.commands.massband.Massband_NoLimit;
import net.icelane.massband.io.commands.massband.Massband_Reset;
import net.icelane.massband.io.commands.massband.Massband_Settings;


public class MassbandCommand extends CommandBase{

	@Override
	public String name() {
		return "massband";
	}
	
	@Override
	public void initialize() {
		addCommand(Massband_Clear.class);
		addCommand(Massband_Limit.class);
		addCommand(Massband_NoLimit.class);
		addCommand(Massband_Count.class);
		addCommand(Massband_Info.class);
		addCommand(Massband_Mode.class);
		addCommand(Massband_Reset.class);
		addCommand(Massband_Settings.class);
		addCommand(Massband_Debug.class);
	}
	
	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
	
		//**** EASTEREGG ****
		if (args.length == 1 && (args[0].equalsIgnoreCase("cat") || args[0].equalsIgnoreCase("meow") || args[0].equalsIgnoreCase(":3"))){
			sender.sendMessage("§aMeow §c:3");
			return true;
		}
		//*******************

		//**** Hidden features ****
		if (sender.isOp() && args.length == 1 && args[0].equalsIgnoreCase("_load")){
			Config.load();
			sender.sendMessage("§c(i) §6Massband config loaded");
			return true;
		}
		
		if (sender.isOp() && args.length == 1 && args[0].equalsIgnoreCase("_save")){
			Config.save();
			sender.sendMessage("§c(i) §6Massband config saved");
			return true;
		}
		
		if (sender.isOp() && args.length == 1 && args[0].equalsIgnoreCase("_disable")){
			sender.sendMessage("§c/!\\ §6Massband will be disabled!");
			Plugin.get().disable();
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
