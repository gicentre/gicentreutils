package org.gicentre.utils.multisketch;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.gicentre.utils.FrameTimer;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

// ***********************************************************************************************
/** Class for showing a sequence of Processing sketches. Can be used to construct 'powerpoint'
 *  type presentations that contain an arbitrary set of sketches. See also the <code>Slide</code>
 *  class for showing simple text screens as part of a slide show. Progress through the slide show
 *  is controlled with the PageUp ad PageDown keys.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.3, 1st August, 2011.
 */ 
// ***********************************************************************************************

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

public class SlideShow extends Panel
{
    // ---------------------------- Object variables -----------------------------
    
    private static final long serialVersionUID = 8413004006375140745L;
    
    private CardLayout cardLayout;
    private int currentPageNumber,numPages;
    private EmbeddedSketch currentSketch;
    private HashMap<Integer,EmbeddedSketch> pages;
    private HashMap<Integer,Slide> slides;
    private HashMap<EmbeddedSketch,Integer> sketches;
    private PApplet parent;
    private SlideSketch slideSketch;
    private FrameTimer timer;
    private PFont font;                 // Font used for timer.
    private int fontSize;
    private int fontColour;
    
    private int advanceKey,retreatKey;  // Keys to control slide advancing/retreating.
    
    private Format formatter;
    private int countdown;
        
    // ------------------------------- Constructor ------------------------------- 
    
    /** Initialises the slideshow.
      * @param parent Parent sketch containing the slideshow. 
      */
    public SlideShow(PApplet parent)
    {
        super();
        this.parent = parent;
        parent.registerKeyEvent(this);
        cardLayout = new CardLayout();
        pages = new HashMap<Integer,EmbeddedSketch>(); 
        slides = new HashMap<Integer,Slide>(); 
        sketches = new HashMap<EmbeddedSketch,Integer>();
        setLayout(cardLayout);
        currentPageNumber = 0;
        numPages = 0;
        slideSketch = null;
        currentSketch = null;
        timer = null;
        formatter = new SimpleDateFormat("mm:ss");
        countdown = 0;
        advanceKey = KeyEvent.VK_PAGE_DOWN;
        retreatKey = KeyEvent.VK_PAGE_UP;
    }
    
    // --------------------------------- Methods ---------------------------------
    
    /** Adds the given sketch to the slide show.
      * @param sketch Sketch to add to the slide show. 
      */
    public void addSketch(EmbeddedSketch sketch)
    {
        addSketch(sketch,0);
    }
    
    /** Adds the given sketch to the slide show with the given offset from the top of the slide.
      * @param sketch Sketch to add to the slide show. 
      * @param offset Offset from top of slide in pixels.
      */
    @SuppressWarnings("boxing")
    public void addSketch(EmbeddedSketch sketch, int offset)
    {
        numPages++;
        sketch.setIsActive(false);      // Ensure we don't have any invisible active sketches.
        
        // If this is a new sketch, add it to the set of sketches.
        if (!sketches.keySet().contains(sketch))
        {
            sketches.put(sketch, numPages);
            add(new SketchPanel(parent,sketch,offset),Integer.toString(numPages));
        }
        
        // Store a reference to this sketch for this page.
        pages.put(numPages,sketch);
    }
    
    /** Adds the given slide to the slide show.
      * @param slide Slide to add to the slide show. 
      */
    @SuppressWarnings("boxing")
    public void addSlide(Slide slide)
    {
        if (slideSketch == null)
        {
            // If this is the first slide, we need to create singleton sketch to hold this and any subsequent slides.
            slideSketch = new SlideSketch(slide,parent);
            slideSketch.setIsActive(false);
            add(new SketchPanel(parent,slideSketch),"slide");
            if (timer != null)
            {
                slideSketch.setTimer(this, font);
            }
        }
                
        // Store the slide.
        numPages++;
        slides.put(numPages, slide);
        
        // Insert a null object as a place holder for a slide.
        pages.put(numPages,null);
    }
    
    
    /** Adds a timer to the slide show displayed with the given font but default colour and size. This
      * results in the time since this method was last called being displayed in the top right corner 
      * of any slides in the slide show. Will not display the timer in any embedded sketches. To display 
      * the time in a sketch, call <code>displayTime()</code> from the <code>draw()</code> method of the
      * embedded sketch.
      * @param timerFont Font used to display timer. 
      */
    public void addTimer(PFont timerFont)
    {
        addTimer(timerFont,18,Color.gray.getRGB());
    }
    
    /** Adds a timer to the slide show displayed using the given font characteristics. This results in the
      * time since this method was last called being displayed in the top right corner of any slides in the 
      * slide show. Will not display the timer in any embedded sketches. To display the time in a sketch, 
      * call <code>displayTime()</code> from the <code>draw()</code> method of the embedded sketch.
      * @param timerFont Font used to display timer. 
      * @param timerFontSize Size of font in pixels used to display timer.
      * @param timerFontColour Colour of font used to display timer.
      */
    public void addTimer(PFont timerFont, int timerFontSize, int timerFontColour)
    {
        this.font = timerFont;
        this.fontSize = timerFontSize;
        this.fontColour = timerFontColour;
        timer = new FrameTimer();
        timer.startTimer();
    }
    
