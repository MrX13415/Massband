package de.MrX13415.Massband;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


public class PlayerVars{

	public static final int MODE_SIMPLE = 0;
	public static final int MODE_LENGTH = 1;
	public static final int MODE_SURFACE = 2;

	private Player player;
	private int mode = MODE_SIMPLE;	//default
	
	private CountBlocks myThread = null;
	
	private double lenght = 0;
	
	private double dimensionLength = 0;
	private double dimensionWith = 0;
	private double dimensionHieght = 0;
	
	public int blockCount = 0;
	public ArrayList<Material> blocksCount_Material = new ArrayList<Material>();
	public ArrayList<Integer> blocksCount_counts = new ArrayList<Integer>();
	
	private ArrayList<Vector> wayPoints = new ArrayList<Vector>();
	
	private boolean ignoreHeight = true;	//Y-axe
	
	public PlayerVars(Player player){
		this.player = player;
		removeAllWayPoints();
	}
	
	public void addPoint(int x, int y, int z) {
		wayPoints.add(new Vector(x, y, z));
	}
	
	public Vector getVector(int index) {
		return wayPoints.get(index);
	}
	
	public int getWayPointListSize(){
		return wayPoints.size();
	}	

	public ArrayList<Vector> getWayPointList(){
		return wayPoints;
	}	
	
	public void removeAllWayPoints() {
		wayPoints.clear();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setLenght(double lenght) {
		this.lenght = lenght;
	}
	
	public double getLenght() {
		return lenght;
	}
		
	public double getDimensionWith() {
		return dimensionWith;
	}
	
	public double getDimensionLength() {
		return dimensionLength;
	}
	
	public double getDimensionHieght() {
		return dimensionHieght;
	}
	
	public int getMode() {
		return mode;
	}
	
	public void setBlockCountingThread(CountBlocks thread) {
		this.myThread = thread;
	}
	
	public CountBlocks getBlockCountingThread() {
		return myThread;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public double computingVectors(){
		if (getWayPointListSize() >= 2) {
			lenght = 0;
						
			for (int vectorIndex = 0; vectorIndex < getWayPointListSize() - 1; vectorIndex++) {
				Vector firstV = getVector(vectorIndex);
				Vector nextV = getVector(vectorIndex + 1);
				
				if (ignoreHeight){
					firstV.setY(0);
					nextV.setY(0);
				}
				
				lenght += firstV.distance(nextV);
			}
			lenght += 1;	//add last point
		}
		return lenght;
	}
	
	public void calculateDiminsions(){
		if (ignoreHeight){
			wayPoints.get(0).setY(0);
			wayPoints.get(1).setY(0);
		}
		
		//calculate dimensions
		dimensionWith = Math.abs(wayPoints.get(0).getX() - wayPoints.get(1).getX()) + 1;		
		dimensionLength = Math.abs(wayPoints.get(0).getZ() - wayPoints.get(1).getZ()) + 1;
		dimensionHieght = Math.abs(wayPoints.get(0).getY() - wayPoints.get(1).getY()) + 1;

	}
	
	public void countBlocks(World world){	
		myThread = new CountBlocks(this);
		myThread.start();
		
//		int blockCount = ;
		
//		int yStart = (int) Math.min(wayPoints.get(0).getY(), wayPoints.get(1).getY());
//		int yEnd = (int) Math.max(wayPoints.get(0).getY(), wayPoints.get(1).getY());
//		
//		int zStart = (int) Math.min(wayPoints.get(0).getZ(), wayPoints.get(1).getZ());
//		int zEnd = (int) Math.max(wayPoints.get(0).getZ(), wayPoints.get(1).getZ());
//	
//		int xStart = (int) Math.min(wayPoints.get(0).getX(), wayPoints.get(1).getX());
//		int xEnd = (int) Math.max(wayPoints.get(0).getX(), wayPoints.get(1).getX());
//
//		int blockCount = 0;
//		
//		for (int yIndex = yStart; yIndex <= yEnd; yIndex++) {
//			for (int zIndex = zStart; zIndex <= zEnd; zIndex++) {
//				for (int xIndex = xStart; xIndex <= xEnd; xIndex++) {
//					//get Block
//					Block b = Massband.server.getWorld(world.getName()).getBlockAt(xIndex, yIndex, zIndex);
//					//count blocks exept air ...
//					if (b.getTypeId()!= 0) blockCount++;
//				}
//			}	
//		}
//		
//		return this.blockCount = blockCount;
	}
	
	public void printArray(int page) {
		int linesPerPage = 7;
		double pages = (double) blocksCount_Material.size() / (double) linesPerPage;
		if((int) pages < pages) pages +=1; //correct pages count if pages is a double value 
		int startIndex = (page - 1) * linesPerPage;
		
		player.sendMessage(ChatColor.GRAY + "- Block counts --- page " + page + "/" + (int) pages + " ------------------------");
		
		for (int materialIndex = startIndex; materialIndex < blocksCount_Material.size(); materialIndex++) {
			Material material = blocksCount_Material.get(materialIndex);
			int count = blocksCount_counts.get(materialIndex);
			
			player.sendMessage(ChatColor.WHITE + "  + " + material + ": " + ChatColor.GOLD + count);
			
			if (materialIndex >= (startIndex + linesPerPage - 1)) break;
		}
		player.sendMessage(ChatColor.GRAY + "----------------------------------------------------");
		player.sendMessage(ChatColor.WHITE + " Total content: " + ChatColor.GOLD + blockCount + ChatColor.WHITE + " Blocks" + ChatColor.GRAY + " (exept air)");
		player.sendMessage(ChatColor.GRAY + "----------------------------------------------------");
	}
		
	/**ignors the Y axe
	 * 
	 * @param bool
	 */
	public void setignoreHeight(boolean bool) {
		ignoreHeight = bool;
	}
	
	public boolean getignoreHeight() {
		return ignoreHeight;
	}
}
