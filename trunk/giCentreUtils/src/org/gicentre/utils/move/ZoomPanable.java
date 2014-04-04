package org.gicentre.utils.move;

import org.gicentre.utils.move.ZoomPan.ZoomPanBehaviour;
import org.gicentre.utils.move.ZoomPan.ZoomPanDirection;

import processing.core.PGraphics;
import processing.core.PVector;

//  *****************************************************************************************
/** Interface for describing the behaviour of a zoomable component.
 *  @author Jo Wood and Aidan Slingsby, giCentre, City University London.
 *  @version 3.3, 1st August 2012. 
 */ 
//  *****************************************************************************************

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

interface ZoomPanable 
{
	/** Should perform the zooming/panning transformation. This method should be called in the
	 *  draw() method before any drawing that is to be zoomed or panned. 
	 */
	abstract void transform();
	
	/** Should perform the zooming/panning transformation in the given graphics context. This version of transform()
	 *  should be used for transforming off-screen buffers that were not provided to the constructor. Can
	 *  be useful when a sketch temporarily creates an off-screen buffer that needs to be zoomed and panned
	 *  in the same way as the main PApplet.
	 */
	abstract void transform(PGraphics offScreenBuffer);
	
	/** Should reset the display to unzoomed and unpanned position.
	 */
	abstract void reset();

	/** Should add a listener to be informed when some zooming or panning has finished.
	 *  @param zoomPanListener Listener to be informed when some zooming or panning has finished.
	 */
	abstract void addZoomPanListener(ZoomPanListener zoomPanListener);

	/** Should remove the given listener from those to be informed when zooming/panning has finished.
	 *  @param zoomPanListener Listener to remove.
	 *  @return True if listener found and removed.
	 */
	abstract boolean removeZoomPanListener(ZoomPanListener zoomPanListener);

	/** Should set the key that must be pressed before mouse actions are active. By default, no key
	 *  is needed for the mouse to be active. Specifying a value allows normal mouse actions to
	 *  be intercepted without zooming or panning. To set the mouse mask to no key, specify a 
	 *  mouseMask value of 0. Mouse actions can be disabled entirely by setting the mouseMask
	 *  to a negative value.
	 *  @param mouseMask Keyboard modifier required to activate mouse actions. Valid values are
	 *  <code>CONTROL</code>, <code>SHIFT</code>, <code>ALT</code>, <code>0</code> and <code>-1</code>. 
	 */
	abstract void setMouseMask(int mouseMask);

	/** Should report the current mouse position in coordinate space. This method should be used
	 *  in preference to <code>mouseX </code>and <code>mouseY</code> if the current display 
	 *  has been zoomed or panned.
	 *  @return Coordinates of current mouse position accounting for any zooming or panning.
	 */
	abstract PVector getMouseCoord();

	/** Should report the current zoom scale. Can be used for drawing objects that maintain their
	 *  size when zooming.
	 *  @return Current zoom scale. 
	 */
	abstract double getZoomScale();
	
	/** Should set a new zoom scale. Can be used for programmatic control of zoomer, such as
	 *  eased interpolated zooming.
	 *  @param zoomScale New zoom scale. A value of 1 indicates no zooming, values above
	 *         0 and below 1 will shrink the display; values above 1 will enlarge the 
	 *         display. Values less than or equal to 0 will be ignored. 
	 */
	abstract void setZoomScale(double zoomScale);

	
	/** Should report the current zoom scale in X. Can be used for drawing objects that maintain their
	 *  size when zooming.
	 *  @return Current zoom scale. 
	 */
	abstract double getZoomScaleX();
	
	/** Should set a new zoom scale in X. Can be used for programmatic control of zoomer, such as
	 *  eased interpolated zooming.
	 *  @param zoomScale New zoom scale. A value of 1 indicates no zooming, values above
	 *         0 and below 1 will shrink the display; values above 1 will enlarge the 
	 *         display. Values less than or equal to 0 will be ignored. 
	 */
	abstract void setZoomScaleX(double zoomScaleX);


	/** Should report the current zoom scale in Y. Can be used for drawing objects that maintain their
	 *  size when zooming.
	 *  @return Current zoom scale. 
	 */
	abstract double getZoomScaleY();
	
	/** Should set a new zoom scale in Y. Can be used for programmatic control of zoomer, such as
	 *  eased interpolated zooming.
	 *  @param zoomScale New zoom scale. A value of 1 indicates no zooming, values above
	 *         0 and below 1 will shrink the display; values above 1 will enlarge the 
	 *         display. Values less than or equal to 0 will be ignored. 
	 */
	abstract void setZoomScaleY(double zoomScaleY);

	
	/** Should set the zoom/pan behaviour type
	 *  @param zoomPanType  BOTH_DIRECTIONS=normal; VERTICAL_ONLY=only in y; HORIZONTAL_ONLY=only in x
	 */
	@Deprecated
	abstract void setZoomPanBehaviour(ZoomPanBehaviour zoomPanType);

	/** Should report the zoom/pan behaviour type.
	 *  @return  BOTH_DIRECTIONS=normal; VERTICAL_ONLY=only in y; HORIZONTAL_ONLY=only in x
	 */
	@Deprecated
	abstract ZoomPanBehaviour getZoomPanBehaviour();

	
	/** Should set the zooming/panning direction
	 *  @param zoomPanDirection
	 */
	abstract void setZoomPanDirection(ZoomPanDirection zoomPanDirection);

