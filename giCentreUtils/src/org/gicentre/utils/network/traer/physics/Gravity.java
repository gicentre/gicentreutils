package org.gicentre.utils.network.traer.physics;

// *****************************************************************************************
/** Represents a gravity function which may be applied to particles.
 *  @author Carl Pearson with minor modifications by Jo Wood.
 *  @version 3.4, 5th February, 2016.
 */
// *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer and Carl Pearson. See http://murderandcreate.com/physics and
 * http://code.google.com/p/traerphysics/. The traer.physics package is distributed under the
 * Artistic Licence: http://dev.perl.org/licenses/
 */

public class Gravity extends Function<Particle,Particle> 
{
	// ---------------------------- Object and class variables -----------------------------
	
	private Vector3D gravity;
								/** Default gravity value (0) */
	public static final float DEFAULT_GRAVITY = 0f;
	
	// ----------------------------------- Constructors ------------------------------------
	
	/** Creates a zero-gravitational function.
	 */
	public Gravity() 
	{
		this(DEFAULT_GRAVITY); 
	}
	
	/** Creates a gravity function with the given gravity value applied in the 'y' direction.
	 *  @param grav Gravitational parameter.
	 */
	public Gravity(float grav) 
	{ 
		this(0,grav,0);
	}
	
	/** Creates a gravity function with the given gravity value applied 3 dimensions.
	 *  @param gx Gravitational parameter to be applied in the x-direction.
	 *  @param gy Gravitational parameter to be applied in the y-direction.
	 *  @param gz Gravitational parameter to be applied in the z-direction.
	 */
	public Gravity(float gx, float gy, float gz)
	{
		gravity = Vector3D.of(gx,gy,gz);
	}
	
	/** Creates a gravity function with the given gravity value applied 3 dimensions.
	 *  @param grav3d Gravitational parameter to be applied in the 3 dimensions.
	 */
	public Gravity(Vector3D grav3d) 
	{ 
		gravity = grav3d.copy();
	}

	
	// ------------------------------------- Methods ---------------------------------------
	
	/** Sets the gravity function with the given gravity value applied in the 'y' direction.
	 *  @param grav Gravitational parameter.
	 *  @return The newly modified gravity function.
	 */
	public Gravity setGravity(float grav) 
	{ 
		return setGravity(0,grav,0); 
	}
	
	/** Sets the gravity function with the given gravity value applied 3 dimensions.
	 *  @param gx Gravitational parameter to be applied in the x-direction.
	 *  @param gy Gravitational parameter to be applied in the y-direction.
	 *  @param gz Gravitational parameter to be applied in the z-direction.
	 *  @return The newly modified gravity function.
	 */
	public Gravity setGravity(float gx, float gy, float gz) 
	{ 
		gravity.set(gx,gy,gz); 
		return this; 
	}
	
	/** Sets the gravity function with the given gravity value applied 3 dimensions.
	 *  @param grav3d Gravitational parameter to be applied in three dimensions.
	 *  @return The newly modified gravity function.
	 */
	public Gravity setGravity(Vector3D grav3d) 
	{ 
		gravity.set(grav3d); 
		return this; 
	}

	/** Applies this gravity function to the given particle, modifying its velocity and direction as appropriate.
	 *  @param p Particle upon which to apply the gravity.
	 */
	@Override
	public Particle apply(Particle p) 
	{
		p.getForce().add(gravity);
		return p;
	}
}