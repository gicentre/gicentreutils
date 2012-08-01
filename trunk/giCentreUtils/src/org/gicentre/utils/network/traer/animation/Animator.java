package org.gicentre.utils.network.traer.animation;

import java.util.Vector;

//  *****************************************************************************************
/** Class for controlling all the smoothers. It can be used to create smoothers and then call
 *  this class's <code>tick()</code> method inside a sketch's <code>draw()</code> to advance 
 *  the time for all smoothers.
 *  @author Jeffrey Traer Bernstein with Minor Modifications by Jo Wood.
 *  @version 31st July 2012.
 */
//  *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer. See http://web.archive.org/web/20060911111322/http://www.cs.princeton.edu/~traer/animation/
 * for an archive of the original animation package.
 */

public class Animator implements Tickable
{
	// ----------------------------- Object variables ------------------------------

	private Vector<Tickable> smoothers;
	private float smoothness;
	
	// ------------------------------- Constructors --------------------------------
	
	/** Creates an animator with the given smoothness.
	 *  @param smoothness The smoothness of the animator between 0 and 1. A value of 0 has abrupt 
	 *                    changes, 1 is very smooth. A value of 0.9 gives nice workable smoothness
	 *                    for typical animations.
	 */
	public Animator(float smoothness)
	{
		this.smoothness = smoothness;
		smoothers = new Vector<Tickable>();
	}

	// ---------------------------------- Methods ----------------------------------
	
	/** Adds a smoother that will be handled by this animator. A smoother is a normalised one-pole filter
	 *  that transitions towards a target at a rate determined by its smoothness.
	 *  @return The smoother that has been added to those handled by this animator.
	 */
	public final Smoother makeSmoother()
	{
		Smoother s = new Smoother(smoothness);
		smoothers.add(s);
		return s;
	}

	/** Adds a 2D smoother that will be handled by this animator. A 2D smoother is made of two smoothers
	 *  to smooth 2D movements.
	 *  @return The smoother that has been added to those handled by this animator.
	 */
	public final Smoother2D make2DSmoother()
	{
		Smoother2D s = new Smoother2D(smoothness);
		smoothers.add(s);
		return s;
	}

	/** Adds a 3D smoother that will be handled by this animator. A 3D smoother is made of three smoothers
	 *  to smooth 3D movements.
	 *  @return The smoother that has been added to those handled by this animator.
	 */
	public final Smoother3D make3DSmoother()
	{
		Smoother3D s = new Smoother3D(smoothness);
		smoothers.add(s);
		return s;
	}

	/** Advances time for all smoothers that have been made by this animator. This method is normally 
	 *  called from within the sketch wishing to smooth transitions.
	 */
	public final void tick()
	{
		for (Tickable t : smoothers)
		{
			t.tick();
		}
	}

	/** Sets the smoothness of all smoothers that have been made by this animator.
	 *  @param smoothness The smoothness of the animator between 0 and 1. A value of 0 has abrupt 
	 *                    changes, 1 is very smooth. A value of 0.9 gives nice workable smoothness
	 *                    for typical animations.
	 */
	public final void setSmoothness(float smoothness)
	{
		for (Tickable t : smoothers)
		{
			t.setSmoothness(smoothness);
		}
	}
}