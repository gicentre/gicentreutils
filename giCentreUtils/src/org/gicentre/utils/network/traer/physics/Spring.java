package org.gicentre.utils.network.traer.physics;

//*****************************************************************************************
/** Class for representing a physical spring by extending {@link TwoBodyForce} to calculate
 *  the force  with a spring constant ({@link #ks}), damping factor ({@link #d}), and an 
 *  ideal length ({@link #l}). Thus, the positions of the {@link Particle}s on either end 
 *  obey the equation:<br />
 *  <code>ddot(r) = -k/m * (r-l) - d/m * dot(r)</code>
 *  @author Jeffrey Traer Bernstein, Carl Pearson and minor modifications by Jo Wood.
 *  @since 4.0
 */
// *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer and Carl Pearson. See http://murderandcreate.com/physics and
 * http://code.google.com/p/traerphysics/. The traer.physics package is distributed under the
 * Artistic Licence: http://dev.perl.org/licenses/
 */
public class Spring extends TwoBodyForce
{

	// --------------------------- Class and object variables -----------------------------
	
	private float l;		// The ideal spring length; always > 0.
	private float ks; 		// The Spring-force constant; always > 0.
	private float d;		// The damping constant; always >= 0.
	
	// ---------------------------------- Constructor -------------------------------------
	
	/** Creates a spring with the given properties between the given particles.
	 *  @param oneEnd the one end {@link Particle}; cannot be null
	 *  @param theOtherEnd the other end {@link Particle}; cannot be null
	 *  @param ks the spring constant.
	 *  @param d the damping constant.
	 *  @param l the ideal length of the spring.
	 *  @throws NullPointerException if either Particle is null, via {@link TwoBodyForce#TwoBodyForce(Particle, Particle)}
	 *  @throws IllegalArgumentException if l is not positive, via {@link #setRestLength(float)}.
	 *  @throws IllegalArgumentException if ks not positive, via {@link #setStrength(float)}.
	 *  @throws IllegalArgumentException if d less than 0, via {@link #setDamping(float)}.
	 */
	public Spring(final Particle oneEnd, final Particle theOtherEnd, final float ks, final float d, final float l) throws NullPointerException, IllegalArgumentException 
	{
		super(oneEnd,theOtherEnd);
		setStrength(ks);
		setDamping(d);
		setRestLength(l);
	}

	// ----------------------------------- Methods --------------------------------------
	
	/**
	 * Gets the current Spring length; uses the end particle positions to do so.
	 * @return the current Spring length
	 */
	public final float currentLength() 
	{
		return getOneEnd().position().distanceTo( getTheOtherEnd().position() );
	}

	/** Reports the ideal length, {@link #l}; always more than 0.
	 *  @return the ideal length of the spring.
	 */
	public final float restLength() 
	{ 
		return l; 
	}

	/** Sets the ideal length of the spring.
	 *  @param l the new rest length; must be > 0.
	 *  @return This spring with its new rest length.
	 *  @throws IllegalArgumentException if <code>l<=0</code>
	 */
	public final Spring setRestLength(final float l) throws IllegalArgumentException 
	{
		if (l<=0) 
		{
			throw new IllegalArgumentException("Rest length l <= 0; spring ideal length must be positive.");
		}
		this.l = l; return this;
	}

	
	/** Reports the strength of the spring {@link #ks}; always more than 0.
	 * @return {@link #ks}, the spring constant
	 */
	public final float strength() 
	{ 
		return ks; 
	}
	
	/** Sets the strength of the spring {@link #ks}; must be greater than 0.
	 *  @param ks the new spring constant; must be greater than 0.
	 *  @return This spring with its new strength.
	 *  @throws IllegalArgumentException if ks is not positive.
	 */
	public final Spring setStrength(final float ks)	throws IllegalArgumentException 
	{
		if (ks<=0)
		{
			throw new IllegalArgumentException("Spring strength ks <= 0; spring strength must be positive.");
		}
		this.ks = ks; 
		return this;
	}
	
	/** Reports the damping constant of the spring {@link #d}; Will always be greater than 0.
	 *  @return {@link #d}, the damping constant.
	 */
	public final float damping()
	{ 
		return d; 
	}
	
	/** Sets the damping constant of the spring {@link #d}.
	 *  @param d the new damping constant. Must not be negative but can be 0 for no damping.
	 *  @return This spring with its new damping constant.
	 *  @throws IllegalArgumentException if d is negative.
	 */
	public final Spring setDamping(final float d) throws IllegalArgumentException 
	{
		if (d<0)
		{
			throw new IllegalArgumentException("Spring damping is < 0; damping constant must be positive.");
		}
		this.d = d; return this;
	}

	/** Calculates the spring forces on each of the particles at either end of the spring.
	 *  @return the spring forces at each end of the spring.
	 */
	@Override 
	public ForcePair forcePair() 
	{        
		// First, set the spring force equal to the distance between the particles.
		Vector3D springForce = Vector3D.subtract(getOneEnd().position(), getTheOtherEnd().position()); 
		
		// Set the spring force equal to the negative difference between the distance and the ideal length scaling by the spring constant ks.
		springForce.length(-(springForce.length()-l)).multiplyBy(ks);  
		
		// Set the damping force equal to the difference between the velocities.
		Vector3D dampingForce = Vector3D.subtract(getOneEnd().velocity(), getTheOtherEnd().velocity())  
		.projectOnto(springForce)                  // project that force in the direction of the spring, i.e. - the velocity along the spring
		.multiplyBy(-d);                           // scale to the damping factor.

		// Combine the spring and damping forces.
		springForce.add(dampingForce);
		
		return equalAndOpposite(springForce); 	   // Apply the springForce to oneEnd, and -springForce to theOtherEnd.
	}
}
