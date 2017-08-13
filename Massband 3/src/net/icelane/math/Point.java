package net.icelane.math;

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
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Point) return equals((Point) obj);
		return false;
	}
	
	/**
	 * Weather the X, Y and Z coordinates of the given point it equals to this point.
	 * @param point A point to compare with.
	 * @return True if both points are equal.
	 */
	public boolean equals(Point point) {
		return (this.getX() == point.getX()
				&& this.getY() == point.getY()
				&& this.getZ() == point.getZ());
	}
	
	@Override
	public String toString() {
		return String.format("(x: %s, y: %s, z: %s)", x, y, z);
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
	 * Returns a two dimensional (2D) copy of this point object where the X and Z coordinates are swapped.
	 * @return A new point object.
	 */
	public Point get2D_YZ() {
		return new Point(z, y, 0);
	}

	/**
	 * Returns a two dimensional (2D) copy of this point object where the Y and Z coordinates are swapped.
	 * @return A new point object.
	 */
	public Point get2D_XZ() {
		return new Point(x, z, 0);
	}

	/**
	 * Returns a two dimensional (2D) copy of this point object.
	 * @return A new point object.
	 */
	public Point get2D_XY() {
		return new Point(x, y, 0);
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
