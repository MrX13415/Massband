package net.icelane.massband.core;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import net.icelane.massband.Server;
import net.icelane.massband.config.configs.PlayerConfig;
import net.icelane.massband.minecraft.HoloText;
import net.icelane.massband.resources.Messages;

public class Massband {

	public static final String Interact_Permission = "massband.interact";
	
	private static HashMap<UUID, Massband> list = new HashMap<>();
	
	private static boolean debug;     // debug mode enabled.
	private static boolean debugMsg;  // debug messages are shown.
	
	private Player player;
	private PlayerConfig config;
	private HashMap<UUID, Marker> worldMarkersList = new HashMap<>(); // UUID => World.ID
	private Interact interact;
	private boolean disabled;    // disables massband for this player
	
	private Massband(Player player) {
		this.player = player;
		config(); //initialize config
	}
		
	public static Massband newInstance(Player player){
		Massband obj = new Massband(player);
		list.put(player.getUniqueId(), obj);
		return obj;
	}
	
	public static Massband get(Player player){
		Massband obj = get(player.getUniqueId());
		if (obj == null) obj = newInstance(player);
		return obj;
	}
	
	public static Massband get(UUID uuid){
		return list.get(uuid);
	}
	
	public boolean hasPermission() {
		return canUse(this.player);
	}
	
	public static boolean hasPermission(Player player) {
		return canUse(player);
	}
	
	public static boolean canUse(Player player) {
		boolean perm = player.hasPermission(Interact_Permission);
		Massband obj = get(player);
		
		if (perm && !obj.disabled) return true;
		if (!perm && obj.disabled) return false;
		
		obj.disabled = !perm;	
		if (obj.disabled)  obj.reset();  	
		return false;
	}

	public void load(){
		config().load();
		this.interact = new Interact(this);
	}
	
	public void save(){
		config().save();
	}
	
	public void reset(){
		this.clean(player.getWorld().getUID());
		this.interact = new Interact(this);
	}
	
	public static void removeAllMarkers(CommandSender sender) {
		for (World world : Server.get().getWorlds()) {
			int count = Marker.clean(world);
			if (count == 0) continue; 
			
			Server.logger().info(String.format(Messages.getString("Massband.markers_removed_console"), world.getName(), count)); //$NON-NLS-1$
			if (sender == null) continue;
			if (sender instanceof Player) 
				sender.sendMessage(String.format(Messages.getString("Massband.markers_removed"), world.getName(), count));		 //$NON-NLS-1$
		}
	}
	
	/**
	 * Clean all Players.
	 * @see Massband#clean()
	 */
	public static void cleanAll(){
		for (Entry<UUID, Massband> entry : list.entrySet()){
			entry.getValue().clean();
		}
	}
	
	/**
	 * Removes all markers from all Worlds for this object.
	 * @see Massband#clean(UUID)
	 */
	public void clean(){
		for (UUID key : worldMarkersList.keySet())
			clean(key);
	}	
	
	/**
	 * Removes all markers from given World for this object. 
	 * @param worldUID The unique Id of a world.
	 */
	public void clean(UUID worldUID){
		Marker m = worldMarkersList.get(worldUID);
		if (m == null) return;
		m.removeAll();
		worldMarkersList.put(worldUID, null);
	}	
	
	public boolean hasItem(){
		return getInteract().hasItemInHand(player);
	}
	
	public void interact(PlayerInteractEvent event){
		this.getInteract().interact(event);
	}
	
	public void join(PlayerJoinEvent event){
		load();
	}

	public void quit(PlayerQuitEvent event){
		save();
		clean();
	}
	
	public void teleport(PlayerTeleportEvent event) {
		
	}
	
	public void move(PlayerMoveEvent event){
		HoloText.showOwnerTagsOnPlayerMove(event);
	}

	public void worldChange(World worldFrom){
		if (player.getWorld() != worldFrom){
			getMarkers(worldFrom).hideAll();
			if (this.hasItem()) getMarkers(player.getWorld()).showAll();
		}
	}
	
	public static void chuckLoad(ChunkLoadEvent event){
		if (event.isNewChunk()) return;

		//DEBUG
		if (Massband.debugMessage()) Server.get().getConsoleSender().sendMessage("�e--> Chunk loaded!");
		
		for (UUID uuid : list.keySet()){
			Massband obj = Massband.get(uuid);
			
			if (!obj.getPlayer().isOnline()) continue;
			if (!obj.hasItem()) continue;
			if (obj.getPlayer().getWorld() != event.getWorld()) continue;
			if (obj.getMarkerCount(event.getWorld()) == 0) continue;
			
			// show markers ...
			obj.getMarkers(event.getWorld()).showAll();
		}
	}
	
	public static void chuckUnload(ChunkUnloadEvent event){
		// We need to fix this because, this event can no longer be cancelled.
		// So someone thought it's better to remove the function rather then
		// marking it as deprecated, and just disabled it's implementation.
		//if (event.isCancelled()) return;
		
		for (UUID uuid : list.keySet()){
			Massband obj = Massband.get(uuid);
			
			if (!obj.getPlayer().isOnline()) continue;

			obj.getMarkers(event.getWorld()).hideInChunck(event.getChunk());
				//Server.get().getConsoleSender().sendMessage("�9 --> Markers removed!");
		}
	}
	
	public Player getPlayer() {
		return player;
	}

	public Marker getMarkers(World world){
		Marker m = worldMarkersList.get(world.getUID());
		if (m == null){
			m = new Marker(player, world);
			worldMarkersList.put(world.getUID(), m);
		}
		return m;
	}
	
	public int getMarkerCount(World world){
		Marker m = worldMarkersList.get(world.getUID());
		if (m == null) return 0;
		return m.getCount();
	}
	
	public void hideMarkers(World world){
		Marker m = worldMarkersList.get(world.getUID());
		if (m == null) return;
		m.hideAll();
	}
	
	public void showMarkers(World world){
		Marker m = worldMarkersList.get(world.getUID());
		if (m == null) return;
		m.showAll();
	}

	public Interact getInteract() {
		if (this.interact == null) this.interact = new Interact(this);
		return this.interact;
	}

	public PlayerConfig config() {
		if (config == null) config = PlayerConfig.initialize(player);
		return config;
	}
	
	public static boolean debug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		Massband.debug = debug;
		if (!debug) setDebugMessage(false);
		Server.logger().warning(Messages.getString("Massband.debug") + (debug ? Messages.getString("Massband.debug_enabled") : Messages.getString("Massband.debug_disbabled"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public static boolean debugMessage() {
		return debugMsg;
	}

	public static void setDebugMessage(boolean debugMsg) {
		Massband.debugMsg = debugMsg;
		Server.logger().warning(Messages.getString("Massband.debugmessages") + (debug ? Messages.getString("Massband.debug_enabled") : Messages.getString("Massband.debug_disbabled"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
}
