package net.icelane.massband.io.commands.massband.debug;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;
import net.icelane.massband.resources.Messages;

public class Debug_Message extends CommandBase{

	@Override
	public String name() {
		return "message";
	}
	
	@Override
	public void initialize() {
		setAliases("msg");
		setDescription(Messages.getString("Debug_Message.description")); //$NON-NLS-1$
		setUsage(Messages.getString("Debug_Message.usage")); //$NON-NLS-1$
		setPermission("massband.debug.message", true);
		setDebugRequired(true);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {	
		if (args.length == 1){
			// enable or disable permissions ...
			Massband.setDebugMessage(args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("1"));
	
			if (Massband.debugMessage()) {
				if (sender instanceof Player) sender.sendMessage(Messages.getString("Debug_Message.show")); //$NON-NLS-1$
			} else {
				if (sender instanceof Player) sender.sendMessage(Messages.getString("Debug_Message.hide")); //$NON-NLS-1$
			}
			
			return true;
		}
		
		return false;
	}

}
