package net.icelane.math;

import java.util.ArrayList;

import org.bukkit.util.Vector;

import net.icelane.massband.Server;

public class Polygon {

	private static long calcStartTime;
	
	/**
	 * Returns the calculated area of an polygon defined by the given points.
	 * </br>
	 * <b>Note:</b> If the polygon intersect itself, the resulting area will be wrong.
	 * @param points An array of points defining the polygon to calculate the area of.
	 * @return The area of the given polygon.
	 */
	public static double getArea(Point... points){
		if (points.length < 3) return 0;
		
		double area = 0;	
		for(int index_p0 = 0; index_p0 < points.length; index_p0++ ){
			int index_p1 = (index_p0 + 1) % points.length;  // index of the next point (p1 is next of p0)
			
			area += points[index_p0].getX() * points[index_p1].getY();
			area -= points[index_p1].getX() * points[index_p0].getY();
		}
		
		area = Math.abs(area) / 2.0d;		
		return area;
	}

	/**
	 * Weather the orientation of the polygon, defined by the given point array is clockwise or counter-clockwise.
	 * </br>
	 * <b>Note:</b> The result is twice the enclosed area, with a +/- convention.
	 * @param points The polygon as point array.
	 * @return Weather the polygon is clockwise orientated.
	 */
	public static boolean isClockwise(Point... points){
		if (points.length < 3) return false;
		
		double avg_direction = 0;
		
		for(int index = 0; index < points.length; index++){
			Point p1 = points[index];
			Point p2 = (index < (points.length - 1) ? points[index+1] : points[0]);
			
			avg_direction += (p2.getX() - p1.getX()) * (p2.getY() + p1.getY()); //(x2 - x1) * (y2 + y1)
			
			p1 = p2;
		}

		return (avg_direction >= 0);
	}

	/**
	 * Resizes the outline of a polygon by the given offset.</br>
	 * The offset value corresponds to the distance between the two edges of the base polygon to the target polygon.
	 * @param points The polygon as point array.
	 * @param offset The distance to expand or reduce the polygon.
	 * @return The resized polygon as point array.
	 */
	public static Point[] resize(Point[] points, double offset){
		if (points.length < 3) return points;
		
		boolean clockwise = isClockwise(points);
		ArrayList<Point> offPoints = new ArrayList<Point>();

		Point point1 = points[points.length - 1];
		Point point2 = null;
		
		for (int pointIndex = 0; pointIndex <= points.length; pointIndex++) {
			Point point3 = null;
			if (pointIndex < points.length) {
				point3 = points[pointIndex];
			}else {
				// add last point at the end
				point3 = points[0];
			}
			
			if (point2 != null){
				if (point1 != null){
					offPoints.add( getOffsetVertex(point1, point2, point3, offset, clockwise) );	
				}
				point1 = point2;
			}
			point2 = point3;
		}

		Point[] result = new Point[offPoints.size()];
		return offPoints.toArray(result);
	}
	
	/**
	 * Returns the new vertex point of two vector defined by three points
	 * when each vector gets moved about the given distance 90° to itself.
	 * </br>
	 * <b>Warning:</b></br>
	 * Description might not be accurate. To complex to explain properly.</br>
	 * So here be dragons. You have been warned!
	 * @param p1 The first point.
	 * @param p2 The second point.
	 * @param p3 The tired point. 
	 * @param distance The distance between the new and the old vector of (p1, p2, p3)
	 * @param clockwise Weather the points are clockwise orientated.
	 * @return The new vertex point.
	 */
	public static Point getOffsetVertex(Point p1, Point p2, Point p3, double distance, boolean clockwise){
		// vector p1->p2
		Vector vec1 = new Vector();
		vec1.setX(p2.getX() - p1.getX());
		vec1.setY(p2.getY() - p1.getY());
		
		// vector p2->p3
		Vector vec2 = new Vector();
		vec2.setX(p3.getX() - p2.getX());
		vec2.setY(p3.getY() - p2.getY());
		
		double delta = 180 - VectorUtil.getAngle(vec1, vec2);
		boolean isIn = VectorUtil.isPositivCrossSign(vec1, vec2);

		double beta = delta / 2.0; 
		double alpha = 90 - beta;
		double c = distance;
		
		// calculate distance from current to the new point ...
		// /!\ don't do this if beta=0! The resulting numbers would be huge!
		if (beta > 0) c = distance / Math.cos(Math.toRadians(alpha));
	
		Vector vecp = VectorUtil.getPointVector(p1, p2, c);
		
		// inner corners ...
		if (isIn){
			vecp.setX(vecp.getX() * -1);
			vecp.setY(vecp.getY() * -1);
		}
		
		if (!clockwise){
			vecp.setX(vecp.getX() * -1);
			vecp.setY(vecp.getY() * -1);
		}
		
		// inner corners ...
		if (isIn)
		{
			beta = beta * -1;
		}
		// if not clockwise: beta = beta * -1
		
		Vector vecResult = VectorUtil.rotateDeg(vecp, beta);
		
		return new Point(p2.getX() + vecResult.getX(), p2.getY() + vecResult.getY());
	}
	
