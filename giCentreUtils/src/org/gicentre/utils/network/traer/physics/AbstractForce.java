package org.gicentre.utils.network.traer.physics;

// *****************************************************************************************
/** A skeletal implementation of {@link Force} covering the methods concerned with the on/off
 *  state. For the typical user wishing to make a custom Force, it is best to extend this 
 *  class (or one of the other skeletal implementations that meet the custom needs more 
 *  specifically: {@link TargetedForce}, {@link UniversalForce}, or {@link TwoBodyForce}) and
 *  deal only with defining the {@link Force#apply()} and {@link Force#apply(Particle)} methods. 
 *  @author Carl Pearson, Jeffrey Traer Bernstein with Minor Modifications by Jo Wood.
 *  @version 3.4, 5th February, 2016.
 */
// *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer and Carl Pearson. See http://murderandcreate.com/physics and
 * http://code.google.com/p/traerphysics/. The traer.physics package is distributed under the
 * Artistic Licence: http://dev.perl.org/licenses/
 */

public abstract class AbstractForce implements Force 
{
	// ----------------------------- Object variables ------------------------------
	
	// The internal state field; calls to {@link #isOn()}<code>==on</code>.  For small modifications to the on/off behaviour in extending classes,
	// the sub-classes should use the methods in this class (e.g., {@link #turnOn()}), hence this field is private and cannot be accessed directly.

	private boolean isOn;
	
	// ------------------------------- Constructors --------------------------------
	
	/** Creates a default abstract force set to <code>on</code>.
	 */
	protected AbstractForce() 
	{ 
		this(true); 
	}

	/** Creates an abstract force with the given state.
	 *  @param isOn The initial state of the force.
	 */
	protected AbstractForce(boolean isOn) 
	{ 
		this.isOn = isOn;
	}


	// ------------------------ Interface methods  ------------------------
	
	/** Turns the force off.
	 * @return This force now in an 'off' state.
	 */
	public AbstractForce turnOff() 
	{ 
		return turnOn(false); 
	}

	/** Turns the force on.
	 * @return This force now in an 'on' state. 
	 */
	public AbstractForce turnOn() 
	{
		return turnOn(true); 
	}

	/** Sets the force to the given state.
	 *  @param isOn Determins if this force is on or off.
	 *  @return This force in its newly set state.
	 */
	public AbstractForce turnOn(boolean isOn) 
	{
		this.isOn = isOn;
		return this; 
	}

	/** Reports whether or not the force is on.
	 *  @return True if the force is on.
	 */
	public boolean isOn() 
	{ 
		return isOn;
	}

	/** Reports whether or not the force is off.
	 *  @return True if the force is off.
	 */
	public boolean isOff() 
	{ 
		return !isOn(); 
	}
}