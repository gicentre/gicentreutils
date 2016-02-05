package org.gicentre.tests;

import org.gicentre.utils.stat.BarChart;
import org.gicentre.utils.stat.XYChart;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

//  *****************************************************************************************
/** Tests the formatting of chart values and categories.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.4 5th February, 2016. 
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

public class ChartFormatTest extends PApplet
{
    // ------------------------------ Starter method ------------------------------- 

    /** Creates a simple application to test the chart drawing utilities.
     *  @param args Command line arguments (ignored). 
     */
    public static void main(String[] args)
    {   
        PApplet.main(new String[] {"org.gicentre.tests.ChartFormatTest"});
    }

    // ----------------------------- Object variables ------------------------------

    private BarChart chart1;
    private XYChart chart2;
    private PFont font1, font2;
    private boolean isFont1,isThickLine, isDarkBar, isDarkLine, isLargeFont;
              
    // ---------------------------- Processing methods -----------------------------

    /** Sets the size and of the sketch and its maximum pixel density.
     */
	public void settings()
	{
		size(1200,600);
		pixelDensity(displayDensity());
	}
    
    /** Sets up the chart and fonts.
     */
    public void setup()
    {   
    	font1 = createFont("SansSerif",10);
    	font2 = createFont("Serif",10);
    	isFont1 = true;
    	isThickLine = false;
    	isDarkBar  = false;
    	isLargeFont = false;
    	isDarkLine = true;
    	
        
        float[] barData = new float[] {0.21235f, 0.5f, 1,     2,     4,     8,     16,    32,    64,    128,   256,   512,   1024, 2048};
        
        chart1 = new BarChart(this);
        chart1.setData(barData);
        chart1.showValueAxis(true);
        chart1.setValueFormat("###,###.####");
        chart1.showCategoryAxis(true);
        chart1.showValueAxis(true);    
        chart1.setBarLabels(new String[] {"Item 1","Item 2","Item 3","Item 4","Item 5","Item 6","Item 7","Item 8","Item 9","Item 10","Item 11","Item 12","Item 13", "Item 14"});
       
        chart2 = new XYChart(this);
        float[] xData = new float[]{2,4,6,8,12,14,15,16,23};
        float[] yData = new float[]{6.0f,7.2f,5.8f,4.3f,2.1f,3.5f,6.8f,6.2f,5.8f};
        float[] sizeData = new float[]{1,10,4,20,13,6,2,8,6};
        chart2.setData(xData, yData);
        chart2.showXAxis(true);
        chart2.showYAxis(true);
        chart2.setMinX(0);
        chart2.setMinY(0);
        chart2.setXFormat("###,###.###");
        chart2.setPointSize(10);
        chart2.setYFormat("0.0");
      
      
        chart2.setPointSize(sizeData, 16);
        chart2.setXAxisLabel("This is the x-axis");
        chart2.setYAxisLabel("This is the y-axis");    
    }

    /** Draws some charts.
     */
    public void draw()
    {   
        background(255);
       
        // These settings should have no effect on the appearance of the chart
        strokeWeight(30);		// Would be thick if test fails.		
        stroke(color(255,0,0));	// Would be red if test fails.
        fill(color(0,255,0));	// Would be green if test fails.
        textAlign(RIGHT,TOP);	// Text labels would appear uncentred if test fails.
       
        
        // These settings should affect appearance of the charts.
        textFont(isFont1? font1 : font2);
        textSize(isLargeFont ? 18 : 10);
                
        chart1.setBarColour(isDarkBar? color(40,0,0) : color(150,150,220));
        chart2.setLineWidth(isThickLine? 4: 1);         
        chart2.setLineColour(isDarkLine? color(0,0,40) : color(220,150,150));
        chart2.updateLayout();
  
        chart1.draw(20,1,width*.5f-20,height-22);
        chart2.draw(width*.5f+20,1,width*.5f-20,height-22);
        
        
        fill(80,0,0);
        textAlign(LEFT,TOP);
        textFont(font1);
        textSize(14);
        text("This text should be left-aligned with the first column centre. B to switch bar colour; F to switch fonts; L to switch line colour; S to switch font size; W to switch line width.",
        	chart1.getDataToScreen(new PVector(0,0)).x,20,width*0.4f,height*0.3f);
        noLoop();
    }
    
    /** Responds to key presses that configure the appearance of the chart.
     */
    public void keyPressed()
    {
    	if (key == 'b')
    	{
    		isDarkBar = !isDarkBar;
    		loop();
    	}
    	else if (key == 'f')
    	{
    		isFont1 = !isFont1;
    		loop();
    	}
    	else if (key == 'l')
    	{
    		isDarkLine = !isDarkLine;    		
    		loop();
    	}
    	else if (key == 's')
    	{
    		isLargeFont = !isLargeFont;    		
    		loop();
    	}
    	else if (key == 'w')
    	{
    		isThickLine = !isThickLine;    		
    		loop();
    	}
    }
    
}