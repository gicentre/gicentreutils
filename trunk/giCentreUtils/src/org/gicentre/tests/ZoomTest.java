package org.gicentre.tests;

import org.gicentre.utils.move.Ease;
import org.gicentre.utils.move.ZoomPan;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;


//  ****************************************************************************************
/** Tests zooming and panning in a simple Processing sketch. 
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.1, 13th August, 2010. 
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
public class ZoomTest extends PApplet
{
    // ------------------------------ Starter method ------------------------------- 

    /** Creates a simple application to test the zooming and panning in Processing.
     *  Can be controlled with the left and right mouse buttons and 'R' to reset 
     *  quickly, and 'B' to do an animated reset with a small bounce.
     *  @param args Command line arguments (ignored). 
     */
    public static void main(String[] args)
    {   
        PApplet.main(new String[] {"org.gicentre.tests.ZoomTest"});
    }

    // ----------------------------- Object variables ------------------------------

    private ZoomPan zoomer;
    private float morphT;           // Controls morphing of the zoom display (0-1).
    private PVector panOffset;      // Pan offset used for morphed panning.
    private double zoomScale;       // Zoom level used for morphed zooming.
    
    // ---------------------------- Processing methods -----------------------------

    /** Sets up the sketch.
     */
    public void setup()
    {   
        size(640,400);
        smooth(); 
        morphT = 1;                 // 1 indicates no morphing. 
        zoomer = new ZoomPan(this);
    }

    /** Draws a simple object that can be zoomed and panned.
     */
    public void draw()
    {   
        background(255);
        
        pushMatrix();       // Preserve the non-zoomed display.
        
        if (morphT < 1)
        {
            // Move the zoom pan with an eased transition.
            zoomer.setPanOffset(lerp(panOffset.x,0,Ease.bounceOut(morphT)), 
                                lerp(panOffset.y,0,Ease.bounceOut(morphT)));
            zoomer.setZoomScale(lerp((float)zoomScale,1,Ease.bounceOut(morphT)));
            morphT += 0.02f;
        }
        
        zoomer.transform();     // Do the zooming and panning.
        
        // Display a simple background
        stroke(120);
        strokeWeight(0.5f);
        for (float x=0; x<=width; x+= width/20f)
        {
            line(x,0,x,height);
        }
        
        for (float y=0; y<=height; y+= height/20f)
        {
            line(0,y,width,y);
        }
        
        // Display a simple object.
        stroke(80);
        strokeWeight(2);
        fill(180,120,120);
        ellipse(width/2, height/2, 60,60);
        
        popMatrix();        // Retrieve the non zoomed display
        textAlign(PConstants.LEFT, PConstants.BOTTOM);
        fill(80);
        text("This text remains unaffected by zooming and panning.",10,height-3);
    }
    
    /** Responds to key presses by allowing the display to be reset.
     */
    public void keyPressed()
    {
        if ((key == 'r') || (key == 'R'))
        {
            zoomer.reset();
        }
        else if ((key == 'b') || (key == 'b'))
        {
            // Resets the zoom/pan with a little bounce.
            panOffset = zoomer.getPanOffset();
            zoomScale = (float)zoomer.getZoomScale();
            morphT = 0;
        }
                
    }
}