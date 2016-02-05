package org.gicentre.utils.spatial;

import processing.core.PVector;

// *****************************************************************************************
/** Defines the minimum behaviour of all map projection classes. Any map projection should
 *  be able to perform a forward and inverse transformation as well as provide a textual
 *  description of the projection type. 
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.4, 5th February, 2016. 
 */ 
// *****************************************************************************************

/* This file is part of giCentre utilities library. gicentre.utils is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * gicentre.utils is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this
 * source code (see COPYING.LESSER included with this source code). If not, see 
 * http://www.gnu.org/licenses/.
 */

public interface MapProjection 
{
	/** Should provide a general description of the transformation.
     *  @return Description of the transformation.
     */
   public abstract String getDescription();
   
   /** Should perform a forward transform on the given coordinates. Applying transformCoords()
    *  followed by invTransformCoords() to a point should leave it unaltered (other than possibly
    *  subject to minor rounding effects).
    *  @param p point location coordinates to transform.
    *  @return Transformed point coordinates.
    */     
  public abstract PVector transformCoords(PVector p);
  
   /** Should perform an inverse transform on the given coordinates. Applying transformCoords()
    *  followed by invTransformCoords() to a point should leave it unaltered (other than possibly
    *  subject to minor rounding effects).
    *  @param p point location coordinates to transform.
    *  @return Transformed point coordinates.
    */     
  public abstract PVector invTransformCoords(PVector p);
}
