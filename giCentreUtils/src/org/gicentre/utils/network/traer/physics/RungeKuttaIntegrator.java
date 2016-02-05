package org.gicentre.utils.network.traer.physics;

import java.util.*;

//*****************************************************************************************
/** Class capable of performing Runge Kutta integration. Compared to the Euler integrators,
 *  this one is slower but is more stable.
 *  @author Carl Pearson, Jeffrey Traer Bernstein and minor modifications by Jo Wood.
 *  @version 3.4, 5th February, 2016.
 */
// *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer and Carl Pearson. See http://murderandcreate.com/physics and
 * http://code.google.com/p/traerphysics/. The traer.physics package is distributed under the
 * Artistic Licence: http://dev.perl.org/licenses/
 */

public class RungeKuttaIntegrator extends Integrator 
{
	// ------------------------------- Object variables --------------------------------
	
	private Map<Particle,Vector3D> originalPositions;
	private Map<Particle,Vector3D> originalVelocities;
	private Map<Particle,Vector3D> k1Forces;
	private Map<Particle,Vector3D> k1Velocities;
	private Map<Particle,Vector3D> k2Forces;
	private Map<Particle,Vector3D> k2Velocities;
	private Map<Particle,Vector3D> k3Forces;
	private Map<Particle,Vector3D> k3Velocities;
	private Map<Particle,Vector3D> k4Forces;
	private Map<Particle,Vector3D> k4Velocities;

	// --------------------------------- Constructor -----------------------------------
	
	/** Sets up the integrator to be used by the given particle system.
	 *  @param s Particle system upon which to perform the integration.
	 */
	public RungeKuttaIntegrator(ParticleSystem s) 
	{ 
		super(s);
	}

	// ----------------------------------- Methods -------------------------------------
	
	
	/** Provides the function capable of performing the integration.
	 *  @param kForces Forces to be applied to particles during integration.
	 *  @param kVelocities Velocities of particles.
	 *  @return The function that performs the integration.
	 */
	protected static final Function<Particle,?> kFunctor(final Map<Particle, Vector3D> kForces, final Map<Particle,Vector3D> kVelocities) 
	{
		return new Function<Particle,Object>()
		{
			@Override 
			public Object apply(Particle p) 
			{
				kForces.put(p, p.getForce().copy());
				kVelocities.put(p, p.velocity().copy());
				p.clearForce();
				return null;
			}
		};
	}

	/** Provides the function that applies the single increment of the particles' positions and velocities.
	 * @param kForces Forces associated with the particles/
	 * @param kVelocities Velocities of the particles.
	 * @param originalPositions The original positions of the particles before integration.
	 * @param originalVelocities The original velocities of the particles before integration. 
	 * @param deltaT Time step over which to move the particles. 
	 * @return The function that increments the particles.
	 */
	protected static final Function<Particle,?>	kApplier(final Map<Particle, Vector3D> kForces, final Map<Particle,	Vector3D> kVelocities, final Map<Particle,Vector3D> originalPositions, final Map<Particle,Vector3D> originalVelocities,	final float deltaT)
	{
		return new Function<Particle,Object>() 
		{
			@Override 
			public Object apply(Particle p) 
			{
				p.position().set(kVelocities.get(p)).multiplyBy(0.5f*deltaT).add(originalPositions.get(p)); 			
				p.velocity().set(kForces.get(p)).multiplyBy(0.5f*deltaT/p.mass()).add(originalVelocities.get(p));
				p.clearForce();
				return null;
			}
		};
	}

