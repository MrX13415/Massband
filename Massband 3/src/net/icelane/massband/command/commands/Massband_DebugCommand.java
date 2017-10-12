package net.icelane.massband.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.Plugin;
import net.icelane.massband.command.CommandBase;
import net.icelane.massband.core.Massband;

public class Massband_DebugCommand extends CommandBase{

	@Override
	public String name() {
		return "debug";
	}
	
	@Override
	public void initialize() {
		setAliases("debug");
		setDescription("Debug command. peep bop.");
		setPermission("massband.debug", false);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1){
			if (args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("1")){
				Massband.setDebug(true);
				if (sender instanceof Player) sender.sendMessage("§c(i) §6Debug enabled");
			}else{
				Massband.setDebug(false);
				if (sender instanceof Player) sender.sendMessage("§c(i) §6Debug disabled");
			}
			return true;
		}
		
		if (Massband.isDebug() && sender.isOp() && args.length == 2 && args[0].equalsIgnoreCase("_permission")){
			Plugin.get().setPermissionsEnabled(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("1"));
			if (Plugin.get().isPermissionsEnabled())
				sender.sendMessage("§aDebug: /!\\ §6Permissions enabled");
			else
				sender.sendMessage("§cDebug: /!\\ §6Permissions disabled!");
			
			return true;
		}
		
		return false;
	}

	@Override
	public boolean command(Player player, Command cmd, String label, String[] args) {
		//command(player, cmd, label, args);
		
		//Massband obj = Massband.get(player);

		//obj.getMarkers(player.getWorld()).setMaxCount(1);
		player.sendMessage("§7BEEP §cBOP");
		
		return true;
	}

}
