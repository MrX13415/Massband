package net.icelane.math;

import java.util.ArrayList;

import org.bukkit.util.Vector;

public class Polygon {

	/**
	 * Returns the calculated area of an polygon defined by the given points.
	 * </br>
	 * Note: If the polygon intersect itself, the resulting area will be wrong.
	 * @param points An array of points defining the polygon to calculate the area of.
	 * @return The area of the given polygon.
	 */
	public static double getArea(Point... points){
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
	 * Note: The result is twice the enclosed area, with a +/- convention.
	 * @param points The polygon as point array.
	 * @return Weather the polygon is clockwise orientated.
	 */
	public static boolean isClockwise(Point... points){
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
	 * Resizes the outline of a polygon by the given offset.
	 * The offset value corresponds to the distance between the two edges of the base polygon to the target polygon.
	 * @param points The polygon as point array.
	 * @param offset The distance to expand or reduce the polygon.
	 * @return The resized polygon as point array.
	 */
	public static Point[] resize(Point[] points, double offset){
		ArrayList<Point> forPoints = new ArrayList<Point>();
		for (Point point : points) {
			forPoints.add(point);
		}
		
		boolean clockwise = isClockwise(points);
		ArrayList<Point> offPoints = new ArrayList<Point>();

		Point p1 = points[points.length - 1];
		Point p2 = null;
		
		forPoints.add(points[0]);
		
		for (Point p3 : forPoints) {
			if (p2 != null){
				if (p1 != null){
					offPoints.add( getOffsetEdge(p1, p2, p3, offset, clockwise) );	
				}
				p1 = p2;
			}
			p2 = p3;
		}

		Point[] result = new Point[offPoints.size()];
		return offPoints.toArray(result);
	}
	
	/**
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param distance
	 * @param clockwise
	 * @return
	 */
	public static Point getOffsetEdge(Point p1, Point p2, Point p3, double distance, boolean clockwise){
		Vector vec1 = new Vector();
		vec1.setX(p2.getX() - p1.getX());
		vec1.setY(p2.getY() - p2.getY());
		
		Vector vec2 = new Vector();
		vec2.setX(p3.getX() - p2.getX());
		vec2.setY(p3.getY() - p2.getY());
		
		double delta = 180 - VectorUtil.getAngle(vec1, vec2);
		boolean isIn = VectorUtil.getCrossSign(vec1, vec2);

		double beta = delta / 2.0; 
		double alpha = 90 - beta;
		double c = distance / Math.cos(Math.toRadians(alpha));
	
		Vector vecp = VectorUtil.getPointVector(p1, p2, c);
		
		if (isIn){
			vecp.setX(vecp.getX() * -1);
			vecp.setY(vecp.getY() * -1);
		}
		
		if (!clockwise){
			vecp.setX(vecp.getX() * -1);
			vecp.setX(vecp.getX() * -1);
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
	
	
	public static double CalcBlockPolyAreaOffset(Point[] points){
	// find diagonals between all points and calc area offset
	double offset = 0;
	Point pointPrev = null;
	for (Point point : points) {
	if (pointPrev != null){
	// if x and y is different we need to add 0.5 to our area
	// so each dialgonal will also be counted as full block.
	if ( point.getX() != pointPrev.getX() && point.getY() != pointPrev.getY())
		offset += 0.5;
	}
	
	pointPrev = point;
	}
	
	return offset;
	}

	
	public static long GetBlockArea(Point[] points){
		// calulate (almost) block poly-points ...
		Point[] block_points = toBlockPoly( points ); // extendPoly(
		
		// find diagonals between all points and calc area offset
		double offset = CalcBlockPolyAreaOffset(block_points);
		
		// calc block area
		return Math.round( getArea(block_points) + offset );
	}
	
	public static Point[] toBlockPoly(Point[] points)
	{
		ArrayList<Point> outPoints = new ArrayList<Point>();
		
		for (int pointIndex = 0; pointIndex < points.length; pointIndex++) {
			Point point = points[pointIndex];
			
			// add last point at the end
			if (pointIndex == points.length - 1){
				point = points[0];
			}
			
			if (pointIndex > 0){
				
				Point pointPrev = new Point(points[pointIndex - 1].getX(), points[pointIndex - 1].getY());

				// length of vector p1->p2
				double vec_length = Math.ceil(pointPrev.getLength(point));
				
				// calculate vector p1->p2
				Vector vec = new Vector();
				vec.setX(point.getX() - pointPrev.getX());
				vec.setX(point.getY() - pointPrev.getY());
				double vec_m = (vec.getX() == 0) ? 1 : vec.getY() / vec.getX();   // 1 if normal vector
				 
				// calculate sub points between points ...
				for (int subPointIndex = 0; subPointIndex < vec_length - 1; subPointIndex++) {
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
					subvec.setX( (vec.getY() < 0 && subvec.getY() > 0) ? subvec.getY() * -1 : subvec.getY() );
					
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
