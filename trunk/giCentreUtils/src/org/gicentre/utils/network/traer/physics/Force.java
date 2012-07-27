package org.gicentre.utils.network.traer.physics;

// *****************************************************************************************
/** Interface specifying the basic mechanics of a Force. There are two basic skeletal 
 *  implementations: a {@link TargetedForce} with specified targets, where the {@link #apply()}
 *  method is used; and a {@link UniversalForce} that is applied to a {@link Particle}, where
 *  the {@link #apply(Particle)} method is used.
 *  <p> </p>
 *  The {@link Spring} force between two Particles is an example of the first form, while the 
 *  universal free-fall {@link Gravity} is an example of the second form.
 *  <p> </p>
 *  An intermediate skeletal implementation of this interface is provided in the {@link AbstractForce}
 *  class. Unless substantial extra behaviour associated with turning a Force on and off needs 
 *  to be specified, in general users should extend the AbstractForce class instead of
 *  implementing this interface.
 *  @author Jeffrey Traer Bernstein, Carl Pearson and minor modifications by Jo Wood.
 *  @since 4.0
 */
// *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer and Carl Pearson. See http://murderandcreate.com/physics and
 * http://code.google.com/p/traerphysics/. The traer.physics package is distributed under the
 * Artistic Licence: http://dev.perl.org/licenses/
 */
public interface Force 
{
	/** Should turn the Force off. After calling this method, calls to {@link #isOff()} should return
	 *  <code>true</code>. Implementations of the Force interface should delegate this call to the
	 *  {@link #turnOn(boolean)} method, with a <code>false</code> argument.
	 *  @return this Force, de-activated.
	 */
	public abstract Force turnOff();

	/** Should turn the Force on. After calling this method, calls to {@link #isOn()} should return 
	 *  <code>true</code>.  Implementations of the Force interface should delegate this call to the
	 *  {@link #turnOn(boolean)} method, with a <code>true</code> argument.
	 *  @return this Force, activated.
	 */
	public abstract Force turnOn();

	/** Should set the Force to the argument <code>isOn</code>.  Subsequent calls to {@link #isOn()} should
	 *  return the value of <code>isOn</code>. Implementations of the Force interface should delegate
	 *  {@link #turnOff()} and {@link #turnOn()} to this method.
	 *  @param isOn the state to set the Force in.
	 *  @return this Force, with appropriate activation condition.
	 */
	public abstract Force turnOn(boolean isOn);

	/** Should <code>true</code> if this force is on.  Should also consistently return !{@link #isOff()}. 
	 *  Implementations of the Force interface should delegate the behaviour of one of these methods to
	 *  the other, to ensure consistency.
	 *  @return <code>true</code> if this force is on.
	 */
	public abstract boolean isOn();

	/** Should return <code>true</code> if this force is off.  Should also consistently return !{@link #isOn()}.
	 *  Implementations of the Force interface should delegate the behaviour of one of these methods to the
	 *  other, to ensure consistency.
	 *  @return <code>true</code> if this force is off.
	 */
	public abstract boolean isOff();

	/** Should apply this Force.
	 *  @return this Force
	 *  @throws UnsupportedOperationException if the implementing class uses {@link #apply(Particle)} instead.
	 *  @throws IllegalStateException optionally, if the Force is currently off.
	 */
	public abstract Force apply() throws UnsupportedOperationException, IllegalStateException;

	/** Should apply this Force to a Particle p.
	 *  @param p the Particle to apply the Force to; may not be null
	 *  @return the Particle p, after the Force is applied.
	 *  @throws UnsupportedOperationException if the implementing class uses {@link #apply()} instead.
	 *  @throws IllegalStateException optionally, if the Force is currently off, or if the Particle is fixed
	 *  @throws NullPointerException if <code>p == null</code>
	 */
	public abstract Particle apply(Particle p) throws UnsupportedOperationException, IllegalStateException, NullPointerException;
}