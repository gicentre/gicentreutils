package org.gicentre.utils.spatial;

import processing.core.PVector;

//  *****************************************************************************************
/** Class for representing the French national coordinate system. This is a Lambert conformal
 *  conic projection divided into one of 4 zones, plus a modified zone II projection for 
 *  national maps. 
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

public class FrenchNTF implements MapProjection
{
    // --------------------- Object and class variables ---------------------
    
                                 /** Indicates a transformation from longitude/latitude. */
    public static final int FROM_LAT_LONG = 1;
                                 /** Indicates a transformation to longitude/latitude. */
    public static final int TO_LAT_LONG = 2;
    
    private static final double RAD2DEG = 180.0/Math.PI;
    private static final double DEG2RAD = Math.PI/180.0;
    private static final double PI_OVER_2 = Math.PI/2.0;
    private static final double PI_OVER_4 = Math.PI/4.0;
    
    private static final int ZONE_I   = 1;
    private static final int ZONE_II  = 2;
    private static final int ZONE_III = 3;
    private static final int ZONE_IV  = 4;
    private static final int ZONE_IIe = 5;
    
    private double latOrigin,lngOrigin,     // Projection parameters.
                   phi1,phi2,
                   falseEasting, falseNorthing;
    
    private double es,esOver2,aF,n,rho0;    // Projection constants.
 
    private int direction,zone;
    
    private Ellipsoid clarke1880,wgs84;
    
    private boolean doInterpolation;
                                      
    // ------------------------ Constructors -----------------------
    
    /** Initialises the transformer assuming a forward transformation is from
      * longitude/latitude to French National Grid coordinates using the national 'Lambert IIe'.
      * projection.
      */
    public FrenchNTF()
    { 
        this("2e",FROM_LAT_LONG);
    }
    
    /** Initialises the transformer assuming a forward transformation is from
      * longitude/latitude to French National Grid coordinates. This is equivalent to calling the constructor
      * with FROM_LAT_LONG as the parameter.
      * @param zone Lambert zone (one of "1", "2", "3", "4" or "2e") 
      */
    public FrenchNTF(String zone)
    {
        this(zone,FROM_LAT_LONG);
    }
    
    /** Initialises the transformer in the given direction assuming the 
      * national 'Lambert IIe' projection.
      * @param direction Indicates whether transforming to or from longitude/latitude.
      */
    public FrenchNTF(int direction)
    { 
       this("2e",direction);
    }
   
    /** Initialises the transformer in the given direction.
      * @param zoneText Lambert zone (one of '1', '2', '3', '4' or '2e'). 
      * @param direction Indicates whether transforming to or from longitude/latitude.
      */
    public FrenchNTF(String zoneText, int direction)
    {
        this.direction = direction;
        
        // Projection parameters.
        clarke1880 = new Ellipsoid(Ellipsoid.CLARKE_1880);
        wgs84      = new Ellipsoid(Ellipsoid.WGS_84);
        
        setZone(zoneText);
    }
    
    // ------------------------- Methods --------------------------
    
    /** Converts the given longitude/latitude coordinates into Lambert conformal conic using
      * the appropriate zone. Longitude andlatitude should be in decimal degrees with positive North
      * and positive East. 
      * @param p Longitude/latitude point. Longitude varies within +-180 degrees, latitude within +-90 degrees.
      * @return French coordinates of the given longitude/latitude location.
      */
    public PVector latLongToFrench(PVector p)
    {
        double phi = p.y*DEG2RAD;
        double lamda = p.x*DEG2RAD;
        
        double slat,
               es_sin,
               t,
               rho,
               dlam,
               theta;

        if (Math.abs(Math.abs(phi) - PI_OVER_2) > 1.0e-10)
        {
            slat = Math.sin(phi);
            es_sin = es*slat;
            t = Math.tan(PI_OVER_4 - phi/2) / Math.pow((1.0 - es_sin) / (1.0 + es_sin), esOver2);
            rho = aF * Math.pow(t, n);
        }
        else
        {
            if ((phi * n) <= 0)
            { 
                System.err.println("Warning: Cannot project point at lat="+p.y+" lng="+p.x);
                return new PVector((float)falseEasting, (float)falseNorthing);
            }
            rho = 0.0;
        }
            
        dlam = lamda - lngOrigin;
        theta = n * dlam;
        
        float easting  = (float)(rho * Math.sin(theta) + falseEasting);
        float northing = (float)(rho0 - rho * Math.cos(theta) + falseNorthing);

        return new PVector(easting,northing);
    }

    /** Converts the given French coordinates to longitude/latitude location assuming 
      * the zone defined in the constructor.  
      * @param p Location of point as an easting and northing in the French NTF projection (metres from local origin).
      * @return Longitude/latitude coordinates of the given location (decimal degrees).
      */
    public PVector frenchToLatLong(PVector p)
    {
        double easting = p.x;
        double northing = p.y;
        double t;
        double phiHat;
        double tempPhi = 0.0;
        double sin_Phi;
        double es_sin;
        double theta = 0.0;
        double tolerance = 4.85e-10;
        
        double lambda,phi;
 
        double dy = northing - falseNorthing;
        double dx = easting - falseEasting;
        double rho0_MINUS_dy = rho0 - dy;
        double rho = Math.sqrt(dx * dx + (rho0_MINUS_dy) * (rho0_MINUS_dy));
        
        if (n < 0.0)
        {
            rho *= -1.0;
            dy *= -1.0;
            dx *= -1.0;
            rho0_MINUS_dy *= -1.0;
        }
        
        if (rho != 0.0)
        {
            theta = Math.atan2(dx, rho0_MINUS_dy);
            t = Math.pow(rho / aF , 1.0 / n);
            phiHat = PI_OVER_2 - 2.0 * Math.atan(t);
            while (Math.abs(phiHat - tempPhi) > tolerance)
            {
                tempPhi = phiHat;
                sin_Phi = Math.sin(phiHat);
                es_sin = es*sin_Phi;
                phiHat = PI_OVER_2 - 2.0 * Math.atan(t * Math.pow((1.0 - es_sin) / (1.0 + es_sin), esOver2));
            }
            phi = phiHat;
            lambda = theta / n + lngOrigin;
         
            // Make sure transformed values are within limits.
            if (Math.abs(phi) < 2.0e-7)  // Force latitude to 0 to avoid -0 degrees.
            {
                phi = 0.0;
            }
            
            if (phi > PI_OVER_2)
            {
                phi = PI_OVER_2;
            }
            else if (phi < -PI_OVER_2)
            {
                phi = -PI_OVER_2;
            }
        
            if (lambda > Math.PI)
            {
                if (lambda - Math.PI < 3.5e-6)
                {
                    lambda = Math.PI;
                }
            }
            if (lambda < -Math.PI)
            {
                if (Math.abs(lambda + Math.PI) < 3.5e-6)
                {
                    lambda = -Math.PI;
                }
            }
        
            if (Math.abs(lambda) < 2.0e-7)  // Force longitude to 0 to avoid -0 degrees.
            {
                lambda= 0.0;
            }

            if (lambda > Math.PI) 
            {
                lambda = Math.PI;
            }
            else if (lambda < -Math.PI)
            {
                lambda= -Math.PI;
            }
        }
        else
        {
            if (n > 0.0)
            {
                phi = PI_OVER_2;
            }
            else
            {
                phi = -PI_OVER_2;
            }
                
            lambda = lngOrigin;
        }
        
        float lng = (float)(lambda*RAD2DEG);
        float lat = (float)(phi*RAD2DEG);
        
        return new PVector(lng,lat);
    }
    
    /** Performs a forward longitude/latitude to French grid transform on the given location.
      * @param p point location coordinates to transform.
      * @return Transformed location.
      */     
    public PVector transformCoords(PVector p)
    {
        if (direction == FROM_LAT_LONG)
        {
            // Convert from WGS84 to Clarke 1880 and project from geographic to French coordinates.
            return latLongToFrench(wgs84.projectDatum(p,Ellipsoid.CLARKE_1880));
        }
        
        // Project from French to geographic coordinates and from Clarke 1830 to WGS84
        return clarke1880.projectDatum(frenchToLatLong(p),Ellipsoid.WGS_84);
    }
    
    /** Performs an inverse French grid to lat/long transform on the given location.
      * @param p point location coordinates to transform.
      * @return Transformed footprint.
      */     
    public PVector invTransformCoords(PVector p)
    {
        if (direction == TO_LAT_LONG)
        {            
            // Convert from French to geographic coordinates and from Clarke 1830 to WGS84 ellipsoid.
            return clarke1880.projectDatum(latLongToFrench(p),Ellipsoid.WGS_84);
        }
            
        // Convert from WGS84 to Clarke 1880 and project from geographic to French coordinates.
        return frenchToLatLong(wgs84.projectDatum(p,Ellipsoid.CLARKE_1880)); 
    }
    
    /** Provides a general description of the transformation.
      * @return Description of the transformation.
      */
    public String getDescription()
    {
        if (direction == FROM_LAT_LONG)
        {
            return "Lat/long to French NTF National Grid transformation.";
        }
        return "French NTF National Grid to lat/long transformation.";
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
    
    /** Reports the ellipsoid used in the transformation (Clarke 1880).
      * @return Ellipsoid used in the transformation.
      */
    public Ellipsoid getEllipsoid()
    {
        return clarke1880; 
    }
    
    /** Sets the zone and projection parameters for subsequent transformations.
      * @param zoneText Text describing zone. Should be one of '1', '2', '3', '4' or '2e'.
      */ 
    public void setZone(String zoneText)
    {
        if (zoneText.equals("1"))
        {
            zone = ZONE_I;
        }
        else if (zoneText.equals("2"))
        {
            zone = ZONE_II;
        }
        else if (zoneText.equals("3"))
        {
            zone = ZONE_III;
        }
        else if (zoneText.equals("4"))
        {
            zone = ZONE_IV;
        }
        else if (zoneText.equalsIgnoreCase("2e"))
        {
            zone = ZONE_IIe;
        }
        else
        {
            System.err.println("Unknown Lambert Zone '"+zoneText+"' provided to FrenchNTF projection. Assuming 'IIe'");
            zone = ZONE_IIe;
        }
        
        switch (zone)
        {
            case ZONE_I:
                latOrigin = 49.5*DEG2RAD;           // N 49d 30'
                phi1      = 50.39591167*DEG2RAD;    // N 50d 23' 45.282"
                phi2      = 48.59852278*DEG2RAD;    // N 48d 35' 54.682"
                falseEasting  = 600000.0;
                falseNorthing = 200000.0;
                break;
            
            case ZONE_II:
            case ZONE_IIe:
                latOrigin = 46.8*DEG2RAD;           // N 46d 48'
                phi1      = 47.69601444*DEG2RAD;    // N 47d 41' 45.652"
                phi2      = 45.89891889*DEG2RAD;    // N 45d 53' 56.108"
                falseEasting  = 600000.0;
                falseNorthing = 200000.0;
                if (zone == ZONE_IIe)
                {
                    falseNorthing = 2200000;
                }
                break;
            
            case ZONE_III:
                latOrigin = 44.1*DEG2RAD;           // N 44d 06'
                phi1      = 44.99609389*DEG2RAD;    // N 44d 59' 45.938"
                phi2      = 43.19929139*DEG2RAD;    // N 43d 11' 57.449"
                falseEasting  = 600000.0;
                falseNorthing = 200000.0;
                break;
                
            case ZONE_IV:
                latOrigin = 42.165*DEG2RAD;         // N 42d 09' 54"
                phi1      = 42.76766333*DEG2RAD;    // N 42d 46' 03.588"
                phi2      = 41.56038778*DEG2RAD;    // N 41d 33' 37.396"
                falseEasting  = 234358.0;
                falseNorthing = 185861.369;
                break;
        }
       
        // Derived constants.
        lngOrigin = 2.337229167*DEG2RAD;    // Paris meridian at 2d 20' 14.025"
        
        double a = clarke1880.getEquatorialRadius();
        double es2 = clarke1880.getSquaredEccentricity();
        es = Math.sqrt(es2);
        esOver2 = es/2.0;
        
        // Latitudinal origin.
        double slat = Math.sin(latOrigin);
        double es_sin = es*slat; 
        double t0 = Math.tan(PI_OVER_4 - latOrigin / 2) / Math.pow((1.0 - es_sin) / (1.0 + es_sin), esOver2);
        
        // First standard parallel.
        double slat1 = Math.sin(phi1);
        double clat = Math.cos(phi1);
        es_sin = es*slat1;
        double m1 = (clat / Math.sqrt(1.0 - es_sin * es_sin));
        double t1 = Math.tan(PI_OVER_4 - phi1/2) / Math.pow((1.0 - es_sin) / (1.0 + es_sin), esOver2);
  
        // Second standard parallel.
        slat = Math.sin(phi2);
        clat = Math.cos(phi2);
        es_sin = es*slat;
        double m2 = (clat / Math.sqrt(1.0 - es_sin * es_sin));
        double t2 = Math.tan(PI_OVER_4 - phi2/2) / Math.pow((1.0 - es_sin) / (1.0 + es_sin), esOver2);
        n = Math.log(m1 / m2) / Math.log(t1 / t2);
              
        
        double F = m1 / (n * Math.pow(t1, n));
        aF = a*F;
        if ((t0 == 0) && (n < 0))
        {
             rho0 = 0.0;
        }
        else
        {
            rho0 = aF * Math.pow(t0, n);
        }
    }
   
    /** Reports the Lambert zone number used for the projection. Numbers vary
      * from 1 to 4 with 1-3 being north, central and southern sections of France,
      * 4 being Corsica. Note the  national 'etendu' zone is 2.
      * @return Lambert zone number (latitudinal zone).
      */ 
    public int getZoneNumber()
    {
        switch (zone)
        {
            case ZONE_I:
                return 1;
                
            case ZONE_II:
            case ZONE_IIe:
                return 2;
                
            case ZONE_III:
                return 3;
                
            case ZONE_IV:
                return 4;
        }
        
        // We should never get to this line.
        System.err.println("Unknown French NTF zone. Assuming II");
        return 2;
    }
   
    /** Reports the Lambert zone letter. This is 'e' for the national 'etendu' projection
      * or ' ' if not.  
      * @return Lambert zone letter (latitudinal zone).
      */ 
    public char getZoneLetter()
    {
        if (zone == ZONE_IIe)
        {
            return 'e';
        }
        return ' ';
    }
}