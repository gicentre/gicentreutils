package org.gicentre.utils.network.traer.physics;

import java.util.Iterator;

//*****************************************************************************************
/** Abstraction of functions capable of performing iterated transformations.
 *  @param <From> Source for transform function.
 *  @param <To> Result of transform function.
 *  @author Carl Pearson with minor modifications by Jo Wood.
 *  @version 3.4, 5th February, 2016.
 */
// *****************************************************************************************

/* This file is included in the giCentre utilities library, but was developed independently by
 * Jeff Traer and Carl Pearson. See http://murderandcreate.com/physics and
 * http://code.google.com/p/traerphysics/. The traer.physics package is distributed under the
 * Artistic Licence: http://dev.perl.org/licenses/
 */
public abstract class Function<From,To> 
{
	/** Should apply the function on the given From value.
	 *  @param from From class upon which to apply the function.
	 *  @return The transformed value.
	 */
	public abstract To apply(From from);

	/** Applies the given sideEffector on the the source.
	 * @param <From> From type
	 * @param <To> To Type
	 * @param source Source upon which to apply the given sideEffector.
	 * @param sideEffector SideEffector to apply to the given data.
	 */
	public static <From,To> void functor(Iterable<From> source, Function<From,To> sideEffector) 
	{
		for (From f : source)
		{
			sideEffector.apply(f);
		}
	}

	
	/** Transforms the given source using the given function.
	 *  @param <To> To type.
	 *  @param <From> From type.
	 *  @param source Source upon which to apply.
	 *  @param transform Function to perform the transformation
	 *  @return An iterable view of the the transformed data.
	 */
	public static <To,From> Iterable<To> transform(final Iterable<From> source, final Function<From,To> transform)
	{
		return new Iterable<To>() 
		{
			public Iterator<To> iterator() 
			{
				return new Iterator<To>()
				{
					Iterator<From> delegate = source.iterator();
					public boolean hasNext() 
					{ 
						return delegate.hasNext(); 
					}
					public To next() 
					{ 
						return transform.apply(delegate.next()); 
					}
					public void remove()
					{ 
						throw new UnsupportedOperationException("Iterator provided via Iterable interface does not support removal.");
					}
				};
			}
		};
	}

	
	/** Combines the given function with this one.
	 *  @param <ToNew> The new type.
	 *  @param otherFunction Other function to combine with this one.
	 *  @return Combined function.
	 */
	public <ToNew> Function<From, ToNew> combine(final Function<To,ToNew> otherFunction) 
	{
		final Function<From,To> local = this;
		
		return new Function<From, ToNew>() 
		{
			@Override 
			public ToNew apply(From arg0) 
			{ 
				return otherFunction.apply(local.apply(arg0));
			}
		};
	}
}