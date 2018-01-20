package net.icelane.massband.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import net.icelane.massband.core.Massband;

public class WorldEvents implements Listener {

	private static WorldEvents eventHandler = new WorldEvents();

	public static WorldEvents getListener(){
		return eventHandler;
	}
		
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event){
		Massband.chuckLoad(event);
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		Massband.chuckUnload(event);
	}
	
}
