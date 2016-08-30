package net.icelane.massband.minecraft;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class FloatingLabel {

	private ArmorStand entity;
	
	private FloatingLabel(ArmorStand entity){
		this.entity = entity;
	}
	
	public static FloatingLabel create(World world, Block block, String text){
		
		Location location = new Location(world,
				block.getX() + 0.5f,
				block.getY() - 0.7f,
				block.getZ() + 0.5f);

		return create(location, text);
	}
	
	public static FloatingLabel create(Location location, String text){
		ArmorStand entity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		
		entity.setGravity(false);
		entity.setCustomNameVisible(true);
		entity.setCustomName(text);
		
		FloatingLabel label = new FloatingLabel(entity);
		
		return label;
	}
	
	public ArmorStand getEntity(){
		return entity;
	}
	
}
