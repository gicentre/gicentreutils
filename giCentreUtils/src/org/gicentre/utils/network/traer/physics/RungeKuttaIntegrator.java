package org.gicentre.utils.network.traer.physics;

import java.util.*;

//*****************************************************************************************
/** Class capable of performing Runge Kutta integration. Compared to the Euler integrators,
 *  this one is slower but is more stable.
 *  @author Carl Pearson, Jeffrey Traer Bernstein and minor modifications by Jo Wood.
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
	@SuppressWarnings("hiding")
	protected Function<Particle,?> updater(final Map<Particle,Vector3D> k1Forces, final Map<Particle,Vector3D> k1Velocities, final Map<Particle,Vector3D> k2Forces, final Map<Particle,Vector3D> k2Velocities, final Map<Particle,Vector3D> k3Forces, final Map<Particle,Vector3D> k3Velocities, final Map<Particle,Vector3D> k4Forces, final Map<Particle,Vector3D> k4Velocities, final Map<Particle,Vector3D> originalPositions, final Map<Particle,Vector3D> originalVelocities, final float deltaT) 
	{
		return new Function<Particle,Object>() 
		{
			@Override 
			public Object apply(Particle from)
			{
				from.age += deltaT;
				Vector3D originalPosition = originalPositions.get(from);
				Vector3D k1Velocity = k1Velocities.get(from).multiplyBy(deltaT/6.0f);
				Vector3D k2Velocity = k2Velocities.get(from).multiplyBy(deltaT/3.0f);
				Vector3D k3Velocity = k3Velocities.get(from).multiplyBy(deltaT/3.0f);
				Vector3D k4Velocity = k4Velocities.get(from).multiplyBy(deltaT/6.0f);

				from.position().set(originalPosition).add(k1Velocity).add(k2Velocity).add(k3Velocity).add(k4Velocity);

				// Update velocity
				Vector3D originalVelocity = originalVelocities.get(from);
				Vector3D k1Force = k1Forces.get(from).multiplyBy(deltaT / (6.0f*from.mass()));
				Vector3D k2Force = k2Forces.get(from).multiplyBy(deltaT / (3.0f*from.mass()));
				Vector3D k3Force = k3Forces.get(from).multiplyBy(deltaT / (3.0f*from.mass()));
				Vector3D k4Force = k4Forces.get(from).multiplyBy(deltaT / (6.0f*from.mass()));

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
			
		//side effect fill builders and clear forces
		Function.functor(originalPositions.keySet(), kFunctor(k4Forces,k4Velocities));

		/////////////////////////////////////////////////////////////
		// put them all together and what do you get?

		Function.functor(originalPositions.keySet(), 
				         updater(k1Forces, k1Velocities, k2Forces, k2Velocities, k3Forces, k3Velocities, k4Forces, k4Velocities, originalPositions, originalVelocities, deltaT));
		return this;
	}
}