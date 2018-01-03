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
		return config;
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
		return new File(getBasePath(), name(getPlayer()));
	}
	
	public Player getPlayer() {
		return Server.get().getPlayer(uuid);
	}
	
	protected void setPlayer(Player player) {
		this.uuid = player.getUniqueId();
	}

}
