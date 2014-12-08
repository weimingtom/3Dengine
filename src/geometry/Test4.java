package geometry;

import java.awt.*;
import java.awt.image.*;

import javax.swing.JFrame;

public class Test4 extends JFrame
{

	BufferedImage bi;
	BufferedImage biMask;
	public Test4(){
		Color color = Color.cyan;
		IndexColorModel colorModel = new IndexColorModel(1, 2,
				new byte[]{0, (byte) color.getRed()},
				new byte[]{0, (byte) color.getGreen()},
				new byte[]{0, (byte) color.getBlue()},0);

		setBackground(Color.black);

		Rectangle b = new Rectangle(640, 480);

		bi = new BufferedImage(b.width, b.height
				, BufferedImage.TYPE_BYTE_INDEXED, colorModel);

		biMask = new BufferedImage(b.width, b.height
				, BufferedImage.TYPE_BYTE_INDEXED, colorModel);

		Graphics2D big = (Graphics2D)bi.getGraphics();
		Graphics2D biMaskg = (Graphics2D)biMask.getGraphics();
		WritableRaster wr = bi.getRaster();

		TexturePaint thing = new TexturePaint(bi, new Rectangle(b.width, b.height));
		int[] v = {1};
		for(int i = 0; i < b.width; i+= 4){
			for(int j = 0; j < b.height; j+= 4){
				wr.setPixel(i, j, v);
			}
		}

		biMaskg.fillRect(50, 50, 200, 100);
		//biMaskg.fillRect(50, 250, 100, 200);
		big.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN));
		big.drawImage(biMask, 0, 0, null);

		//biMaskg.dispose();
		big.dispose();
	}
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.red);
		g2d.fillRect(10, 10, 100, 100);
		g2d.drawImage(bi, 0, 0, null);
	}

	public static void main(String args[])
	{
		Test4 t = new Test4();
		t.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		t.setBounds(new Rectangle(0, 0, 200, 200));
		t.setVisible(true);
	}

}