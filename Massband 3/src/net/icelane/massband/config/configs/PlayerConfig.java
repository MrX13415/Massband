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
			Entry.define("interact.material",
					Material.class,
					Material.STICK, 
					"");
	
	public Entry_Boolean interact_preventAction = 
			Entry.define("interact.prevent-action",
					true,
					"");
	
	public Entry_Boolean interact_switchbuttons = 
			Entry.define("interact.switch-buttons",
					false,
					"");
	
	public Entry_Long interact_doubleClickTimeFrame = 
			Entry.define("interact.doubleclick-timeframe",
					150L,
					"ms");

}
