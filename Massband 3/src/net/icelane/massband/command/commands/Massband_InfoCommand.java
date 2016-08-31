package net.icelane.massband.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.icelane.massband.command.CommandBase;

public class InfoCommand extends CommandBase{

	@Override
	public void initialize() {
		setLabel("info");
		setAliases("i");
		setDescription("Provides info");
		setPermissionNode("info");
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}

}
