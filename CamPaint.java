import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing 
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;				// color of regions of interest (set by mouse press)
	private Color paintColor = Color.green;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
	finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		// TODO: YOUR CODE HERE
		if(displayMode == 'w')											//draws image on webcam
		{
			g.drawImage(image, 0, 0, null);
		}
		if(displayMode == 'r')											//gets changed recolored image and prints on webcam
		{
			g.drawImage(finder.getRecoloredImage(), 0, 0, null);
		}
		if(displayMode == 'p')											//updates window to painting image
		{
			g.drawImage(painting, 0, 0, null);
		}
		repaint();
	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage() {
		// TODO: YOUR CODE HERE
		if(targetColor != null)	
		{
			finder.setImage(image);				//constantly updates webcame with current image
			finder.findRegions(targetColor);
			if(finder.largestRegion() != null)
			{
				ArrayList<Point> painter = finder.largestRegion();			//sets a list of points to the largest colred region
				finder.recolorImage();										//sets recolored image
				
				
				for(int i = 0; i<painter.size();i++) 
				{
					painting.setRGB(painter.get(i).x, painter.get(i).y, paintColor.getRGB());	//each pixel in painting is updates as the brush moves
				}
			}
			
		}
	}

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		// TODO: YOUR CODE HERE
		if(image != null)
		{
			targetColor = new Color(image.getRGB(x, y));
			System.out.println("the color is " + targetColor);
		}
		repaint();
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}