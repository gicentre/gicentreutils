package org.gicentre.utils.move;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

// *****************************************************************************************
/** Class to allow interactive zooming and panning of the Processing display. To use, simply
 *  create a ZoomPan object in setup(), then call its transform() method at the start of 
 *  draw(). Panning is controlled with the right mouse button dragging. Zooming is, by 
 *  default, controlled with a left mouse drag up to zoom in, left-drag down to zoom out. 
 *  Can also zoom in and out with the mouse wheel if present. Mouse actions can be filtered
 *  so that they only work if a modifier key is pressed (ALT, SHIFT or CONTROL) by calling
 *  the setMouseMask() method.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.1.2, 14th April, 2011. 
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

public class ZoomPan
{
	// ---------------------------- Object variables -----------------------------

	private PVector zoomStartPosition,oldPosition,panOffset;
	private double zoomScale, zoomStep;
	private AffineTransform trans,iTrans;
	private boolean isZooming, isPanning,isMouseCaptured;
	private boolean allowZoomButton, allowPanButton;
	private int mouseMask = 0;
	private PApplet aContext;
	private PGraphics graphics; //don't reference this directly - always use getGraphics()
	private Vector<ZoomPanListener> listeners;
	private int zoomMouseButton=PConstants.LEFT; // Implies pan is the other button

	// ------------------------------- Constructor ------------------------------- 

	/** Initialises the zooming and panning transformations for the given applet context. 
	 *  Can be used to have independent zooming in multiple windows by creating multiple
	 *  objects each with a different PApplet object.
	 *  @param aContext Applet context in which zooming and panning are to take place. 
	 */
	public ZoomPan(PApplet aContext)
	{
		this.aContext = aContext;
		if (aContext == null)
		{
			System.err.println("Warning: No applet context provided for ZoomPan.");
			return;
		}
		graphics = null;
		allowZoomButton = true;
		allowPanButton = true;
		listeners = new Vector<ZoomPanListener>();
		reset();
		aContext.registerMouseEvent(this);
		aContext.addMouseWheelListener(new MouseWheelMonitor());
	}

	/** Initialises the zooming and panning transformations for the given applet and graphics contexts. 
	 *  This version of the constructor allows a graphics context separate from the applet to be applied
	 *  so that buffered off-screen drawing can be applied. 
	 *  @param aContext Applet context in which zooming and panning are to take place. 
	 *  @param graphics Graphics context in which to draw.
	 */
	public ZoomPan(PApplet aContext, PGraphics graphics)
	{
		this.aContext = aContext;
		this.graphics = graphics;

		if (aContext == null)
		{
			System.err.println("Warning: No applet context provided for ZoomPan.");
			return;
		}
		if (graphics == null)
		{
			System.err.println("Warning: No graphics context provided for ZoomPan.");
			return;
		}
		allowZoomButton = true;
		allowPanButton = true;
		listeners = new Vector<ZoomPanListener>();
		reset();
		aContext.registerMouseEvent(this);
		aContext.addMouseWheelListener(new MouseWheelMonitor());
	}

	// ------------------------------ Public methods -----------------------------

	/** Performs the zooming/panning transformation. This method should be called in the
	 *  draw() method before any drawing that is to be zoomed or panned. 
	 */
	public void transform()
	{    
		getGraphics().translate((float)trans.getTranslateX(),(float)trans.getTranslateY());
		getGraphics().scale((float)trans.getScaleX(),(float)trans.getScaleY());
	}
	
	/** Performs the zooming/panning transformation in the given graphics context. This version of transform()
	 *  can be used for transforming off-screen buffers that were not provided to the constructor. Can
	 *  be useful when a sketch temporarily creates an off-screen buffer that needs to be zoomed and panned
	 *  in the same way as the main PApplet.
	 */
	public void transform(PGraphics offScreenBuffer)
	{    
		offScreenBuffer.translate((float)trans.getTranslateX(),(float)trans.getTranslateY());
		offScreenBuffer.scale((float)trans.getScaleX(),(float)trans.getScaleY());
	}
	
	/** Resets the display to unzoomed and unpanned position.
	 */
	public void reset()
	{
		trans           = new AffineTransform();
		iTrans          = new AffineTransform();
		zoomScale       = 1;
		panOffset       = new PVector(0,0);
		zoomStep        = 1.05;
		isZooming       = false;
		isPanning       = false;
		isMouseCaptured = false;
	}

	/** Adds a listener to be informed when some zooming or panning has finished.
	 *  @param zoomPanListener Listener to be informed when some zooming or panning has finished.
	 */
	public void addZoomPanListener(ZoomPanListener zoomPanListener)
	{
		listeners.add(zoomPanListener); 
	}

	/** Removes the given listener from those to be informed when zooming/panning has finished.
	 *  @param zoomPanListener Listener to remove.
	 *  @return True if listener found and removed.
	 */
	public boolean removeZoomPanListener(ZoomPanListener zoomPanListener)
	{
		return listeners.remove(zoomPanListener); 
	}
	

	/** Sets the key that must be pressed before mouse actions are active. By default, no key
	 *  is needed for the mouse to be active. Specifying a value allows normal mouse actions to
	 *  be intercepted without zooming or panning. To set the mouse mask to no key, specify a 
	 *  mouseMask value of 0. Mouse actions can be disabled entirely by setting the mouseMask
	 *  to a negative value.
	 *  @param mouseMask Keyboard modifier required to activate mouse actions. Valid values are
	 *  <code>CONTROL</code>, <code>SHIFT</code>, <code>ALT</code>, <code>0</code> and <code>-1</code>. 
	 */
	public void setMouseMask(int mouseMask)
	{
		if (mouseMask < 0)
		{
			this.mouseMask = -1;
			return;
		}

		switch (mouseMask)
		{
			case PConstants.CONTROL:
				this.mouseMask = InputEvent.CTRL_DOWN_MASK;
				break;
	
			case PConstants.SHIFT:
				this.mouseMask = InputEvent.SHIFT_DOWN_MASK;
				break;
	
			case PConstants.ALT:
				this.mouseMask = InputEvent.ALT_DOWN_MASK;
				break;
	
			default:
				this.mouseMask = 0;
		}
	}      

	/** Reports the current mouse position in coordinate space. This method should be used
	 *  in preference to <code>mouseX </code>and <code>mouseY</code> if the current display 
	 *  has been zoomed or panned.
	 *  @return Coordinates of current mouse position accounting for any zooming or panning.
	 */
	public PVector getMouseCoord()
	{
		return getDispToCoord(new PVector(aContext.mouseX,aContext.mouseY));
	}

	/** Reports the current zoom scale. Can be used for drawing objects that maintain their
	 *  size when zooming.
	 *  @return Current zoom scale. 
	 */
	public double getZoomScale()
	{
		return zoomScale;
	}

	/** Sets a new zoom scale. Can be used for programmatic control of zoomer, such as
	 *  eased interpolated zooming.
	 *  @param zoomScale New zoom scale. A value of 1 indicates no zooming, values above
	 *         0 and below 1 will shrink the display; values above 1 will enlarge the 
	 *         display. Values less than or equal to 0 will be ignored. 
	 */
	public void setZoomScale(double zoomScale)
	{
		this.zoomScale = zoomScale;
		calcTransformation();
	}


	/** Reports the current pan offset. Useful when wishing to use an interpolated panning
	 *  between this current value and some new pan offset.
	 *  @return Current pan offset. Negative coordinates indicate an offset to the left
	 *          or upwards, positive values to the right or downward.       
	 */
	public PVector getPanOffset()
	{
		return new PVector(panOffset.x,panOffset.y);
	}

	/** Sets a new pan offset. Can be used for programmatic control of panning, such as
	 *  eased interpolated zooming and panning.
	 *  @param panX X coordinate of new pan offset. A value of 0 indicates no translation
	 *         of the display on the horizontal axis; a negative value indicates a 
	 *         translation to the left; a positive value indicates translation to the right.
	 *  @param panY Y coordinate of new pan offset. A value of 0 indicates no translation
	 *         of the display on the vertical axis; a negative value indicates a translation
	 *         upwards; a positive value indicates translation downwards.
	 *         
	 */
	public void setPanOffset(float panX, float panY)
	{
		panOffset.x = panX;
		panOffset.y = panY;
		calcTransformation();
	}

	/** Reports whether display is currently being zoomed (i.e. mouse is being dragged with 
	 *  zoom key/button pressed).
	 *  @return True if display is being actively zoomed. 
	 */
	public boolean isZooming()
	{
		return isZooming;
	}

	/** Reports whether display is currently being panned (ie mouse is being dragged with
	 *  pan key/button pressed).
	 *  @return True if display is being actively panned. 
	 */
	public boolean isPanning()
	{
		return isPanning;
	}

	/** Reports whether a mouse event has been captured by the zoomer. This allows zoom and 
	 *  pan events to be separated from other mouse actions. Usually only useful if the zoomer
	 *  uses some mouse mask.
	 *  @return True if mouse event has been captured by the zoomer. 
	 */
	public boolean isMouseCaptured()
	{
		return isMouseCaptured;
	}
	
	/** Determines whether or not zooming via a button press is permitted. By default zooming is 
	 *  enabled with the appropriate mouse button, but can be disabled by setting allowZoom to false.
	 *  Note that the scroll wheel will zoom whether or not the zoom button is activated.
	 *  @param allowZoom Zooming permitted via mouse button press if true.
	 */
	public void allowZoomButton(boolean allowZoom)
	{
		this.allowZoomButton = allowZoom;
	}
	
	/** Determines whether or not panning is permitted via a button press. By default panning is
	 *  enabled with the appropriate mouse button, but can be disabled by setting allowPan to false.
	 *  @param allowPan Panning permitted via mouse button press if true.
	 */
	public void allowPanButton(boolean allowPan)
	{
		this.allowPanButton = allowPan;
	}

	/** Updates zoom and pan transformation according to mouse activity.
	 *  @param e Mouse event.
	 */
	public void mouseEvent(MouseEvent e)
	{   
		if (mouseMask == -1)
		{
			// If mouse has been disabled with a negative mouse mask, don't do anything.
			return;
		}

		if (e.getID() == MouseEvent.MOUSE_RELEASED)
		{
			// Regardless of mouse mask, if the mouse is released, 
			// that is the end of the zooming and panning.

			boolean isZoomEnded = false;
			boolean isPanEnded = false;

			if (isZooming)
			{
				isZooming = false;
				isZoomEnded = true;
			}

			if (isPanning)
			{
				isPanning = false;
				isPanEnded = true;
			}

			zoomStep = 1.05;
			isMouseCaptured = false;

			// Inform all listeners that some zooming or panning has just finished.
			for (ZoomPanListener listener: listeners)
			{
				if (isZoomEnded)
				{
					listener.zoomEnded();
				}
				if (isPanEnded)
				{
					listener.panEnded();
				}
			}
		}

		// The remaining events only apply if the mouse mask is specified and it is pressed.
		if ((e.getModifiersEx() & mouseMask) != mouseMask)
		{
			return;
		}

		if (e.getID() == MouseEvent.MOUSE_PRESSED)
		{
			isMouseCaptured   = true;
			zoomStartPosition = new PVector(e.getX(),e.getY());
			oldPosition       = new PVector(e.getX(),e.getY());
		}
		else if (e.getID() == MouseEvent.MOUSE_DRAGGED)
		{
			// Check in case applet has been destroyed.
			if ((aContext == null) || (oldPosition == null))
			{
				return;
			}

			if ((aContext.mouseButton==zoomMouseButton) && (allowZoomButton))
			{
				isZooming = true;

				if (aContext.mouseY < oldPosition.y)
				{
					zoomScale *= zoomStep;
				}
				else if (aContext.mouseY > oldPosition.y)
				{
					zoomScale /= zoomStep;
				}
				doZoom();
				zoomStep += 0.005;    // Accelerate zooming with prolonged drag.
			}
			else if (allowPanButton)
			{        
				isPanning = true;
				//panOffset.setLocation((panOffset.y + e.getX() - oldPosition.y),
				//                     panOffset.y+ e.getY() - oldPosition.y);
				panOffset.add(new PVector(e.getX()-oldPosition.x,e.getY()-oldPosition.y));
				calcTransformation(); 
			}

			oldPosition = new PVector(e.getX(),e.getY());
		}
	}

	/** Transforms the given point from display to coordinate space. Display space is that which
	 *  has been subjected to zooming and panning. Coordinate space is the original space into 
	 *  which objects have been placed before zooming and panning. For most drawing operations you
	 *  should not need to use this method. It is available for those operations that do not draw
	 *  directly, but need to know the transformation between coordinate and screen space.
	 *  @param p 2D point in zoomed display space.
	 *  @return Location of point in original coordinate space. 
	 */
	public PVector getDispToCoord(PVector p)
	{
		Point2D.Float pCoord = new Point2D.Float();
		Point2D.Float pDisp = new Point2D.Float(p.x,p.y);
		iTrans.transform(pDisp,pCoord);
		return new PVector(pCoord.x,pCoord.y);        
	}

	/** Transforms the given point from coordinate to display space. Display space is that which
	 *  has been subjected to zooming and panning. Coordinate space is the original space into 
	 *  which objects have been placed before zooming and panning. For most drawing operations you
	 *  should not need to use this method. It is available for those operations that do not draw
	 *  directly, but need to know the transformation between coordinate and screen space.
	 *  @param p 2D point in original coordinate space.
	 *  @return Location of point in zoomed display space. 
	 */
	public PVector getCoordToDisp(PVector p)
	{
		Point2D.Float pDisp = new Point2D.Float();
		Point2D.Float pCoord = new Point2D.Float(p.x,p.y);
		trans.transform(pCoord,pDisp);
		return new PVector(pDisp.x,pDisp.y); 
	}

	/** Sets mouse button for zooming. If this is set to either LEFT or RIGHT, the other button (RIGHT or LEFT)
	 *  will be set for panning.
	 *  @param zoomMouseButton Zoom mouse button (must be either PConstants.LEFT or PConstants.RIGHT
	 */
	public void setZoomMouseButton(int zoomMouseButton)
	{
		if (zoomMouseButton==PConstants.LEFT || zoomMouseButton==PConstants.RIGHT)
		{
			this.zoomMouseButton=zoomMouseButton;
		}
		else
		{
			System.err.println("setZoomMouseButton: Parameter must be LEFT, RIGHT or CENTER");
		}
	}

	/** Replacement for Processing's <code>text()</code> method for faster and more accurate placement of 
	 *  characters in Java2D mode when a zoomed font is to be displayed. This method is not necessary when
	 *  text is not subject to scaling via zooming, nor is is necessary in <code>P2D</code>, <code>P3D</code>
	 *  or <code>OpenGL</code> modes.
	 *  @deprecated Should replace with the <code>text()</code> method of this class. The two methods are
	 *              identical, but text2() is retained for backward naming compatibility only.
	 *  @param textToDisplay Text to be displayed.
	 *  @param xPos x-position of the the text to display in original unzoomed screen coordinates.
	 *  @param yPos y-position of the the text to display in original unzoomed screen coordinates.
	 */  
	public void text2(String textToDisplay, float xPos, float yPos)
	{
		// Call the static version providing the applet context that was given to the constructor.
		text(aContext,textToDisplay, xPos,yPos);
	}
	
	/** Replacement for Processing's <code>text()</code> method for faster and more accurate placement of 
	 *  characters in Java2D mode when a zoomed font is to be displayed. This method is not necessary when
	 *  text is not subject to scaling via zooming, nor is is necessary in <code>P2D</code>, <code>P3D</code>
	 *  or <code>OpenGL</code> modes.
	 *  @param textToDisplay Text to be displayed.
	 *  @param xPos x-position of the the text to display in original unzoomed screen coordinates.
	 *  @param yPos y-position of the the text to display in original unzoomed screen coordinates.
	 */  
	public void text(String textToDisplay, float xPos, float yPos)
	{
		// Call the static version providing the applet context that was given to the constructor.
		text(aContext,textToDisplay, xPos,yPos);
	}

	/** Replacement for Processing's <code>text()</code> method for faster and more accurate placement of 
	 *  characters in Java2D mode when a zoomed font is to be displayed. This version does not require a
	 *  ZoomPan object to be instantiated but does need the <code>PApplet</code> context to be provided.
	 *  As with the other <code>text2()</code> method it is not necessary to call this method if the
	 *  text is not subject to scaling via zooming, nor is is necessary in <code>P2D</code>, <code>P3D</code>
	 *  or <code>OpenGL</code> modes.
	 *  @deprecated Should replace with the <code>text()</code> method of this class. The two methods are
	 *              identical, but text2() is retained for backward naming compatibility only.
	 *  @param applet Sketch in which text is to be drawn.
	 *  @param textToDisplay Text to be displayed.
	 *  @param xPos x-position of the the text to display in original unzoomed screen coordinates.
	 *  @param yPos y-position of the the text to display in original unzoomed screen coordinates.
	 */  
	public static void text2(PApplet applet, String textToDisplay, float xPos, float yPos)
	{
		text(applet,textToDisplay,xPos,yPos);
	}
	
	/** Replacement for Processing's <code>text()</code> method for faster and more accurate placement of 
	 *  characters in Java2D mode when a zoomed font is to be displayed. This version does not require a
	 *  ZoomPan object to be instantiated but does need the <code>PApplet</code> context to be provided.
	 *  As with the other <code>text2()</code> method it is not necessary to call this method if the
	 *  text is not subject to scaling via zooming, nor is is necessary in <code>P2D</code>, <code>P3D</code>
	 *  or <code>OpenGL</code> modes.
	 *  @param applet Sketch in which text is to be drawn.
	 *  @param textToDisplay Text to be displayed.
	 *  @param xPos x-position of the the text to display in original unzoomed screen coordinates.
	 *  @param yPos y-position of the the text to display in original unzoomed screen coordinates.
	 */  
	public static void text(PApplet applet, String textToDisplay, float xPos, float yPos)
	{
		// If we are not using the default renderer, use text() as normal.
		if (!(applet.g.getClass().getName().equals(PConstants.JAVA2D)))
		{
			applet.text(textToDisplay, xPos, yPos);
			return;
		}

		// Store the original transformation and text size.
		applet.pushMatrix();
		float origTextSize = applet.g.textSize;

		// Find the current x-scaling from the transformation matrix.
		float xScale = applet.g.getMatrix().get(null)[0];

		// Find the location of the text position subject to the current transformation.
		PVector newTextPos = new PVector(0,0);
		applet.g.getMatrix().mult(new PVector(xPos,yPos),newTextPos);

		// Temporarily reset the transformation matrix while we place the text.
		applet.resetMatrix();

		// Size and place the text.
		applet.textSize(xScale*origTextSize);
		applet.text(textToDisplay,newTextPos.x,newTextPos.y);

		// Restore the original transformation and text size.
		applet.textSize(origTextSize);
		applet.popMatrix();
	}

	// ----------------------------- Private and package methods -----------------------------

	/** Supplies the graphics - either that supplied in the constructor or that from aContext
	 *  (the reason for this method is that the latter may change).
	 */
	private PGraphics getGraphics()
	{
		if (graphics==null)
		{
			return aContext.g;
		}

		return graphics;
	}

	/** Zooms in or out depending on the current values of zoomStartPosition and zoomScale.
	 */
	void doZoom()
	{
		// Find coordinate-space location of first mouse click.
		PVector pCoord = getDispToCoord(new PVector(zoomStartPosition.x,zoomStartPosition.y));

		// Do the zooming transformation.   
		calcTransformation();

		// Find new pixel location of original mouse click location.
		PVector newZoomStartPosition = getCoordToDisp(pCoord);

		// Translate by change in click position.
		//panOffset.setLocation(panOffset.x + zoomStartPosition.x - newZoomStartPosition.x,
		//                    panOffset.y + zoomStartPosition.y - newZoomStartPosition.y);
		panOffset.add(new PVector(zoomStartPosition.x-newZoomStartPosition.x,zoomStartPosition.y-newZoomStartPosition.y));

		// Finish off transformation by incorporating shifted click position.
		calcTransformation();
	}

	/** Reports the mouse mask being used.
	 *  @return Mouse mask being used to identify zoom/pan control.
	 */
	int getMouseMask()
	{
		// This method is of package-wide scope to allow inner classes to have access to it.
		return mouseMask;
	}

	/** Reports the zoom step being used.
	 *  @return The amount of zooming that occurs when display zoomed by 1 unit.
	 */
	double getZoomStep()
	{
		// This method is of package-wide scope to allow inner classes to have access to it.
		return zoomStep;
	}

	/** Sets the new zoom-scaling programmatically. Unlike the public method setZoomScale()
	 *  this version is for internal use where recalculation of transformations is handled
	 *  elsewhere.
	 *  @param zoomScale New zoom scale to be used.
	 */
	void setZoomScaleWithoutRecalculation(double zoomScale)
	{
		// This method is of package-wide scope to allow inner classes to have access to it.
		this.zoomScale = zoomScale;
	}

	/** Programmatically sets the start position of a zooming activity. Normally, while the mouse
	 *  is held down on a given point, all zooming is relative to this position. This gets reset
	 *  whenever a new point is selected with the mouse. This method allows that position to be
	 *  set programmatically, for example for use with a mouse wheel zooming without a mouse press. 
	 *  @param zoomStartPosition Position in screen coordinates of the start of a zoom activity.
	 */
	void setZoomStartPosition(PVector zoomStartPosition)
	{
		this.zoomStartPosition = zoomStartPosition;
	}

	/** Finds the affine transformations that convert between original and display coordinates. 
	 *  Updates both the forward transformation (for display) and inverse transformation (for 
	 *  decoding of mouse locations. 
	 */
	private void calcTransformation()
	{    
		trans = new AffineTransform();
		iTrans = new AffineTransform();

		float centreX = (float)(getGraphics().width*(1-zoomScale))/2;
		float centreY = (float)(getGraphics().height*(1-zoomScale))/2;

		trans.translate(centreX+panOffset.x,centreY+panOffset.y);
		trans.scale(zoomScale,zoomScale);

		iTrans.scale(1/zoomScale,1/zoomScale);
		iTrans.translate(-centreX-panOffset.x, -centreY-panOffset.y);
	}

	// ------------------------------ Nested classes -----------------------------

	/** Class to handle mouse wheel events. 
	 */
	private class MouseWheelMonitor implements MouseWheelListener
	{
		protected MouseWheelMonitor()
		{
			// Empty constructor required so it can be instantiated by the containing 
			// class without having to create a synthetic accessor method.
		}

		/** Responds to a mouse wheel change event by zooming in or out.
		 *  @param e Mouse wheel event. 
		 */
		public void mouseWheelMoved(MouseWheelEvent e)
		{     
			// Test to see if mouse mask is specified and it is pressed.
			if ((e.getModifiersEx() & getMouseMask()) != getMouseMask())
			{
				return;
			}

			setZoomStartPosition(new PVector(e.getX(),e.getY()));

			if (e.getWheelRotation() < 0)
			{
				setZoomScaleWithoutRecalculation(getZoomScale()*getZoomStep());
				doZoom();                    
			}
			else if (e.getWheelRotation() > 0)
			{
				setZoomScaleWithoutRecalculation(getZoomScale()/getZoomStep());
				doZoom();                    
			}   
		}
	}
}