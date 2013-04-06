package org.gicentre.utils.spatial;

import processing.core.PVector;

//  *************************************************************************************
/** Class for representing Universal Transverse Mercator projections. Includes forward
 *  and inverse transforms from/to lat,long. Uses transformation equations from Snyder
 *  (1987) Atlas of Map Projections, and the Ordnance Survey (2002) <a href="http://www.ordnancesurvey.co.uk/oswebsite/gps/information/coordinatesystemsinfo/guidecontents/index.html" target="new">
 *  A Guide to Coordinate Systems of Great Britain</a>. If explicit projection origins
 *  are not given, an appropriate UTM zone should be set before transforming in either
 *  direction. This can be determined by passing a reasonably central longitude/latitude
 *  coordinate pair to the constructor.
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

public class UTM implements MapProjection
{
    // ---------------- Object and class variables ----------------
    
    private Ellipsoid ellipsoid;  // Reference ellipsoid.
    private int zoneNumber;       // UTM zone number (longitudinal).
    private char zoneLetter;      // UTM zone letter (latitudinal).
       
    private boolean doInterpolation;
    private int direction;        // Direction of forward transformation.
    
    private static final float RAD2DEG = 57.29577951f;
    private static final float DEG2RAD = 0.0174533f;
    
                                 /** Indicates a transformation from longitude/latitude */
    public static final int FROM_LAT_LONG = 1;
                                 /** Indicates a transformation to longitude/latitude */
    public static final int TO_LAT_LONG = 2;

    // ------------------------ Constructor -----------------------
    
    /** Initialises the UTM converter with the given ellipsoid and zone. The
     *  forward transformation will be from UTM to longitude/latitude. 
     *  @param ellipsoid to use in projection.
     *  @param zoneNumber Zone number of UTM projection.
     *  @param zoneLetter Zone letter of UTM projection.
     */
    public UTM(Ellipsoid ellipsoid, int zoneNumber, char zoneLetter)
    {
        this.ellipsoid = ellipsoid;
        doInterpolation = true;
        setZone(zoneNumber,zoneLetter); 
        direction = TO_LAT_LONG;  
    }
    
    /** Initialises the UTM converter with the given ellipsoid. The initial
      * longitude/latitude location sets the default UTM zone. This can be changed by 
      * calling the setZone() method. The forward transformation will be from
      * longitude/latitude to UTM.  
      * @param ellipsoid to use in projection.
      * @param lat Initial latitude coordinate.
      * @param lng Initial longitude coordinate.
      */
    public UTM(Ellipsoid ellipsoid, float lat, float lng)
    {
        this.ellipsoid = ellipsoid;
        doInterpolation = true;
        setZone(lat,lng);  
        direction = FROM_LAT_LONG; 
    }
    
    // ------------------------- Methods --------------------------
    
    /** Performs a forward transform on the given location.
      * @param p Point coordinates to transform.
      * @return Transformed footprint.
      */     
    public PVector transformCoords(PVector p)
    {
        if (direction == FROM_LAT_LONG)
        {
            return latLongToUTM(p);
        }
        return UTMToLatLong(p);
    }
    
    /** Performs an inverse UTM transform on the given location.
      * @param p Point coordinates to transform.
      * @return Transformed footprint.
      */     
    public PVector invTransformCoords(PVector p)
    {
        if (direction == FROM_LAT_LONG)
        {
            return UTMToLatLong(p);
        }
        return latLongToUTM(p);
    }
    
    /** Provides a general description of the transformation.
      * @return Description of the transformation.
      */
    public String getDescription()
    {
        return new String("UTM zone "+getZone()+" lat/long transformation."); 
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
       
    /** Converts the given (<code>lat</code>,<code>lng</code>) coordinates into a UTM location.
      * longitude/latitude should be in decimal degrees with positive North and positive East. 
      * The UTM zone to use should already have been set though the
      * constructor or a call to <code>setzone()</code>.
      * @param p Longitude/latitude point. Longitude varies within +-180 degrees, latitude within +-90 degrees.
      * @return UTM location.
      */ 
    public PVector latLongToUTM(PVector p)
    {
        double lngOrigin = (zoneNumber-1)*6 - 180 + 3;  //+3 puts origin in middle of zone
        double latOrigin = 0;                           // Default is no latitude offset.
        double xOffset = 500000;        // Default UTM zone offset.
        double yOffset = 0;
        
        if (p.y < 0)
        {
            yOffset = 10000000;         // 10,000,000 metre offset for southern hemisphere
        }
         
        return latLongToUTM(p, lngOrigin, latOrigin, xOffset, yOffset, 0.9996);
    }
     
    /** Converts the given (<code>lng</code>,<code>lat</code>) coordinates into a UTM location using
      * the given scale factor and origins.
      * longitude/latitude should be in decimal degrees with positive North and positive East. 
      * This version of the conversion does not require a UTM zone to be set
      * as the origin is set explicitly.
      * @param p Longitude/latitude point. Longitude varies within +-180 degrees, latitude within +-90 degrees.
      * @param lngOrigin Origin longitude (+-180.0).
      * @param latOrigin Origin latitude (+-90.0).
      * @param xOffset UTM coordinate X origin.
      * @param yOffset UTM coordinate Y origin.
      * @param scaleFactor Scale factor on central meridian used in transformation.
      * @return UTM location.
      */ 
    public PVector latLongToUTM(PVector p, 
                                double lngOrigin, double latOrigin,
                                double xOffset, double yOffset,
                                double scaleFactor)
    {
        double a  = ellipsoid.getEquatorialRadius(),
               b  = ellipsoid.getPolarRadius();
        double e2 = ellipsoid.getSquaredEccentricity();
        double n  = ellipsoid.getN(),
               n2 = n*n,
               n3 = n2*n;
        double lngTemp = ((p.x-lngOrigin)+180)-(int)(((p.x-lngOrigin)+180)/360)*360-180;
       
        double phi     = p.y*DEG2RAD;
        double phi0    = latOrigin*DEG2RAD;
        double lambda  = lngTemp*DEG2RAD;
        double lambda2 = lambda*lambda;
        double lambda3 = lambda2*lambda;
        double sinPhi  = Math.sin(phi);
        double sin2Phi = sinPhi*sinPhi;
        double cosPhi  = Math.cos(phi);
        double cos3Phi = cosPhi*cosPhi*cosPhi;
        double cos5Phi = cos3Phi*cosPhi*cosPhi;
        double tanPhi  = Math.tan(phi);
        double tan2Phi = tanPhi*tanPhi;
        double tan4Phi = tan2Phi*tan2Phi;
        
        double v   = a*scaleFactor/Math.sqrt(1-e2*sin2Phi);
        double rho = a*scaleFactor*(1-e2)/Math.pow(1-e2*sin2Phi,1.5);
        double neta2 = v/rho -1;
        
        double M = b*scaleFactor*((1+n +1.25*n2 + 1.25*n3)*(phi-phi0) -
                                  (3*n + 3*n2   + 2.625*n3)*Math.sin(phi-phi0)*Math.cos(phi+phi0) +
                                  (1.875*n2 + 1.875*n3)*Math.sin(2*(phi-phi0))*Math.cos(2*(phi+phi0)) -
                                  (1.45833333333333333*n3*Math.sin(3*(phi-phi0))*Math.cos(3*(phi+phi0))));
        
        double I    = M + yOffset;
        double II   = v/2*sinPhi*cosPhi;
        double III  = v/24*sinPhi*cos3Phi*(5-tan2Phi + 9*neta2); 
        double IIIA = v/720*sinPhi*cos5Phi*(61 - 58*tan2Phi+tan4Phi);
        double IV   = v*cosPhi;
        double V    = v/6*cos3Phi*(v/rho - tan2Phi);
        double VI   = v/120*cos5Phi*(5 - 18*tan2Phi + tan4Phi + 14*neta2 - 58*tan2Phi*neta2);

        return new PVector((float)(xOffset + IV*lambda + V*lambda3 + VI*lambda2*lambda3),
                           (float)(I + II*lambda2 + III*lambda2*lambda2 + IIIA*lambda3*lambda3));
    } 
    
    /** Converts the given UTM coordinates into a longitude/latitude location. This 
      * requires a UTM zone to have been set (via constructor) in order to
      * complete the conversion. 
      * longitude/latitude will be in decimal degrees with positive North and positive East. 
      * @param p Location of point as an easting and northing in the UTM projection (metres from local origin).
      * @return location in geographical coordinates (longitude,latitude).
      */ 
    public PVector UTMToLatLong(PVector p)
    {
        double lngOrigin = (zoneNumber-1)*6 - 180 + 3;  //+3 puts origin in middle of zone
        double latOrigin = 0;                           // Default is no latitude offset.
        double xOffset = 500000;        // Default UTM zone offset.
        double yOffset = 0;
        
        if((zoneLetter - 'N') < 0)
        {
            yOffset = 10000000;        // Remove 10,000,000 metre offset used for southern hemisphere
        }
        
        return UTMToLatLong(p, lngOrigin,latOrigin, xOffset,yOffset,0.9996);
    }
    
    /** Converts the given UTM coordinates into a (<code>lng</code>,<code>lat</code>) referenced location.
      * Longitude/latitude will be in decimal degrees with positive North and positive East. 
      * This transformation is independent of the UTM zone defined by the
      * constructor since all parameters are given explicitly. 
      * @param p Location of point as an easting and northing in the UTM projection (metres from local origin).
      * @param lngOrigin Origin longitude (+-180)
      * @param latOrigin Origin latitude (+-90)
      * @param xOffset UTM coordinate X origin.
      * @param yOffset UTM coordinate Y origin.
      * @param scaleFactor Scale factor on central meridian used in transformation.
      * @return location in geographical coordinates (longitude,latitude).
      */ 
    public PVector UTMToLatLong(PVector p,  
                                double lngOrigin, double latOrigin,
                                double xOffset, double yOffset,
                                double scaleFactor)
    {
        double a  = ellipsoid.getEquatorialRadius(),
               b  = ellipsoid.getPolarRadius();
        double e2 = ellipsoid.getSquaredEccentricity();
        double n  = ellipsoid.getN(),
               n2 = n*n,
               n3 = n2*n;
        
        double phi0 = latOrigin*DEG2RAD;       
        double phi  = (p.y - yOffset)/(a*scaleFactor) + phi0;
        double M;
        
        M = b*scaleFactor*((1+n +1.25*n2 + 1.25*n3)*(phi-phi0) -
                           (3*n + 3*n2   + 2.625*n3)*Math.sin(phi-phi0)*Math.cos(phi+phi0) +
                           (1.875*n2 + 1.875*n3)*Math.sin(2*(phi-phi0))*Math.cos(2*(phi+phi0)) -
                           (1.45833333333333333*n3*Math.sin(3*(phi-phi0))*Math.cos(3*(phi+phi0))));
        
        //System.out.println((northing-yOffset-M));
        
        while (Math.abs(p.y-yOffset-M) >= 0.01)
        {
            phi = (p.y-yOffset-M)/(a*scaleFactor) + phi;
            
            M = b*scaleFactor*((1+n +1.25*n2 + 1.25*n3)*(phi-phi0) -
                               (3*n + 3*n2   + 2.625*n3)*Math.sin(phi-phi0)*Math.cos(phi+phi0) +
                               (1.875*n2 + 1.875*n3)*Math.sin(2*(phi-phi0))*Math.cos(2*(phi+phi0)) -
                               (1.45833333333333333*n3*Math.sin(3*(phi-phi0))*Math.cos(3*(phi+phi0))));
        }
        
        double sinPhi  = Math.sin(phi);
        double secPhi  = 1 / Math.cos(phi);
        double sin2Phi = sinPhi*sinPhi;
        double tanPhi  = Math.tan(phi);
        double tan2Phi = tanPhi*tanPhi;
        double tan4Phi = tan2Phi*tan2Phi;
        double tan6Phi = tan4Phi*tan2Phi;
     
        double v   = a*scaleFactor/Math.sqrt(1-e2*sin2Phi);
        double v2  = v*v;
        double v3  = v2*v;
        double v5  = v3*v2;
        double v7  = v5*v2; 
        double rho = a*scaleFactor*(1-e2)/Math.pow(1-e2*sin2Phi,1.5);
        double neta2 = v/rho -1;
        
        double VII  = tanPhi/(2*rho*v);
        double VIII = tanPhi/(24*rho*v3)*(5 + 3*tan2Phi + neta2 - 9*tan2Phi*neta2);
        double IX   = tanPhi/(720*rho*v5)*(61 + 90*tan2Phi + 45*tan4Phi);
        double X    = secPhi/v;
        double XI   = secPhi/(6*v3)*(v/rho + 2*tan2Phi);
        double XII  = secPhi/(120*v5)*(5 + 28*tan2Phi + 24*tan4Phi);
        double XIIA = secPhi/(5040*v7)*(61 + 662*tan2Phi + 1320*tan4Phi + 720*tan6Phi); 
           
        double E = p.x-xOffset;  
        double E2 = E*E;
        double E3 = E2*E;
        double E4 = E3*E;
        double E5 = E4*E;
        double E6 = E5*E;
        double E7 = E6*E;
        
        return new PVector((float)(lngOrigin + RAD2DEG*(X*E - XI*E3 + XII*E5 - XIIA*E7)),
                           (float)(RAD2DEG*(phi -VII*E2 + VIII*E4 - IX*E6)));
    }
    
    /** Sets the UTM zone determined by the given (<code>lat</code>,<code>lng</code>) location.
      * Numbers vary from 1 to 60, where 1 is around 175W, and 60 is 175E. Letters vary 
      * from N,M,O...X going north from equator and M,L,K...C going south from
      * equator.
      * @param lat Latitude (+-90)
      * @param lng Longitude (+-180)
      */ 
    public void setZone(float lat, float lng)
    {
        // Make sure the longitude is between -180.00 .. 179.9
        double longTemp = (lng+180)-(int)((lng+180)/360)*360-180;
        zoneNumber = (int)((longTemp + 180)/6) + 1;
       
        if ((lat >= 56) && (lat < 64) && (longTemp >= 3.0) && (longTemp < 12.0))
        {
            zoneNumber = 32;
        }
    
        // Special zones for Svalbard
        if ((lat >= 72) && (lat < 84)) 
        {
            if ((longTemp >= 0) && (longTemp < 9.0))
            {
                zoneNumber = 31;
            }
            else if ((longTemp >= 9.0) && (longTemp < 21.0))
            {
                zoneNumber = 33;
            }
            else if ((longTemp >= 21.0) && (longTemp < 33.0))
            {
                zoneNumber = 35;
            }
            else if ((longTemp >= 33.0) && (longTemp < 42.0))
            {
                zoneNumber = 37;
            }
        }
        
        if((84 >= lat) && (lat >= 72)) 
            zoneLetter = 'X';
        else if((72 > lat) && (lat >= 64)) 
            zoneLetter = 'W';
        else if((64 > lat) && (lat >= 56))
            zoneLetter = 'V';
        else if((56 > lat) && (lat >= 48))
            zoneLetter = 'U';
        else if((48 > lat) && (lat >= 40))
            zoneLetter = 'T';
        else if((40 > lat) && (lat >= 32))
            zoneLetter = 'S';
        else if((32 > lat) && (lat >= 24))
            zoneLetter = 'R';
        else if((24 > lat) && (lat >= 16))
            zoneLetter = 'Q';
        else if((16 > lat) && (lat >= 8))
            zoneLetter = 'P';
        else if(( 8 > lat) && (lat >= 0))
            zoneLetter = 'N';
        else if(( 0 > lat) && (lat >= -8)) 
            zoneLetter = 'M';
        else if((-8 > lat) && (lat >= -16)) 
            zoneLetter = 'L';
        else if((-16 >lat) && (lat >= -24)) 
            zoneLetter = 'K';
        else if((-24 >lat) && (lat >= -32)) 
            zoneLetter = 'J';
        else if((-32 >lat) && (lat >= -40)) 
            zoneLetter = 'H';
        else if((-40 >lat) && (lat >= -48))
            zoneLetter = 'G';
        else if((-48 >lat) && (lat >= -56))
            zoneLetter = 'F';
        else if((-56 >lat) && (lat >= -64)) 
            zoneLetter = 'E';
        else if((-64 >lat) && (lat >= -72)) 
            zoneLetter = 'D';
        else if((-72 >lat) && (lat >= -80)) 
            zoneLetter = 'C';
        else zoneLetter = 'Z';      // Error flag indicating latitude is outside the UTM limits.
    }
    
    /** Sets the zone number and letter for subsequent transformations.
      * @param zoneNumber Zone number to use.
      * @param zoneLetter Zone letter to use.
      */ 
    public void setZone(int zoneNumber, char zoneLetter)
    {
        this.zoneNumber = zoneNumber;
        this.zoneLetter = zoneLetter;
    }
    
    /** Reports the UTM zone currently used by the projection. Numbers vary
      * from 1 to 60, where 1 is around 175W, and 60 is 175E. 
      * @return UTM zone number (longitudinal zone).
      */ 
    public int getZoneNumber()
    {
        return zoneNumber;
    }
    
    /** Reports the UTM zone letter currently used by the projection. Letters vary 
      * from N,M,O...X going north from equator and M,L,K...C going south from
      * equator. Returns Z if outside 84N or 80S. 
      * @return UTM zone letter (latitudinal zone).
      */ 
    public char getZoneLetter()
    {
        return zoneLetter;
    }

    /** Reports the full UTM zone of currently used by the projection.
      * @return UTM zone (e.g 19F for southern tip of South America).
      */ 
    public String getZone()
    {
        return new String(Integer.toString(zoneNumber))+Character.toString(zoneLetter);
    }
    
    /** Reports the ellipsoid used in the transformation.
      * @return Ellipsoid used in the UTM transformation.
      */
    public Ellipsoid getEllipsoid()
    {
        return ellipsoid; 
    }
}