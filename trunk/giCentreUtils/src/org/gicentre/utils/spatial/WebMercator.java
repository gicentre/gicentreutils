package org.gicentre.utils.spatial;

import processing.core.PVector;

//  *******************************************************************************************
/** Class for representing the Web Mercator coordinate system. This is a Mercator projection
 *  assuming a spherical rather than ellipsoidal figure of the earth. Used for transforming
 *  to/from projected GoogleMaps, OpenStreetMaps and BingMaps.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.2, 1st August, 2011. 
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

public class WebMercator
{
    // --------------------- Object and class variables ---------------------
    
                                  /** Indicates a transformation from longitude/latitude. */
    public static final int FROM_LAT_LONG = 1;
                                 /** Indicates a transformation to longitude/latitude. */
    public static final int TO_LAT_LONG = 2;
    
    private static final double RAD2DEG = 180.0/Math.PI;
    private static final double DEG2RAD = Math.PI/180.0;
    private static final double PI_OVER_4 = Math.PI/4.0;
    private static final double PI_OVER_2 = Math.PI/2.0;
          
    private int direction;
    
    private Ellipsoid sphere;
    private double R, lamda0;
    
    private boolean doInterpolation;
                                      
    // ------------------------ Constructors -----------------------
    
    /** Initialises the transformer assuming a forward transformation is from
      * longitude/latitude WGS84 to Web (spherical) Mercator coordinates. This is 
      * equivalent to calling the constructor with FROM_LAT_LONG as the parameter. 
      */
    public WebMercator()
    {
        this(FROM_LAT_LONG);
    }
        
    /** Initialises the transformer in the given direction.
      * @param direction Indicates whether transforming to or from longitude/latitude.
      */
    public WebMercator(int direction)
    {
        this.direction = direction;
        sphere = new Ellipsoid(Ellipsoid.SPHERE); 
        R = sphere.getEquatorialRadius();			// Spherical system with constant radius.
        lamda0 = 0;									// Prime meridian through Greenwich.
    }
    
    // ------------------------- Methods --------------------------
    
    /** Converts the given longitude/latitude coordinates into Web (spherical) Mercator coordinates.  
      * Longitude/latitude should be in decimal degrees with positive North and negative East. 
      * @param p Longitude/latitude point. Longitude varies within +-180 degrees, latitude within +-88 degrees.
      * @return Web Mercator coordinates of the given longitude/latitude location or null if input coordinates are out of bounds.
      */
    public PVector latLongToWebMercator(PVector p)
    {
    	if ((p.x < -180) || (p.x > 180))
    	{
    		System.err.println("latLongToWebMercator: Longitude out of bounds: "+p.x);
    		return null;
    	}
    	if ((p.y < -88) || (p.y > 88))
    	{
    		System.err.println("latLongToWebMercator: Latitude out of bounds: "+p.y);
    		return null;
    	}
    	
        double phi = p.y*DEG2RAD;
        double lamda = p.x*DEG2RAD;
        
        double easting  = R*(lamda-lamda0);
        double northing = R*Math.log(Math.tan(PI_OVER_4 + phi/2));
        
        return new PVector((float)easting,(float)northing);
    }
    
    /** Converts the given Web Mercator coordinates to WGS84 longitude/latitude location.  
      * @param p Location of point as an easting and northing in the Web Mercator projection.
      * @return Longitude/latitude coordinates of the given location (decimal degrees).
      */
    public PVector webMercatorToLatLong(PVector p)
    {
        double easting = p.x;
        double northing = p.y;
        
        double D = -northing/R;
        
        double lat = RAD2DEG*(PI_OVER_2 - 2*Math.atan(Math.exp(D)));
        double lng = RAD2DEG*easting/R;
       
        return new PVector((float)lng,(float)lat);
    }
    
    /** Performs a forward longitude/latitude to Web Mercator grid transform on the given location.
      * @param p Point coordinates to transform.
      * @return Transformed footprint.
      */     
    public PVector transformCoords(PVector p)
    {
        if (direction == FROM_LAT_LONG)
        {            
            return latLongToWebMercator(p);
        }
                                
        return webMercatorToLatLong(p);
    }
    
    /** Performs an inverse Web Mercator grid to longitude/latitude transform on the given location.
      * @param p Point coordinates to transform.
      * @return Transformed footprint.
      */     
    public PVector invTransformCoords(PVector p)
    {
        if (direction == TO_LAT_LONG)
        {            
        	return latLongToWebMercator(p);
        }
               
        return webMercatorToLatLong(p);      
    }
    
    /** Provides a general description of the transformation.
      * @return Description of the transformation.
      */
    public String getDescription()
    {
        if (direction == FROM_LAT_LONG)
        {
            return new String("Lat/long to Web Mercator transformation.");
        }
        return new String("Web Mercator to lat/long transformation.");
    }
    
    /** Indicates whether the transformation should use nearest neighbour (false)
      * or some interpolator (true).
      * @return True if transformation should perform some local interpolation.
      */
    public boolean doInterpolation()
    {
        return doInterpolation; 
    }
    
    /** Sets whether the transformation should use nearest neighbour (false)
      * or some interpolator (true).
      * @param doInterpolation True if transformation should perform some local interpolation.
      */
    public void setInterpolation(boolean doInterpolation)
    {
        this.doInterpolation = doInterpolation; 
    }
    
    /** Reports the ellipsoid used in the transformation.
      * @return Ellipsoid used in the transformation.
      */
    public Ellipsoid getEllipsoid()
    {
        return sphere; 
    }
    
}