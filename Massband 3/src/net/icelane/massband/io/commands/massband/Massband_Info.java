package net.icelane.massband.io.commands.massband;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.icelane.massband.Plugin;
import net.icelane.massband.io.CommandBase;

public class Massband_Info extends CommandBase{

	@Override
	public String name() {
		return "info";
	}
	
	@Override
	public void initialize() {
		setAliases("i");
		setDescription("Provides info");
		setPermission("massband.command.info", true);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
		Plugin plugin = Plugin.get();
		sender.sendMessage(String.format("§a%s §cversion §9$s", plugin.getName(), plugin.getDescription().getVersion()));
		return true;
	}
	
}
