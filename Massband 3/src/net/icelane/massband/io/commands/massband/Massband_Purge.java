package net.icelane.massband.io.commands.massband;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;
import net.icelane.massband.resources.Messages;

public class Massband_Purge extends CommandBase {

	@Override
	public String name() {
		return "purge";
	}

	@Override
	public void initialize() {
		setAliases("prg");
		setDescription(Messages.getString("Massband_Purge.description")); //$NON-NLS-1$
		setPermission("massband.command.purge", PermissionDefault.OP);
		setVisibility(Visibility.Permission);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String alias, String[] args) {
		sender.sendMessage(Messages.getString("Massband_Purge.purge")); //$NON-NLS-1$
		sender.sendMessage(Messages.getString("Massband_Purge.purge_info")); //$NON-NLS-1$
		Massband.removeAllMarkers(sender);
		sender.sendMessage(Messages.getString("Massband_Purge.purge_done")); //$NON-NLS-1$
		return true;
	}
	
}
