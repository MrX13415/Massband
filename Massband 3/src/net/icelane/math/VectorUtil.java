package net.icelane.math;

import org.bukkit.util.Vector;

public class VectorUtil {

	/**
	 * Rotates a vector around the origin (0,0) by an angle in radians.  
	 * @param vec The vector to rotate.
	 * @param angle The angle in radians.
	 * @return The rotated vector.
	 */
	public static Vector rotate(Vector vec, double angle){
		double rad_cos = Math.cos(angle);
		double rad_sin = Math.sin(angle);
		
		Vector n = new Vector();
		n.setX(rad_cos * vec.getX() - rad_sin * vec.getY());
		n.setY(rad_sin * vec.getX() + rad_cos * vec.getY());
		
		return n;
	}
	
	/**
	 * Rotates a vector around the origin (0,0) by an angle in degrees.  
	 * @param vec The vector to rotate.
	 * @param angle The angle in degrees.
	 * @return The rotated vector.
	 */
	public static Vector rotateDeg(Vector vec, double angle){
		return rotate(vec, Math.toRadians(angle));
	}
	
	/**
	 * Returns a vector to a point between to points with the given distance from the first point.
	 * @param point1 The first point.
	 * @param point2 The second point.
	 * @param distance The distance between the new point and the first point.
	 * @return The vector to the point between the two given points.
	 */
	public static Vector getPointVector(Point point1, Point point2, double distance){		
		//double length = Math.ceil( GetVectorLength(p1, p2));   // length of p1->p2
		
		Vector vec = new Vector();
		vec.setX(point2.getX() - point1.getX());
		vec.setY(point2.getY() - point1.getY());
		double vec_m = (vec.getX() == 0) ? 1 : vec.getY() / vec.getX();   // 1 if normal vector
		
		Vector subvec = new Vector();

		if (vec.getX() != 0 && vec.getY() != 0){
			// calculate sub vector p1->p(x) with distance (index)
			subvec.setX(Math.abs(distance / Math.sqrt( 1 + Math.pow(vec_m, 2) )));
			subvec.setY(Math.abs(distance * vec_m / Math.sqrt( 1 + Math.pow(vec_m, 2) )));
		}
		
		if (vec.getX() == 0){
			// special case if there is a "normal vector" on x axis
			subvec.setX(0);
			subvec.setY(distance);
		}
		else if (vec.getY() == 0){
			// special case if there is a "normal vector" on y axis
			subvec.setX(distance);
			subvec.setY(0);
		}
		
		// correct direction (+ or -) for the sub vector
		if (vec.getX() < 0 && subvec.getX() > 0)
			subvec.setX(subvec.getX() * -1);
		if (vec.getY() < 0 && subvec.getY() > 0)
			subvec.setY(subvec.getY() * -1);
		
		return subvec;
	}
	
	/**
	 * Returns the cross sign between to vectors.
	 * @param v The first vector.
	 * @param w The second vector.
	 * @return True if cross is positive. False if negative or zero.
	 */
	public static boolean getCrossSign(Vector v, Vector w){
		return (v.getX() * w.getY()) > (w.getX() * v.getY());  //x1 * y2 > x2 * y1
	}
		
	/**
	 * Returns the angle between to vectors.
	 * @param v The first vector.
	 * @param w The second vector.
	 * @return The angle in degrees.
	 */
	public static double getAngle(Vector v, Vector w)
	{
		try{
			// get dot product
			double dot_prod = v.dot(w);

			// get magnitudes
			double magA = Math.pow(v.dot(v), 0.5);
			double magB = Math.pow(w.dot(w), 0.5);

			// get cosine value
			double cos = dot_prod / magA / magB;
			// get angle in radians and then convert to degrees
			double angle = Math.acos(cos);
			// basically doing angle <- angle mod 360
			double ang_deg = Math.toDegrees(angle) % 360;

			//#if ang_deg-180>=0:
			//# As in if statement
			//#    return 360 - ang_deg
			//#else:     

			return ang_deg;
		}
		catch(Exception ex){ }
		return 0;
	}
	
}
