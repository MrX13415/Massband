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
import net.icelane.massband.resources.Messages;

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
		setDescription(Messages.getString("Debug_Matrix.description")); //$NON-NLS-1$
		setUsage(Messages.getString("Debug_Matrix.usage")); //$NON-NLS-1$
		setPermission("massband.debug.matrix", true);
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
				player.sendMessage(String.format(Messages.getString("Debug_Matrix.running"), count, (size_x * size_y))); //$NON-NLS-1$
				return true;
			}
			
			if (markers.getMaxCount() != -1) {
				player.sendMessage(Messages.getString("Debug_Matrix.markerinfo")); //$NON-NLS-1$
				return true;
			}
			
			count = 0;
			pos_x = 0;
			pos_z = 0;
			playerLocation = player.getLocation();
			
			createTask = Server.get().getScheduler().scheduleSyncRepeatingTask(Plugin.get(), new Runnable() {
				@Override
				public void run() {
					// Only place a number of markers per "run" of the task. 
					int perRunCount = (size_x * size_y) - count + 1;
					if (perRunCount > 20) perRunCount = 20;
					
					for (int i = 0; i < perRunCount; i++) {
						// place a marker ...
						Location loc = new Location(player.getWorld(),
								playerLocation.getBlockX() + pos_x,
								pos_y,
								playerLocation.getBlockZ() + pos_z);			
						markers.add(loc.getBlock(), BlockFace.UP);
						count++;
							
						if (createTask == -1) {
							player.sendMessage(String.format(Messages.getString("Debug_Matrix.created"), size_x, size_y, pos_y, (count -1))); //$NON-NLS-1$
							break;
						}
						
						if (count > (size_x * size_y) || markers.isCountLimitReached()) { 
							Server.get().getScheduler().cancelTask(createTask);
							createTask = -1; // Cancel task ...
						}

						player.sendMessage(String.format(Messages.getString("Debug_Matrix.creating"), count, (size_x * size_y))); //$NON-NLS-1$
						
						
						pos_x++;
						// row completed: Move to next ...
						if (pos_x >= size_x) {
							pos_x = 0;
							pos_z++;
							//Matrix completed: Place the last marker on the first marker (#).
							if (pos_z >= size_y) {
								pos_z = 0;
							}
						}
					}

				}
			}, 0, 1);
		
			return true;
			} catch (Exception e) {
				player.sendMessage(Messages.getString("Debug_Matrix.argumenterror")); //$NON-NLS-1$
			}
			player.sendMessage(Messages.getString("Debug_Matrix.usage_info")); //$NON-NLS-1$
			return true;
		}
		
		return false;
	}

}
