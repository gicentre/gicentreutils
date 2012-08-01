package org.gicentre.utils.network.traer.animation;

//  *****************************************************************************************
/** A single smoother for transitions towards a target over time. It is a normalised one-pole
 *  filter that transitions towards a target at a rate determined by its smoothness
 *  @author Jeffrey Traer Bernstein with Minor Modifications by Jo Wood.
 *  @version 31st July 2012.
 */
//  *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer. See http://web.archive.org/web/20060911111322/http://www.cs.princeton.edu/~traer/animation/
 * for an archive of the original animation package.
 */
public class Smoother implements Tickable
{

	// ----------------------------- Object variables ------------------------------

	private float a;
	private float gain;
	private float lastOutput;
	private float input;

	// ------------------------------- Constructors --------------------------------

	/** Creates a smoother with the given smoothness.
	 *  @param smoothness The smoothness of the transition towards a target. It is scaled  between 
	 *                    0 and 1. A value of 0 has abrupt changes, 1 is very smooth. A value of
	 *                    0.9 gives nice workable smoothness for typical animations.
	 */
	public Smoother(float smoothness)
	{
		setSmoothness(smoothness);
		setValue(0.0F);
	}

	/** Creates a smoother with the given smoothness and start value.
	 *  @param smoothness The smoothness of the transition towards a target. It is scaled  between 
	 *                    0 and 1. A value of 0 has abrupt changes, 1 is very smooth. A value of
	 *                    0.9 gives nice workable smoothness for typical animations.
	 *  @param start Start value that will move towards a target.
	 */
	public Smoother(float smoothness, float start)
	{
		setSmoothness(smoothness);
		setValue(start);
	}

	// ---------------------------------- Methods ----------------------------------

	/** Sets the smoothness value that determines the rate of transition towards a target.
	 *  @param smoothness The smoothness of the transition towards a target. It is scaled  between 
	 *                    0 and 1. A value of 0 has abrupt changes, 1 is very smooth. A value of
	 *                    0.9 gives nice workable smoothness for typical animations.
	 */
	public final void setSmoothness(float smoothness)
	{
		a = -smoothness;
		gain = 1.0F + a;
	}
	
	/** Advances the time used by the smoother to move towards its target.
	 */
	public final void tick()
	{
		lastOutput = gain * input - a * lastOutput;
	}

	/** Sets the target value aimed at by the smoother.
	 *  @param target Target value aimed at by the smoother.
	 */
	public final void setTarget(float target)
	{
		input = target;
	}
	
	/** Reports the target value aimed at by this smoother at a rate determined by the smoothness.
	 *  @return Target of the smoother.
	 */
	public final float getTarget()
	{
		return input;
	}

	/** Move the smoother to the given target value immediately regardless of the smoothness value.
	 *  @param x New target value to jump to.
	 */
	public void setValue(float x)
	{
		input = x;
		lastOutput = x;
	}
	
	/** Reports the current value of the smoother. This will be somewhere between the source and target
	 *  depending on the smoothness and number of times <code>tick()</code> has been called.
	 *  @return Current value of the smoother.
	 */
	public final float getValue()
	{
		return lastOutput;
	}
}