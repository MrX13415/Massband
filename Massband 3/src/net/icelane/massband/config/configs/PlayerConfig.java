package net.icelane.massband.config.configs;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.icelane.massband.config.Entry;
import net.icelane.massband.config.EntryTypes.Entry_Boolean;
import net.icelane.massband.config.EntryTypes.Entry_Enum;
import net.icelane.massband.config.EntryTypes.Entry_Long;
import net.icelane.massband.config.PlayerConfigBase;

public class PlayerConfig extends PlayerConfigBase<PlayerConfig> {

	public static PlayerConfig initialize(Player player) {
		return PlayerConfigBase.initialize(player, PlayerConfig.class);
	}

	public static PlayerConfig getDefault() {
		return PlayerConfigBase.getDefault(PlayerConfig.class);
	}
	
	// ---------- CONFIG ---------- //
	
	@Override
	public String defaultName() {
		return "defaults.yml";
	}

	@Override
	public String name(Player player) {
		return String.format("players/%s.yml", player.getUniqueId());
	}
	
	public Entry_Enum<Material> interact_material =
			Entry.define("Interact.Material",
					Material.class,
					Material.STICK, 
					"The material a player needs to hold, to be able to set markers.");
	
	public Entry_Boolean interact_preventAction = 
			Entry.define("Interact.Prevent-Action",
					true,
					"Weather the default action (e.g. \"breaking a block\") should be preventet,",
					"when the \"interact material\" is been holded.");
	
	public Entry_Boolean interact_switchbuttons = 
			Entry.define("Interact.Switch-Buttons",
					false,
					"Inverts actions of the left and right mouse buttons. Usefull for left-handed.");
	
	public Entry_Long interact_doubleClickTimeFrame = 
			Entry.define("Interact.DoubleClick-TimeFrame",
					150L,
					"The time frame, which defines when two clicks are counted as \"double click\".",
					"In Milliseconds.");

}
