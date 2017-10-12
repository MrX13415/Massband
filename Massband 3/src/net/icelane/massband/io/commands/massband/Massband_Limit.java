package net.icelane.massband.io.commands.massband;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;

public class Massband_Limit extends CommandBase{

	@Override
	public String name() {
		return "limit";
	}
	
	@Override
	public void initialize() {
		setAliases("limited", "limit", "lm");
		setDescription("Set the number of markers to be placed to 1.");
		setPermission("massband.command.count", true);
		setInGameOnly(true);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
		return true;
	}

	@Override
	public boolean command(Player player, Command cmd, String label, String[] args) {
		Massband obj = Massband.get(player);

		obj.getMarkers(player.getWorld()).setMaxCount(1);
		player.sendMessage("§7Marker count set to: §c1");
		
		return true;
	}

}
