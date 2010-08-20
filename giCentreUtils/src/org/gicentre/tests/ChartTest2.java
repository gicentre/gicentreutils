package org.gicentre.tests;

import org.gicentre.utils.stat.BarChart;
import org.gicentre.utils.stat.XYChart;

import processing.core.PApplet;
import processing.core.PConstants;

//  ****************************************************************************************
/** Tests more sophisticated chart options in a Processing sketch. 
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.1, 19th August, 2010. 
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
public class ChartTest2 extends PApplet
{
    // ------------------------------ Starter method ------------------------------- 

    /** Creates a simple application to test the chart drawing utilities.
     *  @param args Command line arguments (ignored). 
     */
    public static void main(String[] args)
    {   
        PApplet.main(new String[] {"org.gicentre.tests.ChartTest2"});
    }

    // ----------------------------- Object variables ------------------------------

    private BarChart barChart;
    private XYChart  xyChart;

    private boolean showXAxis, showYAxis;
    private boolean showXAxisLabel, showYAxisLabel;
    private boolean transpose;
    private int barGap=2;

    // ---------------------------- Processing methods -----------------------------

    /** Sets up the chart and fonts.
     */
    public void setup()
    {   
        size(350,350);
        smooth();
        textFont(createFont("Helvetica",10));
        textSize(10);
        
        showXAxis      = true;
        showYAxis      = true;
        showXAxisLabel = true;
        showYAxisLabel = true;
        transpose      = false;

        float[] chartData = new float[] {12,-7,16,13,25};

        barChart = new BarChart(this);
        barChart.setData(chartData);

        barChart.showValueAxis(showYAxis);
        barChart.showCategoryAxis(showXAxis);
        barChart.transposeAxes(false);
        barChart.setBarGap(barGap);
        barChart.setCategoryAxisLabel("This is the x axis");
        barChart.setValueAxisLabel("This is the y axis");
        barChart.setBarColour(color(200,150,150));
        barChart.setCategoryAxisAt(0);
       
        
        xyChart = new XYChart(this);
        xyChart.setData(new float[] {1,2,3,4,5}, chartData);
        xyChart.setXAxisLabel("This is the x axis");
        xyChart.setYAxisLabel("This is the y axis");
        xyChart.setLineColour(color(80,30,30));
        xyChart.setLineWidth(2);
        xyChart.setPointSize(10);
        xyChart.setPointColour(color(80,80,150,180));
        xyChart.showXAxis(showXAxis);
        xyChart.showYAxis(showYAxis);
        xyChart.setXAxisAt(0);
    }

    /** Draws some charts.
     */
    public void draw()
    {   
        background(255);
        noLoop();
        
        // Draw a bounding rectangle to check graph occupies space correctly.
        strokeWeight(1);
        stroke(250,100,100);
        rect(1,1,width-2,height-2);
        
        // Draw the bar chart first, then overlay the line chart.
        barChart.draw(1,1,width-2,height-2);
        xyChart.draw(1,1,width-2,height-2); 
    }

    public void keyPressed()
    {
        if (key == 'x')
        {
            showXAxis = !showXAxis;
            barChart.showCategoryAxis(showXAxis);
            xyChart.showXAxis(showXAxis);
            loop();
        }
        else if (key == 'y')
        {
            showYAxis = !showYAxis;
            barChart.showValueAxis(showYAxis);
            xyChart.showYAxis(showYAxis);
            loop();
        }
        else if (key == 'l')
        {
            showXAxisLabel = !showXAxisLabel;
            showYAxisLabel = !showYAxisLabel;
            
            xyChart.setXAxisLabel(showXAxisLabel?"This is the x axis":null);
            xyChart.setYAxisLabel(showYAxisLabel?"This is the y axis":null);
            
            barChart.setCategoryAxisLabel(showXAxisLabel?"This is the x axis":null);
            barChart.setValueAxisLabel(showYAxisLabel?"This is the y axis":null);
            
            loop();
        }
        if (key == 't')
        {
            transpose = !transpose;
            barChart.transposeAxes(transpose);
            barChart.setReverseCategories(transpose);
            xyChart.transposeAxes(transpose);
            loop();
        }

        if (key == CODED)
        {
            if ((keyCode == PConstants.LEFT) && (barGap > 0))
            {
                barGap--;
                barChart.setBarGap(barGap);
                loop();
            }
            else if ((keyCode == PConstants.RIGHT) && (barGap < width))
            {
                barGap++;
                barChart.setBarGap(barGap);
                loop();
            }
        }
    }
}