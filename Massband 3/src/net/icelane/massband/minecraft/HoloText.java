package net.icelane.massband.minecraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import net.icelane.massband.Server;
import net.icelane.massband.config.configs.Config;
import net.icelane.massband.core.Marker;

public class HoloText {
	
	public static final String metadata_Identifier = "Identifier";
	public static final String metadata_Object = "HoloText_Object";
	public static final String metadata_OwnerUUID = "OwnerUUID";
	public static final String metadata_IsOwnerTag = "IsOwnerTag";
	
	private static double defaultEntityLineOffset = Config.get().defaultEntityLineOffset.get(); // 0.3;	
	private static long defaultOwnerHideTicks = Config.get().defaultOwnerHideTicks.get(); //20L * 3; //ticks (20 tick => 1 sec)
	private static long defaultOwnerShowDelayTicks = Config.get().defaultOwnerShowDelayTicks.get(); //10L; //ticks (1 tick => 50 ms)

	private static Plugin plugin;
	
	private	static long ownerTags_LastRun;
	
	private String format_ownerName = "§o§8%s";
	
	private Player player;
	private Location location;
	private String text;
	private ArrayList<ArmorStand> entities = new ArrayList<>();
	private ArrayList<Double> entityOffsets = new ArrayList<>();
	private double lineOffset = defaultEntityLineOffset;
	private boolean visible;
	
	private boolean ownerTagsEnabled = Config.get().marker_showOwnerTags.get();
	private boolean ownerShown;
	private int ownerNameEntityId;
	private long ownerHideTaskTicks;
	private BukkitTask ownerHideTask;
	private BukkitTask ownerShowTask;

