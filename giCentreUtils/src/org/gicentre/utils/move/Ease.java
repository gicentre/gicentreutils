package org.gicentre.utils.move;

import processing.core.PConstants;

//********************************************************************************************
/** Utility class containing a set of static easing methods. Each takes a value between 0 and
 *  1 and returns a new value also between 0 and 1 representing the 'eased' position. This can
 *  be used with <code>lerp()</code> type functions to give a non-linear scaling between the 
 *  start (0) and end (1) of an interpolated sequence. The most common application will be
 *  in animated transitions where animation speed varies between the start and end points.
 *  @author Jo Wood, giCentre, City University London, based on the Flash examples discussed
 *          by Robert Penner, 2003, 
 *          <a href="http://www.robertpenner.com/easing/penner_chapter7_tweening.pdf" 
 *          target="_blank">Programming Macromedia Flash MX</a>.
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

public class Ease 
{
    /** Private constructor to prevent inadvertent instantiation of this utility class 
     */
    private Ease()
    {
        throw new AssertionError("Cannot create instance of the Ease class.");
    }
    
    // ---------------------------- Utility methods -----------------------------
    
    /** Provides a sinusoidal easing in function. Value starts slowly at t=0 and
     *  accelerates to a maximum value at t=1.
     *  @param t Time value between 0-1.
     *  @return Eased value at the given time step.
     */
    public static float sinIn(float t)
    {
        return sinIn(t,1);
    }
      
    /** Provides a reversible sinusoidal easing in function. Value starts slowly at
     *  t=0 and accelerates to a maximum value at t=1. If the <code>direction</code>
     *  parameter is negative, the direction of the function is reversed. This can 
     *  be useful for oscillating animations.
     *  @param t Time value between 0-1.
     *  @param direction Direction of easing, forward if non-negative, or reverse if negative.
     *  @return Eased value at the given time step.
     */
    public static float sinIn(float t, float direction)
    {
        if (direction < 0)
        {
            // Reverse direction for a return journey.
            return sinOut(t,1);
        }
        return (float)(1-Math.cos(t*PConstants.HALF_PI));
    }

    /** Provides a sinusoidal easing out function. Value starts rapidly at t=0 and
     *  decelerates to a minimum value at t=1.
     *  @param t Time value between 0-1.
     *  @return Eased value at the given time step.
     */
    public static float sinOut(float t)
    {
      return sinOut(t,1);
    }

    /** Provides a reversible sinusoidal easing out function. Value starts rapidly at
     *  t=0 and decelerates to a minimum value at t=1. If the <code>direction</code>
     *  parameter is negative, the direction of the function is reversed. This can 
     *  be useful for oscillating animations.
     *  @param t Time value between 0-1.
     *  @param direction Direction of easing, forward if non-negative, or reverse if negative.
     *  @return Eased value at the given time step.
     */
    public static float sinOut(float t, float direction)
    {
        if (direction<0)
        {
            return sinIn(t,1);
        }
        return (float)Math.sin(t*PConstants.HALF_PI);
    }
    
    /** Provides a sinusoidal easing in and out function. Value starts slowly at t=0, 
     *  accelerates towards t=0.5 and then decelerates towards t=1.
     *  @param t Time value between 0-1.
     *  @return Eased value at the given time step.
     */
    public static float sinBoth(float t)
    {
        return (float)(1-Math.cos(t*PConstants.PI))/2f;
    }
    
    /** Provides a cubic easing in function. Value starts slowly at t=0 and  accelerates 
     *  to a maximum value at t=1.
     *  @param t Time value between 0-1.
     *  @return Eased value at the given time step.
     */
    public static float cubicIn(float t)
    {
        return cubicIn(t,1);
    }
    
    /** Provides a reversible cubic easing in function. Value starts slowly at
     *  t=0 and accelerates to a maximum value at t=1. If the <code>direction</code>
     *  parameter is negative, the direction of the function is reversed. This can 
     *  be useful for oscillating animations.
     *  @param t Time value between 0-1.
     *  @param direction Direction of easing, forward if non-negative, or reverse if negative.
     *  @return Eased value at the given time step.
     */
    public static float cubicIn(float t, float direction)
    {
        if (direction < 0)
        {
            return cubicOut(t,1);
        }
        return t*t*t;
    }

    /** Provides a cubic easing out function. Value starts rapidly at t=0 and
     *  decelerates to a minimum value at t=1.
     *  @param t Time value between 0-1.
     *  @return Eased value at the given time step.
     */
    public static float cubicOut(float t)
    {
      return cubicOut(t,1);
    }
    
    /** Provides a reversible cubic easing out function. Value starts rapidly at
     *  t=0 and decelerates to a minimum value at t=1. If the <code>direction</code>
     *  parameter is negative, the direction of the function is reversed. This can 
     *  be useful for oscillating animations.
     *  @param t Time value between 0-1.
     *  @param direction Direction of easing, forward if non-negative, or reverse if negative.
     *  @return Eased value at the given time step.
     */
    public static float cubicOut(float t, float direction)
    {
        if (direction < 0)
        {
            return cubicIn(t,1);
        } 
        float tPrime = 1-t;
        return 1-tPrime*tPrime*tPrime;
    }

    /** Provides a cubic easing in and out function. Value starts slowly at t=0, 
     *  accelerates towards t=0.5 and then decelerates towards t=1.
     *  @param t Time value between 0-1.
     *  @return Eased value at the given time step.
     */
    public static float cubicBoth(float t)
    {
        if (t < 0.5)
        {
            float tPrime = t*2;
            return 0.5f*tPrime*tPrime*tPrime;
        }
      
        float tPrime = 2-t*2;
        return 0.5f*(2-tPrime*tPrime*tPrime);
    }
    
    /** Provides a quartic easing in function. Value starts slowly at t=0 and  accelerates 
     *  to a maximum value at t=1.
     *  @param t Time value between 0-1.
     *  @return Eased value at the given time step.
     */
    public static float quarticIn(float t)
    {
        return quarticIn(t,1);
    }
    
    /** Provides a reversible quartic easing in function. Value starts slowly at
     *  t=0 and accelerates to a maximum value at t=1. If the <code>direction</code>
     *  parameter is negative, the direction of the function is reversed. This can 
     *  be useful for oscillating animations.
     *  @param t Time value between 0-1.
     *  @param direction Direction of easing, forward if non-negative, or reverse if negative.
     *  @return Eased value at the given time step.
     */
    public static float quarticIn(float t, float direction)
    {
        if (direction < 0)
        {
            return quarticOut(t,1);
        }
        return t*t*t*t;
    }

    /** Provides a quartic easing out function. Value starts rapidly at t=0 and
     *  decelerates to a minimum value at t=1.
     *  @param t Time value between 0-1.
     *  @return Eased value at the given time step.
     */
    public static float quarticOut(float t)
    {
      return quarticOut(t,1);
    }
    
    /** Provides a reversible quartic easing out function. Value starts rapidly at
     *  t=0 and decelerates to a minimum value at t=1. If the <code>direction</code>
     *  parameter is negative, the direction of the function is reversed. This can 
     *  be useful for oscillating animations.
     *  @param t Time value between 0-1.
     *  @param direction Direction of easing, forward if non-negative, or reverse if negative.
     *  @return Eased value at the given time step.
     */
    public static float quarticOut(float t, float direction)
    {
        if (direction < 0)
        {
            return quarticIn(t,1);
        } 
        float tPrime = 1-t;
        return 1-tPrime*tPrime*tPrime*tPrime;
    }

    /** Provides a quartic easing in and out function. Value starts slowly at t=0, 
     *  accelerates towards t=0.5 and then decelerates towards t=1.
     *  @param t Time value between 0-1.
     *  @return Eased value at the given time step.
     */
    public static float quarticBoth(float t)
    {
        if (t < 0.5)
        {
            float tPrime = t*2;
            return 0.5f*tPrime*tPrime*tPrime*tPrime;
        }
      
        float tPrime = 2-t*2;
        return 0.5f*(2-tPrime*tPrime*tPrime*tPrime);
    }
    
    /** Provides a quintic easing in function. Value starts slowly at t=0 and  accelerates 
     *  to a maximum value at t=1.
     *  @param t Time value between 0-1.
     *  @return Eased value at the given time step.
     */
    public static float quinticIn(float t)
    {
        return quinticIn(t,1);
    }
    
    /** Provides a reversible quintic easing in function. Value starts slowly at
     *  t=0 and accelerates to a maximum value at t=1. If the <code>direction</code>
     *  parameter is negative, the direction of the function is reversed. This can 
     *  be useful for oscillating animations.
     *  @param t Time value between 0-1.
     *  @param direction Direction of easing, forward if non-negative, or reverse if negative.
     *  @return Eased value at the given time step.
     */
    public static float quinticIn(float t, float direction)
    {
        if (direction < 0)
        {
            return quinticOut(t,1);
        }
        return t*t*t*t*t;
    }

    /** Provides a quintic easing out function. Value starts rapidly at t=0 and
     *  decelerates to a minimum value at t=1.
     *  @param t Time value between 0-1.
     *  @return Eased value at the given time step.
     */
    public static float quinticOut(float t)
    {
      return quinticOut(t,1);
    }
    
    /** Provides a reversible quintic easing out function. Value starts rapidly at
     *  t=0 and decelerates to a minimum value at t=1. If the <code>direction</code>
     *  parameter is negative, the direction of the function is reversed. This can 
     *  be useful for oscillating animations.
     *  @param t Time value between 0-1.
     *  @param direction Direction of easing, forward if non-negative, or reverse if negative.
     *  @return Eased value at the given time step.
     */
    public static float quinticOut(float t, float direction)
    {
        if (direction < 0)
        {
            return quinticIn(t,1);
        } 
        float tPrime = 1-t;
        return 1-tPrime*tPrime*tPrime*tPrime*tPrime;
    }

    /** Provides a quintic easing in and out function. Value starts slowly at t=0, 
     *  accelerates towards t=0.5 and then decelerates towards t=1.
     *  @param t Time value between 0-1.
     *  @return Eased value at the given time step.
     */
    public static float quinticBoth(float t)
    {
        if (t < 0.5)
        {
            float tPrime = t*2;
            return 0.5f*tPrime*tPrime*tPrime*tPrime*tPrime;
        }
      
        float tPrime = 2-t*2;
        return 0.5f*(2-tPrime*tPrime*tPrime*tPrime*tPrime);
    }
    
    /** Provides a parabolic bouncing easing in function. From t=0 value starts with a small
     *  'bounce' that gets larger towards t=1.
     *  @param t Time value between 0-1.
     *  @return Eased value at the given time step.
     */
    public static float bounceIn(float t)
    {
        return bounceIn(t,1);
    }

    /** Provides a reversible parabolic bouncing easing in function. From t=0 value starts
     *  with a small 'bounce' that gets larger towards t=1. If the <code>direction</code>
     *  parameter is negative, the direction of the function is reversed. This can 
     *  be useful for oscillating animations.
     *  @param t Time value between 0-1.
     *  @param direction Direction of easing, forward if non-negative, or reverse if negative.
     *  @return Eased value at the given time step.
     */
    public static float bounceIn(float t, float direction)
    {
        if (direction < 0)
        {
            // Reverse direction for a return journey.
            return bounceOut(t);
        }
      
        float tPrime = 1-t;
      
        if (tPrime < 0.36364)            // 1/2.75
        {
            return 1- 7.5625f*tPrime*tPrime;
        }
        
        if (tPrime < 0.72727)            // 2/2.75
        {
            return 1- (7.5625f*(tPrime-=0.545454f)*tPrime + 0.75f);
        }
        
        if (tPrime < 0.90909)            // 2.5/2.75
        {
            return 1- (7.5625f*(tPrime-=0.81818f)*tPrime + 0.9375f);
        }
        
        return 1- (7.5625f*(tPrime-=0.95455f)*tPrime + 0.984375f); 
    }
    

    /** Provides a parabolic bouncing easing out function. From t=0 value starts with an
     *  accelerating motion until destination reached then it bounces back in increasingly
     *  small bounces finally settling at 1 when t=1.
     *  @param t Time value between 0-1.
     *  @return Eased value at the given time step.
     */
    public static float bounceOut(float t)
    {
        return bounceOut(t,1);
    }
      
    /** Provides a reversible parabolic bouncing easing out function. From t=0 value starts with
     *  an accelerating motion until destination reached then it bounces back in increasingly
     *  small bounces finally settling at 1 when t=1. If the <code>direction</code> parameter is
     *  negative, the direction of the function is reversed. This can be useful for oscillating 
     *  animations.
     *  @param t Time value between 0-1.
     *  @param direction Direction of easing, forward if non-negative, or reverse if negative.
     *  @return Eased value at the given time step.
     */
    public static float bounceOut(float t, float direction)
    {
        if (direction < 0)
        {
            // Reverse direction for a return journey.
            return bounceIn(t);
        }
      
        float tPrime = t;
        
        if (tPrime < 0.36364)            // 1/2.75
        {
            return 7.5625f*tPrime*tPrime;
        }
        if (tPrime < 0.72727)            // 2/2.75
        {
            return 7.5625f*(tPrime-=0.545454f)*tPrime + 0.75f;
        }
        if (tPrime < 0.90909)            // 2.5/2.75
        {
            return 7.5625f*(tPrime-=0.81818f)*tPrime + 0.9375f;
        }
        
        return 7.5625f*(tPrime-=0.95455f)*tPrime + 0.984375f; 
    }

    /** Provides an elastic easing in function simulating a 'pinged' elastic. From t=0 value starts
     *  with a large perturbation damping down towards a value of 0.5 as t=1.
     *  @param t Time value between 0-1.
     *  @return Eased value at the given time step.
     */
    public static float elasticIn(float t)
    {
        if (t <= 0)
        {
            return 0;
        }

        if (t >= 1)
        {
            return 0.5f;
        }

        float p = 0.25f;        // Period
        float a = 1.05f;        // Amplitude.
        float s = 0.0501716f;   // asin(1/a)*p/TWO_PI;
     
        return (float)Math.max(0,0.5 + a*Math.pow(2,-10*t)*Math.sin((t-s)*PConstants.TWO_PI/p));
    }

    /** Provides an elastic easing out function simulating an increasingly agitated elastic. From
     *  t=0 value starts at 0.5 with increasingly large perturbations ending at 1 when t=1.
     *  @param t Time value between 0-1.
     *  @return Eased value at the given time step.
     */
    public static float elasticOut(float t)
    {
        if (t <= 0)
        {
            return 0.5f;
        }

        if (t >= 1)
        {
            return 1;
        }

        float tPrime = 1-t;
        float p = 0.25f;        // Period
        float a = 1.05f;        // Amplitude.
        float s = 0.0501717f;   // asin(1/a)*p/TWO_PI;
     
        return (float)Math.min(1,0.5 - a*Math.pow(2,-10*tPrime)*Math.sin((tPrime-s)*PConstants.TWO_PI/p));
    }
}