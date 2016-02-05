package org.gicentre.utils.network.traer.physics;

//  *****************************************************************************************
/** TwoBodyForce is {@link Force} between two specified {@link Particle}s, which extends 
 *  {@link TargetedForce} and simplifies implementation of custom Forces. A TwoBodyForce deals 
 *  with all of the mechanics of getting at the two "ends" of Force and reduces the details of
 *  what a user needs to specify down to creating the force {@link Vector3D}s that act on the 
 *  ends. If the forces are of the standard equal-and-opposite variety, only the force on one
 *  end needs to be provided.
 *  @author Carl Pearson and minor modifications by Jo Wood.
 *  @version 3.4, 5th February, 2016.
 */
//  *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer and Carl Pearson. See http://murderandcreate.com/physics and
 * http://code.google.com/p/traerphysics/. The traer.physics package is distributed under the
 * Artistic Licence: http://dev.perl.org/licenses/
 */
public abstract class TwoBodyForce extends TargetedForce 
{
	// -------------------------------- Object variables ----------------------------------
	
	private Particle oneEnd;		// One particle in the intertwined pair; classes extending this one should use getOneEnd() to access it.
	private Particle theOtherEnd;	// The Other {@link Particle} in the intertwined pair.
	
	// ---------------------------------- Constructor -------------------------------------
	
	/** Creates a two-body force between the two given particles. Turns on by default. Neither 
	 *  {@link Particle} is mutated by this operation, though later use of the {@link #apply()} 
	 *  method will mutate the {@link Particle#force} vector.
	 *  @param oneEnd the one end {@link Particle}, cannot be null
	 *  @param theOtherEnd the other end {@link Particle}, cannot be null
	 *  @throws NullPointerException if either {@link Particle} is <code>null</code>.
	 */
	protected TwoBodyForce(final Particle oneEnd, final Particle theOtherEnd) throws NullPointerException 
	{
		// For Developers: the {@link Particle}s are null-checked, then delegated to the 
		// {@link #directSetOneEnd(Particle)} and {@link #directSetTheOtherEnd(Particle)} methods.
		super();
		thrower(oneEnd,theOtherEnd);
		directSetOneEnd(oneEnd);
		directSetTheOtherEnd(theOtherEnd);
	}

	// ------------------------------------ Methods ---------------------------------------
	
	/** Reports the particle at {@link #oneEnd}. Mutating this {@link Particle} is permitted.
	 * @return {@link #oneEnd}
	 */
	public final Particle getOneEnd() 
	{ 
		return oneEnd; 
	}
	
	/** Reports the particle at {@link #theOtherEnd}. Mutating this {@link Particle} is permitted.
	 *  @return {@link #theOtherEnd}
	 */
	public final Particle getTheOtherEnd() 
	{ 
		return theOtherEnd; 
	}
	
	/** Applies this Force to {@link #oneEnd} and {@link #theOtherEnd}, which modifies their 
	 *  {@link Particle#force} values.
	 *  Users extending this class to create custom Forces need only implement {@link #forcePair()}, 
	 *  taking advantage of the static packaging method {@link #equalAndOpposite(Vector3D)} or 
	 *  {@link #specifyBoth(Vector3D, Vector3D)} to create the {@link ForcePair}.
	 *  @return This two body force.
	 */
	public TwoBodyForce apply() 
	{
		if (isOn() && (oneEnd.isFree() || theOtherEnd.isFree())) 
		{
			ForcePair fp = forcePair();
			if (oneEnd.isFree())
			{
				oneEnd.addForce(fp.forceOnOneEnd());
			}
			if (theOtherEnd.isFree())
			{
				theOtherEnd.addForce(fp.forceOnTheOtherEnd());
			}
		}
		return this;
	}
	

	/** Turns this force off.
	 *  @return this TwoBodyForce
	 */
	@Override 
	public TwoBodyForce turnOff() 
	{ 
		return turnOn(false); 
	}

	/** Turns this force on.
	 *  @return this TwoBodyForce
	 */
	@Override 
	public TwoBodyForce turnOn() 
	{ 
		return turnOn(true); 
	}

	/** Turns this force on or off.
	 *  @param isOn This force is turned on if true, otherwise it is turned off.
	 *  @return this TwoBodyForce
	 */
	@Override public TwoBodyForce turnOn(boolean isOn) 
	{ 
		super.turnOn(isOn); 
		return this; 
	}

	/** Sets {@link #oneEnd} to the give particle.
	 *  @param p The particle at one end of this Force; cannot be null
	 *  @return This force.
	 *  @throws NullPointerException if the particle is <code>null</code>.
	 */
	protected TwoBodyForce setOneEnd(final Particle p) throws NullPointerException 
	{
		return (p!=null) ? directSetOneEnd(p) : thrower("Argument p is null.");
	}
	
