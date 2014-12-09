package geometry;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Arena extends JPanel implements KeyListener {
	boolean paused = false;
	boolean suncycle = false;
	static Arena arena;
	Environment e;
	Timer timer;
	HashSet<Character>     keysDown;	// Holds the keys that are currently down.
	RenderingHints rh;					// for drawing the graphics
	int t; //an attempt at making the suncycle work

	public static void main(String[] args) {
		// Set up the arena and an environment
		arena = new Arena();
		Environment e = new Environment();
		arena.e = e;
		arena.buildEnvironment();
		arena.keysDown = new HashSet<Character>();
		arena.setPreferredSize(new Dimension(e.WIDTH, e.HEIGHT));
		arena.rh = new RenderingHints(null);
		//arena.rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Set up a JFrame to hold our arena (Jpanel)
		JFrame arenaFrame = new JFrame();
		arenaFrame.addKeyListener(arena);
		arenaFrame.add(arena);
		arenaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		arenaFrame.setLocation(500, 50);
		arenaFrame.pack();
		arenaFrame.setVisible(true);

		// Start the arena's run loop
		arena.timer = new Timer(30, arena.runLoop);
		arena.timer.start();

	}
	

	@Override
	public void keyPressed(KeyEvent ke) {
		keysDown.add(ke.getKeyChar());
	}

	@Override
	public void keyReleased(KeyEvent ke) {
		if(!keysDown.remove(ke.getKeyChar()))
			System.err.println("Arena: Removing a key that is not in the keysDown set.");
	}

	@Override
	public void keyTyped(KeyEvent ke) {
	}


	public void paint(Graphics g){
		Graphics2D graphics = (Graphics2D) g;
		graphics.addRenderingHints(rh);
		e.render(graphics);	
	}


	/**
	 * In the main() method we started a Timer to go off so many times per second.
	 * This runLoop method is called every time the timer goes off.
	 * It inspects the HashSet of keys that are currently pressed, and for each
	 * that is currently pressed, performs some appropriate action.
	 */
	ActionListener runLoop = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(keysDown.contains('d'))
				e.rotateRight();
			if(keysDown.contains('D'))
				e.rotateRight();
			if(keysDown.contains('a'))
				e.rotateLeft();
			if(keysDown.contains('A'))
				e.rotateLeft();
			if(keysDown.contains('s'))
				e.moveBackward();
			if(keysDown.contains('S'))
				e.moveBackward();
			if(keysDown.contains('w'))
				e.moveForward();
			if(keysDown.contains('W'))
				e.moveForward();
			if(keysDown.contains('j'))
				e.moveDown();
			if(keysDown.contains('J'))
				e.moveDown();
			if(keysDown.contains('l'))
				e.moveUp();
			if(keysDown.contains('L'))
				e.moveUp();
			if(keysDown.contains('['))
				e.nearFarther();
			if(keysDown.contains(']'))
				e.nearCloser();
			if(keysDown.contains('g')){
				if(suncycle)
					return;
				else
					e.moveLightNorth();
			}
			if(keysDown.contains('G')){
				if(suncycle)
					return;
				else
					e.moveLightNorth();
			}
			if(keysDown.contains('v')){
				if(suncycle)
					return;
				else
					e.moveLightWest();
			}				
			if(keysDown.contains('V')){
				if(suncycle)
					return;
				else
					e.moveLightWest();
			}				
			if(keysDown.contains('b')){
				if(suncycle)
					return;
				else
					e.moveLightSouth();
			}
			if(keysDown.contains('B')){
				if(suncycle)
					return;
				else
					e.moveLightSouth();
			}
			if(keysDown.contains('n')){
				if(suncycle)
					return;
				else
					e.moveLightEast();
			}
			if(keysDown.contains('N')){
				if(suncycle)
					return;
				else
					e.moveLightEast();
			}
			if(keysDown.contains('i')){
				if(suncycle)
					return;
				else
					e.moveLightUp();
			}
			if(keysDown.contains('I')){
				if(suncycle)
					return;
				else
					e.moveLightUp();
			}
			if(keysDown.contains('k')){
				if(suncycle)
					return;
				else
					e.moveLightDown();
			}
			if(keysDown.contains('K')){
				if(suncycle)
					return;
				else
					e.moveLightDown();
			}
			if(keysDown.contains(' ')){
				if(suncycle)
					return;
				else
					e.summonLight();
			}
			if(keysDown.contains('t'))
				e.teleport();
			if(keysDown.contains('T'))
				e.teleport();
			if(keysDown.contains('p'))
				paused = !paused;
			if(keysDown.contains('P'))
				paused = !paused;
			if(keysDown.contains('u')){
				if(suncycle)
					return;
				else{
					suncycle = true;
					t=0;
					e.lightSource = new Point3D(-150,150,-0);
					runSunCycle();
				}
			}
			if(keysDown.contains('U')){
				if(suncycle)
					return;
				else{
					suncycle = true;
					t = 0;
					e.lightSource = new Point3D(-150,150,-0);
					runSunCycle();
				}
			}
			if(suncycle)
				runSunCycle();
			moveEnvironmentObjects();
			repaint();
		}
	};

	private void moveEnvironmentObjects(){
		if(paused){
			return;
		}
		double factor = 200;
		int k = 3;
		for(int i = 0; i < e.objects.size(); i++){
			EnvironmentObject eo = e.objects.get(i);
			double cx = eo.center.x;
			double cy = eo.center.y;
			double cz = eo.center.z;
			eo.center.x -= cy / factor;
			eo.center.y += cx / factor;
			for(int j = 0; j < eo.triangles.size(); j++){
				eo.triangles.get(j).onFloor = eo.triangles.get(j).onFloor;
			}
		}
	}

	private void runSunCycle(){
		while(t<150){
			if(System.currentTimeMillis()%1000 == 0){
				e.moveLightUp();
				e.moveLightEast();
				e.moveLightSouth();
				t++;
			}
		}
		while(t>148){
			if(System.currentTimeMillis()%100 == 0){
				e.moveLightDown();
				e.moveLightEast();
				e.moveLightSouth();
				t++;
			}
			if(t ==300){
				suncycle = false;
				return;
			}
		}
	}

	/**
	 * Creates a (random) environment 
	 */
	private void buildEnvironment(){
		e = new Environment();

		// Build the floor
		int factor = 5;
		for(int i = -30; i <= 30; i++){
			for(int j = -30; j <= 30; j++){
				e.addTriangle(new Triangle3D(
						new Point3D(i*factor, j*factor, -10),
						new Point3D((i+1)*factor, j*factor, -10),
						new Point3D((i+1)*factor, (j+1)*factor, -10), true));
				e.addTriangle(new Triangle3D(
						new Point3D(i*factor, j*factor, -10),
						new Point3D((i+1)*factor, (j+1)*factor, -10),
						new Point3D(i*factor, (j+1)*factor, -10), true));
			}
		}

		factor = 100;
		for(int i = 0; i < 200; i++){
			e.addObject(EnvironmentObject.makeCube(Math.random()*5, 
					(Math.random() - 0.5)*factor, (Math.random() - 0.5)*factor, (Math.random())*35));
		}
	}


	/**
	 * Creates an environment for 3D graphing
	 */
	private void buildGraphSurface(){
		e = new Environment();

		// Build the floor
		int factor = 5;
		for(int i = -30; i <= 30; i++){
			for(int j = -30; j <= 30; j++){
				e.addTriangle(new Triangle3D(
						new Point3D(i*factor, j*factor, -10),
						new Point3D((i+1)*factor, j*factor, -10),
						new Point3D((i+1)*factor, (j+1)*factor, -10)));
				e.addTriangle(new Triangle3D(
						new Point3D(i*factor, j*factor, -10),
						new Point3D((i+1)*factor, (j+1)*factor, -10),
						new Point3D(i*factor, (j+1)*factor, -10)));
			}
		}
		
		// build the function terrain
		factor = 150;
		double step = 0.1;
		for(double x = -1.0; x <= 1.0; x += step){
			for(double y = -1.0; y <= 1.0; y += step){
				e.addTriangle(new Triangle3D(
						new Point3D(x*factor, y*factor, f(x, y)),
						new Point3D((x+step)*factor, y*factor, f(x+step, y)),
						new Point3D((x+step)*factor, (y+step)*factor, f(x+step, y+step))));
				e.addTriangle(new Triangle3D(
						new Point3D(x*factor, y*factor, f(x, y)),
						new Point3D((x+step)*factor, (y+step)*factor, f(x+step, y+step)),
						new Point3D(x*factor, (y+step)*factor, f(x, y+step))));

			}
		}
	}
	
	private double f(double x, double y){
		return 20*Math.sin(7*x+3*y);
	}
}
