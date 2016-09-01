package net.icelane.massband;


import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;


public class Config {

	public static Entry interact_material      = new Entry("interact.material", Material.STICK.toString());
	public static Entry interact_preventAction = new Entry("interact.prevent-action", false);
	public static Entry interact_mouseButton   = new Entry("interact.mouse-button", "");
	
	public static Entry player_maxVisibleMarkers = new Entry("player.max-visible-markers", 100);
	
	
	public static void defaults(){
		addDefault(interact_material);
		addDefault(interact_preventAction);
	}
	
	public static void addDefault(Entry entry){
		FileConfiguration config = Plugin.config();
		config.addDefault(entry.path(), entry.defaultValue());
	}
	
	public static Object get(Entry entry){
		FileConfiguration config = Plugin.config();
		return config.get(entry.path());
	}
	
	public static void save(){
		Plugin.config().options().copyDefaults(true);
		Plugin.get().saveConfig();
	}
	
	public static class Entry{
		
		private String path    = "";
		private String comment = "";
		private Object defaultValue;
		
		public Entry(String path, Object value) {
			this(path, value, "");
		}

		public Entry(String path, Object value, String comment) {
			this.path = path;
			this.defaultValue = value;
			this.comment = comment;
		}

		public String path() {
			return path;
		}

		public String comment() {
			return comment;
		}
		public Object defaultValue() {
			return defaultValue;
		}

		public Object value() {
			return get(this);
		}
		
		public String valueStr() {
			return get(this).toString();
		}
		
		public void setPath(String path) {
			this.path = path;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public void setDefaultValue(Object value) {
			this.defaultValue = value;
		}
		
	}
}
