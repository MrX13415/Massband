package net.icelane.massband.io.commands.massband;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;

public class Massband_Debug extends CommandBase{

	@Override
	public String name() {
		return "debug";
	}
	
	@Override
	public void initialize() {
		setAliases("debug");
		setDescription("Debug command. peep bop.");
		setUsage("[true|false]");
		setPermission("massband.debug", false);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1){
			if (args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("1")){
				Massband.setDebug(true);
				if (sender instanceof Player) sender.sendMessage("�cDebug: �6/!\\ �aDebug enabled");
			}else{
				Massband.setDebug(false);
				if (sender instanceof Player) sender.sendMessage("�cDebug: �6/!\\ �cDebug disabled");
			}
			return true;
		}
		
		return false;
	}

	@Override
	public boolean command(Player player, Command cmd, String label, String[] args) {
		return true;
	}

}