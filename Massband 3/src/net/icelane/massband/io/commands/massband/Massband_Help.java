package net.icelane.massband.io.commands.massband;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.icelane.massband.Plugin;
import net.icelane.massband.core.Massband;
import net.icelane.massband.io.CommandBase;
import net.icelane.massband.io.CommandText;

public class Massband_Help extends CommandBase{

	@Override
	public String name() {
		return "help";
	}
	
	@Override
	public void initialize() {
		setAliases("info", "i", "h");
		setDescription("Information on how to use massband.");
		setPermission("massband.command.help", true);
	}

	@Override
	public boolean command(CommandSender sender, Command cmd, String label, String[] args) {
		Plugin plugin = Plugin.get();
		sender.sendMessage(String.format("§a%s §cversion §9%s", plugin.getName(), plugin.getDescription().getVersion()));
		if (!isPlayer(sender)) sender.sendMessage("§7(Use ingame for more information.)");
		return true;
	}
	
	@Override
	public boolean command(Player player, Command cmd, String alias, String[] args) {
		// call the "console" part first."
		super.command(player, cmd, alias, args);
		
		Massband obj = Massband.get(player);
		
		String[] helpMsgR = new String[]
				{
					String.format(
					CommandText.getInteractPermissionWarning(player) + 
					" §7Hold the item §9%s §7in any hand to interact with Massband.", obj.getInteract().getMaterial()),
					" ",
					" §6Right click §7on any face of a block to place a §cMarker§7.",
					" §6Right click §7on the same block again to remove it.",
					" §7Add additional markers to start measuring.",
					" §7Add at least 3 markers to measure the area.",
					" §7Use the command §a/mb §6limit §7and §a/mb §6nolimit §7to switch between",
					" §7just two and unlimited markers.",
					" ",
					" §6Left click §7on the first marker (§c#§7) to switch between",
					" §7the different 2D and 3D coordinate modes.",		
					" §6Left click §7on any other marker to switch between axis when",
					" §7using §cBlocks§7 mode. (§7§9(§cX§9)§7 => User defined; (§cX§7) => Auto)",
					" ",
					" §6Double left click §7on any block to toggel between §cblock§7",
					" §7and §cvector§7 measuring mode. §6Double right click §7on a block with",
					" §7no marker to remove all markers. (§a/mb §6clear§7)"
				};
		
		// For "switched" buttons: Make it easier for other languages to get a proper translation.
		String[] helpMsgL = new String[]
				{
					String.format(
					CommandText.getInteractPermissionWarning(player) + 
					" §7Hold the item §9%s §7in any hand to interact with Massband.", obj.getInteract().getMaterial()),
					" ",
					" §6Left click §7on any face of a block to place a §cMarker§7.",
					" §6Left click §7on the same block again to remove it.",
					" §7Add additional markers to start measuring.",
					" §7Add at least 3 markers to measure the area.",
					" §7Use the command §a/mb §6limit §7and §a/mb §6nolimit §7to switch between",
					" §7just two and unlimited markers.",
					" ",
					" §6Right click §7on the first marker (§c#§7) to switch between",
					" §7the different 2D and 3D coordinate modes.",		
					" §6Right click §7on any other marker to switch between axis when",
					" §7using §cBlocks§7 mode. (§7§9(§cX§9)§7 => User defined; (§cX§7) => Auto)",
					" ",
					" §6Double right click §7on any block to toggel between §cblock§7",
					" §7and §cvector§7 measuring mode. §6Double left click §7on a block with",
					" §7no marker to remove all markers. (§a/mb §6clear§7)"
				};
		
		String[] helpMsg = helpMsgR;
		if (obj.getInteract().isSwitchButtons()) helpMsg = helpMsgL;
		
		player.sendMessage("§6How to use Massband:");
		player.sendMessage("§6------------------------------------------------------------");
		player.sendMessage(helpMsg);
		
		return true;
	}
}
