package net.icelane.massband.io.commands.massband;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.core.Marker;
import net.icelane.massband.core.Marker.MeasureMode;
import net.icelane.massband.io.CommandBase;
import net.icelane.massband.resources.Messages;
import net.icelane.massband.core.Massband;

public class Massband_Mode extends CommandBase{

	@Override
	public String name() {
		return "mode";
	}
	
	@Override
	public void initialize() {
		setAliases("md", "m");
		setDescription(Messages.getString("Massband_Mode.description")); //$NON-NLS-1$
		setHelp(Messages.getString("Massband_Mode.help")); //$NON-NLS-1$
		setPermission("massband.command.mode", true);
		setUsage(Messages.getString("Massband_Mode.usage")); //$NON-NLS-1$
		setInGameOnly(true);
		setTabList(Messages.getString("Massband_Mode.tablist_blocks"), Messages.getString("Massband_Mode.tablist_vectors")); //$NON-NLS-1$ //$NON-NLS-2$
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
				MeasureMode value = null;
				int valueInt = -1;
				try{valueInt = Integer.valueOf(args[0]);}catch (NumberFormatException ex){}
				
				int index = 0; 
				for (MeasureMode mode : Marker.MeasureMode.values()){
					if (index == valueInt) valueInt = index;
					if (mode.toString().toLowerCase().contains(args[0].toLowerCase().trim())){
						value = mode;
					}
					index++;
				}
				
				if (value == null) value = Marker.MeasureMode.values()[valueInt];  
				
				obj.getMarkers(player.getWorld()).setMode(value);
				obj.getMarkers(player.getWorld()).recalculate();
				
				player.sendMessage(String.format(Messages.getString("Massband_Mode.mode_set"), value.toString().toLowerCase())); //$NON-NLS-1$
			}catch (Exception ex){
				player.sendMessage(Messages.getString("Massband_Mode.argumenterror")); //$NON-NLS-1$
			}
		}else{
			player.sendMessage(String.format(Messages.getString("Massband_Mode.mode"), obj.getMarkers(player.getWorld()).getMode().toString().toLowerCase()));		 //$NON-NLS-1$
		}
		
		return true;
	}

}
