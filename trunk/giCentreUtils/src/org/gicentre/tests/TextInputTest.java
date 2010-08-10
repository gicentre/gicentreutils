package org.gicentre.tests;

import org.gicentre.utils.gui.TextInput;

import processing.core.PApplet;
import processing.core.PFont;

//  ****************************************************************************************
/** Tests the TextInput class in a simple Processing sketch. 
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.0, 10th August, 2010. 
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

@SuppressWarnings("serial")
public class TextInputTest extends PApplet
{
    // ------------------------------ Starter method ------------------------------- 

    /** Creates a simple application to test the text input field.
     *  @param args Command line arguments (ignored). 
     */
    public static void main(String[] args)
    {   
        PApplet.main(new String[] {"org.gicentre.tests.TextInputTest"});
    }

    // ----------------------------- Object variables ------------------------------
    
    TextInput textInput;
    PFont font;
 
    // ---------------------------- Processing methods -----------------------------

    /** Sets up the text input field.
     */
    public void setup()
    {   
        size(640,350);
        smooth(); 
        font = createFont("sans-serif",10);
        textInput = new TextInput(this,font,14);
    }

    /** Draws the text input field.
     */
    public void draw()
    {   
        background(255);
        noLoop();
        
        fill(120,60,60);
        textFont(font, 18);
        text("Type in some text and click mouse when finished.",30,30);
        
        textInput.draw(30,50);
        
    }
    
    /** Calls the text input key press monitoring.
     */
    public void keyPressed()
    {
        textInput.keyPressed();
        loop();
    }
    
    /** Display input text in console when mouse is clicked. 
     */
    public void mousePressed()
    {
        System.out.println("Input text is '"+textInput.getText()+"'");
    }
}