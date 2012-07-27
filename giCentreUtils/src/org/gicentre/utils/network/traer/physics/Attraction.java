package org.gicentre.utils.network.traer.physics;

// *****************************************************************************************
/** This class implements TwoBodyForce to represent an inverse-square law force with a force 
 *  constant and minimal distance. The minimal distance is necessary to avoid numerical 
 *  instability associated with division by very small values. Providing a positive k creates
 *  an attractive force, a negative k creates a repulsive force
 *  <p> </p>
 *  A typical use of attraction would be to implement planetary gravitation. The gravitational
 *  force G*m_1*m_2 / r^2 could be implemented as simply as supplying G (scaled appropriately
 *  to mass and distance units used elsewhere in the system), and an appropriate minimal 
 *  separation to the scale of the problem.
 *  @author Jeffrey Traer Bernstein, Carl Pearson and minor modifications by Jo Wood.
 */
// *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer and Carl Pearson. See http://murderandcreate.com/physics and
 * http://code.google.com/p/traerphysics/. The traer.physics package is distributed under the
 * Artistic Licence: http://dev.perl.org/licenses/
 */

public class Attraction extends TwoBodyForce 
{
	// ------------------------------- Object variables -------------------------------
	
	private float distanceMin;
	private float k;
	
	// --------------------------------- Constructor ----------------------------------
	
	/** Creates an Attraction between oneEnd and theOtherEnd, with scale k and minimum distance distanceMin.
	 *  @param oneEnd The particle at one end.
	 *  @param theOtherEnd The particle at the other end.
	 *  @param k the force scale between the two particles.
	 *  @param distanceMin the minimum distance between particles above which the force acts.
	 *  @throws NullPointerException if either particle is null.
	 *  @throws IllegalArgumentException if distanceMin is <=0.
	 */
	public Attraction( final Particle oneEnd, final Particle theOtherEnd, final float k, final float distanceMin)
														throws NullPointerException, IllegalArgumentException
	{
		super(oneEnd, theOtherEnd);
		setStrength(k);
		setMinimumDistance(distanceMin);
	}

	// ----------------------------------- Methods ------------------------------------
	
	/** Reports the minimum separation distance above which the attraction is applied.
	 *  @return the minimum separation distance
	 */
	public final float getMinimumDistance()
	{ 
		return distanceMin;
	}
	
	/** Sets the minimum separation distance above which the attraction is applied.
	 *  @param d the new minimum distance
	 *  @return this Attraction with the new minimum distance setting.
	 *  @throws IllegalArgumentException if d<=0
	 */
	public final Attraction	setMinimumDistance(float d)	throws IllegalArgumentException 
	{
		if (d<=0)
		{
			throw new IllegalArgumentException("Argument d is "+d+"; cannot specify a minimum distance <=0.");
		}
		distanceMin = d;
		return this;
	}

	/** Sets the strength of this Attraction.  Positive k is an attractive force, negative k a repulsive one.
	 *  @param k the strength of the Attraction.
	 *  @return this Attraction with the new attractive force.
	 */
	public final Attraction	setStrength(float k)
	{ 
		this.k = k; 
		return this; 
	}
	
	/** Reports the strength of this Attraction.
	 *  @return the strength of this Attraction; positive for attractive forces, negative for repulsive forces.
	 */
	public final float getStrength()
	{ 
		return k; 
	}

	/** Implements the Attraction force calculation.
	 *  @return the ForcePair to act on oneEnd and theOtherEnd
	 */
	@Override 
	protected ForcePair forcePair() 
	{
		Vector3D fromTheOtherEndtoOneEnd = Vector3D.subtract(getOneEnd().position(), getTheOtherEnd().position()).floor(distanceMin);
		fromTheOtherEndtoOneEnd.length(-k*getOneEnd().mass()*getTheOtherEnd().mass()/fromTheOtherEndtoOneEnd.lengthSquared());
		return equalAndOpposite(fromTheOtherEndtoOneEnd);
	}
}