package org.gicentre.utils.stat;

import java.text.DecimalFormat;
import java.util.ArrayList;

import processing.core.PApplet;

//  ****************************************************************************************
/** Abstract class for representing a statistical chart. This class provides the core 
 *  functionality common to all charts. A chart will contain a set of axes each corresponding
 *  to a set of data. The way in which each axis/data set is displayed will depend on the 
 *  nature of the chart represented by the subclass.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.1, 7th December, 2010. 
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

public abstract class AbstractChart
{
    // ----------------------------- Object variables ------------------------------

                        /** Parent sketch in which this chart is to be drawn.*/
    protected PApplet parent;
                             
                        /** The datasets to be charted. */
    protected float[][] data;
                        /** Tic mark values for optional axis display. */
    protected float[][] tics,logTics;                     
                                                  
                        /** Determines if the two primary axes should be transposed.*/
    protected boolean transposeAxes;              
                        /** For numerical formatting of axis labels. */
    protected DecimalFormat[] axisFormatter; 
    
                        /** Indicates a side of the chart */
    protected enum Side { TOP, BOTTOM, LEFT, RIGHT, NO_SIDE}
    
    
    private float minBorder;    // Minimum internal border between bounds and chart.
    private float borderL,borderR,borderT,borderB;  // Actual internal borders between bounds and chart.
    private float minBorderL,minBorderR,minBorderT,minBorderB;
    
    private boolean[] isLogScale;   // Indicates whether or not data are shown on log scale.
        
    private float[] min,max;    // Minimum and maximum values for data on each axis.
    private float[] minLog,maxLog;
        
                                // Determines if minimum and maximum values are to be 
                                // set explicitly (true) or by the data (false).
    private boolean[] forceMin,forceMax;
    
    private boolean[] showAxis; // Determines whether a chart axis is to be shown.
    private Side[] axisPositions;   // Position of axes.
    
    private static final int MAX_DIMENSIONS = 20; // Maximum number of dimensions represented by chart.

    // ------------------------------- Constructors --------------------------------

    /** Initialises the chart settings. Subclasses should normally call this constructor as a 
     *  convenience to initialise properties common to all charts.
     *  @param parent Sketch in which this chart is to appear.
     */
    protected AbstractChart(PApplet parent)
    {
        this.parent = parent;
        
        data           = new float[MAX_DIMENSIONS][];
        tics           = new float[MAX_DIMENSIONS][];
        logTics        = new float[MAX_DIMENSIONS][];
        
        min            = new float[MAX_DIMENSIONS];
        max            = new float[MAX_DIMENSIONS];
        minLog         = new float[MAX_DIMENSIONS];
        maxLog         = new float[MAX_DIMENSIONS];
        isLogScale     = new boolean[MAX_DIMENSIONS];

        forceMin       = new boolean[MAX_DIMENSIONS];
        forceMax       = new boolean[MAX_DIMENSIONS];
        showAxis       = new boolean[MAX_DIMENSIONS];
        
        axisPositions  = new Side[MAX_DIMENSIONS];
        axisFormatter  = new DecimalFormat[MAX_DIMENSIONS];
        
        
        transposeAxes = false;
        minBorder      = 1;
        borderL        = minBorder;
        borderR        = minBorder;
        borderT        = minBorder;
        borderB        = minBorder;
        minBorderL     = minBorder;
        minBorderR     = minBorder;
        minBorderT     = minBorder;
        minBorderB     = minBorder;
        
        for (int i=0; i<MAX_DIMENSIONS; i++)
        {
            forceMin[i]      = false;
            forceMax[i]      = false;
            showAxis[i]      = false;
            isLogScale[i]    = false;
            axisPositions[i] = Side.NO_SIDE;
            axisFormatter[i] = new DecimalFormat("###,###,###.######");    
        }
    }

    // ---------------------------------- Methods ----------------------------------

    /** Should draw the chart within the given bounds. All implementing classes must include
     *  this method do do the drawing.
     *  @param xOrigin left-hand pixel coordinate of the area in which to draw the chart.
     *  @param yOrigin top pixel coordinate of the area in which to draw the chart.
     *  @param width Width in pixels of the area in which to draw the chart.
     *  @param height Height in pixels of the area in which to draw the chart.
     */
    protected abstract void draw(float xOrigin, float yOrigin, float width, float height);
  
    /** Sets the data to be displayed along the given axis of the chart. Updates the min and max ranges
     *  in response to the new data.
     *  @param dimension Dimension of the data to add.
     *  @param data Collection of data items to represent in the chart.
     */
    protected void setData(int dimension, float[] data)
    {
        if (dimension >= MAX_DIMENSIONS)
        {
            System.err.println("Warning: Cannot set data for dimension "+dimension+": permissable range 0-"+(MAX_DIMENSIONS-1));
            return;
        }
        this.data[dimension] = data;
        updateChart(dimension);
    }
    
    /** Reports the data to be displayed along the given axis of the chart. 
     *  @param dimension Dimension of the data to report.
     *  @return data Collection of data items represented in the chart or null if no data exist in the given dimension.
     */
    protected float[] getData(int dimension)
    {
        if (dimension >= MAX_DIMENSIONS)
        {
            System.err.println("Warning: Cannot get data for dimension "+dimension+": permissable range 0-"+(MAX_DIMENSIONS-1));
            return null;
        }
        return data[dimension];
    }
        
    /** Sets the minimum and maximum values of the data to be charted on the axis of the given dimension.
     *  If either the min or max values given are <code>Float.NaN</code>, then the minimum or maximum
     *  value respectively will be set to that of the min/max data items in the given dimension.
     *  @param dimension Dimension of the data whose minimum value is to be set.
     *  @param min Minimum value to be represented on the axis or <code>Float.NaN</code> for natural data minimum.
     *  @param max Maximum value to be represented on the axis or <code>Float.NaN</code> for natural data maximum..
     */
    protected void setRange(int dimension, float min, float max)
    {
        if (Float.isNaN(min))
        {
            forceMin[dimension] = false;
        }
        else
        {
            forceMin[dimension] = true;
            this.min[dimension] = min;
        }

        if (Float.isNaN(max))
        {
            forceMax[dimension] = false;
        }
        else
        {
            forceMax[dimension] = true;
            this.max[dimension] = max;     
        }
        updateChart(dimension);
    }
    
    /** Sets the minimum value of the data to be charted on the axis of the given dimension.
     *  If the given value is <code>Float.NaN</code>, then the minimum value will be set to 
     *  the minimum of the data items in the given dimension.
     *  @param dimension Dimension of the data whose minimum value is to be set.
     *  @param min Minimum value to be represented on the axis or <code>Float.NaN</code> if data
     *             minimum is to be used.
     */
    protected void setMin(int dimension, float min)
    {
        if (Float.isNaN(min))
        {
            forceMin[dimension] = false;
        }
        else
        {
            forceMin[dimension] = true;
            this.min[dimension] = min;
        }
        updateChart(dimension);
    }
    
    /** Reports the minimum value of the data to be charted on the axis of the given dimension.
     *  @param dimension Dimension of the data whose minimum value is to be retrieved.
     *  @return Minimum value to be represented on the axis.
     */
    protected float getMin(int dimension)
    {
       return min[dimension];
    }
    
    /** Reports the maximum value of the data to be charted on the axis of the given dimension.
     *  @param dimension Dimension of the data whose maximum value is to be retrieved.
     *  @return Maximum value to be represented on the axis.
     */
    protected float getMax(int dimension)
    {
       return max[dimension];
    }
    
    /** Reports the minimum value of the log10 of the data to be charted on the axis of the given dimension.
     *  @param dimension Dimension of the data whose minimum value is to be retrieved.
     *  @return Minimum value to be represented on the axis assuming log scaling.
     */
    protected float getMinLog(int dimension)
    {
       return minLog[dimension];
    }
    
    /** Reports the maximum value of the log10 of the data to be charted on the axis of the given dimension.
     *  @param dimension Dimension of the data whose maximum value is to be retrieved.
     *  @return Maximum value to be represented on the axis assuming log scaling.
     */
    protected float getMaxLog(int dimension)
    {
       return maxLog[dimension];
    }
    
    /** Sets the maximum value of the data to be charted on the axis of the given dimension.
     *  If the given value is <code>Float.NaN</code>, then the maximum value will be set to 
     *  the maximum of the data items in the given dimension.
     *  @param dimension Dimension of the data whose maximum value is to be set.
     *  @param max Maximum value to be represented on the axis or <code>Float.NaN</code> if data
     *             minimum is to be used.
     */
    protected void setMax(int dimension, float max)
    {
        if (Float.isNaN(max))
        {
            forceMax[dimension] = false;
        }
        else
        {
            forceMax[dimension] = true;
            this.max[dimension] = max;
        }
       updateChart(dimension);
    }
    
    /** Sets the minimum internal border between the edge of the graph and the drawing area.
     *  @param border Minimum internal border size in pixel units.
     */
    protected void setMinBorder(float border)
    {
        minBorder = border;
    }
    
    /** Sets the minimum internal border between the given edge of the chart and the drawing area.
     *  If the given value is less than that given to the no-arguments method <code>setMinBorder()</code>,
     *  then this method has no effect.
     *  @param border Border at the given side in pixel units.
     *  @param side Side of the chart to set the minimum border size.
     */
    protected void setMinBorder(float border, Side side)
    {
        switch (side)
        {
            case TOP:
                minBorderT = Math.max(minBorder,border);
                break;
            case BOTTOM:
                minBorderB = Math.max(minBorder,border);
                break;
            case LEFT:
                minBorderL = Math.max(minBorder,border);
                break;
            case RIGHT:
                minBorderR = Math.max(minBorder,border);
                break;
            default:
                // Do nothing.
        }
    }
    
    /** Sets the internal border between the given edge of the chart and the drawing area.
     *  This method is used for explicit setting of border dimensions and will also reset
     *  the minimum border to the given dimension.
     *  @param border Border at the given side in pixel units.
     *  @param side Side of the chart to set the border size.
     */
    protected void setBorder(float border, Side side)
    {
        switch (side)
        {
            case TOP:
                borderT = border;
                minBorderT = border;
                break;
            case BOTTOM:
                borderB = border;
                minBorderB = border;
                break;
            case LEFT:
                borderL = border;
                minBorderL = border;
                break;
            case RIGHT:
                borderR = border;
                minBorderR = border;
                break;
            default:
                // Do nothing.
        }
    }
     
    /** Reports the minimum internal border between the chart and the drawing area.
     *  @return Minimum border between chart and drawing area in pixel units.
     */
    protected float getMinBorder()
    {
        return minBorder;
    }
    
    /** Reports the internal border between the given edge of the chart and the drawing area.
     *  This value is at least <code>getMinBorder()</code> and large enough to accommodate any axis labelling.
     *  @param side Side of the chart to query.
     *  @return Border at the given side in pixel units.
     */
    protected float getBorder(Side side)
    {
        switch (side)
        {
            case TOP:
                return Math.max(minBorderT,borderT);
            case BOTTOM:
                return Math.max(minBorderB,borderB);
            case LEFT:
                return Math.max(minBorderL,borderL);
            case RIGHT:
                return Math.max(minBorderR,borderR);
            default:
                return 0;
        }
    }
    
    /** Determines whether or not the axis representing the given dimension is drawn.
     *  @param dimension Dimension of the data to have axis displayed or hidden.
     *  @param isVisible Axis is drawn if true.
     *  @param side Side of chart along which axis is drawn.
     */
    protected void showAxis(int dimension, boolean isVisible, Side side)
    {
        if (this.showAxis[dimension] != isVisible)
        {
            this.showAxis[dimension] = isVisible;
            if (isVisible)
            {
                axisPositions[dimension] = side;
            }
            else
            {
                axisPositions[dimension] = Side.NO_SIDE;
            }

            if (side == Side.TOP)
            {
                borderT    = minBorder;
                minBorderT = minBorder;
                if (isVisible)
                {
                    //  Update the border to accommodate labels assuming horizontal text.
                    borderT = Math.max(borderT,parent.textAscent()+parent.textDescent());
                }
            }
            else if (side == Side.BOTTOM)
            {
                borderB    = minBorder;
                minBorderB = minBorder;
                if (isVisible)
                {
                    //  Update the border to accommodate labels assuming horizontal text.
                    borderB = Math.max(borderB,parent.textAscent()+parent.textDescent());
                }
            }
            else if (side == Side.LEFT)
            {
                
                //System.err.println("Finding left border using dimension "+dimension);
                
                borderL    = minBorder;
                minBorderL = minBorder;
                if (isVisible)
                {
                    //  Update the border to accommodate largest label assuming horizontal text.
                    for (float tic : tics[dimension])
                    {
                        borderL = Math.max(borderL, parent.textWidth(axisFormatter[dimension].format(tic)));
                    }
                }
            }
            else if (side == Side.RIGHT)
            {
                borderR    = minBorder;
                minBorderR = minBorder;
                if (isVisible)
                {
                    //  Update the border to accommodate largest label assuming horizontal text.
                    for (float tic : tics[dimension])
                    {
                        borderR = Math.max(borderR, parent.textWidth(axisFormatter[dimension].format(tic)));
                    }
                }
            }
           
            // Rounding of axis values may increase range.
            updateChart(dimension);
        }
    }
    
    /** Reports whether or not the axis representing the given dimension is drawn.
     *  @param dimension Dimension of the data to query.
     *  @return True if given axis dimension is drawn.
     */
    protected boolean getShowAxis(int dimension)
    {
        if (dimension >= MAX_DIMENSIONS)
        {
            return false;
        }
        return showAxis[dimension];
    }
    
    /** Reports whether or not the data in the given dimension are to be represented on the log10 scale.
     *  @param dimension Dimension of the data to query.
     *  @return True if the data in the given dimension are to be log-scaled.
     */
    protected boolean getIsLogScale(int dimension)
    {
        if (dimension >= MAX_DIMENSIONS)
        {
            return false;
        }
        return isLogScale[dimension];
    }
    
    /** Determines whether or not the data in the given dimension are to be represented on the log10 scale.
     *  @param dimension Dimension of the data to set.
     *  @param isLog True if the data in the given dimension are to be log-scaled or false if linear.
     */
    protected void setIsLogScale(int dimension, boolean isLog)
    {
        if (dimension >= MAX_DIMENSIONS)
        {
            return;
        }
        isLogScale[dimension] = isLog;
    }
    
    /** Sets the numerical format for numbers shown on the axis of the given dimension.
     *  @param dimension Dimension of the data axis to format.
     *  @param format Format for numbers on the given data axis.
     */
    protected void setFormat(int dimension, String format)
    {
        axisFormatter[dimension] = new DecimalFormat(format);
        
        if (showAxis[dimension])
        {
            // Only left or right borders can be affected by changes in label format (assuming horizontal text).
            if (axisPositions[dimension] == Side.LEFT)
            {
               
                borderL = minBorderL;
                for (float tic : tics[dimension])
                {
                    borderL = Math.max(borderL, parent.textWidth(axisFormatter[dimension].format(tic)));
                }
            }
            else if (axisPositions[dimension] == Side.RIGHT)
            {
                borderR = minBorderR;
                for (float tic : tics[dimension])
                {
                    borderR = Math.max(borderR, parent.textWidth(axisFormatter[dimension].format(tic)));
                }
            }
        }
    }
    
    /** Converts the given value, which is assumed to be positive, to a log value
     *  scaled between 0 and 1
     *  @param dataItem Item from which to find log value.
     *  @param minLogValue Minimum value of the log10 of dataItem (used to scale result between 0-1)
     *  @param maxLogValue Maximum value of the log10 of dataItem (used to scale result between 0-1)
     *  @return Log-scaled equivalent of the given value.
     */
    protected static float convertToLog(double dataItem, double minLogValue, double maxLogValue)
    {
        if (dataItem <= 0)
        {
            return 0;
        }
        return (float)((Math.log10(dataItem)-minLogValue)/(maxLogValue-minLogValue));
    }
    
    /** Converts the given value assumed to be on a log scale between 0 and 1, to an non-log value. 
     *  @param logValue 0-1 log value from which the data value is to be found. If this value is
     *                  outside the 0-1 range, a value of 0 will be returned.
     *  @param minLogValue Minimum value of the log10 of dataItem (used to unscale the logValue from 0-1)
     *  @param maxLogValue Maximum value of the log10 of dataItem (used to unscale the logValue from 0-1)
     *  @return Data value that would have produced the given scaled log value within the given range.
     */
    protected static float convertFromLog(double logValue, double minLogValue, double maxLogValue)
    {
        if ((logValue < 0) || (logValue >1))
        {
            return 0;
        }
        
        double unscaledLog = logValue*(maxLogValue-minLogValue) + minLogValue;
        return (float)Math.pow(10,unscaledLog);
    }
   
    // ------------------------------ Private methods ------------------------------

    /** Updates the scaling and labelling of the chart depending on the values to be represented. 
     *  This method should be called whenever the range of data to be represented have changed.
     *  @param dimension Index referring to the data dimension to update.
     */
    private void updateChart(int dimension)
    {
        // Update the range of values of dataset if it exists.
        if ((data[dimension] != null) && (data[dimension].length > 0))
        {                                   
            if (forceMin[dimension] == false)
            {
                min[dimension]    = Float.MAX_VALUE;  
                minLog[dimension] = Float.MAX_VALUE;
                
                for (float dataItem : data[dimension])
                {
                    min[dimension] = Math.min(min[dimension], dataItem);
                }
            }
            else
            {
                minLog[dimension] = (float)Math.log10(Math.max(Math.min(0.001,max[dimension]/1000.0), min[dimension]));  
                //System.err.println("Min x forced to be "+min[dimension]+" with min log at "+minLog[dimension]);
            }

            if (forceMax[dimension] == false)
            {
                max[dimension]    = -Float.MAX_VALUE;
                maxLog[dimension] = -Float.MAX_VALUE;
                
                for (float dataItem : data[dimension])
                {
                    max[dimension] = Math.max(max[dimension], dataItem);
                }
            }
            else
            {
                maxLog[dimension] = (float)Math.log10(max[dimension]);
            }

            

            tics[dimension] = getTics(min[dimension], max[dimension]);
            
            //System.err.println("** Min log:"+minLog[dimension]);    
            logTics[dimension] = getLogTics(Math.pow(10,minLog[dimension]), max[dimension]);

            if (showAxis[dimension])
            {
                for (float tic : tics[dimension])
                {
                    if (forceMax[dimension] == false)
                    {
                        max[dimension] = Math.max(max[dimension], tic);
                    }
                    if (forceMin[dimension] == false)
                    {
                        min[dimension] = Math.min(min[dimension], tic);
                    }
                }
                
                for (float tic : logTics[dimension])
                {
                    if (forceMax[dimension] == false)
                    {
                        maxLog[dimension] = Math.max(maxLog[dimension], tic);
                    }
                    if (forceMin[dimension] == false)
                    {
                        minLog[dimension] = Math.min(minLog[dimension], tic);
                    }
                }
            }
        }
    }

    /** Provides an array of tic marks at rounded intervals between the given minimum and maximum values.
     *  @param minVal Minimum value to be represented within the tic marks.
     *  @param maxVal Maximum value to be represented within the tic marks.
     *  @return Array of tic mark positions.
     */
    private static float[] getTics(double minVal, double maxVal)
    {
        float spacing = findSpacing(minVal, maxVal);
        float minTic = (float)Math.floor(minVal/spacing)*spacing;
        float tic = minTic;
        int numTics = 0;

        while (tic < maxVal)
        {
            tic = minTic + numTics*spacing;
            numTics++;
        }

        float[] tics = new float[numTics];
        for (int i=0; i<numTics; i++)
        {
            tics[i] = minTic + i*spacing;
        }
        return tics;
    }
    
    /** Provides an array of tic marks at rounded intervals on a log scale between the given minimum and maximum values.
     *  @param minVal Minimum value to be represented within the tic marks.
     *  @param maxVal Maximum value to be represented within the tic marks.
     *  @return Array of tic mark positions.
     */
    private static float[] getLogTics(double minVal, double maxVal)
    {
        
        // Find decade of smallest value
        int minDecade = (int)Math.round(Math.log10(minVal)-0.5);
        int maxDecade = (int)Math.round(Math.log10(maxVal)+0.5);
        double maxLog = Math.log10(maxVal);
        float[] mult;
        
        int range = maxDecade-minDecade;
        if (range < 3)
        {
            mult = new float[] {1, 2, 5};
        }
        else if (range < 5)
        {
            mult = new float[] {1, 5};
        }
        else
        {
            mult = new float[] {1};
        }
         
        //System.err.println("Min decade: "+Math.pow(10,minDecade)+ " max decade: "+Math.pow(10, maxDecade)+" range from "+minVal+" to "+maxVal);
        //int numTics = (range+1)*mult.length;
        
        ArrayList<Double> tics = new ArrayList<Double>();
        
        for (int i=0; i<=range; i++)
        {
            for (int m=0; m<mult.length; m++)
            {
                double tic = Math.log10(Math.pow(10.0,minDecade+i) * mult[m]);
                if (tic <= maxLog)
                {
                    tics.add(new Double(tic));
                }
            }
        }
        
        float[] ticArray = new float[tics.size()];
        for (int i=0; i<ticArray.length; i++)
        {
            ticArray[i] = tics.get(i).floatValue();
        }
        return ticArray;

        
        

        /*
        System.err.println("Min decade: "+Math.pow(10,minDecade)+" max decade: "+Math.pow(10,maxDecade));
        
        float spacing = findSpacing(minVal, maxVal);
        //float minTic = Math.round(minVal/spacing)*spacing;
        float minTic = (float)minVal;
        
        float tic = minTic;
        int numTics = 0;

        while (tic < maxVal)
        {
            tic = minTic + numTics*spacing;
            numTics++;
        }
        float maxTic = minTic + (numTics-1)*spacing;
 
        if (minTic <=0)
        {
            minTic = (float)(maxTic/Math.pow(10,numTics));
        }
 
        return getTics(Math.log10(minTic), Math.log10(maxTic));
        */
    }

    /** Finds a suitable spacing between the given minimum and maximum values. Used for positioning
     *  tic marks for axis labels.
     *  @param minVal Minimum value to be represented along a chart axis.
     *  @param maxVal Maximum value to be represented along a chart axis.
     *  @return Suitable space between rounded values.
     */
    private static float findSpacing(double minVal, double maxVal)
    {
        double r = maxVal-minVal;
        double newMaxVal = maxVal;
        if (r <= 0)
        {
            newMaxVal = minVal+1;
        }

        int n = (int)Math.floor(Math.log10(r));
        int minInterval =  4;
        int maxInterval = 7;
        double[] mu = new double[] {0.1,0.2,0.5,1,2,5};

        int i=0;
        while (i <mu.length)
        {
            int interval = (int) Math.round(r/(mu[i]*Math.pow(10, n)))+1;
            if ((interval >= minInterval) && (interval <=maxInterval))
            {
                return (float)(mu[i]*Math.pow(10,n));
            }
            i++;
        }
        // Shouldn't get to this line.
        return (float)(newMaxVal-minVal);
    }
}