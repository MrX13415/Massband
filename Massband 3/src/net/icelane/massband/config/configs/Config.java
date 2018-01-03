package net.icelane.massband.config.configs;

import net.icelane.massband.config.ConfigBase;
import net.icelane.massband.config.Entry;
import net.icelane.massband.config.EntryTypes.Entry_Boolean;
import net.icelane.massband.config.EntryTypes.Entry_Double;
import net.icelane.massband.config.EntryTypes.Entry_Long;

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

	public Entry_Double defaultEntityLineOffset = Entry.define("marker.LineOffset", 0.3, "");	
	
	public Entry_Boolean marker_showOwnerTags     = Entry.define("marker.OwnerTags.Enable", true, "");
	public Entry_Long defaultOwnerHideTicks = Entry.define("marker.OwnerTags.HideTicks", 20L * 3, "ticks (20 tick => 1 sec)");
	public Entry_Long defaultOwnerShowDelayTicks = Entry.define("marker.OwnerTags.DelayTicks", 10L, "ticks (1 tick => 50 ms)");

}