    /** Adds a countdown timer displayed using the given font and default size and colour to the slide show.
      * This results in a timer that counts down from the given start time being displayed in the top right
      * corner of any slides in the slide show. When the timer reaches zero, the time will remain displayed
      * as 0:00. Will not display the timer in any embedded sketches. To display the time in a sketch, call
      * <code>displayTime()</code> from the <code>draw()</code> method of the embedded sketch.
      * @param startTime Start time in seconds (e.g. to add a 2 minute countdown, this should be 120). 
      * @param timerFont Font used to display timer. 
      */
    public void addCountdownTimer(int startTime, PFont timerFont)
    {
        addCountdownTimer(startTime,timerFont,18,Color.gray.getRGB());
    }
    
    /** Adds a countdown timer displayed using the given font characteristics to the slide show. This results
      * in a timer that counts down from the given start time being displayed in the top right corner of any 
      * slides in the slide show. When the timer reaches zero, the time will remain displayed as 0:00. Will 
      * not display the timer in any embedded sketches. To display the time in a sketch, call 
      * <code>displayTime()</code> from the <code>draw()</code> method of the embedded sketch.
      * @param startTime Start time in seconds (e.g. to add a 2 minute countdown, this should be 120). 
      * @param timerFont Font used to display timer. 
      * @param timerFontSize Size of font in pixels used to display timer.
      * @param timerFontColour Colour of font used to display timer.
      */
    public void addCountdownTimer(int startTime, PFont timerFont, int timerFontSize, int timerFontColour)
    {
        this.countdown = Math.max(0, startTime);
        this.font = timerFont;
        this.fontSize = timerFontSize;
        this.fontColour = timerFontColour;

        timer = new FrameTimer();
        timer.startTimer();
    }
    
    /** Displays the time since <code>addTimer()</code> was last called in the top-right corner of the
      * given sketch. Nothing will be displayed if <code>addTimer()</code> has not been called.
      * @param sketch Sketch in which to display the time.
      * @param timerFont Font in which to display time.
      */
    public void displayTime(EmbeddedSketch sketch, PFont timerFont)
    {
        String formattedTime;
        if (countdown > 0)
        {
            formattedTime = formatter.format(new Integer(Math.max(0,(countdown - (int)timer.getElapsedTime())*1000)));
        }
        else
        {
            formattedTime = formatter.format(new Integer((int)timer.getElapsedTime()*1000));
        }
        sketch.pushStyle();
        sketch.textAlign(PConstants.RIGHT, PConstants.TOP);
        sketch.fill(fontColour);
        sketch.textFont(timerFont,fontSize);
        sketch.text(formattedTime,sketch.width-4,4);
        sketch.popStyle();
    }
    
    /** Sets the currently displayed slide to the given one.
      * @param slideNumber Number of slide to display. 
      */
    @SuppressWarnings("boxing")
    public void setCurrentSlide(int slideNumber)
    {
        // Check slide request is within range and is a new slide.
        if ((slideNumber < 1) || (slideNumber > numPages))
        {
            return;
        }
                
        // Ensure that any old slide has been made inactive before removing it.
        if (currentSketch != null)
        {
            currentSketch.setIsActive(false);
        }

        // Show requested slide. 
        currentPageNumber = slideNumber;
        currentSketch = pages.get(currentPageNumber);
        
        // If this is a slide, set it to display the slideSketch set to the relevant slide.
        if (currentSketch == null)
        {
            slideSketch.setSlide(slides.get(currentPageNumber));
            currentSketch = slideSketch;
            currentSketch.setIsActive(true);
            cardLayout.show(this,"slide");
            currentSketch.requestFocusInWindow();
            slideSketch.fadeIn();
        }
        else
        {
            // Display and activate the current sketch.
            currentSketch.setIsActive(true);
            cardLayout.show(this,sketches.get(currentSketch).toString());
            currentSketch.requestFocusInWindow();
        }
    }
    
    /** Starts the slide show from the first slide.
      */
    public void startShow()
    {
        startShow(1);
    }
    
    /** Starts the slide show from the given slide. If this is greater than the number of slides
      * currently stored, the slide show will start from the last slide in the collection.
      * @param slideNum The number of the slide from which to start. 
      */
    public void startShow(int slideNum)
    {
        setCurrentSlide(slideNum);
    }
    
    /** Sets the key that advances the slideshow by one slide. The values should be one of the
      * <code>KeyEvent</code> codes. The default value is <code>KeyEvent.VK_PAGE_DOWN</code>.
      * @param advanceKey Key code corresponding to the key that will advance each slide. 
      */
    public void setAdvanceKey (int advanceKey)
    {
        this.advanceKey = advanceKey;
    }
    
    /** Sets the key that sends the slideshow back by one slide. The values should be one of the
      * <code>KeyEvent</code> codes. The default value is <code>KeyEvent.VK_PAGE_UP</code>.
      * @param retreatKey Key code corresponding to the key that will move back to the previous slide. 
      */
    public void setRetreatKey (int retreatKey)
    {
        this.retreatKey = retreatKey;
    }
    
    /** Controls sequence though slide show with the pageUp and pageDown keys
      * @param e Key press event.
      */
    public void keyEvent(KeyEvent e)
    {
        if (e.getID() == KeyEvent.KEY_PRESSED)
        {
            if (e.getKeyCode() == advanceKey)
            {
                setCurrentSlide(currentPageNumber+1);
            }
            else if (e.getKeyCode() == retreatKey)
            {
                setCurrentSlide(currentPageNumber-1);
            }
        }
    }
}