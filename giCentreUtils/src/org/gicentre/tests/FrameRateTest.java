package org.gicentre.tests;

import org.gicentre.utils.FrameTimer;

import processing.core.PApplet;
import processing.core.PConstants;

//*****************************************************************************************
/** Simple class to test frame rate reporting.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.4, 5th February 2014. 
 */ 
//  *****************************************************************************************

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
public class FrameRateTest extends PApplet
{
	// ------------------------------ Starter method ------------------------------- 

	/** Creates a simple application to test frame rate reporting.
	 *  @param args Command line arguments (ignored). 
	 */
	public static void main(String[] args)
	{   
		PApplet.main(new String[] {"org.gicentre.tests.FrameRateTest"});
	}

	// ----------------------------- Object variables ------------------------------	

	private FrameTimer timer; 	// Needs to be object-wide since used by both setup() and draw().
	private int numEllipses;

	// ------------------------------ Initialisation -------------------------------

	/** Sets the size and of the sketch and its maximum pixel density.
     */
	public void settings()
	{
		size(500,200);
		pixelDensity(displayDensity());
	}
	
	/** Sets up the sketch by initialising the colour picker.
	 */
	public void setup()
	{
		timer = new FrameTimer(30);  	// Initialise the timer to report once every 30 frames.
		//timer = new FrameTimer(2,5);  // Initialise the timer to report once every 5 seconds after a 2 second delay.
		numEllipses = 1024;
		textSize(18);
		textAlign(PConstants.RIGHT,PConstants.TOP);
	}

	// ------------------------------ Processing draw -----------------------------
	
	/** Draws some random ellipses and reports the frame rate.
	 */
	public void draw()
	{
		background(255);
		timer.update();			// This is only needed if we are using a time-based frame counter.
		
		// Do some drawing of randomly placed and coloured ellipses.
		stroke(0,120);
		for (int i=0; i<numEllipses; i++)
		{
			fill(random(0,150),random(0,150),random(0,150),80);
			ellipse(random(10,width-10),random(30,height-10),random(8,20),random(8,20));
		}
		
		fill(0,220);
		String fps = timer.getFrameRateAsText();
		if (fps.length()>0)
		{
			text(numEllipses+" ellipses drawn at "+fps+" fps",width-5,0);
		}
	}
	
	// --------------------------- Processing interaction -------------------------
	
	/** Allows the number of ellipses to be drawn to be controlled via the left and right arrows.
	 */
	public void keyPressed()
	{
		if (key == CODED)
		{
			if ((keyCode == LEFT) && (numEllipses>1))
			{
				numEllipses /=2;
			}
			else if (keyCode == RIGHT)
			{
				numEllipses *=2;
			}		
		}
	}
}
