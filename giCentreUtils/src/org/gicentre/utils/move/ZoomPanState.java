package org.gicentre.utils.move;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

// *****************************************************************************************
/** Class that stores the state of a ZoomPan instance and transform coordinates accordingly.
 *  Modifiable only from classes within the package. Only intended to be created and modified
 *  by ZoomPan.
 *  Used by ZoomPan internally to store its state. 
 *  @author Jo Wood and Aidan Slingsby, giCentre, City University London.
 *  @version 3.4.1, 25th February, 2016. 
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

public class ZoomPanState implements Cloneable
{
	// ---------------------------- Object variables -----------------------------
	
	private PApplet aContext;				// The sketch
	private PGraphics pGraphics;    		// An offscreen buffer 
	private AffineTransform trans,iTrans;   // The transformation
	private double zoomScaleX;      		// Zoom scale in X
	private double zoomScaleY;         		// Zoom scale in Y
	private double panX,panY;				// Panning offset.
	private double maxPanXOffset,maxPanYOffset;

	// ------------------------------- Constructor ------------------------------- 
	
	/** Do not use this constructor but instead call request it from ZoomPan.
	 *  @param aContext Parent sketch.
	 *  @param pGraphics Graphics context to zoom.
	 */
	ZoomPanState(PApplet aContext, PGraphics pGraphics)
	{
		this.aContext=aContext;
		this.pGraphics=pGraphics;
		zoomScaleX=1;
		zoomScaleY=1;
		panX = 0;
		panY = 0;
		maxPanXOffset = -1;				// Absolute max panning offset in x direction (original unzoomed coordinates), or negative if no maximum.
		maxPanYOffset = -1;				// Absolute max panning offset in y direction (original unzoomed coordinates), or negative if no maximum.

		trans=new AffineTransform();
		iTrans=new AffineTransform();
	}

	// ------------------------------ Public methods -----------------------------
	
	/** Transforms the given point from display to coordinate space. Display space is that which
	 *  has been subjected to zooming and panning. Coordinate space is the original space into 
	 *  which objects have been placed before zooming and panning. For most drawing operations you
	 *  should not need to use this method. It is available for those operations that do not draw
	 *  directly, but need to know the transformation between coordinate and screen space.
	 *  @param p 2D point in zoomed display space.
	 *  @return Location of point in original coordinate space. 
	 */
	PVector getDispToCoord(PVector p)
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
	PVector getCoordToDisp(PVector p)
	{
		Point2D.Float pDisp = new Point2D.Float();
		Point2D.Float pCoord = new Point2D.Float(p.x,p.y);
		trans.transform(pCoord,pDisp);
		return new PVector(pDisp.x,pDisp.y); 
	}
	
	/** Transforms the given point from coordinate to display space. Display space is that which
	 *  has been subjected to zooming and panning. Coordinate space is the original space into 
	 *  which objects have been placed before zooming and panning. For most drawing operations you
	 *  should not need to use this method. It is available for those operations that do not draw
	 *  directly, but need to know the transformation between coordinate and screen space.
	 *  @param x x location of the original coordinate space.
	 *  @param y y location of the original coordinate space.
	 *  @return Location of point in zoomed display space. 
	 */
	PVector getCoordToDisp(double x, double y)
	{
		Point2D.Double pDisp = new Point2D.Double();
		Point2D.Double pCoord = new Point2D.Double(x,y);
		trans.transform(pCoord,pDisp);
		return new PVector((float)pDisp.x,(float)pDisp.y); 
	}
	
	/** Performs the zooming/panning transformation. This method should be called in the
	 *  draw() method before any drawing that is to be zoomed or panned. 
	 */	
	void transform()
	{    
		getGraphics().translate((float)trans.getTranslateX(),(float)trans.getTranslateY());
		getGraphics().scale((float)trans.getScaleX(),(float)trans.getScaleY());
	}
	
	/** Performs the zooming/panning transformation in the given graphics context. This version of transform()
	 *  can be used for transforming off-screen buffers that were not provided to the constructor. Can
	 *  be useful when a sketch temporarily creates an off-screen buffer that needs to be zoomed and panned
	 *  in the same way as the main PApplet.
	 *  @param offScreenBuffer Graphics context in which to apply the zoom / pan transformation.
	 */
	void transform(PGraphics offScreenBuffer)
	{    
		offScreenBuffer.translate((float)trans.getTranslateX(),(float)trans.getTranslateY());
		offScreenBuffer.scale((float)trans.getScaleX(),(float)trans.getScaleY());
	}
	
	/** Reports the current zoom scale in X. Can be used for drawing objects that maintain their
	 *  size when zooming.
	 *  @return Current zoom scale. 
	 */
	double getZoomScale()
	{
		return zoomScaleX;
	}

	/** Reports the current zoom scale in X. Can be used for drawing objects that maintain their
	 *  size when zooming.
	 *  @return Current zoom scale. 
	 */
	double getZoomScaleX()
	{
		return zoomScaleX;
	}

	/** Reports the current zoom scale in Y. Can be used for drawing objects that maintain their
	 *  size when zooming.
	 *  @return Current zoom scale. 
	 */
	double getZoomScaleY()
	{
		return zoomScaleX;
	}
	
	/** Sets the zoom scale in X.
	 *  @param zs New zoom scale. 
	 */
	void setZoomScaleX(double zs)
	{
		this.zoomScaleX = zs;
	}
	
	/** Sets the zoom scale in Y.
	 *  @param zs New zoom scale. 
	 */
	void setZoomScaleY(double zs)
	{
		this.zoomScaleY = zs;
	}
	
	/** Sets the affine transform to be used to perform zooming and panning. This forward transform converts
	 *  unzoomed coordinates to the zoomed screen position.
	 *  @param trans Forward transformation.
	 */
	void setTransform(AffineTransform trans)
	{
		this.trans = trans;
	}
	
	/** Reports the affine transform used to perform zooming and panning. This forward transform converts
	 *  unzoomed coordinates to the zoomed screen position.
	 *  @return Forward transformation.
	 */
	AffineTransform getTransform()
	{
		return trans;
	}
	
	/** Sets the inverse affine transform to be used to perform zooming and panning. This inverse transform converts
	 *  zoomed coordinates to their unzoomed screen position.
	 *  @param trans Inverse transformation.
	 */
	void setInvTransform(AffineTransform iTrans)
	{
		this.iTrans = iTrans;
	}
	
	/** Reports the inverse affine transform used to perform zooming and panning. This inverse transform converts
	 *  zoomed screen position (e.g. under mouse) to their original unzoomed position.
	 *  @return Forward transformation.
	 */
	AffineTransform getInvTransform()
	{
		return iTrans;
	}
	
	/** Provides the Processing applet context subject to zooming.
	 *  @return Processing context.
	 */
	PApplet getContext()
	{
		return aContext;
	}
	
	/** Reports the current pan offset. Useful when wishing to use an interpolated panning
	 *  between this current value and some new pan offset.
	 *  @return Current pan offset. Negative coordinates indicate an offset to the left
	 *          or upwards, positive values to the right or downward.       
	 */
	PVector getPanOffset()
	{
		return new PVector((float)panX,(float)panY);
	}
	
	/** Supplies the graphics - either that supplied in the constructor or that from aContext
	 *  (the reason for this method is that the latter may change).
	 */
	PGraphics getGraphics()
	{
		if (pGraphics==null)
		{
			return aContext.g;
		}

		return pGraphics;
	}
	
	/** Creates a clone of this zoomPan state.
	 *  @return Clone of this zoomPan state.
	 */
	@Override
	public Object clone()
	{
		ZoomPanState zoomPanState=new ZoomPanState(aContext,pGraphics);
		zoomPanState.zoomScaleX=this.zoomScaleX;
		zoomPanState.zoomScaleY=this.zoomScaleY;
		//zoomPanState.panOffset=this.panOffset;
		zoomPanState.panX = this.panX;
		zoomPanState.panY = this.panY;
		zoomPanState.trans=new AffineTransform(trans);
		zoomPanState.iTrans=new AffineTransform(iTrans);
		return zoomPanState;
	}
	
	/** Sets the maximum permitted panning offsets. The coordinates provided should be the unzoomed ones.
	 *  So to prevent panning past the 'edge' of the unzoomed display, values would be set to 0. Setting
	 *  values of (10,40) would allow the display to be panned 10 unzoomed pixels to the left or right
	 *  of the unzoomed display area and 40 pixels up or down.
	 *  @param maxX Maximum number of unzoomed pixels by which the display can be panned in the x-direction.
	 *  @param maxY Maximum number of unzoomed pixels by which the display can be panned in the y-direction.
	 */
	void setMaxPanOffset(double maxX, double maxY)
	{
		this.maxPanXOffset = maxX;
		this.maxPanYOffset = maxY;
	}
	
	/** Sets a new pan offset. Can be used for programmatic control of panning, such as eased interpolated
	 *  zooming and panning.
	 *  @param panX X coordinate of new pan offset. A value of 0 indicates no translation of the display on
	 *              the horizontal axis; a negative value indicates a translation to the left; a positive 
	 *              value indicates translation to the right.
	 *  @param panY Y coordinate of new pan offset. A value of 0 indicates no translation of the display on
	 *              the vertical axis; a negative value indicates a translation upwards; a positive value 
	 *              indicates translation downwards.
	 */
	void setPanOffset(double panX, double panY)
	{
		this.panX = panX;
		this.panY = panY;
		
		if ((maxPanXOffset >= 0) || (maxPanYOffset >=0))
		{
			// May need to constrain panning.
			PVector tl = getCoordToDisp(new PVector((float)-maxPanXOffset,(float)-maxPanYOffset));
			PVector br = getCoordToDisp(new PVector((float)(aContext.width+maxPanXOffset),(float)(aContext.height+maxPanYOffset)));

			if (tl.x > 0)
			{
				this.panX -= tl.x;
			}
			if (tl.y > 0)
			{
				this.panY -= tl.y;
			}

			if (br.x < aContext.width)
			{
				this.panX += (aContext.width-br.x);
			}
			if (br.y < aContext.height)
			{
				this.panY += (aContext.height-br.y);
			}
		}
	}
	
	/** Increments the pan offset by the given amounts in the x and y directions.
	 *  @param panX Amount by which to change the x pan offset. A value of 0 indicates no translation of the display on
	 *              the horizontal axis; a negative value indicates a translation to the left; a positive 
	 *              value indicates translation to the right.
	 *  @param panY Amount by which to change the y pan offset. A value of 0 indicates no translation of the display on
	 *              the vertical axis; a negative value indicates a translation upwards; a positive value 
	 *              indicates translation downwards.
	 */
	void addPanOffset(double panX, double panY)
	{
		this.panX += panX;
		this.panY += panY;
		
		if ((maxPanXOffset >= 0) || (maxPanYOffset >=0))
		{
			// May need to constrain panning.
			PVector tl = getCoordToDisp(new PVector((float)-maxPanXOffset,(float)-maxPanYOffset));
			PVector br = getCoordToDisp(new PVector((float)(aContext.width+maxPanXOffset),(float)(aContext.height+maxPanYOffset)));

			if (tl.x > 0)
			{
				this.panX -= tl.x;
			}
			if (tl.y > 0)
			{
				this.panY -= tl.y;
			}
			
			if (br.x < aContext.width)
			{
				this.panX += (aContext.width-br.x);
			}
			if (br.y < aContext.height)
			{
				this.panY += (aContext.height-br.y);
			}
		}
	}
}
