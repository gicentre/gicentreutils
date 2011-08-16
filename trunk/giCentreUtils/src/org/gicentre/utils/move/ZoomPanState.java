package org.gicentre.utils.move;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class ZoomPanState implements Cloneable{

	//package-wide visibility - directly access and manipulated by ZoomPan
	PApplet aContext;		//the sketch
	PGraphics pGraphics;    //an offscreen buffer 
	AffineTransform trans,iTrans;   //the transformation
	double zoomScale;               //zoom scale
	PVector panOffset;              //offset

	
	// *****************************************************************************************
	/** Class that stores the state of a ZoomPan instance and transform coordinates accordingly.
	 * 
	 * Modifiable only from classes within the package. Only intended to be created and modified
	 * by ZoomPan.
	 * 
	 * Used by ZoomPan internally to store its state. 
	 * 
     *  @author Jo Wood and Aidan Slingsby, giCentre, City University London.
     *  @version 1, 1st August, 2011. 
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

	/** Constructor
	 * Do not use - request from ZoomPan
	 */
	ZoomPanState(PApplet aContext, PGraphics pGraphics)
	{
		this.aContext=aContext;
		this.pGraphics=pGraphics;
		zoomScale=1;
		panOffset=new PVector();
		trans=new AffineTransform();
		iTrans=new AffineTransform();
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
	
	/** Performs the zooming/panning transformation. This method should be called in the
	 *  draw() method before any drawing that is to be zoomed or panned. 
	 */	public void transform()
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
	
	/** Reports the current zoom scale. Can be used for drawing objects that maintain their
	 *  size when zooming.
	 *  @return Current zoom scale. 
	 */
	public double getZoomScale()
	{
		return zoomScale;
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
	
	public Object clone(){
		ZoomPanState zoomPanState=new ZoomPanState(aContext,pGraphics);
		zoomPanState.zoomScale=this.zoomScale;
		zoomPanState.panOffset=this.panOffset;
		zoomPanState.trans=new AffineTransform(trans);
		zoomPanState.iTrans=new AffineTransform(iTrans);
		return zoomPanState;
	}
}
