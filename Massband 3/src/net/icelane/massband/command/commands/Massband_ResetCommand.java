package net.icelane.massband.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.command.CommandBase;
import net.icelane.massband.core.Massband;

public class Massband_ResetCommand extends CommandBase{

	@Override
	public String name() {
		return "reset";
	}
	
	@Override
	public void initialize() {
		setAliases("rst");
		setDescription("Resets your settings to default.");
		setPermissionNode("reset");
		setInGameOnly(true);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String alias, String[] args) {
		return false;
	}
	
	@Override
	public boolean command(Player player, Command cmd, String label, String[] args) {
		Massband obj = Massband.get(player);
		
		obj.reset();
		
		player.sendMessage("§Settings have been reset to default");
		
		return true;
	}

}
