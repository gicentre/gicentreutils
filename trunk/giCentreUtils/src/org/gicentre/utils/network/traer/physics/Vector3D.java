package org.gicentre.utils.network.traer.physics;

import org.gicentre.utils.geom.Locatable;

import processing.core.PVector;


//  *****************************************************************************************
/** Represents 3D vectors and the operations on them.  Most operations change the underlying
 *  Vector3D.  So if v1 = (1,1,1) = v2, after adding (0,0,1) to v1 via {@link #add(float, float, float)},
 *  v1 != v2.  The one exception is the {@link Vector3D#cross(Vector3D)} method, which always 
 *  returns a new Vector3D.  Also, none of the operations alter their parameters, except in
 *  self-referential conditions (e.g., <code>Vector3D.add(v1,v2,v1)</code> modifies v1).
 *  <br /><br />
 *  The methods in the class are designed to allow "chaining"; given a Vector3D v1, it could be used as:
 *  <br /><code>
 *  v1.add(1,2,3);<br />
 *  v1.add(3,2,1);<br />
 *  v1.multiplyBy(2);</code>
 *  <br />
 *  However, because the class is designed to allow chaining, those operations can also be done as:
 *  <br />
 *  <code>v1.add(1,2,3).add(3,2,1).multiplyBy(2);</code>
 *  <br /><br />
 *  Standard mathematical order-of-operations applies, so
 *  <br />
 *  <code>v1.add(1,2,3).add(3,2,1).multiplyBy(2) == v1.add(3,2,1).add(1,2,3).multiplyBy(2)<br /> 
 *  v1.add(1,2,3).add(3,2,1).multiplyBy(2) != v1.multiplyBy(2).add(1,2,3).add(3,2,1)</code>
 *  <br /><br />
 *  Finally, most of the methods throw NullPointerException if provided a null argument instead
 *  of Vector3D argument.
 *  @author Jeffrey Traer Bernstein, Carl Pearson and minor modifications by Jo Wood.
 */
//  *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer and Carl Pearson. See http://murderandcreate.com/physics and
 * http://code.google.com/p/traerphysics/. The traer.physics package is distributed under the
 * Artistic Licence: http://dev.perl.org/licenses/
 */

public class Vector3D implements Locatable
{
	// -------------------------------- Object variables ----------------------------------
	
	private float x;			// The x component of the vector.
	private float y;			// The y component of the vector.
	private float z;			// The z component of the vector.
	private PVector location;	// PVector representation for compatibility with Processing.

	// ---------------------------------- Constructors -------------------------------------
	
	/** Creates a (0,0,0) Vector3D.
	 */
	public Vector3D()
	{ 
		this(0,0,0); 
	}
	
	/** Creates a 3D vector from the location defined in the given pVector.
	 *  @param location Location information used to create this 3d vector.
	 */
	public Vector3D(final PVector location)
	{ 
		this.x = location.x;
		this.y = location.y;
		this.z = location.z; 
		this.location = new PVector(x,y,z);
	}
	
	/** Creates a 3D vector with the given x, y, z components.
	 *  @param x the x component of the vector.
	 *  @param y the y component of the vector.
	 *  @param z the z component of the vector.
	 */
	public Vector3D(final float x, final float y, final float z)
	{ 
		this.x = x;
		this.y = y;
		this.z = z; 
		location = new PVector(x,y,z);
	}

	/** Creates a new cloned 3D vector copied from the components of the given vector.
	 *  @param p the source Vector3D.
	 *  @throws NullPointerException if p is null
	 */
	public Vector3D(final Vector3D p) throws NullPointerException 
	{
		if (p!=null)
		{ 
			location = new PVector();
			set(p.x, p.y, p.z); 
		}
		else 
		{
			thrower("Argument Vector3D p cannot be null in constructor Vector3D(p).");
		}
	}
	
	/** Static constructor; convenience method for chaining calls.  Replaces
	 * <br />
	 * <code>Vector3D v2 = (new Vector3D(x,y,z)).add(v1)...</code>
	 * <br />with<br />
	 * <code>Vector3D v2 = Vector3D.of(x,y,z).add(v1)...</code>
	 * @param x the x component of the vector.
	 * @param y the y component of the vector.
	 * @param z the z component of the vector.
	 * @return A new Vector3D, with the appropriate components
	 */
	public static final Vector3D of(final float x, final float y, final float z )
	{
		return new Vector3D(x,y,z); 
	}
	
