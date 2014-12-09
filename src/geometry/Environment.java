package geometry;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.imageio.ImageIO;



public class Environment {
	int WIDTH = 800;
	int HEIGHT = 600;
	DSArrayList<Point2D.Double> points;
	ArrayList<Triangle3D> triangles;
	ArrayList<EnvironmentObject> objects;
	double[][] transform = {{1,0,0,0},{0,1,0,0},{0,0,1,0}, {0, 0, 0, 1}};
	Rectangle r = new Rectangle(WIDTH,HEIGHT);

	Graphics2D graphics;     //NOT USED            // The rendering context

	//buffered image for texturing
	public BufferedImage loadTheImage(){
		BufferedImage img = null;
		try{
			img = ImageIO.read(new File("/home/kbeine/blackandwhite.jpg"));
		}catch(IOException e){
			System.err.print("Did not load texture");
		}
		return img;
	}
	
	/*Color color = Color.cyan;
	IndexColorModel colorModel = new IndexColorModel(1, 2,
			new byte[]{0, (byte) color.getRed()},
			new byte[]{0, (byte) color.getGreen()},
			new byte[]{0, (byte) color.getBlue()},0);
	BufferedImage textureimg = new BufferedImage(20, 20
			, BufferedImage.TYPE_BYTE_INDEXED, colorModel);*/
	BufferedImage textureimg = loadTheImage();
	TexturePaint TEXTURE = new TexturePaint(textureimg, r);
	GradientPaint gradient = new GradientPaint(10,10, Color.white, 50,50, Color.blue, true);
	Point2D.Double cameraLocation;
	Point2D.Double cameraDirection;
	double[][] leftRotationMatrix;
	double[][] rightRotationMatrix;
	double[][] moveForwardMatrix;
	double[][] moveBackwardMatrix;


	/**
	 * kind of working.....
	 * if the light source is outside the frame it works.
	 * if he light source is directly above the center, half lights up and half is dark....
	 */

	Point3D lightSource = new Point3D(1,1,1);

	// Locations of the view frustrum planes
	double near = 1;
	double left = -1;
	double right = +1;
	double top = 1;
	double bottom = -1;

	// Camera position
	double[] cameraPos = {0, 0, 0, 1};
	double   cameraAngle = 0;
	double   angleStep = 0.05;

	public Environment(){
		triangles = new ArrayList<Triangle3D>();
		objects = new ArrayList<EnvironmentObject>();
		points = new DSArrayList<Point2D.Double>();
		leftRotationMatrix = makeLRRotation(-angleStep);
		rightRotationMatrix = makeLRRotation(angleStep);
		moveForwardMatrix = makeFBMatrix(-1);
		moveBackwardMatrix = makeFBMatrix(1);
	}

	public void render(Graphics2D graphics){
		//testing gradient painting on background
		graphics.setPaint(gradient);
		graphics.fillRect(0, 0, WIDTH, WIDTH);
		graphics.setColor(Color.WHITE);

		// Render the triangles
		Triangle3D.cameraPos = new Point3D(cameraPos[0], cameraPos[1], cameraPos[2]);
		Collections.sort(triangles);
		for(int i = 0; i < triangles.size(); i++)
			if(triangleIsVisible(triangles.get(i)) && triangleIsFacingCamera(triangles.get(i)))
				renderTriangle(triangles.get(i), graphics);

		// Render the EnvironmentObjects
		EnvironmentObject.cameraPos = new Point3D(cameraPos[0], cameraPos[1], cameraPos[2]);
		Collections.sort(objects);
		for(int i = 0; i < objects.size(); i++)
			renderObject(objects.get(i), graphics);
	}

	private boolean triangleIsVisible(Triangle3D t){
		Point2D.Double cameraVec = new Point2D.Double(-Math.sin(cameraAngle), Math.cos(cameraAngle));
		for(int i = 0; i < 3; i++){
			if((t.points[i].x - cameraPos[0])*cameraVec.x + (t.points[i].y - cameraPos[1])*cameraVec.y < 0)
				return false;
		}
		return true;
	}

	private boolean triangleIsFacingCamera(Triangle3D t){
		return sigmaVal(t.points[0], t.points[1], t.points[2],
				new Point3D(cameraPos[0], cameraPos[1], cameraPos[2])) > 0;

	}

