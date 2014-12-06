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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Arena extends JPanel implements KeyListener {
	static Arena arena;
	Environment e;
	Timer timer;
	HashSet<Character>     keysDown;	// Holds the keys that are currently down.
	RenderingHints rh;					// for drawing the graphics

	public static void main(String[] args) {
		// Set up the arena and an environment
		arena = new Arena();
		Environment e = new Environment();
		arena.e = e;
		arena.buildEnvironment();
		arena.keysDown = new HashSet<Character>();
		arena.setPreferredSize(new Dimension(e.WIDTH, e.HEIGHT));
		arena.rh = new RenderingHints(null);
		arena.rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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
	 * It inspects the HashSet of keys that are currenlty pressed, and for each
	 * that is currently pressed, performs some appropriate action.
	 */
	ActionListener runLoop = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(keysDown.contains('d'))
				e.rotateRight();
			if(keysDown.contains('a'))
				e.rotateLeft();
			if(keysDown.contains('s'))
				e.moveBackward();
			if(keysDown.contains('w'))
				e.moveForward();
			if(keysDown.contains('j'))
				e.moveDown();
			if(keysDown.contains('l'))
				e.moveUp();
			if(keysDown.contains('['))
				e.nearFarther();
			if(keysDown.contains(']'))
				e.nearCloser();
			moveEnvironmentObjects();
			repaint();
		}
	};

	private void moveEnvironmentObjects(){
		double factor = 200;
		int k = 3;
		for(int i = 0; i < e.objects.size(); i++){
			EnvironmentObject eo = e.objects.get(i);
			double cx = eo.center.x;
			double cy = eo.center.y;
			double cz = eo.center.z;
			eo.center.x -= cy / factor;
			eo.center.y += cx / factor;
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
						new Point3D((i+1)*factor, (j+1)*factor, -10)));
				e.addTriangle(new Triangle3D(
						new Point3D(i*factor, j*factor, -10),
						new Point3D((i+1)*factor, (j+1)*factor, -10),
						new Point3D(i*factor, (j+1)*factor, -10)));
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
