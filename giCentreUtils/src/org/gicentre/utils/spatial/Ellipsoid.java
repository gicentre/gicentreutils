package org.gicentre.utils.spatial;

import java.io.Serializable;

import processing.core.PVector;

//  ****************************************************************************************
/** Stores an ellipsoid representation for global map projections.
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

public class Ellipsoid implements Serializable
{
    // --------------------- Object Variables ----------------------
    
    private int id;      // Ellipsoid ID.
    private double a,b;  // Equatorial and polar radii (major and minor semi-axes).  
    private double e2;   // Squared ellipsoid eccentricity.
    private double n;    // Flattening (speeds up projection calculations).
    
    private static final double RAD_TO_DEG = 180.0/Math.PI;
    private static final double DEG_TO_RAD = Math.PI/180.0;
    
                                /** Undefined ellipsoid. */
    public static final int UNDEFINED = -1;
                                /** Airy 1830 ellipsoid (used by OSGB National Grid). */
    public static final int AIRY_1830 = 1;
                                /** Australian National ellipsoid. */
    public static final int AUSTRALIAN_NATIONAL = 2;
                                /** Bessel 1841 ellipsoid (used by Swiss 1903 datum). */
    public static final int BESSEL_1841 = 3;
                                /** Bessel 1841 (Namibia) ellipsoid. */
    public static final int BESSEL_1841_NAMIBIA = 4;
                                /** Clarke 1866 ellipsoid (used by NAD 1927 datum). */  
    public static final int CLARKE_1866 = 5;
                                /** Clarke 1880 ellipsoid. */  
    public static final int CLARKE_1880 = 6;
                                /** Everest ellipsoid. */  
    public static final int EVEREST = 7;
                                /** Fischer 1960 (Mercury) ellipsoid. */  
    public static final int FISCHER_1960 = 8;
                                /** Fischer 1968 ellipsoid. */  
    public static final int FISCHER_1968 = 9;
                                /** GRS 1967 ellipsoid. */  
    public static final int GRS_1967 = 10;
                                /** GRS 1980 ellipsoid (used by NAD 1983 datum). */
    public static final int GRS_1980 = 11;
                                /** Helmert 1906 ellipsoid. */  
    public static final int HELMERT_1906 = 12; 
                                /** Hough ellipsoid. */  
    public static final int HOUGH = 13;  
                                /** International ellipsoid. */  
    public static final int INTERNATIONAL = 14;
                                /** Krassovsky ellipsoid. */  
    public static final int KRASSOVSKY = 15;
                                /** Modified Airy ellipsoid. */  
    public static final int MODIFIED_AIRY = 16;
                                /** Modified Everest ellipsoid. */  
    public static final int MODIFIED_EVEREST = 17;
                                /** Modified Fischer 1960 ellipsoid. */  
    public static final int MODIFIED_FISCHER_1960 = 18;
                                /** South American ellipsoid. */  
    public static final int SOUTH_AMERICAN = 19;
                                /** WGS 60 ellipsoid. */  
    public static final int WGS_60 = 20;
                                /** WGS 66 ellipsoid. */  
    public static final int WGS_66 = 21;
                                /** WGS 72 ellipsoid. */  
    public static final int WGS_72 = 22;
                                /** WGS 84 ellipsoid. */  
    public static final int WGS_84 = 23;
    							/** Standard sphere. */  
    public static final int SPHERE = 99;
        
                      /** Used to ensure consistency when serializing and deserializing. */  
    static final long serialVersionUID = -4698774124137349325L;
    
    // -------------------- Constructor -------------------------
    
    /** Creates an ellipsoid using the given ID.
      * @param id Ellipsoid ID.
      */
    public Ellipsoid(int id)
    {
        switch (id)
        {
            case AIRY_1830:
                store(id,6377563.396, 0.0066705397616);
                break;
            case AUSTRALIAN_NATIONAL:
                store(id,6378160, 0.006694542);
                break;
            case BESSEL_1841:
                store(id,6377397, 0.006674372);
                break;
            case BESSEL_1841_NAMIBIA:
                store(id,6377484, 0.006674372);
                break;
            case CLARKE_1866:
                store(id,6378206, 0.006768658);
                break;
            case CLARKE_1880:
                store(id,6378249, 0.006803511);
                break;
            case EVEREST:
                store(id,6377276, 0.006637847);
                break;
            case FISCHER_1960:
                store(id,6378166, 0.006693422);
                break;
            case FISCHER_1968:
                store(id,6378150, 0.006693422);
                break;
            case GRS_1967:
                store(id,6378160, 0.006694605);
                break;
            case GRS_1980:
                store(id,6378137, 0.00669438);
                break;
            case HELMERT_1906:
                store(id,6378200, 0.006693422);
                break;
            case HOUGH:
                store(id,6378270, 0.00672267);
                break;
            case INTERNATIONAL:
                store(id,6378388, 0.00672267);
                break;
            case KRASSOVSKY:
                store(id,6378245, 0.006693422);
                break;
            case MODIFIED_AIRY:
                store(id,6377340.189, 0.00667054);
                break;
            case MODIFIED_EVEREST:
                store(id,6377304, 0.006637847);
                break;
            case MODIFIED_FISCHER_1960:
                store(id,6378155, 0.006693422);
                break;
            case SOUTH_AMERICAN:
                store(id,6378160, 0.006694542);
                break;
            case WGS_60:
                store(id,6378165, 0.006693422);
                break;
            case WGS_66:
                store(id,6378145, 0.006694542);
                break;
            case WGS_72:
                store(id,6378135, 0.006694318);
                break;
            case WGS_84:
                store(id,6378137, 0.00669438);
                break;
            case SPHERE:
                store(id,6378137, 0);
                break;
            default:
                store(-1,-1,-1);
                break;
        }
    }
    
    /** Reports the ID of this ellipsoid. For example, <code>Ellipsoid.WGS84</code>.
      * @return ID of this ellipsoid.
      */
    public int getID()
    {
        return this.id;
    }
    
    /** Reports the equatorial radius represented by this ellipsoid.
      * In projection terms, this is the ellipsoid's semi-major axis. 
      * @return Equatorial radius of this ellipsoid.
      */
    public double getEquatorialRadius()
    {
        return a;
    }
    
    /** Reports the polar radius represented by the ellipsoid. In projection
      * terms this is the ellipsoid's semi-minor axis.
      * @return Polar radius of the ellipsoid.
      */
    public double getPolarRadius()
    {
        return b; 
    }

    /** Reports the squared eccentricity of this ellipsoid.
      * @return eccentricity Squared eccentricity of this ellipsoid.
      */
    public double getSquaredEccentricity()
    {
        return e2;
    }
    
    /** Reports the degree of flattening as the ratio (a-b)/(a+b).
      * @return Degree of flattening.
      */
    public double getN()
    {
        return n;  
    }
    
    /** Reports the name associated with this ellipsoid.
      * @return Name of this ellipsoid.
      */
    public String getName()
    {
        return getName(this.id);
    }
    
    // ------------- Datum conversions ------------- 
    
    /** Converts a given lat/long coordinate pair using this ellipsoid datum into a longitude/latitude
      * pair using the given datum. Note: Currently, the only conversion supported
      * are between WGS84 (GRS80), Airy 1830 (used by Ordnance Survey National Grid) Clarke 1880
      * (used by IGN France) and Bessel 1841 (used by Swiss National Grid).
      * @param p Longitude/latitude point. Longitude varies within +-180 degrees, latitude within +-90 degrees.
      * @param newDatum New datum used in conversion.
      * @return Converted latitude/longitude coordinates.
      */
    public PVector projectDatum(PVector p, int newDatum)
    {
        // For full tables of parameters, see 
        // http://earth-info.nga.mil/GandG/coordsys/onlinedatum/CountryEuropeTable.html
        
        double lng = p.x;
        double lat = p.y;
        
        if (newDatum == id) // No conversion necessary.
        {
            System.err.println("Warning: Already using "+getName(id)+". No conversion necessary.");
            return new PVector((float)lng,(float)lat);
        }
        
        // Convert from WGS84 into AIRY_1830, BESSEL_1841 or CLARKE_1880
        if (id == WGS_84)
        {
            if (newDatum == AIRY_1830)
            {
                return molodensky(lng,lat, -573.604,-0.000011960023,-375,111,-431);
            }
            
            if (newDatum == BESSEL_1841)
            {
                return molodensky(lng,lat, 251, 0.000014192702,86,98,119);
            }
            
            if (newDatum == CLARKE_1880)
            {
                return molodensky(lng,lat, 112.145, 0.000054750714,168,60,-320);
            }
            
            if (newDatum == GRS_1980)
            {
                // GRS80 and WGS84 use same ellipsoid.
                return new PVector((float)lng,(float)lat);
            }
        }
       
        // Convert from Airy 1830 to WGS84/GRS80.
        if (id == AIRY_1830)
        {
            if ((newDatum == WGS_84) || (newDatum == GRS_1980))
            {
                return molodensky(lng, lat, 573.604,0.000011960023,375,-111,431);
            }
        }
        
        // Convert from Bessel 1841 to WGS84/GRS80.
        if (id == BESSEL_1841)
        {
            if ((newDatum == WGS_84) || (newDatum == GRS_1980))
            {
                return molodensky(lng, lat, -251, -0.000014192702,-86,-98,-119);
            }
        }
        
        // Convert from Clarke 1880 to WGS84/GRS80.
        if (id == CLARKE_1880)
        {
            if ((newDatum == WGS_84) || (newDatum == GRS_1980))
            {
                return molodensky(lng, lat, -112.145, -0.000054750714,-168,-60,320);
            }
        }
        
        // Can only get this far, if transformation not supported.    
        System.err.println("Warning: Conversion from "+getName(id)+" to "+ getName(newDatum)+" not supported.");
        return null; 

    }
    
    /** Reports the name associated with the given ellipsoid ID.
      * @param id Ellipsoid ID.
      * @return Name of given ellipsoid.
      */
    public static String getName(int id) 
    {
        switch (id)
        {
            case AIRY_1830:
                return new String("Airy 1830");
            case AUSTRALIAN_NATIONAL:
                return new String("Australian National");
            case BESSEL_1841:
                return new String("Bessel 1841");
            case BESSEL_1841_NAMIBIA:
                return new String("Bessel 1841 (Namibia)");
            case CLARKE_1866:
                return new String("Clarke 1866");
            case CLARKE_1880:
                return new String("Clarke 1880");
            case EVEREST:
                return new String("Everest");
            case FISCHER_1960:
                return new String("Fischer 1960");
            case FISCHER_1968:
                return new String("Fischer 1968");
            case GRS_1967:
                return new String("GRS 1967");
            case GRS_1980:
                return new String("GRS 1980");
            case HELMERT_1906:
                return new String("Helmert 1906");
            case HOUGH:
                return new String("Hough");
            case INTERNATIONAL:
                return new String("International");
            case KRASSOVSKY:
                return new String("Krassovsky");
            case MODIFIED_AIRY:
                return new String("Modified Airy");
            case MODIFIED_EVEREST:
                return new String("Modified Everest");
            case MODIFIED_FISCHER_1960:
                return new String("Modified Fischer 1960");
            case SOUTH_AMERICAN:
                return new String("South American");
            case WGS_60:
                return new String("WGS 60");
            case WGS_66:
                return new String("WGS 66");
            case WGS_72:
                return new String("WGS 72");
            case WGS_84:
                return new String("WGS 84");
            case SPHERE:
                return new String("Sphere");
            default:
                return new String("Undefined");
        }
    }
    
    // ----------------------- Private Methods ------------------------
    
    /** Stores the given ellipsoid parameters and calculates some convenience
      * constants such as b and n. 
      * @param eid Ellipsoid ID.
      * @param radius Equatorial radius.
      * @param ee2 Squared ellipsoid eccentricity.
      */ 
    private void store(int eid, double radius, double ee2)
    {
        this.id = eid;
        this.a  = radius;

        this.e2 = ee2;
        this.b  = Math.sqrt(a*a*(1-e2));
        this.n  = (a-b)/(a+b);
    }
    
    /** Performs a Molodensky transformation using the given parameters. Used for projecting between datums. See
      * <a http://earth-info.nga.mil/GandG/coordsys/onlinedatum/CountryEuropeTable.html" target="_new">
      * http://earth-info.nga.mil/GandG/coordsys/onlinedatum/CountryEuropeTable.html</a>.
      * @param lng Longitude coordinate to transform (decimal degrees).
      * @param lat Latitude coordinate to transform (decimal degrees).
      * @param dA Molodensky parameter (offset in major axis).
      * @param dF Molodensky parameter (offset in ellipsoid flattening).
      * @param dX Molodensky parameter (offset in x direction).
      * @param dY Molodensky parameter (offset in y direction).
      * @param dZ Molodensky parameter (offset in z direction).
      * @return Transformed lat/long coordinates (decimal degrees).
      */
    private PVector molodensky(double lng, double lat, double dA, double dF, double dX, double dY, double dZ)
    {     
        double myA   = a - dA;
        double myF   = (1/298.257223563) - dF;
        double myES  = 2*myF - myF*myF;  
        double myLat = lat*DEG_TO_RAD;       
        double myLng = lng*DEG_TO_RAD; 
    
        double sinLat = Math.sin(myLat);
        double sinLng = Math.sin(myLng);
        double cosLat = Math.cos(myLat);
        double cosLng = Math.cos(myLng);
            
        double rn = myA / Math.sqrt(1 - (myES*sinLat*sinLat));
        double rm = myA * (1 - myES) / (Math.pow(1 - myES * sinLat*sinLat,1.5));
            
        double d1 = ((-dX*sinLat*cosLng - dY*sinLat*sinLng) + dZ*cosLat);
        double d2 = dA*(rn*myES*sinLat*cosLat)/myA;
        double d3 = dF*(rm/(1-myF) + rn*(1-myF))*sinLat*cosLat;
            
        double dLat = (d1+d2+d3)/ rm;
        double dLng = (-dX*sinLng + dY*cosLng) / (rn*cosLat);
            
        return new PVector((float)((myLng+dLng)*RAD_TO_DEG),(float)((myLat+dLat)*RAD_TO_DEG));
    }
}