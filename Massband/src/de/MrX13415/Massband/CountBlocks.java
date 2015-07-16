package de.MrX13415.Massband;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class CountBlocks extends BukkitRunnable{
	
	private volatile World world = null;
	private ArrayList<Vector> wayPoints = null;
	private PlayerVars tmpVars;
	
	private int blockCount = 0;
	private ArrayList<Material> blocksCount_Material = new ArrayList<Material>();
	private ArrayList<Integer> blocksCount_counts = new ArrayList<Integer>();
	
	private Player threadOwner = null;
	private boolean interrupted = false;
	private boolean threadEndRechearched = false;
	
	public CountBlocks(PlayerVars playerVars){
		this.world = playerVars.getPlayer().getWorld();
		this.wayPoints = playerVars.getWayPointList();
		this.tmpVars = playerVars;
		this.threadOwner = tmpVars.getPlayer();
		this.tmpVars.setBlockCountingThread(this);
	}
	
	/** Counts the Blocks in a cubid
	 *  (except air)
	 *  
	 * @param world
	 * @param wayPoints
	 * @return
	 */
	public void run(){
		Massband.threads.add(this);
		
		tmpVars.lastWorld = world;
		
		//count ...
		blockCount = countBlocks(world, wayPoints);
//		System.out.println("count: " + blockCount);
		
		if (! interrupted) {
			tmpVars.blockCount = this.blockCount;
			tmpVars.blocksCount_Material = this.blocksCount_Material;
			tmpVars.blocksCount_counts = this.blocksCount_counts;
			
			tmpVars.getPlayer().sendMessage(String.format(Massband.getLanguage().COUNTBLOCK_TOTAL, blockCount));		
		}
		
		threadEndRechearched = true;
		Massband.log.info(Massband.consoleOutputHeader + " Block-counting Thread from " + threadOwner.getName() + " was Interrupted");
		wasInterrupted();
		
//		System.out.println("Thread died: " + this.isAlive());
	}
		
	private synchronized int countBlocks(World world, ArrayList<Vector> wayPoints){
		int yStart = (int) Math.min(wayPoints.get(0).getY(), wayPoints.get(1).getY());
		int yEnd = (int) Math.max(wayPoints.get(0).getY(), wayPoints.get(1).getY());
		
		int zStart = (int) Math.min(wayPoints.get(0).getZ(), wayPoints.get(1).getZ());
		int zEnd = (int) Math.max(wayPoints.get(0).getZ(), wayPoints.get(1).getZ());
	
		int xStart = (int) Math.min(wayPoints.get(0).getX(), wayPoints.get(1).getX());
		int xEnd = (int) Math.max(wayPoints.get(0).getX(), wayPoints.get(1).getX());

		int blockCount = 0;
				
		//perc-----------------
		//calculate dimensions
		double dimensionWith = Math.abs(wayPoints.get(0).getX() - wayPoints.get(1).getX()) + 1;		
		double dimensionLength = Math.abs(wayPoints.get(0).getZ() - wayPoints.get(1).getZ()) + 1;
		double dimensionHieght = Math.abs(wayPoints.get(0).getY() - wayPoints.get(1).getY()) + 1;

		int maxblocks = (int)(dimensionHieght * dimensionWith * dimensionLength);
		
		int percindex = 0;
		long timeBefore = System.currentTimeMillis();
		long timeAfter = timeBefore + 2500; //one second after ...
		long time = 0;
		long timeLastPercMessage = timeBefore;
		int speed = 0;
		boolean measureSpeed = true;
		int sleepCounter = 0;

		//perc--------------
		
		//y
		//
		//
		
		
		
		for (int zIndex = zStart; zIndex <= zEnd; zIndex++) {
			if (interrupted) break;
			
			for (int xIndex = xStart; xIndex <= xEnd; xIndex++) {
				if (interrupted) break;
						
				for (int yIndex = yStart; yIndex <= yEnd; yIndex++)  {
					if (interrupted) break;
	
//					//get Block
//					Chunk chunk = world.getChunkAt(xIndex, zIndex);
//					if (!chunk.isLoaded()){
//						System.out.println("CUNK LOADED: " + chunk.isLoaded());
//						boolean suc = chunk.load();
//						System.out.println("CHUNK LOADED SUCCESSFULLY? " + chunk.isLoaded() + " (" + suc + ")");
//					}
										
					Block block = world.getBlockAt(xIndex, yIndex, zIndex);
					//System.out.println("Y: " + yIndex + " X: " + xIndex + " Z: " + zIndex);
					
					//count blocks except air ...
					if (block.getType() != Material.AIR) blockCount++;
										
					Material blockType = block.getType();

					if (blockType.toString().endsWith("_ON")){
						String newMatN = blockType.toString().substring(0, blockType.toString().lastIndexOf("_ON")) + "_OFF";
						blockType = Material.getMaterial(newMatN);
					}
					
					if (blocksCount_Material.contains(blockType)) {
						int index = blocksCount_Material.indexOf(blockType);
						blocksCount_counts.set(index, blocksCount_counts.get(index) + 1); //add 1 to the given material ...
						
					}else{
						blocksCount_Material.add(blockType);
						blocksCount_counts.add(1);				//add a new Material ...
					}
				
					percindex += 1;
					
					//measure speed	...							
					if ((System.currentTimeMillis()) >= timeAfter && measureSpeed) {
						measureSpeed = false;
						speed = (int) Math.round(percindex / 2.5d);
						System.out.println(percindex);
						tmpVars.getPlayer().sendMessage(String.format(Massband.getLanguage().COUNTBLOCK_SPEED, speed));
					}
					
					//perc ---------------------
					if(System.currentTimeMillis() - timeLastPercMessage > 5000){
						double perc = 100d / (double)maxblocks * (double)percindex;
						tmpVars.getPlayer().sendMessage(String.format(Massband.getLanguage().COUNTBLOCK_PERCENTAGE, Math.round(perc * 1000d) / 1000d));
						timeLastPercMessage = System.currentTimeMillis();
					}

					sleepCounter++;
					
					if (sleepCounter >= Massband.configFile.blockCountingSpeed){
						sleepCounter = 0;
						try {
							Thread.sleep(0, 100000);
						} catch (Exception e) {
						}
					}
				}
			}
		}
		
		if (! interrupted) {
			time = System.currentTimeMillis() - timeBefore;
			tmpVars.getPlayer().sendMessage(String.format(Massband.getLanguage().COUNTBLOCK_TIME, time));
		}
	
		return blockCount;
	}
	
	public void setOwner(Player owner) {
		this.threadOwner = owner;
	}
	
	public Player getOwner() {
		return threadOwner;
	}
	
	public void interrupt() {
		threadOwner.sendMessage(Massband.getLanguage().THREADS_INTERUPT_TRY);
		interrupted = true;
	}
	
	public void wasInterrupted(){
		if (threadEndRechearched) {
			if (threadOwner != null){
				Massband.getPlayerVars(threadOwner).setBlockCountingThread(null);
			}
			
			if (interrupted) threadOwner.sendMessage(Massband.getLanguage().THREADS_INTERUPT);
			threadEndRechearched = false;
			
			Massband.threads.remove(this);
		}
	}
	
	public boolean getEndRecharched() {
		return threadEndRechearched;
	}
	
	/** interrupts all running Block-counting Threads
	 * 
	 */
	public static void interuptAll(CommandSender commandSender){
		String sender = "";
		Player player = null;
		
		if (commandSender instanceof Player) {
			player = (Player) commandSender;
			sender = player.getName();
		}
		
		if (player != null) {
			player.sendMessage(String.format(Massband.getLanguage().THREADS_INTERUPT_TRY_ALL, Massband.threads.size()));
		}
		if (sender.length() > 0)Massband.log.info(Massband.consoleOutputHeader + " " + sender + " trys to interrupt all Block countings ... (count: " + Massband.threads.size() + ")");
		else Massband.log.info(Massband.consoleOutputHeader + " Interrupting all Block countings ... (count: " + Massband.threads.size() + ")");
		
		//create a clone of the threads array ...
		@SuppressWarnings("unchecked")
		ArrayList<CountBlocks> threads = (ArrayList<CountBlocks>) Massband.threads.clone();

		for (CountBlocks thread : threads) {
			if (!thread.getEndRecharched()){				
				thread.interrupt();
				
				int timeout = 5000; // in millis
				long timeOutTime = System.currentTimeMillis() + timeout; 
				
				while (Massband.threads.contains(thread)){
					if (System.currentTimeMillis() > timeOutTime) {
						Massband.log.warning(Massband.consoleOutputHeader + " Timeout: Could not interrupt the thread from player" + thread.threadOwner.getName() + "!");
						break;
					}
				}

				if (thread.getOwner() != null) thread.getOwner().sendMessage(String.format(Massband.getLanguage().THREADS_INTERUPT_FROM_PLAYER, sender));
			}
		}
		
		threads = null;
		
		if (Massband.threads.isEmpty()) {
			if (player != null) {
				player.sendMessage(String.format(Massband.getLanguage().THREADS_INTERUPT_OK, Massband.threads.size()));
			}
			
			Massband.log.info(Massband.consoleOutputHeader + " All running Threads interrupted ! (left: " + Massband.threads.size() + ")");
		
		}else{
			if (player != null) {
				player.sendMessage(String.format(Massband.getLanguage().THREADS_INTERUPT_ERROR1, Massband.threads.size()));
			}
			
			Massband.log.info(Massband.consoleOutputHeader + " Could not interrupt some Threads ! (count: " + Massband.threads.size() + "). Please try again.");
		}
	}
	
	public void printArray() {
		Massband.log.warning("MASSBAND COUNTS:");
		for (int materialIndex = 0; materialIndex < blocksCount_Material.size(); materialIndex++) {
			Material material = blocksCount_Material.get(materialIndex);
			int count = blocksCount_counts.get(materialIndex);
			
			Massband.log.warning("  + " +material + ":  " + count);
			
		}
		Massband.log.warning("----------------");
	}
	
}
