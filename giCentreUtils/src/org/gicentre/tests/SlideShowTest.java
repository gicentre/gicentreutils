package org.gicentre.tests;

import java.awt.GridLayout;

import org.gicentre.utils.multisketch.Slide;
import org.gicentre.utils.multisketch.SlideShow;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

//  ****************************************************************************************
/** Tests the use of embedded sketches, text and images in a slide show.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.1, 7th September, 2010. 
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
public class SlideShowTest extends PApplet
{
    // ------------------------------ Starter method ------------------------------- 

    /** Creates a simple application to test the slide show classes.
     *  @param args Command line arguments (ignored). 
     */
    public static void main(String[] args)
    {   
        PApplet.main(new String[] {"org.gicentre.tests.SlideShowTest"});
    }

    // ----------------------------- Object variables ------------------------------
    
    private SlideShow slideShow; // Needs to be object wide if sketches are to show the timer.
    PFont font;                  // Needs to be object wide if sketch and slide timers use same font.

    
    // ---------------------------- Processing methods -----------------------------

    /** Sets up the slideshow and embedded sketches.
     */
    public void setup()
    {   
        // For a windowed application comment the line above and use line below instead.
        size(1024,768, PConstants.JAVA2D);
        
        // This slide show sketch should not loop and does not use the draw() method.
        noLoop();
        
        // For efficiency it is best to create the fonts for the slide show once at the start.
        font = createFont("Helvetica",10);
        
        // The slide show is added to the centre of the sketch area.
        setLayout(new GridLayout(1,1));
              
        // Create the slide show object and add an optional timer.
        slideShow = new SlideShow(this);
        slideShow.addCountdownTimer(120,font);
        
        
        // Each slide has a default font that must be provided in the constructor.
        // Text is added using addLine(), images added using addImage().
        Slide slide = new Slide(font);      
        slide.addLine("This is slide one");
        slide.addLine("with some text in a different size",font,18);
        slideShow.addSlide(slide);
        
        
        
        // The slide show must be added to this master sketch in order for it to be visible.
        add(slideShow);
        
        // This starts the slide show off.
        slideShow.startShow();
    }
    
    
    
}