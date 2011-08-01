package org.gicentre.utils;

import java.text.DecimalFormat;

// *****************************************************************************************
/** Class for displaying frame rates in a sketch. To use, declare a <code>FrameTimer</code> 
 *  object at the top of a sketch, initialise it in the <code>setup()</code> method, and 
 *  call <code>displayFrameRate()</code> in the <code>draw()</code> method. 
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

public class FrameTimer
{
    // -------------------------------- Object Variables ---------------------------------  
    
    private long thisTime,lastTime; // Two points in time over which to measure frame rate.
    private int frameCounter;       // Keeps track of the the number of frames drawn.
    private int reportRate;         // Number of frames to skip before reporting frame rate.
    private DecimalFormat rounder;  // For rounded decimal output.
    private long elapsedTime;
    
    // ----------------------------------- Constructor ----------------------------------- 
    
    /** Creates a timer that will report the frame rate once in every 50 frames.
     */
    public FrameTimer()
    {
        this(50);
    }
    
    /** Creates a timer that will report the frame rate once in every <code>reportRate</code>
     *  frames.
     *  @param reportRate Number of frames to elapse before reporting current frame rate. 
     */
    public FrameTimer(int reportRate)
    {
        this.reportRate = reportRate;
        rounder = new DecimalFormat("#,##0.0");
        thisTime = System.currentTimeMillis();
        lastTime = thisTime;
        frameCounter = 0;
        elapsedTime = 0;
    }
    
    // ------------------------------------ Methods -------------------------------------  
    
    /** Displays the current frame rate. This method must be called every time a new frame
      * is drawn, but will only display results one in every <code>reportRate</code> frames. 
      */
    public void displayFrameRate()
    {
        frameCounter++;
        
        if(frameCounter%reportRate==0)
        { 
            lastTime = thisTime; 
            thisTime = System.currentTimeMillis(); 
            System.out.println(rounder.format((reportRate*1000)/(thisTime-lastTime))+" frames per second.");
        }
    }
    
    /** Starts a timer that can be used for timing specific parts of a programming. Call
      * <code>getElapsedTime</code> to retrieve the number of seconds since a call was made
      * to this method. 
      */
    public void startTimer()
    {
        elapsedTime = System.currentTimeMillis();
    }
    
    /** Retrieves the time since the last call to <code>startTimer()</code>
      * @return Time in seconds since <code>startTimer</code> was last called, or 0 if it has not been called. 
      */
    public float getElapsedTime()
    {
        if (elapsedTime == 0)
        {
            return 0f;
        }
        return (System.currentTimeMillis()-elapsedTime)/1000f;
    }
}