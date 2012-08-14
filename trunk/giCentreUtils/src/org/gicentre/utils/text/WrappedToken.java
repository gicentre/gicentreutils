package org.gicentre.utils.text;

import java.awt.Rectangle;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

// ****************************************************************************************
/** Holds details of wrapped tokens from org.gicentre.utils.text.wordWrapAndTokenise()
 *  and can return the screen bounds of the token, using the current textFont, textSize
 *  and textAlign.
 *  @author Aidan Slingsby, giCentre, City University London.
 *  @version 1.1, July 2012 
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
public class WrappedToken {

	public String text;
	public String id;
	public float x;
	public float y;
	public Rectangle bounds=new Rectangle();
	
	public String toString(){
		return id+": "+text+" "+x+","+y;
	}
	
	/** Returns a rectangle describing the screen bounds of this token (if drawn correctly)
	 *  Corrects for the different textAlign modes of the sketch.
	 *  @param sketch Sketch doing the drawing.
	 *  @return Bounds of this token in screen coordinates.
	 */
	public Rectangle getBounds(PApplet sketch){
		return getBounds(sketch.g);
	}

	/** Returns a rectangle describing the screen bounds of this token (if drawn correctly)
	 *  Corrects for the different textAlign modes of the pGraphics.
	 *  @param pGraphics The graphics context within which the text drawing takes place.
	 *  @return Bounds of this token in screen coordinates.
	 */
	public Rectangle getBounds(PGraphics pGraphics){
		float w=pGraphics.textWidth(this.text);
		float h=pGraphics.textAscent()+pGraphics.textDescent();
		float xPos=this.x;
		float yPos=this.y;
		
		if (pGraphics.textAlignY==PConstants.CENTER)
		{
			yPos-=(h/2);
		}
		else if (pGraphics.textAlignY==PConstants.BOTTOM)
		{
			yPos-=h;
		}

		if (pGraphics.textAlign==PConstants.CENTER)
		{
			xPos-=w/2;
		}
		else if (pGraphics.textAlign==PConstants.RIGHT)
		{
			xPos-=w;
		}
		return new Rectangle(Math.round(xPos),Math.round(yPos),Math.round(w),Math.round(h));
	}
}