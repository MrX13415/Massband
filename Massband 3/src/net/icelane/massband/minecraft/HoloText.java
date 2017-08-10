package net.icelane.massband.minecraft;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class HoloText {

	private static double EntityLineOffset = 0.25;
	
	private ArrayList<ArmorStand> entities = new ArrayList<>();
	
	public HoloText(ArmorStand entity){
		entities.add(entity);
	}
	
	public static HoloText create(World world, Block block, String text){
		return create(world, block, BlockFace.UP, text);		
	}
	
	public static HoloText create(World world, Block block, BlockFace face, String text){
		return create(getBlockFaceLocation(world, block, face), text);
	}
	
	public static HoloText create(Location location, String text){
		HoloText holotext = new HoloText(createEntity(location, ""));
		if (text.length() > 0) holotext.setText(text);
		return holotext;
	}
	
	private static ArmorStand createEntity(Location location, String text){
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
		
	public void addLine(String text){
		
	}
		
	public int getLineCount(){
		return entities.size();
	}
	
	
	public boolean hasEntity(Entity entity){
		for (ArmorStand _entity : entities) {
			if (_entity.getEntityId() == entity.getEntityId()) return true;
		}
		return false;
	}
	
	private ArmorStand getEntity(int index){
		return entities.get(index);
	}
		
	private ArmorStand getEntity(){
		return entities.get(entities.size() - 1);
	}
	
	public ArrayList<ArmorStand> getEntities(){
		return entities;
	}
	
	public boolean equals(HoloText holo) {
		return this.getEntity().getEntityId() == holo.getEntity().getEntityId();
	}
	
	@Override
	public String toString() {
		return getEntity().getCustomName();
	}
	
	public HoloText clone(){
		return create(getEntity().getLocation(), getEntity().getCustomName());
	}
	
	public void remove(){
		for (ArmorStand entity : entities) {
			entity.remove();
		}
	}
	
	public boolean isValid(){
		for (ArmorStand entity : entities) {
			if (!entity.isValid()) return false;
		}
		return true;
	}
	
	public boolean move(World world, Block block, BlockFace face){
		return move(getBlockFaceLocation(world, block, face));
	}
	
	public boolean move(Block block, BlockFace face){
		return move(getBlockFaceLocation(getEntity().getWorld(), block, face));
	}

	public boolean move(Location location){
		boolean result = true;
		for (int index = 0; index < entities.size(); index++) {
			if (!entities.get(index).teleport(location)) result = false;
			index++;
		}
		return result;
	}
	
	public void hide(){
		for (ArmorStand entity : entities) {
			entity.remove();
		}
	}
	
	public boolean show(){
		if (isValid()) return false;
		if (!getChunk().isLoaded()) return false;
		
		for (int index = 0; index < entities.size(); index++) {
			getEntity(index).remove();
			entities.set(index, createEntity(getEntity(index).getLocation(), getEntity(index).getCustomName()));
		}
		
		return true;
	}
	
	public String getText(){
		String out = "";
		for (ArmorStand entity : entities) {
			out += entity.getCustomName() + "\n";
		}
		return out;
	}
		
	public void setText(String text){
		text = text.replace("\r\n", "\n");
		text = text.replace("\r", "\n");
		String lines[] = text.split("\n"); 

		// save the last (lowest) line of the old text, so we still know the old location ...
		ArmorStand baseEntity = getEntity();
		
		// remove the hole text ...
		remove();
		entities.clear();
		
		for (int index = 0; index < lines.length; index++) {
			
			// calculate the Y offset according to the number of lines in the text
			double offset = (lines.length - index - 1) * EntityLineOffset;
						
			// calculate the new location of the current line based on the old text
			Location location = baseEntity.getLocation();
			location.setY(location.getY() + offset);
			
			//BUG: Somehow the teleport does not work in some cases (unknown reason)
			//   [...]
			//   entity.teleport(location);
			//   [...]
		
			entities.add(createEntity(location, lines[index]));
		}
		
		// remove the temporary save entity of the old text ...
		baseEntity.remove();
		baseEntity = null;
	}
	
	public Chunk getChunk(){
		return getEntity().getWorld().getChunkAt(getEntity().getLocation());	
	}
}
