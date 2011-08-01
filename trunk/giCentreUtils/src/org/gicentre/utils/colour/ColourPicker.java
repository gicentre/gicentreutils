package org.gicentre.utils.colour;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

// *****************************************************************************************
/** Creates a graphical colour picker using Cynthia Brewer's 'ColorBrewer' schemes.
 *  ColorBrewer specifications and designs developed by Cynthia Brewer 
 *  (<a href="http://colorbrewer.org/" target="_blank">colorbrewer.org</a>).
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.2, 1st August, 2011.
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

public class ColourPicker implements MouseListener, MouseMotionListener
{
    // ------------------ Object variables --------------------

    private PApplet sketch;
    private boolean isActive;
    private int xBorder, yBorder;
    private Vector<PickerListener> pickerListeners;
    private HashMap<Rectangle2D.Float, ColourTable> swatches;

    private ColourTable[] coloursCont, coloursDiv, coloursCat;      // Colour tables to use.
    
    private int imgWidth,imgHeight;
    private float barWidth, bottom;
    private PImage img;      // Offscreen buffer for displaying the swatches.

    private int lastColour;
    private ColourTable lastColourTable;

    private boolean isDragged;

    // --------------------- Constructors ----------------------

    /** Initialises the colour picker with default border size, but does not 
     *  display it [requires setIsActive(true)].
     *  @param sketch Sketch that will use the colour picker.
     */
    public ColourPicker(PApplet sketch)
    {
        this(sketch,30,30);
    }
    
    /** Initialises the colour picker with the given border sizes, but does
     *  not display it (requires setIsActive(true)]. The border defines the number
     *  of pixels from within the given sketch, the colour picker is drawn. So
     *  border values of 0 would take up the entire sketch, a border value of 10
     *  would leave a 10 pixel wide band of the original sketch visible behind the
     *  colour picker.
     *  @param sketch Sketch that will use the colour picker.
     *  @param xBorder Width in pixels of the border to the left and right of the picker.
     *  @param yBorder Height in pixels of the border to the top and bottom of the picker.
     */
    public ColourPicker(PApplet sketch, int xBorder, int yBorder)
    {
        this.sketch = sketch;
        this.xBorder = xBorder;
        this.yBorder = yBorder;
        isActive = false;
        pickerListeners = new Vector<PickerListener>();
        lastColour = Integer.MAX_VALUE;
        lastColourTable = null;
        isDragged = false;
        
        // Create the colour schemes to show.
        coloursCont = new ColourTable[18];
        coloursCont[0]  = ColourTable.getPresetColourTable(ColourTable.YL_GN,0,1);
        coloursCont[1]  = ColourTable.getPresetColourTable(ColourTable.YL_GN_BU,0,1);
        coloursCont[2]  = ColourTable.getPresetColourTable(ColourTable.GN_BU,0,1);
        coloursCont[3]  = ColourTable.getPresetColourTable(ColourTable.BU_GN,0,1);
        coloursCont[4]  = ColourTable.getPresetColourTable(ColourTable.PU_BU_GN,0,1);
        coloursCont[5]  = ColourTable.getPresetColourTable(ColourTable.PU_BU,0,1);
        coloursCont[6]  = ColourTable.getPresetColourTable(ColourTable.BU_PU,0,1);
        coloursCont[7]  = ColourTable.getPresetColourTable(ColourTable.RD_PU,0,1);
        coloursCont[8]  = ColourTable.getPresetColourTable(ColourTable.PU_RD,0,1);
        coloursCont[9]  = ColourTable.getPresetColourTable(ColourTable.OR_RD,0,1);
        coloursCont[10] = ColourTable.getPresetColourTable(ColourTable.YL_OR_RD,0,1);
        coloursCont[11] = ColourTable.getPresetColourTable(ColourTable.YL_OR_BR,0,1);
        coloursCont[12] = ColourTable.getPresetColourTable(ColourTable.PURPLES,0,1);
        coloursCont[13] = ColourTable.getPresetColourTable(ColourTable.BLUES,0,1);
        coloursCont[14] = ColourTable.getPresetColourTable(ColourTable.GREENS,0,1);
        coloursCont[15] = ColourTable.getPresetColourTable(ColourTable.ORANGES,0,1);
        coloursCont[16] = ColourTable.getPresetColourTable(ColourTable.REDS,0,1);
        coloursCont[17] = ColourTable.getPresetColourTable(ColourTable.GREYS,0,1);

        coloursDiv = new ColourTable[9];
        coloursDiv[0]  = ColourTable.getPresetColourTable(ColourTable.PU_OR,0,1);
        coloursDiv[1]  = ColourTable.getPresetColourTable(ColourTable.BR_B_G,0,1);
        coloursDiv[2]  = ColourTable.getPresetColourTable(ColourTable.P_R_GN,0,1);
        coloursDiv[3]  = ColourTable.getPresetColourTable(ColourTable.PI_Y_G,0,1);
        coloursDiv[4]  = ColourTable.getPresetColourTable(ColourTable.RD_BU,0,1);
        coloursDiv[5]  = ColourTable.getPresetColourTable(ColourTable.RD_GY,0,1);
        coloursDiv[6]  = ColourTable.getPresetColourTable(ColourTable.RD_YL_BU,0,1);
        coloursDiv[7]  = ColourTable.getPresetColourTable(ColourTable.SPECTRAL,0,1);
        coloursDiv[8]  = ColourTable.getPresetColourTable(ColourTable.RD_YL_GN,0,1);

        coloursCat = new ColourTable[8];
        coloursCat[0]  = ColourTable.getPresetColourTable(ColourTable.SET1_9);
        coloursCat[1]  = ColourTable.getPresetColourTable(ColourTable.SET2_8);
        coloursCat[2]  = ColourTable.getPresetColourTable(ColourTable.SET3_12);
        coloursCat[3]  = ColourTable.getPresetColourTable(ColourTable.PASTEL1_9);
        coloursCat[4]  = ColourTable.getPresetColourTable(ColourTable.PASTEL2_8);
        coloursCat[5]  = ColourTable.getPresetColourTable(ColourTable.DARK2_8);
        coloursCat[6]  = ColourTable.getPresetColourTable(ColourTable.PAIRED_12);
        coloursCat[7]  = ColourTable.getPresetColourTable(ColourTable.ACCENT_8);

        createPickerImage(sketch.createGraphics(Math.max(800,sketch.width-2*xBorder),
                                                Math.max(540,sketch.height-2*yBorder), 
                                                PConstants.JAVA2D));
    }

    // ----------------------- Methods ------------------------

    /** Draws the colour swatches from which items may be picked. This method should be called
     *  from within your sketch's <code>draw()</code> method. If the colour picker is not
     *  active, nothing will be drawn.
     */
    public void draw()
    {
        if (!isActive)
        {
            return;
        }

        sketch.pushStyle();
        sketch.image(img,xBorder,yBorder, sketch.width-2*xBorder, sketch.height-2*yBorder);
        sketch.noFill();
        sketch.strokeWeight(0.5f);
        sketch.stroke(120,50);
        sketch.rect(xBorder,yBorder, sketch.width-2*xBorder, sketch.height-2*yBorder);
        drawSelected();
        sketch.popStyle();
    }

    /** Reports whether or not the colour picker is currently active. An active colour picker
     *  is one that is displayed in the current sketch and able to accept colour selection.
     *  @return True if the colour picker is active.
     */
    public boolean getIsActive()
    {
        return isActive;
    }

    /** Determines whether or not the colour picker should be currently active. An active colour
     *  picker is one that is displayed in the current sketch and able to accept colour selection.
     *  @param isActive If true, the colour picker is made active.
     */
    public void setIsActive(boolean isActive)
    {
        this.isActive = isActive;

        if (isActive)
        {
            sketch.addMouseListener(this);
            sketch.addMouseMotionListener(this);
        }
        else
        {
            sketch.removeMouseListener(this);
            sketch.removeMouseMotionListener(this);
        }
    }

    /** Reports the last selected colour. Colours are represented as a Processing integer value,
     *  so can be placed directly inside Processing methods such as <code>fill()</code> and 
     *  <code>stroke()</code>.
     *  @return Last selected colour or <code>Integer.MAX_VALUE</code> if no colour has yet been selected.
     */
    public int getLastColour()
    {
        return lastColour;
    }

    /** Reports the last selected colour table.
     *  @return Last selected colour table or null if no colour table has yet been selected.
     */
    public ColourTable getLastColourTable()
    {
        return lastColourTable;
    }

    /** Adds the given pickerListener to those that will be informed when a colour has been chosen
     *  by the colour picker.
     *  @param pickerListener  Listener to add to those informed when a colour has been chosen.
     */
    public void addPickerListener(PickerListener pickerListener)
    {
        pickerListeners.add(pickerListener);
    }

    /** Removes the given pickerListener from those that will be informed when a colour has been chosen
     *  by the colour picker.
     *  @param pickerListener  Listener to remove from those informed when a colour has been chosen.
     *  @return True if the given listener was present and then removed.
     */
    public boolean removePickerListener(PickerListener pickerListener)
    {
        return pickerListeners.remove(pickerListener);
    } 

    // --------------------- Event handling ----------------------

    /** Responds to a mouse click in the colour picker, if active, updating the last selected
     *  colour and firing an event to all <code>PickerListener</code>s.
     *  event.
     *  @param e Mouse event storing the location of the mouse click.
     */
    public void mouseClicked(MouseEvent e)
    {
        if (isActive)
        {
            float xScale = (sketch.width-2*xBorder)/(float)imgWidth;
            float yScale = (sketch.height-2*yBorder)/(float)imgHeight;

            float xClick = (e.getX()-xBorder)/xScale;
            float yClick = (e.getY()-yBorder)/yScale;

            for (Rectangle2D.Float rect : swatches.keySet())
            {
                if (rect.contains(xClick,yClick))
                {
                    lastColourTable = swatches.get(rect);

                    // Find the colour selected:
                    float pos =(float)((xClick-rect.getX())/rect.getWidth());  // Scaled between 0-1
                    float range = lastColourTable.getMaxIndex() - lastColourTable.getMinIndex();

                    if (lastColourTable.getIsDiscrete())
                    {
                        if (range==1)
                        {
                            // Dicrete versions of continuous schemes need to be quantized.
                            pos = ((int)(pos*9))/9f + 0.055555f;            
                        }
                        else  
                        {
                            // Dicrete qualitative scheme.
                            float numColours = range+1;
                            pos = pos*numColours+0.5f;
                        }
                    }

                    lastColour = lastColourTable.findColour(pos);
                    fireColourSelectionEvent();
                    return;
                }
            }

            // If a click has occurred outside of a swatch, set the selected colour/colour table to null.
            lastColourTable = null;
            lastColour = Integer.MAX_VALUE;
            fireColourSelectionEvent();
        }
    }

    /** Would respond to a mouse entering the colour picker, but does nothing in this case.
     *  @param e Mouse event (ignored).
     */
    public void mouseEntered(MouseEvent e)
    {
        // Do nothing.
    }

    /** Would respond to a mouse leaving the colour picker, but does nothing in this case.
     *  @param e Mouse event (ignored).
     */  
    public void mouseExited(MouseEvent e)
    {
        // Do nothing.
    }

    /** Would respond to a mouse being pressed in the colour picker, but does nothing in this case.
     *  @param e Mouse event (ignored).
     */
    public void mousePressed(MouseEvent e)
    {
        // Do nothing.
    }

    /** Responds to a mouse being released over the colour picker. If this release is after a mouse
     *  drag and it is over a colour, it fires an event to all <code>PickerListener</code>s.
     *  @param e Mouse event (ignored).
     */
    public void mouseReleased(MouseEvent e)
    {
        if ((isActive) && (isDragged))
        {
            isDragged = false;
            if (lastColourTable != null)
            {
                fireColourSelectionEvent(); 
            }
        }
    } 

    /** Responds to a mouse being dragged over the colour picker, if active, updating the 
     *  last selected colour. Listeners are not informed until the mouse is released.
     *  @param e Mouse event storing the location of the mouse drag.
     */
    public void mouseDragged(MouseEvent e)
    {
        if (isActive)
        {
            isDragged = true;
            float xScale = (sketch.width-2*xBorder)/(float)imgWidth;
            float yScale = (sketch.height-2*yBorder)/(float)imgHeight;

            float xClick = (e.getX()-xBorder)/xScale;
            float yClick = (e.getY()-yBorder)/yScale;

            for (Rectangle2D.Float rect : swatches.keySet())
            {
                if (rect.contains(xClick,yClick))
                {
                    lastColourTable = swatches.get(rect);

                    // Find the colour selected:
                    float pos =(float)((xClick-rect.getX())/rect.getWidth());  // Scaled between 0-1
                    float range = lastColourTable.getMaxIndex() - lastColourTable.getMinIndex();

                    if (lastColourTable.getIsDiscrete())
                    {
                        if (range==1)
                        {
                            // Discrete versions of continuous schemes need to be quantized.
                            pos = ((int)(pos*9))/9f + 0.055555f;            
                        }
                        else  
                        {
                            // Discrete qualitative scheme
                            float numColours = range+1;
                            pos = pos*numColours+0.5f;
                        }
                    }
                    lastColour = lastColourTable.findColour(pos);
                    return;
                }
            }
            // Don't do anything if the mouse has been dragged outside a swatch.
        }
    }

    /** Would respond to a mouse being moved over the colour picker, but does nothing in this case.
     *  @param e Mouse event (ignored).
     */
    public void mouseMoved(MouseEvent e)
    {
        // Do nothing.
    }

    // --------------------- Private methods ----------------------

    /** Informs any listeners that a colour has been selected by the picker.
     */
    private void fireColourSelectionEvent()
    {
        for (PickerListener listener : pickerListeners)
        {
            listener.colourChosen();
        }
    }

    /** Creates the image for displaying the swatches for colour selection. This should only 
     *  need to be called once.
     *  @param buffer Offscreen buffer in which to draw the swatches.
     */
    private void createPickerImage(PGraphics buffer)
    {
        swatches = new HashMap<Rectangle2D.Float, ColourTable>();
        
        // Use the class loader to ensure we look in this .jar file for the fonts.
        PFont largeFont=null,smallFont=null;
        ClassLoader cl = getClass().getClassLoader();

        InputStream stream = cl.getResourceAsStream("data/BonvenoCF-Light-18.vlw");
        if (stream != null) 
        {
        	String cn = stream.getClass().getName();
        	if (!cn.equals("sun.plugin.cache.EmptyInputStream")) 
        	{
        		try
        		{
        			largeFont = new PFont(stream);
        			stream.close();
        		}
        		catch (IOException e)
        		{
        			// Silently fail.
        		}
        	}
        }

        stream = cl.getResourceAsStream("data/BonvenoCF-Light-10.vlw");
        if (stream != null) 
        {
        	String cn = stream.getClass().getName();
        	if (!cn.equals("sun.plugin.cache.EmptyInputStream")) 
        	{
        		try
        		{
        			smallFont = new PFont(stream);
        			stream.close();
        		}
        		catch (IOException e)
        		{
        			// Silently fail.
        		}
        	}
        }
        
        if (largeFont == null)
        {
        	largeFont = sketch.createFont("sans serif",18);
        }
        if (smallFont == null)
        {
        	smallFont = sketch.createFont("sans serif",10);
        }
        
        buffer.beginDraw();
        buffer.smooth();
        buffer.background(255,220);
        buffer.textAlign(PConstants.CENTER,PConstants.TOP);
        buffer.strokeWeight(0.5f);
        buffer.stroke(180);

        imgWidth  = buffer.width;
        imgHeight = buffer.height;

        int border = 8;
        int colOffset=border; 
        int textSpace = 46; 
        barWidth = (imgWidth - (border*6 + textSpace*2)) / 4;
        int rowOffset = 24 + border;

        buffer.fill(120);
        buffer.textFont(largeFont);
        buffer.text("Sequential",border+barWidth+textSpace/2,border);
        buffer.text("Diverging", 2*border + 3*(border+barWidth) + 1.5f*textSpace,border);
        buffer.text("Categorical",2*border + 2.7f*(border+barWidth) + 1.5f*textSpace, border + (coloursDiv.length+1)*25);
        buffer.textFont(smallFont);

        // Brewer continuous colours 
        for (int bar=0; bar<coloursCont.length; bar++)
        {
            // Draw a continuous colour bar.
            float inc = 0.01f;
            colOffset = border;

            for (float i=0; i<1-inc; i+=inc)
            {
                buffer.fill(coloursCont[bar].findColour(i));
                buffer.stroke(coloursCont[bar].findColour(i));
                buffer.rect(colOffset + barWidth*i, rowOffset + bar*25 ,barWidth*inc+1,20);
            }

            // Draw rectangle around continuous colour bar.
            buffer.stroke(0,100);
            buffer.noFill();
            buffer.rect(colOffset,rowOffset + bar*25, barWidth,20);

            // Store the rectangle and the colour scheme
            swatches.put(new Rectangle2D.Float(colOffset, rowOffset + bar*25, barWidth, 20),coloursCont[bar]);

            // Label colour scheme.
            buffer.fill(80);
            buffer.textAlign(PConstants.CENTER,PConstants.TOP);
            buffer.text(coloursCont[bar].getName(),colOffset + barWidth+(border+textSpace)/2, rowOffset +bar*25 + 5);

            // Draw the discrete version of the continuous Brewer colour scheme.
            colOffset += barWidth+border + textSpace;
            buffer.stroke(0,100);
            inc = 1/9.0f;

            for (float i=0; i<1; i+=inc)
            {
                buffer.fill(coloursCont[bar].findColour(i + 0.5f*inc));
                buffer.rect(colOffset + barWidth*i, rowOffset + bar*25, barWidth*inc,20);
            }

            // Store the rectangle and a discrete version of the colour scheme
            ColourTable discreteCTable = new ColourTable(coloursCont[bar]);
            discreteCTable.setIsDiscrete(true);
            swatches.put(new Rectangle2D.Float(colOffset, rowOffset + bar*25, barWidth, 20),discreteCTable);

        }

        // Brewer diverging colour schemes.
        for (int bar=0; bar<coloursDiv.length; bar++)
        {
            // Draw a continuous colour bar.
            float inc = 0.01f;
            colOffset = (int)(barWidth+border*2)*2 + textSpace;

            for (float i=0; i<1-inc; i+=inc)
            {
                buffer.fill(coloursDiv[bar].findColour(i));
                buffer.stroke(coloursDiv[bar].findColour(i));
                buffer.rect(colOffset + barWidth*i, rowOffset + bar*25 ,barWidth*inc+1,20);
            }

            // Draw rectangle around continuous colour bar.
            buffer.stroke(0,100);
            buffer.noFill();
            buffer.rect(colOffset,rowOffset + bar*25, barWidth,20);

            // Store the rectangle and the colour scheme
            swatches.put(new Rectangle2D.Float(colOffset, rowOffset + bar*25, barWidth, 20),coloursDiv[bar]);

            // Label colour scheme.
            buffer.fill(80);
            buffer.textAlign(PConstants.CENTER,PConstants.TOP);
            buffer.text(coloursDiv[bar].getName(),colOffset + barWidth+(border+textSpace)/2, rowOffset +bar*25 + 5);

            // Draw the discrete version of the continuous Brewer colour scheme.
            colOffset += barWidth+border + textSpace;
            buffer.stroke(0,100);
            inc = 1/9.0f;

            for (float i=0; i<1; i+=inc)
            {
                buffer.fill(coloursDiv[bar].findColour(i + 0.5f*inc));
                buffer.rect(colOffset + barWidth*i, rowOffset + bar*25, barWidth*inc,20);
            }

            // Store the rectangle and a discrete version of the colour scheme
            ColourTable discreteCTable = new ColourTable(coloursDiv[bar]);
            discreteCTable.setIsDiscrete(true);
            swatches.put(new Rectangle2D.Float(colOffset, rowOffset + bar*25, barWidth, 20),discreteCTable);
        }

        // Brewer qualitative colour schemes.
        colOffset = (int)((barWidth+border*2)*2.2 + textSpace*1.5);
        for (int bar=0; bar<coloursCat.length; bar++)
        {
            coloursCat[bar].setIsDiscrete(true);
            // Draw the Brewer colour scheme as a discrete colour bar.
            rowOffset = 24 + border + (coloursDiv.length+1)*25;
            buffer.stroke(0,100);

            // Find out how many discrete colours have been defined to set appropriate number of boxes in bar.
            int numColours = coloursCat[bar].getColourRules().size()-1;
            for (float i=0; i<numColours; i++)
            {
                buffer.fill(coloursCat[bar].findColour(i+1));
                buffer.rect(colOffset + barWidth*i/numColours, rowOffset + bar*25, barWidth/numColours,20);
            }

            // Label colour scheme.
            buffer.textAlign(PConstants.RIGHT,PConstants.TOP);
            buffer.fill(80);
            buffer.text(coloursCat[bar].getName(),colOffset-border, rowOffset +bar*25 + 5);

            // Store the rectangle and the colour scheme
            swatches.put(new Rectangle2D.Float(colOffset, rowOffset + bar*25, barWidth, 20),coloursCat[bar]);
            bottom = rowOffset + bar*25+20;
        } 
        buffer.endDraw();

        // Create a displayable image from the offscreen buffer.
        img = buffer.get(0,0,imgWidth, imgHeight);
    }

    /** Draws the currently selected colour and colour table.
     */
    private void drawSelected()
    {
        float xScale = (sketch.width-2*xBorder)/(float)imgWidth;
        float yScale = (sketch.height-2*yBorder)/(float)imgHeight;

        float selectedBarWidth = 20*xScale;
        float selectedBarHeight = 1.3f*barWidth*yScale;
        float x = (sketch.width-xBorder) - 3.9f*selectedBarWidth;
        float y = yBorder +bottom*yScale - selectedBarHeight;

        // Draw empty rectangles if no colour currently selected.
        if (lastColour == Integer.MAX_VALUE)
        {
            sketch.rect(x,y + selectedBarHeight/4,selectedBarWidth*3,selectedBarHeight/2);
            x = (sketch.width-xBorder) - 5.4f*selectedBarWidth;
            sketch.rect(x,y,selectedBarWidth,selectedBarHeight);
            return;
        }

        // Draw last selected colour
        sketch.fill(lastColour);
        sketch.rect(x,y + selectedBarHeight/4,selectedBarWidth*3,selectedBarHeight/2);

        // Draw last selected colour table.
        x = (sketch.width-xBorder) - 5.4f*selectedBarWidth;

        if (lastColourTable.getIsDiscrete())
        {
            float maxIndex = lastColourTable.getMaxIndex();
            float range = lastColourTable.getMaxIndex()-lastColourTable.getMinIndex();

            if (range == 1)
            {
                // Discrete version of a continuous scheme.
                float inc = 1/9.0f;
                for (float i=0; i<1; i+=inc)
                {
                    sketch.fill(lastColourTable.findColour(maxIndex - (i*range + 0.5f*inc)));
                    sketch.rect(x, y+selectedBarHeight*i,selectedBarWidth, selectedBarHeight*inc+1);
                }
            }
            else
            {
                // Qualitative categorical scheme.        
                int numColours = lastColourTable.getColourRules().size()-1;
                for (float i=0; i<numColours; i++)
                {
                    sketch.fill(lastColourTable.findColour(numColours -i));
                    sketch.rect(x, y+ selectedBarHeight*i/numColours, selectedBarWidth, selectedBarHeight/numColours);
                }
            }
        }
        else
        {
            float inc = 0.01f;
            float maxIndex = lastColourTable.getMaxIndex();
            float range = lastColourTable.getMaxIndex()-lastColourTable.getMinIndex();

            for (float i=0; i<1-inc; i+=inc)
            {
                sketch.fill(lastColourTable.findColour(maxIndex - i*range));
                sketch.stroke(lastColourTable.findColour(maxIndex - i*range));
                sketch.rect(x, y+selectedBarHeight*i,selectedBarWidth, selectedBarHeight*inc+1);
            }

            // Draw rectangle around continuous colour bar.
            sketch.stroke(200);
            sketch.strokeWeight(Math.min(0.5f,xScale));
            sketch.noFill();
            sketch.rect(x,y,selectedBarWidth,selectedBarHeight);
        }
    }
}