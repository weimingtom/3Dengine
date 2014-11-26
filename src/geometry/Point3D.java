package geometry;


public class Point3D {
   double x, y, z;		
   
   public Point3D(double xx, double yy, double zz){
	   x = xx;
	   y = yy;
	   z = zz;
   }
   
   public Point3D(double[] coords){
	   x = coords[0];
	   y = coords[1];
	   z = coords[2];
   }
   
   double[] getCoords(){
	   double[] rv = new double[4];
	   rv[0] = x;
	   rv[1] = y;
	   rv[2] = z;
	   rv[3] = 1;
	   return rv;
   }
   
   public Point3D add(Point3D p){
	   return new Point3D(x + p.x, y + p.y, z + p.z);
   }
   
   public Point3D multAndAdd(double factor, Point3D p){
	   return new Point3D(x + factor*p.x, y + factor*p.y, z + factor*p.z);
   }
   
   public String toString(){
	   return String.format("(%.0f,  %.0f, %.0f)", x, y, z);
   }
   
   public boolean equals(Point3D p){
	   return x == p.x && y == p.y && z == p.z;
   }
}
