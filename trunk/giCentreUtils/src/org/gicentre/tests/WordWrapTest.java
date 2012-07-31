package org.gicentre.tests;

import java.awt.Rectangle;
import java.util.List;

import org.gicentre.utils.text.WordWrapper;
import org.gicentre.utils.text.WrappedToken;

import processing.core.PApplet;

//  *****************************************************************************************
/** Tests the text wrap class by displaying wrapped text with individual highlighting of wards.
 *  @author Aidan Slingsby with minor modifications by Jo Wood, giCentre, City University London.
 *  @version 3.2 31st July, 2012. 
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

@SuppressWarnings("serial")
public class WordWrapTest extends PApplet
{
	 // ------------------------------ Starter method ------------------------------- 

    /** Creates a simple application to test the word wrapping class.
     *  @param args Command line arguments (ignored). 
     */
    public static void main(String[] args)
    {   
        PApplet.main(new String[] {"org.gicentre.tests.WordWrapTest"});
    }
    
    // ----------------------------- Object variables ------------------------------
	
	private String s ="{other}The {adjective}quick{other} {adjective}brown{other} fox jumped over the {adjective}lazy{other} dog. "+
					  "Meanwhile, many {adjective}big{other} jackdaws quickly zipped over the fox pen.";
	
	// ---------------------------- Processing methods -----------------------------
	
	public void setup()
	{
		size(450,250);
		smooth();
		
		// The current font metrics are used by the text wrapper. 
		textSize(32);
		textAlign(LEFT,TOP);
		textLeading(textAscent()+textDescent());
	}

	public void draw()
	{
		background(255);
		noStroke();

		int wrapWidth=width-20;

		float x=10;
		float y=10;
		fill(150);
		
		List<WrappedToken> wrappedTokens = WordWrapper.wordWrapAndTokenise(s, x,y,wrapWidth, this.g);
		boolean cursorOverRedText=false;
		for (WrappedToken wrappedToken:wrappedTokens)
		{
			if (wrappedToken.id.equals("adjective"))
			{
				if (wrappedToken.getBounds(this).contains(mouseX,mouseY))
				{
					cursorOverRedText=true;
					Rectangle r=wrappedToken.getBounds(this);
					pushStyle();
					noFill();
					stroke(100);
					rect(r.x,r.y,r.width,r.height);
					popStyle();
				}
				fill(255,0,0);
			}
			else
			{
				fill(150);
			}
			text(wrappedToken.text,wrappedToken.x,wrappedToken.y);
		}

		if (cursorOverRedText)
		{
			cursor(HAND);
		}
		else
		{
			cursor(ARROW);
		}
	}
}