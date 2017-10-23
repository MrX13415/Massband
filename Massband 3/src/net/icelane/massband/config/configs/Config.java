package net.icelane.massband.config.configs;

import org.bukkit.Material;

import net.icelane.massband.config.ConfigBase;
import net.icelane.massband.config.Entry;
import net.icelane.massband.config.EntryTypes.*;

public class Config extends ConfigBase {

	@Override
	public String name() {
		return "config.yml";
	}

	public static Entry_Double defaultEntityLineOffset = Entry.define("marker.LineOffset", 0.3, "");	
	
	public static Entry_Boolean marker_showOwnerTags     = Entry.define("marker.OwnerTags.Enable", true, "");
	public static Entry_Long defaultOwnerHideTicks = Entry.define("marker.OwnerTags.HideTicks", 20L * 3, "ticks (20 tick => 1 sec)");
	public static Entry_Long defaultOwnerShowDelayTicks = Entry.define("marker.OwnerTags.DelayTicks", 10L, "ticks (1 tick => 50 ms)");

	
}
