package org.gicentre.tests;

import java.util.ArrayList;

import org.gicentre.utils.stat.StandardEllipse;

import processing.core.PApplet;
import processing.core.PVector;

// ****************************************************************************************
/** Tests the standard ellipse in a simple Processing sketch. Draws some random points then
 *  their weighted and unweighted standard ellipse.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.1, 12th January, 2011. 
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

@SuppressWarnings("serial")
public class StandardEllipseTest extends PApplet
{
	// ------------------------------ Starter method ------------------------------- 

	/** Creates a simple application to test the Likert chart widget.
	 *  @param args Command line arguments (ignored). 
	 */
	public static void main(String[] args)
	{   
		PApplet.main(new String[] {"org.gicentre.tests.StandardEllipseTest"});
	}

	// ----------------------------- Object variables ------------------------------

	private ArrayList<PVector> points;
	private static final int NUM_POINTS = 150;
	private static final int HIGH_WEIGHT = 10;

	// ------------------------------ Initialisation -------------------------------

	/** Creates a set of points from which the standard ellipses will be calculated.
	 */
	public void setup()
	{
		size(700,300);
		smooth();

		points = new ArrayList<PVector>();

		for (int i=0; i<NUM_POINTS; i++)
		{  
			if (random(1) < 0.5)
			{
				// Add a highly weighted point somewhere above-left of centre.
				points.add(new PVector(width/6 + random(width/6),
									   height/3+random(-height/10,height/10),
									   HIGH_WEIGHT));
			}
			else
			{
				// Add a unit weighted point anywhere in the central belt of the region.
				points.add(new PVector(width/2 + random(-width/3,width/3),
									   height/2+random(-height/6,height/6)));
			}
		}
	}

	// ------------------ Processing draw --------------------

	/** Draws the points, randomly perturbs them and draws their standard ellipse.
	 */
	public void draw()
	{
		background(255);
		strokeWeight(3);

		// Plot the original points and make them move about a bit.
		for (PVector p : points)
		{
			p.x += random(-2,2);
			p.y += random(-2,2);

			if (p.z == 0)
			{
				stroke(128,80,80);
			}
			else
			{
				stroke(183,80,80);
			}    
			point(p.x,p.y);
		}

		strokeWeight(1);

		// Calculate the standard ellipse of the points.
		StandardEllipse standardEllipse = new StandardEllipse(points);

		// Draw the unweighted version of the ellipse.
		stroke(128,128,183);
		fill(128,128,183,50);
		standardEllipse.draw(this);

		// Draw the ellipse axes.
		stroke(80,80,80,100);
		standardEllipse.drawAxes(this);

		// Draw the weighted version of the ellipse.
		standardEllipse.setIsWeighted(true);
		stroke(193,128,128);
		fill(183,128,128,50);
		standardEllipse.draw(this);

		// Draw the ellipse axes.
		stroke(80,80,80,100);
		standardEllipse.drawAxes(this);
	}
}