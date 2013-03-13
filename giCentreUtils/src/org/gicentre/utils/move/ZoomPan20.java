package org.gicentre.utils.move;

import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.gicentre.utils.move.ZoomPanListener;
import org.gicentre.utils.move.ZoomPan.ZoomPanBehaviour;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.event.MouseEvent;

// *****************************************************************************************
/** Class to allow interactive zooming and panning of the Processing display. This is the 
 *  Processing 2.x implementation that uses Processing 2's event handling model. This should
 *  not be created directly, but instead it will be created at runtime by the <code>ZoomPan</code>
 *  class if it detects Processing 2.x core libraries. Despite this, the class has to remain
 *  public so that it can be registered by Processing's event handling model.
 *  @author Jo Wood and Aidan Slingsby, giCentre, City University London.
 *  @version 3.3, 13th March, 2013. 
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
public class ZoomPan20 implements ZoomPanable
{
	// ---------------------------- Object variables -----------------------------

	private ZoomPanState zoomPanState;                // zoomScale and panOffset is now stored in an instance of zoomPanState
	                                                  // All reporting of the zoom/offset and coordinate transformation in handled by this object
	private PVector zoomStartPosition,oldPosition;
	private double zoomStep;
	private boolean isZooming, isPanning,isMouseCaptured;
	private boolean allowZoomButton, allowPanButton;
	private int mouseMask = 0;
	private Vector<ZoomPanListener> listeners;
	private int zoomMouseButton=PConstants.LEFT; 	  // Implies pan is the other button
	
	private Rectangle mouseBoundsMask=null; 		  // Zoom/pan bounding box (in screen space) mask for mouse controlled zooming/panning.
	private Long timeAtLastWheelZoom=null;			  // The time at which the mouse wheel was last used - null if it hasn't been used since the last zoom event
	private Timer timer; 							  // Timer so that we have a delay before a zoom event is triggered with the timer
	private int millisecondsBeforeWheelZoomEvent=700; // Milliseconds before a zoom event is triggered by the mouse wheel - set to 0.7 of a second by default
	
	double minZoomScale=Double.MIN_VALUE;
	double maxZoomScale=Double.MAX_VALUE;
	
	private ZoomPanBehaviour zoomPanType=ZoomPanBehaviour.BOTH_DIRECTIONS; //By default, zooming/panning is in x and y
	
	/* TODO: Panning constraints not yet implemented.
	private float maxPanXOffset = -1;				// Absolute maximum permitted panning offset in x direction, or negative if no maximum.
	private float maxPanYOffset = -1;				// Absolute maximum permitted panning offset in y direction, or negative if no maximum.
	*/
	
	// ------------------------------- Constructor ------------------------------- 

	/** Initialises the zooming and panning transformations for the given applet context. 
	 *  Can be used to have independent zooming in multiple windows by creating multiple
	 *  objects each with a different PApplet object.
	 *  @param aContext Applet context in which zooming and panning are to take place. 
	 */
	ZoomPan20(PApplet aContext)
	{
		zoomPanState=new ZoomPanState(aContext,null);
		if (aContext == null)
		{
			System.err.println("Warning: No applet context provided for ZoomPan.");
			return;
		}
		allowZoomButton = true;
		allowPanButton = true;
		listeners = new Vector<ZoomPanListener>();
		reset();
		timer=new Timer();
		
		aContext.registerMethod("mouseEvent", this);
		aContext.addMouseWheelListener(new MouseWheelMonitor());
	}

	/** Initialises the zooming and panning transformations for the given applet and graphics contexts. 
	 *  This version of the constructor allows a graphics context separate from the applet to be applied
	 *  so that buffered off-screen drawing can be applied. 
	 *  @param aContext Applet context in which zooming and panning are to take place. 
	 *  @param graphics Graphics context in which to draw.
	 */
	ZoomPan20(PApplet aContext, PGraphics graphics)
	{
		zoomPanState=new ZoomPanState(aContext,graphics);

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
		timer=new Timer();
		
		// Old PRE 2.0 code: aContext.registerMouseEvent(this);
		aContext.registerMethod("mouseEvent", this);
				
		aContext.addMouseWheelListener(new MouseWheelMonitor());
	}

	// ------------------------------ Public methods -----------------------------

	/** Performs the zooming/panning transformation. This method should be called in the
	 *  draw() method before any drawing that is to be zoomed or panned. 
	 */
	public void transform()
	{    
		zoomPanState.transform();
	}
	
	/** Performs the zooming/panning transformation in the given graphics context. This version of transform()
	 *  can be used for transforming off-screen buffers that were not provided to the constructor. Can
	 *  be useful when a sketch temporarily creates an off-screen buffer that needs to be zoomed and panned
	 *  in the same way as the main PApplet.
	 */
	public void transform(PGraphics offScreenBuffer)
	{    
		zoomPanState.transform(offScreenBuffer);
	}
	
	/** Resets the display to unzoomed and unpanned position.
	 */
	public void reset()
	{
		zoomPanState.trans		= new AffineTransform();
		zoomPanState.iTrans		= new AffineTransform();
		zoomPanState.zoomScale	= 1;
		zoomPanState.panOffset	= new PVector(0,0);
		zoomStep    	    	= 1.05;
		isZooming       		= false;
		isPanning       		= false;
		isMouseCaptured 		= false;
		
		//inform listeners that zooming/panning has ended
		for (ZoomPanListener zoomPanListener:listeners){
			zoomPanListener.panEnded();
			zoomPanListener.zoomEnded();
		}
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
		/* Pre 2.0 code:
			case PConstants.CONTROL:
				this.mouseMask = InputEvent.CTRL_DOWN_MASK;
				break;
	
			case PConstants.SHIFT:
				this.mouseMask = InputEvent.SHIFT_DOWN_MASK;
				break;
	
			case PConstants.ALT:
				this.mouseMask = InputEvent.ALT_DOWN_MASK;
				break;
		*/
	
			case PConstants.CONTROL:
				this.mouseMask = PConstants.CONTROL;
				break;
	
			case PConstants.SHIFT:
				this.mouseMask = PConstants.SHIFT;
				break;
	
			case PConstants.ALT:
				this.mouseMask = PConstants.ALT;
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
		return getDispToCoord(new PVector(zoomPanState.aContext.mouseX,zoomPanState.aContext.mouseY));
	}

	/** Reports the current zoom scale. Can be used for drawing objects that maintain their
	 *  size when zooming.
	 *  @return Current zoom scale. 
	 */
	public double getZoomScale()
	{
		return zoomPanState.getZoomScale();
	}

	/** Sets a new zoom scale. Can be used for programmatic control of zoomer, such as
	 *  eased interpolated zooming.
	 *  @param zoomScale New zoom scale. A value of 1 indicates no zooming, values above
	 *         0 and below 1 will shrink the display; values above 1 will enlarge the 
	 *         display. Values less than or equal to 0 will be ignored. 
	 */
	public void setZoomScale(double zoomScale)
	{
		setZoomScaleWithoutRecalculation(zoomScale);
		calcTransformation();
	}
	
	/**Sets the zoom/pan behaviour type
	 * 
	 * @param zoomPanType  BOTH_DIRECTIONS=normal; VERTICAL_ONLY=only in y; HORIZONTAL_ONLY=only in x
	 */
	public void setZoomPanBehaviour(ZoomPanBehaviour zoomPanType){
		this.zoomPanType=zoomPanType;
		calcTransformation();
	}

	/**Get the zoom/zan behaviour type
	 * 
	 * @return  BOTH_DIRECTIONS=normal; VERTICAL_ONLY=only in y; HORIZONTAL_ONLY=only in x
	 */
	public ZoomPanBehaviour getZoomPanBehaviour(){
		return zoomPanType;
	}


	/** Reports the current pan offset. Useful when wishing to use an interpolated panning
	 *  between this current value and some new pan offset.
	 *  @return Current pan offset. Negative coordinates indicate an offset to the left
	 *          or upwards, positive values to the right or downward.       
	 */
	public PVector getPanOffset()
	{
		return zoomPanState.getPanOffset();
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
		zoomPanState.panOffset.x = panX;
		zoomPanState.panOffset.y = panY;
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

		if (e.getAction() == MouseEvent.RELEASE)
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

		if ((mouseMask < 0) || ((mouseMask == PConstants.SHIFT)   && !e.isShiftDown())
				 			|| ((mouseMask == PConstants.CONTROL) && !e.isControlDown())
				 			|| ((mouseMask == PConstants.ALT)     && !e.isAltDown()))
		{
			return;
		}

		// Only interpret the mousepressed event if the mouse is within mouseBoundsMask (or there's no mouseBoundsMask)
		if ((e.getAction() == MouseEvent.PRESS)	&& 
			((mouseBoundsMask==null) || (mouseBoundsMask.contains(zoomPanState.aContext.mouseX,zoomPanState.aContext.mouseY))))
		{
			isMouseCaptured   = true;
			zoomStartPosition = new PVector(e.getX(),e.getY());
			oldPosition       = new PVector(e.getX(),e.getY());
		}
		// Dragging is allowed outside the mouseBoundsMask
		else if (e.getAction() == MouseEvent.DRAG)
		{
			// Check in case applet has been destroyed.
			if ((zoomPanState.aContext == null) || (oldPosition == null))
			{
				return;
			}

			if ((zoomPanState.aContext.mouseButton==zoomMouseButton) && (allowZoomButton) && isMouseCaptured)
			{
				isZooming = true;

				if (zoomPanState.aContext.mouseY < oldPosition.y)
				{
					setZoomScaleWithoutRecalculation(zoomPanState.zoomScale*zoomStep);
				}
				else if (zoomPanState.aContext.mouseY > oldPosition.y)
				{
					setZoomScaleWithoutRecalculation(zoomPanState.zoomScale/zoomStep);
				}
				doZoom();
				zoomStep += 0.005;    // Accelerate zooming with prolonged drag.
			}
			else if (allowPanButton && isMouseCaptured)
			{        
				isPanning = true;
				//panOffset.setLocation((panOffset.y + e.getX() - oldPosition.y),
				//                     panOffset.y+ e.getY() - oldPosition.y);
				zoomPanState.panOffset.add(new PVector(e.getX()-oldPosition.x,e.getY()-oldPosition.y));
				calcTransformation(); 
			}

			oldPosition = new PVector(e.getX(),e.getY());
		}
	}

	/** Sets the minimum permitted zoom scale (i.e. how far zoomed out a view is allowed to be). If the
	 *  current zoom level is smaller than the new minimum, the zoom scale will be set to the new 
	 *  minimum value. A value above zero but less than one means that the view will be smaller than
	 *  its natural size. A value greater than one means the view will be larger than its natural size.
	 *  @param minZoomScale
	 */
	public void setMinZoomScale(double minZoomScale)
	{
		this.minZoomScale=minZoomScale;
		
		if (zoomPanState.zoomScale < minZoomScale)
		{
			setZoomScale(minZoomScale);
		}
	}
	
	/** Sets the maximum permitted zoom scale (i.e. how far zoomed in a view is allowed to be). If the
	 *  current zoom level is larger than the new maximum, the zoom scale will be set to the new 
	 *  maximum value. A value above zero but less than one means that the view will be smaller than
	 *  its natural size. A value greater than one means the view will be larger than its natural size.
	 *  @param maxZoomScale
	 */
	public void setMaxZoomScale(double maxZoomScale)
	{
		this.maxZoomScale=maxZoomScale;
		
		if (zoomPanState.zoomScale > maxZoomScale)
		{
			setZoomScale(maxZoomScale);
		}
	}
	
	// TODO: Panning constraints not yet implemented.
	/* Sets the maximum permitted panning offsets. The coordinates provided should be the unzoomed ones.
	 *  So to prevent panning past the 'edge' of the unzoomed display, values would be set to 0. Setting
	 *  values of (10,40) would allow the display to be panned 10 unzoomed pixels to the left or right
	 *  of the unzoomed display area and 40 pixels up or down.
	 *  @param maxX Maximum number of unzoomed pixels by which the display can be panned in the x-direction.
	 *  @param maxY Maximum number of unzoomed pixels by which the display can be panned in the y-direction.
	 *
	public void setMaxPanOffset(float maxX, float maxY)
	{
		this.maxPanXOffset = maxX;
		this.maxPanYOffset = maxY;
		
		boolean panChanged = false;

		if (panOffset.x >=0)
		{
			if (panOffset.x > maxPanXOffset)
			{
				panOffset.x = maxPanXOffset;
				panChanged = true;
			}
			if (panOffset.x < -maxPanXOffset)
			{
				panOffset.x = -maxPanXOffset;
				panChanged = true;
			}
		}

		if (panOffset.y >=0)
		{
			if (panOffset.y > maxPanYOffset)
			{
				panOffset.y = maxPanYOffset;
				panChanged = true;
			}
			if (panOffset.y < -maxPanYOffset)
			{
				panOffset.y = -maxPanYOffset;
				panChanged = true;
			}
		}
		
		if (panChanged)
		{
			calcTransformation();
		}
	}
	*/
	
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
		return zoomPanState.getDispToCoord(p);
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
		return zoomPanState.getCoordToDisp(p);
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
	 *  @param textToDisplay Text to be displayed.
	 *  @param xPos x-position of the the text to display in original unzoomed screen coordinates.
	 *  @param yPos y-position of the the text to display in original unzoomed screen coordinates.
	 */  
	public void text(String textToDisplay, float xPos, float yPos)
	{
		// Call the static version providing the applet context that was given to the constructor.
		ZoomPan.text(zoomPanState.aContext,textToDisplay, xPos,yPos);
	}

	/** Provides a copy (cloned snapshot) of the current ZoomPanState.
	 *  You can assume that this will not change its state.
	 *  @return Copy of the current zoomPanState.
	 */
	public ZoomPanState getZoomPanState()
	{
		return (ZoomPanState)zoomPanState.clone();
	}

	// ----------------------------- Private and package methods -----------------------------


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
		zoomPanState.panOffset.add(new PVector(zoomStartPosition.x-newZoomStartPosition.x,zoomStartPosition.y-newZoomStartPosition.y));

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
		// This method is of package-wide scope to allow inner classes to have access to it.*
		//limit zoom to min/max
		synchronized (this) 
		{
			zoomPanState.zoomScale = zoomScale;
			zoomPanState.zoomScale=Math.min(zoomPanState.zoomScale,maxZoomScale);
			zoomPanState.zoomScale=Math.max(zoomPanState.zoomScale,minZoomScale);
		}
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

		double centreX = (zoomPanState.getGraphics().width*(1-zoomPanState.zoomScale))/2;
		double centreY = (zoomPanState.getGraphics().height*(1-zoomPanState.zoomScale))/2;

		zoomPanState.trans = new AffineTransform();
		zoomPanState.iTrans = new AffineTransform();

		//scale depending on the type
		if (zoomPanType==ZoomPanBehaviour.BOTH_DIRECTIONS){
			zoomPanState.trans.translate(centreX+zoomPanState.panOffset.x,centreY+zoomPanState.panOffset.y);
			zoomPanState.trans.scale(zoomPanState.zoomScale,zoomPanState.zoomScale);
			zoomPanState.iTrans.scale(1/zoomPanState.zoomScale,1/zoomPanState.zoomScale);
			zoomPanState.iTrans.translate(-centreX-zoomPanState.panOffset.x, -centreY-zoomPanState.panOffset.y);
		}
		else if (zoomPanType==ZoomPanBehaviour.VERTICAL_ONLY){
			zoomPanState.trans.translate(0,centreY+zoomPanState.panOffset.y);
			zoomPanState.trans.scale(1,zoomPanState.zoomScale);
			zoomPanState.iTrans.scale(1,1/zoomPanState.zoomScale);
			zoomPanState.iTrans.translate(0, -centreY-zoomPanState.panOffset.y);
		}
		else if (zoomPanType==ZoomPanBehaviour.HORIZONTAL_ONLY){
			zoomPanState.trans.translate(centreX+zoomPanState.panOffset.x,0);
			zoomPanState.trans.scale(zoomPanState.zoomScale,1);
			zoomPanState.iTrans.scale(1/zoomPanState.zoomScale,1);
			zoomPanState.iTrans.translate(-centreX-zoomPanState.panOffset.x,0);
		}

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
		@SuppressWarnings("synthetic-access")
		public void mouseWheelMoved(MouseWheelEvent e)
		{     
			// Test to see if mouse mask is specified and it is pressed.
			if ((e.getModifiersEx() & getMouseMask()) != getMouseMask())
			{
				return;
			}
			
			//Ignore if outside the  mouseBoundsMask
			if ((mouseBoundsMask!=null) && (!mouseBoundsMask.contains(e.getX(),e.getY())))
			{
				return;
			}
			
			setZoomStartPosition(new PVector(e.getX(),e.getY()));

			if (e.getWheelRotation() < 0)
			{
				setZoomScaleWithoutRecalculation(getZoomScale()*getZoomStep());
				doZoom();
				
				// Store the time at which this was done
				timeAtLastWheelZoom = new Long(new Date().getTime());
				
				// Schedule triggering a zoom event
				timer.schedule(new CheckTriggerWheelZoomEvent(), millisecondsBeforeWheelZoomEvent);
			}
			else if (e.getWheelRotation() > 0)
			{
				setZoomScaleWithoutRecalculation(getZoomScale()/getZoomStep());
				doZoom();
				
				// Store the time at which this was done
				timeAtLastWheelZoom = new Long(new Date().getTime());
				
				// Schedule triggering a zoom event
				timer.schedule(new CheckTriggerWheelZoomEvent(), millisecondsBeforeWheelZoomEvent);
			}   
		}
	}
	
	/** Scheduled by the timer. If the last mouse wheel was used at least millisecondsBeforeWheelZoomEvent ago,
	 *  the event is triggered
	 */
	private class CheckTriggerWheelZoomEvent extends TimerTask
	{
		/** Creates a new event when the zoom wheel is triggered.
		 */
		public CheckTriggerWheelZoomEvent() 
		{
			// Empty constructor to stop synthetic accessor method from having to be created.
		}

		@SuppressWarnings("synthetic-access")
		@Override
		/**
		 * Checks if the last wheel zoom was at least millisecondsBeforeWheelZoomEvent ago.
		 * If so, zoom event is triggered on any ZoomPanListeners
		 */
		public void run() 
		{
			if (timeAtLastWheelZoom!=null)
			{
				if (timeAtLastWheelZoom.longValue()+millisecondsBeforeWheelZoomEvent-100<new Date().getTime())
				{
					for (ZoomPanListener listener:listeners)
					{
						timeAtLastWheelZoom=null;
						listener.zoomEnded();
					}
				}
			}
		}
		
	}
}