	/** Creates a 'zero' vector.
	 *  @return a new Vector3D (0,0,0)
	 */
	public static final Vector3D of() 
	{ 
		return new Vector3D(0,0,0);
	}
	
	// ------------------------------------ Methods ---------------------------------------
	
	/** Copies the provided Vector3D.
	 *  @param from the source Vector3D.
	 *  @return a new Vector3D with identical components to from.
	 *  @throws NullPointerException if the from vector is null.
	 */
	public final static Vector3D copy(Vector3D from)
	{ 
		return new Vector3D(from); 
	}
	
	
	/** Provides a copy of this Vector3D.
	 * @return a new Vector3D with identical components to this one
	 */
	public final Vector3D copy()
	{ 
		return copy(this); 
	}

	/** Throws a NullPointerException with the provided message.
	 *  @param message the message
	 *  @return convenience return type for use with ? : ; operator
	 */
	public static final Vector3D thrower(String message) throws NullPointerException 
	{
		throw new NullPointerException(message);
	}

	/** Subtracts the arguments from this vector's components.
	 *  @param x the x component of the vector.
	 *  @param y the y component of the vector.
	 *  @param z the z component of the vector.
	 *  @return this, modified by subtraction
	 */
	@SuppressWarnings("hiding")
	public final Vector3D subtract(final float x, final float y, final float z)
	{ 
		return set(this.x-x, this.y-y, this.z-z); 
	}
	
	/** Subtracts the argument from this vector.
	 * @param p the vector to subtract from this one; p is unmodified by this call
	 * @return this, modified by subtraction
	 * @throws NullPointerException if p is null
	 */
	public final Vector3D subtract( final Vector3D p ) throws NullPointerException
	{ 
		return p!=null ? subtract(this, p, this) : thrower("Argument Vector3D p cannot be null in subtract(p)."); 
	}
	
	/** Returns a new Vector3D, v1 - v2.
	 *  @param v1 the first vector; not modified by this operation.
	 *  @param v2 the second vector; not modified by this operation.
	 *  @return a new Vector3D, v1-v2.
	 *  @throws NullPointerException if either of v1 or v2 is null
	 */
	public final static Vector3D subtract( final Vector3D v1, final Vector3D v2) throws NullPointerException
	{
		return subtract(v1,v2,null);
	}
	
	/** Returns v1-v2 in target, or a new Vector3D if target is null.
	 *  @param v1 the first vector; unmodified by this operation
	 *  @param v2 the second vector; unmodified by this operation
	 *  @param target the target vector, may be null
	 *  @return target, or a new Vector3D
	 *  @throws NullPointerException if either of v1 or v2 is null
	 */
	public final static Vector3D subtract(final Vector3D v1, final Vector3D v2, final Vector3D target) throws NullPointerException
	{ 
		return  ((v1!=null) && (v2!=null)) ? (target==null) ? 
					new Vector3D(v1.x-v2.x,v1.y-v2.y,v1.z-v2.z) : target.set(v1.x-v2.x,v1.y-v2.y,v1.z-v2.z) :
																  ((v1==null) && (v2==null)) ? thrower("Both Vector3D v1 and v2 are null in subtract(v1,v2,target).") :
																  v1==null ? thrower("Vector3D v1 is null in subtract(v1,v2,target).") :
																	  		 thrower("Vector3D v2 is null in subtract(v1,v2,target)."); 
	}
	
	/** Adds the arguments to this vector's components.
	 * @param x the x component of the vector.
	 * @param y the y component of the vector.
	 * @param z the z component of the vector.
	 * @return this, modified by addition.
	 */
	@SuppressWarnings("hiding")
	public final Vector3D add(final float x, final float y, final float z)
	{ 
		return set(this.x+x, this.y+y, this.z+z); 
	}
	
