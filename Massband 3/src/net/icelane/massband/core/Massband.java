package net.icelane.massband.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.icelane.massband.minecraft.HoloText;

public class Massband {

	private static HashMap<UUID, Massband> list = new HashMap<>();
	
	private Player player;
	private Marker markers;
	public int markerCount = 2;
	public boolean specialMode = false;
	
	private Massband(Player player) {
		this.player = player;
		this.markers = new Marker(player);
	}
	
	public static Massband newInsatnce(Player player){
		Massband obj = new Massband(player);
		list.put(player.getUniqueId(), obj);
		return obj;
	}
	
	public static Massband get(Player player){
		Massband obj = get(player.getUniqueId());
		if (obj == null) obj = newInsatnce(player);
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
		this.markers.removeAll();
	}
	
	
	public void interact(PlayerInteractEvent event){
		
		Block block    = event.getClickedBlock();
		BlockFace face = event.getBlockFace();
		ItemStack item = event.getItem();
	
		if (item == null) return;
		if (item.getType() != Material.STICK) return;

		Block blocka = block;
		Block blockb = blocka;
		if (specialMode){
			int size = (int)Math.sqrt(markerCount);
			for (int indexa = 0; indexa < size; indexa++){
				blockb = blocka;
				for (int indexb = 0; indexb < size; indexb++){
					addPoint(blockb.getX(), blockb.getY(), blockb.getZ());
					computingVectors();
					
					markers.add(blockb, face, "Test: " + lenght);
					blockb = blockb.getRelative(BlockFace.EAST);
				}
				blocka = blocka.getRelative(BlockFace.NORTH);
			}
		}
		
		double lastLenght = lenght;
		
		addPoint(block.getX(), block.getY(), block.getZ());
		computingVectors();
		
		if (wayPoints.size() == 1) {
			markers.changeFirst(block, face, "§c#", true);
		}else{
			markers.changeLast(block, face, String.format("§6%sm", lenght), true);
			if (markers.hasMore()){
				markers.getLast().setText(String.format("§7(%s) §6%sm", markers.getCount() + 1, lenght));
				markers.getLastBetween().setText(String.format("§7#%s: §a%sm", markers.getCount(), lastLenght));
			}
		}
		
		
		if (wayPoints.size() >= markerCount) {
			wayPoints.clear();
			lenght = 0;
		}		
	}
	
	public void itemChange(PlayerItemHeldEvent event) {
		ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
		
		if (newItem == null || newItem.getType() != Material.STICK){
			markers.hideAll();
		}else{
			markers.showAll();
		}
	}
	
	private double lenght = 0;
	private ArrayList<Vector> wayPoints = new ArrayList<Vector>();
	
	public void addPoint(int x, int y, int z) {
		wayPoints.add(new Vector(x, y, z));
	}
	
	public Vector getVector(int index) {
		return wayPoints.get(index);
	}
	
	public double computingVectors(){
		if (wayPoints.size() >= 2) {
			lenght = 0;
						
			for (int vectorIndex = 0; vectorIndex < wayPoints.size() - 1; vectorIndex++) {
				Vector firstV = getVector(vectorIndex);
				Vector nextV = getVector(vectorIndex + 1);
				
//				if (ignoredaxes.contains(AXIS.X)){firstV.setX(0);nextV.setX(0);}
//				if (ignoredaxes.contains(AXIS.Y)){firstV.setY(0);nextV.setY(0);}
//				if (ignoredaxes.contains(AXIS.Z)){firstV.setZ(0);nextV.setZ(0);}
					
				lenght += firstV.distance(nextV);
			}
			lenght += 1;	//add last point
		}
		return lenght;
	}

}
