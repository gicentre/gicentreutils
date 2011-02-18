package org.gicentre.utils.multisketch;

import processing.core.PApplet;
import processing.core.PFont;

//******************************************************************************************
/** Class for representing a slide as Processing sketch. Can be used to embed a Slide object
 *  inside a sketch. 
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.1, 18th February, 2011. 
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

public class SlideSketch extends EmbeddedSketch
{
    // -------------------------- Object variables --------------------------
    
    private static final long serialVersionUID = -4261583373118384565L;
    private Slide slide;
    int fadeLevel;
    private SlideShow slideShow;        // Slide show used for displaying elapsed time.
    private PFont font;                 // Font for displaying time.

    // ---------------------------- Constructors ----------------------------

    /** Creates a new sketch from the given slide.
      * @param slide Slide to embed in the sketch. 
      * @param parent Parent sketch in which this is embedded.
      */
    public SlideSketch(Slide slide, PApplet parent)
    {
        this.slide = slide;
        setParentSketch(parent);
        fadeLevel = 0;
        slideShow = null;
    }
    
    // -------------------------- Processing Methods --------------------------
  
    /** Initialises the processing sketch.
      */
    public void setup()
    {
        size(getParentSketch().width,getParentSketch().height);
        smooth();
        //frameRate(10);
        textAlign(LEFT,TOP);
    }
  
    /** Draws the slide.
      */
    public void draw()
    {
        super.draw();
        background(255);
        
        if (slide == null)
        {
            return;
        }
        slide.draw(this);
        
        if ((slideShow != null) && (font != null))
        {
            slideShow.displayTime(this, font);
        }
        
        // Do fading if requested.
        if (fadeLevel > 0)
        {
            fill(255,fadeLevel);
            noStroke();
            rect(0,0,width,height);
            fadeLevel-=10;
        }
    }
    
    // ------------------------------- Methods --------------------------------
    
    /** Provides a simple fade-in transition. 
      */
    public void fadeIn()
    {
        fadeLevel = 255;
    }
        
    /** Sets the given slide the one to be drawn.
      * @param slide The slide to draw in this sketch. 
      */
    public void setSlide(Slide slide)
    {
        this.slide = slide;
    }
    
    /** Sets the timer to display in the corner of each slide.
      * @param slideShow Slide show that keeps track of time.
      * @param font Font used to display the time.
      */
    public void setTimer(SlideShow slideShow, PFont font)
    {
        this.slideShow = slideShow;
        this.font = font;
    }
}