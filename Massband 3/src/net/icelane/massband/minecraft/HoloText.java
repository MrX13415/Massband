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
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import net.icelane.massband.Server;

public class HoloText {
	
	public static final String metadata_Identifier = "Identifier";
	public static final String metadata_Object = "HoloText_Object";
	public static final String metadata_OwnerName = "OwnerName";
	
	private static double defaultEntityLineOffset = 0.3;	
	private static long defaultOwnerHideTicks = 20L * 3; //ticks (20 tick => 1 sec)
	
	private static Plugin plugin;

	private Player player;
	private Location location;
	private String text;
	private ArrayList<ArmorStand> entities = new ArrayList<>();
	private ArrayList<Double> entityOffsets = new ArrayList<>();
	private double lineOffset = defaultEntityLineOffset;
	private boolean visible;
	
	private boolean ownerShown;
	private double ownerShowTime;
	private int ownerNameEntityId;
	private BukkitTask ownerHideTask;
	
	private MetadataValue ownerName;
	private MetadataValue identifier;
	
	
	private HoloText(Player player, Location location){
		//if (plugin == null) throw new InitializationError("Not initialized. Use initialize(...) once befor using any HoloText object.");
		this.player = player;
		setLocation(location);
	}
	
	public HoloText(Player player, Location location, String text){
		this(player, location);
		setText(text);
	}
	
	public static void initialize(Plugin plugin) {
		HoloText.plugin = plugin;
	}

	public static HoloText create(Player player, World world, Block block, String text){
		return create(player, world, block, BlockFace.UP, text);		
	}
	
	public static HoloText create(Player player, World world, Block block, BlockFace face, String text){
		return create(player, getBlockFaceLocation(world, block, face), text);
	}
	
	public static HoloText create(Player player, Location location, String text){
		return new HoloText(player, location, text);
	}
	
	public HoloText clone(){
		HoloText holotext = new HoloText(this.player, this.location.clone());		
		holotext.identifier = this.identifier;
		holotext.ownerName = this.ownerName;
		holotext.setText(getText());
		//if (ownerShown) holotext.showOwner();
		return holotext;
	}
	
	public static MetadataValue getMetadata(Entity entity, String metadataKey) {
		return getMetadata(plugin, entity, metadataKey);
	}
	
	public static MetadataValue getMetadata(Plugin plugin, Entity entity, String metadataKey) {
		if (entity == null) return null;
		for (MetadataValue metavalue : entity.getMetadata(metadataKey)) {
			if (!metavalue.getOwningPlugin().getName().equals(plugin.getName())) continue;
			return metavalue;
		}
		return null;
	}
	
	public void prepareMetadata(String objectIdentifier) {
		this.identifier = new FixedMetadataValue(plugin, objectIdentifier);
		this.ownerName = new FixedMetadataValue(plugin, player.getName().toString());
	}

	public void writeOwnerTagMetadata(ArmorStand entity) {
		entity.setMetadata("OwnerTag", new FixedMetadataValue(plugin, true));
	}
	
	public void writeMetadata() {
		if (this.identifier == null && this.ownerName == null) return;
		
		for (ArmorStand entity : entities) {
			writeMetadata(entity, this);
		}	
	}
	
