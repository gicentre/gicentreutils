package org.gicentre.tests;

import org.gicentre.utils.geom.Ellipse;

import processing.core.PApplet;
import processing.core.PVector;


// ****************************************************************************************
/** Tests the drawing ellipse arcs as Bezier curves. Can use arrow keys to move the start
 *  and end of the ellipse segment.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.3, 6th April, 2013 
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
public class EllipseTest extends PApplet
{
	// ------------------------------ Starter method ------------------------------- 

	/** Creates a simple application to test the Likert chart widget.
	 *  @param args Command line arguments (ignored). 
	 */
	public static void main(String[] args)
	{   
		PApplet.main(new String[] {"org.gicentre.tests.EllipseTest"});
	}

	// ----------------------------- Object variables ------------------------------

	private float startAngle,endAngle;

	// ------------------------------ Initialisation -------------------------------

	/** Initialises the test.
	 */
	public void setup()
	{
		size(600, 600);
		startAngle = 0;
		endAngle = radians(90);
	}

	// ------------------ Processing draw --------------------

	/** Draws an ellipse using bezier curves.
	 */
	public void draw()
	{
		background(255);
		noFill();
		stroke(0);
		strokeWeight(0.4f);

		float cx = width/2;
		float cy = height/2;
		float eWidth = 200;
		float eHeight = 20;
		float theta = radians(123);

		// Angular scale
		float diam = min(width-20, height-20);
		ellipse(cx, cy, diam, diam);
		for (float ang=0; ang<TWO_PI-0.01; ang+=radians(15))
		{
			line(cx, cy, cx+0.5f*diam*cos(ang), cy+0.5f*diam*sin(ang));
		}

		// Processing ellipse
		pushMatrix();
		translate(cx,cy);
		rotate(theta);
		ellipse(0, 0, eWidth, eHeight);
		popMatrix();

		// Parametric equation of ellipse
		Ellipse e = new Ellipse(cx, cy, eWidth, eHeight, theta);
		stroke(0, 80);
		for (float lamda=0; lamda<TWO_PI-0.01; lamda += radians(15))
		{
			PVector p = e.getPosition(lamda);
			ellipse(p.x, p.y, 4, 4);
		}

		// Full bezier arc 
		println("Start: "+round(degrees(startAngle))+" End: "+round(degrees(endAngle))+" C angle: "+round(PApplet.degrees((float)(Ellipse.clockwiseAngleBetween(startAngle,endAngle)))));
		PVector[] vertices = e.getBezierVertices(startAngle,endAngle);
		fill(0,150,0,100);
		strokeWeight(2);
		stroke(0, 100,0, 200);
		beginShape();
		vertex(vertices[0].x,vertices[0].y);
		for (int i=1; i<vertices.length; i+=3)
		{
			bezierVertex(vertices[i].x,vertices[i].y, 
					vertices[i+1].x,vertices[i+1].y,
					vertices[i+2].x,vertices[i+2].y);      
		}
		endShape();


		// Arc curve to be represented by bezier
		//strokeWeight(4);
		//stroke(100, 0, 0, 100);
		//arc(cx, cy, eWidth, eHeight, startAngle, endAngle);

		// Bezier curve of ellipse arcs
		strokeWeight(3);
		stroke(0, 0, 100, 200);
		noFill();
		PVector[] bez = e.getBezier(startAngle, endAngle);
		bezier(bez[0].x, bez[0].y, bez[1].x, bez[1].y, bez[2].x, bez[2].y, bez[3].x, bez[3].y);

		noLoop();
	}


	public void keyPressed()
	{
		if (key == CODED)
		{
			if (keyCode == LEFT)
			{
				startAngle -= radians(15);
				loop();
			}
			else if (keyCode == RIGHT)
			{
				startAngle += radians(15);
				loop();
			}
			else if (keyCode == DOWN)
			{
				endAngle -= radians(15);
				loop();
			}
			else if (keyCode == UP)
			{
				endAngle += radians(15);
				loop();
			}
		}
	}
}