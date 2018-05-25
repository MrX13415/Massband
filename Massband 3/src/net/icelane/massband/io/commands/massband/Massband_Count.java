package net.icelane.massband.io.commands.massband;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.config.configs.Config;
import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;
import net.icelane.massband.resources.Messages;

public class Massband_Count extends CommandBase{

	@Override
	public String name() {
		return "count";
	}
	
	@Override
	public void initialize() {
		setAliases("markers", "cnt", "c");
		setDescription(Messages.getString("Massband_Count.description")); //$NON-NLS-1$
		setPermission("massband.command.count", true);
		setUsage(Messages.getString("Massband_Count.usage")); //$NON-NLS-1$
		setInGameOnly(true);
		setTabList("10", "1", "-1");
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
		return true;
	}

	@Override
	public boolean command(Player player, Command cmd, String label, String[] args) {
		Massband obj = Massband.get(player);
		
		if (args.length == 1){
			try{
				int value = Integer.valueOf(args[0]);
				if (value < 1 && value > -1 ) value = 1;  // min marker count is 1!
				if (value < 0) value = -1;  // no "limit"
				
				obj.getMarkers(player.getWorld()).setMaxCount(value);
				
				player.sendMessage(String.format(Messages.getString("Massband_Count.count_set"), value == -1 ? Messages.getString("Massband_Count.nolimit") : value)); //$NON-NLS-1$ //$NON-NLS-2$
				
				// player limit
				int playerlimit = Config.get().marker_PlayerMaxCount.get();	
				if (value < 0 || value >= playerlimit)
					player.sendMessage(String.format(Messages.getString("Massband_Count.limit"), playerlimit)); //$NON-NLS-1$
					
			}catch (NumberFormatException ex){
				player.sendMessage(Messages.getString("Massband_Count.argumenterror")); //$NON-NLS-1$
			}
		}else{
			int count = obj.getMarkers(player.getWorld()).getMaxCount();
			player.sendMessage(Messages.getString("Massband_Count.count") + (count == -1 ? Messages.getString("Massband_Count.nolimit") : count));		 //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return true;
	}

}