	private MetadataValue ownerUUID;
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
		holotext.ownerUUID = this.ownerUUID;
		holotext.setText(getText());
		if (ownerShown) holotext.showOwner(0, ownerHideTaskTicks);
		return holotext;
	}
	
	public static HoloText getObject(ArmorStand entity) {
		return getObject(net.icelane.massband.Plugin.get(), entity);
	}
	
	protected static HoloText getObject(Plugin plugin, ArmorStand entity) {
		if (entity == null) return null;
		
		// get the HoloText object it belongs to ...
		MetadataValue hlobject = HoloText.getMetadata(plugin, entity, HoloText.metadata_Object);
		if (hlobject == null) return null; 
		if (hlobject.value() instanceof HoloText) return (HoloText) hlobject.value();
		return null;
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
		
	/**
	 * Prepares a default set of meta data to this object.
	 * Containing a given identifier and the UUID of the player created this object.
	 *   
	 * @param objectIdentifier
	 */
	public void prepareDefaultMetadata(String objectIdentifier) {
		this.identifier = new FixedMetadataValue(plugin, objectIdentifier);
		this.ownerUUID = new FixedMetadataValue(plugin, player.getUniqueId().toString());
	}

	public void writeOwnerTagMetadata(ArmorStand entity) {
		entity.setMetadata(metadata_IsOwnerTag, new FixedMetadataValue(plugin, true));
	}
	
	public void writeMetadata() {
		if (this.identifier == null && this.ownerUUID == null) return;
		
		for (ArmorStand entity : entities) {
			writeMetadata(entity, this);
		}	
	}
	
	private static void writeMetadata(ArmorStand entity, HoloText holotext) {
		entity.setMetadata(metadata_Object, new FixedMetadataValue(plugin, holotext));
		
		if (holotext.identifier != null) 
			entity.setMetadata(metadata_Identifier, holotext.identifier);
		if (holotext.ownerUUID != null)
			entity.setMetadata(metadata_OwnerUUID, holotext.ownerUUID);
	}
	
	private static void setEntityAttriubtes(ArmorStand entity) {
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
		setEntityAttriubtes(entity);
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
		try {
			return _move(location);
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean _move(Location location){
		if (entities.size() == 0) return false;
		
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
		//ownerNameEntityId = 0;
		ArmorStand ownerEntity = null;
		
		if (getEntityCount() > 0 && isOwnerEntity(getFirstEntity())) {
			ownerEntity = getFirstEntity();
			entities.remove(0);
		}
		
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
		
		if (ownerEntity != null) {
			entities.add(0, ownerEntity);
			ownerShown = true;
			ownerNameEntityId = ownerEntity.getEntityId();
		}
		visible = true;
	}

	public void showOwner() {
		if (!ownerTagsEnabled) return;
		
		showOwner(defaultOwnerShowDelayTicks, defaultOwnerHideTicks);
	}
	
	public void showOwner(long showdelayticks, long autohideticks) {
		if (!ownerTagsEnabled) return;
		
		if (ownerShowTask != null) return;
		// reset the current "hide" task if present.
		if(ownerHideTask != null) {
			ownerHideTask.cancel();
			ownerHideTask = null;
			scheduleHideTask(ownerHideTaskTicks);
		}
		
		if (showdelayticks <= 0) {
			if(ownerShowTask != null) {
				ownerShowTask.cancel();
				ownerShowTask = null;
			}
			showOwnerInstantly(autohideticks);
			return;
		}
		
		ownerShowTask = Server.get().getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				ownerShowTask = null;
				showOwnerInstantly(autohideticks);
			}
		}, showdelayticks);	
	}
	
	private boolean showOwnerInstantly(long autohideticks) {
		if (!ownerTagsEnabled) return false;
		
		ownerShowTask = null;
		
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
		ArmorStand entity = createEntity(this, location, String.format(format_ownerName, player.getName()));
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
		ownerHideTaskTicks = autohideticks;
		scheduleHideTask(autohideticks);
		
		return true;
	}
	
	public static void showOwnerTagsOnPlayerMove(PlayerMoveEvent event){
		// owner tags are disabled!
		if (!Config.get().marker_showOwnerTags.get()) return;
		
		// run only 4 times per second ...
		if (System.currentTimeMillis() - ownerTags_LastRun < 250) return;
		ownerTags_LastRun = System.currentTimeMillis(); 
		
		List<Entity> nearby = event.getPlayer().getNearbyEntities(10, 10, 10);

		for (Entity entity : nearby) {		
			// check for ArmorStand ...
			if (!(entity instanceof ArmorStand)) continue;
			
			// check of Massband marker ...
			//MetadataValue objectType = HoloText.getMetadata(plugin, entity, HoloText.metadata_Identifier);
			//if (objectType == null || !objectType.asString().equals(Marker.Metadata_Identifier)) continue;

			// get the HoloText object it belongs to ...
			//MetadataValue hlobject = HoloText.getMetadata(plugin, entity, HoloText.metadata_Object);
			//if (hlobject == null || !(hlobject.value() instanceof HoloText)) continue;

			// check of Massband marker ...
			if (!Marker.isMarker(plugin, (ArmorStand)entity)) continue;

			// get the HoloText object it belongs to ...
			HoloText marker = HoloText.getObject(plugin, (ArmorStand)entity);
			if (marker == null) continue;
			
			//HoloText marker = (HoloText) hlobject.value();
			// show the owner tag if the player is not the owner itself.
			if (!marker.isOwner(event.getPlayer())) marker.showOwner();
		}		
	}
	
	private boolean isOwnerEntity(Entity entity) {
		if (entity == null) return false;		
		if (entity.getEntityId() == ownerNameEntityId) return true;
		
		MetadataValue ownerTag = HoloText.getMetadata(entity, metadata_IsOwnerTag);
		if (ownerTag != null && ownerTag.asBoolean()) return true;
	
		return false;
	}
	
	public boolean hasOwnerEntity() {
		ArmorStand entity = getFirstEntity();
		if (entity == null) return false;
		if (!isOwnerEntity(entity)) return false;
		
		if (!entity.isValid()) {
			removeOwnerTag();
			return false;
		}
		
		return true;
	}
	
	public void scheduleHideTask(long autohideticks) {
		// run scheduler for auto hiding ...
		if (autohideticks > 0 && ownerHideTask == null) {
			ownerHideTask = Server.get().getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					hideOwner();
					ownerHideTask = null;
				}
			}, autohideticks - 1);				
		}
	}
	
	public boolean hideOwner() {
		if (!hasOwnerEntity()) return false;
		removeOwnerTag();
		return true;
	}
	
	private void removeOwnerTag() {
		if(ownerHideTask != null) {
			ownerHideTask.cancel();
			ownerHideTask = null;
		}
		
		if(ownerShowTask != null) {
			ownerShowTask.cancel();
			ownerShowTask = null;
		}
		
		getFirstEntity().remove();
		entities.remove(0);
		entityOffsets.remove(0);
		ownerShown = false;
		ownerNameEntityId = 0;
	}
	
	public boolean isOwnerShown() {
		return ownerShown;
	}

	public boolean isOwner(Player player) {
		return this.player.getUniqueId().equals(player.getUniqueId());
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

	public String getOwnerNameFormat() {
		return format_ownerName;
	}

	public void setOwnerNameFormat(String format_ownerName) {
		this.format_ownerName = format_ownerName;
	}

	public BukkitTask getOwnerHideTask() {
		return ownerHideTask;
	}

	public BukkitTask getOwnerShowTask() {
		return ownerShowTask;
	}
}
