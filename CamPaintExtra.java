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
public class CamPaintExtra extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinderExtra finder;			// handles the finding
	private Color targetColor;				// color of regions of interest (set by mouse press)
	private Color paintColor = Color.green;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece
	private int brushes = 1;				//number of brushes
	private ArrayList<Color> colorMatrix;	//list of all colors that need to be found (list of targetColor colors functionally)
	private ArrayList<ArrayList<Point>> colorPoints;	//stores all list of points for the largest region of each color

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaintExtra() {
		colorMatrix = new ArrayList<Color>();
		colorPoints = new ArrayList<ArrayList<Point>>();
		finder = new RegionFinderExtra();
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
		if(displayMode == 'w' || displayMode == 'l')	//key 'l' added for strobe lights!
		{
			g.drawImage(image, 0, 0, null);
		}
		if(displayMode == 'r')
		{
			g.drawImage(finder.getRecoloredImage(), 0, 0, null);
		}
		if(displayMode == 'p')
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
		//need to make sure there is a color
		if(colorMatrix.size() != 0)		
		{
			//update image
			finder.setImage(image);
			//only run code if all points have been stored
			if(brushes == 0 )
			{
				//make a new list of regions in regions that will be added to colorRegions
				for(int i = 0; i< colorMatrix.size();i++)
					finder.findRegions(colorMatrix.get(i));
				
				if(finder.largestRegion() != null)
				{
					finder.setSize(0); //if statement set size to 1 - set it back to 0
					for(int z =0;z < colorMatrix.size();z++)	//add each largest region to color points
					{
						colorPoints.add(z, finder.largestRegion());
					}
					finder.setSize(0); //needs to be set back to zero if user changes brushes
					
					finder.recolorImage();
					//set the painting!
					for(int q = 0; q<colorPoints.size();q++)
					{
						for(int j = 0; j<colorPoints.get(q).size();j++)
						{
							painting.setRGB(colorPoints.get(q).get(j).x, colorPoints.get(q).get(j).y, paintColor.getRGB());
						}
					}
				}
				finder.clearRegions(); //need to clear the regions if want to make a new brush (new brush made with '+' key press)
			}
			
		}
		if(targetColor != null)
		{
			//functionality added for strobe lights
			if(displayMode == 'l')
			{
				for(int y = 0; y< image.getHeight()-1;y++)
					for(int x = 0; x< image.getWidth()-1; x++)
						image.setRGB(x, y, targetColor.getRGB());					
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
			if(displayMode == 'l')
			{
				targetColor = new Color(image.getRGB(x, y));
			}
			else
			{
				colorMatrix.add(new Color(image.getRGB(x, y)));
				brushes--;
				System.out.println("the brush count is " + brushes);
			}
		}
		repaint();
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if(k == '+')
		{
			brushes++;
			System.out.println("the brush count is " + brushes);
		}
		else if (k == 'p' || k == 'r' || k == 'w' || k =='l') { // display: painting, recolored image, webcam, or brush count
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
				new CamPaintExtra();
			}
		});
	}
}