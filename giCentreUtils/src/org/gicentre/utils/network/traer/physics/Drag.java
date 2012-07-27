package org.gicentre.utils.network.traer.physics;

// *****************************************************************************************
/** Class capable of applying the drag on a particle to inhibit its motion.
 *  @author Carl Pearson, Jeffrey Traer Bernstein and minor modifications by Jo Wood.
 */
// *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer and Carl Pearson. See http://murderandcreate.com/physics and
 * http://code.google.com/p/traerphysics/. The traer.physics package is distributed under the
 * Artistic Licence: http://dev.perl.org/licenses/
 */

public class Drag extends Function<Particle,Particle> 
{
	// ---------------------------- Object and class variables -----------------------------
	
	private float drag;
	public static final float DEFAULT_DRAG = 0.01f;

	// ----------------------------------- Constructors ------------------------------------
	
	/** Creates a drag with the default value.
	 */
	public Drag() 
	{ 
		this(DEFAULT_DRAG); 
	}
	
	/** Creates a drag with the given value.
	 *  @param drag Drag to apply to particles.
	 */
	public Drag(float drag) 
	{ 
		this.drag = drag; 
	}

	// ------------------------------------- Methods ---------------------------------------
	
	/** Sets the drag to the given value.
	  *  @param drag Drag to apply to particles.
	  *  @return The drag with the newly modified value.
	  */
	public Drag setDrag(float drag) 
	{ 
		this.drag = drag; 
		return this; 
	}
	
	/** Applies this drag to the given particle.
	 *  @param p Particle upon which to apply the drag.
	 *  @return The particle with a modified velocity in response to this drag.
	 */
	@Override
	public Particle apply(Particle p) 
	{
		p.getForce().add(p.velocity().copy().multiplyBy(-drag));
		return p;
	}
}
