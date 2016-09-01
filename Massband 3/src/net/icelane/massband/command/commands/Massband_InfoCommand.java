package net.icelane.massband.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.icelane.massband.Plugin;
import net.icelane.massband.command.CommandBase;

public class Massband_InfoCommand extends CommandBase{

	@Override
	public String name() {
		return "info";
	}
	
	@Override
	public void initialize() {
		setAliases("i");
		setDescription("Provides info");
		setPermissionNode("info");
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
		Plugin plugin = Plugin.get();
		sender.sendMessage("�a" + plugin.getName() + " �7version �c" + plugin.getDescription().getVersion());
		return true;
	}
	
}