	/** Sets {@link #theOtherEnd} to the give particle.
	 *  @param p The particle at the other end of this Force; cannot be null
	 *  @return This force.
	 *  @throws NullPointerException if the particle is <code>null</code>.
	 */
	protected TwoBodyForce setTheOtherEnd( final Particle p) throws NullPointerException 
	{
		return (p!=null) ? directSetTheOtherEnd(p): thrower("Argument p is null.");
	}  
	
	/** Creates a {@link ForcePair} from two not necessarily equal and opposite forces.
	 *  @param forceOnOneEnd the force to be applied to {@link #oneEnd}
	 *  @param forceOnTheOtherEnd the force to be applied to {@link #theOtherEnd}
	 *  @return the appropriate ForcePair
	 *  @throws NullPointerException if either {@link Vector3D}<code>==null</code>
	 */
	@SuppressWarnings("synthetic-access")
	protected static ForcePair specifyBoth(final Vector3D forceOnOneEnd, final Vector3D forceOnTheOtherEnd) throws NullPointerException 
	{ 
		ForcePair.thrower(forceOnOneEnd, forceOnTheOtherEnd);
		return new ForcePair(forceOnOneEnd, forceOnTheOtherEnd);
	}

	/** Creates a force pair for when the forces to be applied are equal and opposite. Only the force on oneEnd needs to be specified.
	 *  @param forceOnOneEnd the force on oneEnd
	 *  @return the ForcePair
	 *  @throws NullPointerException if forceOnOneEnd is null
	 */
	@SuppressWarnings("synthetic-access")
	protected static ForcePair equalAndOpposite(final Vector3D forceOnOneEnd) throws NullPointerException 
	{
		if (forceOnOneEnd == null)
		{
			ForcePair.thrower("Argument forceOnOneEnd == null.");
		}
		
		return new ForcePair(forceOnOneEnd);
	}
	
	
	/** Should apply the force to the pair of particles. This is the method which must be overridden by
	 *  implementing classes allowing the force to be customised. Static methods for constructing a 
	 *  {@link ForcePair} are provided within this class: {@link #equalAndOpposite(Vector3D)}
	 *  or {@link #specifyBoth(Vector3D, Vector3D)}.
	 *  @return a {@link ForcePair} specifying the forces to apply to {@link #oneEnd} and {@link #theOtherEnd}.
	 */
	protected abstract ForcePair forcePair();
	
	
	// -------------------------------- Private methods -----------------------------------

	/** Sets {@link #oneEnd} to the give particle. This version is only to be used when 
	 *  <code>p!=null</code> is already known
	 *  @param p The particle at one end of this Force; must not be null
	 *  @return This force.
	 *  @throws NullPointerException if the particle is <code>null</code>.
	 */
	private TwoBodyForce directSetOneEnd(final Particle p) 
	{ 
		oneEnd = p; 
		return this; 
	}

	/** Sets {@link #theOtherEnd} to the give particle. This version is only to be used when 
	 *  <code>p!=null</code> is already known
	 *  @param p The particle at the other end of this Force; must not be null
	 *  @return This force.
	 *  @throws NullPointerException if the particle is <code>null</code>.
	 */
	private TwoBodyForce directSetTheOtherEnd(final Particle p) 
	{ 
		theOtherEnd = p; 
		return this; 
	}
	
	/** Local convenience method for throwing NullPointerExceptions for Particles.
	 * @param oneEnd one Particle to check
	 * @param theOtherEnd the other Particle to check
	 * @return true if no exception is thrown
	 */
	private static boolean thrower(Particle oneEnd, Particle theOtherEnd) 
	{
		if ((oneEnd==null) || (theOtherEnd==null)) 
		{
			if ((oneEnd==null) && (theOtherEnd==null))
			{ 
				throw new NullPointerException("Both end Particles are null."); 
			}
			else if (oneEnd==null) 
			{ 
				throw new NullPointerException("The oneEnd Particle is null."); 
			}
			else 
			{ 
				throw new NullPointerException("The theOtherEnd Particle is null."); 
			}
		}
		return true;
	}

	/** Convenience method for throwing NullPointerExceptions. Return type specified to allow
	 *  use in ternary (?:) expressions.
	 *  @param message the message in the resulting NullPointerException
	 *  @return nothing; return type simply for syntax compatibility with ternary expressions
	 */
	private static TwoBodyForce thrower(String message) 
	{ 
		throw new NullPointerException(message); 
	}

	// -------------------------------- Nested classes -----------------------------------
	
	/** Class that wraps Vector3D forces to apply to the two ends of this TwoBodyForce.
	 *  <br>
	 *  For the especially memory conscious, the ForcePair fields can be manipulated directly;
	 *  use the {@link #updateBoth(Vector3D, Vector3D)} or {@link #updateEqualAndOpposite(Vector3D)}
	 *  or use the getters and manipulate the Vector3Ds directly.
	 *  @author Carl Pearson
	 */
	protected static class ForcePair
	{
		// -------------------------------- Object variables ----------------------------------
		
