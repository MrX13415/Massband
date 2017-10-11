package net.icelane.massband.command.commands;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.Plugin;
import net.icelane.massband.Server;
import net.icelane.massband.command.CommandBase;
import net.icelane.massband.config.configs.Config;
import net.icelane.massband.core.Marker;
import net.icelane.massband.core.Massband;


public class MassbandCommand extends CommandBase{

	@Override
	public String name() {
		return "massband";
	}
	
	@Override
	public void initialize() {
		addCommand(Massband_ClearCommand.class);
		addCommand(Massband_LimitCommand.class);
		addCommand(Massband_NoLimitCommand.class);
		addCommand(Massband_CountCommand.class);
		addCommand(Massband_InfoCommand.class);
		addCommand(Massband_ModeCommand.class);
		addCommand(Massband_ResetCommand.class);
		addCommand(Massband_SettingsCommand.class);
		addCommand(Massband_DebugCommand.class);
	}
	
	//TODO move into it's own command class
	int bxs;
	int bx;
	int bzs;
	int bz;
	int by;
	int count = 0;
	int ctask = -1;
	Location xloc;
	
	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {

		//**** DEBUG ****
		if (sender.isOp() && args.length == 2 && args[0].equalsIgnoreCase("debug")){
			if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("1")){
				Massband.setDebug(true);
				sender.sendMessage("§c(i) §6Debug enabled");
			}else{
				Massband.setDebug(false);
				sender.sendMessage("§c(i) §6Debug disabled");
			}
			return true;
		}
		//***************
		
		if (Massband.isDebug() && sender.isOp() && 
				args.length == 4 && args[0].equalsIgnoreCase("_matrix")){
			
			try {
				bxs = Integer.parseInt(args[1]);
				bzs = Integer.parseInt(args[2]);
				by = Integer.parseInt(args[3]);

			if (!(sender instanceof Player)) return false;
			Player player = (Player)sender;
			Marker markers = Massband.get(player).getMarkers(player.getWorld());
			
			if (ctask > -1) {
				sender.sendMessage("§cAllready runngin: " + count + " / " + (bxs * bzs));
				return true;
			}
			count = 0;;
			bx = 0;
			bz = 0;
			xloc = player.getLocation();
			
			ctask = Server.get().getScheduler().scheduleSyncRepeatingTask(Plugin.get(), new Runnable() {
				@Override
				public void run() {
					int iiii = (bxs * bzs) - count + 1;
					if (iiii > 50) iiii = 10;
					
					for (int i = 0; i < iiii; i++) {
						Location loc = new Location(player.getWorld(),
								xloc.getBlockX() + bx,
								by,
								xloc.getBlockZ() + bz);
						markers.add(loc.getBlock(), BlockFace.UP);
						count++;
						
						if (count > (bxs * bzs)) { 
							sender.sendMessage("§aMatrix created [" + bxs + " x " + bzs + "] y " + by + " | count: " + (count -1));
							Server.get().getScheduler().cancelTask(ctask);
							ctask = -1;
						} else {
							sender.sendMessage("§7Creating matrix " + count + " / " + (bxs * bzs));
						}
						
						bx++;
						if (bx >= bxs) {
							bx = 0;
							bz++;
							if (bz >= bzs) {
								bz = 0;
							}
						}
					}

				}
			}, 0, 1);
		
			return true;
			} catch (Exception e) {
				sender.sendMessage("§cInvalid numbers!");
			}
			sender.sendMessage("§cUsage: mb _matrix <dx> <dz> <y>");
			return true;
		}
		
		//**** EASTEREGG ****
		if (args.length == 1 && (args[0].equalsIgnoreCase("cat") || args[0].equalsIgnoreCase("meow") || args[0].equalsIgnoreCase(":3"))){
			sender.sendMessage("§aMeow §c:3");
			return true;
		}
		//*******************

		//**** Hidden features ****
		if (sender.isOp() && args.length == 1 && args[0].equalsIgnoreCase("_load")){
			Config.load();
			sender.sendMessage("§c(i) §6Massband config loaded");
			return true;
		}
		
		if (sender.isOp() && args.length == 1 && args[0].equalsIgnoreCase("_save")){
			Config.save();
			sender.sendMessage("§c(i) §6Massband config saved");
			return true;
		}
		
		if (sender.isOp() && args.length == 1 && args[0].equalsIgnoreCase("_disable")){
			sender.sendMessage("§c/!\\ §6Massband will be disabled!");
			Plugin.get().disable();
			return true;
		}

		if (args.length == 1 && (args[0].equalsIgnoreCase("_colors") || args[0].equalsIgnoreCase("_color"))){
			sender.sendMessage("§7Colors: §11 §22 §33 §44 §55 §66 §77 §88 §99 §aa §bb §cc §dd §ee §ff");
			return true;
		}
		//*******************
		
		return false;
	}
	
}
