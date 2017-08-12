package net.icelane.massband.core;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class Massband {

	private static HashMap<UUID, Massband> list = new HashMap<>();
	
	private Player player;
	private HashMap<String, Markers> worldMarkersList = new HashMap<>(); // String => World.Name
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
		return (player.getInventory().getItemInMainHand().getType() == interact.getMaterial()
				|| player.getInventory().getItemInOffHand().getType() == interact.getMaterial());
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
	
	//DEBUG:	int lid = -1;
	public void move(PlayerMoveEvent event){
//DEBUG:
//		try {
//			HoloText mm = getMarkers(player.getWorld()).get(0);
//			boolean b = getMarkers(player.getWorld()).get(0).isValid();
//			Server.get().getConsoleSender().sendMessage("VALID: " + (b?"§a( OK )":"§c( NO )") + " OBJ: §6" + mm.getEntity().getEntityId());
//			
//			 
//			if (lid != -1 && mm.getEntity().getEntityId() != lid){
//				Server.get().getConsoleSender().sendMessage("§c!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//				Server.get().getConsoleSender().sendMessage("§c!!!                       !!!");
//				Server.get().getConsoleSender().sendMessage("§c!!! Marker Enitiy CHANGED !!!");
//				Server.get().getConsoleSender().sendMessage("§c!!!                       !!!");
//				Server.get().getConsoleSender().sendMessage("§c!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//			}
//				
//			lid = mm.getEntity().getEntityId();
//		} catch (Exception e) {
//			Server.get().getConsoleSender().sendMessage("§cNo Marker!");
//		}
	}
	
	public void worldChange(World worldFrom){
		if (getPlayer().getWorld() != worldFrom){
			getMarkers(worldFrom).hideAll();
			if (this.hasItem()) getMarkers(getPlayer().getWorld()).showAll();
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

	public Markers getMarkers(World world){
		Markers m = worldMarkersList.get(world.getName());
		if (m == null){
			m = new Markers(world);
			worldMarkersList.put(world.getName(), m);
		}
		return m;
	}
	
	public int getMarkerCount(World world){
		Markers m = worldMarkersList.get(world.getName());
		if (m == null) return 0;
		return m.getCount();
	}

	public Interact getInteract() {
		return interact;
	}

}
