package org.gicentre.tests;

import java.util.ArrayList;

import org.gicentre.utils.stat.StandardEllipse;

import processing.core.PApplet;
import processing.core.PVector;

// ****************************************************************************************
/** Tests the standard ellipse that summarises lat/long values in a simple Processing sketch.
 *  Sets some static points at extreme E-W margins and one further point controlled by the
 *  mouse. This tests the ability to calculate standard ellipse across the 'join' in a
 *  global map projection.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.4, 5th February, 2016.
 */ 
// *****************************************************************************************

/* This file is part of giCentre utilities library. gicentre.utils is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * gicentre.utils is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this
 * source code (see COPYING.LESSER included with this source code). If not, see 
 * http://www.gnu.org/licenses/.
 */

public class StandardEllipseLatLongTest extends PApplet
{
	// ------------------------------ Starter method ------------------------------- 

	/** Creates a simple application to test the Likert chart widget.
	 *  @param args Command line arguments (ignored). 
	 */
	public static void main(String[] args)
	{   
		PApplet.main(new String[] {"org.gicentre.tests.StandardEllipseLatLongTest"});
	}

	// ----------------------------- Object variables ------------------------------

	private ArrayList<PVector> points;
	private PVector mousePoint;

	// ------------------------------ Initialisation -------------------------------

	/** Sets the size and of the sketch and its maximum pixel density.
     */
	public void settings()
	{
		size(720,360);
		pixelDensity(displayDensity());
	}
	
	/** Creates a set of points from which the standard ellipses will be calculated.
	 */
	public void setup()
	{
		points = new ArrayList<PVector>();	
		points.add(new PVector(173,45,1));
		points.add(new PVector(175,55,1));
		points.add(new PVector(180,55,1));
		points.add(new PVector(-175,55,1));
		points.add(new PVector(-173,45,1));
		
		mousePoint = new PVector(0,0,1);
		points.add(mousePoint);
	}

	// ------------------ Processing draw --------------------

	/** Draws the points and their standard ellipse.
	 */
	public void draw()
	{
		background(255);
		strokeWeight(3);
		stroke(0);
		
		// Transform to show lat long range in window
		scale(width/360,-height/180);
		translate(180,-90);

		// Plot the original points 
		for (PVector p : points)
		{
			point(p.x,p.y);
		}

		strokeWeight(1);

		// Calculate the standard ellipse of the points.
		StandardEllipse standardEllipse = new StandardEllipse(points,true);
		standardEllipse.setIsWeighted(false);

		// Draw the the ellipse.
		stroke(128,128,183);
		fill(128,128,183,50);
		standardEllipse.draw(this);

		// Draw the ellipse axes.
		stroke(80,80,80,100);
		standardEllipse.drawAxes(this);
	}
	
	/** Updates one of the points to reflect current mouse position.
	 */
	public void mouseClicked()
	{
		mousePoint.x = map(mouseX,0,width,-180,180);
		mousePoint.y = -map(mouseY,0,height,-90,90);
	}
	
	/** Updates one of the points to reflect current mouse position.
	 */
	public void mouseDragged()
	{
		mousePoint.x = map(mouseX,0,width,-180,180);
		mousePoint.y = -map(mouseY,0,height,-90,90);
	}
}