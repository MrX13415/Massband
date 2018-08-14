package net.icelane.massband.io;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import net.icelane.massband.Plugin;
import net.icelane.massband.config.configs.Config;
import net.icelane.massband.core.Massband;
import net.icelane.massband.io.commands.MassbandCommand;
import net.icelane.massband.resources.Messages;

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

	public enum PermissionVisibility{
		/**
		 * Nobody will see the permission in the help message.
		 */
		None,
		/**
		 * Only in the console the permission will be displayed.
		 */
		Console,
		/**
		 * Everyone who is OP will see the permission in the help message.
		 */
		OP,
		/**
		 * Everyone will see the permission in the help message.
		 */
		All
	}
	
	public static String getWildCardDescription(CommandBase command) {
		return String.format(Messages.getString("CommandText.WildCardDescription"), command.getName()); //$NON-NLS-1$
	}
	
	public static String getIngameOnly(CommandBase command){
		String format = Messages.getString("CommandText.OnlyIngame"); //$NON-NLS-1$
		return format;
	}
	
	public static String getDebugRequired(CommandBase command){
		String format = Messages.getString("CommandText.DebugRequired"); //$NON-NLS-1$
		return format;
		
	}	
	
	public static String getInteractPermissionWarning(CommandSender sender) {
		if (sender instanceof Player && !sender.hasPermission(Massband.Interact_Permission))
			return String.format(Messages.getString("CommandText.InteractPermissionMissing"), Massband.Interact_Permission);	 //$NON-NLS-1$
		return "";
	}
	
	public static String getHelp(CommandBase command, CommandSender sender){
		// output formats 
		String format_header  = Messages.getString("CommandText.help_format_header"); //$NON-NLS-1$
		String format_desc    = Messages.getString("CommandText.help_format_description"); //$NON-NLS-1$
		String format_perm    = Messages.getString("CommandText.help_format_permission"); //$NON-NLS-1$
		String format_subPerm = Messages.getString("CommandText.help_format_subpermission"); //$NON-NLS-1$
		String format_usage   = Messages.getString("CommandText.help_format_usage"); //$NON-NLS-1$
		String format_cmd     = Messages.getString("CommandText.help_format_commandentry"); //$NON-NLS-1$
		
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
		String out_usage  = (command.getCommands().size() > 0) ? String.format(format_usage, Messages.getString("CommandText.help_usage_command1")) : ""; //$NON-NLS-1$ //$NON-NLS-2$
		String out_args   = "";

		// define description
		if (help.length() > 0){
			out_desc = String.format(format_desc, help); 
		}
				
		PermissionVisibility permVis = (PermissionVisibility) Config.get().help_permissionVisibility.get();
		boolean showPerms = false;
		switch (permVis) {
		case None:
			break;
		case Console:
		 	showPerms = !(sender instanceof Player); break; 
		case OP:
			showPerms = sender.isOp(); break;
		case All:
			showPerms = true; break;
		default: break;
		}
		
		if (showPerms) {
			// define permission
			if ( command.getPermission() != null){
				if (Plugin.get().isPermissionsEnabled()) {
					out_perm = String.format(format_perm, command.getPermission().getName());	
				}
				else if (command.getPermission().getDefault() == PermissionDefault.FALSE ||
						command.getPermission().getDefault() == PermissionDefault.OP) {
					out_perm = String.format(format_perm, Messages.getString("CommandText.OP")); //$NON-NLS-1$
				}
			}
			
			for (Permission	subPerm : command.getSubPermissions()) {
				if (Plugin.get().isPermissionsEnabled()) {
					out_perm += String.format(format_subPerm, subPerm.getName());	
				}
			}
		}
		
		int visibleCommands = 0;
		
		// define commands (args)
		for (CommandBase cmd : command.getCommands()) {
			if (!cmd.isVisible(sender)) continue;

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
			if (visibleCommands > 0 && !usage.toLowerCase().contains(Messages.getString("CommandText.help_usage_command2"))) { //$NON-NLS-1$
				usage = Messages.getString("CommandText.help_usage_command3") + usage; //$NON-NLS-1$
			}
			
			usage = usage.replace("<self>", command.getNames());
			out_usage = String.format(format_usage, usage);
		}
		
		String infos = "";	
		if (command instanceof MassbandCommand)
			infos += "\n" + Messages.getString("CommandText.help_usage_info");
		
		String warns = "\n" + getInteractPermissionWarning(sender);
		
		return out_header + out_desc + out_perm + out_usage + out_args + infos + warns;
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
		String format_header = Messages.getString("CommandText.permission_denied_header"); //$NON-NLS-1$
		String format_perm = Messages.getString("CommandText.permission_denied_permission"); //$NON-NLS-1$
		
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
				out_perm = String.format(format_perm, Messages.getString("CommandText.OP")); //$NON-NLS-1$
			}
		}
				
		// define header and basic output
		String out_header = String.format(format_header, parents, label, out_perm);

		return out_header;
	}
		
}
