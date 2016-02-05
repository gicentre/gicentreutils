package org.gicentre.tests;

import java.util.Random;

import org.gicentre.utils.colour.ColourPicker;
import org.gicentre.utils.colour.PickerListener;

import processing.core.PApplet;

//****************************************************************************************
/** Tests the standard colour picker class for selecting Brewer colour palettes.
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

public class ColourPickerTest extends PApplet
{
	// ------------------------------ Starter method ------------------------------- 

	/** Creates a simple application to test the Likert chart widget.
	 *  @param args Command line arguments (ignored). 
	 */
	public static void main(String[] args)
	{   
		PApplet.main(new String[] {"org.gicentre.tests.ColourPickerTest"});
	}

	// ----------------------------- Object variables ------------------------------

	ColourPicker colourPicker;
	private ColourListener colourListener;
	int lineColour;

	// ------------------------------ Initialisation -------------------------------

	/** Sets the size and of the sketch and its maximum pixel density.
      */
    public void settings()
    {
    	 size(800,600);
    	 pixelDensity(displayDensity());
    }
    
	/** Sets up the sketch by initialising the colour picker.
	 */
	public void setup()
	{
		surface.setResizable(true);
		strokeWeight(2);
		lineColour = color(180,0,0);

		// The colour picker needs to know the sketch (this) in which to draw.
		// The last two parameters define the width and height of the border around 
		// the colour picker relative to the size of this sketch.
		colourPicker = new ColourPicker(this,50,80);

		// The optional listener can be used to make changes as soon as a new colour is selected.
		colourListener = new ColourListener();
		colourPicker.addPickerListener(colourListener);
	}

	// ------------------------------ Processing draw -----------------------------

	/** Draws a simple sketch with the option of displaying the colour picker.
	 */
	public void draw()
	{
		background(255);

		// Draw some random stuff to represent a sketch.
		stroke(lineColour);
		Random rand = new Random(5432);

		for (int i=0; i<100; i++)
		{
			line(rand.nextFloat()*width,rand.nextFloat()*height, 
					rand.nextFloat()*width,rand.nextFloat()*height);
		}

		// Add this to display the colour picker.
		colourPicker.draw();
	}

	// --------------------------- Keyboard handling -----------------------------

	/** Turns the colour picker on or off with the 'c' key.
	 */
	public void keyPressed()
	{
		if ((key == 'c') || (key == 'C'))
		{
			colourPicker.setIsActive(!colourPicker.getIsActive());
		}
	}

	// -------------------- Nested classes ----------------------

	/** This class listens out for a colour selection using the colour picker and updates
	 * the sketch's lineColour accordingly.
	 */
	private class ColourListener implements PickerListener
	{
		/** Creates a colour listener.
		 */
		public ColourListener() 
		{
			// Empty constructor.
		}

		/** Responds to a colour being chosen by the colour picker.
		 */
		public void colourChosen()
		{
			int pickedColour = colourPicker.getLastColour();
			if (pickedColour != Integer.MAX_VALUE)
			{
				lineColour = pickedColour;
			}
		}
	}
}