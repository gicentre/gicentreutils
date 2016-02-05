package org.gicentre.utils.network.traer.physics;

import java.util.*;

//*****************************************************************************************
/** Class capable of performing a settling Runge Kutta integration. Compared to the 
 *  Euler integrators this one is slower but is more stable. This version can be forced to 
 *  stabalise after a given settling period.
 *  @author Carl Pearson, Jeffrey Traer Bernstein and minor modifications by Jo Wood.
 *  @version 3.4, 5th February, 2016.
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
	
								/** Default settling age (50). */
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
	 *  @param k1f K1 forces.
	 *  @param k1v K1 velocities.
	 *  @param k2f K2 forces.
	 *  @param k2v K2 velocities.
	 *  @param k3f K3 forces.
	 *  @param k3v K3 velocities.
	 *  @param k4f K4 forces.
	 *  @param k4v K4 velocities.
	 *  @param oPos Original position.
	 *  @param oVel Original velocity.
	 *  @param deltaT Change in time units.
	 * @return Function that updates the particle positions.
	 */
	
	protected Function<Particle,?> updater(final Map<Particle,Vector3D> k1f, final Map<Particle,Vector3D> k1v, final Map<Particle,Vector3D> k2f, final Map<Particle,Vector3D> k2v, final Map<Particle,Vector3D> k3f, final Map<Particle,Vector3D> k3v, final Map<Particle,Vector3D> k4f, final Map<Particle,Vector3D> k4v, final Map<Particle,Vector3D> oPos, final Map<Particle,Vector3D> oVel, final float deltaT) 
	{
		final Function<Particle,?> superUpdater = super.updater(k1f,k1v, k2f,k2v, k3f,k3v, k4f,k4v, oPos,oVel, deltaT);

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