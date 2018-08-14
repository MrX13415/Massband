package net.icelane.massband.io.commands.massband;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;
import net.icelane.massband.resources.Messages;

public class Massband_Default extends CommandBase{

	@Override
	public String name() {
		return "default";
	}
	
	@Override
	public void initialize() {
		setAliases("default", "def", "reset", "rst");
		setDescription(Messages.getString("Massband_Default.description")); //$NON-NLS-1$
		setPermission("massband.command.default", true);
		setInGameOnly(true);
		setVisibility(Visibility.InGameOnly);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String alias, String[] args) {
		return false;
	}
	
	@Override
	public boolean command(Player player, Command cmd, String label, String[] args) {
		Massband obj = Massband.get(player);
		obj.reset();
		
		player.sendMessage(String.format(Messages.getString("Massband_Default.resetmessage"), player.getName())); //$NON-NLS-1$
		
		return true;
	}

}
