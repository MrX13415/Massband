package de.MrX13415.Massband;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CountBlocks extends Thread{
	
	private World world = null;
	private ArrayList<Vector> wayPoints = null;
	private PlayerVars tmpVars;
	private int blockCount = 0;
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
		
		//count ...
		blockCount = countBlocks(world, wayPoints);
//		System.out.println("count: " + blockCount);

		if (! interrupted) tmpVars.getPlayer().sendMessage(ChatColor.WHITE + "Content: " + ChatColor.GOLD + blockCount + ChatColor.WHITE + " Blocks" + ChatColor.GRAY + " (exept air)");
		
		threadEndRechearched = true;
		Massband.log.info(Massband.consoleOutputHeader + " Block-counting Thread from " + threadOwner.getName() + " was Interrupted");
		wasInterrupted();
		
//		System.out.println("Thread died: " + this.isAlive());
	}
		
	private int countBlocks(World world, ArrayList<Vector> wayPoints){
		int yStart = (int) Math.min(wayPoints.get(0).getY(), wayPoints.get(1).getY());
		int yEnd = (int) Math.max(wayPoints.get(0).getY(), wayPoints.get(1).getY());
		
		int zStart = (int) Math.min(wayPoints.get(0).getZ(), wayPoints.get(1).getZ());
		int zEnd = (int) Math.max(wayPoints.get(0).getZ(), wayPoints.get(1).getZ());
	
		int xStart = (int) Math.min(wayPoints.get(0).getX(), wayPoints.get(1).getX());
		int xEnd = (int) Math.max(wayPoints.get(0).getX(), wayPoints.get(1).getX());

		int blockCount = 0;
		
//		//perc-----------------
		//calculate dimensions
//		double dimensionWith = Math.abs(wayPoints.get(0).getX() - wayPoints.get(1).getX()) + 1;		
//		double dimensionLength = Math.abs(wayPoints.get(0).getZ() - wayPoints.get(1).getZ()) + 1;
//		double dimensionHieght = Math.abs(wayPoints.get(0).getY() - wayPoints.get(1).getY()) + 1;
//
//		int maxblocks = (int)(dimensionHieght * dimensionWith * dimensionLength);
		int percindex = 0;
		long timeBefore = System.currentTimeMillis();
		long timeAfter = timeBefore + 1000; //one second after ...
		long time = 0;
		int speed = 0;
		boolean measureSpeed = true;
//		double perc = 0;
//		//perc--------------
		
		for (int yIndex = yStart; yIndex <= yEnd; yIndex++)  {
			if (interrupted) break;
			for (int zIndex = zStart; zIndex <= zEnd; zIndex++) {
				if (interrupted) break;
				for (int xIndex = xStart; xIndex <= xEnd; xIndex++) {
					if (interrupted) break;
					
					//get Block
					Block b = Massband.server.getWorld(world.getName()).getBlockAt(xIndex, yIndex, zIndex);
					//count blocks except air ...
					if (b.getTypeId() != 0) blockCount++;
				
					//measure speed	...							
					if (measureSpeed) percindex += 1;
					if ((System.currentTimeMillis()) >= timeAfter && measureSpeed) {
						measureSpeed = false;
						speed = percindex;	
						tmpVars.getPlayer().sendMessage("Speed: " + ChatColor.GOLD + speed + ChatColor.WHITE + " Blocks/s");
					}

					if (Massband.configFile.blockCountingSpeedLimit) {
						try {
							Thread.sleep(0, 1);
						} catch (Exception e) {
						}
					}
					
				}
			}
//			//perc ---------------------
//			perc = 100d / (double)maxblocks * (double)percindex;
//			System.out.println("counting ... " + perc + "% (" + percindex + "/" + maxblocks + ")");
//			//perc --------------------
		}
		
		if (! interrupted) {
			time = System.currentTimeMillis() - timeBefore;
			tmpVars.getPlayer().sendMessage("Time: " + ChatColor.GOLD + time + ChatColor.WHITE + " ms");
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
		threadOwner.sendMessage(ChatColor.GRAY + "Tray to Interrrupt Block-counting ...");
		interrupted = true;
	}
	
	public void wasInterrupted(){
		if (threadEndRechearched) {
			if (threadOwner != null){
				Massband.getPlayerVars(threadOwner).setBlockCountingThread(null);
			}
			
			if (interrupted) threadOwner.sendMessage(ChatColor.GRAY + "Block counting Interrupted");
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
		String sender = "[SERVER]";
		Player player = null;
		
		if (commandSender instanceof Player) {
			player = (Player) commandSender;
			sender = player.getName();
		}
		
		if (player != null) {
			player.sendMessage(Massband.consoleOutputHeader + ChatColor.RED + " Try to interrupt all Threads ... (count: " + Massband.threads.size() + ")");
		}
		Massband.log.info(Massband.consoleOutputHeader + ChatColor.RED + " " + sender + " trys to interrupt all Threads ... (count: " + Massband.threads.size() + ")");
		
		for (int ThreadIndex = 0; ThreadIndex < Massband.threads.size(); ThreadIndex++) {
			CountBlocks thread = Massband.threads.get(ThreadIndex);
			if (thread.isAlive()){
				thread.interrupt();
				
				while (! thread.threadEndRechearched) {
				}
				if (thread.getOwner() != null) thread.getOwner().sendMessage(Massband.consoleOutputHeader + ChatColor.RED + " Your block-counting was interrupted by " + ChatColor.GOLD + sender);
			}
		}
		
		if (player != null) {
			player.sendMessage(Massband.consoleOutputHeader +  ChatColor.RED + " All running Threads interrupted ! (count: " + Massband.threads.size() + ")");
		}
		
		Massband.log.info(Massband.consoleOutputHeader + " All running Threads interrupted ! (count: " + Massband.threads.size() + ")");
	}
		
}
