package net.icelane.massband.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.icelane.massband.Plugin;
import net.icelane.massband.Server;
import net.icelane.massband.config.configs.Config;
import net.icelane.massband.minecraft.HoloText;
import net.icelane.massband.resources.Messages;
import net.icelane.math.Point;
import net.icelane.math.Polygon;


public class Marker {
	
	public static final String Metadata_Identifier = "net.icelane.massband:Marker"; 
	
	public enum MeasureMode{
		BLOCKS,
		VECTORS
	}
	
	public enum BlockAxis{
		None,
		X,
		Y,
		Z
	}

	private Player player;
	private World world;
	
	private String format_markerFirst    = Messages.getString("Marker.format_first");              // (1) Length (2) additional info        //$NON-NLS-1$
	private String format_markerLast     = Messages.getString("Marker.format_last");               // (1) marker count (2) length (3) area  //$NON-NLS-1$
	private String format_marker         = Messages.getString("Marker.format");                    // (1) marker index (2) length           //$NON-NLS-1$
	private String format_markerOne      = Messages.getString("Marker.format_length");             // (2) Length                            //$NON-NLS-1$
	private String format_mode_axis      = Messages.getString("Marker.format_mode_axis");          // (1) Axis 1 (2) Axis 2                 //$NON-NLS-1$
	private String format_blocks_length  = Messages.getString("Marker.format_blocks_length");      // (1) Length (2) Axis                   //$NON-NLS-1$  ^
	private String format_blocks_perim   = Messages.getString("Marker.format_blocks_perimeter");   // (1) Perimeter Length                  //$NON-NLS-1$  
	private String format_blocks_area    = Messages.getString("Marker.format_blocks_area");        // (1) Area measurement                  //$NON-NLS-1$
	private String format_blocks_auto    = Messages.getString("Marker.format_blocks_auto");        // (1) Axis                              //$NON-NLS-1$
	private String format_blocks_axis    = Messages.getString("Marker.format_blocks_axis");        // (1) Axis                              //$NON-NLS-1$
	private String format_vectors_length = Messages.getString("Marker.format_vectors_length");     // (1) Length                            //$NON-NLS-1$
	private String format_vectors_perim  = Messages.getString("Marker.format_vectors_perimeter");  // (1) Perimeter Length                  //$NON-NLS-1$
	private String format_vectors_area   = Messages.getString("Marker.format_vectors_area");       // (1) Area measurement                  //$NON-NLS-1$

	private String text_axis_X             = Messages.getString("Marker.axis_x");                    //$NON-NLS-1$
	private String text_axis_Y             = Messages.getString("Marker.axis_y");                    //$NON-NLS-1$
	private String text_axis_Z             = Messages.getString("Marker.axis_z");                    //$NON-NLS-1$

	private ArrayList<HoloText> markerList = new ArrayList<>();
	private ArrayList<Block> blockList = new ArrayList<>();
	private ArrayList<BlockFace> faceList = new ArrayList<>();
	private ArrayList<MarkerSettings> settingsList = new ArrayList<>();
	
	private MeasureMode mode      = MeasureMode.BLOCKS;
	private int maxCount          = 1;
	private BlockAxis ignoredAxis = BlockAxis.None;
	
	private double distance;
	
	public Marker(Player player, World world) {
		HoloText.initialize(Plugin.get());
		this.player = player;
		this.world = world;
		
		// get defaults for the current player 
		Massband obj = Massband.get(player);
		mode = (MeasureMode) obj.config().default_mode.get();
		maxCount = obj.config().default_markercount.get();
		ignoredAxis = (BlockAxis) obj.config().default_ignoredAxis.get();
	}

	/**
	 * Removes all ArmorStand entities in from a given world which are identified as "Marker".
	 * @see #isMarker(ArmorStand)
	 * @param world A world object to clean.
	 * @return The number of the removed entities
	 */
	public static int clean(World world) {		
		Collection<ArmorStand> entities = world.getEntitiesByClass(ArmorStand.class);
		int count = 0;
		
		for (ArmorStand armorStand : entities) {
			if (!Marker.isMarker(armorStand)) continue;
			armorStand.remove();
			count++;
		}
		return count;
	}
	
