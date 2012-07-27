package org.gicentre.utils.network.traer.physics;

// *****************************************************************************************
/** Modified Euler integrator that evolves the state of a particle system. Compared to the 
 *  Runge-Kutta integrator, this one is faster, but can be less stable.
 *  @author Jeffrey Traer Bernstein, Carl Pearson and minor modifications by Jo Wood.
 */
// *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer and Carl Pearson. See http://murderandcreate.com/physics and
 * http://code.google.com/p/traerphysics/. The traer.physics package is distributed under the
 * Artistic Licence: http://dev.perl.org/licenses/
 */
public class ModifiedEulerIntegrator extends Integrator 
{
	// --------------------------------- Constructors ----------------------------------

	/** Sets up the integrator.
	 *  @param s Particle system upon which to perform the integration.
	 */
	public ModifiedEulerIntegrator(ParticleSystem s)
	{ 
		super(s);
	}

	// ----------------------------------- Methods -------------------------------------

	/** Advances the integrator by one step.
	 *  @param deltaT the magnitude of the time step to advance.
	 */
	public ModifiedEulerIntegrator step(float deltaT)
	{
		s.clearForces();
		s.applyForces();

		float halftt = 0.5f*deltaT*deltaT;

		Vector3D a;
		for (Particle p : s.getParticles())
		{
			if (p.isFree()) 
			{
				a = p.getForce().multiplyBy(1/p.mass());           
				p.position().add( p.velocity().copy().multiplyBy(deltaT)).add( a.copy().multiplyBy(halftt));
				p.velocity().add( a.multiplyBy(deltaT));
			}
		}
		return this;
	}
}