	private void renderTriangle(Triangle3D t, Graphics2D graphics){
		Point2D.Double[] pts = new Point2D.Double[3]; // 2D coords of projected triangles
		double[] x = new double[3];                      // Same thing, different form
		double[] y = new double[3];                      // ""
		Path2D.Double p = new Path2D.Double();

		// Transform them points form the 3D world to the 2D screen
		for(int i = 0; i < 3; i++){
			pts[i] = project(t.points[i]);
			x[i] = pts[i].x * WIDTH;
			y[i] = HEIGHT - pts[i].y * HEIGHT;
		}

		// Draw the outlines of the triangles
		        graphics.setColor(Color.red);
		        for(int i = 0; i < 3; i++)
		            graphics.drawLine((int)x[i], (int)y[i], (int)x[(i+1)%3], (int)y[(i+1)%3]);

		// Draw the interiors of the triangles

		
		
		if(!t.onFloor){
			Point3D[] test = t.getHypotenuse();
			Point3D midpoint = new Point3D(test[0].x/2+test[1].x/2, 
					test[0].y/2+test[1].y/2, test[0].z/2+ test[1].z/2);
			double a = midpoint.x-cameraPos[0];
			double b = midpoint.y-cameraPos[1];
			double c = midpoint.z-cameraPos[2];
			double dist = Math.sqrt((a*a)+(b*b)+(c*c));
			Point3D tlc = t.topleftpoint();
			Point3D l = test[0];
			Point3D r = test[1];
			double factor;
			double rwi = l.distance(tlc)+r.distance(tlc);
			
			double rhi = (pts[0].x - pts[1].x)*(pts[0].x - pts[1].x) + (pts[0].y-pts[1].y)*(pts[0].y-pts[1].y);
			rhi = Math.sqrt(rhi);
			
			if(dist<1)
				factor = 1000;
			//else if (dist> 1000)
				//factor = 1;
			else
				factor = 5000/Math.sqrt((a*a) + (b*b) + (c*c)); //factor to shrink/enlarge texture based on triangle's distance from camera.
			
			
			TexturePaint texture;
			System.out.println(rwi);
		
			texture = new TexturePaint(textureimg,
					new Rectangle2D.Double(x[0], y[0],
							factor, factor));
			
			graphics.setPaint(texture);
			p.moveTo(x[0], y[0]);
			p.lineTo(x[1], y[1]);
			p.lineTo(x[2], y[2]);
			p.closePath();
			graphics.fill(p);
			
		} else{

			double shade = shadeTriangle(lightSource,t);
			// applies a shade to the current triangle based on the angle of the vectors. 
			// maybe call the color a different way to get a larger variety of colors, instead of 255.
			if(shade<=1&&shade>=0){

				Color C = Color.getHSBColor((float)(.47*shade), (float)(.75*shade),(float)(.9*shade));
				graphics.setPaint(C);
			}
			else{  
				shade=0.1;

				Color C = Color.getHSBColor((float)(.47*.1), (float)(.75*.1),(float)(.9*.1));
				graphics.setPaint(C);
			}
			//int alpha = Math.abs((int)(255-factor)) > 255 ? 255 : Math.abs((int)(255-factor));
			//Color C = new Color(0,0,0,alpha);
			//graphics.setPaint(C);
			p.moveTo(x[0], y[0]);
			p.lineTo(x[1], y[1]);
			p.lineTo(x[2], y[2]);
			p.closePath();
			graphics.fill(p);
		}
	}

	private double shadeTriangle(Point3D lightSource, Triangle3D face){
		/* need a vector from light to center of the face of the object
		 * need normal vector from surface
		 * normalize the vectors
		 * then
		 * cos(theta) = N dot L   theta will be between 0 and 1
		 * 
		 */

		Point3D p0 = face.points[0];
		Point3D p1 = face.points[1];
		Point3D p2 = face.points[2];
		double x = (p0.x+p1.x+p2.x)/3;
		double y = (p0.y+p1.y+p2.y)/3;
		double z = (p0.z+p1.z+p2.z)/3;
		Point3D centerFace = new Point3D(x,y,z);

		// the light vector, vector from triangle to light source, so norm and lv are in the same direction
		Point3D lv = new Point3D(lightSource.x-centerFace.x,lightSource.y-centerFace.y,lightSource.z-centerFace.z);
		Point3D sideA = new Point3D(p0.x-p1.x,p0.y-p1.y,p0.z-p1.z);
		Point3D sideB = new Point3D(p0.x-p2.x,p0.y-p2.y,p0.z-p2.z);
		Point3D norm = vectorCross(sideA,sideB);
		norm = normalizeVect(norm);
		lv = normalizeVect(lv);

		double cosTheta = dotProduct(lv,norm);
		return cosTheta;
	}

	private Point3D vectorCross(Point3D A, Point3D B){
		double z = A.x*B.y-A.y*B.x;
		double x = A.y*B.z-A.z*B.y;
		double y = A.z*B.x-A.x*B.z;
		Point3D norm = new Point3D(x,y,z);
		return norm;
	}
	
	private Point3D normalizeVect(Point3D v){
		double mag = Math.sqrt(v.x*v.x+v.y*v.y+v.z*v.z);
		Point3D rv = new Point3D(v.x/mag,v.y/mag,v.z/mag);
		return rv;
	}
	
	private double dotProduct(Point3D a,Point3D b){
		double rv = a.x*b.x+a.y*b.y+a.z*b.z;
		return rv;
	}
	
	private void renderObject(EnvironmentObject o, Graphics2D graphics){
		DSArrayList<Triangle3D> objectTriangles = o.getTriangles();
		for(int ti = 0; ti < objectTriangles.size(); ti++)
			if(triangleIsVisible(objectTriangles.get(ti)) &&
					triangleIsFacingCamera(objectTriangles.get(ti)))
				renderTriangle(objectTriangles.get(ti), graphics);
	}

