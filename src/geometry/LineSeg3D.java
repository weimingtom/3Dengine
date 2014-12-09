package geometry;

public class LineSeg3D {
	public double x1, x2, y1, y2, z1, z2;
	public Point3D beg, end;
	public Vector vecequiv;
	
	public LineSeg3D(Point3D beg, Point3D end){
		this.x1 = beg.x;
		this.x2 = end.x;
		this.y1 = beg.y;
		this.y2 = end.y;
		this.z1 = beg.z;
		this.z2 = end.z;
		this.beg = beg;
		this.end = end;
		vecequiv = new Vector(beg, end);
	}
	
	public LineSeg3D(double x1, double x2, double y1, double y2, double z1, double z2){
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.z1 = z1;
		this.z2 = z2;
		this.beg = new Point3D(x1, y1, z1);
		this.end = new Point3D(x2, y2, z2);
		vecequiv = new Vector(this.beg, this.end);
	}
	
	public Vector toVector(){
		return this.vecequiv;
	}
	
	public boolean intersects(LineSeg3D th){
		return true;
	}
}
