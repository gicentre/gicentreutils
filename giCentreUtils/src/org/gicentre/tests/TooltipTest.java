package org.gicentre.tests;

import org.gicentre.utils.spatial.Direction;
import org.gicentre.utils.gui.Tooltip;

import processing.core.PApplet;
import processing.core.PFont;

//  ****************************************************************************************
/** Tests the Processing Utilities Tooltip class.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.4, 4th February, 2016. 
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

public class TooltipTest extends PApplet
{

    // ------------------------------ Starter method ------------------------------- 

    /** Runs the tooltip sketch as an application.
     *  @param args Command line arguments (ignored). 
     */
    public static void main(String[] args)
    {   
        PApplet.main(new String[] {"org.gicentre.tests.TooltipTest"});
    }
    
    // ----------------------------- Object variables ------------------------------
    
    PFont font;
    Tooltip tooltip1, tooltip2, tooltip3, tooltip4, tooltip5,tooltip6;
    String message = "This is just a test to see if we can get a tooltip with a reasonably large "+
                     "amount of text within it. It might be used to create a temporary 'help bubble' "+
                     "in which instructons for using or interpreting part of a sketch may be placed."+
                     "\n\n"+
                     "Can include separate paragraphs separated by any number of new lines. Help bubbles "+
                     "can also be used to point to particular parts of a sketch.";

    // ------------------------------- Constructors --------------------------------

    
    // ---------------------------- Processing methods -----------------------------

    /** Sets the size and of the sketch and its maximum pixel density.
     */
	public void settings()
	{
		size(800,700);
		pixelDensity(displayDensity());
	}
    
    /** Initialise the sketch.
     */
    public void setup()
    {                   
        font = createFont("sans-serif",10);
        
        tooltip1 = new Tooltip(this,font,14,250);
        tooltip1.setText(message);
        tooltip1.setBorderWidth(2);
        tooltip1.setBorderColour(100);
        tooltip1.setBackgroundColour(color(220,160,160,100));
        tooltip1.setTextColour(color(20,90,20));
        tooltip1.setAnchor(Direction.SOUTH);
        tooltip1.setIsCurved(true);
        tooltip1.showPointer(true);
        
        tooltip2 = new Tooltip(this,font,14,200);
        tooltip2.setText("X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X "+
                         ". . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . "+
                         ". . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . ");
        tooltip2.setIsCurved(true);
        tooltip2.showPointer(true);
        tooltip2.setAnchor(Direction.SOUTH_WEST);
        tooltip2.setPointerSize(100);
        
        tooltip3 = new Tooltip(this,font,14,240);
        tooltip3.setText(message);
        tooltip3.showPointer(true);
        tooltip3.setAnchor(Direction.NORTH);
        
        tooltip4 = new Tooltip(this,font,14,200);
        tooltip4.setText(message);
        tooltip4.showPointer(true);
        tooltip4.setIsCurved(true);
        tooltip4.showCloseIcon(true);
        tooltip4.setPointerSize(14);
        tooltip4.setBackgroundColour(color(0,140));
        tooltip4.setTextColour(color(255));
        tooltip4.setBorderColour(color(180));
        tooltip4.setBorderWidth(3);
        tooltip4.setAnchor(Direction.EAST);
         
        tooltip5 = new Tooltip(this,font,18,120);
        tooltip5.setText("A simple tooltip with default settings");
        
        tooltip6 = new Tooltip(this,font,14,400);
        tooltip6.setBackgroundColour(color(255,100,100,180));
        tooltip6.setAnchor(Direction.SOUTH);
        tooltip6.setIsFixedWidth(false);
        tooltip6.setText("Press 1 or 2 to toggle pointers");
    }

    /** Draws the tooltips.
     */
    public void draw()
    {   
        background(255);
        tooltip1.draw(width*.5f, height*.4f);
        tooltip2.draw(width*.7f, height*.5f);
        tooltip3.draw(width*.5f, height*.6f);
        tooltip4.draw(width*.3f, height*.5f);
        tooltip5.draw(width*.4f, height*.5f);
        tooltip6.draw(mouseX,mouseY);
    }
        
    /** Allows pointers to be turned on or off with the keyboard.
     */
    public void keyPressed()
    {
        if (key=='1')
        {
            tooltip1.showPointer(true);
            tooltip2.showPointer(true);
            tooltip3.showPointer(true);
            tooltip4.showPointer(true);
            tooltip5.showPointer(true);
        }
        else if (key=='2')
        {
            tooltip1.showPointer(false);
            tooltip2.showPointer(false);
            tooltip3.showPointer(false);
            tooltip4.showPointer(false);
            tooltip5.showPointer(false);
        }
    }
}