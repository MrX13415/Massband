package de.MrX13415.Massband;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PlayerVars{

	private Player player;
	
	private double lenght = 0;
	
	private ArrayList<Vector> wayPoints = new ArrayList<Vector>();
	
	private boolean ignoreHeight = true;	//Y-axe
	
	public PlayerVars(Player player){
		this.player = player;
		removeAllWayPoints();
	}
	
	public void addVector(int x, int y, int z) {
		wayPoints.add(new Vector(x, y, z));
	}
	
	public Vector getVector(int index) {
		return wayPoints.get(index);
	}
	
	public int getWayPointListSize(){
		return wayPoints.size();
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
