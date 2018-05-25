package net.icelane.massband.io.commands.massband;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;
import net.icelane.massband.io.commands.massband.debug.Debug_Matrix;
import net.icelane.massband.io.commands.massband.debug.Debug_Message;
import net.icelane.massband.io.commands.massband.debug.Debug_OwnerTags;
import net.icelane.massband.io.commands.massband.debug.Debug_Permissions;
import net.icelane.massband.resources.Messages;

public class Massband_Debug extends CommandBase{

	@Override
	public String name() {
		return "debug";
	}
	
	@Override
	public void initialize() {
		setAliases("debug");
		setDescription(Messages.getString("Massband_Debug.description")); //$NON-NLS-1$
		setUsage(Messages.getString("Massband_Debug.usage")); //$NON-NLS-1$
		setPermission("massband.debug", false);

		addCommand(Debug_Message.class);
		addCommand(Debug_Matrix.class);
		addCommand(Debug_OwnerTags.class);
		addCommand(Debug_Permissions.class);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1){
			if (args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("1")){
				Massband.setDebug(true);
				if (sender instanceof Player) sender.sendMessage(Messages.getString("Massband_Debug.enabled")); //$NON-NLS-1$
			}else{
				Massband.setDebug(false);
				if (sender instanceof Player) sender.sendMessage(Messages.getString("Massband_Debug.disabled")); //$NON-NLS-1$
			}
			return true;
		}
		
		return false;
	}

}
