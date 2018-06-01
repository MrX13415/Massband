package net.icelane.massband.io.commands.massband.debug;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.Plugin;
import net.icelane.massband.Server;
import net.icelane.massband.io.CommandBase;
import net.icelane.massband.resources.Messages;

public class Debug_Permissions extends CommandBase{

	@Override
	public String name() {
		return "permissions";
	}
	
	@Override
	public void initialize() {
		setAliases("permission", "perm");
		setDescription(Messages.getString("Debug_Permissions.description")); //$NON-NLS-1$
		setUsage(Messages.getString("Debug_Permissions.usage")); //$NON-NLS-1$
		setPermission("massband.debug.permission", true);
		setDebugRequired(true);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {	
		if (args.length == 1) {
			// enable or disable permissions ...
			Plugin.get().setPermissionsEnabled(args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("1"));
			Server.logger().warning(Messages.getString("Debug_Permissions.permission_console") + (Plugin.get().isPermissionsEnabled() ? Messages.getString("Debug_Permissions.permission_console_enabled") : Messages.getString("Debug_Permissions.permission_console_disabled"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			
			if (Plugin.get().isPermissionsEnabled()) {
				if (sender instanceof Player) sender.sendMessage(Messages.getString("Debug_Permissions.permission_enabled")); //$NON-NLS-1$
			} else {
				if (sender instanceof Player) sender.sendMessage(Messages.getString("Debug_Permissions.permission_disabled")); //$NON-NLS-1$
			}
			
			return true;
		}
		
		return false;
	}

}
