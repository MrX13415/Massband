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
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

public class HoloText {

	private static double EntityLineOffset = 0.3;
	
	private Location location;
	private String text;
	private ArrayList<ArmorStand> entities = new ArrayList<>();
	private ArrayList<Double> entityOffsets = new ArrayList<>();
	
	public HoloText(Location location){
		this(location, "");
	}
	
	public HoloText(Location location, String text){
		setLocation(location);
		setText(text);
	}

	public static HoloText create(World world, Block block, String text){
		return create(world, block, BlockFace.UP, text);		
	}
	
	public static HoloText create(World world, Block block, BlockFace face, String text){
		return create(getBlockFaceLocation(world, block, face), text);
	}
	
	public static HoloText create(Location location, String text){
		return new HoloText(location, text);
	}
	
	public HoloText clone(){
		return create(getLocation(), getText());
	}
	
	private static void setEntityAttirubtes(ArmorStand entity) {
		entity.setVisible(false);
		entity.setGravity(false);
		entity.setCollidable(false);
		entity.setFallDistance(0);
		entity.setVelocity(new Vector());
		entity.setGliding(false);
		entity.setAI(false);
		
		entity.setMarker(true);   // very small hitbox
		entity.setArms(false);
		entity.setBasePlate(false);

		//entity.setInvulnerable(true);
		entity.setSmall(true);
		entity.setSilent(true);
		
		//entity.setRemoveWhenFarAway(true);
		entity.setCanPickupItems(false);
	}
	
	private static ArmorStand createEntity(Location location, String text){
		ArmorStand entity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		setEntityAttirubtes(entity);
		
		if (text != null) entity.setCustomName(text);
		else entity.setCustomName(" ");
		entity.setCustomNameVisible(true);
		
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
		
	public int getEntityCount(){
		return entities.size();
	}

	public boolean equals(HoloText holo) {
		return this.getEntity().getEntityId() == holo.getEntity().getEntityId();
	}
	
	@Override
	public String toString() {
		return getEntity().getCustomName();
	}
	
	public void remove(){
		for (ArmorStand entity : entities) {
			entity.remove();
		}
	}

	public void clearButLast(){
		for (int index = entities.size() - 2; index >= 0; --index) {
			ArmorStand entity = entities.get(index);
			entity.remove();
			entities.remove(index);
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
			ArmorStand entity = entities.get(index);
			double offset = entityOffsets.get(index);
			
			Location newLocation = new Location(location.getWorld(),
					location.getX(), location.getY() + offset, location.getZ(), 
					location.getYaw(), location.getPitch());
			
			boolean ok = entity.teleport(newLocation, TeleportCause.PLUGIN);
			if (!ok) result = false;
			index++;
		}
		setLocation(location);
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
		return this.text;
	}
	
	public String[] getLines() {
		return getText().split("\n");
	}
	
	public void setText(String text){
		// fix new lines
		text = text.replace("\r\n", "\n");
		text = text.replace("\r", "\n");
		
		this.text = text;
		redrawText();
	}
	
	/**
	 * Calculates the Y offset according to the number of lines in the text.
	 * @param lineIndex The index of the line
	 * @return The calculated offset value
	 */
	public double getLineOffset(int lineIndex) {
		return (getLines().length - lineIndex - 1) * EntityLineOffset;
	}
	
	public void redrawText(){
		String lines[] = getLines(); 

		// trim entity list ...
		while (getEntityCount() > lines.length) {
			getEntity(0).remove();
			entities.remove(0);
		}

		entityOffsets.clear();
		int entityIndex = getEntityCount() - lines.length;
 
		for (int index = 0; index < lines.length; index++) {
			// prevent empty entities ...
			if (lines[index].equals(""))
				lines[index] = " "; 
			
			// calculate the Y offset according to the number of lines in the text
			double offset = getLineOffset(index);
		
			// calculate the new location of the current line
			Location location = getLocation().clone();
			location.setY(location.getY() + offset);
			
			// reuse existing entities ...
			if (entityIndex < 0) {
				entities.add(index, createEntity(location, lines[index]));
			}else{	
				getEntity(index).setCustomName(lines[index]);
				getEntity(index).teleport(location);
			}
			entityOffsets.add(offset);
			
			entityIndex++;
		}

		// clean up ...
//		while(getEntityCount() > entityIndex) {
//			entities.get(getEntityCount() - 1).remove();
//			entities.remove(getEntityCount() - 1);
//		}
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
	
	public Chunk getChunk(){
		return getEntity().getWorld().getChunkAt(getEntity().getLocation());	
	}

	public static double getEntityLineOffset() {
		return EntityLineOffset;
	}

	public static void setEntityLineOffset(double entityLineOffset) {
		EntityLineOffset = entityLineOffset;
	}

	public Location getLocation() {
		return location;
	}

	private void setLocation(Location location) {
		this.location = location;
	}
	
}
