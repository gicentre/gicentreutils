package org.gicentre.utils.network.traer.animation;

//  *****************************************************************************************
/** A 3D smoother for transitions towards a target over time at a rate determined by its 
 *  smoothness. 3D smoothers are useful for 3D animation where three values must be 
 *  transitioned together.  
 *  @author Jeffrey Traer Bernstein with Minor Modifications by Jo Wood.
 *  @version 1st August 2012.
 */
//  *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer. See http://web.archive.org/web/20060911111322/http://www.cs.princeton.edu/~traer/animation/
 * for an archive of the original animation package.
 */
public class Smoother3D implements Tickable
{
	// ----------------------------- Object variables ------------------------------

	private Smoother x,y,z;
	
	// ------------------------------- Constructors --------------------------------
	
	/** Creates a 3D smoother with the given smoothness.
	 *  @param smoothness The smoothness of the transition towards a target. It is scaled  between 
	 *                    0 and 1. A value of 0 has abrupt changes, 1 is very smooth. A value of
	 *                    0.9 gives nice workable smoothness for typical animations.
	 */
	public Smoother3D(float smoothness)
	{
		x = new Smoother(smoothness);
		y = new Smoother(smoothness);
		z = new Smoother(smoothness);
	}

	/** Creates a 3D smoother with the given smoothness and start values.
	 *  @param startX Initial x value that will move towards a target.
	 *  @param startY Initial y value that will move towards a target.
	 *  @param startZ Initial z value that will move towards a target.
	 *  @param smoothness The smoothness of the transition towards a target. It is scaled  between 
	 *                    0 and 1. A value of 0 has abrupt changes, 1 is very smooth. A value of
	 *                    0.9 gives nice workable smoothness for typical animations.
	 */
	public Smoother3D(float startX, float startY, float startZ, float smoothness)
	{
		x = new Smoother(smoothness, startX);
		y = new Smoother(smoothness, startY);
		z = new Smoother(smoothness, startZ);
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
		z.setSmoothness(smoothness);
	}

	/** Advances the time used by the smoother to move towards its targets.
	 */
	public final void tick()
	{
		x.tick();
		y.tick();
		z.tick();
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

	/** Sets the target z value aimed at by the smoother.
	 *  @param targetZ Z target value aimed at by the smoother.
	 */
	public final void setZTarget(float targetZ)
	{
		z.setTarget(targetZ);
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

	/** Reports the target z value aimed at by this smoother at a rate determined by the smoothness.
	 *  @return z target of the smoother.
	 */
	public final float getZTarget()
	{
		return z.getTarget();
	}

	/** Sets the target values aimed at by the smoother.
	 *  @param targetX X target value aimed at by the smoother.
	 *  @param targetY Y target value aimed at by the smoother.
	 *  @param targetZ Z target value aimed at by the smoother.
	 */
	public final void setTarget(float targetX, float targetY, float targetZ)
	{
		x.setTarget(targetX);
		y.setTarget(targetY);
		z.setTarget(targetZ);
	}

	/** Move the smoother to the given values immediately regardless of the smoothness value.
	 *  @param valueX New x target value to jump to.
	 *  @param valueY New y target value to jump to.
	 *  @param valueZ New z target value to jump to.
	 */
	public final void setValue(float valueX, float valueY, float valueZ)
	{
		x.setValue(valueX);
		y.setValue(valueY);
		z.setValue(valueZ);
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

	/** Move the smoother to the given z value immediately regardless of the smoothness value.
	 *  @param valueZ New z target value to jump to.
	 */
	public final void setZ(float valueZ)
	{
		z.setValue(valueZ);
	}

	/** Reports the current x value of the smoother. This will be somewhere between the source and
	 *  target x values depending on the smoothness and number of times <code>tick()</code> has been called.
	 *  @return Current x value of the smoother.
	 *  @deprecated Consider using <code>getX()</code> instead for standard accessor naming.
	 */
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

	/** Reports the current z value of the smoother. This will be somewhere between the source and
	 *  target z values depending on the smoothness and number of times <code>tick()</code> has been called.
	 *  @return Current z value of the smoother.
	 *  @deprecated Consider using <code>getZ()</code> instead for standard accessor naming.
	 */
	public final float z()
	{
		return getZ();
	}
	
	/** Reports the current z value of the smoother. This will be somewhere between the source and
	 *  target z values depending on the smoothness and number of times <code>tick()</code> has been called.
	 *  @return Current z value of the smoother.
	 */
	public final float getZ()
	{
		return z.getValue();
	}
}