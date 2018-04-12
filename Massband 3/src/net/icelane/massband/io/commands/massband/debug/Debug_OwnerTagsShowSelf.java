package net.icelane.massband.io.commands.massband.debug;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.Plugin;
import net.icelane.massband.Server;
import net.icelane.massband.io.CommandBase;
import net.icelane.massband.minecraft.HoloText;

public class Debug_OwnerTagsShowSelf extends CommandBase{

	@Override
	public String name() {
		return "OwnerTagsShowSelf";
	}
	
	@Override
	public void initialize() {
		setAliases("OwnerTagsShowSelf");
		setDescription("Owner tags will be shown to the owner as well.");
		setUsage("[true|false]");
		setPermission("massband.debug.ownertagsshowself", true);
		setDebugRequired(true);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {	
		if (args.length == 1){
			// enable or disable permissions ...
			HoloText.setOwnerTagsShowSelf(args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("1"));
			Server.logger().warning("OwnerTagsShowSelf " + (Plugin.get().isPermissionsEnabled() ? "enabled" : "disabled!"));
			
			if (Plugin.get().isPermissionsEnabled())
				if (sender instanceof Player) sender.sendMessage("§cDebug: §aShow owner tags to yourself, enabled");
			else
				if (sender instanceof Player) sender.sendMessage("§cDebug: §cShow owner tags to yourself, disabled");
			
			return true;
		}
		
		return false;
	}

}
