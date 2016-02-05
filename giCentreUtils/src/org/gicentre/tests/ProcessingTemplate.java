package org.gicentre.tests;

import processing.core.PApplet;

//  ****************************************************************************************
/** TODO Describe class here. 
 *  TODO: @author Your name, giCentre, City University London.
 *  TODO: @version 3.4, Replace with version and date 
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

public class ProcessingTemplate extends PApplet
{

    // ------------------------------ Starter method ------------------------------- 

    /** Allows the processing sketch to be run as an application.
     *  @param args Command line arguments (ignored). 
     */
    public static void main(String[] args)
    {   
        // TODO: Change class name to reflect the class containing the sketch.
        PApplet.main(new String[] {"org.gicentre.tests.ProcessingTemplate"});
    }
    
    // ----------------------------- Object variables ------------------------------

    // ------------------------------- Constructors --------------------------------

    
    // ---------------------------- Processing methods -----------------------------

    /** Sets the size and of the sketch and its maximum pixel density.
     */
	public void settings()
	{
		size(800,600);
		// For tests, setting maximum pixel density useful. For real sketches this may depend on speed and other consdierations.
		pixelDensity(displayDensity());
	}
	
    /** Initialise the sketch.
     */
    public void setup()
    {   
       // Add initialisation here.
    }

    /** Draw the sketch.
     */
    public void draw()
    {   
        background(255);
        fill(220,160,160);
        
        ellipse(width/2,height/2,width/4,height/4);
    }
    
    // ---------------------------------- Methods ----------------------------------

    // ------------------------------ Private methods ------------------------------
}