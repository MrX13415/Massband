package de.MrX13415.Massband;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.Vector;


public class PlayerVars{
	
	public enum AXIS{
		X, Y, Z, NONE;
	}
	
	public static final int MODE_SIMPLE = 0;
	public static final int MODE_LENGTH = 1;
	public static final int MODE_SURFACE = 2;
	public static final int MODE_FIXED = 3;

	private Player player;
	private int mode = MODE_SIMPLE;	//default
	private boolean isEnabled = true;
	
	private CountBlocks myThread = null;
	
	private double lenght = 0;
	private double fixedLenght = 0;
	
	private double dimensionLength = 0;
	private double dimensionWidth = 0;
	private double dimensionHieght = 0;
	
	public int blockCount = 0;
	public ArrayList<Material> blocksCount_Material = new ArrayList<Material>();
	public ArrayList<Integer> blocksCount_counts = new ArrayList<Integer>();
	public World lastWorld; 
			
	private ArrayList<Vector> wayPoints = new ArrayList<Vector>();
	
	private ArrayList<AXIS> ignoredaxes = new ArrayList<AXIS>();
	
	public PlayerVars(Player player){
		this.player = player;
		removeAllWayPoints();
		ignoredaxes.add(AXIS.Y); //ignore height by default
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
		
	public double getFixedLenght() {
		return fixedLenght;
	}

	public void setFixedLenght(double fixedLenght) {
		this.fixedLenght = fixedLenght;
	}

	public double getDimensionWith() {
		return dimensionWidth;
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
	
	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public ArrayList<AXIS> getIgnoredAxes() {
		return ignoredaxes;
	}
	
	public String getIgnoredAxesAsString() {
		String raxes = "";
		for (AXIS axis : ignoredaxes) {
			if (axis.equals(AXIS.X)) raxes += Massband.getLanguage().AXIS_X;
			if (axis.equals(AXIS.Y)) raxes += Massband.getLanguage().AXIS_Y;
			if (axis.equals(AXIS.Z)) raxes += Massband.getLanguage().AXIS_Z;
			raxes += ", ";
		}
		if (raxes.endsWith(", ")) raxes = raxes.substring(0, raxes.length() - 2);
		if (ignoredaxes.isEmpty()) raxes = Massband.getLanguage().AXIS_NONE;
		return raxes;
	}
	
	public void setAxes(ArrayList<AXIS> axes) {
		this.ignoredaxes = axes;
	}

	public double computingVectors(){
		if (getWayPointListSize() >= 2) {
			lenght = 0;
						
			for (int vectorIndex = 0; vectorIndex < getWayPointListSize() - 1; vectorIndex++) {
				Vector firstV = getVector(vectorIndex);
				Vector nextV = getVector(vectorIndex + 1);
				
				if (ignoredaxes.contains(AXIS.X)){firstV.setX(0);nextV.setX(0);}
				if (ignoredaxes.contains(AXIS.Y)){firstV.setY(0);nextV.setY(0);}
				if (ignoredaxes.contains(AXIS.Z)){firstV.setZ(0);nextV.setZ(0);}
						
				lenght += firstV.distance(nextV);
			}
			lenght += 1;	//add last point
		}
		return lenght;
	}
	
	public void calculateDiminsions(){		
		if (ignoredaxes.contains(AXIS.X)){wayPoints.get(0).setX(0);wayPoints.get(1).setX(0);}
		if (ignoredaxes.contains(AXIS.Y)){wayPoints.get(0).setY(0);wayPoints.get(1).setY(0);}
		if (ignoredaxes.contains(AXIS.Z)){wayPoints.get(0).setZ(0);wayPoints.get(1).setZ(0);}
			
		//calculate dimensions
		dimensionWidth = Math.abs(wayPoints.get(0).getX() - wayPoints.get(1).getX()) + 1;		
		dimensionLength = Math.abs(wayPoints.get(0).getZ() - wayPoints.get(1).getZ()) + 1;
		dimensionHieght = Math.abs(wayPoints.get(0).getY() - wayPoints.get(1).getY()) + 1;

	}
	
	public void countBlocks(World world){	
		myThread = new CountBlocks(this);
		Massband.server.getScheduler().runTaskAsynchronously(Massband.thisPlugin, myThread);
		
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
	
	public boolean printBlockListPage(int page) {
		if (blocksCount_Material.size() <= 0){
			player.sendMessage(Massband.getLanguage().COUNTBLOCK_CMD_FIRST);
			return false;
		}
		
		int linesPerPage = 7;
		double pages = (double) blocksCount_Material.size() / (double) linesPerPage;
		if((int) pages < pages) pages +=1; //correct pages count if pages is a double value 
		int startIndex = (page - 1) * linesPerPage;
		
		player.sendMessage(String.format(Massband.getLanguage().COUNTBLOCK_BLPAGE_HEADER1, page, (int) pages));
		
		for (int materialIndex = startIndex; materialIndex < blocksCount_Material.size(); materialIndex++) {
			Material material = blocksCount_Material.get(materialIndex);
			int count = blocksCount_counts.get(materialIndex);
			
			player.sendMessage(String.format(Massband.getLanguage().COUNTBLOCK_BLPAGE_LINE, material, count));
			
			if (materialIndex >= (startIndex + linesPerPage - 1)) break;
		}
		player.sendMessage(Massband.getLanguage().COUNTBLOCK_BLPAGE_BREAK_LINE);
		player.sendMessage(String.format(Massband.getLanguage().COUNTBLOCK_TOTAL, blockCount));
		player.sendMessage(Massband.getLanguage().COUNTBLOCK_BLPAGE_FOOTER);
		return true;
	}
	
	public boolean createBlocklistBook(String projectName, String objectName, String author){
		if (blocksCount_Material.size() <= 0){
			player.sendMessage(Massband.getLanguage().COUNTBLOCK_CMD_FIRST);
			return false;
		}
		
		final ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
	    BookMeta book = (BookMeta)item.getItemMeta();
	
		book.setAuthor(author);
		
		String title = projectName + ": " + objectName;
		if (projectName.equalsIgnoreCase(objectName)) title = projectName;
		title +=  " - Bill of materials";
		
		book.setTitle(title);		
		
		//              1234567890123456789
		
		
		String pageMain = String.format(
				
			       "\n"
			     + "\n"
			     + "§4§l§nBill of Materials\n\n"
			     + "\n"			
			     + "§0by §4%s\n"				
			     + "\n"
			     + "\n"
			     + "\n"
			     + "\n"
			     + "§8%s\n"
			     + "§8%s"
			     , author, Massband.pdfFile.getName(), Massband.pdfFile.getVersion());
	
		book.addPage(pageMain);
		
		String page1 = String.format(
				       "§4§nProject:\n\n"			
				     + "§0 %s§0\n"				
				     + "\n"				
				     , projectName);
		
		//if (projectName.equalsIgnoreCase(objectName)){
			page1 += String.format(
				       "§4§nObject:\n\n"
				     + "§0 %s§0\n"
				     + "\n"
				     , objectName);
		//}
		
		book.addPage(page1);
		
		String page2 = String.format(
				   "§4§nLocation:\n"
				 + "\n"
				 + "§0 World: §4%s\n\n"
			     + "§0 X: §4%s\n"
				 + "§0 Y: §4%s\n"
				 + "§0 Z: §4%s\n"
				 + "\n",
				 lastWorld.getName(),
			     wayPoints.get(0).getBlockX(),
			     wayPoints.get(0).getBlockY(),
			     wayPoints.get(0).getBlockZ());
		
		book.addPage(page2);
		
	    String page3 = String.format(
				   "§4§nDimensions:\n"
				 + "\n"
				 + "§0 W: §4%s\n"
				 + "§0 L: §4%s\n"
				 + "§0 H: §4%s\n"
				 + "\n"
				 + "§0Cuboid volume:\n"
				 + "%s\n"
				 + "Block count:\n"
				 + "%s\n",
			     dimensionWidth,
			     dimensionLength,
			     dimensionHieght,
			     formatBookNumberLine((int)(dimensionWidth * dimensionLength * dimensionHieght), 9, 10),
			     formatBookNumberLine(blockCount, 9, 10));
		
		book.addPage(page3);
		
		int pageIndex = 0;
		int materialIndex = 0; 
		
		while(materialIndex < blocksCount_Material.size()) {
			
			String page = "";
			
			if (pageIndex == 0) page = "§4§nMaterials:\n\n";
			
			//on the first material page only 5 will fit ...
			for (int i = 0; i < (pageIndex == 0 ? 5 : 6); i++) {
				
				if (materialIndex >= blocksCount_Material.size()){
					break;
				}
				
				Material material = blocksCount_Material.get(materialIndex);
				String matN = material.toString();
				//matN = matN.replace("BLOCK", "");
				//matN = matN.replace("ORE", "");
				//matN = matN.replace("STATIONARY", "STAT~");
				//matN = matN.replace("STONE", "ST~ ");
				//matN = matN.replace("DOUBLE", "DBL~");
				matN = matN.replace("_OFF", "");	
				matN = matN.replace("_", " ");
				matN = matN.replace("  ", " ");
				matN = matN.trim();
				
				//if (matN.equalsIgnoreCase("ST~")) matN = "STONE";
				
				//if (matN.length() > 16) matN = matN.substring(0, 16) + "~";
					
				int count = blocksCount_counts.get(materialIndex);
				
				//make first char upper and the rest lower case ...
				String materialName = matN.substring(0, 1).toUpperCase() + matN.substring(1).toLowerCase();
				
				if (matN.contains(" ")){
					materialName = "";
					for (String elem : matN.split(" ")) {
						materialName += elem.substring(0, 1).toUpperCase() + elem.substring(1).toLowerCase() + " ";
					}
					materialName.trim();
				}
				
				String nameLine = String.format("§0%s\n", materialName);				
				String countLine = formatBookNumberLine(count, 7, 12);
				
				//only 256 chars per page ...
				if (page.length() + nameLine.length() + countLine.length() <= 256){
					page += nameLine;
					page += countLine;
					materialIndex++;
				}
			}
			
			book.addPage(page);
			pageIndex++;
		}
			
	
		item.setItemMeta(book);
		
		this.getPlayer().getInventory().addItem(item);
		
		return true;
	}
	
	private String formatBookNumberLine(int inputVal, int zeroCount, int leadingSpaceCount){
		String inputStr =  String.valueOf(inputVal);
		String leadingZeroStr = "";
		
		if (inputStr.length() < zeroCount){
			String pattern = "";
			for (int i = 0; i < zeroCount; i++) {
				pattern += "0";
			}
			leadingZeroStr = pattern.substring(inputStr.length());
		}
		
		String resultLine = String.format("§7%s§4%s§0\n", leadingZeroStr, inputVal);

		String leadingSpaces = "";
		for (int j = 0; j < leadingSpaceCount; j++) {
			leadingSpaces += " ";
		}
		resultLine = leadingSpaces + resultLine;
		
		return resultLine;	
	}
	
	public boolean findMaterial(ArrayList<String> materialnames) {
		if (blocksCount_Material.size() <= 0){
			player.sendMessage(Massband.getLanguage().COUNTBLOCK_CMD_FIRST);
			return false;
		}
		
		player.sendMessage(Massband.getLanguage().COUNTBLOCK_BLPAGE_HEADER2);
		
		for (String materialname : materialnames) {
			
			ArrayList<Material> requestedMaterials = new ArrayList<Material>();
						
			for (Material mat : Material.values()) {
				if (mat.name().toLowerCase().startsWith(materialname.toLowerCase())) requestedMaterials.add(mat);
			}
			if (requestedMaterials.size() <= 0) player.sendMessage(String.format(Massband.getLanguage().COUNTBLOCK_BLPAGE_NO_MAT, materialname));
			
			for (Material requestedMaterial : requestedMaterials) {
				int count = 0;	
				
				for (int materialIndex = 0; materialIndex < blocksCount_Material.size(); materialIndex++) {
					Material material = blocksCount_Material.get(materialIndex);
					
					if (material.equals(requestedMaterial)){
						count = blocksCount_counts.get(materialIndex);
						break;
					}
				}

				player.sendMessage(String.format(Massband.getLanguage().COUNTBLOCK_BLPAGE_LINE, requestedMaterial.name(), count));
			}
			
			player.sendMessage(Massband.getLanguage().COUNTBLOCK_BLPAGE_BREAK_LINE);
		}
		
		player.sendMessage(Massband.getLanguage().COUNTBLOCK_BLPAGE_FOOTER);
		
		return true;
	}

}
