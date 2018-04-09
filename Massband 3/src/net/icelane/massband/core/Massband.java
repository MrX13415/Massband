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

public class Massband {

	private static HashMap<UUID, Massband> list = new HashMap<>();
	
	private static boolean debug;
	
	private Player player;
	private PlayerConfig config;
	private HashMap<UUID, Marker> worldMarkersList = new HashMap<>(); // UUID => World.ID
	private Interact interact;
	
	private Massband(Player player) {
		this.player = player;
		this.config = PlayerConfig.initialize(player);
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
			
			Server.logger().info(String.format("[%s] %s Markers removed from world", world.getName(), count));
			if (sender == null) continue;
			if (sender instanceof Player) 
				sender.sendMessage(String.format("§9World: §7[§5%s§7] §c%s §6Markers removed", world.getName(), count));		
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

		//DEBUG: Server.get().getConsoleSender().sendMessage("§e--> Chunk loaded!");
		
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
		if (event.isCancelled()) return;
		
		for (UUID uuid : list.keySet()){
			Massband obj = Massband.get(uuid);
			
			if (!obj.getPlayer().isOnline()) continue;

			obj.getMarkers(event.getWorld()).hideInChunck(event.getChunk());
				//Server.get().getConsoleSender().sendMessage("§9 --> Markers removed!");
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
		return config;
	}
	
	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		Massband.debug = debug;
		Server.logger().warning("Debug mode " + (debug ? "enabled" : "disabled"));
	}
	
}
