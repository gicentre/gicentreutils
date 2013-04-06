package org.gicentre.utils.spatial;

//  ****************************************************************************************
/** Class for representing an Albers British Columbia projection. This is an Albers equal
 *  area conic projection with a GRS_1980 ellipsoid and false origin.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.3, 27th June, 2012.  
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

public class AlbersBC extends AlbersEqualAreaConic
{

    // ----------------------------------- Constructor -----------------------------------
    
    /** Initialises the Albers British Columbia projection. The forward transformation
     *  will be from lat/long to Albers. 
     */
    public AlbersBC()
    {
        super(new Ellipsoid(Ellipsoid.GRS_1980),50,58.5,-126.0,45.0,1000000.0,0.0);
    }
    
    // ------------------------------------- Methods -------------------------------------
    
    /** Provides a general description of the forward transformation.
      * @return Description of the transformation.
      */
    public String getDescription()
    {
        return "Lat/long to Albers British Columbia transformation.";
    }
}