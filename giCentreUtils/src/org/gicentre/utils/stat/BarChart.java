package org.gicentre.utils.stat;

import org.gicentre.utils.colour.ColourTable;

import processing.core.PApplet;
import processing.core.PConstants;

//  ********************************************************************************
/** Represents a bar chart. Appearance can be customised such as display of axes, 
 *  bar colours, orientations etc. 
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.1, 20th August, 2010. 
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
    
    private String[] catLabels;                  // Category labels.
    private boolean showLabels;
    private String categoryLabel, valueLabel;    // Axis labels.
    private Float catAxisPosition;               // Position of the category axis (defaults to minumum value).
          
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
        categoryLabel = null;
        valueLabel    = null;
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
        
        // Extra spacing required to fit axis labels. This can't be handled by the AbstractChart
        // because not all charts label their axes in the same way.
        
        float extraLeftBorder =2;
        float extraRightBorder =2;
        float extraTopBorder =2;
        float extraBottomBorder =2;
         
        // Allow space to the right of the horizontal axis to accommodate right-hand tic label.
        if ((getShowAxis(0)) || ((transposeAxes) && (getShowAxis(1))))
        {
            int axis = transposeAxes?1:0;
            String lastLabel;
            if ((catLabels != null) && (!transposeAxes))
            {
                lastLabel = catLabels[catLabels.length-1];
            }
            else
            {
                lastLabel = axisFormatter[axis].format(tics[axis][tics[axis].length-1]);
            }
            extraRightBorder += parent.textWidth(lastLabel)/2f;
        }
        
        // Allow space above the vertical axis to accommodate the top tic label.
        if ((getShowAxis(1)) || ((transposeAxes) && (getShowAxis(0))))
        {   
            extraTopBorder += parent.textAscent()/2f+2;
        }
        
        // Allow space to the left of the vertical axis to accommodate its label.
        if (((valueLabel != null) && getShowAxis(1)) || ((transposeAxes) && (categoryLabel != null) && getShowAxis(0)))
        {
            extraLeftBorder += parent.textAscent()+parent.textDescent();
        }
        
        // Allow space below the horizontal axis to accommodate its label.
        if (((categoryLabel != null) && getShowAxis(0)) || ((transposeAxes) && (valueLabel != null) && getShowAxis(1)))
        {
            extraBottomBorder +=parent.textAscent()+parent.textDescent();
        }  
        
        float left   = getBorder(Side.LEFT) + extraLeftBorder;
        float right  = width - (getBorder(Side.RIGHT)+extraRightBorder);
        float bottom = height-(getBorder(Side.BOTTOM)+extraBottomBorder);
        float top    = getBorder(Side.TOP)+extraTopBorder;
        float hRange = right-left;
        float vRange = bottom-top;
        float axisValue;
        if (catAxisPosition == null)
        {
            // Default value axis is at the lowest value.
            axisValue = getMin(1);
        }
        else
        {
            axisValue = catAxisPosition.floatValue();
        }
        
                
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
        //TODO: Allow axis position to be set.
    
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
                    parent.rect(left+(hRange*(axisValue-getMin(1))/(getMax(1)-getMin(1))), top + i*(barWidth+barGap), hRange*(data[1][index]-axisValue)/(getMax(1)-getMin(1)),barWidth);
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
                    parent.rect(left + i*(barWidth+barGap), bottom-(vRange*(axisValue-getMin(1))/(getMax(1)-getMin(1))), barWidth, -vRange*(data[1][index]-axisValue)/(getMax(1)-getMin(1)));
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
            
            // Draw axis label if requested.
            if (valueLabel != null)
            {
                if (transposeAxes)
                {
                    parent.textAlign(PConstants.CENTER,PConstants.TOP);
                    parent.text(valueLabel,(left+right)/2f,bottom+getBorder(Side.BOTTOM)+2);
                }
                else
                {
                    parent.textAlign(PConstants.CENTER,PConstants.BOTTOM);
                    // Rotate label.
                    parent.pushMatrix();
                     parent.translate(left-(getBorder(Side.LEFT)+1),(top+bottom)/2f);
                     parent.rotate(-PConstants.HALF_PI);
                     parent.text(valueLabel,0,0);
                    parent.popMatrix();
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
            
            // Draw axis label if requested
            if (categoryLabel != null)
            {
                if (transposeAxes)
                {
                    parent.textAlign(PConstants.CENTER,PConstants.BOTTOM);
                    // Rotate label.
                    parent.pushMatrix();
                     parent.translate(left-(getBorder(Side.LEFT)+1),(top+bottom)/2f);
                     parent.rotate(-PConstants.HALF_PI);
                     parent.text(categoryLabel,0,0);
                    parent.popMatrix();
                }
                else
                {
                    parent.textAlign(PConstants.CENTER,PConstants.TOP);
                    parent.text(categoryLabel,(left+right)/2f,bottom+getBorder(Side.BOTTOM)+2);
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
    
    /** Sets the position of the category axis. Note that this position will be somewhere on along
     *  the value range.
     *  @param value Position of axis in data units.
     */
    public void setCategoryAxisAt(float value)
    {
        this.catAxisPosition = new Float(value);

        // Update range if the axis lies outside of existing range.
        // Note that the category axis is placed somewhere on the value range.
        if (value < getMin(1))
        {
            setMinValue(value);
        }
        else if (value >getMax(1))
        {
            setMaxValue(value);
        }
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
        
        if (transposeAxes)
        {
            // Bar labels are up the side.
            for (String label : labels)
            {
                border = Math.max(border, parent.textWidth(label));
            }
            setMinBorder(border+2,Side.LEFT);
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
        super.showAxis(0,showAxis,transposeAxes?Side.LEFT:Side.BOTTOM);
        
        if (showAxis && showLabels)
        {
            // Need to recalculate space for labels if they are being made to reappear.
            setBarLabels(catLabels);
        }
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
    
    /** Sets the category axis label. If null, no label is drawn.
     *  @param label Category axis label to draw or null if no label to be drawn.
     */
    public void setCategoryAxisLabel(String label)
    {
        this.categoryLabel = label;
    }
    
    /** Sets the value axis label. If null, no label is drawn.
     *  @param label Value-axis label to draw or null if no label to be drawn.
     */
    public void setValueAxisLabel(String label)
    {
        this.valueLabel = label;
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