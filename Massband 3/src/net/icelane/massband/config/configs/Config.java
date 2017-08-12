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

	public static Entry_Enum<Material> interact_material   = Entry.define("interact.material", Material.class, Material.STICK, "");
	public static Entry_Boolean interact_preventAction     = Entry.define("interact.prevent-action", true, "");
	public static Entry_Boolean interact_switchbuttons     = Entry.define("interact.switch-buttons", false, "");
	public static Entry_Long interact_doubleClickTimeFrame = Entry.define("interact.doubleclick-timeframe", 200L, "ms");
		
}
