package net.icelane.massband.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.command.CommandBase;
import net.icelane.massband.core.Massband;

public class Massband_NoLimitCommand extends CommandBase{

	@Override
	public String name() {
		return "nolimit";
	}
	
	@Override
	public void initialize() {
		setAliases("unlimited", "nolimit", "nol", "nl");
		setDescription("Set the number of markers to be placed to unlimited.");
		setPermissionNode("count");
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
		player.sendMessage("§7Marker count set to: §cNo Limit");
		
		return true;
	}

}
