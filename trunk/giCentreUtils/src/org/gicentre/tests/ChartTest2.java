package org.gicentre.tests;

import org.gicentre.utils.stat.BarChart;
import org.gicentre.utils.stat.XYChart;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

//  ****************************************************************************************
/** Tests more sophisticated chart options in a Processing sketch. 
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.3, 1st August, 2011.
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
    private boolean capValues;
    private int barGap=2;
    private int barPad=0;
    
    private PVector dataScreenLocation;
    private PVector dataPoint;

    // ---------------------------- Processing methods -----------------------------

    /** Sets up the chart and fonts.
     */
    public void setup()
    {   
        size(550,350);
        smooth();
        textFont(createFont("Helvetica",10));
        textSize(10);
        
        showXAxis          = true;
        showYAxis          = true;
        showXAxisLabel     = true;
        showYAxisLabel     = true;
        transpose          = false;
        capValues          = false;
        dataScreenLocation = null;
       
        float[] chartData = new float[] {12,-7,16,13,25,6,4,7,5,-3,-6,2,5,4,10,4,6,7,9,3};

        barChart = new BarChart(this);
        barChart.setData(chartData);
        barChart.transposeAxes(false);
        barChart.setBarGap(barGap);
        barChart.setBarColour(color(200,150,150));
        barChart.setShowEdge(true);
        barChart.setCategoryAxisLabel("This is the x axis");
        barChart.setValueAxisLabel("This is the y axis");
        barChart.showCategoryAxis(showXAxis);
        barChart.showValueAxis(showYAxis);
        barChart.setCategoryAxisAt(0);          // Allow bars to dip below axis when negative. 
               
        xyChart = new XYChart(this);
        xyChart.setData(new float[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20}, chartData);
        
        xyChart.setLineColour(color(80,30,30));
        xyChart.setLineWidth(2);
        xyChart.setPointSize(10);
        xyChart.setPointColour(color(80,80,150,180));
        xyChart.setXAxisAt(0);
        xyChart.setMinY(barChart.getMinValue()); // Scale line graph to use same space as bar graph.
        xyChart.setMaxY(barChart.getMaxValue());
        xyChart.calcDataSpacing();
    }

    /** Draws some charts.
     */
    public void draw()
    {   
        background(255);
        noLoop();
                
        // Draw the bar chart first, then overlay the line chart.
        stroke(150,100,100,200);
        barChart.draw(2,1,width-4,height-2);  
        
        PVector bottomLeft = barChart.getDataToScreen(new PVector(0,barChart.getMinValue()));
        PVector topRight   = barChart.getDataToScreen(new PVector(barChart.getNumBars()-1,barChart.getMaxValue()));
                         
        xyChart.draw(bottomLeft.x-xyChart.getLeftSpacing(),topRight.y-xyChart.getTopSpacing(),
        		     topRight.x+xyChart.getRightSpacing()-(bottomLeft.x-xyChart.getLeftSpacing()),
        		     bottomLeft.y+xyChart.getBottomSpacing()-(topRight.y-xyChart.getTopSpacing()));
        
        // Draw the last clicked data item.
        if (dataPoint != null)
        {
            stroke(0,200);
            strokeWeight(2);
            
            float barValue = barChart.getData()[(int)dataPoint.x];
            if (((barValue > 0) && (dataPoint.y <=barValue) && (dataPoint.y >= 0)) ||
                ((barValue < 0) && (dataPoint.y >=barValue) && (dataPoint.y <=0)))
            {
                fill(50,150,50,180);
            }
            else
            {
                fill(150,50,50,180);
            }
            ellipse(dataScreenLocation.x,dataScreenLocation.y,10,10);
        }
    }
    
    public void mousePressed()
    {
        dataPoint = barChart.getScreenToData(new PVector(mouseX,mouseY));
        if (dataPoint == null)
        {
            System.out.println("Screen point of "+mouseX+","+mouseY+" is outside the data area.");
        }
        else
        {
            System.out.println("Screen point of "+mouseX+","+mouseY+" gives XY data point of "+(int)dataPoint.x+","+dataPoint.y);
            dataScreenLocation = barChart.getDataToScreen(dataPoint);
            System.out.println("    which in turn gives screen point of "+
                               Math.round(dataScreenLocation.x)+","+Math.round(dataScreenLocation.y));
        }
        loop();
    }
    
    public void mouseDragged()
    {
        dataPoint = barChart.getScreenToData(new PVector(mouseX,mouseY));
        if (dataPoint != null)
        {
            dataScreenLocation = barChart.getDataToScreen(dataPoint);
        }
        loop();
    }

    public void keyPressed()
    {
        if (key == 'c')
        {
            capValues = ! capValues;
            
            if (capValues)
            {
                barChart.setMaxValue(20);
                barChart.setMinValue(-5);
                xyChart.setMaxY(20);
                xyChart.setMinY(-5);
            }
            else
            {
                // Use data to determine range.
                barChart.setMaxValue(Float.NaN);
                barChart.setMinValue(Float.NaN);
                xyChart.setMinY(barChart.getMinValue()); 
                xyChart.setMaxY(barChart.getMaxValue());
            }
            loop();
        }
        else if (key == 'x')
        {
            showXAxis = !showXAxis;
            barChart.showCategoryAxis(showXAxis);
            loop();
        }
        else if (key == 'y')
        {
            showYAxis = !showYAxis;
            barChart.showValueAxis(showYAxis);
            
            // Because the value axis visbility has changed, this might result in
            // a rescaling of the data range. Therefore the XY chart needs to update
            // its range accordingly.
            xyChart.setMinY(barChart.getMinValue());
            xyChart.setMaxY(barChart.getMaxValue());
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
            else if (keyCode == PConstants.DOWN)
            {
                if (barPad > 0)
                {
                    barPad--;
                    barChart.setBarPadding(barPad);
                    loop();
                }
            }
            else if (keyCode == PConstants.UP)
            {
                if (barPad < width)
                {
                    barPad++;
                    barChart.setBarPadding(barPad);
                    loop();
                }
            }
        }
    }
}