package geometry;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/**
 * Holds points of a triangle in 3D.
 * The points are always in counter-clockwise order
 * as seen from the "outside" of the triangle's object, 
 * if it has an outside, and an object.
 */

public class Triangle3D implements Shape, Comparable<Triangle3D>{
	boolean inCube;
	Point3D[] points;		// Vertices of the triangle
	Triangle3D[] triangles; // Neighbor triangles. triangles[0] is on edge 0-1.
	public static Point3D cameraPos; // for use with our compareTo method
	
	/**
	 * Constructor
	 * Constructs a Triangle3D having the three given points as vertices
	 * @param p0  first vertex
	 * @param p1  second vertex
	 * @param p2  third vertex, going counter-clockwise around the triangle as seen from the "outside"
	 */
	public Triangle3D(Point3D p0, Point3D p1, Point3D p2){
		points = new Point3D[3];
		points[0] = p0;
		points[1] = p1;
		points[2] = p2;
	}
	
	public Triangle3D(Point3D p0, Point3D p1, Point3D p2, boolean ic){
		points = new Point3D[3];
		points[0] = p0;
		points[1] = p1;
		points[2] = p2;
		inCube = ic;
	}
	
	/**
	 * Prints a triple of the triangle's points.
	 */
	public String toString(){
		return "Triangle: (" + points[0].toString() + ", " + 
				points[1].toString() + ", " + points[2].toString() + ")";
	}
	
	/**
	 * Translates the triangle by adding point d to all three vertices
	 * @param d  the displacement vector
	 * @return
	 */
	public Triangle3D add(Point3D d){
		return new Triangle3D(points[0].add(d), points[1].add(d), points[2].add(d));
	}
	
	public boolean hasVertex(Point3D p){
		return points[0].equals(p) || points[1].equals(p) || points[2].equals(p);
	}

	@Override
	public int compareTo(Triangle3D o) {
		Double myDist = (points[0].x - cameraPos.x)*(points[0].x - cameraPos.x) +
				(points[0].y - cameraPos.y)*(points[0].y - cameraPos.y) +
				(points[0].z - cameraPos.z)*(points[0].z - cameraPos.z);
		Double otherDist = (o.points[0].x - cameraPos.x)*(o.points[0].x - cameraPos.x) +
				(o.points[0].y - cameraPos.y)*(o.points[0].y - cameraPos.y) +
				(o.points[0].z - cameraPos.z)*(o.points[0].z - cameraPos.z);
		return otherDist.compareTo(myDist);
	}

	@Override
	public boolean contains(Point2D arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Rectangle2D arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(double arg0, double arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(double arg0, double arg1, double arg2, double arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Rectangle getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle2D getBounds2D() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PathIterator getPathIterator(AffineTransform arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PathIterator getPathIterator(AffineTransform arg0, double arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean intersects(Rectangle2D arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean intersects(double arg0, double arg1, double arg2, double arg3) {
		// TODO Auto-generated method stub
		return false;
	}
}