		private Vector3D forceOnOneEnd;			// The force on the oneEnd Particle.
		private Vector3D forceOnTheOtherEnd;	// The force on the otherEnd Particle. This is null in the case of ForcePair created as equal and opposite.		
		private final boolean equalAndOpposite;	// Whether or not a ForcePair is equal and opposite; set permanently at instantiation

		// ---------------------------------- Constructor -------------------------------------
		
		/** Creates a force pair with unequal forces at each end.
		 *  @param forceOnOneEnd the force on the oneEnd particle; cannot be null
		 *  @param forceOnTheOtherEnd the force on the otherEnd particle; cannot be null
		 */
		private ForcePair(final Vector3D forceOnOneEnd, final Vector3D forceOnTheOtherEnd) 
		{
			this.forceOnOneEnd = forceOnOneEnd;
			this.forceOnTheOtherEnd = forceOnTheOtherEnd;
			equalAndOpposite = false;
		}

		/** Creates a force pair with equal and opposite forces at each end.
		 *  @param forceOnOneEnd the force on the oneEnd particle; cannot be null
		 */
		private ForcePair(final Vector3D forceOnOneEnd) 
		{
			this.forceOnOneEnd = forceOnOneEnd;
			equalAndOpposite = true;
		}

		// ------------------------------------ Methods ---------------------------------------
		
		/** Reports the force at oneEnd.
		 *  @return the force at oneEnd
		 */
		protected Vector3D forceOnOneEnd() 
		{ 
			return forceOnOneEnd; 
		}

		/** Reports the force at theOtherEnd. Changing this Vector3D will not mutate the forceOnOneEnd, even in the equal-and-opposite case.
		 *  @return the force at theOtherEnd
		 */
		protected Vector3D forceOnTheOtherEnd()
		{ 
			return equalAndOpposite ? forceOnOneEnd.copy().multiplyBy(-1f): forceOnTheOtherEnd; 
		}

		/** Sets the force at oneEnd according to forceOnOneEnd argument, and the force on theOtherEnd as equal and opposite.
		 *  @param forceOnOneEnd the force on oneEnd
		 *  @return this ForcePair, modified
		 *  @throws NullPointerException if forceOnOneEnd is null
		 */
		protected ForcePair updateEqualAndOpposite(Vector3D forceOnOneEnd) throws NullPointerException, IllegalStateException 
		{
			if (!equalAndOpposite)
			{
				throw new IllegalStateException("This ForcePair was not setup as an equal and opposite force pair.");
			}
			
			if (forceOnOneEnd == null) 
			{
				return thrower("The forceOnOneEnd Vector3D is null.");
			} 
			this.forceOnOneEnd = forceOnOneEnd;
			return this;
		}

		/** Updates the force for both ends according to the parameters.
		 * @param forceOnOneEnd the force on oneEnd Particle
		 * @param forceOnTheOtherEnd the force on theOtherEnd Particle
		 * @return this ForcePair, modified
		 * @throws NullPointerException if either argument is null
		 */
		protected ForcePair updateBoth(Vector3D forceOnOneEnd, Vector3D forceOnTheOtherEnd) throws NullPointerException 
		{
			thrower(forceOnOneEnd, forceOnTheOtherEnd);
			this.forceOnOneEnd = forceOnOneEnd;
			this.forceOnTheOtherEnd = forceOnTheOtherEnd;
			return this;
		}

		// -------------------------------- Private methods -----------------------------------
		
		/** Check to see if an exception needs to be thrown.
		 *  @param forceOnOneEnd The force acting on one end of the pair.
		 *  @param forceOnTheOtherEnd The force acting on one end of the pair. 
		 */
		private static void thrower(Vector3D forceOnOneEnd, Vector3D forceOnTheOtherEnd) throws NullPointerException 
		{
			if (forceOnOneEnd==null || forceOnTheOtherEnd==null) 
			{
				if ((forceOnOneEnd==null) && (forceOnTheOtherEnd==null))
				{
					throw new NullPointerException("Both Vector3D forces are null."); 
				}
				else if (forceOnOneEnd==null) 
				{
					throw new NullPointerException("The forceOnOneEnd Vector3D is null."); 
				}
				else 
				{ 
					throw new NullPointerException("The forceOnTheOtherEnd Vector3D is null."); 
				}
			}
		}

		/** Customisable null pointer exception.
		 *  @param message Message to display with this exception.
		 *  @return The forcePair generating this exception.
		 */
		private static ForcePair thrower(String message) 
		{ 
			throw new NullPointerException(message); 
		}
	}
}