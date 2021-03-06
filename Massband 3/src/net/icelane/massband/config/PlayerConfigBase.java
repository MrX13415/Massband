package net.icelane.massband.config;

import java.io.File;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.icelane.massband.Server;

public abstract class PlayerConfigBase<T extends PlayerConfigBase<T>> extends ConfigBase<T> {

	private UUID uuid;
	
	public static <T extends PlayerConfigBase<T>> T initialize(Player player, Class<T> cfgclass) {
		T config = ConfigBase.initialize(cfgclass);
		config.setPlayer(player);
		config.loadDefault();
		config.initializeEnties();
		return config;
	}
	
	public ConfigBase<T> getDefaultConfig() {
		return PlayerConfigBase.getDefault(getConfigClass());	
	}
	
	public static <T extends PlayerConfigBase<T>> T getDefault(Class<T> cfgclass) {
		return PlayerConfigBase.initialize(null, cfgclass);
	}
	
	public void initializeEnties() {
		if (isDefault()) return;
			
		
//		T defaultConfig = getDefault(getConfigClass());
//		
//		
//		
//		for (Field defaultField : defaultConfig.getClass().getDeclaredFields()) {
//			if (!Entry.class.isAssignableFrom(defaultField.getType())) continue;
//			
//			try {
//				Field configField = this.getClass().getField(defaultField.getName());		
//				Entry<?> defaultEntry = (Entry<?>) defaultField.get(defaultConfig);
//				Entry<?> configEntry = (Entry<?>) configField.get(this);
//				
//				configEntry.setDefaultEntry(defaultEntry);			
//			} catch (Exception ex) {
//				Server.logger().warning("Unable to initialize default entry.");
//			}
//		}	
	}
	
	public boolean isDefault() {
		return uuid == null;
	}
	
	@Override
	public String name() {
		return defaultName();
	}
	
	/**
	 * The name and extension of this config file. May also specify a relative
	 * path.<br>
	 * <br>
	 * Example: <code>"things/config.yml"</code>
	 * 
	 * @return the name and the extension as string
	 */
	public abstract String defaultName();
	
	/**
	 * Use to alter the defined entries after they are initialized.
	 */
	public void postInitialize() {
		
	}
	
	/**
	 * The name and extension of this config file. May also specify a relative
	 * path.<br>
	 * <br>
	 * Example: <code>"things/config.yml"</code>
	 * @param player The player object
	 * @return the name and the extension as string
	 */
	public abstract String name(Player player);

	@Override
	public T load() {
		process(getFilePath(), false, false);  // load
		postInitialize();
		return getConfigClass().cast(this);
	}
	
	@Override
	public void save() {
		process(getFilePath(), true, false);  // save
	}
	
	public T loadDefault() {
		process(super.getFilePath(), false, true);  // load
		return getConfigClass().cast(this);
	}

	public void saveDefault() {
		process(super.getFilePath(), true, true);  // save
	}
	
	@Override
	public File getFilePath(){
		if (getPlayer() == null) return super.getFilePath();
		return new File(getBasePath(), name(getPlayer()));
	}
	
	public Player getPlayer() {
		return Server.get().getPlayer(uuid);
	}
	
	protected void setPlayer(Player player) {
		if (player == null) return;
		this.uuid = player.getUniqueId();
	}

}
