package org.gicentre.tests;

import org.gicentre.utils.gui.HelpScreen;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;


//  ****************************************************************************************
/** Tests the Processing Utilities HelpScreen class.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.2, 1st August, 2011.
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
public class HelpScreenTest extends PApplet
{

    // ------------------------------ Starter method ------------------------------- 

    /** Runs the help screen sketch as an application.
     *  @param args Command line arguments (ignored). 
     */
    public static void main(String[] args)
    {   
        PApplet.main(new String[] {"org.gicentre.tests.HelpScreenTest"});
    }
    
    // ----------------------------- Object variables ------------------------------
    
    private PFont font;
    private HelpScreen helpScreen;
    private boolean showTitle, showFooter;
    private int verticalSpacing;
    
    private static final String TITLE_TEXT  = "This is the title";
    private static final String FOOTER_TEXT = "Last modified 20th June 2010";

 
    // ---------------------------- Processing methods -----------------------------

    /** Initialises the sketch.
     */
    public void setup()
    {   
        size(800,600);
        smooth(); 
        
        font = createFont("sansSerif",14);
        showTitle  = true;
        showFooter = true;
        verticalSpacing = 20;
        
        helpScreen = new HelpScreen(this,font);
        helpScreen.setIsActive(true);
        helpScreen.setHeader(TITLE_TEXT, verticalSpacing, 14);
        helpScreen.setFooter(FOOTER_TEXT, verticalSpacing, 8);
        helpScreen.putEntry("F", "Toggles the footer on and off");
        helpScreen.putEntry("H", "Toggles this help screen on and off");
        helpScreen.addSpacer();
        helpScreen.putEntry("T", "Toggles the title on and off");
        helpScreen.addSpacer();
        helpScreen.putEntry("Up/down arrows", "Change vertical spacing");
    }

    /** Draws the help screen if has been toggled on.
     */
    public void draw()
    {   
        background(255);
        noLoop();
        if (helpScreen.getIsActive())
        {
            helpScreen.draw();
        }
    }
        
    /** Modifies help screen content or display.
     */
    public void keyPressed()
    {
        if ((key=='f') || (key=='F'))
        {
            showFooter = !showFooter;
            if (showFooter)
            {
                helpScreen.setFooter(FOOTER_TEXT, verticalSpacing, 8);
            }
            else
            {
                helpScreen.setFooter(null, 0,0);
            }
            loop();
        }
        else if ((key=='h') || (key=='H'))
        {
            helpScreen.setIsActive(!helpScreen.getIsActive());
            loop();
        }
        else if ((key=='t') || (key=='T'))
        {
            showTitle = !showTitle;
            if (showTitle)
            {
                helpScreen.setHeader(TITLE_TEXT, verticalSpacing, 14);
            }
            else
            {
                helpScreen.setHeader(null, 0,0);
            }
            loop();
        }
        
        if (key == CODED)
        {
            if (keyCode == PConstants.UP)
            {
                verticalSpacing++;
                if (showFooter)
                {
                    helpScreen.setFooter(FOOTER_TEXT, verticalSpacing, 8);
                    loop();
                }
                if (showTitle)
                {
                    helpScreen.setHeader(TITLE_TEXT, verticalSpacing, 14);
                    loop();
                }
                
            }
            else if ((keyCode == PConstants.DOWN) && (verticalSpacing > 0))
            {
                verticalSpacing--;
                if (showFooter)
                {
                    helpScreen.setFooter(FOOTER_TEXT, verticalSpacing, 8);
                    loop();
                }
                if (showTitle)
                {
                    helpScreen.setHeader(TITLE_TEXT, verticalSpacing, 14);
                    loop();
                }
            }
        }
    }
}