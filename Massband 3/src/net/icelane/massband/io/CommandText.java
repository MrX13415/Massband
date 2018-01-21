package net.icelane.massband.io;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import net.icelane.massband.Plugin;
import net.icelane.massband.core.Massband;

public class CommandText {

/*
Colors:
	BLACK:         §0
	DARK_BLUE:     §1
	DARK_GREEN:    §2
	DARK_AQUA:     §3
	DARK_RED:      §4
	DARK_PURPLE:   §5
	GOLD:          §6
	GRAY:          §7
	DARK_GRAY:     §8
	BLUE:          §9
	GREEN:         §a
	AQUA:          §b
	RED:           §c
	LIGHT_PURPLE:  §d
	YELLOW:        §e
	WHITE:         §f
	
Effects:
	MAGIC:         §k
	BOLD:          §l
	STRIKETHROUGH: §m
	UNDERLINE:     §n
	ITALIC:        §o
	RESET:         §r
*/

	public static String getWildCardDescription(CommandBase command) {
		return String.format("Gives full access to the %s command.", command.getName());
	}
	
	public static String getIngameOnly(CommandBase command){
		String format = "§cOnly for ingame usage!";
		return format;
	}
	
	public static String getDebugRequired(CommandBase command){
		String format = "§cEnable Debug mode first!";
		return format;
		
	}	
	
	public static String getHelp(CommandBase command, CommandSender sender){
		// output formats 
		String format_header = "\n§aCommand: §7/%1$s§c%2$s §aAliases: §c%3$s";
		String format_desc   = "\n  §7%1$s";
		String format_perm   = "\n  §aPermission: §c%1$s";
		String format_usage  = "\n  §aUsage: §6%1$s";
		String format_cmd    = "  §f - §c%1$s §6%2$s §7%3$s";
		
		// command info
		String label   = command.getName();
		String aliases = command.getAliasesString();
		String usage   = command.getUsage(sender).trim();
		String desc    = command.getDescription(sender).trim();
		String help    = command.getHelp().trim();
		 
		// get list of parent commands
		String parents = "";
		CommandBase parent = command.getParent();
		while (parent != null) {
			parents = parent.getName() + " " + parents;
			parent  = parent.getParent();
		}
		
		// define header and basic output
		String out_header = String.format(format_header, parents, label, aliases);
		String out_desc   = String.format(format_desc, desc);
		String out_perm   = "";
		String out_usage  = (command.getCommands().size() > 0) ? String.format(format_usage, "<command>") : "";
		String out_args   = "";

		// define description
		if (help.length() > 0){
			out_desc = String.format(format_desc, help); 
		}
		
		// define permission
		if ( command.getPermission() != null){
			if (Plugin.get().isPermissionsEnabled()) {
				out_perm = String.format(format_perm, command.getPermission().getName());	
			}
			else if (command.getPermission().getDefault() == PermissionDefault.FALSE ||
					command.getPermission().getDefault() == PermissionDefault.OP) {
				out_perm = String.format(format_perm, "OP");
			}
		}
		
		int visibleCommands = 0;
		
		// define commands (args)
		for (CommandBase cmd : command.getCommands()) {
			// handle the visibility of the command
			switch (cmd.getVisibility()) {
			case Hidden: continue; // The command is hidden so we skip it!
			case Permission:
				if (sender == null) break;
				// if we don't have the permission, skip it ...
				if (!cmd.hasPermission(sender)) continue;
				if (cmd.debugRequired && !Massband.isDebug()) continue;
				break;
			default: break;
			}
			
			// add it ...
			out_args += String.format(format_cmd,
					cmd.getName(),
					cmd.getUsage(sender).trim(),
					cmd.getDescription(sender).trim());

			if (command.getCommands().size() - command.getCommands().indexOf(cmd) > 1) out_args += "\n";
			visibleCommands++;
		}
		if (!out_args.isEmpty()) out_args = "\n" + out_args;
		
		// define usage
		if (usage.length() > 0){
			if (visibleCommands > 0 && !usage.toLowerCase().contains("command")) {
				usage = "<command> | " + usage;
			}
			
			usage = usage.replace("<self>", command.getNames());
			out_usage = String.format(format_usage, usage);
		}
		
		return out_header + out_desc + out_perm + out_usage + out_args;
	}
	
	public static String getFullCommandName(CommandBase command, boolean parentOnly) {
		String parents = "";
		CommandBase parent = command.getParent();
		while (parent != null) {
			parents = parent.getName() + " " + parents;
			parent  = parent.getParent();
		}
		return parentOnly ? parents : String.format("%s %s", command.getName());
	}
	
	public static String getPermissionDenied(CommandBase command){
		return getPermissionDenied(command, command.getPermission());
	}
	
	public static String getPermissionDenied(CommandBase command, Permission permission){
		// output formats 
		String format_header = "§cDENIED §aCommand: §7/%s§c%s %s";
		String format_perm = "§aPermission: §6%s";
		
		// command info
		String label   = command.getName();
		// get full parent command name
		String parents = getFullCommandName(command, true);

		// permission
		String out_perm = "";
		//CommandBase parentcmd = command.getParent();
		//Permission perm = permission;
			
		//while ( parentcmd != null && perm == null){
		//	perm = parentcmd.getPermission();
		//	parentcmd = parentcmd.getParent();
		//}
				
		if ( permission != null){
			if (Plugin.get().isPermissionsEnabled()) {
				out_perm = String.format(format_perm, permission.getName());	
			}
			else if (permission.getDefault() == PermissionDefault.FALSE ||
					permission.getDefault() == PermissionDefault.OP) {
				out_perm = String.format(format_perm, "OP");
			}
		}
				
		// define header and basic output
		String out_header = String.format(format_header, parents, label, out_perm);

		return out_header;
	}
		
}
