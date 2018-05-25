package net.icelane.massband.io.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.Plugin;
import net.icelane.massband.Server;
import net.icelane.massband.config.configs.Config;
import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;
import net.icelane.massband.io.commands.massband.Massband_Clear;
import net.icelane.massband.io.commands.massband.Massband_Settings;
import net.icelane.massband.resources.Messages;
import net.icelane.massband.io.commands.massband.Massband_Count;
import net.icelane.massband.io.commands.massband.Massband_Debug;
import net.icelane.massband.io.commands.massband.Massband_Help;
import net.icelane.massband.io.commands.massband.Massband_Limit;
import net.icelane.massband.io.commands.massband.Massband_Mode;
import net.icelane.massband.io.commands.massband.Massband_NoLimit;
import net.icelane.massband.io.commands.massband.Massband_Default;


public class MassbandCommand extends CommandBase{

	@Override
	public String name() {
		return "massband";
	}
	
	@Override
	public void initialize() {
		addCommand(Massband_Help.class);
		addCommand(Massband_Limit.class);
		addCommand(Massband_NoLimit.class);
		addCommand(Massband_Count.class);
		addCommand(Massband_Mode.class);
		addCommand(Massband_Clear.class);
		addCommand(Massband_Default.class);
		addCommand(Massband_Settings.class);
		addCommand(Massband_Debug.class);
	}
	
	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length <= 0) return false;
		
		if (args.length == 1) {
			//******* Easteregg *******
			if (args[0].equalsIgnoreCase("cat") || args[0].equalsIgnoreCase("meow") || args[0].equalsIgnoreCase(":3")){
				sender.sendMessage(Messages.getString("MassbandCommand.meow")); //$NON-NLS-1$
				return true;
			}
			//*************************
	
			//**** Hidden Features ****
			if (args[0].equalsIgnoreCase("_colors") || args[0].equalsIgnoreCase("_color")){
				sender.sendMessage(Messages.getString("MassbandCommand.colors")); //$NON-NLS-1$
				return true;
			}
			
			if (args[0].equalsIgnoreCase("_disable") && sender.isOp()){
				sender.sendMessage(Messages.getString("MassbandCommand.massband_disabled")); //$NON-NLS-1$
				Plugin.get().disable();
				return true;
			}
			
			if (args[0].equalsIgnoreCase("_load") && sender.isOp()){
				Config.get().load();
				sender.sendMessage(Messages.getString("MassbandCommand.config_loaded")); //$NON-NLS-1$
				return true;
			}
			
			if (args[0].equalsIgnoreCase("_save") && sender.isOp()){
				Config.get().save();
				sender.sendMessage(Messages.getString("MassbandCommand.config_saved")); //$NON-NLS-1$
				return true;
			}
			
			if (args[0].equalsIgnoreCase("_clean") && sender.isOp()){
				sender.sendMessage(Messages.getString("MassbandCommand.cleanup")); //$NON-NLS-1$
				Massband.removeAllMarkers(sender);
				sender.sendMessage(Messages.getString("MassbandCommand.cleanup_done")); //$NON-NLS-1$
				return true;
			}
			//*************************
		}
		
		if (args.length == 2) {
			//**** Hidden Features ****
			if (args[0].equalsIgnoreCase("_load") && sender.isOp()){
				Player player = Server.get().getPlayer(args[1].trim());
				if (player != null)
				{
					Massband.get(player).config().load();
					sender.sendMessage(String.format(Messages.getString("MassbandCommand.playerconfig_loaded"), player.getName())); //$NON-NLS-1$
				}else {
					sender.sendMessage(String.format(Messages.getString("MassbandCommand.playernotfound") + args[1].trim()));		 //$NON-NLS-1$
				}
				return true;
			}
			
			
			if (args[0].equalsIgnoreCase("_save") && sender.isOp()){
				Player player = Server.get().getPlayer(args[1].trim());
				if (player != null)
				{
					Massband.get(player).config().save();
					sender.sendMessage(String.format(Messages.getString("MassbandCommand.playerconfig_saved"), player.getName())); //$NON-NLS-1$
				}else {
					sender.sendMessage(String.format(Messages.getString("MassbandCommand.playernotfound"), args[1].trim()));		 //$NON-NLS-1$
				}
				return true;
			}
			//*************************
		}
		
		return false;
	}

}
