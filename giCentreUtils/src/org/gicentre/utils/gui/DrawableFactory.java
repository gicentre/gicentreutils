package org.gicentre.utils.gui;

//*****************************************************************************************
/** Factory to produce drawable renderers. This factory will allow renderers created in 
 *  external libraries to be used by giCentre utilities.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.4, 5th February, 2016.
 */ 
//*****************************************************************************************

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

public class DrawableFactory 
{

	/** Creates a Drawable instance from the given Handy (sketchy hand-drawn library) renderer.
	 *  @param handy Hand-drawn renderer.
	 *  @return Instance of the Drawable interface capable of sketchy hand-drawn rendering.
	 */
	public static Drawable createHandyRenderer(org.gicentre.handy.HandyRenderer handy)
	{
		return new HandyUtilsRenderer(handy);
	}
	
	// ----------------------------------- Inner classes -----------------------------------
	
	/** Bridging class that wraps a HandyRenderer object into an instance of the Drawable interface.
 	  */
	private static class HandyUtilsRenderer implements Drawable
	{
		private org.gicentre.handy.HandyRenderer handy;
		
		public HandyUtilsRenderer(org.gicentre.handy.HandyRenderer handy)
		{
			this.handy = handy;
		}
		
		/** Draws a 2D point at the given coordinates. 
		 *  @param x x coordinate of the point.
		 *  @param y y coordinate of the point.
		 */
		public void point(float x, float y)
		{
			handy.point(x,y);
		}
		
		/** Draws a 2D line between the given coordinate pairs. 
		 *  @param x1 x coordinate of the start of the line.
		 *  @param y1 y coordinate of the start of the line.
		 *  @param x2 x coordinate of the end of the line.
		 *  @param y2 y coordinate of the end of the line.
		 */
		public void line(float x1, float y1, float x2, float y2)
		{
			handy.line(x1, y1, x2, y2);
		}
		
		/** Draws a rectangle using the given location and dimensions. By default the x,y coordinates
		 *  will be the top left of the rectangle, but the meanings of these parameters can be changed 
		 *  with Processing's rectMode() command.
		 *  @param x x coordinate of the rectangle position
		 *  @param y y coordinate of the rectangle position.
		 *  @param w Width of the rectangle (but see modifications possible with rectMode())
		 *  @param h Height of the rectangle (but see modifications possible with rectMode())
		 */
		public void rect(float x, float y, float w, float h)
		{
			handy.rect(x, y, w, h);
		}
		
		/** Draws an ellipse using the given location and dimensions. By default the x,y coordinates
		 *  will be centre of the ellipse, but the meanings of these parameters can be changed with 
		 *  Processing's ellipseMode() command.
		 *  @param x x coordinate of the ellipse's position
		 *  @param y y coordinate of the ellipse's position.
		 *  @param w Width of the ellipse (but see modifications possible with ellipseMode())
		 *  @param h Height of the ellipse (but see modifications possible with ellipseMode())
		 */
		public void ellipse(float x, float y, float w, float h)
		{
			handy.ellipse(x, y, w, h);
		}
		
		/** Draws a triangle through the three pairs of coordinates.
		 *  @param x1 x coordinate of the first triangle vertex.
		 *  @param y1 y coordinate of the first triangle vertex.
		 *  @param x2 x coordinate of the second triangle vertex.
		 *  @param y2 y coordinate of the second triangle vertex.
		 *  @param x3 x coordinate of the third triangle vertex.
		 *  @param y3 y coordinate of the third triangle vertex.
		 */
		public  void triangle(float x1, float y1, float x2, float y2, float x3, float y3)
		{
			handy.triangle(x1, y1, x2, y2, x3, y3);
		}
		
		/** Draws a complex line that links the given coordinates. 
		 *  @param xCoords x coordinates of the line.
		 *  @param yCoords y coordinates of the line.
		 */
		public void polyLine(float[] xCoords, float[] yCoords)
		{
			handy.polyLine(xCoords, yCoords);
		}
		
		/** Draws a closed polygon shape based on the given arrays of vertices.
		 *  @param xCoords x coordinates of the shape.
		 *  @param yCoords y coordinates of the shape.
		 */
		public void shape(float[] xCoords, float[] yCoords)
		{
			handy.shape(xCoords, yCoords);
		}
	}
}