	private Point2D.Double project(Point3D q){
		// First we transform the point in 3-space
		Point3D p = new Point3D(vecMatMultiply(q.getCoords(), transform));
		Point2D.Double rv = new Point2D.Double();
		double x = (near*(p.x/p.y) - left)/(right - left);
		double y = (near*(p.z/p.y) - bottom)/(top - bottom);
		rv.x = x;
		rv.y = y;
		return rv;
	}

	public void addTriangle(Triangle3D t){
		triangles.add(t);
	}

	public void addObject(EnvironmentObject o){
		objects.add(o);
	}

	public void rotateRight(){
		cameraAngle -= angleStep;
		updateTransform();
	}

	public void rotateLeft(){
		cameraAngle += angleStep;
		updateTransform();
	}

	public void moveBackward(){
		double[] cp = {cameraPos[0] + Math.sin(cameraAngle), cameraPos[1] - Math.cos(cameraAngle), 
				cameraPos[2], 0};
		cameraPos = cp;
		//System.out.printf("Camera pos: (%.2f, %.2f, %.2f) ", cameraPos[0], cameraPos[1], cameraPos[2]);
		//System.out.printf("%.2f  ", cameraAngle * 180 / Math.PI);
		//System.out.printf("Camera pos: (%.2f, %.2f, %.2f)\n", cameraDir[0], cameraDir[1], cameraDir[2]);
		updateTransform();
	}

	public void moveForward(){
		double[] cp = {cameraPos[0] - Math.sin(cameraAngle), cameraPos[1] + Math.cos(cameraAngle), 
				cameraPos[2], 0};
		cameraPos = cp;
		updateTransform();
	}

	public void nearCloser(){
		near -= 0.1;
	}

	public void nearFarther(){
		near += 0.1;
	}

	public void moveUp(){
		cameraPos[2] += 0.5;
		updateTransform();
	}

	public void moveDown(){
		cameraPos[2] -= 0.5;
		updateTransform();
	}
	public void moveLightNorth(){
		lightSource = lightSource.translate(0, 1, 0);
	}

	public void moveLightSouth(){
		lightSource = lightSource.translate(0, -1, 0);
	}
	public void moveLightEast(){
		lightSource = lightSource.translate(1, 0, 0);
	}

	public void moveLightWest(){
		lightSource = lightSource.translate(-1, 0, 0);
	}

	public void moveLightUp(){
		lightSource = lightSource.translate(0, 0, 1);
	}

	public void moveLightDown(){
		if(lightSource.z > -10)
			lightSource = lightSource.translate(0, 0, -1);
		else
			return;
	}

	public void summonLight(){
		lightSource = new Point3D(cameraPos[0],cameraPos[1],cameraPos[2]);
	}
	
	public void moveLight(Point3D pts){
		lightSource = pts;
	}

	public void teleport(){
		cameraPos[0] = lightSource.x;
		cameraPos[1] = lightSource.y;
		cameraPos[2] = lightSource.z;
		updateTransform();

	}

	private void updateTransform(){
		double[][] newTransform = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0},
				{-cameraPos[0], -cameraPos[1], -cameraPos[2], 1}};
		transform = matMatMultiply(newTransform, makeLRRotation(-cameraAngle));
	}

	private double[][] matMatMultiply(double[][] m1, double[][] m2){
		double[][] newMat = new double[4][4];

		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)
				for(int k = 0; k < 4; k++)
					newMat[i][j] += m1[i][k] * m2[k][j];
		return newMat;
	}

	private double[] vecMatMultiply(double[] v, double[][]m){
		double newVec[] = new double[4];
		for(int j = 0; j < 4; j++)
			for(int k = 0; k < 4; k++)
				newVec[j] += v[k] * m[k][j];
		return newVec;

	}

	// Returns v1 + m*v2
	private double[] vecVecMultAndAdd(double[] v1, double m, double[] v2){
		double[] rv = {v1[0] + m*v2[0], v1[1] + m*v2[1], v1[2] + m*v2[2], v1[3] + m*v2[3]};
		return rv;
	}

	private double[][] makeLRRotation(double angle){
		double rv[][] =  {
				{Math.cos(angle), Math.sin(angle), 0, 0},
				{-Math.sin(angle), Math.cos(angle), 0, 0},
				{0, 0, 1, 0}, {0, 0, 0, 1}};
		return rv;
	}

	private double[][] makeFBMatrix(double distance){
		double rv[][] = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, distance, 0, 1}};
		return rv;
	}

	private double sigmaVal(Point3D A, Point3D B, Point3D C, Point3D D){
		Point3D b = B.multAndAdd(-1, A);
		Point3D c = C.multAndAdd(-1, A);
		Point3D d = D.multAndAdd(-1, A);

		return b.x*c.y*d.z + b.y*c.z*d.x + b.z*c.x*d.y -
				b.z*c.y*d.x - b.y*c.x*d.z - b.x*c.z*d.y;
	}

}