package net.icelane.massband.command;

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

	public static String getHelp(CommandBase command){
		// output formats 
		String format_header = "§aCommand: /%s§c%s §aAliases §c%s";
		String format_desc   = "\n§aUsage: §6%s§7%s";
		String format_cmd    = "§f - §c%s §6%s §7%s";
		
		// command info
		String label   = command.getLabel();
		String aliases = command.getAliasesString();
		String usage   = command.getUsage();
		String decs    = command.getDescription();
		 
		// get list of parent commands
		String parents = "";
		CommandBase parent = command.getParent();
		while (parent != null) {
			parents = parent.getLabel() + " " + parents;
			parent  = parent.getParent();
		}
		
		// define header and basic output
		String out_header = String.format(format_header, parents, label, aliases);
		String out_desc = "";
		String out_args = "";
		
		// define desc
		if (usage.length() > 0){
			if (!usage.endsWith(" ")) usage += " ";
		}else if (command.getCommands().size() > 0 ){
			usage = "<command> ";
		}
		
		if (decs.length() > 0){
			out_desc = String.format(format_desc, usage, decs); 
		}

		// define commands (args)
		for (CommandBase cmd : command.getCommands()) {
			out_args += String.format(format_cmd, cmd.getLabel(), cmd.getUsage(), cmd.getDescription());
			
			if (command.getCommands().size() - command.getCommands().indexOf(cmd) > 1) out_args += "\n";
		}
		
		return out_header + out_desc + "\n" + out_args;
	}
	
	public static String getPermissionDenied(CommandBase command){
		// output formats 
		String format_header = "§cDENIED §aCommand: /%s§c%s §aPermission: §c%s";
		
		// command info
		String label   = command.getLabel();
	
		// get list of parent commands
		String parents = "";
		CommandBase parent = command.getParent();
		while (parent != null) {
			parents = parent.getLabel() + " " + parents;
			parent  = parent.getParent();
		}
		
		// define header and basic output
		String out_header = String.format(format_header, parents, label, command.getPermission());

		return out_header;
	}
		
}
