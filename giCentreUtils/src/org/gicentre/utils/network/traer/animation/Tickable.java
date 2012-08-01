package org.gicentre.utils.network.traer.animation;

//  *****************************************************************************************
/** Defines the behaviour of all tickable smoothers that can be advanced over time.  
 *  @author Jeffrey Traer Bernstein with Minor Modifications by Jo Wood.
 *  @version 31st July 2012.
 */
//  *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer. See http://web.archive.org/web/20060911111322/http://www.cs.princeton.edu/~traer/animation/
 * for an archive of the original animation package.
 */
public interface Tickable
{

	/** Should advance the time used by the smoother to move towards its target.
	 */
    public abstract void tick();

    /** Should set the smoothness value that determines the rate of transition towards a target.
	 *  @param smoothness The smoothness of the transition towards a target. It is scaled  between 
	 *                    0 and 1. A value of 0 has abrupt changes, 1 is very smooth. A value of
	 *                    0.9 gives nice workable smoothness for typical animations.
	 */
    public abstract void setSmoothness(float smoothness);
}