package geometry;

import java.util.HashMap;
import java.util.Scanner;

/**
 * This class contains static methods that can be useful
 * when doing 3D geometry.
 *
 */
public class Tools3D {
	
	/**
	 * You can use "main" for testing your code.
	 */
	public static void main(String[] args){
		// This test reads:
		// *  Nine ints giving the vertices (a,b,c),(d,e,f),(g,h,i) of a triangle
		// *  A number N giving the number of segments that follow
		// *  N lines of six ints each, the endpoints (p,q,r),(s,t,u) of the segments 
		// For each segment it says whether it pierces, shaves, nicks or misses the triangle
		/*Triangle3D t = new Triangle3D(new Point3D(17, 3, -1),
				new Point3D(3, -12, 17), new Point3D(7, 9, 11));
		HashMap<String, Integer> m = new HashMap<String, Integer>();
		m.put("Pierces", 0);
		m.put("Misses", 0);
		m.put("Nicks", 0);
		m.put("Shaves", 0);
		int count = 0;
		while(count < 40){
			Point3D A = new Point3D(Math.round(Math.random()*60 - 30),
					Math.round(Math.random()*60 - 30), Math.round(Math.random()*60 - 30));
			if(t.hasVertex(A))
				continue;
			Point3D B = new Point3D(Math.round(Math.random()*60 - 30),
					Math.round(Math.random()*60 - 30), Math.round(Math.random()*60 - 30));
			if(t.hasVertex(B))
				continue;
			String a = segTriIntersection(t, A, B);
			int v = m.get(a);
			if(v < 10){
				System.out.printf("%c %.0f %.0f %.0f %.0f %.0f %.0f\n", a.charAt(0), A.x, A.y, A.z, B.x, B.y, B.z);
				m.put(a, v+1);
				count++;
			}
		}*/
		
		Scanner scanner = new Scanner(System.in);
		Triangle3D t = readTriangle(scanner);
		int N     = scanner.nextInt();
		for(int j = 0; j < N; j++){
			Point3D p1 = readPoint(scanner);
			Point3D p2 = readPoint(scanner);
			System.out.println(segTriIntersection(t, p1, p2));
		}
	}

	/**
	 * Solves the 3D triangle-segment intersection problem
	 * @param t		A Triangle3D
	 * @param A 	A Point3D
	 * @param B 	A Point3D
	 * @return     Checks whether segment AB intersects triangle t.
	 * 				"Pierces" means that the segment intersects the interior of the triangle
	 * 				"Shaves" means that the segment intersects an edge of the triangle
	 * 				"Nicks" means that the segment intersects a vertex of the triangle
	 * 				"Misses" means it misses.
	 */
	public static String segTriIntersection(Triangle3D t, Point3D A, Point3D B){
		Point3D P = t.points[0];
		Point3D Q = t.points[1];
		Point3D R = t.points[2];
		
		if(sigmaVal(P, Q, R, A) * sigmaVal(P, Q, R, B) > 0)
			return "Misses";
		
		double spq = sigmaVal(A, P, Q, B);
		double sqr = sigmaVal(A, Q, R, B);
		double srp = sigmaVal(A, R, P, B);
		
		int nzero = 0;
		if(spq == 0) nzero++;
		if(sqr == 0) nzero++;
		if(srp == 0) nzero++;
		
		if(nzero == 1)
			return "Shaves";
		
		if(nzero == 2)
			return "Nicks";
		
		if((spq > 0 && sqr > 0 && srp > 0) ||
				(spq < 0 && sqr < 0 && srp < 0))
			return "Pierces";
		
		return "Misses";
	}
	
	/**
	 * Reads one Point3D object from the given Scanner object
	 *
	 * @param in The input source. Right now, needs to be a Scanner
	 * @return   A point read from s
	 */
	static Point3D readPoint(Object in){
		Point3D retVal = null;
		if(in instanceof Scanner){
			Scanner s = (Scanner)in;
			double a = s.nextDouble(); 
			double b = s.nextDouble();
			double c = s.nextDouble();
			retVal = new Point3D(a, b, c);
		}
		return retVal;
		
	}
	
	/**
	 * 
	 * @param   in The input source. Right now, needs to be a Scanner
	 * @return     A triangle read from in.
	 */
	static Triangle3D readTriangle(Object in){
		Triangle3D retVal = null;
		if(in instanceof Scanner){
			Scanner s = (Scanner)in;
			Point3D p1 = readPoint(s);
			Point3D p2 = readPoint(s);
			Point3D p3 = readPoint(s);
			retVal = new Triangle3D(p1, p2, p3);
		}
		return retVal;
		
	}
	

	/**
	 * Gives the value of the determinant obtained from four points. From this value,
	 * one could obtain the orientation of the four points in space, and the volume
	 * of their tetrahedron.
	 * @param A A Point3D
	 * @param B A Point3D
	 * @param C A Point3D
	 * @param D A Point3D
	 * @return The determinant of the 3x3 matrix whose rows are B-A, C-A, D-A.
	 * 
	 *  The return value will be six times the volume of the tetrahedron. 
	 *  If the return value is 0, then the points are co-planar.
	 *  A positive return value indicates that vertices of the triangle ABC
	 *  will appear in counter-clockwise order, as seen from point D. 
	 */
	public static double sigmaVal(Point3D A, Point3D B, Point3D C, Point3D D){
		Point3D b = B.multAndAdd(-1, A);
		Point3D c = C.multAndAdd(-1, A);
		Point3D d = D.multAndAdd(-1, A);
		
		return b.x*c.y*d.z + b.y*c.z*d.x + b.z*c.x*d.y -
				b.z*c.y*d.x - b.y*c.x*d.z - b.x*c.z*d.y;
	}
}
