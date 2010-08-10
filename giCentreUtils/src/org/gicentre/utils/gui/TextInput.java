package org.gicentre.utils.gui;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

//  ****************************************************************************************
/** Class for creating a text-field type input area. Typed text is displayed on screen with
 *  a simple caret. Can be used for single or multiple lines. No decoration of the input 
 *  area is provided but this can be added externally. 
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

public class TextInput
{
    // ----------------------------- Object variables ------------------------------

    private ArrayList<StringBuffer> inputLines;
    
    private int caretPos;
    private int activeLineNumber;

    private PFont font;                 // Font used for display.
    private float textSize;             // Size of text to display.
    private PApplet parent;             // Sketch to use this input field.
    
    private static final String NEW_LINE = System.getProperty("line.separator");
    

    // ------------------------------- Constructors --------------------------------

    /** Initialises the text input area with the given font.
     *  @param parent Sketch in which this text input area is to appear.
     *  @param font Font used to display text.
     *  @param textSize Size of text to display in pixel units.
     */
    public TextInput(PApplet parent, PFont font, float textSize)
    {
        this.parent = parent;
        this.font = font;
        this.textSize = textSize;
        inputLines = new ArrayList<StringBuffer>();
        inputLines.add(new StringBuffer());
        caretPos = 0;
        activeLineNumber = 0;
    }

    // ---------------------------------- Methods ----------------------------------


    /** Displays the currently entered input text at the given coordinates. This method should
     *  be called whenever the text needs updating.
     *  @param x X-coordinate of the top-left of the input field
     *  @param y Y-coordinate of the top-left of the input field.
     */
    public void draw(float x, float y)
    {
        parent.pushStyle();

        parent.textAlign(PConstants.LEFT,PConstants.TOP);
        parent.textFont(font, textSize);
        float lineHeight = parent.textAscent()+parent.textDescent();

        // Draw text.
        parent.fill(0);
        
        float yPos = y;
        for (StringBuffer line : inputLines)
        {
            parent.text(line.toString(), x, yPos);
            yPos += lineHeight;
        }
        
        String activeLine = inputLines.get(activeLineNumber).toString();
        yPos = y + (activeLineNumber*lineHeight);

        // Draw caret.
        parent.strokeWeight(0.5f);
        parent.stroke(200);
        float caretX = x + parent.textWidth(activeLine.substring(0,caretPos));
        parent.line(caretX,yPos,caretX,yPos+lineHeight);

        parent.popStyle();
    }

    /** Updates the text input area with whatever is typed in with the keyboard. It is the responsibility
     *  of the parent sketch to call this method to monitor key presses. This is most likely to be 
     *  done in the parent sketch's own <code>keyPressed()</code> method.
     */
    public void keyPressed()
    {
        if ((parent.key==PConstants.RETURN) || (parent.key == PConstants.ENTER))
        {
            String currentText = inputLines.get(activeLineNumber).toString();
            String textToSplit = currentText.substring(caretPos);
            inputLines.remove(activeLineNumber);
            inputLines.add(activeLineNumber,new StringBuffer(currentText.substring(0, caretPos)));
                        
            caretPos = 0;
            activeLineNumber++;
            inputLines.add(activeLineNumber,new StringBuffer(textToSplit));
        }
        else if (parent.key==PConstants.BACKSPACE)
        {
            if (caretPos > 0)
            {
                inputLines.get(activeLineNumber).deleteCharAt(caretPos-1);
                caretPos--;
            }
            else
            {
                // We may be at the start of a new line.
                if (activeLineNumber > 0)
                {
                    String textToMerge = inputLines.get(activeLineNumber).toString();
                    inputLines.remove(activeLineNumber);
                    activeLineNumber--;
                    inputLines.get(activeLineNumber).append(textToMerge);
                    caretPos = inputLines.get(activeLineNumber).length()-textToMerge.length();
                }
            }
        }
        else if (parent.key==PConstants.DELETE)
        {
            if (caretPos < inputLines.get(activeLineNumber).length())
            {
                inputLines.get(activeLineNumber).deleteCharAt(caretPos);
            }
            else if (activeLineNumber < inputLines.size()-1)
            {
                // Merge with next line if it exists.
                String textToMerge = inputLines.get(activeLineNumber+1).toString();
                inputLines.remove(activeLineNumber+1);
                inputLines.get(activeLineNumber).append(textToMerge);
            }
        }
        else if (parent.key == PConstants.CODED)
        {
            // Allow left and right arrows to move caret.
            if (parent.keyCode == PConstants.LEFT) 
            {  
                if (caretPos > 0)
                {
                    caretPos--;
                }
                else if (activeLineNumber > 0)
                {
                    // Move back a line.
                    activeLineNumber--;
                    caretPos = inputLines.get(activeLineNumber).length();
                }
            }
            else if (parent.keyCode == PConstants.RIGHT)
            {  
                if (caretPos < inputLines.get(activeLineNumber).length())
                {
                    caretPos++;
                }
                else if (activeLineNumber < inputLines.size()-1)
                {
                    // Move forward a line.
                    activeLineNumber++;
                    caretPos = 0;
                }
            }
        }
        else 
        {
            inputLines.get(activeLineNumber).insert(caretPos,Character.toString(parent.key));
            caretPos++;
        }
    }
    
    /** Sets the text to be displayed in the text input area. This can be useful if you wish to
     *  clear the text area after return has been pressed for example.
     *  @param text Text to display in text input area. Can be empty if you wish to 'reset' the field.
     */
    public void setText(String text)
    {
        if (text == null)
        {
            text = new String();
        }
        inputLines.clear();
        activeLineNumber = 0;
        
        inputLines.add(new StringBuffer(text));
        caretPos = text.length();
    }
    
    /** Reports the text that has been entered in the input field.
     *  @return Text entered in the input field.
     */
    public String getText()
    {
        StringBuffer output = new StringBuffer();
        for (int i=0; i<inputLines.size(); i++)
        {
            StringBuffer line = inputLines.get(i);
            output.append(line);
            if (i < inputLines.size()-1)
            {
                output.append(NEW_LINE);
            }
        }
        return output.toString();
    }
}