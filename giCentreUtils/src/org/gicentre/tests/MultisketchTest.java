package org.gicentre.tests;

import java.awt.GridLayout;

import org.gicentre.utils.multisketch.EmbeddedSketch;
import org.gicentre.utils.multisketch.SketchPanel;

import processing.core.PApplet;


// ****************************************************************************************
/** Tests the embedding of multiple sketches using the multisketch package.
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
public class MultisketchTest extends PApplet
{
	// ------------------------------ Starter method ------------------------------- 

	/** Creates a simple application to test the multiple sketch embedding.
	 *  @param args Command line arguments (ignored). 
	 */
	public static void main(String[] args)
	{   
		PApplet.main(new String[] {"org.gicentre.tests.MultisketchTest"});
	}

	// ------------------------------ Initialisation -------------------------------

	/** Initialises the test by embedding two sketches in a single window using java awt panels.
	 */
	public void setup()
	{
		size(600, 300);
		setLayout(new GridLayout(0,2)); 
		noLoop(); 

		ASketch       sketch1 = new ASketch(); 
		AnotherSketch sketch2 = new AnotherSketch(); 

		SketchPanel sp1 = new SketchPanel(this,sketch1); 
		add(sp1); 
		sketch1.setIsActive(true); 

		SketchPanel sp2 = new SketchPanel(this,sketch2); 
		add(sp2); 
		sketch2.setIsActive(true); 

	}

	// -------------- Nested classes representing embedded sketches. -------------- 

	/** Simple embedded sketch that can be placed in its own window. 
	 *  @version 1.3, 5th November, 2013.
	 */ 
	class ASketch extends EmbeddedSketch 
	{ 
		float rotationAngle;  

		/** Initialises the sketch ready to display some animated text. 
		 */
		public void setup()
		{ 
			size(300, 300); 
			textFont(createFont("Serif", 32), 32); 
			textAlign(CENTER, CENTER); 
			fill(120, 20, 20); 
			rotationAngle = 0;
		} 

		/** Displays some text and animates a change in size.
		 */
		public void draw() 
		{ 
			super.draw();   // Should be the first line of draw(). 

			background(255, 200, 200); 

			pushMatrix(); 
			translate(width/2, height/2); 
			rotate(rotationAngle); 
			text("Hello world", 0, 0); 
			popMatrix(); 

			rotationAngle += 0.01;
		}
	}

	// Simple embedded sketch that can be placed in its own window. 
	// Version 1.3, 5th November, 2013. 

	class AnotherSketch extends EmbeddedSketch 
	{ 
		float textScale;  

		/** Initialises the sketch ready to display some animated text. 
		 */
		public void setup()
		{ 
			size(300, 300);  
			textFont(createFont("SansSerif", 24), 24); 
			textAlign(CENTER, CENTER); 
			fill(20, 120, 20); 
			textScale = 0;
		} 

		/** Displays some text and animates a change in size.
		 */
		public void draw() 
		{ 
			super.draw();   // Should be the first line of draw(). 
			background(200, 255, 200); 

			pushMatrix(); 
			translate(width/2, height/2); 
			scale(0.1f+sin(textScale), 1); 
			text("Hello again", 0, 0); 
			popMatrix(); 

			textScale += 0.02;
		}
	}
}