	/** Provides the function that updates the particles in the system.
	 *  @param k1f K1 force.
	 *  @param k1v K1 velocity.
	 *  @param k2f K2 force.
	 *  @param k2v K2 velocity.
	 *  @param k3f K3 force.
	 *  @param k3v K3 velocity.
	 *  @param k4f K4 force.
	 *  @param k4v K4 velocity.
	 *  @param oPos Original position.
	 *  @param oVel Original velocity.
	 *  @param deltaT Change in time units.
	 *  @return Function that updates the particle positions.
	 */
	@SuppressWarnings("static-method")
	protected Function<Particle,?> updater(final Map<Particle,Vector3D> k1f, final Map<Particle,Vector3D> k1v, final Map<Particle,Vector3D> k2f, final Map<Particle,Vector3D> k2v, final Map<Particle,Vector3D> k3f, final Map<Particle,Vector3D> k3v, final Map<Particle,Vector3D> k4f, final Map<Particle,Vector3D> k4v, final Map<Particle,Vector3D> oPos, final Map<Particle,Vector3D> oVel, final float deltaT) 
	{
		return new Function<Particle,Object>() 
		{
			@Override 
			public Object apply(Particle from)
			{
				from.age += deltaT;
				Vector3D originalPosition = oPos.get(from);
				Vector3D k1Velocity = k1v.get(from).multiplyBy(deltaT/6.0f);
				Vector3D k2Velocity = k2v.get(from).multiplyBy(deltaT/3.0f);
				Vector3D k3Velocity = k3v.get(from).multiplyBy(deltaT/3.0f);
				Vector3D k4Velocity = k4v.get(from).multiplyBy(deltaT/6.0f);

				from.position().set(originalPosition).add(k1Velocity).add(k2Velocity).add(k3Velocity).add(k4Velocity);

				// Update velocity
				Vector3D originalVelocity = oVel.get(from);
				Vector3D k1Force = k1f.get(from).multiplyBy(deltaT / (6.0f*from.mass()));
				Vector3D k2Force = k2f.get(from).multiplyBy(deltaT / (3.0f*from.mass()));
				Vector3D k3Force = k3f.get(from).multiplyBy(deltaT / (3.0f*from.mass()));
				Vector3D k4Force = k4f.get(from).multiplyBy(deltaT / (6.0f*from.mass()));

				from.velocity().set(originalVelocity).add(k1Force).add(k2Force).add(k3Force).add(k4Force);
				return null;
			}
		};
	}

	/** Instantiates the original positions and velocities of the particles. This has the side-effect of 
	 *  clearing all forces on particles.
	 */
	 protected final void allocateParticles() 
	 {
		originalPositions = new HashMap<Particle,Vector3D>();
		originalVelocities = new HashMap<Particle,Vector3D>();
		for (Particle p : s.getParticles() ) 
		{
			if (p.isFree()) 
			{
				originalPositions.put(p, p.position().copy());
				originalVelocities.put(p, p.velocity().copy());
				p.clearForce();
			}
		}
	}

	 /** Performs the incrementing of the particles' positions and velocities over the given time step.
	  *  @param deltaT Time step over which to update the particles.
	  *  @return The integrator that updates the system.
	  */
	public RungeKuttaIntegrator step(float deltaT)
	{      
		allocateParticles();
			
		/////////////////////////////////               
		// make k1
		s.applyForces(); // apply forces

			
		// instantiate maps
		k1Forces = new HashMap<Particle,Vector3D>();
		k1Velocities = new HashMap<Particle,Vector3D>();

		// side effect fill the builders, and clear forces
		Function.functor(originalPositions.keySet(), kFunctor(k1Forces,k1Velocities));
			
		////////////////                
		// make k2

		// side effect particle positions/velocities from k1
		Function.functor(originalPositions.keySet(), kApplier(k1Forces,k1Velocities,originalPositions,originalVelocities,deltaT));

		//apply forces
		s.applyForces();

		//reset builders
		k2Forces = new HashMap<Particle,Vector3D>();
		k2Velocities = new HashMap<Particle,Vector3D>();                

		//side effect fill builders and clear forces
		Function.functor(originalPositions.keySet(), kFunctor(k2Forces,k2Velocities));

		/////////////////////////////////////////////////////
		// get k3 values

		// side effect particle positions/velocities from k2
		Function.functor(originalPositions.keySet(), kApplier(k2Forces,k2Velocities,originalPositions,originalVelocities,deltaT));

		//apply forces
		s.applyForces();
		
		//reset builders
		k3Forces = new HashMap<Particle,Vector3D>();
		k3Velocities = new HashMap<Particle,Vector3D>();                

		//side effect fill builders and clear forces
		Function.functor(originalPositions.keySet(), kFunctor(k3Forces,k3Velocities));

		//////////////////////////////////////////////////
		// get k4 values

		// side effect particle positions/velocities from k2
		Function.functor(originalPositions.keySet(), kApplier(k3Forces,k3Velocities,originalPositions,originalVelocities,deltaT*2));

		//apply forces
		s.applyForces();

		//reset builders
		k4Forces = new HashMap<Particle,Vector3D>();
		k4Velocities = new HashMap<Particle,Vector3D>();                
			
		// Side effect fill builders and clear forces
		Function.functor(originalPositions.keySet(), kFunctor(k4Forces,k4Velocities));

		/////////////////////////////////////////////////////////////
		// put them all together and what do you get?

		Function.functor(originalPositions.keySet(), 
				         updater(k1Forces, k1Velocities, k2Forces, k2Velocities, k3Forces, k3Velocities, k4Forces, k4Velocities, originalPositions, originalVelocities, deltaT));
		return this;
	}
}