	/** Adds the argument Vector3D's components to this Vector3D's components.
	 *  @param p the Vector3D to be added to this one; unmodified by this operation
	 *  @return this Vector3D, after modification
	 *  @throws NullPointerException if p==null
	 */
	public final Vector3D add(Vector3D p) throws NullPointerException
	{ 
		return p!=null ? add(this,p,this) : thrower("Argument p in add(p) is null."); 
	}
	
	/** Creates a new Vector3D from v1+v2.
	 * @param v1 one Vector3D, unmodified by this operation
	 * @param v2 the other Vector3D, unmodified by this operation
	 * @return a new Vector3D, v1+v2
	 * @throws NullPointerException if either v1 or v2 is null
	 */
	public static Vector3D add(Vector3D v1, Vector3D v2) throws NullPointerException
	{ 
		return add(v1,v2,null); 
	}
	
	/** Returns v1+v2 in target, or a new Vector3D if target is null
	 *  @param v1 the first vector; unmodified by this operation (unless also the target)
	 *  @param v2 the second vector; unmodified by this operation (unless also the target)
	 *  @param target the target vector, may be null
	 *  @return target, or a new Vector3D
	 *  @throws NullPointerException if either of v1 or v2 is null
	 */
	public static Vector3D add(Vector3D v1, Vector3D v2, Vector3D target) throws NullPointerException 
	{
		return ((v1!=null) && (v2!=null)) ? (target==null) ? new Vector3D(v1.x+v2.x,v1.y+v2.y,v1.z+v2.z) : target.set(v1.x+v2.x,v1.y+v2.y,v1.z+v2.z) :
			v1==null && v2==null ? thrower("Both Vector3D v1 and v2 are null in add(v1,v2,target).") :
				v1==null                             ? thrower("Vector3D v1 is null in add(v1,v2,target).") :
					thrower("Vector3D v2 is null in add(v1,v2,target).");
	}

	
	/** Reports the x component of this vector, but consider using getX() instead to follow standard accessor notation.
	 *  @return the x component of this vector.
	 */
	public final float x()
	{ 
		return x; 
	}
	
	/** Reports the y component of this vector, but consider using getY() instead to follow standard accessor notation.
	 *  @return the y component of this vector.
	 */
	public final float y()
	{ 
		return y; 
	}
	
	/** Reports the the z component of this vector, but consider using getZ() instead to follow standard accessor notation.
	 *  @return the z component of this vector.
	 */
	public final float z()
	{ 
		return z; 
	}

	/** Reports the x component of this vector.
	 *  @return the x component of this vector.
	 */
	public final float getX()
	{ 
		return x; 
	}
	
	/** Reports the y component of this vector.
	 *  @return the y component of this vector.
	 */
	public final float getY()
	{ 
		return y; 
	}
	
	/** Reports the the z component of this vector.
	 *  @return the z component of this vector.
	 */
	public final float getZ()
	{ 
		return z; 
	}
	
	/** Reports the location represented by this vector.
	 *  @return Location represented by this vector.
	 */
	public PVector getLocation() 
	{
		return location;
	}

	/** Sets the x component and return this Vector3D after modification.
	 *  @param x the new x component of this vector.
	 *  @return this Vector3D, after modification
	 */
	public final Vector3D setX(float x)
	{ 
		this.x = x; 
		location.x = x;
		return this; 
	}
	
	/** Sets the y component and return this Vector3D after modification.
	 * @param y the new y component of this vector.
	 * @return this Vector3D, after modification
	 */
	public final Vector3D setY(float y)
	{ 
		this.y = y; 
		location.y = y;
		return this; 
	}
	
	/** Sets the z component and return this Vector3D after modification.
	 *  @param z the new z component of this vector.
	 *  @return this Vector3D, after modification
	 */
	public final Vector3D setZ(float z)
	{
		this.z = z;
		location.z = z;
		return this; 
	}
	
	/** Sets all the components.
	 * @param x the desired x component of this vector.
	 * @param y the desired y component of this vector.
	 * @param z the desired z component of this vector.
	 * @return this Vector3D, after modification.
	 */
	public final Vector3D set(float x, float y, float z)
	{ 
		return setX(x).setY(y).setZ(z); 
	}
	
