package geometry;

public class Vector {
	double x,y,z;
	public Vector(Point3D one, Point3D two){
		this.x = two.x - one.x;
		this.y = two.y - one.y;
		this.z = two.z - one.z;
	}
	public Vector(double x,double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vector(Point3D p){
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}
	public Vector(LineSeg3D l){
		this.x = l.vecequiv.x;
		this.y = l.vecequiv.y;
		this.z = l.vecequiv.z;
	}
	public Vector cross(Vector v){
		double i = this.y*v.z - this.z - v.y;
		double j = -(this.x*v.z - this.z*v.x);
		double k = this.x*v.y - this.y*v.x;
		return new Vector(i,j,k);
	}
	public double dot(Vector v){
		return this.x*v.x + this.y*v.y + this.z*v.z;
	}
	public double magnitude(){
		return Math.sqrt(x*x+y*y+z*z);
	}
	public double theta(Vector v){
		return Math.acos(this.dot(v)/(this.magnitude()*v.magnitude()));
	}
	public boolean equals(Vector v){
		return (this.x == v.x && this.y == v.y && this.z == v.z);
	}
	public Point3D toPoint3D(){
		return new Point3D(this.x, this.y, this.z);
	}
}
