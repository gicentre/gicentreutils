package org.gicentre.utils.spatial;

import processing.core.PVector;

//  ******************************************************************************************
/** Class for representing the Ordnance Survey GB, National Grid coordinate system. This is a
 *  UTM transformation with a modified scale factor and transformed local origin. Can be used
 *  to convert between OSGB National Grid and longitude/latitude coordinate systems. See
 *  <a href="http://www.ordnancesurvey.co.uk/oswebsite/gps/information/coordinatesystemsinfo/guidecontents/index.html"
 *  target="_new">www.ordnancesurvey.co.uk/oswebsite/gps/information/coordinatesystemsinfo/guidecontents</a>
 *  for details of the transformation.
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

public class OSGB extends UTM
{
    // ------------------------ Object variables -------------------------
    
    private double scaleFactor,         // Prime meridian scale factor.
                   latOrigin,lngOrigin,
                   xOffset, yOffset;
    private int direction;
    
    private Ellipsoid wgs84,airy1830;
            
    // --------------------------- Constructors --------------------------
    
    /** Initialises the transformer assuming a forward transformation is into OSGB
      * coordinates. This is equivalent to calling the constructor with FROM_LAT_LONG
      * as the parameter. Assumes that geographic data use the WGS84 ellipsoid. The 
      * transformer will convert data to/from the AIRY_1830 ellipsoid and perform a
      * UTM transformation with a scale factor of 0.9996012717, geographic origin of
      * 49N, 2W and local OSGB origin of E = 400 000, N = -100 000.
      */
    public OSGB()
    {
        this(FROM_LAT_LONG);
    }
        
    /** Initialises the transformer. Assumes that geographic data use
      * the WGS84 ellipsoid. The transformer will convert data to/from the
      * AIRY_1830 ellipsoid and perform a UTM transformation with a
      * scale factor of 0.9996012717, geographic origin of 49N, 2W and 
      * local OSGB origin of E = 400 000, N = -100 000.
      * @param direction Indicates whether transforming to or from longitude/latitude.
      */
    public OSGB(int direction)
    {
        super(new Ellipsoid(Ellipsoid.AIRY_1830),50,-2);
        wgs84 = new Ellipsoid(Ellipsoid.WGS_84);
        airy1830 = getEllipsoid();
        this.direction = direction;
        scaleFactor = 0.9996012717;
        latOrigin   =  49;
        lngOrigin   =  -2;
        xOffset     =  400000;
        yOffset     = -100000;
    }
    
    // ------------------------- Methods --------------------------
    
    /** Performs a forward longitude/latitude to OSGB transformation of the given location.
      * @param p Point coordinates to transform
      * @return Transformed point location.
      */     
    public PVector transformCoords(PVector p)
    {
        if (direction == FROM_LAT_LONG)
        {            
            // Convert from WGS84 to Airy 1830 and project from geographic to OSBG coordinates.
            return latLongToUTM(wgs84.projectDatum(p,Ellipsoid.AIRY_1830),lngOrigin,latOrigin,xOffset,yOffset,scaleFactor);
        }
        // Project from OSGB to geographic coordinates and convert from Airy 1830 to WGS84.
        return airy1830.projectDatum(UTMToLatLong(p, lngOrigin,latOrigin, xOffset,yOffset, scaleFactor),Ellipsoid.WGS_84);
    }
    
    /** Performs an inverse OSGB to longitude/latitude transform of the given location.
      * @param p Point coordinates to transform.
      * @return Transformed point location.
      */     
    public PVector invTransformCoords(PVector p)
    {
        if (direction == FROM_LAT_LONG)
        {                      
            // Project from OSGB to geographic coordinates. and convert from Airy 1830 to WGS84
            return airy1830.projectDatum(UTMToLatLong(p,lngOrigin,latOrigin,xOffset,yOffset,scaleFactor),Ellipsoid.WGS_84);
        }
        // Convert from WGS84 to Airy 1830 and project from geographic to OSBG coordinates.
         return latLongToUTM(wgs84.projectDatum(p,Ellipsoid.AIRY_1830),lngOrigin,latOrigin,xOffset,yOffset,scaleFactor);
    }
    
    /** Provides a general description of the transformation.
      * @return Description of the transformation.
      */
    public String getDescription()
    {
        if (direction == FROM_LAT_LONG)
        {
            return "Lat/long to Ordnance Survey GB National Grid transformation.";
        }
        return "Ordnance Survey GB National Grid to lat/long transformation.";
    }
    
    
    /* * Used for testing accuracy of transformation.
      * @param args Command line arguments (ignored).
      * /       
    public static void main(String args[])
    {
        OSGB trans = new OSGB(FROM_LAT_LONG);
        Footprint fp = new Footprint(1.717921583f,52.65757031f);
        System.out.println("Original is: "+fp);
        System.out.println("Trans is: "+trans.transformCoords(fp));
        
        Footprint fp2 = new Footprint(651409.903f,313177.270f);
        System.out.println("Inv Trans is: "+trans.invTransformCoords(fp2));
    }
    */
}