package net.icelane.massband.core;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
	
	public void interact(PlayerInteractEvent event){
		this.interact.interact(event);
	}
	
	public void join(PlayerJoinEvent event){
		load();
	}

	@EventHandler
	public void quit(PlayerQuitEvent event){
		save();
		clean();
	}
	
	public void worldChange(World worldFrom){
		if (player.getWorld() != worldFrom){
			getMarker(worldFrom).hideAll();
			getMarker(player.getWorld()).showAll();
		}
	}
	
	public Player getPlayer() {
		return player;
	}

	
	public Markers getMarker(World world){
		Markers m = worldMarkersList.get(world.getName());
		if (m == null){
			m = new Markers(world);
			worldMarkersList.put(world.getName(), m);
		}
		return m;
	}

	public Interact getInteract() {
		return interact;
	}

}
