import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
															// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}
	
	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		// TODO: YOUR CODE HERE 
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		regions = new ArrayList<ArrayList<Point>>();
		ArrayList <Point> toVisit = new ArrayList <Point>();
		//loop over each pixel
		for(int y = 0; y< image.getHeight();y++)
		{
			for(int x = 0; x< image.getWidth();x++)
			{
				//check to see if a pixel is unvisited and is the right color
				if(visited.getRGB(x,y) == 0 && colorMatch(new Color(image.getRGB(x, y)), targetColor))
				{
					ArrayList<Point> region = new ArrayList<Point>();
					Point p = new Point(x,y);
					toVisit.add(p);
					//keep executing if there is something to visit
					while(toVisit.size() != 0)
					{
						Point q = toVisit.get(toVisit.size() - 1);
						region.add(q);
						visited.setRGB(q.x,q.y, 1);											
						toVisit.remove(toVisit.size()-1);				//need to remove to prevent infinite loop
						//loop over all neighbors
						for(int my = q.y-1; my<= q.y+1; my++)
							for(int mx = q.x-1; mx<= q.x+1;mx++) 
							{
								//check to make sure pixel is in the window
								if(mx>= 0 && mx<= image.getWidth()-1 && my<=image.getHeight()-1 && my >=0)
								{
									//pixel must be unvisited and the right color to be part of region
									if(visited.getRGB(mx, my) == 0 && colorMatch(new Color(image.getRGB(mx, my)), targetColor))
										{
											toVisit.add(new Point(mx, my));
										}
								}
									
							}
					}
					//add to regions!
					if(region.size() >= minRegion)
					{
						regions.add(region);

					}
					
				}
			}
		}
		
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		// TODO: YOUR CODE HERE
		//difference between each color must be less than or equal to the maxColorDiff
		return (Math.abs(c1.getRed()-c2.getRed())<=maxColorDiff && Math.abs(c1.getBlue()-c2.getBlue())<=maxColorDiff && Math.abs(c1.getGreen()-c2.getGreen())<=maxColorDiff);
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		// TODO: YOUR CODE HERE
		ArrayList<Point> largest = null; //set to null in case nothing is in regions
		if(regions.size() != 0)
		{
			largest = regions.get(0);
			if(regions.size()>1)
			{
				for(int j = 1; j< regions.size(); j++)
					if(largest.size() < regions.get(j).size())			//compares sizes between index 0 and each other index
					{
						largest = regions.get(j);
					}
			}
		}
		return largest;
		
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		// TODO: YOUR CODE HERE
		//sets each region to a randomly assigned color then matches all pixels in that region to be that color
		for(ArrayList<Point> region: regions) {
			Color c = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
			for(Point p: region)
				recoloredImage.setRGB(p.x, p.y, c.getRGB());
		}
			
		
		
	}
}