	/** Should report the zoom/pan zooming/panning direction
	 *  @return  zoomPanDirection
	 */
	abstract ZoomPanDirection getZoomPanDirection();

	
	/** Should report the current pan offset. Useful when wishing to use an interpolated panning
	 *  between this current value and some new pan offset.
	 *  @return Current pan offset. Negative coordinates indicate an offset to the left
	 *          or upwards, positive values to the right or downward.       
	 */
	abstract PVector getPanOffset();
	
	/** Should set a new pan offset. Can be used for programmatic control of panning, such as
	 *  eased interpolated zooming and panning.
	 *  @param panX X coordinate of new pan offset. A value of 0 indicates no translation
	 *         of the display on the horizontal axis; a negative value indicates a 
	 *         translation to the left; a positive value indicates translation to the right.
	 *  @param panY Y coordinate of new pan offset. A value of 0 indicates no translation
	 *         of the display on the vertical axis; a negative value indicates a translation
	 *         upwards; a positive value indicates translation downwards.
	 *         
	 */
	abstract void setPanOffset(float panX, float panY);
	
	/** Should report whether display is currently being zoomed (i.e. mouse is being dragged with 
	 *  zoom key/button pressed).
	 *  @return True if display is being actively zoomed. 
	 */
	abstract boolean isZooming();

	/** Should report whether display is currently being panned (ie mouse is being dragged with
	 *  pan key/button pressed).
	 *  @return True if display is being actively panned. 
	 */
	abstract boolean isPanning();

	/** Should report whether a mouse event has been captured by the zoomer. This allows zoom and 
	 *  pan events to be separated from other mouse actions. Usually only useful if the zoomer
	 *  uses some mouse mask.
	 *  @return True if mouse event has been captured by the zoomer. 
	 */
	abstract boolean isMouseCaptured();
	
	/** Should determine whether or not zooming via a button press is permitted. By default zooming is 
	 *  enabled with the appropriate mouse button, but can be disabled by setting allowZoom to false.
	 *  Note that the scroll wheel will zoom whether or not the zoom button is activated.
	 *  @param allowZoom Zooming permitted via mouse button press if true.
	 */
	abstract void allowZoomButton(boolean allowZoom);
	
	/** Should determine whether or not panning is permitted via a button press. By default panning is
	 *  enabled with the appropriate mouse button, but can be disabled by setting allowPan to false.
	 *  @param allowPan Panning permitted via mouse button press if true.
	 */
	abstract void allowPanButton(boolean allowPan);

	/** Should set the minimum permitted zoom scale (i.e. how far zoomed out a view is allowed to be). If the
	 *  current zoom level is smaller than the new minimum, the zoom scale will be set to the new 
	 *  minimum value. A value above zero but less than one means that the view will be smaller than
	 *  its natural size. A value greater than one means the view will be larger than its natural size.
	 *  @param minZoomScale
	 */
	abstract void setMinZoomScale(double minZoomScale);
	
	/** Should set the maximum permitted zoom scale (i.e. how far zoomed in a view is allowed to be). If the
	 *  current zoom level is larger than the new maximum, the zoom scale will be set to the new 
	 *  maximum value. A value above zero but less than one means that the view will be smaller than
	 *  its natural size. A value greater than one means the view will be larger than its natural size.
	 *  @param maxZoomScale
	 */
	abstract void setMaxZoomScale(double maxZoomScale);
	
	/** Should transform the given point from display to coordinate space. Display space is that which
	 *  has been subjected to zooming and panning. Coordinate space is the original space into 
	 *  which objects have been placed before zooming and panning. For most drawing operations you
	 *  should not need to use this method. It is available for those operations that do not draw
	 *  directly, but need to know the transformation between coordinate and screen space.
	 *  @param p 2D point in zoomed display space.
	 *  @return Location of point in original coordinate space. 
	 */
	abstract PVector getDispToCoord(PVector p);
	
	/** Should transform the given point from coordinate to display space. Display space is that which
	 *  has been subjected to zooming and panning. Coordinate space is the original space into 
	 *  which objects have been placed before zooming and panning. For most drawing operations you
	 *  should not need to use this method. It is available for those operations that do not draw
	 *  directly, but need to know the transformation between coordinate and screen space.
	 *  @param p 2D point in original coordinate space.
	 *  @return Location of point in zoomed display space. 
	 */
	abstract PVector getCoordToDisp(PVector p);

	/** Should set mouse button for zooming. If this is set to either LEFT or RIGHT, the other button (RIGHT or LEFT)
	 *  will be set for panning.
	 *  @param zoomMouseButton Zoom mouse button (must be either PConstants.LEFT or PConstants.RIGHT
	 */
	abstract void setZoomMouseButton(int zoomMouseButton);

	/** Should provide a replacement for Processing's <code>text()</code> method for faster and more accurate placement
	 *  of characters. This can be useful for rendering modes that do not scale text well when under large zooming.
	 *  @param textToDisplay Text to be displayed.
	 *  @param xPos x-position of the the text to display in original unzoomed screen coordinates.
	 *  @param yPos y-position of the the text to display in original unzoomed screen coordinates.
	 */  
	abstract void text(String textToDisplay, float xPos, float yPos);
	
	/** Should provide a copy (cloned snapshot) of the current ZoomPanState that this will not change its state.
	 *  @return Copy of the current zoomPanState.
	 */
	abstract ZoomPanState getZoomPanState();
}
