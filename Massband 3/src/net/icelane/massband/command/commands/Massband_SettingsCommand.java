package net.icelane.massband.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.command.CommandBase;
import net.icelane.massband.core.Massband;

public class Massband_SettingsCommand extends CommandBase{

	@Override
	public String name() {
		return "settings";
	}
	
	@Override
	public void initialize() {
		setAliases("setting", "set");
		setDescription("Change various settings for you personal.");
		setPermissionNode("settings");
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
