package org.gicentre.utils.network.traer.physics;

//*****************************************************************************************
/** Abstract integrator that defines a number of preset integrator factories.
 *  @author Carl Pearson with minor modifications by Jo Wood.
 */
// *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer and Carl Pearson. See http://murderandcreate.com/physics and
 * http://code.google.com/p/traerphysics/. The traer.physics package is distributed under the
 * Artistic Licence: http://dev.perl.org/licenses/
 */
public abstract class Integrator  
{
	
	// --------------------------------- Object variables ----------------------------------
	
	protected ParticleSystem s;
	
	/** Lists the different integration methods that can be produced by the integrator factory.
	 */
	public enum METHOD 
	{ 
		RUNGEKUTTA 
		{
			@Override 
			public Integrator factory(ParticleSystem physics) 
			{ 
				return new RungeKuttaIntegrator(physics); 
			}
		},
		
		EULER 
		{
			@Override 
			public Integrator factory(ParticleSystem physics) 
			{ 
				return new BackwardEulerIntegrator(physics);
			}
		},
		
		MODEULER 
		{
			@Override 
			public Integrator factory(ParticleSystem physics) 
			{ 
				return new ModifiedEulerIntegrator(physics); 
			}
		},
		
		SRUNGEKUTTA 
		{
			@Override public Integrator factory(ParticleSystem physics) 
			{ 
				return new SettlingRungeKuttaIntegrator(physics); 
			}
		}; 
	
		public abstract Integrator factory(ParticleSystem physics);
	}
	
	// ----------------------------------- Constructor -------------------------------------
	
	/** Creates a new integrator that will apply to the given particle system.
	 *  @param s Particle system that will evolve using this integrator.
	 */
	public Integrator(ParticleSystem s) 
	{ 
		this.s=s; 
	}
	
	/** Should increment the integrator by a single time step.
	 *  @param t Time step.
	 *  @return The Integrator after stepping forward by the given time step.
	 */
	public abstract Integrator step(float t);
}