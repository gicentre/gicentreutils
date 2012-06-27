package org.gicentre.utils.spatial;

import processing.core.PVector;

//  ****************************************************************************************
/** Class for representing Albers Equal Area Conic projections. Includes forward and inverse
 *  transforms from/to lat,long. Uses transformation equations from Snyder (1987) Atlas of 
 *  Map Projections. Some code adapted from C program by T. Mittan, Feb, 1992 - see 
 *  <a href="http://geography.usgs.gov/ftp/software/current_software/gctpc2/alberfor.c" target="_new">
 *  http://geography.usgs.gov/ftp/software/current_software/gctpc2/alberfor.c</a>.
 *  @author Jo Wood, giCentre, City University London and T. Mittan.
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

public class AlbersEqualAreaConic implements MapProjection
{
    // ---------------- Object and class variables ----------------
    
    private Ellipsoid ellipsoid;  // Reference ellipsoid.
         
    private boolean doInterpolation;
    private int direction;        // Direction of forward transformation.
    
    private static final double RAD2DEG   = 180.0/Math.PI;
    private static final double DEG2RAD   = Math.PI/180.0;
    private static final double TWO_PI    = Math.PI*2.0;
    private static final double PI_OVER_2 = Math.PI*0.5;
    private static final double DBLLONG   = 4.61168601e18;
    
                                 /** Indicates a transformation from latitude/longitude */
    public static final int FROM_LAT_LONG = 1;
    
                                 /** Indicates a transformation to latitude/longitude */
    public static final int TO_LAT_LONG = 2;
    

    // Projection parameters.
    private double phi1;            // First standard parallel in radians.           
    private double phi2;            // Second standard parallel in radians.
    private double lngCentre;       // Centre longitude in radians.
    private double latCentre;       // Centre latitude in radians.
    private double falseEast;       // x offset in metres
    private double falseNorth;      // y offset in meters

    // Projection constants.
    private double sinRho,cosRho;
    private double eccentricity;
    private double con;
    private double c,ns0,rh;

    private double ms1,ms2,qs0,qs1,qs2;        

    // ------------------------ Constructor -----------------------
    
    /** Initialises the Albers converter with the given ellipsoid and
      * standard parallels. Defaults to a projection centred at lng/lat of
      * (0,0). The forward transformation will be from Lat/long to Albers 
      * equal area conic.
      * @param ellipsoid to use in projection.
      * @param lat1 First standard parallel in degrees (north of equator positive).
      * @param lat2 Second standard parallel in degrees (north of equator positive).
      */
    public AlbersEqualAreaConic(Ellipsoid ellipsoid, double lat1, double lat2)
    {
        this(ellipsoid,lat1,lat2,0,0,0,0);
    }
    
    /** Initialises the Albers converter with the given ellipsoid. The 
      * forward transformation will be from Lat/long to Albers equal 
      * area conic. 
      * @param ellipsoid to use in projection.
      * @param lat1 First standard parallel in degrees (north of equator positive).
      * @param lat2 Second standard parallel in degrees (north of equator positive).
      * @param lon0 Centre longitude in degrees (east of Greenwich positive).
      * @param lat0 Centre latitude in degrees (north of equator positive).
      * @param falseEast False easting (offset in metres).
      * @param falseNorth False northing (offset in metres).
      */
    public AlbersEqualAreaConic(Ellipsoid ellipsoid, double lat1, double lat2, double lon0, double lat0, double falseEast, double falseNorth)
    {
        this.ellipsoid  = ellipsoid;
        doInterpolation = true;
        direction       = FROM_LAT_LONG; 
        this.phi1       = lat1*DEG2RAD;
        this.phi2       = lat2*DEG2RAD;
        this.lngCentre  = lon0*DEG2RAD;
        this.latCentre  = lat0*DEG2RAD;
        this.falseEast  = falseEast;
        this.falseNorth = falseNorth;
            
        // Check we have two separate standard parallels.
        if (Math.abs(phi1 + phi2) < 1.0e-10)
        {
            System.err.println("Standard parallels must be separate (currently set at "+lat1+" and "+lat2+")");
            return;
        }
      
        eccentricity = Math.sqrt(ellipsoid.getSquaredEccentricity());
        sinRho = Math.sin(phi1);
        cosRho = Math.cos(phi1);
        con = sinRho;

        ms1 = msfnz(sinRho,cosRho);
        qs1 = qsfnz(sinRho);

        sinRho = Math.sin(phi2);
        cosRho = Math.cos(phi2);

        ms2 = msfnz(sinRho,cosRho);
        qs2 = qsfnz(sinRho);
        
        sinRho = Math.sin(latCentre);
        cosRho = Math.cos(latCentre);

        qs0 = qsfnz(sinRho);

        if (Math.abs(phi1 - phi2) > 1.0e-10)
        {
            ns0 = (ms1*ms1 - ms2*ms2)/(qs2-qs1);
        }
        else
        {
            ns0 = con;
        }
        
        c = ms1*ms1 + ns0*qs1;
        rh = ellipsoid.getEquatorialRadius()*Math.sqrt(c - ns0*qs0)/ns0;
    }
    
    
    // ------------------------- Methods --------------------------
    
    /** Performs a forward transformation (latitude/longitude to Albers) on the
      * given location.
      * @param p Location of point to transform.
      * @return Transformed location coordinates.
      */     
    public PVector transformCoords(PVector p)
    {
        if (direction == FROM_LAT_LONG)
        {
            return latLongToAlbers(p);
        }
        return AlbersToLatLong(p);
    }
    
    /** Performs an inverse transformation (Albers to latitude/longitude) on the  given location.
      * @param p Location of point to transform.
      * @return Transformed location coordinates.
      */     
    public PVector invTransformCoords(PVector p)
    {
        if (direction == FROM_LAT_LONG)
        {
            return AlbersToLatLong(p);
        }
        return latLongToAlbers(p);
    }
    
    /** Provides a general description of the transformation.
      * @return Description of the transformation.
      */
    public String getDescription()
    {
        if (direction== FROM_LAT_LONG)
        {
            return "Lat/long to Albers conic equal area transformation.";
        }
        return "Albers conic equal area to lat/long transformation."; 
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
       
    /** Converts the given (<code>lng</code>, <code>lat</code>) coordinates into a location referenced
      * using the Albers equal area conic projection.
      * Latitude/longitude coordinates should be in decimal degrees with positive North and positive East. 
      * @param p Longitude/latitude point. Longitude varies within +-180 degrees, latitude within +-90 degrees.
      * @return Location (units in metres) using Albers coordinate system.
      */ 
    public PVector latLongToAlbers(PVector p)
    {
        double phi     = p.y*DEG2RAD;
        double lambda  = p.x*DEG2RAD;
        
        double qs = qsfnz(Math.sin(phi));
        double rh1 = ellipsoid.getEquatorialRadius() * Math.sqrt(c - ns0 * qs)/ns0;
        double theta = ns0 * adjustLong(lambda-lngCentre); 

        return new PVector((float)(rh1 * Math.sin(theta) + falseEast),
                           (float)(rh - rh1 * Math.cos(theta) + falseNorth));
    }
     
    /** Converts the given coordinates that use the Albers equal area conic projection
      * into a (longitude,latitude) location. 
      * Longitude and latitude will be in decimal degrees with positive North and positive East. 
      * @param p Location of point as an easting and northing in the Albers projection (metres from local origin).
      * @return location in geographical coordinates (longitude/latitude decimal degrees).
      */ 
    public PVector AlbersToLatLong(PVector p)
    {
        double rh1;          // Height above ellipsoid.
        double qs;       
        double con1;         // Temporary sign value.
        double theta;
        long   flag;         // Error flag.
        double easting = p.x;
        double northing = p.y;
        
        double phi,lambda;

        flag = 0;
        easting -= falseEast;
        northing = (float)(rh - northing + falseNorth);
        
        if (ns0 >= 0)
        {
            rh1 = Math.sqrt(easting*easting + northing*northing);
            con1 = 1.0;
        }
        else
        {
            rh1 = -Math.sqrt(easting*easting + northing*northing);
            con1 = -1.0;
        }

        theta = 0.0;
        if (rh1 != 0.0)
        {
            theta = Math.atan2(con1*easting, con1*northing);
        }
        
        con1 = rh1 * ns0 / ellipsoid.getEquatorialRadius();
        qs = (c - con1 * con1) / ns0;
        if (eccentricity >= 1e-10)
        {
            con1 = 1 - .5 * (1.0 - ellipsoid.getSquaredEccentricity()) * Math.log((1.0 - eccentricity) / (1.0 + eccentricity))/eccentricity;
   
            if (Math.abs(Math.abs(con1) - Math.abs(qs)) > .0000000001 )
            {
                phi = phi1z(qs);
      
                if (flag == Double.NaN)
                {
                    System.err.println("Problem calculating inverse Albers projection");
                    return null;
                }
            }
            else
            {
                if (qs >= 0)
                {
                    phi = PI_OVER_2;
                }
                else
                {
                    phi = -PI_OVER_2;
                }
            }
        }
        else
        {
            phi = phi1z(qs);
            if (flag == Double.NaN)
            {
                System.err.println("Problem calculating inverse Albers projection");
                return null;
            }
        }

        lambda = adjustLong(theta/ns0 + lngCentre);
        
        return new PVector((float)(lambda*RAD2DEG),(float)(phi*RAD2DEG));
    }
       
    /** Reports the ellipsoid used in the transformation.
      * @return Ellipsoid used in the UTM transformation.
      */
    public Ellipsoid getEllipsoid()
    {
        return ellipsoid; 
    }
    
    // ----------------------------- Private methods ----------------------------
    
    /** Computes the constant small m which is the radius of a parallel of latitude,
      * phi, divided by the semimajor axis.
      * @param sinPhi Sine of latitude.
      * @param cosPhi Cosine of latitude.
      */
    private double msfnz(double sinPhi, double cosPhi)
    {
        double con1 = eccentricity * sinPhi;
        return((cosPhi / (Math.sqrt (1.0 - con1 * con1))));
    }
    
    /** Computes the constant small q which is the radius of a parallel of latitude,
      * phi, divided by the semimajor axis.
      * @param sinPhi Sine of latitude.
      */
    private double qsfnz(double sinPhi)
    {
        double con1;

        if (eccentricity > 1.0e-7)
        {
            con1 = eccentricity * sinPhi;
            return (( 1.0 - eccentricity*eccentricity)*(sinPhi/(1.0 - con1*con1) - (.5/eccentricity)*Math.log((1.0 - con1)/(1.0 + con1))));
        }
        return(2.0 * sinPhi);
    }
    
    /** Adjusts a longitude angle to range from -180 to 180 degrees (in radians).
      * @param origLng Longitude value to adjust.
      * @return Longitude value guaranteed to be within +- 180 (in radians).
      */
    private double adjustLong(double origLng) 
    {
        long count = 0;
        double lng = origLng;
        for(;;)
        {
            if (Math.abs(lng) <= Math.PI)
            {
                break;
            }
            else if (((long) Math.abs(lng/Math.PI)) < 2)
            {
                lng = lng -(lng<0?-1:1*TWO_PI);
            }
            else if (((long) Math.abs(lng / TWO_PI)) < Long.MAX_VALUE)
            {
                lng = lng-(((long)(lng / TWO_PI))*TWO_PI);
            }
            else if (((long) Math.abs(lng / (Long.MAX_VALUE*TWO_PI))) < Long.MAX_VALUE)
            {
                lng = lng-(((long)(lng / (Long.MAX_VALUE*TWO_PI))) * (TWO_PI*Long.MAX_VALUE));
            }
            else if (((long) Math.abs(lng / (DBLLONG * TWO_PI))) < Long.MAX_VALUE)
            {
                lng = lng-(((long)(lng / (DBLLONG * TWO_PI))) * (TWO_PI*DBLLONG));
            }
            else
            {
                lng = lng-(lng<0?-1:1 *TWO_PI);
            }
            count++;
  
            if (count > 4)
                break;
        }

        return(lng);
    }
    
    /** Computes phi1, the latitude for the inverse of the Albers Conic Equal-Area projection.
     *  @param qs Angle in radians.
     *  @return phi1 or Double.NaN if error computing value.
     */
    private double phi1z (double qs)
    {
        double eccntSq = ellipsoid.getSquaredEccentricity();
        double dphi;
        double con1;
        double com;
        double sinpi;
        double cospi;
        double phi;

        phi = asinz(.5 * qs);
        if (eccentricity < 1.0e-10) 
        {
            return(phi);
        }
            
        eccntSq = eccentricity * eccentricity;
         
        for (int i=1; i<=25; i++)
        {
            sinpi = Math.sin(phi);
            cospi = Math.cos(phi);
            con1 = eccentricity * sinpi; 
            com = 1.0 - con1*con1;
            dphi = .5 * com*com/cospi * (qs/(1.0-eccntSq) - sinpi / com + 
                   .5 / eccentricity * Math.log ((1.0 - con1) / (1.0 + con1)));
            phi = phi + dphi;
                        
            if (Math.abs(dphi) <= 1e-7)
            {
                return(phi);  
            }
        }
        System.err.println("Convergence error when calculating inverse Albers projection");
        return Double.NaN;
    }
    
    /** Calculates the inverse sin and eliminates rounding errors.
      * @param origCon Value to calculate inverse sine from.
      * @return Inverse sine.
      */
    private double asinz (double origCon)
    {
    	double con1 = origCon;
        if (Math.abs(con1) > 1.0)
        {
            if (con1 > 1.0)
            {
                con1 = 1.0;
            }
            else
            {
                con1 = -1.0;
            }
        }
        return(Math.asin(con1));
    }
}