	/** 
	 * Weather the given ArmorStand is identified as "Marker"
	 * based on the meta data attached to it. 
	 * @see HoloText#isIdentifier(org.bukkit.plugin.Plugin, String, ArmorStand)
	 * @param entity The entity to check.
	 * @return True if it was identified as "Marker"
	 */
	public static boolean isMarker(ArmorStand entity) {
		return isMarker(Plugin.get(), entity);
	}
	
	public static boolean isMarker(org.bukkit.plugin.Plugin plugin, ArmorStand entity) {
		return HoloText.isIdentifier(plugin, Marker.Metadata_Identifier, entity);
	}
	
	public void hideAll(){
		for (HoloText marker : markerList){
			marker.hide();
		}
	}
	
	public boolean hideInChunck(Chunk chunk){
		boolean result = false;
		for (HoloText marker : markerList){
			for(Entity entity : chunk.getEntities()){
				if (marker.hasEntity(entity)){
					//DEBUG
					if (Massband.debugMessage()) Server.logger().info("hideInChunck " + marker.getText() + " --> " + marker.getFirstEntity().getEntityId());
					
					marker.hide();
					if (!result) result = true;
				}
			}
		}
		return result;
	}
		
	public boolean showAll(){
		boolean result = false;
		for (HoloText marker : markerList){
			boolean b = marker.show();
			if (!result) result = b; 
		}
		return result;
	}
	
	public void removeAll(){
		for(HoloText marker : markerList){
			marker.remove();
		}
		markerList.clear();
		blockList.clear();
		faceList.clear();
		settingsList.clear();
	}
	
	public boolean remove(int index){
		HoloText holotext = markerList.remove(index);
		blockList.remove(index);
		faceList.remove(index);
		settingsList.remove(index);
		if (holotext != null) holotext.remove();
		return (holotext != null);
	}

	private boolean inBounds(int index, List<?> list){
		return index > -1 && index < list.size();
	}
	
	public int getCount(){
		return markerList.size();
	}
	
	public boolean isCountLimitReached() {
		return getCount() >= Config.get().marker_PlayerMaxCount.get();
	}
	
	public HoloText get(int index){
		if (!inBounds(index, markerList)) return null;
		return markerList.get(index);
	}
	
	public Block getBlock(int index){
		if (!inBounds(index, blockList)) return null;
		return blockList.get(index);
	}
	
	public BlockFace getBlockFace(int index){
		if (!inBounds(index, faceList)) return null;
		return faceList.get(index);
	}
	
	public MarkerSettings getSettings(int index){
		if (!inBounds(index, settingsList)) return null;
		return settingsList.get(index);
	}

	public HoloText getLast(){
		return getCount() > 0 ? markerList.get(getCount() - 1) : null;
	}
	
	public Block getLastBlock(){
		return getCount() > 0 ? blockList.get(getCount() - 1) : null;
	}
	
	public BlockFace getLastBlockFace(){
		return getCount() > 0 ? faceList.get(getCount() - 1) : null;
	}
	
	public MarkerSettings getLastSettings(){
		return getCount() > 0 ? settingsList.get(getCount() - 1) : null;
	}
	
	public int indexOf(Location location){
		for (int index = 0; index < getCount(); index++){
			Block block = getBlock(index);
			if (block == null) continue;
			
			if(location.getBlockX() == block.getX() && 
			   location.getBlockY() == block.getY() &&
			   location.getBlockZ() == block.getZ()) return index;
		}
		return -1;
	}
		
	public boolean has(Location location){
		return indexOf(location) > -1;
	}	
	
