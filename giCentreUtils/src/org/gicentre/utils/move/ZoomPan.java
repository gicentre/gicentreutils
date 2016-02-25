package org.gicentre.utils.move;

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
 *  @author Jo Wood and Aidan Slingsby, giCentre, City University London.
 *  @version 3.4.1, 25th February 2016. 
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
	// ----------------------- Object and class variables -------------------------
	
	private ZoomPanable zoomer;
	
	/** Enumerates zooming and panning constraints.
	 */
	public enum ZoomPanDirection
	{
		/** Zooming and panning in both directions. The default.*/ 
		ZOOM_PAN_BOTH,
		/** Only zoom and pan in a vertical direction (y).*/
		ZOOM_PAN_VERTICAL,  
		/** Only zoom and pan in a horizontal  direction (x).*/
		ZOOM_PAN_HORIZONTAL,   
		/** Zoom in vertical direction only (y), pan in both. */
		ZOOM_VERTICAL_PAN_BOTH,
		/** Zoom in horizontal direction only (x), pan in both. */
		ZOOM_HORIZONTAL_PAN_BOTH,   
	}
	
	// ------------------------------- Constructors ------------------------------- 

	protected ZoomPan()
	{
		// Do nothing as this version of the constructor is only ever called by subclasses.
	}
	
	/** Initialises the zooming and panning transformations for the given applet context. 
	 *  Can be used to have independent zooming in multiple windows by creating multiple
	 *  objects each with a different PApplet object.
	 *  @param aContext Applet context in which zooming and panning are to take place. 
	 */
	public ZoomPan(PApplet aContext)
	{
		// From v.3.4 onwards, ZoomPan relies on Processing 3 or greater.
		try
		{
			// Attempt Processing V3.xx first.
			zoomer = new ZoomPan30(aContext);
		}
		catch (Throwable e3)
		{
			System.err.println("This version of giCentreUtils works only with Processing 3 or greater. For older versions of Processing, see http://www.gicentre.net/software/#/utils");
		}
	}

	/** Initialises the zooming and panning transformations for the given applet and graphics contexts. 
	 *  This version of the constructor allows a graphics context separate from the applet to be applied
	 *  so that buffered off-screen drawing can be applied. 
	 *  @param aContext Applet context in which zooming and panning are to take place. 
	 *  @param graphics Graphics context in which to draw.
	 */
	public ZoomPan(PApplet aContext, PGraphics graphics)
	{
		// From v.3.4 onwards, ZoomPan relies on Processing 3 or greater.
		try
		{
			zoomer = new ZoomPan30(aContext,graphics);
		}
		catch (Throwable e3)
		{
			System.err.println("This version of giCentreUtils works only with Processing 3 or greater. For older versions of Processing, see http://www.gicentre.net/software/#/utils");
		}
	}

	// ------------------------------ Public methods -----------------------------

	/** Performs the zooming/panning transformation. This method should be called in the
	 *  draw() method before any drawing that is to be zoomed or panned. 
	 */
	public void transform()
	{    
		zoomer.transform();
	}
	
	/** Performs the zooming/panning transformation in the given graphics context. This version of transform()
	 *  can be used for transforming off-screen buffers that were not provided to the constructor. Can
	 *  be useful when a sketch temporarily creates an off-screen buffer that needs to be zoomed and panned
	 *  in the same way as the main PApplet.
	 *  @param offScreenBuffer Graphics context in which to apply the zoom/pan transformation.
	 */
	public void transform(PGraphics offScreenBuffer)
	{    
		zoomer.transform(offScreenBuffer);
	}
	
	/** Resets the display to unzoomed and unpanned position.
	 */
	public void reset()
	{
		zoomer.reset();
	}

	/** Adds a listener to be informed when some zooming or panning has finished.
	 *  @param zoomPanListener Listener to be informed when some zooming or panning has finished.
	 */
	public void addZoomPanListener(ZoomPanListener zoomPanListener)
	{
		zoomer.addZoomPanListener(zoomPanListener);
	}

	/** Removes the given listener from those to be informed when zooming/panning has finished.
	 *  @param zoomPanListener Listener to remove.
	 *  @return True if listener found and removed.
	 */
	public boolean removeZoomPanListener(ZoomPanListener zoomPanListener)
	{
		return zoomer.removeZoomPanListener(zoomPanListener);
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
		zoomer.setMouseMask(mouseMask);
	}

	/** Reports the current mouse position in coordinate space. This method should be used
	 *  in preference to <code>mouseX </code>and <code>mouseY</code> if the current display 
	 *  has been zoomed or panned.
	 *  @return Coordinates of current mouse position accounting for any zooming or panning.
	 */
	public PVector getMouseCoord()
	{
		return zoomer.getMouseCoord();
	}

	/** Reports the current zoom scale. Can be used for drawing objects that maintain their
	 *  size when zooming.
	 *  @return Current zoom scale. 
	 */
	public double getZoomScale()
	{
		return zoomer.getZoomScale();
	}

	/** Reports the current zoom scale in X. Can be used for drawing objects that maintain their
	 *  size when zooming.
	 *  @return Current zoom scale. 
	 */
	public double getZoomScaleX()
	{
		return zoomer.getZoomScaleX();
	}

	/** Reports the current zoom scale in Y. Can be used for drawing objects that maintain their
	 *  size when zooming.
	 *  @return Current zoom scale. 
	 */
	public double getZoomScaleY()
	{
		return zoomer.getZoomScaleY();
	}

	
	/** Sets a new zoom scale. Can be used for programmatic control of zoomer, such as
	 *  eased interpolated zooming.
	 *  @param zoomScale New zoom scale. A value of 1 indicates no zooming, values above
	 *         0 and below 1 will shrink the display; values above 1 will enlarge the 
	 *         display. Values less than or equal to 0 will be ignored. 
	 */
	public void setZoomScale(double zoomScale)
	{
		zoomer.setZoomScale(zoomScale);
	}

	/** Sets a new zoom scale in X. Can be used for programmatic control of zoomer, such as
	 *  eased interpolated zooming.
	 *  @param zoomScaleX New horizontal zoom scale. A value of 1 indicates no zooming, values above
	 *         0 and below 1 will shrink the display; values above 1 will enlarge the 
	 *         display. Values less than or equal to 0 will be ignored. 
	 */
	public void setZoomScaleX(double zoomScaleX)
	{
		zoomer.setZoomScaleX(zoomScaleX);
	}

	
	/** Sets a new zoom scale in Y. Can be used for programmatic control of zoomer, such as
	 *  eased interpolated zooming.
	 *  @param zoomScaleY New vertical zoom scale. A value of 1 indicates no zooming, values above
	 *         0 and below 1 will shrink the display; values above 1 will enlarge the 
	 *         display. Values less than or equal to 0 will be ignored. 
	 */
	public void setZoomScaleY(double zoomScaleY)
	{
		zoomer.setZoomScaleY(zoomScaleY);
	}
	
	
	/** Sets the zooming/panning direction
	 *  @param zoomPanDirection Type of zoom panning. Should be one of<br>
	 *  <code>ZOOM_PAN_BOTH</code>              Zooming and panning in both directions. The default.<br> 
	 *	<code>ZOOM_PAN_VERTICAL</code>          Only zoom and pan in a vertical direction (y). <br>
	 *  <code>ZOOM_PAN_HORIZONTAL</code>        Only zoom and pan in a horizontal  direction (x).<br>
	 *  <code>ZOOM_VERTICAL_PAN_BOTH</code>     Zoom in vertical direction only (y), pan in both.<br>
	 *  <code>ZOOM_HORIZONTAL_PAN_BOTH</code>   Zoom in vertical direction only (x), pan in both.
	 */
	public void setZoomPanDirection(ZoomPanDirection zoomPanDirection)
	{
		zoomer.setZoomPanDirection(zoomPanDirection);
	}

	/** Reports the zooming/panning direction
	 *  @return Direction options of controllable zooming and panning.
	 */
	public ZoomPanDirection getZoomPanDirection()
	{
		return zoomer.getZoomPanDirection();
	}

	
	/** Reports the current pan offset. Useful when wishing to use an interpolated panning
	 *  between this current value and some new pan offset.
	 *  @return Current pan offset. Negative coordinates indicate an offset to the left
	 *          or upwards, positive values to the right or downward.       
	 */
	public PVector getPanOffset()
	{
		return zoomer.getPanOffset();
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
		zoomer.setPanOffset(panX, panY);
	}

	/** Reports whether display is currently being zoomed (i.e. mouse is being dragged with 
	 *  zoom key/button pressed).
	 *  @return True if display is being actively zoomed. 
	 */
	public boolean isZooming()
	{
		return zoomer.isZooming();
	}

	/** Reports whether display is currently being panned (ie mouse is being dragged with
	 *  pan key/button pressed).
	 *  @return True if display is being actively panned. 
	 */
	public boolean isPanning()
	{
		return zoomer.isPanning();
	}

	/** Reports whether a mouse event has been captured by the zoomer. This allows zoom and 
	 *  pan events to be separated from other mouse actions. Usually only useful if the zoomer
	 *  uses some mouse mask.
	 *  @return True if mouse event has been captured by the zoomer. 
	 */
	public boolean isMouseCaptured()
	{
		return zoomer.isMouseCaptured();
	}
	
	/** Determines whether or not zooming via a button press is permitted. By default zooming is 
	 *  enabled with the appropriate mouse button, but can be disabled by setting allowZoom to false.
	 *  Note that the scroll wheel will zoom whether or not the zoom button is activated.
	 *  @param allowZoom Zooming permitted via mouse button press if true.
	 */
	public void allowZoomButton(boolean allowZoom)
	{
		zoomer.allowZoomButton(allowZoom);
	}
	
	/** Determines whether or not panning is permitted via a button press. By default panning is
	 *  enabled with the appropriate mouse button, but can be disabled by setting allowPan to false.
	 *  @param allowPan Panning permitted via mouse button press if true.
	 */
	public void allowPanButton(boolean allowPan)
	{
		zoomer.allowPanButton(allowPan);
	}
	
	/** Sets the minimum permitted zoom scale (i.e. how far zoomed out a view is allowed to be). If the
	 *  current zoom level is smaller than the new minimum, the zoom scale will be set to the new 
	 *  minimum value. A value above zero but less than one means that the view will be smaller than
	 *  its natural size. A value greater than one means the view will be larger than its natural size.
	 *  @param minZoomScale Minimum permitted zoom scale.
	 */
	public void setMinZoomScale(double minZoomScale)
	{
		zoomer.setMinZoomScale(minZoomScale);
	}
	
	/** Sets the maximum permitted zoom scale (i.e. how far zoomed in a view is allowed to be). If the
	 *  current zoom level is larger than the new maximum, the zoom scale will be set to the new 
	 *  maximum value. A value above zero but less than one means that the view will be smaller than
	 *  its natural size. A value greater than one means the view will be larger than its natural size.
	 *  @param maxZoomScale Maximum permitted zoom scale.
	 */
	public void setMaxZoomScale(double maxZoomScale)
	{
		zoomer.setMaxZoomScale(maxZoomScale);
	}
	
	/** Sets the maximum permitted panning offsets. The coordinates provided should be the unzoomed ones.
	 *  So to prevent panning past the 'edge' of the unzoomed display, values would be set to 0. Setting
	 *  values of (10,40) would allow the display to be panned 10 unzoomed pixels to the left or right
	 *  of the unzoomed display area and 40 pixels up or down.
	 *  @param maxX Maximum number of unzoomed pixels by which the display can be panned in the x-direction.
	 *  @param maxY Maximum number of unzoomed pixels by which the display can be panned in the y-direction.
	 */
	public void setMaxPanOffset(float maxX, float maxY)
	{
		zoomer.setMaxPanOffset(maxX, maxY);
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
		return zoomer.getDispToCoord(p);
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
		return zoomer.getCoordToDisp(p);
	}

	/** Sets mouse button for zooming. If this is set to either LEFT or RIGHT, the other button (RIGHT or LEFT)
	 *  will be set for panning.
	 *  @param zoomMouseButton Zoom mouse button (must be either PConstants.LEFT or PConstants.RIGHT
	 */
	public void setZoomMouseButton(int zoomMouseButton)
	{
		zoomer.setZoomMouseButton(zoomMouseButton);
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
		zoomer.text(textToDisplay, xPos, yPos);
	}
	
	/** Replacement for Processing's <code>text()</code> method for faster and more accurate placement of 
	 *  characters in Java2D mode when a zoomed font is to be displayed. This version does not require a
	 *  ZoomPan object to be instantiated but does need the <code>PApplet</code> context to be provided.
	 *  As with the other <code>text()</code> method it is not necessary to call this method if the
	 *  text is not subject to scaling via zooming, nor is is necessary in <code>P2D</code>, <code>P3D</code>
	 *  or <code>OpenGL</code> modes.
	 *  @param applet Sketch in which text is to be drawn.
	 *  @param textToDisplay Text to be displayed.
	 *  @param xPos x-position of the the text to display in original unzoomed screen coordinates.
	 *  @param yPos y-position of the the text to display in original unzoomed screen coordinates.
	 */  
	public static void text(PApplet applet, String textToDisplay, double xPos, double yPos)
	{
		// If we are not using the default renderer, use text() as normal.
		if (!(applet.g.getClass().getName().equals(PConstants.JAVA2D)))
		{
			applet.text(textToDisplay, (float)xPos, (float)yPos);
			return;
		}

		// Store the original transformation and text size.
		applet.pushMatrix();
		float origTextSize = applet.g.textSize;

		// Find the current x-scaling from the transformation matrix.
		float xScale = applet.g.getMatrix().get(null)[0];

		// Find the location of the text position subject to the current transformation.
		PVector newTextPos = new PVector(0,0);
		applet.g.getMatrix().mult(new PVector((float)xPos,(float)yPos),newTextPos);

		// Temporarily reset the transformation matrix while we place the text.
		applet.resetMatrix();

		// Size and place the text.
		applet.textSize(xScale*origTextSize);
		applet.text(textToDisplay,newTextPos.x,newTextPos.y);

		// Restore the original transformation and text size.
		applet.textSize(origTextSize);
		applet.popMatrix();
	}
	
	/** Provides a copy (cloned snapshot) of the current ZoomPanState.
	 *  You can assume that this will not change its state.
	 *  @return Copy of the current zoomPanState.
	 */
	public ZoomPanState getZoomPanState()
	{
		return zoomer.getZoomPanState();
	}
}