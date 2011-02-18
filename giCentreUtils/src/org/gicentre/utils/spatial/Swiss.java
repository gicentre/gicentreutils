package org.gicentre.utils.spatial;

import processing.core.PVector;

//  *******************************************************************************************
/** Class for representing the Swiss coordinate system. This is an oblique Mercator projection
 *  with a 90 degree azimuth, centre at the the old observatory at Bern and scale factor of 1.0. 
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.1, 18th February, 2011. 
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

public class Swiss
{
    // --------------------- Object and class variables ---------------------
    
                                  /** Indicates a transformation from longitude/latitude. */
    public static final int FROM_LAT_LONG = 1;
                                 /** Indicates a transformation to longitude/latitude. */
    public static final int TO_LAT_LONG = 2;
    
    private static final double RAD2DEG = 180.0/Math.PI;
    private static final double DEG2RAD = Math.PI/180.0;
    private static final double PI_OVER_4 = Math.PI/4.0;
    
    private double latOrigin,lngOrigin,
                   falseEasting, falseNorthing;
                                        // Derived constants.
    private double a,e2,e,c,equivLatOrgPrime,K,R;
    
    private int direction;
    
    private Ellipsoid wgs84,bessel1841;
    
    private boolean doInterpolation;
                                      
    // ------------------------ Constructors -----------------------
    
    /** Initialises the transformer assuming a forward transformation is from
      * longitude/latitude to Swiss coordinates. This is equivalent to calling the constructor
      * with FROM_LAT_LONG as the parameter. 
      */
    public Swiss()
    {
        this(FROM_LAT_LONG);
    }
        
    /** Initialises the transformer in the given direction.
      * @param direction Indicates whether transforming to or from longitude/latitude.
      */
    public Swiss(int direction)
    {
        this.direction = direction;
        
        // Projection parameters.
        wgs84         = new Ellipsoid(Ellipsoid.WGS_84);
        bessel1841    = new Ellipsoid(Ellipsoid.BESSEL_1841);
        latOrigin     =  46.95240556*DEG2RAD;   // N 46d 57'  8.660"
        lngOrigin     =  7.43958333*DEG2RAD;    // E  7d 26' 22.500"
        falseEasting  =  600000.0;
        falseNorthing =  200000.0;
        
        // Derived constants.
        a   = bessel1841.getEquatorialRadius();
        e2  = bessel1841.getSquaredEccentricity();
        e   = Math.sqrt(e2);
        c = Math.sqrt(1+((e2 * Math.pow(Math.cos(latOrigin), 4)) / (1-e2))); 
        equivLatOrgPrime = Math.asin(Math.sin(latOrigin) / c);
        K = Math.log(Math.tan(PI_OVER_4 + equivLatOrgPrime/2.0)) -
            c*(Math.log(Math.tan(PI_OVER_4 + latOrigin/2.0)) -
            e/2.0 * Math.log((1+e*Math.sin(latOrigin)) / (1-e*Math.sin(latOrigin))));
        R = a*Math.sqrt(1-e2) / (1-e2*Math.sin(latOrigin) * Math.sin(latOrigin));
    }
    
    // ------------------------- Methods --------------------------
    
    /** Converts the given longitude/latitude coordinates into Swiss grid.  
      * Longitude/latitude should be in decimal degrees with positive North and negative East. 
      * @param p Longitude/latitude point. Longitude varies within +-180 degrees, latitude within +-90 degrees.
      * @return Swiss coordinates of the given longitude/latitude location.
      */
    public PVector latLongToSwiss(PVector p)
    {
        double phi = p.y*DEG2RAD;
        double lamda = p.x*DEG2RAD;
        
        double lngPrime = c*(lamda - lngOrigin);
        double w = c*(Math.log(Math.tan(PI_OVER_4 + phi/2.0)) - 
                   e/2.0 * Math.log((1+e*Math.sin(phi)) / (1-e*Math.sin(phi)))) + K;
        double latPrime = 2.0 * (Math.atan(Math.exp(w)) - PI_OVER_4);
    
        double sinLatDoublePrime = Math.cos(equivLatOrgPrime) * Math.sin(latPrime) -
                                   Math.sin(equivLatOrgPrime) * Math.cos(latPrime) * Math.cos(lngPrime);
        double latDoublePrime = Math.asin(sinLatDoublePrime);
    
        double sinLngDoublePrime = Math.cos(latPrime)*Math.sin(lngPrime) / Math.cos(latDoublePrime);
        double lngDoublePrime = Math.asin(sinLngDoublePrime);

        float northing = (float)(R*Math.log(Math.tan(PI_OVER_4 + latDoublePrime/2.0)) + falseNorthing);
        float easting  = (float)(R*lngDoublePrime + falseEasting);

        return new PVector(easting,northing);
    }
    
    /** Converts the given Swiss coordinates to longitude/latitude location.  
      * @param p Location of point as an easting and northing in the Swiss projection (metres from local origin).
      * @return Longitude/latitude coordinates of the given location (decimal degrees).
      */
    public PVector swissToLatLong(PVector p)
    {
        double easting = p.x;
        double northing = p.y;
        double latDoublePrime = 2*(Math.atan(Math.exp((northing - falseNorthing)/R)) - PI_OVER_4);
        double lngDoublePrime = (easting - falseEasting)/R;

        double sinLatPrime = Math.cos(equivLatOrgPrime)*Math.sin(latDoublePrime) +
                             Math.sin(equivLatOrgPrime)*Math.cos(latDoublePrime)*Math.cos(lngDoublePrime);
        double latPrime    = Math.asin(sinLatPrime);

        double sinLngPrime = Math.cos(latDoublePrime)*Math.sin(lngDoublePrime)/Math.cos(latPrime);
        double lngPrime    = Math.asin(sinLngPrime);

        float lng = (float)((lngPrime/c + lngOrigin)*RAD2DEG);
        float lat = (float)(newtonRaphson(latPrime)*RAD2DEG);
        
        return new PVector(lng,lat);
    }
    
    /** Performs a forward longitude/latitude to Swiss grid transform on the given location.
      * @param p Point coordinates to transform.
      * @return Transformed footprint.
      */     
    public PVector transformCoords(PVector p)
    {
        if (direction == FROM_LAT_LONG)
        {            
            // Convert from WGS84 to Bessel 1841 and project from geographic to Swiss coordinates.
            return latLongToSwiss(wgs84.projectDatum(p,Ellipsoid.BESSEL_1841));
        }
                                
        // Project from Swiss to geographic coordinates and convert from Bessel 1841 to WGS84
        return bessel1841.projectDatum(swissToLatLong(p),Ellipsoid.WGS_84);
    }
    
    /** Performs an inverse Swiss grid to longitude/latitude transform on the given location.
      * @param p Point coordinates to transform.
      * @return Transformed footprint.
      */     
    public PVector invTransformCoords(PVector p)
    {
        if (direction == TO_LAT_LONG)
        {            
            // Convert from WGS84 to Bessel 1841 and project from geographic to Swiss coordinates.
            return latLongToSwiss(wgs84.projectDatum(p,Ellipsoid.BESSEL_1841));
        }
               
        // Project from Swiss to geographic coordinates and convert from Bessel 1841 to WGS84
        return bessel1841.projectDatum(swissToLatLong(p),Ellipsoid.WGS_84);        
    }
    
    /** Provides a general description of the transformation.
      * @return Description of the transformation.
      */
    public String getDescription()
    {
        if (direction == FROM_LAT_LONG)
        {
            return new String("Lat/long to Swiss National Grid transformation.");
        }
        return new String("Swiss National Grid to lat/long transformation.");
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
        return bessel1841; 
    }
    
    // -------------------------- Private Methods -------------------------
    
    /** Performs a Newton-Raphson iteration to estimate latitude using the
      * initial estimation.
      * @param initEstimate Initial estimate.
      * @return Final estimate.
      */
    private double newtonRaphson(double initEstimate)
    {
        double estimate = initEstimate;
        double tol = 0.00001;
        double corr;

        double C = (K - Math.log(Math.tan(PI_OVER_4 + initEstimate/2)))/c;

        do
        {
            corr = corrRatio(estimate, C);
            estimate -= corr;
        }
        while (Math.abs(corr) > tol);

        return estimate;
    }

    /** Used be iteration to estimate latitude.
      * @param lat Initial estimate of latitude.
      * @param C Projection constant.
      * @return Correction to be applied to estimate of latitude.
      */
    private double corrRatio(double lat, double C)
    {
        double corr = (C + Math.log(Math.tan(PI_OVER_4 + lat/2.0)) -
                      e/2.0 * Math.log((1+e*Math.sin(lat)) / 
                      (1-e*Math.sin(lat)))) * (((1-e2*Math.sin(lat)*Math.sin(lat)) * Math.cos(lat)) /
                      (1-e2));
        return corr;
    } 
}