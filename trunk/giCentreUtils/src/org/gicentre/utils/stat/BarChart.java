package org.gicentre.utils.stat;

import org.gicentre.utils.colour.ColourTable;

import processing.core.PApplet;
import processing.core.PConstants;

//  ********************************************************************************
/** Represents a bar chart. Appearance can be customised such as display of axes, 
 *  bar colours, orientations etc. 
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

public class BarChart extends AbstractChart
{
    // The bar chart stores the variable corresponding to the length of each bar in
    // dimension 0 of the chart, the category index in dimension 1 and the optional
    // colour variable in dimension 2.
    
    // ----------------------------- Object variables ------------------------------
      
    private int barColour;
    private float barGap;
    private boolean reverseCats;
    private ColourTable cTable;
    
    private String[] catLabels;     // Category labels.
    private boolean showLabels;
          
    // ------------------------------- Constructors --------------------------------
    
    /** Initialises a bar chart.
     *  @param parent Parent sketch in which this chart is to be drawn.
     */
    public BarChart(PApplet parent)
    {
        super(parent);
       
        barGap        = 1;
        barColour     = parent.color(180);
        reverseCats   = false;
        cTable        = null;
        catLabels     = null;
        showLabels    = false;
    }
    
    // ---------------------------------- Methods ----------------------------------
    
    /** Sets the data values to be displayed in the chart. Each item in the given array
     *  is assumed to be in a linear sequence of equal width.
     *  @param values Sequence of values to chart.
     */
    public void setData(float[] values)
    {
        setData(1,values);
        
        float[] categories = new float[values.length];
        for (int i=0; i<categories.length; i++)
        {
            categories[i] = i+1;
        }
        setData(0,categories);
    }

    /** Draws the bar chart within the given bounds.
     *  @param xOrigin left-hand pixel coordinate of the area in which to draw the chart.
     *  @param yOrigin top pixel coordinate of the area in which to draw the chart.
     *  @param width Width in pixels of the area in which to draw the chart.
     *  @param height Height in pixels of the area in which to draw the chart.
     */
    public void draw(float xOrigin, float yOrigin, float width, float height)
    {
        if ((data[1] == null) || (data[1].length==0))
        {
            return;
        }
        
        parent.pushMatrix();
        parent.pushStyle();
        
        // Use a local coordinate system with origin at top-left of drawing area.
        parent.translate(xOrigin,yOrigin);
        
        // TODO: A fudge to guess at the amount of space to allow last (right-most) axis label to be drawn.
        setMinBorder(parent.textWidth("XXX"), Side.RIGHT);
        
        float left   = getBorder(Side.LEFT);
        float right  = width - getBorder(Side.RIGHT);
        float bottom = height-getBorder(Side.BOTTOM);
        float top    = getBorder(Side.TOP);
        float hRange = right-left;
        float vRange = bottom-top;
        float barWidth;
        
        if (transposeAxes)
        {
            barWidth = (vRange - (data[0].length-1)*barGap) / data[0].length;    
        }
        else
        {
            barWidth = (hRange - (data[0].length-1)*barGap) / data[0].length;    
        }
       
        parent.noStroke();
        
        if (cTable == null)
        {
            parent.fill(barColour);
        }
        
        for (int i=0; i<data[0].length; i++)
        {
            if (cTable != null)
            {
                parent.fill(cTable.findColour(data[2][i]));
            }
            
            int index = reverseCats?(data[0].length-1-i):i;
            if (transposeAxes)
            {
                if (getIsLogScale(1))
                {
                    parent.rect(left, top + i*(barWidth+barGap), hRange*convertToLog(data[1][index],getMinLog(1),getMaxLog(1)),barWidth);                    
                }
                else
                {
                    parent.rect(left, top + i*(barWidth+barGap), hRange*(data[1][index]-getMin(1))/(getMax(1)-getMin(1)),barWidth);
                }
            }
            else
            {
                if (getIsLogScale(1))
                {
                    parent.rect(left + i*(barWidth+barGap), bottom, barWidth, -vRange*convertToLog(data[1][index],getMinLog(1),getMaxLog(1)));   
                }
                else
                {
                    parent.rect(left + i*(barWidth+barGap), bottom, barWidth, -vRange*(data[1][index]-getMin(1))/(getMax(1)-getMin(1)));
                }
            }
        }
        
        if (getShowAxis(1))  // Value axis.
        {
            parent.strokeWeight(0.5f);
            parent.stroke(120);
            parent.fill(0,150);

            if (transposeAxes)
            {
                parent.line(left,bottom,right,bottom);
            }
            else
            {
                parent.line(left,bottom,left,top);
            }
            
            if (getIsLogScale(1))
            {                         
                for (float logTic : logTics[1])
                {
                    float tic = (float)Math.pow(10,logTic);
                    if (tic <= getMax(1))
                    {
                        if (transposeAxes)
                        {
                            parent.textAlign(PConstants.CENTER, PConstants.TOP);
                            parent.text(axisFormatter[1].format(tic),left +hRange*(logTic-getMinLog(1))/(getMaxLog(1)-getMinLog(1)),bottom+2);
                        }
                        else
                        {
                            parent.textAlign(PConstants.RIGHT, PConstants.CENTER);
                            parent.text(axisFormatter[1].format(tic),left-2,top +vRange*(getMaxLog(1)-logTic)/(getMaxLog(1)-getMinLog(1)));
                        }
                    }
                }   
            }
            else
            {
                for (float tic : tics[1])
                {
                    if (tic <= getMax(1))
                    {
                        if (transposeAxes)
                        {
                            parent.textAlign(PConstants.CENTER, PConstants.TOP);
                            parent.text(axisFormatter[1].format(tic),left +hRange*(tic-getMin(1))/(getMax(1)-getMin(1)),bottom+2);
                        }
                        else
                        {
                            parent.textAlign(PConstants.RIGHT, PConstants.CENTER);
                            parent.text(axisFormatter[1].format(tic),left-2,top +vRange*(getMax(1)-tic)/(getMax(1)-getMin(1)));
                        }
                    }
                }
            }
        }
        
        if (getShowAxis(0))  // Category axis.
        {
            parent.strokeWeight(0.5f);
            parent.stroke(120);
            parent.fill(0,150);
               
            for (int i=0; i<data[0].length; i++)
            {
                if (transposeAxes)
                {
                    parent.textAlign(PConstants.RIGHT, PConstants.CENTER); 
                    int index = reverseCats?(data[0].length-1-i):i;
                    if (showLabels == false)
                    {
                        parent.text(axisFormatter[0].format(data[0][index]),left-2,top+barWidth/2f + i*(barWidth+barGap));
                    }
                    else
                    {
                        parent.text(catLabels[index],left-2,top+barWidth/2f + i*(barWidth+barGap));
                    }
                    
                }
                else
                {
                    parent.textAlign(PConstants.CENTER, PConstants.TOP); 
                    int index = reverseCats?(data[0].length-1-i):i;
                    if (showLabels == false)
                    {
                        parent.text(axisFormatter[0].format(data[0][index]),left+barWidth/2f + i*(barWidth+barGap),bottom+2);
                    }
                    else
                    {
                        parent.text(catLabels[index],left+barWidth/2f + i*(barWidth+barGap),bottom+2);
                    }
                }
            }
        }
        
        parent.popStyle();
        parent.popMatrix();
    }
    
    /** Determines whether or not the values represented by the length of each bar should be log10-scaled.
     *  @param isLog True if values are to be log10-scaled or false if linear.
     */
    public void setLogValues(boolean isLog)
    {
        setIsLogScale(1, isLog);
    }

    /** Sets the minimum value for the bar chart. Can be used to ensure multiple charts
     *  can share the same origin.
     *  @param minVal Minimum value to use for scaling bar lengths.
     */
    public void setMinValue(float minVal)
    {
       setMin(1,minVal);
    }
    
    /** Sets the maximum value for the bar chart. Can be used to ensure multiple charts
     *  are scaled to the same maximum.
     *  @param maxVal Maximum value to use for scaling bar lengths.
     */
    public void setMaxValue(float maxVal)
    {
       setMax(1,maxVal);
    }
    
    /** Sets the bar names to be displayed as axis labels. If set to null, the category
     *  number is displayed in the axis.
     *  @param labels Array of labels corresponding to each of the bars in the chart.
     */
    public void setBarLabels(String[] labels)
    {
        if (labels == null)
        {
            showLabels = false;
           
            if (getShowAxis(0))
            {
                // This will recalculate appropriate border now we are not showing labels.
                setMinBorder(0, transposeAxes?Side.LEFT:Side.BOTTOM);
                showAxis(0, true, transposeAxes?Side.LEFT:Side.BOTTOM);
            }
            return;
        }
        
        if (labels.length != data[0].length)
        {
            System.err.println("Warning: Number of labels ("+labels.length+") does not match number of bars ("+data[0].length+").");
            return;
        }
        
        this.catLabels = labels;
        showLabels = true;
        
        float border = getMinBorder();
        
        if (transposeAxes == false)
        {
            // Bar labels are on along the bottom.
            border = Math.max(border, parent.textAscent()+parent.textDescent());
            setMinBorder(border,Side.BOTTOM);
            
            // TODO:  This is a bit of a fudge to avoid calculating bar width: Assume we need to make space for 
            //        1/4 of the width of the final label (centred at the middle of the right-hand bar).
            setMinBorder(parent.textWidth(labels[labels.length-1])/4f,Side.RIGHT);
        }
        else
        {
            // Bar labels are up the side.
            for (String label : labels)
            {
                border = Math.max(border, parent.textWidth(label));
            }
            setMinBorder(border,Side.LEFT);
        } 
    }
    
    /** Determines whether or not the value axis is drawn.
     *  @param showAxis Value axis is drawn if true.
     */
    public void showValueAxis(boolean showAxis)
    {
        super.showAxis(1,showAxis,transposeAxes?Side.BOTTOM:Side.LEFT);
    }
    
    /** Determines whether or not the category axis is drawn.
     *  @param showAxis Category axis is drawn if true.
     */
    public void showCategoryAxis(boolean showAxis)
    {
        if (showAxis && showLabels)
        {
            // Need to recalculate space for labels if they are being made to reappear.
            setBarLabels(catLabels);
        }
        super.showAxis(0,showAxis,transposeAxes?Side.LEFT:Side.BOTTOM);
    }
    
    /** Determines if the axes should be transposed (so that categories appear on the 
     *  vertical axis and values on the horizontal axis).
     *  @param transpose Axes are transposed if true.
     */
    public void transposeAxes(boolean transpose)
    {
        this.transposeAxes = transpose;
    }
    
    /** Sets the gap between adjacent bars.
     *  @param gap Gap between adjacent bars in pixels
     */
    public void setBarGap(float gap)
    {
        this.barGap = gap;
    }
    
    /** Determines if the order of the category values should be reversed or not.
     *  @param reverse Category order reversed if true.
     */
    public void setReverseCategories(boolean reverse)
    {
        this.reverseCats = reverse;
    }
        
    /** Sets the numerical format for numbers shown on the value axis.
     *  @param format Format for numbers on the value axis.
     */
    public void setValueFormat(String format)
    {
        setFormat(1, format);
    }
    
    /** Sets the numerical format for numbers shown on the category axis.
     *  @param format Format for numbers on the category axis.
     */
    public void setCategoryFormat(String format)
    {
        setFormat(0, format);
    }
    
    /** Determines the colours of the bars to be displayed on the chart. This method
     *  will give a uniform colour to all bars.
     *  @param colour Colour of bars.
     */
    public void setBarColour(int colour)
    {
        this.barColour = colour;
        cTable = null;      // Ignore and data-colour rules.
        data[2] = null;
    }
    
    /** Provides the data and colour table from which to colour bars. Each data item
     *  should by in the same order as the data provided to <code>setData()</code>.
     *  @param colourData Data used to colour bars
     *  @param cTable Colour table that translates data values into colours.
     */
    public void setBarColour(float[]colourData, ColourTable cTable)
    {
        if (colourData.length != data[1].length)
        {
            System.err.println("Warning: Number of items in bar colour data ("+colourData.length+") does not match number of bars ("+data[1].length+").");
            return;
        }
        
        this.cTable = cTable;
        
        // Store colour data in dimension 2 of the chart.
        setData(2,colourData);
    }
}