	public void add(Block block, BlockFace face){
		// player limit reached ...
		int playerlimit = Config.get().marker_PlayerMaxCount.get();		
		if (getCount() >= playerlimit) {
			player.sendMessage(String.format(Messages.getString("Marker.limiterror"), playerlimit)); //$NON-NLS-1$
			if (getCount() > playerlimit) return;
		}
		
		// reach the current max count setting ...
		if (maxCount > 0 && getCount() >= maxCount + 1){
			removeAll();
		}
		
		if (getCount() == 0){
			HoloText marker = HoloText.create(player, world, block, face, Messages.getString("Marker.first")); //$NON-NLS-1$
			marker.prepareDefaultMetadata(Metadata_Identifier);
			marker.writeMetadata();
			markerList.add(marker);
		}else{
			// Create a clone from the current last marker ...
			HoloText clone = getLast().clone();
			// insert the cloned marker ...
			markerList.add(getCount() - 1, clone);
			// hide the owner tag so it gets created only if it's necessary
			getLast().hideOwner();
			// move the current last marker to it's new location
			getLast().move(world, block, face);
		}
		
		blockList.add(block);
		faceList.add(face);
		settingsList.add(new MarkerSettings());
		
		recalculate();
	}

	public Vector getVector(int index){
		Block block = getBlock(index); 
		return new Vector(
				block.getLocation().getBlockX(),
				block.getLocation().getBlockY(),
				block.getLocation().getBlockZ()
				);
	}
	
	public Point[] getPoints(){
		Point[] points = new Point[blockList.size()];
		
		for(int index = 0; index < points.length; index++){
			// add 0.5 so we have the center of each block ...
			double x = getBlock(index).getLocation().getBlockX() + 0.5;
			double y = getBlock(index).getLocation().getBlockY() + 0.5;
			double z = getBlock(index).getLocation().getBlockZ() + 0.5;
			
			points[index] = new Point(x, y, z);
		}
		
		return points;
	}
	 
	public Point[] get2DPoints() {
		//DEBUG
		if (Massband.debugMessage()) {
			Server.logger().info("-------------------------");
			Server.logger().info(" Ignored: " + getIgnoredAxis());
		}
		Point[] inPoints = getPoints();
		ArrayList<Point> outPoints = new ArrayList<Point>(inPoints.length);
		
		for (int index = 0; index < inPoints.length; index++) {
			Point out = inPoints[index].get2D_XZ();   // top down
			
			switch (getIgnoredAxis()) {
				case X: out = inPoints[index].get2D_YZ(); break;
				case Z: out = inPoints[index].get2D_XY(); break;
				case Y:         // We don't support 3D polygon calculation yet!
				case None:      // So we calculate using top down by default
				default: break;
			}
			
			// only add unique points!
			if(!outPoints.contains(out)) {
				outPoints.add(out);
			
				//DEBUG
				if (Massband.debugMessage()) Server.logger().info(" -> P[" + index + "] " + out + " <= " + inPoints[index]);
			}else {
				//DEBUG
				if (Massband.debugMessage()) Server.logger().info("    P[" + index + "] " + out + " <= " + inPoints[index]);
			}
		}
		
		Point[] result = new Point[outPoints.size()];
		return outPoints.toArray(result);
	}
	
	public double getArea(){
		return Polygon.getArea(Polygon.resize(get2DPoints(), Polygon.vectors_offset));
	}
	
	public long getBlockArea(){
		return Polygon.GetBlockArea(get2DPoints());
	}
	
	public String getValue(double distance, MarkerSettings settings) {
		return getValueStr(distance, settings, false);
	}
	
	public String getValueStr(double distance, MarkerSettings settings, boolean isPermeter) {
		String formatB = format_blocks_length;
		String formatV = format_vectors_length;
		if (isPermeter) {
			formatB = format_blocks_perim;
			formatV = format_vectors_perim;
		}
		
		if (mode == MeasureMode.BLOCKS){
			String strAxis = String.format(
					(settings.isAutoAxis() ? format_blocks_auto : format_blocks_axis),
					getAxisText(settings.getAxis()));
		
			if (isPermeter) distance--; // First marker shound't be counted twice.
			return String.format(formatB, (int) distance, strAxis);
		}
		
		return String.format(formatV, distance);
	}
	
