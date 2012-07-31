package org.gicentre.utils.network.traer.physics;

import org.gicentre.utils.geom.Locatable;

import processing.core.PVector;

// *****************************************************************************************
/** Class for representing a Particle. It contains the Particle's position, velocity, and
 *  the force on the Particle. The particle also has a mass, and a fixed vs free state.
 *  @author Jeffrey Traer Bernstein, Carl Pearson and minor modifications by Jo Wood.
 */
// *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer and Carl Pearson. See http://murderandcreate.com/physics and
 * http://code.google.com/p/traerphysics/. The traer.physics package is distributed under the
 * Artistic Licence: http://dev.perl.org/licenses/
 */

public class Particle implements Locatable
{

	// --------------------------- Class and object variables -----------------------------
	
											/** The default mass for the no-argument constructor. */
	public static final float DEFAULT_MASS = 1.0f;
											/** Whether or not this Particle is fixed. */
	protected boolean isFixed;
											/** The position of the particle in 3d space. It is automatically allocated to 0,0,0 on creation */
	protected Vector3D position;
											/** The 3d velocity of the particle. It is automatically allocated to 0,0,0 on creation. */
	protected Vector3D velocity;
											/** The age of the particle. */
	protected float age;
											/** Whether or not this particle is dead (not attached to any forces or springs). */
	protected boolean isDead;
	private float mass;						// The Particle mass.
	private Vector3D force; 				// The force associated with this particle. It is automatically allocated to 0,0,0 on creation.
	
	// ---------------------------------- Constructor -------------------------------------
	
	/** Creates a particle with the default mass determined by {@link #DEFAULT_MASS}.
	 */
	public Particle() 
	{ 
		this(DEFAULT_MASS); 
	}
	
	/** Creates a particle with a given mass.
	 *  @param m the desired mass which must be greater than 0.
	 *  @throws IllegalArgumentException via {@link #setMass(float)} if mass is negative or zero.
	 */
	public Particle(float m) throws IllegalArgumentException 
	{ 
		position = new Vector3D();
		velocity = new Vector3D();
		force    = new Vector3D();
		age = 0;
		isFixed = false;
		isDead  = false;
		setMass(m);
	}

	// ----------------------------------- Methods --------------------------------------
	
	/** Calculates the distance between this particle and another.
	 *  @param p the other particle
	 *  @return the distance between the two particles
	 *  @throws NullPointerException if p == null
	 */
	public final float distanceTo(Particle p) throws NullPointerException 
	{
		if (p==null)
		{
			throw new NullPointerException("The Particle p is null.");
		}
		return this.position().distanceTo(p.position());
	}

	/** Fixes the particle. That is, it is not subject to forces that may affect its movement.
	 *  @return this Particle with its new fixed state.
	 */
	public final Particle makeFixed()
	{ 
		return setFixed(true);
	}
	
	/** Frees this particle. That is, it becomes subject to forces  in relation to other particles that may affect its movement.
	 *  @return this particle with is new free state.
	 */
	public final Particle makeFree() 
	{ 
		return setFixed(false); 
	}
	
	/** Sets the particle's fixed/free state. Has the side-effect of clearing the velocity if fixed==true.
	 *  @param isFixed the new fixed/free state, fixed if true, free if false.
	 *  @return this particle with its new fixed/free state.
	 */
	public final Particle setFixed(boolean isFixed) 
	{
		this.isFixed = isFixed;
		if (isFixed) 
		{
			velocity.clear();
		}
		return this;
	}
	
	/** Reports whether or not this particle is fixed. 
	 *  @return the fixed/free state of the particle.
	 */
	public final boolean isFixed()
	{
		return isFixed; 
	}
	
	/** Reports whether or not this particle is free. This cqn be seen as a convenience method since it 
	 *  always returns !isFixed(). 
	 *  @return the free/fixed state of the particle.
	 */ 
	public final boolean isFree() 
	{ 
		return !isFixed(); 
	}

	/** Reports the position of the particle.
	 *  @return Position of this particle.
	 */
	public final Vector3D position() 
	{ 
		return position; 
	}
	
	/** Reports the location of this particle.
	 *  @return Location represented of this particle.
	 */
	public PVector getLocation() 
	{
		return position.getLocation();
	}

	/** Reports the velocity of the particle.
	 *  @return Velocity of this particle in 3d coordinates.
	 */
	public final Vector3D velocity() 
	{ 
		return velocity; 
	}

	/** Reports the particle's mass.
	 *  @return Mass of the particle, which is always greater than 0.
	 */
	public final float mass() 
	{ 
		return mass; 
	}
	
	/** Sets the particle's mass to the given value.
	 *  @param m the new mass which must be greater than 0.
	 *  @return this Particle with its new mass.
	 *  @throws IllegalArgumentException if its mass is set to less than 0.
	 */
	public final Particle setMass(float m)
	{
		if (m<=0)
		{
			throw new IllegalArgumentException("Particle mass must be greater than 0.  Supplied m: "+m);
		}
		mass = m;
		return this;
	}
	
	/** Reports the force currently applied to the particle. Using this to clear the force is deprecated; use {@link #clearForce()}
	 *  instead. Using this to add force is deprecated; use {@link #addForce(Vector3D)} instead.
	 *  @return the force on the Particle
	 *  @deprecated Calls to this method should replaced with calls to the more standard {@link #getForce()}.
	 */
	public final Vector3D force()
	{ 
		return getForce(); 
	}
	
	/** Reports the force currently applied to this particle. 
	 *  @return the force on the Particle
	 */
	public final Vector3D getForce()
	{ 
		return force; 
	}
		
	/** Updates the force on this particle by adding the given extra force.
	 *  @param addedForce The force to add to the current particle force
	 *  @return this Particle with its new combined force.
	 *  @throws NullPointerException via {@link Vector3D#add(Vector3D)} if added==null
	 */
	public final Particle addForce(Vector3D addedForce)
	{ 
		force.add(addedForce); 
		return this; 
	}
	
	/** Clears the force on this particle.
	 *  @return this particle with its new 0 force.
	 */
	public final Particle clearForce() 
	{ 
		force.clear(); 
		return this; 
	}

	/** Reports the age of this particle.
	 *  @return the current Particle age.
	 */
	public final float age() 
	{ 
		return age; 
	}

	/** Resets this particle by setting its age to 0, dead=false, clears the position, velocity, force Vector3Ds, and mass = DEFAULT_MASS.
	 *  @return the particle in its new reset state.
	 */
	public Particle reset() 
	{
		age = 0;
		isDead = false;
		position.clear();
		velocity.clear();
		force.clear();
		mass = DEFAULT_MASS;
		return this;
	}
}