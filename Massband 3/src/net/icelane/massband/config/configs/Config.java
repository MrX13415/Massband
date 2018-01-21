package net.icelane.massband.config.configs;

import net.icelane.massband.config.ConfigBase;
import net.icelane.massband.config.Entry;
import net.icelane.massband.config.EntryTypes.Entry_Boolean;
import net.icelane.massband.config.EntryTypes.Entry_Double;
import net.icelane.massband.config.EntryTypes.Entry_Enum;
import net.icelane.massband.config.EntryTypes.Entry_Long;
import net.icelane.massband.io.CommandText;

public class Config extends ConfigBase<Config> {

	private static Config config; 

	public static void initialize() {
		config = ConfigBase.initialize(Config.class);
	}
	
	public static Config get() {
		return config;
	}
	
	// ---------- CONFIG ---------- //
	
	@Override
	public String name() {
		return "config.yml";
	}

	public Entry_Boolean cleanupEnabled = 
			Entry.define("Cleanup.Enabled",
					true,
					"This will remove any leftover \"markers\" on server startup.",
					"This will make sure there arn't any markers without an associated player.");
	
	public Entry_Double defaultEntityLineOffset = 
			Entry.define("Marker.LineOffset",
					0.3,
					"The space between multiple lines of a marker.");	
	
	public Entry_Boolean marker_showOwnerTags = 
			Entry.define("Marker.OwnerTags.Enable",
					true,
					"If another player is nearby, an \"owner tag\" will be shown for each marker,",
					"displaying the name of the player the marker belongs to.");
	
	public Entry_Long defaultOwnerHideTicks = 
			Entry.define("Marker.OwnerTags.HideTicks",
					20L * 3,
					"The minimum time an onwer tag should be visible.",
					"In Ticks. (20 Ticks => 1 sec)");
	
	public Entry_Long defaultOwnerShowDelayTicks = 
			Entry.define("Marker.OwnerTags.DelayTicks",
					10L,
					"The minimum delay before showing the owner tags again.",
					"In Ticks. (10 Ticks => 500 ms)");
	
	public Entry_Enum<CommandText.PermissionVisibility> help_permissionVisibility = 
			Entry.define("Help.PermissionVisibility",
					CommandText.PermissionVisibility.class,
					CommandText.PermissionVisibility.OP,
					"This defines who should be able to see the permission in the help message.");

}
