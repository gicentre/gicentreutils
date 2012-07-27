package org.gicentre.utils.network.traer.physics;

import java.util.*;

//*****************************************************************************************
/** Class capable of performing a settling Runge Kutta integration. Compared to the 
 *  Euler integrators this one is slower but is more stable. This version can be forced to 
 *  stabalise after a given settling period.
 *  @author Carl Pearson, Jeffrey Traer Bernstein and minor modifications by Jo Wood.
 */
// *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer and Carl Pearson. See http://murderandcreate.com/physics and
 * http://code.google.com/p/traerphysics/. The traer.physics package is distributed under the
 * Artistic Licence: http://dev.perl.org/licenses/
 */

public class SettlingRungeKuttaIntegrator extends RungeKuttaIntegrator
{

	// --------------------------- Object and class variables ----------------------------
	
	public static final int DEFAULT_SETTLING_AGE = 50;
	private final int settlingAge;
	private final float epsilon = 0.0001f;

	// --------------------------------- Constructors ------------------------------------
	
	/** Creates the integrator to be applied to the given particle system with the given settling age.
	 *  @param s Particle system to be associated with this integrator.
	 *  @param settlingAge Age in timestep units over which the system is to stabalise.
	 */
	public SettlingRungeKuttaIntegrator(ParticleSystem s, int settlingAge)
	{ 
		super(s); 
		this.settlingAge = settlingAge;
	}
	
	/** Sets up the integrator to be used by the given particle system.
	 *  @param s Particle system upon which to perform the integration.
	 */
	public SettlingRungeKuttaIntegrator(ParticleSystem s)
	{ 
		this(s, DEFAULT_SETTLING_AGE);
	}

	/** Provides the function that updates the particles in the system.
	 * @param k1Forces
	 * @param k1Velocities
	 * @param k2Forces
	 * @param k2Velocities
	 * @param k3Forces
	 * @param k3Velocities
	 * @param k4Forces
	 * @param k4Velocities
	 * @param originalPositions
	 * @param originalVelocities
	 * @param deltaT
	 * @return Function that updates the particle positions.
	 */
	@Override 
	protected Function<Particle,?> updater(final Map<Particle,Vector3D> k1Forces, final Map<Particle,Vector3D> k1Velocities, final Map<Particle,Vector3D> k2Forces, final Map<Particle,Vector3D> k2Velocities, final Map<Particle,Vector3D> k3Forces, final Map<Particle,Vector3D> k3Velocities, final Map<Particle,Vector3D> k4Forces, final Map<Particle,Vector3D> k4Velocities, final Map<Particle,Vector3D> originalPositions, final Map<Particle,Vector3D> originalVelocities,	final float deltaT) 
	{
		final Function<Particle,?> superUpdater = super.updater(k1Forces, 
																k1Velocities,
																k2Forces,
																k2Velocities,
																k3Forces,
																k3Velocities,
																k4Forces,
																k4Velocities,
																originalPositions,
																originalVelocities,
																deltaT);

		return new Function<Particle,Object>() 
		{
			@SuppressWarnings("synthetic-access")
			@Override public Object apply(Particle from) 
			{
				superUpdater.apply(from);

				if (from.velocity().length()<epsilon)
				{
					from.age+=1;
				} 
				else 
				{
					from.age=0;
				}

				if (from.age > settlingAge) 
				{
					from.makeFixed();
				}
				return null;
			}
		};
	}
}