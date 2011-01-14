package org.gicentre.utils.gui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import org.gicentre.utils.spatial.Direction;

import processing.core.*;

// *****************************************************************************************
/** Class to allow a simple 'tooltip' type pop-up panel to display text at a given location.
 *  Can be used for mouse-based tooltips or info-boxes.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.1, 14th January, 2011. 
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

public class Tooltip implements MouseListener
{
    // ----------------------------- Object variables ------------------------------
    
    private PApplet parent;                      // Parent sketch onto which this tooltip will be drawn.
    private PFont font;                          // Font used to display text.
    private ArrayList<String> lines;             // Text to be displayed in tooltip broken down into lines 
    private String originalText;                 // Original text to be displayed in tooltip.
    private float width,displayWidth,maxHeight;  // Size required to display text.
    private float textRowHeight;
    private float textSize;
    private static final int BORDER = 3;
    private float closeSize;
    private float pointerSize;
    private float x,y;                           // Anchor coordinates of the tooltip.
    
    private float borderWidth;
    private int backgroundColour,textColour, borderColour;
    private float xOffset,yOffset;
    private float textTopOffset,textBottomOffset, textLeftOffset, textRightOffset;
    private Direction anchor;
    private boolean showPointer,isCurved,showClose,isActive,isFixedWidth;
    private PVector[] boundary;
            
    // ------------------------------- Constructors --------------------------------
    
    /** Creates a tool tip with given parent sketch and font for displaying text.
     *  @param parent Sketch in which this tooltip will be displayed.
     *  @param font Font used to display text.
     *  @param textSize Size of text in pixels.
     *  @param width Width of tooltip in pixels. Height is determined by the content of the tip.
     */
    public Tooltip(PApplet parent, PFont font, float textSize, float width)
    {
        this.parent   = parent;
        this.font     = font;
        this.textSize = textSize;
        this.width    = width;
        
        displayWidth     = width;
        maxHeight        = 0;
        textRowHeight    = 0;
        borderWidth      = 0.2f;
        textTopOffset    = 0;
        textBottomOffset = 0;
        textLeftOffset   = 0;
        textRightOffset  = 0;
        pointerSize      = 15;
        closeSize        = 20-BORDER;
        borderColour     = new Color(100,100,100,100).getRGB();
        backgroundColour = new Color(255,248,147,200).getRGB();
        textColour       = new Color(50,50,50).getRGB();
        showPointer      = false;
        isCurved         = false;
        showClose        = false;
        isActive         = true;
        isFixedWidth     = true;
        setAnchor(Direction.WEST);
        parent.curveTightness(0.7f);
    }
    
    // ---------------------------------- Methods ----------------------------------
        
    /** Draws the tooltip at the given location.
     *  @param x x coordinate of location to display tooltip.
     *  @param y y coordinate of location to display tooltip.
     */
    public void draw(float x, float y)
    {
        if (isActive == false)
        {
            return;
        }
        
        this.x = x;
        this.y = y;
        
        if ((lines == null) || (lines.size()==0))
        {
            // Nothing to display.
            return;
        }
        
        parent.pushStyle();
        
        // Draw the enclosing shape.
        
        parent.fill(backgroundColour);
        parent.stroke(borderColour);
        parent.strokeWeight(borderWidth);
        
        if (showPointer)
        {
            parent.beginShape();
            if (isCurved)
            {
                for (PVector coord: boundary)
                {
                    parent.curveVertex(x+coord.x,y+coord.y);
                } 
                
                for (int i=0; i<3; i++)
                {
                    PVector coord = boundary[i];
                    parent.curveVertex(x+coord.x,y+coord.y);
                }
            }
            else
            {
                for (PVector coord: boundary)
                {
                    parent.vertex(x+coord.x,y+coord.y);
                }
            }
            parent.endShape(PConstants.CLOSE);
        }
        else
        {
            
            if (isCurved)
            {
                parent.beginShape();
                 parent.curveVertex(x+xOffset,y+yOffset);
                 parent.curveVertex(x+xOffset+displayWidth,y+yOffset);
                 parent.curveVertex(x+xOffset+displayWidth,y+yOffset+maxHeight);
                 parent.curveVertex(x+xOffset,y+yOffset+maxHeight);
                 parent.curveVertex(x+xOffset,y+yOffset);
                 parent.curveVertex(x+xOffset+displayWidth,y+yOffset);
                 parent.curveVertex(x+xOffset+displayWidth,y+yOffset+maxHeight);
                parent.endShape(PConstants.CLOSE);
            }
            else
            {
                parent.rect(x+xOffset,y+yOffset,displayWidth,maxHeight);
            }
        }
        
        // Draw the text inside the shape.
        parent.textFont(font);
        parent.textAlign(PConstants.LEFT, PConstants.TOP);
        
        float yPos = (y+yOffset)+BORDER+textTopOffset-textBottomOffset;
        
        parent.textSize(textSize);
        parent.fill(textColour);
        
        for (int i=0; i<lines.size(); i++)
        {
            String line = lines.get(i);
            parent.text(line, x+xOffset+BORDER+textLeftOffset, yPos);
            yPos += textRowHeight;
        }
        
        // Draw the close symbol.
        if (showClose)
        {
           parent.strokeWeight(1);
           parent.noFill();
           float cornerX = x+xOffset+displayWidth-closeSize-BORDER-textRightOffset;
           float cornerY = y+yOffset+BORDER+textTopOffset-textBottomOffset;
           parent.rect(cornerX, cornerY, closeSize, closeSize);
           parent.line(cornerX+BORDER, cornerY+BORDER, cornerX+closeSize-BORDER, cornerY+closeSize-BORDER);
           parent.line(cornerX+closeSize-BORDER, cornerY+BORDER, cornerX+BORDER, cornerY+closeSize-BORDER);
        }
        
        parent.popStyle();
    }
    
    /** Sets the text to be displayed in the tooltip.
     *  @param text Text to be displayed.
     */
    public void setText(String text)
    {
        this.originalText = text;
        updateLayout();       
    }
    
    /** Changes the colour of border drawn around the tooltip.
     *  @param borderColour New border colour to be drawn around the tooltip.
     */
    public void setBorderColour(int borderColour)
    {
        this.borderColour = borderColour;
    }
    
    /** Changes the colour of text to be displayed in the tooltip.
     *  @param textColour New text colour to be used by the tooltip.
     */
    public void setTextColour(int textColour)
    {
        this.textColour = textColour;
    }
    
    /** Changes the colour of background of the tooltip.
     *  @param backgroundColour New background colour to be used by the tooltip.
     */
    public void setBackgroundColour(int backgroundColour)
    {
        this.backgroundColour = backgroundColour;
    }
    
    /** Changes the width of border drawn around the tooltip.
     *  @param borderWidth Width of the border to be drawn around the tooltip in pixels. 
     *         Can be a fraction of a pixel.
     */
    public void setBorderWidth(float borderWidth)
    {
        this.borderWidth = borderWidth;
    }
    
    /** Determines if tip is drawn with slightly curved boundaries.
     *  @param curve Lines drawn as curves if true.
     */
    public void setIsCurved(boolean curve)
    {
        this.isCurved = curve;
    }
    
    /** Determines the size of the pointer when the tip is drawn with <code>showPointer(true)</code>.
     *  @param size Size of pointer in pixels.
     */
    public void setPointerSize(float size)
    {
        this.pointerSize = size;
        updateLayout();
    }
    
    /** Sets the anchor position of the tooltip. This determines where, in or around 
     *  the tooltip, the rectangle of text is drawn. For example, setting the anchor to 
     *  <code>Direction.WEST</code> will draw the tip to the right of the (x,y)
     *  coordinates provided to <code>draw()</code>.
     *  @param anchorPosition Position of the tip's anchor.
     */
    public void setAnchor(Direction anchorPosition)
    {
        this.anchor = anchorPosition;
        updateLayout();
    }
    
    /** Determines whether or not a pointer drawn as part of the tip pointing towards the anchor.
     *  @param showPointer Pointer is drawn if true.
     */
    public void showPointer(boolean showPointer)
    {
        this.showPointer = showPointer;
        updateLayout();
    }
    
    /** Determines if a 'close' icon should be drawn in the tooltip. If present, clicking on the icon 
     *  will set the state of the tip to be inactive (<code>isActive()</code> will return false).
     *  @param showClose True if a close icon is to be drawn.
     */
    public void showCloseIcon(boolean showClose)
    {
        this.showClose = showClose;
        parent.removeMouseListener(this);
        
        if (showClose)
        {
            parent.addMouseListener(this);
        }
        updateLayout();
    }
    
    /** Determines whether or not this tooltip should be active or not. When inactive it is not 
     *  drawn.
     *  @param isActive True if the tooltip is to be active, false if invisible.
     */
    public void setIsActive(boolean isActive)
    {
        this.isActive = isActive;
        parent.removeMouseListener(this);
        
        if (isActive)
        {
            if (showClose)
            {
                parent.addMouseListener(this);
            }
        }
    }
    
    /** Reports whether or not this tooltip is active. The active status can be controlled 
     *  programmatically through <code>setIsActive()</code> or it can be made inactive by the user 
     *  if the close icon is shown and the user has clicked on it. While the tooltip is inactive,
     *  it cannot be drawn.
     *  @return True if the tooltip is currently active.
     */
    public boolean isActive()
    {
        return isActive;
    }
    
    /** Determines whether or not this tooltip should have a fixed width. While fixed, the tooltip will 
     *  never change its width. If false and the text sent to the tip is one line long and takes up less
     *  than the width of the tip, the width is shrunk. This has no effect if the text to be displayed
     *  occupies more than one line of the tip.
     *  @param isFixedWidth True if the tip width is to be fixed.
     */
    public void setIsFixedWidth(boolean isFixedWidth)
    {
        if (isFixedWidth != this.isFixedWidth)
        {
            this.isFixedWidth = isFixedWidth;
            updateLayout();
        }
    }
    
    /** Reports whether or not this tooltip has a fixed width. While fixed, the tooltip will never change
     *  its width. If false and the text sent to the tip is one line long and takes up less than the 
     *  width of the tip, the width is shrunk.
     *  @return True if the tip width is fixed.
     */
    public boolean isFixedWidth()
    {
        return isFixedWidth;
    }
       
    /** Reports the width of the tooltip. If <code>isFixedWidth()</code> is true or the tooltip 
     *  contains more than one line, this will be the same as the maximum width specified in the 
     *  constructor. Otherwise the width will depend on the width of the text displayed in the
     *  tooltip.
     *  @return Width do the tooltip in pixel units.
     */
    public float getWidth()
    {
        return displayWidth;
    }

    /** Reports the height of the tooltip.
     *  @return Height of the tooltip in pixel units.
     */
    public float getHeight()
    {
        return maxHeight;
    }
    
    /** Checks to see if the user has clicked on the close icon of this tooltip if present.
     *  If clicked, the tooltip state is set to closed.
     *  @param e Mouse event storing the mouse pressed action.
     */
    public void mousePressed(MouseEvent e)
    {
        if (showPointer)
        {
            float cornerX = x+xOffset+displayWidth-closeSize-BORDER-textRightOffset;
            float cornerY = y+yOffset+BORDER+textTopOffset-textBottomOffset;
            
            if ((e.getX() >= cornerX) && (e.getY() >= cornerY) && 
                (e.getX() <= cornerX+closeSize) && (e.getY() <=cornerY+closeSize))
            {
                isActive = false;
            }
        }
    }
    
    /** Would respond to a mouse being clicked in this tooltip, but does nothing in this case.
     *  @param e Mouse event (ignored).
     *  @see  java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e)
    {
        // Do nothing.
    }

    /** Would respond to a mouse entering this tooltip, but does nothing in this case.
     *  @param e Mouse event (ignored).
     *  @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e)
    {
        // Do nothing.
    }

    /** Would respond to a mouse leaving this tooltip, but does nothing in this case.
     *  @param e Mouse event (ignored).
     *  @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e)
    {
        // Do nothing.
    }

    /** Would respond to a mouse being released over this tooltip, but does nothing in this case.
     *  @param e Mouse event (ignored).
     *  @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e)
    {
        // Do nothing
    }
    
    // ------------------------------ Private methods ------------------------------
    
    /** Recalculates the boundary of the tooltip and the text layout within it. Should be called if 
     *  some property of the tooltip that affects layout has been changed.
     */
    private void updateLayout()
    {
        // Find new text offsets that are dependent on pointer size and direction.
        textLeftOffset=0;
        textRightOffset=0;
        textTopOffset = 0;
        textBottomOffset = 0;
        
        if (showPointer)
        {
            if ((anchor == Direction.NORTH) || (anchor == Direction.NORTH_WEST) || (anchor == Direction.NORTH_EAST))
            {
                textTopOffset = pointerSize;
            }
            if ((anchor == Direction.SOUTH) || (anchor == Direction.SOUTH_WEST) || (anchor == Direction.SOUTH_EAST))
            {
                textBottomOffset = pointerSize;
            }
            if ((anchor == Direction.WEST) || (anchor == Direction.NORTH_WEST) || (anchor == Direction.SOUTH_WEST))
            {
                textLeftOffset = pointerSize;
            }
            if ((anchor == Direction.EAST)|| (anchor == Direction.NORTH_EAST) || (anchor == Direction.SOUTH_EAST))
            {
                textRightOffset = pointerSize;
            }
            
            // If we have a diagonal pointer, spread text offset between top and bottom.
            if (textTopOffset+textBottomOffset+textLeftOffset+textRightOffset > pointerSize)
            {
                textTopOffset /= 2;
                textBottomOffset /= 2;
                textLeftOffset /=2;
                textRightOffset /=2;
            }
        }
        
        // Layout text.
        if (originalText != null)
        {
            updateText();
        }
                
        // Find the new position of the popup (depends on anchor and size of tooltip).
        xOffset = 0;
        yOffset = 0;
                        
        if ((anchor == Direction.NORTH) || (anchor == Direction.CENTRE) || (anchor == Direction.SOUTH))
        {
            xOffset = -displayWidth/2f;
        }
        else if ((anchor == Direction.NORTH_EAST) || (anchor == Direction.EAST) || (anchor == Direction.SOUTH_EAST))
        {
            xOffset = -displayWidth;
        }
        
        if ((anchor == Direction.WEST) || (anchor == Direction.CENTRE) || (anchor == Direction.EAST))
        {
            yOffset = -maxHeight/2f;
        }
        else if ((anchor == Direction.SOUTH_WEST) || (anchor == Direction.SOUTH) || (anchor == Direction.SOUTH_EAST))
        {
            yOffset = -maxHeight;
        }
        
        // Update the pointer version of the boundary
        if (anchor == Direction.NORTH)
        {
            boundary = new PVector[7];
            boundary[0] = new PVector(xOffset+displayWidth/2,yOffset);
            boundary[1] = new PVector(xOffset+displayWidth/2+pointerSize,yOffset+pointerSize);
            boundary[2] = new PVector(xOffset+displayWidth,yOffset+pointerSize);
            boundary[3] = new PVector(xOffset+displayWidth,yOffset+maxHeight+pointerSize);
            boundary[4] = new PVector(xOffset,yOffset+maxHeight+pointerSize);
            boundary[5] = new PVector(xOffset,yOffset+pointerSize);
            boundary[6] = new PVector(xOffset+displayWidth/2-pointerSize,yOffset+pointerSize);
        }
        else if (anchor == Direction.NORTH_EAST)
        {
            boundary = new PVector[6];
            boundary[0] = new PVector(xOffset+displayWidth,yOffset);
            boundary[1] = new PVector(xOffset+displayWidth-pointerSize/2,yOffset+pointerSize);
            boundary[2] = new PVector(xOffset+displayWidth-pointerSize/2,yOffset+maxHeight+pointerSize/2);
            boundary[3] = new PVector(xOffset,yOffset+maxHeight+pointerSize/2);
            boundary[4] = new PVector(xOffset,yOffset+pointerSize/2);
            boundary[5] = new PVector(xOffset+displayWidth-pointerSize,yOffset+pointerSize/2);
        }
        else if (anchor == Direction.EAST)
        {
            boundary = new PVector[7];
            boundary[0] = new PVector(xOffset+displayWidth,yOffset+maxHeight/2);
            boundary[1] = new PVector(xOffset+displayWidth-pointerSize,yOffset+maxHeight/2+pointerSize);
            boundary[2] = new PVector(xOffset+displayWidth-pointerSize,yOffset+maxHeight);
            boundary[3] = new PVector(xOffset,yOffset+maxHeight);
            boundary[4] = new PVector(xOffset,yOffset);
            boundary[5] = new PVector(xOffset+displayWidth-pointerSize,yOffset);
            boundary[6] = new PVector(xOffset+displayWidth-pointerSize,yOffset+maxHeight/2-pointerSize);
        }
        else if (anchor == Direction.SOUTH_EAST)
        {
            boundary = new PVector[6];
            boundary[0] = new PVector(xOffset+displayWidth,yOffset+maxHeight);
            boundary[1] = new PVector(xOffset+displayWidth-pointerSize,yOffset+maxHeight-pointerSize/2);
            boundary[2] = new PVector(xOffset,yOffset+maxHeight-pointerSize/2);
            boundary[3] = new PVector(xOffset,yOffset-pointerSize/2);
            boundary[4] = new PVector(xOffset+displayWidth-pointerSize/2,yOffset-pointerSize/2);
            boundary[5] = new PVector(xOffset+displayWidth-pointerSize/2,yOffset+maxHeight-pointerSize);
        }
        else if (anchor == Direction.SOUTH)
        {
            boundary = new PVector[7];
            boundary[0] = new PVector(xOffset+displayWidth/2,yOffset+maxHeight);
            boundary[1] = new PVector(xOffset+displayWidth/2-pointerSize,yOffset+maxHeight-pointerSize);
            boundary[2] = new PVector(xOffset,yOffset+maxHeight-pointerSize);
            boundary[3] = new PVector(xOffset,yOffset-pointerSize);
            boundary[4] = new PVector(xOffset+displayWidth,yOffset-pointerSize);
            boundary[5] = new PVector(xOffset+displayWidth,yOffset+maxHeight-pointerSize);
            boundary[6] = new PVector(xOffset+displayWidth/2+pointerSize,yOffset+maxHeight-pointerSize);
        }
        else if (anchor == Direction.SOUTH_WEST)
        {
            boundary = new PVector[6];
            boundary[0] = new PVector(xOffset,yOffset+maxHeight);
            boundary[1] = new PVector(xOffset+pointerSize/2,yOffset+maxHeight-pointerSize);
            boundary[2] = new PVector(xOffset+pointerSize/2,yOffset-pointerSize/2);
            boundary[3] = new PVector(xOffset+displayWidth,yOffset-pointerSize/2);
            boundary[4] = new PVector(xOffset+displayWidth,yOffset+maxHeight-pointerSize/2);
            boundary[5] = new PVector(xOffset+pointerSize,yOffset+maxHeight-pointerSize/2);
        }
        else if (anchor == Direction.WEST)
        {
            boundary = new PVector[7];
            boundary[0] = new PVector(xOffset,yOffset+maxHeight/2);
            boundary[1] = new PVector(xOffset+pointerSize,yOffset+maxHeight/2-pointerSize);
            boundary[2] = new PVector(xOffset+pointerSize,yOffset);
            boundary[3] = new PVector(xOffset+displayWidth,yOffset);
            boundary[4] = new PVector(xOffset+displayWidth,yOffset+maxHeight);
            boundary[5] = new PVector(xOffset+pointerSize,yOffset+maxHeight);
            boundary[6] = new PVector(xOffset+pointerSize,yOffset+maxHeight/2+pointerSize);
        }
        else if (anchor == Direction.NORTH_WEST)
        {
            boundary = new PVector[6];
            boundary[0] = new PVector(xOffset,yOffset);
            boundary[1] = new PVector(xOffset+pointerSize,yOffset+pointerSize/2);
            boundary[2] = new PVector(xOffset+displayWidth,yOffset+pointerSize/2);
            boundary[3] = new PVector(xOffset+displayWidth,yOffset+maxHeight+pointerSize/2);
            boundary[4] = new PVector(xOffset+pointerSize/2,yOffset+maxHeight+pointerSize/2);
            boundary[5] = new PVector(xOffset+pointerSize/2,yOffset+pointerSize);
        }
    }
    
    /** Updates the layout of the text. Should be called if the text layout needs changing,
     *  for example due to new text being set, or an new anchor size or position being set.
     *  @param text Text to be displayed.
     */
    private void updateText()
    {
        // Need to find height of one row of text
        parent.pushStyle();
        parent.textFont(font,textSize);
        textRowHeight = parent.textAscent()+parent.textDescent();
                
        lines = new ArrayList<String>();
        String[] textLines = originalText.split("\\r?\\n");
        float xPos = BORDER;
        StringBuffer textLine = new StringBuffer();
        float spaceWidth = parent.textWidth(" ");
        maxHeight = 2*BORDER;
         
        for (String line : textLines)
        {
            float maxTextWidth = width-BORDER-textLeftOffset-textRightOffset;
            maxHeight += textRowHeight;
        
            String[] tokens = line.split("\\s");
            for (String token : tokens)
            {
                float tokenWidth = parent.textWidth(token.trim());
                float textWidth = maxTextWidth;
                
                if ((showClose) && (maxHeight-BORDER-textRowHeight < closeSize+BORDER))
                {
                    textWidth = maxTextWidth-closeSize;
                }
                if (xPos+tokenWidth <= textWidth)
                {
                    textLine.append(token);
                    textLine.append(" ");
                    xPos += (tokenWidth+spaceWidth);
                }
                else
                {
                    // Does not fit on end of line so create a new one.
                    lines.add(textLine.toString());
                    maxHeight += textRowHeight;
                    textLine = new StringBuffer(token);
                    textLine.append(" ");
                    xPos = BORDER+tokenWidth+spaceWidth;
                }
            }
            // Add last line.
            lines.add(textLine.toString());
            textLine = new StringBuffer();
            xPos = BORDER;
        }
        parent.popStyle();
        
        if ((isFixedWidth == false) && (lines.size() == 1))
        {
            displayWidth = parent.textWidth(lines.get(0))+BORDER-textLeftOffset-textRightOffset;
        }
        else
        {
            displayWidth = width;
        }
    }
}