package net.icelane.massband.io.commands.massband;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.Plugin;
import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;
import net.icelane.massband.io.CommandText;
import net.icelane.massband.resources.Messages;

public class Massband_Help extends CommandBase{

	@Override
	public String name() {
		return "help";
	}
	
	@Override
	public void initialize() {
		setAliases("info", "i", "h");
		setDescription(Messages.getString("Massband_Help.description")); //$NON-NLS-1$
		setPermission("massband.command.help", true);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
		Plugin plugin = Plugin.get();
		sender.sendMessage(String.format(Messages.getString("Massband_Help.version"), plugin.getName(), plugin.getDescription().getVersion())); //$NON-NLS-1$
		if (!isPlayer(sender)) sender.sendMessage(Messages.getString("Massband_Help.useIngameInfo")); //$NON-NLS-1$
		return true;
	}
	
	@Override
	public boolean command(Player player, Command cmd, String alias, String[] args) {
		// call the "console" part first."
		super.command(player, cmd, alias, args);
		
		Massband obj = Massband.get(player);
		
		String[] helpMsgR = new String[]
				{
					String.format(
					CommandText.getInteractPermissionWarning(player) + 
					Messages.getString("Massband_Help.helpMessage_right_line_1"), obj.getInteract().getMaterial()), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_right_line_2"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_right_line_3"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_right_line_4"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_right_line_5"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_right_line_6"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_right_line_7"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_right_line_8"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_right_line_9"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_right_line_10"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_right_line_11"),		 //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_right_line_12"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_right_line_13"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_right_line_14"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_right_line_15"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_right_line_16"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_right_line_17") //$NON-NLS-1$
				};
		
		// For "switched" buttons: Make it easier for other languages to get a proper translation.
		String[] helpMsgL = new String[]
				{
					String.format(
					CommandText.getInteractPermissionWarning(player) + 
					Messages.getString("Massband_Help.helpMessage_left_line_1"), obj.getInteract().getMaterial()), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_left_line_2"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_left_line_3"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_left_line_4"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_left_line_5"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_left_line_6"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_left_line_7"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_left_line_8"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_left_line_9"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_left_line_10"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_left_line_11"),		 //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_left_line_12"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_left_line_13"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_left_line_14"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_left_line_15"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_left_line_16"), //$NON-NLS-1$
					Messages.getString("Massband_Help.helpMessage_left_line_17") //$NON-NLS-1$
				};
		
		String[] helpMsg = helpMsgR;
		if (obj.getInteract().isSwitchButtons()) helpMsg = helpMsgL;
		
		player.sendMessage(Messages.getString("Massband_Help.helpMessage_header")); //$NON-NLS-1$
		player.sendMessage(Messages.getString("Massband_Help.helpMessage_header_seperator")); //$NON-NLS-1$
		player.sendMessage(helpMsg);
		
		return true;
	}
}
