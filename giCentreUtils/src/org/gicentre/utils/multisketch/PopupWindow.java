package org.gicentre.utils.multisketch;

import java.awt.*;
import java.awt.event.*;

import processing.core.*;

//******************************************************************************************
/** Class for representing sketches in their own popup window.
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

public class PopupWindow extends Frame implements WindowListener,ComponentListener
{   
    // ----------------------- Class and object variables -----------------------

    private static final long serialVersionUID = 2794387333809270860L;
    
    private EmbeddedSketch sketch;  // Sketch contained inside this window.
 
    // ----------------------------- Constructors -------------------------------
   
    /** Creates the new popup window containing the given sketch. 
      * @param parent Main Processing sketch to which this is attached.
      * @param sketch Sketch to be embedded in this window.
      */
    public PopupWindow(PApplet parent, EmbeddedSketch sketch) 
    { 
        this(parent,sketch,sketch.getClass().getName().replace("$",": "),-1,-1);
    } 
        
    /** Creates the new popup window containing the given sketch with title and location.
      * @param parent Main Processing sketch to which this is attached.
      * @param sketch Sketch to be embedded in this window.
      * @param title Title to appear in popup window.
      * @param wx x-coordinate of window position. If negative, the window will be centred on the screen.
      * @param wy y-coordinate of window position. If negative, the window will be centred on the screen.
      */
    public PopupWindow(PApplet parent, EmbeddedSketch sketch, String title, int wx, int wy) 
    { 
        this.sketch = sketch;
        sketch.setParentSketch(parent);
    
        // Setup this window, but don't make it visible.
        if ((wx <0) || (wy < 0))
        {
            // Centre window.
            setLocationRelativeTo(null);
        }
        else
        {
            setLocation(wx,wy);
        }
        setTitle(title);
        addWindowListener(this); 
        addComponentListener(this);
            
        // Initialise the sketch applet.
        setLayout(new BorderLayout());
        add(sketch,BorderLayout.CENTER); 
        sketch.init(); 
            
        // Listen out for a size() command from the sketch.
        sketch.addComponentListener(this);
    }
       
    // ------------------------ Implemented Methods -------------------------
  
    /** Responds to the window being opened by making the embedded sketch active.
      * @param e Event that opened the window.
      */
    public void windowOpened(WindowEvent e)
    {
        sketch.setIsActive(true);
    }
  
    /** Responds to the window being activated by making the embedded sketch active.
      * @param e Event that made the window active.
      */
    public void windowActivated(WindowEvent e)
    {
        sketch.setIsActive(true);
    } 
  
    /** Responds to the window being deactivated by making the embedded sketch inactive.
      * @param e Event that made the window inactive.
      */
    public void windowDeactivated(WindowEvent e)
    {
        sketch.setIsActive(false);
    } 
  
    /** Responds to the window being iconified by making the embedded sketch inactive.
      * @param e Event that iconified the window.
      */
    public void windowIconified(WindowEvent e)
    {
        sketch.setIsActive(false);
    } 
  
    /** Responds to the window being deiconified by making the embedded sketch active.
      * @param e Event that deiconified the window.
      */
    public void windowDeiconified(WindowEvent e)
    {
        sketch.setIsActive(true);
    } 
  
    /** Prepares to close the window by shutting down the embedded sketch.
      * @param e Event that requested the window closure.
      */
    public void windowClosing(WindowEvent e)
    {
        sketch.setIsActive(false);
        dispose();
    } 
    
    /** Closes the window containing the embedded sketch.
      * @param e Event that closed the window.
      */
    public void windowClosed(WindowEvent e)
    {
        // Do nothing as the window should close as normal.
    } 
 
    /** Responds to the component being hidden by deactivating the embedded sketch.
      * @param e Event associated with hiding the window.
      */
    public void componentHidden(ComponentEvent e)
    {
        sketch.setIsActive(false);
    }
  
    /** Responds to the component being shown by activating the embedded sketch.
      * @param e Event associated with showing the window.
      */
    public void componentShown(ComponentEvent e)
    {
        sketch.setIsActive(true);
    } 
 
    /** Responds to the component being resized by redrawing the embedded sketch.
      * @param e Event associated with the window being resized.
      */
    public void componentResized(ComponentEvent e)
    {
        // Check to see if a sketch has been resized via its size() command.
        if (e.getSource() instanceof EmbeddedSketch)
        {
            // Size window to fit the sized sketch within its bounds.
            Dimension sketchSize = sketch.getPreferredSize();
            setSize(new Dimension(sketchSize.width,sketchSize.height));
      
            // We no longer need to listen to the sketch once size has been set.
            sketch.removeComponentListener(this);
            return;
        }
        sketch.loop();
    }

    /** Responds to the component being moved but does nothing in this case.
      * @param e Event associated with the window being moved.
      */
    public void componentMoved(ComponentEvent e)
    {
        // Do nothing.
    }  
} 