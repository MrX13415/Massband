package net.icelane.massband.core;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import net.icelane.massband.Config;
import net.icelane.massband.minecraft.HoloText;


public class Markers {

	public enum MeasureMode{
		BLOCKS,
		VECTORS
	}
	
	public enum BlocksMeasureAxis{
		None,
		X,
		Y,
		Z
	}

	private World world;
	
	private String format_markerFirst  = "§c#";
	private String format_markerLast   = "§7(%1$s) §6%2$s";  // (1) marker count (2) length
	private String format_marker       = "§7#%1$s: §a%2$s";  // (1) marker index (2) length
	private String format_markerOne    = "§6%2$s ";          // (2) length
	private String format_mode_blocks  = "%1d §cblocks %s";
	private String format_blocks_auto  = "§7(%s)";
	private String format_blocks_axis  = "§9(%s)";
	private String format_mode_vectors = "%.3f§cm";
	
	private ArrayList<HoloText> markerList = new ArrayList<>();
	private ArrayList<Block> blockList = new ArrayList<>();
	private ArrayList<BlockFace> faceList = new ArrayList<>();
	private ArrayList<MarkerSettings> settingsList = new ArrayList<>();
	
	private MeasureMode mode = MeasureMode.BLOCKS;
	private int maxCount     = 1;

	private double distance;
	
	
	public Markers(World world) {
		this.world = world;
	}

	public static int getMaxVisibleCount(){
		return Integer.getInteger(Config.player_maxVisibleMarkers.valueStr());
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
				if (marker.getEntity().getEntityId() == entity.getEntityId()){
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
			markerList.add(HoloText.create(world, block, face, ""));
		}else{
			// Create a clone from the last marker and insert it before the last item
			markerList.add(getCount() - 1, getLast().clone());
			// move "new" the last marker to the new location
			getLast().move(block, face); 
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
				if (mode == MeasureMode.VECTORS){
					distance += vecPrev.distance(vec);
				}else{
					int distX = Math.abs(vecPrev.getBlockX() - vec.getBlockX());
					int distY = Math.abs(vecPrev.getBlockY() - vec.getBlockY());
					int distZ = Math.abs(vecPrev.getBlockZ() - vec.getBlockZ());
					
					if (settings.isAutoAxis()){
						int newDist = Math.max(distX, Math.max(distY, distZ));
						if (newDist == distX) settings.setAxis(BlocksMeasureAxis.X);
						if (newDist == distY) settings.setAxis(BlocksMeasureAxis.Y);
						if (newDist == distZ) settings.setAxis(BlocksMeasureAxis.Z);
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
			
			// determine out format ...
			String format = format_marker;			
			if (index == size - 1) format = format_markerLast;  //last
			if (size == 2) format = format_markerOne;
			if (index == 0) format = format_markerFirst;   //first
			
			// change HoloText entity ...
			String value = String.format(format_mode_vectors, distance);
			if (mode == MeasureMode.BLOCKS){
				String strAxis = String.format(settings.isAutoAxis() ? format_blocks_auto : format_blocks_axis, settings.getAxis());	
				value = String.format(format_mode_blocks, (int) distance, strAxis);
			}
					
			String out = String.format(format, index, value);
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
	
	public class MarkerSettings{
		
		private BlocksMeasureAxis axis = BlocksMeasureAxis.None;
		private BlocksMeasureAxis lastAutoaxis = BlocksMeasureAxis.None;
		private boolean autoAxis = true;
		
		public BlocksMeasureAxis getAxis() {
			return axis;
		}
		
		public void setAxis(BlocksMeasureAxis axis) {
			this.axis = axis;
			if (autoAxis) lastAutoaxis = this.axis;
		}
		
		public boolean isAutoAxis() {
			return autoAxis;
		}
		
		public void setAutoAxis(boolean autoAxis) {
			this.autoAxis = autoAxis;
		}

		public BlocksMeasureAxis getLastAutoaxis() {
			return lastAutoaxis;
		}
		
	}
}
