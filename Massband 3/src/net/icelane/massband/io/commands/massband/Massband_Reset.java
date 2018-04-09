package net.icelane.massband.io.commands.massband;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;

public class Massband_Reset extends CommandBase{

	@Override
	public String name() {
		return "reset";
	}
	
	@Override
	public void initialize() {
		setAliases("rst");
		setDescription("Resets all parameters to your default settings in the current world.");
		setPermission("massband.command.reset", true);
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
		
		player.sendMessage(String.format("§aParameters reset to §9default§a.", player.getName()));
		
		return true;
	}

}