	/**
	 * Returns the offset required to calculate the real block count of an "almost" block polygon.
	 * </br>
	 * Basically the function tries to find "diagonals" between the points and adds <code>0.5</code> for every diagonal found.
	 * @param points The polygon as point array.
	 * @return The offset.
	 */
	public static double getBlockPolyAreaOffset(Point[] points){
		if (points.length < 3) return 0; 
				
		// find diagonals between all points and calc area offset
		double offset = 0;
		Point pointPrev = null;
		
		for (Point point : points) {
			if (pointPrev != null){
				// if x and y is different we need to add 0.5 to our area
				// so each diagonal will also be counted as full block.
				if ( point.getX() != pointPrev.getX() && point.getY() != pointPrev.getY())
					offset += 0.5;
				}
			pointPrev = point;
		}
		return offset;
	}

	/**
	 * Returns the area or number of block fitting in an polygon area.
	 * </br>
	 * Assuming a block is 1x1.
	 * @param points The polygon as point array.
	 * @return The number of blocks fitting in the polygon area.
	 */
	public static long GetBlockArea(Point[] points){
		if (points.length < 3) return 0;
				
		// resize the polygon about a value of 0.5
		// to account for the blocks on the outline of input the polygon.
		Point[] block_points = resize(points, 0.5);
		
		// get a "almost blocky" polygon ...
		block_points = toBlockPoly( resize(points, 0.5) ); 
		
		// find diagonals between all points and calculate the area offset.
		double offset = getBlockPolyAreaOffset(block_points);
		
		// calculate the real block area.
		return Math.round( getArea(block_points) + offset );
	}
	
	/**
	 * Converts an ordinary polygon to "almost blocky" version of itself.
	 * </br>
	 * "Almost blocky", because there might be "diagonals" between the points in other words "half-blocks".
	 * The size of the one block is assumed to be 1x1.
	 * <br>
	 * @param points The polygon as point array.
	 * @return The "almost blocky" polygon as point array.
	 */
	public static Point[] toBlockPoly(Point[] points)
	{
		calcStartTime = System.currentTimeMillis();
		if (points.length < 3) return points; 
				
		ArrayList<Point> outPoints = new ArrayList<Point>();
		
		for (int pointIndex = 0; pointIndex <= points.length; pointIndex++) {
			Point point = null;
			if (pointIndex < points.length){
				point = points[pointIndex];
			}else{
				// add last point at the end
				point = points[0];
			}
			
			if (pointIndex > 0){
				
				Point pointPrev = new Point(points[pointIndex - 1].getX(), points[pointIndex - 1].getY());

				// length of vector p1->p2
				double vec_length = Math.ceil(pointPrev.getLength(point));
				
				// calculate vector p1->p2
				Vector vec = new Vector();
				vec.setX(point.getX() - pointPrev.getX());
				vec.setY(point.getY() - pointPrev.getY());
				double vec_m = (vec.getX() == 0) ? 1 : vec.getY() / vec.getX();   // 1 if normal vector
				
				long maxSubPointCount = (long) vec_length;
				
				// calculate sub points between points ...
				for (long subPointIndex = 0; subPointIndex < maxSubPointCount - 1; subPointIndex++) {
					// /!\ Safety time out, just in case the maxSubPointCount is way to big.
					if ((System.currentTimeMillis() - calcStartTime) > 3000) {
						Server.get().getConsoleSender().sendMessage("[Massband] §cWARNING: Polygone calculation timeout (3000ms) exeeded. Operation aborted!");
						return points;
					}
					
					Vector subvec = new Vector();
					
					if (vec.getX() != 0 && vec.getY() != 0){
						// calculate sub vector p1->p(x) with distance (index)))
						subvec.setX( Math.abs(subPointIndex         / Math.sqrt( 1 + Math.pow(vec_m, 2) )) );
						subvec.setY( Math.abs(subPointIndex * vec_m / Math.sqrt( 1 + Math.pow(vec_m, 2) )) );
					}
										
					if (vec.getX() == 0){
						// special case if there is a "normal vector" on y axis
						subvec.setX(0);
						subvec.setY(subPointIndex);
					}else if(vec.getY() == 0){
						// special case if there is a "normal vector" on x axis
						subvec.setX(subPointIndex);
						subvec.setY(0);
					}
					
					// Correct direction (+ or -) for the sub vector
					subvec.setX( (vec.getX() < 0 && subvec.getX() > 0) ? subvec.getX() * -1 : subvec.getX() );
					subvec.setY( (vec.getY() < 0 && subvec.getY() > 0) ? subvec.getY() * -1 : subvec.getY() );
					
					// calculate point on vector
					Point subpoint = new Point();
					subpoint.setX( Math.round(pointPrev.getX() + subvec.getX()) );
					subpoint.setY( Math.round(pointPrev.getY() + subvec.getY()) );
					
					// add new point and prevent duplicate points ...
					if ( !outPoints.get(outPoints.size() - 1).equals(subpoint) ){
						outPoints.add(subpoint);
					}
					
				}	
			}
			
			// add current point2
			if (pointIndex < points.length){
				outPoints.add( new Point( Math.round(point.getX()), Math.round(point.getY())) );
			}
		}
		
		Point[] result = new Point[outPoints.size()];
		return outPoints.toArray(result);
	}

}
