package org.gicentre.utils.spatial;

import processing.core.PVector;

//  ********************************************************************************************
/** Class for representing Oblique Mercator projections. Includes forward and inverse transforms
 *  from and to longitude/latitude. Uses transformation equations from Snyder (1987) Atlas of Map 
 *  Projections summarised at this
 *  <a href="http://www.remotesensing.org/geotiff/proj_list/hotine_oblique_mercator.html target="new">
 *  Hotine Oblique Mercator</a> page. 
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

public class ObliqueMercator
{
    // ---------------- Object and class variables ----------------
    
    private Ellipsoid ellipsoid;  // Reference ellipsoid.
  
    private boolean doInterpolation;
    private int direction;        // Direction of forward transformation.
    
    private static final double RAD2DEG = 180.0/Math.PI;
    private static final double DEG2RAD = Math.PI/180.0;
    private static final double PI_OVER_2 = Math.PI/2.0;
    private static final double PI_OVER_4 = Math.PI/4.0;
    
                                 /** Indicates a transformation from longitude/latitude */
    public static final int FROM_LAT_LONG = 1;
    
                                 /** Indicates a transformation to longitude/latitude */
    public static final int TO_LAT_LONG = 2;
    
    // Projection parameters.
    double fc,      // Latitude of centre of the projection.
           lc,      // Longitude of centre of the projection.
           ac,      // Azimuth (true) of the centre line passing through the centre of the projection.
           gc,      // Rectified bearing of the centre line.
           singc,cosgc,
           kc,      // Scale factor at the centre of the projection.
           FE,      // False Easting at the natural origin.
           FN,      // False Northing at the natural origin.
           uc,vc,
           e,e2,    // Ellipsoid eccentricity.
           A,B,H,   // Projection intermediate constants.
           l0,g0,sing0,cosg0;
          
    // ------------------------ Constructor -----------------------

    /** Initialises the Oblique Mercator converter with the given ellipsoid and
      * projection parameters. The forward transformation will be from Lat/long 
      * to Oblique Merctor.  
      * @param ellipsoid to use in projection.
      */
    public ObliqueMercator(Ellipsoid ellipsoid)
    {
        this.ellipsoid = ellipsoid;
        doInterpolation = true; 
        direction = FROM_LAT_LONG; 

        // Swiss Ch-03 parameters.
        this.ellipsoid = new Ellipsoid(Ellipsoid.BESSEL_1841);
        lc = 7.4395833333333333*DEG2RAD;  // 7 degrees, 26 minutes, 22.5 seconds.
        fc = 46.952405555555556*DEG2RAD;  // 46 degrees, 57 minutes, 8.66 seconds. 
        ac = 90*DEG2RAD;
        gc = 90*DEG2RAD;                          // Assume rectified bearing same as centre line bearing.
        kc = 1;
        FE = -7419820.5907;
        FN = 1200000.0000;
        //FE = 0;
        //FN = 0;  
        
        // Test parameters.
        /*
        ellipsoid = new Ellipsoid(Ellipsoid.EVEREST);
        this.ellipsoid = ellipsoid;
        lc = 2.007128640;
        fc = 0.069813170;
        ac = 0.930536611;
        gc = 0.927295218;
        kc = 0.99984;
        //Ec = 0;
        //Nc = 0;
        FE = 400000;
        FN = 100000;
        */
        
        // Perform initial constant calculations
        double cosfc = Math.cos(fc);
        double sinfc = Math.sin(fc);
        singc = Math.sin(gc);
        cosgc = Math.cos(gc);
        e2 = ellipsoid.getSquaredEccentricity();
        e = Math.sqrt(e2);
        double a  = ellipsoid.getEquatorialRadius();
        
        B  = Math.sqrt(1.0 + e2*cosfc*cosfc*cosfc*cosfc / (1-e2));
        A  = a*B*kc*Math.sqrt(1-e2) / (1-e2*sinfc*sinfc);
        double t0 = Math.tan(PI_OVER_4 - fc/2) / Math.pow((1-e*sinfc) / (1+e*sinfc),e/2);
        double D  = B*Math.sqrt(1-e2) / (cosfc*Math.sqrt(1-e2*sinfc*sinfc)); 
        double DSq = D*D;
        if (DSq <1)
        {
            DSq = 1;     // To avoid problems with computation of F below.
        }
        double F  = D + Math.sqrt(DSq -1);
        if (fc <0)      // For centre south of equator.
        {
            F *= -1;
        }
        H = F*Math.pow(t0,B); 
        double G = (F - 1/F)/2;
        sing0 = Math.sin(ac)/D;
        g0 = Math.asin(sing0);
        cosg0 = Math.cos(g0);
        
        if (ac == 90*DEG2RAD)
        {
            l0 = lc - (PI_OVER_2/B);
            //System.out.println("special case: "+l0); 
        } 
        else
        {
            l0 = lc -(Math.asin(G*Math.tan(g0)))/B;
            //System.out.println("Not special case: "+l0); 
        } 

        vc = 0;
        
        if (ac == 90*DEG2RAD)
        {
            uc = A *(lc - l0);
        }
        else  
        {
            uc = (A/B)*Math.atan(Math.sqrt(DSq-1) / Math.cos(ac));
            if (fc < 0)     // For centre south of equator.
                uc *=-1;
        } 
        
        //System.out.println("\tuc: "+uc);
    }
    
    // ------------------------- Methods --------------------------
    
    /** Performs a forward longitude/latitude to Oblique Mercator transform on the given location.
      * @param p point coordinates to transform.
      * @return Transformed footprint.
      */     
    public PVector transformCoords(PVector p)
    {
        if (direction == FROM_LAT_LONG)
        {
            return latLongToObliqueMercator(p);
        }
        return obliqueMercatorToLatLong(p);
    }
    
    /** Performs an inverse Oblique Mercator to lat/long transform on the given location.
      * @param p Point coordinates to transform
      * @return Transformed footprint.
      */     
    public PVector invTransformCoords(PVector p)
    {
        if (direction == FROM_LAT_LONG)
        {
            return obliqueMercatorToLatLong(p);
        }
        return latLongToObliqueMercator(p);
    }
    
    /** Provides a general description of the transformation.
      * @return Description of the transformation.
      */
    public String getDescription()
    {
        if (direction== FROM_LAT_LONG)
        {
            return new String("Lat/long to Oblique Mercator transformation.");
        }
        return new String("Oblique Mercator to lat/long transformation.");
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
       
    /** Converts the given (<code>lat</code>,<code>lng</code>) coordinates into an oblique Mercator location.
      * Longitude/latitude should be in decimal degrees with positive North and positive East. 
      * @param p Longitude/latitude point. Longitude varies within +-180 degrees, latitude within +-90 degrees.
      * @return Oblique Mercator location.
      */ 
    public PVector latLongToObliqueMercator(PVector p)
    {
        double lngRad = p.x*DEG2RAD;
        double latRad = p.y*DEG2RAD;
        
        double t = Math.tan(PI_OVER_4 - latRad/2) / Math.pow((1 - e*Math.sin(latRad)) / (1 + e*Math.sin(latRad)),e/2);
        double Q = H / Math.pow(t,B);
        double S = (Q - 1/Q) / 2;
        double T = (Q + 1/Q) / 2;
        double V = Math.sin(B*(lngRad - l0));
        double U = (-V* Math.cos(g0) + S*sing0) / T;
        double v = A*Math.log((1-U)/(1+U))/ (2*B);
        //double u = A*Math.atan((S*Math.cos(g0) + V*sing0) / Math.cos(B*(lngRad-l0)))/B;
        
        double u = A*Math.atan2((S*cosg0 + V*sing0), Math.cos(B*(lngRad-l0)))/B;
        
        //u = u-uc;

        double E = v*cosgc + u*singc + FE;  //Ec;
        double N = u*cosgc - v*singc + FN;  //Nc;
        
        //System.out.println("\tt:"+t+" Q:"+Q+" S:"+S+" T:"+T+" V:"+V);
        //System.out.println("\tv:"+v+" u:"+u);

        return new PVector((float)E,(float)N);  
    }
    
    /** Converts the given Oblique Mercator coordinates into a longitude/latitude location.
      * Longitude/latitude will be in decimal degrees with positive North and negative East. 
      * @param p Location of point as an easting and northing in the Oblique Mercator projection (metres from local origin).
      * @return Longitude/latitude location.
      */ 
    public PVector obliqueMercatorToLatLong(PVector p)
    {
        double easting = p.x;
        double northing = p.y;
        double vPrime = (easting-FE)*cosgc - (northing-FN)*singc;
        double uPrime = (northing-FN)*cosgc + (easting-FE)*singc;

        double QPrime = Math.pow(Math.E,-B*vPrime/A);
        double SPrime = (QPrime - 1/QPrime) / 2;
        double TPrime = (QPrime + 1/QPrime) / 2;
        double VPrime = Math.sin(B*uPrime/A);
        double UPrime = (VPrime*cosg0 + SPrime*sing0)/TPrime;
        double tPrime = Math.pow(H / Math.sqrt((1 + UPrime) / (1-UPrime)),1/B);
        double c      = PI_OVER_2 - 2*Math.atan(tPrime);
        double lat    = c + Math.sin(2*c)*(e2/2 + 5*e2*e2/24 + e2*e2*e2/12 + 13*e2*e2*e2*e2/360) +
                            Math.sin(4*c)*(7*e2*e2/48 + 29*e2*e2*e2/240 + 811*e2*e2*e2*e2/11520) +
                            Math.sin(6*c)*(7*e2*e2*e2/120 + 81*e2*e2*e2*e2/1120) + 
                            Math.sin(8*c)*(4279*e2*e2*e2*e2/161280);
        double lng    = l0 - Math.atan((SPrime*cosg0 - VPrime*sing0) / Math.cos(B*uPrime/A))/B;

        return new PVector((float)(lng*RAD2DEG),(float)(lat*RAD2DEG));  
    }
     
    /** Reports the ellipsoid used in the transformation.
      * @return Ellipsoid used in the Oblique Mercator transformation.
      */
    public Ellipsoid getEllipsoid()
    {
        return ellipsoid; 
    }
    
    /*
    public static void main(String args[])
    {
        ObliqueMercator om = new ObliqueMercator(new Ellipsoid(Ellipsoid.BESSEL_1841));
        Footprint fp = new Footprint(9.56f,46.924965f);
        //Footprint fp = new Footprint((float)(1.997871312*RAD2DEG),(float)(0.081258569*RAD2DEG));
        Footprint fp2 = om.transformCoords(fp);
        Footprint fp3 = om.invTransformCoords(fp2);
        System.out.println("Start: "+fp);
        System.out.println("om   : "+fp2);
        System.out.println("Back : "+fp3);
        
        System.out.println("Swiss inv: "+om.invTransformCoords(new Footprint(751000,244000)));
        
    }
    */
}