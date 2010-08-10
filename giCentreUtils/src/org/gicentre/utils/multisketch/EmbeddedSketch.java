package org.gicentre.utils.multisketch;

import java.awt.event.KeyEvent;
import processing.core.*;

//******************************************************************************************
/** Class for representing additional sketches that may be added to the main Processing 
 *  sketch. An embedded sketch should subclass this class. It may contain the normal 
 *  Processing methods such as <code>draw()</code> and <code>setup()</code>. The 
 *  <code>draw()</code> method should call <code>super.draw()</code> to ensure that the 
 *  sketch does not loop when the embedded sketch is not active (e.g. it is in its own 
 *  window that has been minimised).
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

public class EmbeddedSketch extends PApplet
{
    private static final long serialVersionUID = -7647631957655290681L;
    
    // -------------------------- Object variables --------------------------

    private boolean isActive=false;         // Will not loop if sketch is not active.
    private PApplet parent;                 // Sketch that spawned this one.
    private boolean isHighlighted=false;    // Indicates if this panel is to be highlighted.

    // ------------------------------- Methods ------------------------------

    /** Ensures that the sketch does not loop through its draw() method if the 
      * embedded sketch is not active (e.g. it is iconified or invisible).
      */
    public void draw()
    {
        // Stop drawing if window is not being shown. 
        if (isActive)
        {
            loop();
        }
        else
        {
            noLoop();
            return;
        }
    }
    
    /** Handles key press events. If a parent sketch has been passed to this one, the
      * keyboard events are passed to the parent after processing by this sketch.
      * @param e Keyboard event. 
      */
    public void keyPressed(KeyEvent e)
    {       
        // Handle this sketch's keyboard events first.
        super.keyPressed(e);
        
        if ((parent != null) && (parent != this))
        {
            // Then pass events to the parent to consume if available.
            parent.keyPressed(e);
        }
    }
    
    /** Handles key release events. If a parent sketch has been passed to this one, the
      * keyboard events are passed to the parent after processing by this sketch.
      * @param e Keyboard event. 
      */
    public void keyReleased(KeyEvent e)
    {
        // Handle this sketch's keyboard events first.
        super.keyReleased(e);
        
        // Then pass events to the parent to consume if available.
        if ((parent != null) && (parent != this))
        {
            parent.keyReleased(e);
        }
    }
 
    /** Sets the parent sketch that spawned this embedded one. This will also
     *  use the parent's <code>sketchPath</code> in this embedded sketch when
     *  calling methods that rely on the default <code>data/</code> folder for
     *  loading and saving files (e.g. <code>loadImage()</code>, <code>loadFont()</code> etc. 
     * @param parent Parent sketch that created this one.
     */
    public void setParentSketch(PApplet parent)
    {
        this.parent = parent;
        if (parent != null)
        {
            sketchPath = parent.sketchPath;
        }
    }

    /** Reports the parent sketch that spawned this embedded one.
      * @return This sketch's parent sketch.
      */
    public PApplet getParentSketch()
    {
        return parent;
    }

    /** Determines whether or not the embedded sketch is active.
      * @param isActive Flag determining if sketch is active.
      */
    public void setIsActive(boolean isActive)
    {
        this.isActive = isActive;
        loop();
    }

    /** Reports whether or not the embedded sketch is active.
      * @return Reports if sketch is active.
      */
    public boolean isActive()
    {
        return isActive;
    }
    
    /** Determines whether or not the embedded sketch is highlighted.
      * @param isHighlighted Flag determining if sketch is highlighted.
      */
    public void setIsHighlighted(boolean isHighlighted)
    {
        this.isHighlighted = isHighlighted;
    }

    /** Reports whether or not the embedded sketch is highlighted.
      * @return Reports if sketch is highlighted.
      */
    public boolean isHighlighted()
    {
        return isHighlighted;
    }
}