	public double calcDistance(Vector vecPrev, Vector vec, MarkerSettings settings) {
		if (vecPrev == null) return 0;
		
		switch (getIgnoredAxis()) {
		case X: vec.setX(0); vecPrev.setX(0); break;
		case Y: vec.setY(0); vecPrev.setY(0); break;
		case Z: vec.setZ(0); vecPrev.setZ(0); break;
		default: break;
		}
		
		if (mode == MeasureMode.VECTORS){
			return vecPrev.distance(vec);
		}

		// if (mode == MeasureMode.BLOCKS) 
		int distX = Math.abs(vecPrev.getBlockX() - vec.getBlockX());
		int distY = Math.abs(vecPrev.getBlockY() - vec.getBlockY());
		int distZ = Math.abs(vecPrev.getBlockZ() - vec.getBlockZ());
		
		if (settings.isAutoAxis()){
			int newDist = Math.max(distX, Math.max(distY, distZ));
			if (newDist == distX) settings.setAxis(BlockAxis.X);
			if (newDist == distY) settings.setAxis(BlockAxis.Y);
			if (newDist == distZ) settings.setAxis(BlockAxis.Z);
			return newDist;
		}else{
			switch (settings.getAxis()) {
			case X:    return distX;
			case Y:    return distY;
			case Z:    return distZ;
			default:   return Math.max(distX, Math.max(distY, distZ));
			}
		}
	}
	
	private void updateFirstMarker() {
		updateFirstMarker(0);
	}
	
	private void updateFirstMarker(double distance) {
		if (markerList.size() == 0) return;
		
		HoloText holotext = markerList.get(0);
		MarkerSettings settings = getSettings(0);
		
		String perimeterLength = "";			
		if (distance > 0 && getCount() >= 3) {
			double perimeterDistance = distance + calcDistance(
					getVector(getCount() - 1),
					getVector(0),
					settings);

			perimeterLength = getValueStr(perimeterDistance, settings, true);
		}
		
		String modOpts = "";
		if (getIgnoredAxis() != BlockAxis.None){
			modOpts = String.format(format_mode_axis,
					getAxisText(getAllowedAxis()[0]),
					getAxisText(getAllowedAxis()[1]));
		}
		
		holotext.setText(String.format(format_markerFirst, perimeterLength, modOpts));
		holotext.show();
	}
	
