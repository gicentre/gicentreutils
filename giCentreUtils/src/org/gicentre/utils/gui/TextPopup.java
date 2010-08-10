package org.gicentre.utils.gui;

import java.awt.Dimension;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
         
// *****************************************************************************************
/** Creates a popup window for displaying text over a given sketch. Can be used for creating
 *  information and help screens. See also <code>HelpScreen</code> for keyboard shortcut
 *  type help screens.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.0, 10th August, 2010. 
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

public class TextPopup
{
    // ------------------ Object variables --------------------

    private PApplet sketch;
    private boolean isActive;
    private int xBorder, yBorder;
    private int xMargin,yMargin;
    private PFont font;
    private float textSize;
    private int fgColour,bgColour;
    
    
    private ArrayList<TextLine> textLines;

    // --------------------- Constructors ----------------------

    /** Initialises the text popup with default border size and font, but does not 
     *  display it [requires <code>setIsActive(true)</code>].
     *  @param sketch Sketch that will use the popup window.
     */
    public TextPopup(PApplet sketch)
    {
        this(sketch,30,30);
    }
    
    /** Initialises the text popup with default border size and font, but does not 
     *  display it [requires <code>setIsActive(true)</code>]. The border defines the number
     *  of pixels from within the given sketch, the window is drawn. So border values of 0 
     *  would take up the entire sketch, a border value of 10 would leave a 10 pixel wide band 
     *  of the original sketch visible behind the popup.
     *  @param sketch Sketch that will use the text popup.
     *  @param xBorder Width in pixels of the border to the left and right of the popup.
     *  @param yBorder Height in pixels of the border to the top and bottom of the popup.
     */
    public TextPopup(PApplet sketch, int xBorder, int yBorder)
    {
        this(sketch,sketch.createFont("Sans serif", 12),xBorder,yBorder);
    }
    
    /** Initialises the text popup with default border size and the given font, but does not 
     *  display it [requires <code>setIsActive(true)</code>]. The border defines the number
     *  of pixels from within the given sketch, the window is drawn. So border values of 0 
     *  would take up the entire sketch, a border value of 10 would leave a 10 pixel wide band 
     *  of the original sketch visible behind the popup.
     *  @param sketch Sketch that will use the text popup.
     *  @param font Font used for display of text.
     *  @param xBorder Width in pixels of the border to the left and right of the popup.
     *  @param yBorder Height in pixels of the border to the top and bottom of the popup.
     */
    public TextPopup(PApplet sketch, PFont font, int xBorder, int yBorder)
    {
        this.sketch = sketch;
        this.font = font;
        this.xBorder = xBorder;
        this.yBorder = yBorder;
        this.fgColour = sketch.color(50);
        this.bgColour = sketch.color(255,255,250,240);
        textLines = new ArrayList<TextLine>();
        textSize = 12;
        xMargin = 4;
        yMargin = 4;
        isActive = false;
    }

    // ----------------------- Methods ------------------------

    /** Draws the text in the popup window. This method should be called at the end
     *  of the Processing sketch's own <code>draw()</code> method. If the text popup
     *  is not active, nothing will be drawn, if it is active, this popup will be
     *  drawn over the top of the sketch that called it.
     */
    public void draw()
    {
        if (!isActive)
        {
            return;
        }
        
        sketch.pushStyle();
        sketch.textAlign(PConstants.LEFT,PConstants.TOP);
        
        // Draw background.
        sketch.fill(bgColour);
        sketch.stroke(fgColour);
        sketch.strokeWeight(0.3f);
        sketch.rect(xBorder, yBorder, sketch.width-2*xBorder, sketch.height-2*yBorder);
        
        // Draw text.
        sketch.textFont(font);
        
        float yPos = yBorder+yMargin;
        sketch.fill(fgColour);
        for (TextLine line : textLines)
        {
            if (line.getTextSize() <= 0)
            {
                sketch.textSize(textSize);
            }
            else
            {
                sketch.textSize(line.getTextSize());
            }
            sketch.text(line.getText(),
                        xBorder+xMargin,yPos,
                        sketch.width-2*(xBorder+xMargin),sketch.height-2*(yBorder+yMargin));
            yPos += (sketch.textAscent()+sketch.textDescent());
        }
        
        sketch.popStyle();
    }

    /** Reports whether or not the popup is currently active. An active popup is one that is 
     *  displayed in the current sketch.
     *  @return True if the popup is active.
     */
    public boolean getIsActive()
    {
        return isActive;
    }

    /** Determines whether or not the popup should be currently active. An active popup
     *  is one that is displayed in the current sketch.
     *  @param isActive If true, the popup is made active.
     */
    public void setIsActive(boolean isActive)
    {
        this.isActive = isActive;
    }
    
    /** Reports the current text size in pixels.
     *  @return Current text size in pixels.
     */
    public float getTextSize()
    {
        return textSize;
    }
    
    /** Sets the current text size in pixels.
     *  @param textSize New text size in pixels.
     */
    public void setTextSize(float textSize)
    {
        this.textSize = textSize;
    }
    
    /** Sets the background colour of the popup.
     *  @param bgColour Background colour expressed as a Processing integer colour.
     */
    public void setBackgroundColour(int bgColour)
    {
        this.bgColour = bgColour;
    }
    
    /** Sets the foreground colour of the popup. This is the colour of the text displayed
     *  and the border around the popup.
     *  @param fgColour Foreground colour expressed as a Processing integer colour.
     */
    public void setForegroundColour(int fgColour)
    {
        this.fgColour = fgColour;
    }
    
    /** Sets the external margin between the popup and the edge of the sketch. This will cap
     *  values between 0 and the half the width or height of the sketch.
     *  @param xMargin Width in pixels of the margin between the left and right of the popup and the sketch.
     *  @param yMargin Height in pixels of the margin between the top and bottom of the popup and the sketch.
     */
    public void setExternalMargin(int xMargin, int yMargin)
    {
        if (xMargin < 0)
        {
            this.xBorder = 0;
        }
        else if (xMargin >= sketch.width/2)
        {
            this.xBorder = sketch.width/2;
        }
        else
        {
            this.xBorder = xMargin;
        }
        
        if (yMargin < 0)
        {
            this.yBorder = 0;
        }
        else if (yMargin >= sketch.height/2)
        {
            this.yBorder = sketch.height/2;
        }
        else
        {
            this.yBorder = yMargin;
        }
    }

    /** Reports the external margins between the popup and the sketch.
     *  @return Width and height in pixels of the margin between the popup and the sketch. 
     */
    public Dimension getExternalMargin()
    {
        return new Dimension(xBorder,yBorder);
    }
    
    /** Sets the internal margins between the popup and the text displayed within it.
     *  @param xMargin Width in pixels of the margin between the left and right of the popup and the text.
     *  @param yMargin Height in pixels of the margin between the top and bottom of the popup and the text.
     */
    public void setInternalMargin(int xMargin, int yMargin)
    {
        if (xMargin < 0)
        {
            this.xMargin = 0;
        }
        else if (xMargin >= (sketch.width - 2*xBorder)/2)
        {
            this.xMargin = (sketch.width - 2*xBorder)/2;
        }
        else
        {
            this.xMargin = xMargin;
        }
        
        if (yMargin < 0)
        {
            this.yMargin = 0;
        }
        else if (yMargin >= (sketch.height - 2*yBorder)/2)
        {
            this.yMargin = (sketch.height - 2*yBorder)/2;
        }
        else
        {
            this.yMargin = yMargin;
        }
    }
    
    /** Reports the internal margins between the popup and the text displayed within it.
     *  @return Width and height in pixels of the margin between the popup and the text.
     */
    public Dimension getInternalMargin()
    {
        return new Dimension(xMargin,yMargin);
    }
    
    /** Adds a given line of text to that displayed in the popup window
     *  @param textLine Line of text to add.
     */
    public void addText(String textLine)
    {
        addText(textLine,-1);
    }
    
    /** Adds a given line of text to that displayed in the popup window
     *  @param textLine Line of text to add.
     *  @param textSize Vertical size in pixels of text for this line.
     */
    public void addText(String textLine, float textSize)
    {
        String[] lines = PApplet.split(textLine,'\n');
        for (String line : lines)
        {
            textLines.add(new TextLine(line,textSize));
        }
    }
    
    /** Replaces the text to be displayed in the popup window with the given text.
     *  @param text New text to display in the popup window.
     */
    public void setText(String text)
    {
        setText(text,-1);
    }
    
    /** Replaces the text to be displayed in the popup window with the given text at the given size.
     *  @param text New text to display in the popup window.
     *  @param textSize Vertical size in pixels of text for this line.
     */
    public void setText(String text, float textSize)
    {
        this.textLines.clear();
        addText(text,textSize);
    }
    
    /** Clears the text to be displayed in the popup window.
     */
    public void clearText()
    {
        this.textLines.clear();
    }
    
    // ------------------------------ Nested classes ------------------------------
    
    private class TextLine
    {
        // --------------- Object variables --------------- 
        private String text;
        private float lineSize;
        
        // ----------------- Constructors -----------------
        
        /** Creates a new line of text. If size is less than 0, the default text size is used.
         *  @param text Text to store.
         *  @param textSize Size of text.
         */
        public TextLine (String text, float textSize)
        {
            this.text = text;
            this.lineSize = textSize;
        }
        
        // ------------------- Methods -------------------
        
        public String getText()
        {
            return text;
        }
        
        public float getTextSize()
        {
            if (lineSize <0)
            {
                return textSize;
            }
            return lineSize;
        }
    }   
}