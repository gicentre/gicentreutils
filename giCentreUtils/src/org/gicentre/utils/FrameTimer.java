package org.gicentre.utils;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

// *****************************************************************************************
/** Class for displaying frame rates in a sketch. To use, declare a <code>FrameTimer</code> 
 *  object at the top of a sketch, initialise it in the <code>setup()</code> method, and 
 *  call one and only one of the methods for reporting frame rates (<code>displayFrameRate()</code>,
 *  <code>getFrameRate()</code>, <code>getFrameRateAsText()</code>) in the <code>draw()</code> 
 *  method. Frame rates can be reported either every <i>n</i> frames or every <i>t</i> seconds
 *  depending on which constructor is used.
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

public class FrameTimer
{
	// -------------------------------- Object Variables ---------------------------------  

	private long thisTime,lastTime; 	// Two points in time over which to measure frame rate.
	private int lastFrameCount;			// Previous frame count (used by timer only).
	private int frameCounter;       	// Keeps track of the the number of frames drawn.
	private int reportRateInFrames; 	// Number of frames to skip before reporting frame rate.
	private float reportRateInSeconds;	// Number of seconds to skip before reporting frame rate.
	private DecimalFormat rounder;  	// For rounded decimal output.
	private long elapsedTime;			// Time since starting timer.
	private String formattedFPS;		// Textual representation of the FPS.
	private float fps;					// Numerical representation of the FPS.
	private boolean displayResult;		// Indicates if we need to report a frame rate to the console.

	// ----------------------------------- Constructor ----------------------------------- 

	/** Creates a timer that will report the frame rate once in every 50 frames.
	 */
	public FrameTimer()
	{
		this(50);
	}

	/** Creates a timer that will report the frame rate once in every <code>reportRateInFrames</code> frames.
	 *  @param reportRateInFrames Number of frames to elapse before recalculation of the current frame rate. 
	 */
	public FrameTimer(int reportRateInFrames)
	{
		this.reportRateInFrames = reportRateInFrames;
		this.reportRateInSeconds = -1;
		rounder  = new DecimalFormat("#,##0.0");
		thisTime = System.currentTimeMillis();
		lastTime = thisTime;
		frameCounter   = 0;
		lastFrameCount = -1;
		elapsedTime    = 0;
		formattedFPS   = "";
		fps            = 0;
		displayResult  = false;
	}

	/** Creates a timer that will report the frame rate once in every <code>reportRateInSeconds</code> seconds.
	 *  @param delay Number of seconds delay before the first frame rate calculation. This can be useful if you
	 *               wish to ignore frame rates during the initialisation part of a sketch.
	 *  @param reportRateInSeconds Number of seconds between recalculations of the current frame rate. 
	 */
	public FrameTimer(float delay, float reportRateInSeconds)
	{
		this.reportRateInSeconds = reportRateInSeconds;
		this.reportRateInFrames = -1;
		rounder  = new DecimalFormat("#,##0.0");
		thisTime = System.currentTimeMillis();
		lastTime = thisTime;
		frameCounter   = 0;
		lastFrameCount = -1;
		elapsedTime    = 0;
		formattedFPS   = "";
		fps            = 0;
		displayResult  = false;
		
		Timer timedCounter = new Timer();
		timedCounter.schedule(new TimedFrameCounter(), Math.round(delay*1000), Math.round(reportRateInSeconds*1000));
	}

	// ------------------------------------ Methods -------------------------------------  

	/** Displays the current frame rate. To use this method, it should be called every time a new frame
	 *  is drawn, but will only display results one in every <i>n</i> frames or once in every <i>t</i> 
	 *  seconds depending on the constructor used. Note that if using a 'by-frame' counter, you should 
	 *  not call this in combination with any of the other frame reporting methods in the same draw() loop
	 *  as this would over-estimate the actual frame rate.
	 */
	public void displayFrameRate()
	{
		if ((reportRateInFrames >0) && (reportRateInSeconds < 0))
		{
			// If we are doing frame-based reporting, update fps after reportRateInFrames calls to this method.
			frameCounter++;
			if (frameCounter%reportRateInFrames==0)
			{ 
				lastTime = thisTime; 
				thisTime = System.currentTimeMillis(); 
				fps = (reportRateInFrames*1000f)/(1+thisTime-lastTime);
				System.out.println(rounder.format(fps)+" frames per second.");
			}
		}
		else
		{
			// If we are doing time-based reporting, fps is updated by the timer, so request result to be displayed by the timer.
			displayResult = true;
		}
	}

	/** Reports the current frame rate as formatted text. To use this method, it should be called every time a
	 *  new frame is drawn, but will only update results one in every <i>n</i> frames or once in every <i>t</i> 
	 *  seconds depending on the constructor used. Note that if using a 'by-frame' counter, you should 
	 *  not call this in combination with any of the other frame reporting methods in the same draw() loop
	 *  as this would over-estimate the actual frame rate.
	 *  @return String reporting the frame rate in frames per second.
	 */
	public String getFrameRateAsText()
	{
		if ((reportRateInFrames >0) && (reportRateInSeconds < 0))
		{
			frameCounter++;
			// If we are doing frame-based reporting, update fps after reportRateInFrames calls to this method.
			if (frameCounter%reportRateInFrames==0)
			{ 
				lastTime = thisTime; 
				thisTime = System.currentTimeMillis();
				fps = (reportRateInFrames*1000f)/(1+thisTime-lastTime);
				formattedFPS = new String(rounder.format(fps));
			}
		}		
		return formattedFPS;
	}
	
	/** Reports the current frame rate as a number. To use this method, it should be called every time a
	 *  new frame is drawn, but will only update results one in every <i>n</i> frames or once in every <i>t</i> 
	 *  seconds depending on the constructor used. Note that if using a 'by-frame' counter, you should 
	 *  not call this in combination with any of the other frame reporting methods in the same draw() loop
	 *  as this would over-estimate the actual frame rate.
	 *  @return Number of frames per second.
	 */
	public float getFrameRate()
	{
		if ((reportRateInFrames >0) && (reportRateInSeconds < 0))
		{
			frameCounter++;
			// If we are doing frame-based reporting, update fps after reportRateInFrames calls to this method.
			if (frameCounter%reportRateInFrames==0)
			{ 
				lastTime = thisTime; 
				thisTime = System.currentTimeMillis();
				fps = (reportRateInFrames*1000f)/(1+thisTime-lastTime);
			}
		}		
		return fps;
	}

	/** Updates the frame counter. This method should only be called if a frame counter that reports by time
	 *  is to be used. If so, it should be called once per draw() cycle. There is no need to call this method
	 *  if frame rate reporting is on a 'per-frame' basis.
	 */
	public void update()
	{
		if ((reportRateInFrames <0) && (reportRateInSeconds >0))
		{
			frameCounter++;
		}
	}

	/** Starts a timer that can be used for timing specific parts of a program. Call <code>getElapsedTime</code> 
	 *  to retrieve the number of seconds since a call was made to this method. 
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

	// ------------------------------------ Nested classes -------------------------------------

	class TimedFrameCounter extends TimerTask
	{
		@SuppressWarnings("synthetic-access")
		public void run()
		{
			lastTime = thisTime; 
			thisTime = System.currentTimeMillis();
			fps = 1000f*(frameCounter-lastFrameCount)/(1+thisTime-lastTime);
			formattedFPS = rounder.format(fps);
			lastFrameCount = frameCounter;
			
			if (displayResult)
			{
				System.out.println(formattedFPS+" frames per second.");
				displayResult = false;
			}
		}
	}
}