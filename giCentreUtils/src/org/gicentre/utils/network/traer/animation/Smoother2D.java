package org.gicentre.utils.network.traer.animation;

//  *****************************************************************************************
/** A 2D smoother for transitions towards a target over time at a rate determined by its 
 *  smoothness. 2D smoothers are useful for 2D animation where two values must be 
 *  transitioned together.
 *  @author Jeffrey Traer Bernstein with Minor Modifications by Jo Wood.
 *  @version 3.4, 5th February, 2016.
 */
//  *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer. See http://web.archive.org/web/20060911111322/http://www.cs.princeton.edu/~traer/animation/
 * for an archive of the original animation package.
 */
public class Smoother2D implements Tickable
{
	// ----------------------------- Object variables ------------------------------

	private Smoother x,y;
	
	// ------------------------------- Constructors --------------------------------
	
	/** Creates a 2D smoother with the given smoothness.
	 *  @param smoothness The smoothness of the transition towards a target. It is scaled  between 
	 *                    0 and 1. A value of 0 has abrupt changes, 1 is very smooth. A value of
	 *                    0.9 gives nice workable smoothness for typical animations.
	 */
	public Smoother2D(float smoothness)
	{
		x = new Smoother(smoothness);
		y = new Smoother(smoothness);
	}

	/** Creates a 2D smoother with the given smoothness and start values.
	 *  @param startX Initial x value that will move towards a target.
	 *  @param startY Initial y value that will move towards a target.
	 *  @param smoothness The smoothness of the transition towards a target. It is scaled  between 
	 *                    0 and 1. A value of 0 has abrupt changes, 1 is very smooth. A value of
	 *                    0.9 gives nice workable smoothness for typical animations.
	 */
	public Smoother2D(float startX, float startY, float smoothness)
	{
		x = new Smoother(smoothness, startX);
		y = new Smoother(smoothness, startY);
	}

	// ---------------------------------- Methods ----------------------------------
	
	/** Sets the smoothness value that determines the rate of transition towards a target.
	 *  @param smoothness The smoothness of the transition towards a target. It is scaled  between 
	 *                    0 and 1. A value of 0 has abrupt changes, 1 is very smooth. A value of
	 *                    0.9 gives nice workable smoothness for typical animations.
	 */
	public final void setSmoothness(float smoothness)
	{
		x.setSmoothness(smoothness);
		y.setSmoothness(smoothness);
	}

	/** Advances the time used by the smoother to move towards its targets.
	 */
	public final void tick()
	{
		x.tick();
		y.tick();
	}
	
	/** Move the smoother to the given values immediately regardless of the smoothness value.
	 *  @param valueX New x target value to jump to.
	 *  @param valueY New y target value to jump to.
	 */
	public final void setValue(float valueX, float valueY)
	{
		x.setValue(valueX);
		y.setValue(valueY);
	}
	
	/** Move the smoother to the given x value immediately regardless of the smoothness value.
	 *  @param valueX New x target value to jump to.
	 */
	public final void setX(float valueX)
	{
		x.setValue(valueX);
	}

	/** Move the smoother to the given y value immediately regardless of the smoothness value.
	 *  @param valueY New y target value to jump to.
	 */
	public final void setY(float valueY)
	{
		y.setValue(valueY);
	}
	
	/** Sets the target values aimed at by the smoother.
	 *  @param targetX X target value aimed at by the smoother.
	 *  @param targetY Y target value aimed at by the smoother.
	 */
	public final void setTarget(float targetX, float targetY)
	{
		x.setTarget(targetX);
		y.setTarget(targetY);
	}
	
	/** Sets the target x value aimed at by the smoother.
	 *  @param targetX X target value aimed at by the smoother.
	 */
	public final void setXTarget(float targetX)
	{
		x.setTarget(targetX);
	}

	/** Sets the target y value aimed at by the smoother.
	 *  @param targetY Y target value aimed at by the smoother.
	 */
	public final void setYTarget(float targetY)
	{
		y.setTarget(targetY);
	}

	/** Reports the target x value aimed at by this smoother at a rate determined by the smoothness.
	 *  @return x target of the smoother.
	 */
	public final float getXTarget()
	{
		return x.getTarget();
	}

	/** Reports the target y value aimed at by this smoother at a rate determined by the smoothness.
	 *  @return y target of the smoother.
	 */
	public final float getYTarget()
	{
		return y.getTarget();
	}

	/** Reports the current x value of the smoother. This will be somewhere between the source and
	 *  target x values depending on the smoothness and number of times <code>tick()</code> has been called.
	 *  @return Current x value of the smoother.
	 *  @deprecated Consider using <code>getX()</code> instead for standard accessor naming.
	 */
	@Deprecated
	public final float x()
	{
		return getX();
	}
	
	/** Reports the current x value of the smoother. This will be somewhere between the source and
	 *  target x values depending on the smoothness and number of times <code>tick()</code> has been called.
	 *  @return Current x value of the smoother.
	 */
	public final float getX()
	{
		return x.getValue();
	}

	/** Reports the current y value of the smoother. This will be somewhere between the source and
	 *  target y values depending on the smoothness and number of times <code>tick()</code> has been called.
	 *  @return Current y value of the smoother.
	 *  @deprecated Consider using <code>getY()</code> instead for standard accessor naming.
	 */
	@Deprecated
	public final float y()
	{
		return getY();
	}
	
	/** Reports the current y value of the smoother. This will be somewhere between the source and
	 *  target y values depending on the smoothness and number of times <code>tick()</code> has been called.
	 *  @return Current y value of the smoother.
	 */
	public final float getY()
	{
		return y.getValue();
	}
}