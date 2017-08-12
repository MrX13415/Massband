package net.icelane.massband.core;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import net.icelane.massband.minecraft.HoloText;
import net.icelane.math.Point;
import net.icelane.math.Polygon;


public class Markers {

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

	private World world;
	
	private String format_markerFirst    = "§c#\n%1$s";                  // (1) additional info
	private String format_markerLast     = "§7(%1$s§7) §6%2$s\n§9%3$s";  // (1) marker count (2) length (3) area
	private String format_marker         = "§7#%1$s: §a%2$s";            // (1) marker index (2) length
	private String format_markerOne      = "§6%2$s ";                    // (2) length
	private String format_mode_axis      = "§9(%1$s§9, %2$s§9)";         // (0) axis 1 (2) axis 2
	private String format_blocks_length  = "%1d §cblocks %s";            //
	private String format_blocks_area    = "%1d §cblocks²";               // 
	private String format_blocks_auto    = "§7(%s§7)";                   // 
	private String format_blocks_axis    = "§9(%s§9)";                   //
	private String format_vectors_length = "%.3f§cm";                    // 
	private String format_vectors_area   = "%.3f§cm²";                   //

	private String text_axis_X           = "§cX";                        // 
	private String text_axis_Y           = "§aY";                        // 
	private String text_axis_Z           = "§9Z";                        // 
	
	private ArrayList<HoloText> markerList = new ArrayList<>();
	private ArrayList<Block> blockList = new ArrayList<>();
	private ArrayList<BlockFace> faceList = new ArrayList<>();
	private ArrayList<MarkerSettings> settingsList = new ArrayList<>();
	
	private MeasureMode mode = MeasureMode.BLOCKS;
	private int maxCount     = 1;
	private BlockAxis ignoredAxis = BlockAxis.None;
	
	private double distance;
	
	
	public Markers(World world) {
		this.world = world;
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
					//DEBUG: Server.logger().info(marker.getText() + " --> " + marker.getEntity().getEntityId());
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

	private boolean inBounds(int index){
		return index > -1 && index < getCount();
	}
	
	public int getCount(){
		return markerList.size();
	}
	
	public HoloText get(int index){
		if (!inBounds(index)) return null;
		return markerList.get(index);
	}
	
	public Block getBlock(int index){
		if (!inBounds(index)) return null;
		return blockList.get(index);
	}
	
	public BlockFace getBlockFace(int index){
		if (!inBounds(index)) return null;
		return faceList.get(index);
	}
	
	public MarkerSettings getSettings(int index){
		if (!inBounds(index)) return null;
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
		if (getMaxCount() > 0 && getCount() >= getMaxCount() + 1){
			removeAll();
		}
		
		if (getCount() == 0){
			markerList.add(HoloText.create(world, block, face, "#"));
		}else{
			// Create a clone from the current last marker ...
			HoloText clone = getLast().clone();
			// insert the cloned marker ...
			markerList.add(getCount() - 1, clone);			
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
			//block.getLocation().getBlockY();  just at the floor for now
			//TODO: Add level option X, Y and Z
			
			points[index] = new Point(getBlock(index).getLocation().getBlockX(), getBlock(index).getLocation().getBlockZ()); // z is y in minecraft!
		}
		
		return points;
	}
	 
	public double getArea(){
		return Polygon.getArea(Polygon.resize(getPoints(), Polygon.block_offset));
	}
	
	public long getBlockArea(){
		return Polygon.GetBlockArea(getPoints());
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
			
			if (vecPrev != null){
				switch (getIgnoredAxis()) {
				case X: vec.setX(0); vecPrev.setX(0); break;
				case Y: vec.setY(0); vecPrev.setY(0); break;
				case Z: vec.setZ(0); vecPrev.setZ(0); break;
				default: break;
				}
				
				if (mode == MeasureMode.VECTORS){
					distance += vecPrev.distance(vec);
				}else{
					int distX = Math.abs(vecPrev.getBlockX() - vec.getBlockX());
					int distY = Math.abs(vecPrev.getBlockY() - vec.getBlockY());
					int distZ = Math.abs(vecPrev.getBlockZ() - vec.getBlockZ());
					
					if (settings.isAutoAxis()){
						int newDist = Math.max(distX, Math.max(distY, distZ));
						if (newDist == distX) settings.setAxis(BlockAxis.X);
						if (newDist == distY) settings.setAxis(BlockAxis.Y);
						if (newDist == distZ) settings.setAxis(BlockAxis.Z);
						distance += newDist;
					}else{
						switch (settings.getAxis()) {
						case X:    distance += distX; break;
						case Y:    distance += distY; break;
						case Z:    distance += distZ; break;
						default: break;
						}
					}
				}
			}
			vecPrev = vec;
						
			// format value ...
			String value = String.format(format_vectors_length, distance);
			if (mode == MeasureMode.BLOCKS){
				String strAxis = String.format(
						(settings.isAutoAxis() ? format_blocks_auto : format_blocks_axis),
						getAxisText(settings.getAxis()));
				
				value = String.format(format_blocks_length, (int) distance, strAxis);
			}
			
			String out  = "";
			
			if (index == 0){
				String modOpts = "";
				if (getIgnoredAxis() != BlockAxis.None){
					modOpts = String.format(format_mode_axis,
							getAxisText(getAllowedAxis()[0]),
							getAxisText(getAllowedAxis()[1]));
				}
				out = String.format(format_markerFirst, modOpts);
				
			}else{
				String format = format_marker;
				if (index == size - 1) format = format_markerLast;  //last
				if (size == 2) format = format_markerOne;
				
				String strArea = "";
				if (size > 2) {
					if (mode == MeasureMode.BLOCKS)
						strArea = String.format(format_blocks_area, getBlockArea());
					if (mode == MeasureMode.VECTORS)
						strArea = String.format(format_vectors_area, getArea()); 
				}
				out = String.format(format, index, value, strArea);
			}
			
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
