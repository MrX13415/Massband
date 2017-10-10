package net.icelane.massband.command;

import org.bukkit.command.CommandSender;

import net.icelane.massband.Plugin;

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

	public static String getIngameOnly(CommandBase command){
		String format = "§cOnly for ingame usage!";
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
		String usage   = command.getUsage();
		String desc    = command.getDescription();
		String help    = command.getHelp();
		 
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
		String out_perm   = command.isOP() ? String.format(format_perm, "OP") : "";
		String out_usage  = (command.getCommands().size() > 0) ? String.format(format_usage, "<command>") : "";
		String out_args   = "";

		// define description
		if (help.length() > 0){
			out_desc = String.format(format_desc, help); 
		}
		
		// define permission
		if ( Plugin.get().isPermissionsEnabled() && command.getPermission() != null && !command.isOpOnly()){
			out_perm = String.format(format_perm, command.getPermission());
		}
				
		// define usage
		if (usage.length() > 0){
			usage = usage.replace("<command>", command.getNames());
			out_usage = String.format(format_usage, usage);
		}

		// define commands (args)
		for (CommandBase cmd : command.getCommands()) {
			// handle the visibility of the command
			switch (cmd.getVisibility()) {
			case Hidden: continue; // The command is hidden so we skip it!
			case Permission:
				if (sender == null) break;
				// if we don't have the permission, skip it ...
				if (!cmd.hasPermission(sender)) continue;
				break;
			default: break;
			}
			
			out_args += String.format(format_cmd, cmd.getName(), cmd.getUsage(), cmd.getDescription());
			
			if (command.getCommands().size() - command.getCommands().indexOf(cmd) > 1) out_args += "\n";
		}
		
		return out_header + out_desc + out_perm + out_usage + out_args;
	}
	
	public static String getPermissionDenied(CommandBase command){
		// output formats 
		String format_header = "§cDENIED §aCommand: /%s§c%s §aPermission: §c%s";
		
		// command info
		String label   = command.getName();
	
		// get list of parent commands
		String parents = "";
		CommandBase parent = command.getParent();
		while (parent != null) {
			parents = parent.getName() + " " + parents;
			parent  = parent.getParent();
		}
		
		// define header and basic output
		String out_header = String.format(format_header, parents, label, command.isOpOnly() ? "OP" : command.getPermission());

		return out_header;
	}
		
}
