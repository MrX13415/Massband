package net.icelane.massband.io.commands.massband.debug;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.Plugin;
import net.icelane.massband.Server;
import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;
import net.icelane.massband.minecraft.HoloText;

public class Debug_Message extends CommandBase{

	@Override
	public String name() {
		return "message";
	}
	
	@Override
	public void initialize() {
		setAliases("msg");
		setDescription("Debug messages will be shown.");
		setUsage("[true|false]");
		setPermission("massband.debug.message", true);
		setDebugRequired(true);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {	
		if (args.length == 1){
			// enable or disable permissions ...
			Massband.setDebugMessage(args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("1"));
	
			if (Plugin.get().isPermissionsEnabled())
				if (sender instanceof Player) sender.sendMessage("§cDebug: §aShow debug messages");
			else
				if (sender instanceof Player) sender.sendMessage("§cDebug: §cHide debug messages");
			
			return true;
		}
		
		return false;
	}

}
