package net.icelane.math;

import org.bukkit.util.Vector;

public class Point {

	private double x;
	private double y;
	private double z;
	
	public Point(){
		this(0,0,0);
	}
	
	public Point(double x, double y){
		this(x,y,0);
	}
	
	public Point(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public boolean equals(Point point) {
		return (this.getX() == point.getX()
				&& this.getY() == point.getY()
				&& this.getZ() == point.getZ());
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * Returns the distance between this and another point.
	 * @param point Another point to measure the distance to.
	 * @return The distance between the two points.
	 */
	public double getLength(Point point){
		return Point.getLength(this, point);
	}
	
	/**
	 * Returns the distance between two points.
	 * @param point1 The first point
	 * @param point2 The second point
	 * @return The distance between the given points.
	 */
	public static double getLength(Point point1, Point point2){
		return Math.sqrt( Math.pow(point2.getX() -  point1.getX(), 2) + Math.pow(point2.getY() - point1.getY(), 2) );
	}

}
