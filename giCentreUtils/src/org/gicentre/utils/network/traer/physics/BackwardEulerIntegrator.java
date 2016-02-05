package org.gicentre.utils.network.traer.physics;

// *****************************************************************************************
/** Class capable of performing backward Euler integration. Compared to the Runge-Kutta
 *  integrator, this one is faster, but can be less stable.
 *  @author Carl Pearson, Jeffrey Traer Bernstein and minor modifications by Jo Wood.
 *  @version 3.4, 5th February, 2016.
 */
// *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer and Carl Pearson. See http://murderandcreate.com/physics and
 * http://code.google.com/p/traerphysics/. The traer.physics package is distributed under the
 * Artistic Licence: http://dev.perl.org/licenses/
 */

public class BackwardEulerIntegrator extends Integrator
{

	// --------------------------------- Constructors ----------------------------------

	/** Sets up the integrator.
	 *  @param s Particle system upon which to perform the integration.
	 */
	public BackwardEulerIntegrator(ParticleSystem s) 
	{
		super(s); 
	}

	// ----------------------------------- Methods -------------------------------------

	/** Advances the integrator by one step.
	 *  @param deltaT the magnitude of the time step to advance.
	 */
	public BackwardEulerIntegrator step(float deltaT) 
	{
		// Clear any residual forces, then apply all existing forces
		s.clearForces();
		s.applyForces();

		for (Particle p : s.getParticles())
		{
			if (p.isFree()) 
			{                 
				// For all free particles:
				p.velocity().add(p.getForce().multiplyBy(deltaT/p.mass()));      // Update velocity first
				p.position().add(Vector3D.multiplyBy(p.velocity(),deltaT));   // Position based on new velocity
			}
		}
		return this;
	}
}