	public void recalculate(){
		int index = 0;
		int size  = getCount();
		
		double distance = mode == MeasureMode.BLOCKS ? 1 : 0;
		Vector vecPrev  = null;
		
		for(HoloText holotext : markerList){
			// calculate vector distance ...
			Vector vec = getVector(index);
			MarkerSettings settings = getSettings(index);
			
			distance += calcDistance(vecPrev, vec, settings);
					
//			if (vecPrev != null){
//				switch (getIgnoredAxis()) {
//				case X: vec.setX(0); vecPrev.setX(0); break;
//				case Y: vec.setY(0); vecPrev.setY(0); break;
//				case Z: vec.setZ(0); vecPrev.setZ(0); break;
//				default: break;
//				}
//				
//				if (mode == MeasureMode.VECTORS){
//					distance += vecPrev.distance(vec);
//				}else{
//					int distX = Math.abs(vecPrev.getBlockX() - vec.getBlockX());
//					int distY = Math.abs(vecPrev.getBlockY() - vec.getBlockY());
//					int distZ = Math.abs(vecPrev.getBlockZ() - vec.getBlockZ());
//					
//					if (settings.isAutoAxis()){
//						int newDist = Math.max(distX, Math.max(distY, distZ));
//						if (newDist == distX) settings.setAxis(BlockAxis.X);
//						if (newDist == distY) settings.setAxis(BlockAxis.Y);
//						if (newDist == distZ) settings.setAxis(BlockAxis.Z);
//						distance += newDist;
//					}else{
//						switch (settings.getAxis()) {
//						case X:    distance += distX; break;
//						case Y:    distance += distY; break;
//						case Z:    distance += distZ; break;
//						default: break;
//						}
//					}
//				}
//			}
			vecPrev = vec;
						
			// format value ...
			String value = getValue(distance, settings);
			
//			String.format(format_vectors_length, distance);
//			if (mode == MeasureMode.BLOCKS){
//				String strAxis = String.format(
//						(settings.isAutoAxis() ? format_blocks_auto : format_blocks_axis),
//						getAxisText(settings.getAxis()));
//				
//				value = String.format(format_blocks_length, (int) distance, strAxis);
//			}
//			
			String out  = "";
			
			if (index == 0){
				updateFirstMarker();
				index++;
				continue;
			}
			
			String format = format_marker;
			if (index == size - 1) format = format_markerLast;  //last
			if (size == 2) format = format_markerOne;

			// calculate polygon area on last marker ... 
			String strArea = "";
			if (size > 2 && index == (size - 1)) {
				if (mode == MeasureMode.BLOCKS)
					strArea = String.format(format_blocks_area, getBlockArea());
				if (mode == MeasureMode.VECTORS)
					strArea = String.format(format_vectors_area, getArea());
				
				updateFirstMarker(distance); // include length if necessary 
			}
			
			out = String.format(format, index, value, strArea);
						
			holotext.setText(out);
			holotext.show();
			
			index++;
		}
		
		this.distance = distance;
	}
	
	public String getFormat_markerFirst() {
		return format_markerFirst;
	}

	public String getFormat_marker() {
		return format_marker;
	}

	public void setFormat_markerFirst(String format_markerFirst) {
		this.format_markerFirst = format_markerFirst;
	}

	public String getFormat_markerLast() {
		return format_markerLast;
	}

	public void setFormat_markerLast(String format_markerLast) {
		this.format_markerLast = format_markerLast;
	}

	public void setFormat_marker(String format_marker) {
		this.format_marker = format_marker;
	}

	public double getDistance() {
		return distance;
	}

	public MeasureMode getMode() {
		return mode;
	}

	public void setMode(MeasureMode mode) {
		this.mode = mode;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
		
	public BlockAxis[] getAllowedAxis() {
		switch (getIgnoredAxis()) {
			case X: return new BlockAxis[] {BlockAxis.Y, BlockAxis.Z};
			case Y: return new BlockAxis[] {BlockAxis.X, BlockAxis.Z};
			case Z: return new BlockAxis[] {BlockAxis.X, BlockAxis.Y};
			default: return new BlockAxis[] {};
		}
	}
	
	public BlockAxis getIgnoredAxis() {
		return ignoredAxis;
	}

	public void setIgnoredAxis(BlockAxis ignoredAxis) {
		this.ignoredAxis = ignoredAxis;
	}

	public String getAxisText(BlockAxis axis) {
		switch (axis) {
			case X: return text_axis_X;
			case Y: return text_axis_Y;
			case Z: return text_axis_Z;
			default: return "";
		}
	}
		
	public class MarkerSettings{
		
		private BlockAxis axis = BlockAxis.None;
		private BlockAxis lastAutoaxis = BlockAxis.None;
		private boolean autoAxis = true;
		
		public BlockAxis getAxis() {
			return axis;
		}
		
		public void setAxis(BlockAxis axis) {
			this.axis = axis;
			if (autoAxis) lastAutoaxis = this.axis;
		}
		
		public boolean isAutoAxis() {
			return autoAxis;
		}
		
		public void setAutoAxis(boolean autoAxis) {
			this.autoAxis = autoAxis;
		}

		public BlockAxis getLastAutoaxis() {
			return lastAutoaxis;
		}
		
	}
}