	private static void writeMetadata(ArmorStand entity, HoloText holotext) {
		entity.setMetadata(metadata_Object, new FixedMetadataValue(plugin, holotext));
		
		if (holotext.identifier != null) 
			entity.setMetadata(metadata_Identifier, holotext.identifier);
		if (holotext.ownerName != null)
			entity.setMetadata(metadata_OwnerName, holotext.ownerName);
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
	
	private ArmorStand recreateEntity(ArmorStand entity){
		ArmorStand newEntity = createEntity(this, entity.getLocation(), entity.getCustomName());		
		if (isOwnerEntity(entity)) {
			ownerNameEntityId = newEntity.getEntityId();
			writeOwnerTagMetadata(newEntity);
		}
		return newEntity;
	}
	
	private static ArmorStand createEntity(HoloText holotext, Location location, String text){
		ArmorStand entity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		setEntityAttirubtes(entity);
		writeMetadata(entity, holotext);

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

	public int getEntityCount(){
		return entities.size();
	}

	public boolean equals(HoloText holo) {
		return this.getLastEntity().getEntityId() == holo.getLastEntity().getEntityId();
	}
	
	@Override
	public String toString() {
		return getLastEntity().getCustomName();
	}
	
	public void remove(){
		hide();
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
		return move(getBlockFaceLocation(getLastEntity().getWorld(), block, face));
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
		visible = false;
	}
	
	public boolean show(){
		if (isValid()) return false;
		if (!getChunk().isLoaded()) return false;
		
		for (int index = 0; index < entities.size(); index++) {
			getEntity(index).remove();
			entities.set(index, recreateEntity(getEntity(index)));
		}		
		visible = true;
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
		refresh();
	}
	
	/**
	 * Calculates the Y offset according to the number of lines in the text.
	 * @param lineIndex The index of the line
	 * @return The calculated offset value
	 */
	public double getLineOffset(int lineIndex) {
		return Math.abs((getLines().length - lineIndex - 1) * lineOffset);
	}
	
	public void refresh(){
		String lines[] = getLines(); 
		
		// /!\ Important: Reset, because the entity gets reused for "normal" text.
		ownerNameEntityId = 0;
		
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
			Location location = this.location.clone();
			location.setY(location.getY() + offset);
			
			// reuse existing entities ...
			if (entityIndex < 0) {
				entities.add(index, createEntity(this, location, lines[index]));
			}else{	
				getEntity(index).setCustomName(lines[index]);
				getEntity(index).teleport(location);
				writeMetadata(getEntity(index), this);
			}
			entityOffsets.add(offset);
			
			entityIndex++;
		}
		
		visible = true;
		if (ownerShown){
			showOwner();
		}
	}

	public boolean showOwner() {
		return showOwner(defaultOwnerHideTicks);
	}
	
	public boolean showOwner(long autohideticks) {		
		// check if the owner tag is really shown.
		if (ownerShown) {
			ownerShown = isOwnerEntity(getFirstEntity());
		}

		if (!visible) return false;
		if (ownerShown) return false;
		hideOwner();
		
		// calculate the Y offset, so it above the first line.
		// "-1", because "getLineOffset" dosn't know the "new" line yet.2
		double offset = Math.abs(getLineOffset(-1) * -1);
		
		// calculate the new location ...
		Location location = this.location.clone();
		location.setY(location.getY() + offset);
						
		// create name tag ...
		ArmorStand entity = createEntity(this, location, player.getName());
		writeOwnerTagMetadata(entity);
		ownerNameEntityId = entity.getEntityId();
		ownerShown = true;

		// add to entity list ...
		entities.add(0, entity);
		entityOffsets.add(0, offset);
	
		// reset and cancel the current task if present.
		if(ownerHideTask != null) {
			ownerHideTask.cancel();
			ownerHideTask = null;
		}
		
		// run scheduler for auto hiding ...
		if (autohideticks > 0 && ownerHideTask == null) {
			ownerHideTask = Server.get().getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					hideOwner();
					ownerHideTask = null;
				}
			}, defaultOwnerHideTicks);				
		}
		
		return true;
	}
	
	private boolean isOwnerEntity(Entity entity) {
		if (entity == null) return false;		
		if (entity.getEntityId() == ownerNameEntityId) return true;
		
//		MetadataValue ownerTag = HoloText.getMetadata(plugin, entity, "OwnerTag");
//		if (ownerTag != null && ownerTag.asBoolean()) return true;
	
		return false;
	}
	
	public boolean hasOwnerEntity() {
		ArmorStand entity = getFirstEntity();
		if (entity == null) return false;
		if (!isOwnerEntity(entity)) return false;
		
		if (!entity.isValid()) {
			entity.remove();
			entities.remove(0);
			entityOffsets.remove(0);
			
			if(ownerHideTask != null) {
				ownerHideTask.cancel();
				ownerHideTask = null;
			}
			return false;
		}
		
		return true;
	}
	
	public boolean hideOwner() {
		if (!hasOwnerEntity()) return false;

		getFirstEntity().remove();
		entities.remove(0);
		entityOffsets.remove(0);
		ownerShown = false;
		
		if(ownerHideTask != null) {
			ownerHideTask.cancel();
			ownerHideTask = null;
		}
		return true;
	}
	
	public boolean isOwnerShown() {
		return ownerShown;
	}

	public boolean hasEntity(Entity entity){
		for (ArmorStand _entity : entities) {
			if (_entity.getEntityId() == entity.getEntityId()) return true;
		}
		return false;
	}
	
	public ArmorStand getEntity(int index){
		return entities.get(index);
	}
		
	public ArmorStand getLastEntity(){
		return entities.get(entities.size() - 1);
	}
	
	public ArmorStand getFirstEntity(){
		return entities.get(0);
	}
	
	public ArrayList<ArmorStand> getEntities(){
		return entities;
	}
	
	public Chunk getChunk(){
		return getLastEntity().getWorld().getChunkAt(getLastEntity().getLocation());	
	}

	public Location getLocation() {
		return location.clone();
	}

	private void setLocation(Location location) {
		this.location = location;
	}

	public static double getDefaultEntityLineOffset() {
		return defaultEntityLineOffset;
	}

	public static void setDefaultEntityLineOffset(double defaultEntityLineOffset) {
		HoloText.defaultEntityLineOffset = defaultEntityLineOffset;
	}

	public double getLineOffset() {
		return lineOffset;
	}

	public void setLineOffset(double lineOffset) {
		this.lineOffset = lineOffset;
	}
}
