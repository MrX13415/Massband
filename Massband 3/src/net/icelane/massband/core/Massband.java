package net.icelane.massband.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.metadata.MetadataValue;

import net.icelane.massband.Plugin;
import net.icelane.massband.Server;
import net.icelane.massband.config.configs.Config;
import net.icelane.massband.minecraft.HoloText;

public class Massband {

	private static HashMap<UUID, Massband> list = new HashMap<>();
	
	private static boolean debug;
	
	private Player player;
	private HashMap<String, Marker> worldMarkersList = new HashMap<>(); // String => World.Name
	private Interact interact;
	
	private Massband(Player player) {
		this.player = player;
		this.interact = new Interact(this);
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

	public static void load(){
		
	}

	public static void save(){
		
	}
	
	public void reset(){
		this.interact = new Interact(this);
	}
	
	public static void cleanAll(){
		for (Entry<UUID, Massband> entry : list.entrySet()){
			entry.getValue().clean();
		}
	}
	
	public void clean(){
		for (String key : worldMarkersList.keySet()){
			worldMarkersList.get(key).removeAll();
		}
	}	
		
	public boolean hasItem(){
		return interact.hasItemInHand(player);
	}
	
	public void interact(PlayerInteractEvent event){
		this.interact.interact(event);
	}
	
	public void join(PlayerJoinEvent event){
		load();
	}

	public void quit(PlayerQuitEvent event){
		clean();
	}
	
	public void teleport(PlayerTeleportEvent event) {
		
	}
	
	long lastrun;
	
	
	//TODO move to Marker class
	public void move(PlayerMoveEvent event){
		// owner tags are disabled!
		if (!Config.marker_showOwnerTags.get()) return;
		
		// run only 4 times per second ...
		if (System.currentTimeMillis() - lastrun < 250) return;
		lastrun = System.currentTimeMillis(); 
		
		List<Entity> nearby = event.getPlayer().getNearbyEntities(10, 10, 10);

		for (Entity entity : nearby) {		
			// check for ArmorStand ...
			if (!(entity instanceof ArmorStand)) continue;
			
			// check of Massband marker ...
			MetadataValue objectType = HoloText.getMetadata(Plugin.get(), entity, HoloText.metadata_Identifier);
			if (objectType == null || !objectType.asString().equals(Marker.Metadata_Identifier)) continue;

			// get the HoloText object it belongs to ...
			MetadataValue hlobject = HoloText.getMetadata(Plugin.get(), entity, HoloText.metadata_Object);
			if (hlobject == null || !(hlobject.value() instanceof HoloText)) continue;

			HoloText marker = (HoloText) hlobject.value();
			// show the owner tag if the player is not the owner itself.
			if (!marker.isOwner(event.getPlayer())) marker.showOwner();
		}		
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
		Marker m = worldMarkersList.get(world.getName());
		if (m == null){
			m = new Marker(player, world);
			worldMarkersList.put(world.getName(), m);
		}
		return m;
	}
	
	public int getMarkerCount(World world){
		Marker m = worldMarkersList.get(world.getName());
		if (m == null) return 0;
		return m.getCount();
	}
	
	public void hideMarkers(World world){
		Marker m = worldMarkersList.get(world.getName());
		if (m == null) return;
		m.hideAll();
	}
	
	public void showMarkers(World world){
		Marker m = worldMarkersList.get(world.getName());
		if (m == null) return;
		m.showAll();
	}

	public Interact getInteract() {
		return interact;
	}

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		Massband.debug = debug;
		Server.logger().warning("Debug mode " + (debug ? "enabled" : "disabled"));
	}
	
}
