package net.icelane.massband.io.commands.massband;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;

public class Massband_NoLimit extends CommandBase{

	@Override
	public String name() {
		return "nolimit";
	}
	
	@Override
	public void initialize() {
		setAliases("unlimited", "nolimit", "nol", "nl");
		setDescription("Set the number of markers to be placed to unlimited.");
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
		
		obj.getMarkers(player.getWorld()).setMaxCount(-1);
		player.sendMessage("§aMarker count set to: §cNo Limit");
		
		return true;
	}

}
