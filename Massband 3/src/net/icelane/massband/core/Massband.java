package net.icelane.massband.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.icelane.massband.minecraft.FloatingLabel;

public class Massband {

	private static HashMap<UUID, Massband> list = new HashMap<>();
	
	private Player player;

	
	private Massband(Player player) {
		this.player = player;
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
	
	public static void clearAll(){
		for (Entry<UUID, Massband> entry : list.entrySet()){
			entry.getValue().clear();
		}
	}
	
	public void clear(){
		label.getEntity().remove();
		label2.getEntity().remove();
	}
	
	FloatingLabel label;
	FloatingLabel label2;
	
	public void interact(PlayerInteractEvent event){
		
		Block block    = event.getClickedBlock();
		ItemStack item = event.getItem();
	
		if (item == null) return;
		if (item.getType() != Material.STICK) return;
		
		//player.sendMessage("INTERACT: " + block.getType() + " HAND: " + event.getHand());
		
		if (label != null){
			label.getEntity().remove();
		}
		
		addPoint(block.getX(), block.getY(), block.getZ());
		
		computingVectors();
		String text = "§c#";
		if (lenght > 0) text = "§6" + lenght + "m";
		
		if (wayPoints.size() == 1) {
			if (label2 != null){
				label2.getEntity().remove();
			}
			label2 = FloatingLabel.create(player.getWorld(), block, text);
		}else{
			label = FloatingLabel.create(player.getWorld(), block, text);
		}
		
		if (wayPoints.size() >= 2) {
			wayPoints.clear();
			lenght = 0;
			
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