	/** Sets this Vector3D components to those of another Vector3D.
	 *  @param p the other Vector3D  of this vector.
	 *  @return this Vector3D, modified.
	 *  @throws NullPointerException if p is null
	 */
	public final Vector3D set(Vector3D p) throws NullPointerException
	{ 
		return p!=null ? set(p.x, p.y, p.z) : thrower("Argument p is null in set(p)."); 
	}

	/** Multiplies each component by <code>f</code>.             
	 *  @param f the scaling factor.
	 *  @return this Vector3D, modified
	 */
	public final Vector3D multiplyBy(float f)
	{ 
		return set(x*f, y*f, z*f); 
	}
	
	/** Creates a new Vector3D from v, by copying it and multiplying each of its components by f.
	 *  @param v the original Vector3D; unmodified by this operation.
	 *  @param f the scaling factor.
	 *  @return a new Vector3D, v*f.
	 *  @throws NullPointerException if v==null.
	 */
	public final static Vector3D multiplyBy(Vector3D v, float f) throws NullPointerException
	{ 
		return v!=null ? multiplyBy(v,f,null) : thrower("Argument v is null in multiplyBy(v) call.");
	}

	/** Returns the result of v*f in target, or creates a new Vector3D if target==null.
	 *  @param v the source Vector3D; unmodified by this operation.
	 *  @param f the scaling factor.
	 *  @param target the target Vector3D; modified by this operation, may be null.
	 *  @return target, modified, or a new Vector3D.
	 *  @throws NullPointerException if v==null.
	 */
	@SuppressWarnings("null")
	public final static Vector3D multiplyBy(Vector3D v, float f, Vector3D target) throws NullPointerException 
	{
		if (v==null)
		{
			thrower("Argument v is null in multiplyBy(v,f,target) call.");
		}
		return target == null ? of(v.x*f,v.y*f,v.z*f) : target.set(v.x*f,v.y*f,v.z*f);
	}

	/** Limits the length of this Vector3D to <code>f</code>.  Calling this with f less than or equal to 0 sets length to 0.
	 *  @param f the desired limit.
	 *  @return this Vector3D, appropriately modified
	 */
	public final Vector3D limit(float f)
	{ 
		return length() > f? length(f) : this; 
	}

	/** The opposite of {@link #limit(float)}: puts a lower bound on the length of <code>f</code>.  Calling this with 
	 *  f less than or equal to 0 is a no-op.
	 *  @param f the desired minimum length
	 *  @return this Vector3D, appropriately modified
	 */
	public final Vector3D floor( float f )
	{ 
		return f>0 && length() < f ? length(f) : this; 
	}

	/** Sets this Vector3Ds length to one by appropriately scaling x, y, and z.
	 *  @return this Vector3D, modified.
	 *  @throws ArithmeticException if this is a zero vector.
	 */
	public final Vector3D normalize() throws ArithmeticException
	{ 
		return multiplyBy(1/length()); 
	}

	/** Calculates the distance between the tip of this Vector3D and that of p.
	 *  Relies on {@link #subtract(Vector3D, Vector3D)} and {@link #length()}.
	 *  @param v the other Vector3D; unmodified by this operation
	 *  @return the distance between this and p
	 *  @throws NullPointerException if p==null
	 */
	public final float distanceTo(Vector3D v) throws NullPointerException 
	{
		thrower(v,"Argument v is null in distanceTo(p) call.");
		return Vector3D.subtract(this, v).length();
	}

	/** Like {@link #distanceTo(Vector3D)}, only squared
	 *  @param v the other Vector3D; unmodified by this operation
	 *  @return the distance between this and p, squared
	 *  @throws NullPointerException if p==null
	 */
	public final float distanceSquaredTo(Vector3D v) throws NullPointerException 
	{
		thrower(v,"Argument v is null in distanceSquaredTo(p) call."); 
		return Vector3D.subtract(this, v).lengthSquared();
	}

	/** Calculates the distance between this vector and that represented by the three given components.
	 *  @param x The x component of the vector.
	 *  @param y The y component of the vector.
	 *  @param z The z component of the vector.
	 *  @return Shortest Distance between the two vectors.
	 */
	@SuppressWarnings("hiding")
	public final float distanceTo(float x, float y, float z) 
	{
		float dx = this.x - x;
		float dy = this.y - y;
		float dz = this.z - z;
		return (float)Math.sqrt( dx*dx + dy*dy + dz*dz );
	}

