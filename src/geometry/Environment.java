package geometry;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class Environment {
    int WIDTH = 800;
    int HEIGHT = 600;
    DSArrayList<Point2D.Double> points;
    DSArrayList<Triangle3D> triangles;
    ArrayList<EnvironmentObject> objects;
    double[][] transform = {{1,0,0,0},{0,1,0,0},{0,0,1,0}, {0, 0, 0, 1}};
    Graphics2D graphics;                 // The rendering context
    Point2D.Double cameraLocation;
    Point2D.Double cameraDirection;
    double[][] leftRotationMatrix;
    double[][] rightRotationMatrix;
    double[][] moveForwardMatrix;
    double[][] moveBackwardMatrix;

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
        triangles = new DSArrayList<Triangle3D>();
        objects = new ArrayList<EnvironmentObject>();
        points = new DSArrayList<Point2D.Double>();
        leftRotationMatrix = makeLRRotation(-angleStep);
        rightRotationMatrix = makeLRRotation(angleStep);
        moveForwardMatrix = makeFBMatrix(-1);
        moveBackwardMatrix = makeFBMatrix(1);
    }

    public void render(Graphics2D graphics){
        graphics.setColor(Color.DARK_GRAY);
        graphics.fillRect(0, 0, WIDTH, WIDTH);
        graphics.setColor(Color.WHITE);
        
        // Render the triangles
        for(int i = 0; i < triangles.size(); i++)
            if(triangleIsVisible(triangles.get(i)) && triangleIsFacingCamera(triangles.get(i)))
                renderTriangle(triangles.get(i), graphics);
        
        // Render the EnvironmentObjects
        EnvironmentObject.cameraPos = new Point3D(cameraPos[0], cameraPos[1], 0);
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
                new Point3D(cameraPos[0], cameraPos[1], 0)) > 0;
                
    }

    private void renderTriangle(Triangle3D t, Graphics2D graphics){
        Point2D.Double[] pts = new Point2D.Double[3]; // 2D coords of projected triangles
        double[] x = new double[3];                      // Same thing, different form
        double[] y = new double[3];                      // ""

        // Transform them points form the 3D world to the 2D screen
        for(int i = 0; i < 3; i++){
            pts[i] = project(t.points[i]);
            x[i] = pts[i].x * WIDTH;
            y[i] = HEIGHT - pts[i].y * HEIGHT;
        }

        // Draw the outlines of the triangles
        graphics.setColor(Color.black);
        for(int i = 0; i < 3; i++)
            graphics.drawLine((int)x[i], (int)y[i], (int)x[(i+1)%3], (int)y[(i+1)%3]);
        
        // Draw the interiors of the triangles
        graphics.setColor(new Color(140, 180, 220, 200));
        Path2D.Double p = new Path2D.Double();
        p.moveTo(x[0], y[0]);
        p.lineTo(x[1], y[1]);
        p.lineTo(x[2], y[2]);
        p.closePath();
        graphics.fill(p);
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
        double[] cp = {cameraPos[0] + Math.sin(cameraAngle), cameraPos[1] - Math.cos(cameraAngle), 0, 0};
        cameraPos = cp;
        //System.out.printf("Camera pos: (%.2f, %.2f, %.2f) ", cameraPos[0], cameraPos[1], cameraPos[2]);
        //System.out.printf("%.2f  ", cameraAngle * 180 / Math.PI);
        //System.out.printf("Camera pos: (%.2f, %.2f, %.2f)\n", cameraDir[0], cameraDir[1], cameraDir[2]);
        updateTransform();
    }

    public void moveForward(){
        double[] cp = {cameraPos[0] - Math.sin(cameraAngle), cameraPos[1] + Math.cos(cameraAngle), 0, 0};
        cameraPos = cp;
        updateTransform();
    }
    
    public void nearCloser(){
        near -= 0.1;
    }
    
    public void nearFarther(){
        near += 0.1;
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