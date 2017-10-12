package net.icelane.massband.io.commands.massband;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;

public class Massband_Clear extends CommandBase{

	@Override
	public String name() {
		return "clear";
	}
	
	@Override
	public void initialize() {
		setAliases("clr", "remove");
		setDescription("Removes all markers");
		setPermission("massband.command.clear", true);
		setInGameOnly(true);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String alias, String[] args) {
		return false;
	}
	
	@Override
	public boolean command(Player player, Command cmd, String label, String[] args) {
		Massband obj = Massband.get(player);
		
		obj.getMarkers(player.getWorld()).removeAll();
		
		player.sendMessage("�7All markers have been removed");
		
		return true;
	}

}