package org.gicentre.tests;

import java.awt.geom.Rectangle2D;

import org.gicentre.utils.stat.LikertChart;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

//  ****************************************************************************************
/** Tests the Likert chart in a simple Processing sketch. 
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.4, 5th February, 2016.
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

public class LikertTest extends PApplet
{
    // ------------------------------ Starter method ------------------------------- 

    /** Creates a simple application to test the Likert chart widget.
     *  @param args Command line arguments (ignored). 
     */
    public static void main(String[] args)
    {   
        PApplet.main(new String[] {"org.gicentre.tests.LikertTest"});
    }

    // ----------------------------- Object variables ------------------------------

    private PFont font;
    private LikertChart likert, likert2;
    private Rectangle2D bounds,bounds2;
    private boolean showBars, showSecondary,
                    chart1NewData, chart2NewData,
                    scaleToPrimary;
    
    // ---------------------------- Processing methods -----------------------------

    /** Sets the size and of the sketch and its maximum pixel density.
     */
	public void settings()
	{
		size(400,200);
		pixelDensity(displayDensity());
	}
	
    /** Sets up the charts and fonts.
     */
    public void setup()
    {   
        ellipseMode(PConstants.CORNER);
        
        surface.setResizable(true);
        bounds = new Rectangle2D.Float(width*.3f,height*.2f,width*.6f,height*.6f);
        bounds2 = new Rectangle2D.Float(3,3,width*.6f,35);
        
        
        showBars = true;
        scaleToPrimary = true;
        showSecondary = false;
        chart1NewData = false;
        chart2NewData = false;
        
        font = createFont("SansSerif",10);
        likert = new LikertChart(new float[] {2,4,6,8,10},4,0,0,
                "Primary chart with a long title that could be greater than the bounds of the chart", "Secondary data shown with 's' key.");
        likert.setShowTitle(true);
        likert.setSecondaryLineWidth(2);
        likert.setSecondaryDisplay(false);

        likert2 = new LikertChart(new float[] {1,2,3,2,1},0,0,0,
                "Secondary chart", "Only single data source shown here.");
        likert2.setShowTitle(true);
        
  
        likert.setSecondaryChart(likert2);
    }

    /** Draws the charts.
     */
    public void draw()
    {   
        background(255);
        
        // Resize the main chart in proportion to the window size        
        bounds.setRect(width*.3f,height*.2f,width*.6f,height*.6f);
        likert.draw(this,bounds,font);
        
        // Draw a small copy of the secondary chart for reference independent of window size. 
        likert2.draw(this,bounds2,font); 
    }

    /** Responds to key presses allowing appearance of chart to be changed.
     */
    public void keyPressed()
    {
        if (key == '1')
        {
            chart1NewData = !chart1NewData;
            
            if (chart1NewData)
            {
                likert.animateToNewValues(new float[] {100,80,60,40,20},40,0,0);
            }
            else
            {
                likert.animateToNewValues(new float[] {2,4,6,8,10},4,0,0);
            }
           
        }
        else if (key == '2')
        {
            chart2NewData = !chart2NewData;
            
            if (chart2NewData)
            {
                likert2.animateToNewValues(new float[] {50,40,30,20,10},0,0,0);
            }
            else
            {
                likert2.animateToNewValues(new float[] {1,2,3,2,1},0,0,0);
            }
        }
        else if (key == 's')
        {
            showSecondary = !showSecondary;
            likert.setSecondaryDisplay(showSecondary);
        }
        else if (key == 'p')
        {
            scaleToPrimary = !scaleToPrimary;
            likert.setScaleToPrimary(scaleToPrimary);
        }
        else if (key == ' ')
        {
            showBars = !showBars;
            if (showBars)
            {
                likert.animateToBars();
                likert2.animateToBars();
            }
            else
            {
                likert.animateFromBars();
                likert2.animateFromBars();
            }
        }  
        if (key == CODED)
        {
            if (keyCode == PConstants.LEFT)
            {
                likert.setWidthScale(likert.getWidthScale()-0.05f);
                if (likert.getWidthScale() < 0)
                {
                    likert.setWidthScale(0);
                }
            }
            else if (keyCode == PConstants.RIGHT)
            {
                likert.setWidthScale(likert.getWidthScale()+0.05f);
                if (likert.getWidthScale() > 1)
                {
                    likert.setWidthScale(1);
                }
            }
            else if (keyCode == PConstants.UP)
            {
                likert.setAnimationSpeed(likert.getAnimationSpeed()*0.9f);
                likert2.setAnimationSpeed(likert2.getAnimationSpeed()*0.9f);
            }
            else if (keyCode == PConstants.DOWN)
            {
                likert.setAnimationSpeed(likert.getAnimationSpeed()*1.1f);
                likert2.setAnimationSpeed(likert2.getAnimationSpeed()*1.1f);
            }  
        }
    }
    
    /** Responds to a mouse press by highlighting any bar that is at the mouse position when pressed.
     */
    public void mousePressed()
    {
        likert.setHighlightBar(likert.getBarAt(mouseX, mouseY));
    }
}