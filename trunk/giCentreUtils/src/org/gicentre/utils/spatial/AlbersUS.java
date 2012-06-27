package org.gicentre.utils.spatial;

//  ****************************************************************************************
/** Class for representing an Albers projection for all United States. This is an Albers equal
 *  area conic projection with a GRS_1980 ellipsoid and adjusted centre.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.2.2, 27th June, 2012.  
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

public class AlbersUS extends AlbersEqualAreaConic
{

    // ----------------------------------- Constructor -----------------------------------
    
    /** Initialises the Albers US projection. The forward transformation
     *  will be from lat/long to Albers. 
     */
    public AlbersUS()
    {
        super(new Ellipsoid(Ellipsoid.GRS_1980),20.0,60.0,-96.0,40.0,0.0,0.0);
    }
    
    // ------------------------------------- Methods -------------------------------------
    
    /** Provides a general description of the forward transformation.
      * @return Description of the transformation.
      */
    public String getDescription()
    {
        return "Lat/long to Albers US (all states) transformation.";
    }
}