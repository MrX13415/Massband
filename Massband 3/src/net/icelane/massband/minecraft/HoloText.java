package net.icelane.massband.minecraft;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class HoloText {

	private ArmorStand entity;

	public HoloText(ArmorStand entity){
		this.entity   = entity;
	}
	
	public static HoloText create(World world, Block block, String text){
		return create(world, block, BlockFace.UP, text);		
	}
	
	public static HoloText create(World world, Block block, BlockFace face, String text){
		return create(getBlockFaceLocation(world, block, face), text);
	}
	
	public static HoloText create(Location location, String text){
		return new HoloText(createEnitiy(location, text));
	}
	
	private static ArmorStand createEnitiy(Location location, String text){
		ArmorStand entity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		
		entity.setMarker(true);   // very small hitbox
		//entity.setInvulnerable(true);
		entity.setSmall(true);
		entity.setSilent(true);
		
		entity.setGravity(false);
		entity.setArms(false);
		entity.setBasePlate(false);
		entity.setCanPickupItems(false);
		entity.setCollidable(false);
		entity.setVisible(false);
		//entity.setRemoveWhenFarAway(true);
		
		entity.setCustomNameVisible(true);
		entity.setCustomName(text);
		
		return entity;
	}
	
	public static Location getBlockFaceLocation(World world, Block block, BlockFace face){
		double modx = face.getModX();
		double mody = face.getModY();
		double modz = face.getModZ();
		
		// calculate face mod value ...
		     if (modx > 0){ modx =  1.3f;               modz =  0.5f; }
		else if (mody > 0){ modx =  0.5f; mody =  1.0f; modz =  0.5f; }
		else if (modz > 0){ modx =  0.5f;               modz =  1.3f; }		
		else if (modx < 0){ modx = -0.3f;               modz =  0.5f; }
		else if (mody < 0){ modx =  0.5f; mody = -0.7f; modz =  0.5f; }
		else if (modz < 0){ modx =  0.5f;               modz = -0.3f; }

		Location location = new Location(world,
				block.getX() + modx,
				block.getY() + mody,
				block.getZ() + modz);
		
		return location;
	}
	
	public ArmorStand getEntity(){
		return entity;
	}
	
	public boolean equals(HoloText holo) {
		return this.getEntity().getEntityId() == holo.getEntity().getEntityId();
	}
	
	@Override
	public String toString() {
		return getEntity().getCustomName();
	}
	
	public HoloText clone(){
		return create(entity.getLocation(), entity.getCustomName());
	}
	
	public void remove(){
		entity.remove();
	}
	
	public boolean isValid(){
		return entity.isValid();
	}
	
	public boolean move(World world, Block block, BlockFace face){
		return move(getBlockFaceLocation(world, block, face));
	}
	
	public boolean move(Block block, BlockFace face){
		return move(getBlockFaceLocation(entity.getWorld(), block, face));
	}

	public boolean move(Location location){
		return entity.teleport(location);
	}
	
	public void hide(){
		entity.remove();
	}
	
	public boolean show(){
		if (isValid()) return false;
		if (!getChunk().isLoaded()) return false;
		entity.remove();
		entity = createEnitiy(entity.getLocation(), entity.getCustomName());
		return true;
	}
	
	public String getText(){
		return entity.getCustomName();
	}
	
	public void setText(String text){
		entity.setCustomNameVisible(true);
		entity.setCustomName(text);
	}
	
	public Chunk getChunk(){
		return getEntity().getWorld().getChunkAt(getEntity().getLocation());	
	}
}