	/** Projects this Vector3D onto another Vector3D.
	 *  @param p the other Vector3D
	 *  @return this Vector3D, after projection onto p
	 */
	public final Vector3D projectOnto( Vector3D p)
	{ 
		return set(p.copy().length(p.dot(this)/p.length())); 
	}

	/** Calculates the dot product between this Vector3D and another - this.x*p.x + this.y*p.y + this.y*p.y
	 *  @param p the other Vector3D
	 *  @return the dot product; always >=0
	 *  @throws NullPointerException if p==null
	 */
	public final float dot(Vector3D p) throws NullPointerException 
	{
		thrower(p,"Argument p is null in dot(p) call.");
		return x*p.x + y*p.y + z*p.z;
	}	

	/** Reports the length of this vector.
	 *  @return Length of this vector.
	 */
	public final float length() 
	{ 
		return (float)Math.sqrt(lengthSquared()); 
	}
	
	/** Reports the normalised length of this vector scaled by the given scale factor.
	 *  @param f Scale factor.
	 *  @return Normalised length.
	 */
	public final Vector3D length(float f)  
	{ 
		float len = length();
		if (len == 0)
		{
			return multiplyBy(0);
		}
		return multiplyBy(f/length()); 
	}
	
	/** Reports the squared length of this vector.
	 *  @return Squared length of this vector.
	 */
	public final float lengthSquared()
	{ 
		return dot(this); 
	}

	/** Resets this vector back to (0,0,0).
	 */
	public final void clear() 
	{ 
		x = 0; 
		y = 0; 
		z = 0;
		location.x = 0;
		location.y = 0;
		location.z = 0;
	}

	/** Provides a textual representation of this vector.
	 *  @return Text representation of this vector.
	 */
	public final String toString() 
	{ 
		return new String("(" + x + ", " + y + ", " + z + ")"); 
	}

	/** Creates a new Vector3D from this and the cross product with the given vector.
	 *  @param v the other Vector3D in the cross-product.
	 *  @return a new Vector3D, this x v.
	 *  @throws NullPointerException if v is null.
	 */
	public final Vector3D cross(Vector3D v) throws NullPointerException 
	{
		return v==null ?thrower("Argument v is null in cross(Vector3D)."):
						new Vector3D(this.y*v.z - this.z*v.y, this.x*v.z - this.z*v.x, this.x*v.y - this.y*v.x);
	}

	/** Indicates whether or not this Vector3D is the zero vector
	 *  @return true if all components are zero.
	 */
	public boolean isZero() 
	{ 
		return ((x==0) && (y==0) && (z==0)); 
	}

	@Override 
	/** Reports whether or not this vector equals the given one.
	 *  @param other Other object to compare with this one.
	 */
	public boolean equals(Object other) 
	{ 
		return (other instanceof Vector3D) ? equals((Vector3D)other) : false; 
	}
	
	/** Returns a unique hash code that represents the content of this vector. Two vectors with identical 
	 *  components will generate the same hash code, even if they are two separate objects. This is consistent
	 *  with the behaviour of equals().
	 */
	@Override
	public int hashCode()
	{
		int result = Float.floatToIntBits(x);
		result =  1000003*result + Float.floatToIntBits(y);
		result =  1000003*result + Float.floatToIntBits(z);
		return result;
	}

	/** Determines if the components of this vector match those of the given one.
	 *  @param other the other Vector3D.
	 *  @return true if all of the components are equal.
	 */
	protected boolean equals(Vector3D other) 
	{ 
		return this==other ? true : (x==other.x) && (y==other.y) && (z==other.z);
	}
	
	// -------------------------------- Private methods -----------------------------------
	
	/** Convenience method for creating a customised exception reporter if the given vector is null.
	 *  @param v The vector to test for null status. 
	 *  @param message Customised message to be displayed if the vector is null.
	 *  @return Returns true if exception is not thrown.
	 */
	private static boolean thrower(Vector3D v, String message) 
	{
		if (v==null)
		{
			thrower(message);
		}
		return true;
	}
}