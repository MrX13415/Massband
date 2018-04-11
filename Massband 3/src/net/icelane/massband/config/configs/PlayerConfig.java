package net.icelane.massband.config.configs;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.icelane.massband.config.Entry;
import net.icelane.massband.config.EntryTypes.Entry_Boolean;
import net.icelane.massband.config.EntryTypes.Entry_Enum;
import net.icelane.massband.config.EntryTypes.Entry_Integer;
import net.icelane.massband.config.EntryTypes.Entry_Long;
import net.icelane.massband.config.PlayerConfigBase;
import net.icelane.massband.core.Marker.BlockAxis;
import net.icelane.massband.core.Marker.MeasureMode;

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
	
	@Override
	public void postInitialize() {
		default_markercount.addValues("-1");
	}
	
	
	public Entry_Integer default_markercount = 
			Entry.define("Default.MarkerCount",
					1,
					"The default count of markers to be placed. (-1 for no limit.)");
	
	public Entry_Enum<MeasureMode> default_mode = 
			Entry.define("Default.Mode",
					MeasureMode.class,
					MeasureMode.BLOCKS,
					"The default mode to use for measurement.");
	
	public Entry_Enum<BlockAxis> default_ignoredAxis =
			Entry.define("Default.IgnoredAxis",
					BlockAxis.class,
					BlockAxis.None,
					"The axis to be ignored by default, during measurement.");
	
	public Entry_Enum<Material> interact_material =
			Entry.define("Interact.Material",
					Material.class,
					Material.STICK, 
					"The material a player needs to hold, to be able to set markers.");
	
	public Entry_Boolean interact_preventAction = 
			Entry.define("Interact.Prevent-Action",
					true,
					"Weather the default action (e.g. \"breaking a block\") should be prevented,",
					"when the \"interact material\" is been held.");
	
	public Entry_Boolean interact_switchbuttons = 
			Entry.define("Interact.Switch-Buttons",
					false,
					"Inverts actions of the left and right mouse buttons. Useful for left-handed.");
	
	public Entry_Long interact_doubleClickTimeFrame = 
			Entry.define("Interact.DoubleClick-TimeFrame",
					150L,
					"The time frame, which defines when two clicks are counted as \"double click\".",
					"In Milliseconds.");

}
