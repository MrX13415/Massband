package net.icelane.massband.io.commands.massband.debug;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.Plugin;
import net.icelane.massband.Server;
import net.icelane.massband.core.Marker;
import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;

public class Debug_Matrix extends CommandBase{
	
	private int size_x;
	private int pos_x;
	private int size_y;
	private int pos_z;
	private int pos_y;
	private int count = 0;
	private int createTask = -1;
	private Location playerLocation;
	
	@Override
	public String name() {
		return "matrix";
	}
	
	@Override
	public void initialize() {
		setDescription("Create matrix of markers.");
		setUsage("<dx> <dy> <y>");
		setPermission("massband.debug.matrix", false);
		setInGameOnly(true);
		setDebugRequired(true);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
		return true;
	}

	@Override
	public boolean command(Player player, Command cmd, String label, String[] args) {
		if (args.length == 3){			
			try {
				size_x = Integer.parseInt(args[0]);
				size_y = Integer.parseInt(args[1]);
				pos_y = Integer.parseInt(args[2]);

			Marker markers = Massband.get(player).getMarkers(player.getWorld());
			
			if (createTask > -1) {
				player.sendMessage("§cAllready runngin: " + count + " / " + (size_x * size_y));
				return true;
			}
			count = 0;;
			pos_x = 0;
			pos_z = 0;
			playerLocation = player.getLocation();
			
			createTask = Server.get().getScheduler().scheduleSyncRepeatingTask(Plugin.get(), new Runnable() {
				@Override
				public void run() {
					int iiii = (size_x * size_y) - count + 1;
					if (iiii > 50) iiii = 10;
					
					for (int i = 0; i < iiii; i++) {
						Location loc = new Location(player.getWorld(),
								playerLocation.getBlockX() + pos_x,
								pos_y,
								playerLocation.getBlockZ() + pos_z);
						markers.add(loc.getBlock(), BlockFace.UP);
						count++;
						
						if (count > (size_x * size_y)) { 
							player.sendMessage("§aMatrix created [" + size_x + " x " + size_y + "] y " + pos_y + " | count: " + (count -1));
							Server.get().getScheduler().cancelTask(createTask);
							createTask = -1;
						} else {
							player.sendMessage("§7Creating matrix " + count + " / " + (size_x * size_y));
						}
						
						pos_x++;
						if (pos_x >= size_x) {
							pos_x = 0;
							pos_z++;
							if (pos_z >= size_y) {
								pos_z = 0;
							}
						}
					}

				}
			}, 0, 1);
		
			return true;
			} catch (Exception e) {
				player.sendMessage("§cInvalid numbers!");
			}
			player.sendMessage("§cUsage: mb _matrix <dx> <dz> <y>");
			return true;
		}
		
		return false;
	}

}
