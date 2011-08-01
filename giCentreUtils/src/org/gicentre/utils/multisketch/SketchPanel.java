package org.gicentre.utils.multisketch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.Box;

import processing.core.PApplet;


//******************************************************************************************
/** Class for representing additional sketches in their own panel. Used for showing multiple
 *  sketches in the same window. 
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

public class SketchPanel extends Panel implements ComponentListener
{   
    // ----------------------- Class and object variables -----------------------

    private static final long serialVersionUID = 640333322417775109L;
    private EmbeddedSketch sketch;  // Sketch contained inside this panel.
 
    // ----------------------------- Constructors -------------------------------
     
    
    /** Creates the new panel containing the given sketch. 
     * @param parent Main processing sketch in which this is embedded.
     * @param sketch Sketch to be shown in this panel.
     */
    public SketchPanel(PApplet parent, EmbeddedSketch sketch) 
    { 
        this(parent,sketch,0);
    }
    /** Creates the new panel containing the given sketch with the given offset from the top. 
      * @param parent Main processing sketch in which this is embedded.
      * @param sketch Sketch to be shown in this panel.
      * @param offset Offset from top of sketch in pixels.
      */
    public SketchPanel(PApplet parent, EmbeddedSketch sketch, int offset) 
    { 
        setBackground(Color.white);
        setLayout(new BorderLayout());
        this.sketch = sketch;
        sketch.setParentSketch(parent);
        
        // Add spacer at top.
        if (offset > 0)
        {
            add(Box.createRigidArea(new Dimension(sketch.width,offset)),BorderLayout.NORTH);
            add(Box.createRigidArea(new Dimension(sketch.width,offset)),BorderLayout.SOUTH);
        }
        
        // Place new sketch in panel ensuring it has been started.
        add(sketch,BorderLayout.CENTER);
        sketch.init(); 
    
        // Listen out for a size() command from the sketch.
        sketch.addComponentListener(this);
    }
    
    // ------------------------ Implemented Methods -------------------------
  
    /** Responds to the component being hidden by deactivating the embedded sketch.
      * @param e Event associated with hiding the panel.
      */
    public void componentHidden(ComponentEvent e)
    {
        sketch.setIsActive(false);
    }
  
    /** Responds to the component being shown by activating the embedded sketch.
      * @param e Event associated with showing the panel.
      */
    public void componentShown(ComponentEvent e)
    {
        sketch.setIsActive(true);
    } 
 
    /** Responds to the component being resized by redrawing the embedded sketch.
      * @param e Event associated with the panel being resized.
      */
    public void componentResized(ComponentEvent e)
    {       
        // Check to see if a sketch has been resized via its size() command.
        if (e.getSource() instanceof EmbeddedSketch)
        {
            // Size panel to fit the sized sketch within its bounds.
            Dimension sketchSize = sketch.getPreferredSize();
            setSize(new Dimension(sketchSize.width+getBounds().x,sketchSize.height+getBounds().y));
            return;
        }
        sketch.loop();
    }

    /** Responds to the component being moved but does nothing in this case.
      * @param e Event associated with the panel being moved.
      */
    public void componentMoved(ComponentEvent e)
    {
        // Do nothing.
    }  
} 