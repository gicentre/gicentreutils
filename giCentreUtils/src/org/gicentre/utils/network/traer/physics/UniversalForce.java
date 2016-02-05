package org.gicentre.utils.network.traer.physics;


//  *****************************************************************************************
/** A {@link Force} that is applied to {@link Particle}s at the user's discretion, and hence
 *  does not support the {@link Force#apply()} method. Users wishing to create custom Forces 
 *  of this kind should extend this class, implementing the {@link Force#apply(Particle)}
 *  method only.
 *  Examples of this kind of Force are the {@link Gravity} and {@link Drag} classes.
 *  @author Carl Pearson and minor modifications by Jo Wood.
 *  @version 3.4, 5th February, 2016.
 */
//  *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer and Carl Pearson. See http://murderandcreate.com/physics and
 * http://code.google.com/p/traerphysics/. The traer.physics package is distributed under the
 * Artistic Licence: http://dev.perl.org/licenses/
 */
public abstract class UniversalForce extends AbstractForce 
{

	/** This type of {@link Force} must have a target {@link Particle}; an exception will be thrown by this method.
	 *  @return irrelevant, this method will always throw an exception
	 *  @throws UnsupportedOperationException this Force is guaranteed to throw this exception
	 */
	public AbstractForce apply() throws UnsupportedOperationException 
	{
		throw new UnsupportedOperationException("This Force must be applied to a Particle. apply() is not supported by this Force.");
	}

}