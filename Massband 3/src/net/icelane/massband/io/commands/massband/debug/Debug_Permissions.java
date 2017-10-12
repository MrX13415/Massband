package net.icelane.massband.io.commands.massband.debug;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.Plugin;
import net.icelane.massband.Server;
import net.icelane.massband.io.CommandBase;

public class Debug_Permissions extends CommandBase{

	@Override
	public String name() {
		return "permissions";
	}
	
	@Override
	public void initialize() {
		setAliases("permission", "perm");
		setDescription("Enable/Disable Permissions for Massband.");
		setUsage("[true|false]");
		setPermission("massband.debug.permission", true);
		setDebugRequired(true);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {	
		if (args.length == 1){
			// enable or disable permissions ...
			Plugin.get().setPermissionsEnabled(args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("1"));
			Server.logger().warning("Permissions " + (Plugin.get().isPermissionsEnabled() ? "enabled" : "disabled!"));
			
			if (Plugin.get().isPermissionsEnabled())
				if (sender instanceof Player) sender.sendMessage("§cDebug: §6/!\\ §aPermissions enabled");
			else
				if (sender instanceof Player) sender.sendMessage("§cDebug: §6/!\\ §cPermissions disabled!");
			
			return true;
		}
		
		return false;
	}

	@Override
	public boolean command(Player player, Command cmd, String label, String[] args) {
		return true;
	}

}
