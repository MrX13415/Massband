package net.icelane.massband.io.commands.massband;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.icelane.massband.config.configs.Config;
import net.icelane.massband.io.CommandBase;
import net.icelane.massband.resources.Messages;

public class Massband_Load extends CommandBase {

	@Override
	public String name() {
		return "load";
	}

	@Override
	public void initialize() {
		setAliases("reload");
		setDescription(Messages.getString("Massband_Load.description")); //$NON-NLS-1$
		setPermission("massband.command.load", PermissionDefault.OP);
		setVisibility(Visibility.Permission);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String alias, String[] args) {
		Config.get().load();
		sender.sendMessage(Messages.getString("Massband_Load.load_done")); //$NON-NLS-1$
		return true;
	}

}
