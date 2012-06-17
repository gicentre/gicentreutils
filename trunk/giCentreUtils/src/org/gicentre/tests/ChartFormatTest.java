package org.gicentre.tests;

import org.gicentre.utils.colour.ColourTable;
import org.gicentre.utils.stat.BarChart;
import org.gicentre.utils.stat.XYChart;

import processing.core.PApplet;
import processing.core.PConstants;

//  ****************************************************************************************
/** Tests the formatting of chart values and categories.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.2 29th April, 2012. 
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
    
      
    // ---------------------------- Processing methods -----------------------------

    /** Sets up the chart and fonts.
     */
    public void setup()
    {   
        size(1000,600);
        smooth(); 
        textFont(createFont("Helvetica",10));
        textSize(10);

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
        chart2.setLineWidth(0.1f);
        chart2.setXAxisLabel("This is the x-axis");
        chart2.setYAxisLabel("This is the y-axis");
    
    }

    /** Draws some charts.
     */
    public void draw()
    {   
        background(255);
        noLoop();

        strokeWeight(1);
        
        
        chart1.draw(1,1,width*.5f-2,height-2);
        
      
        chart2.draw(width*.5f+1,1,width*.5f-2,height-2);
    }
}