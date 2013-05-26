package org.gicentre.utils.spatial;

import processing.core.PVector;

//  ****************************************************************************************
/** Class for representing Lambert conformal conic projectiAlons. Includes forward and inverse
 *  transforms from/to lat,long. Uses transformation equations from Snyder (1987) <a href=
 *  "http://pubs.er.usgs.gov/publication/pp1395" target="_blank"> Map Projections - A 
 *  Working Manual</a>
 *  @version 3.3.1, 26th May, 2013.  
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

public class LambertConformalConic implements MapProjection
{
	// ---------------- Object and class variables ----------------

	private Ellipsoid ellipsoid;  // Reference ellipsoid.

	private boolean doInterpolation;
	private int direction;        // Direction of forward transformation.

	private static final double RAD2DEG   = 180.0/Math.PI;
	private static final double DEG2RAD   = Math.PI/180.0;
	private static final double PI_OVER_2 = Math.PI*0.5;
	private static final double PI_OVER_4 = Math.PI*0.25;

	/** Indicates a transformation from latitude/longitude */
	public static final int FROM_LAT_LONG = 1;

	/** Indicates a transformation to latitude/longitude */
	public static final int TO_LAT_LONG = 2;


	// Projection parameters.
	private double phi1;            	// First standard parallel in radians.           
	private double phi2;            	// Second standard parallel in radians.
	private double lngCentre;       	// Centre longitude in radians.
	private double falseEast;       	// x offset in metres
	private double falseNorth;      	// y offset in meters

	private double e,eSq;				// Ellipsoid eccentricity and its square.
	private double cosPhi1;				// Cosine of first standard parallel.
	private double cosPhi2;				// Cosine of second standard parallel.
	private double a;					// Equatorial radius from ellipsoid.
	private double n,F,rho0;			// Constants dependent on standard parallels of projection.
	private boolean isSingleParallel;	// Records whether or not we have a single standard parallel.

	// ------------------------ Constructor -----------------------

	/** Initialises the projection converter assuming spherical figure of the earth and single standard parallel 
	 *  and centred at the given location. The forward transformation will be from lat/long to Lambert conformal conic.
	 *  @param lat1 The single standard parallel in degrees (north of equator positive).
	 *  @param lon0 Centre longitude in degrees (east of Greenwich positive).
	 *  @param lat0 Centre latitude in degrees (north of equator positive).
	 */
    public LambertConformalConic(double lat1, double lon0, double lat0)
    {
        this(new Ellipsoid(Ellipsoid.SPHERE),lat1,lat1,lon0,lat0,0,0);
    }
	
	/** Initialises the projection converter with the given ellipsoid. The 
	 * forward transformation will be from lat/long to Lambert conformal conic.
	 * @param ellipsoid to use in projection.
	 * @param lat1 First standard parallel in degrees (north of equator positive).
	 * @param lat2 Second standard parallel in degrees (north of equator positive).
	 * @param lon0 Centre longitude in degrees (east of Greenwich positive).
	 * @param lat0 Centre latitude in degrees (north of equator positive).
	 * @param falseEast False easting (offset in metres).
	 * @param falseNorth False northing (offset in metres).
	 */
	public LambertConformalConic(Ellipsoid ellipsoid, double lat1, double lat2, double lon0, double lat0, double falseEast, double falseNorth)
	{
		this.ellipsoid  = ellipsoid;
		doInterpolation = true;
		direction       = FROM_LAT_LONG; 
		this.phi1       = lat1*DEG2RAD;
		this.phi2       = lat2*DEG2RAD;

		if (Math.abs(phi1 - phi2) < 1.0e-10)
		{
			isSingleParallel = true;
		}
		else
		{
			isSingleParallel = false;
		}

		this.lngCentre  = lon0*DEG2RAD;
		this.falseEast  = falseEast;
		this.falseNorth = falseNorth;

		eSq = ellipsoid.getSquaredEccentricity();
		e = Math.sqrt(eSq);
		a = ellipsoid.getEquatorialRadius();

		cosPhi1 = Math.cos(phi1);
		cosPhi2 = Math.cos(phi2);
		double phi0 = lat0*DEG2RAD;
		double sinPhi0 = Math.sin(phi0);
		double sinPhi1 = Math.sin(phi1);
		double sinPhi2 = Math.sin(phi2);

		double m1 = cosPhi1/Math.sqrt(1-eSq*sinPhi1*sinPhi1);
		double m2 = cosPhi2/Math.sqrt(1-eSq*sinPhi2*sinPhi2);
		double t0 = Math.tan(PI_OVER_4 - phi0/2)/Math.pow((1-e*sinPhi0)/(1+e*sinPhi0),e/2);
		double t1 = Math.tan(Math.PI/4 - phi1/2)/Math.pow((1-e*sinPhi1)/(1+e*sinPhi1),e/2);
		double t2 = Math.tan(Math.PI/4 - phi2/2)/Math.pow((1-e*sinPhi2)/(1+e*sinPhi2),e/2);

		if (isSingleParallel)
		{
			n = sinPhi1;
		}
		else
		{
			n = (Math.log(m1)-Math.log(m2))/(Math.log(t1)-Math.log(t2));
		}
		F = m1/(n*Math.pow(t1,n));
		rho0 = a*F*Math.pow(t0, n);
	}


	// ------------------------- Methods --------------------------

	/** Performs a forward transformation (latitude/longitude to Lambert conformal conic) on the
	 * given location.
	 * @param p Location of point to transform.
	 * @return Transformed location coordinates.
	 */     
	public PVector transformCoords(PVector p)
	{
		if (direction == FROM_LAT_LONG)
		{
			return latLongToLambert(p);
		}
		return LambertToLatLong(p);
	}

	/** Performs an inverse transformation (Lambert conformal conic to latitude/longitude) on the 
	 *  given location.
	 *  @param p Location of point to transform.
	 *  @return Transformed location coordinates.
	 */     
	public PVector invTransformCoords(PVector p)
	{
		if (direction == FROM_LAT_LONG)
		{
			return LambertToLatLong(p);
		}
		return latLongToLambert(p);
	}

	/** Provides a general description of the transformation.
	 *  @return Description of the transformation.
	 */
	public String getDescription()
	{
		if (direction== FROM_LAT_LONG)
		{
			return "Lat/long to Lambert conformal conic transformation.";
		}
		return "Lambert conformal conic to lat/long transformation."; 
	}

	/** Indicates whether the transformation should use nearest neighbour (false)
	 *  or some interpolator (true).
	 *  @return True if transformation should perform some local interpolation.
	 */
	public boolean doInterpolation()
	{
		return doInterpolation; 
	}

	/** Sets whether the transformation should use nearest neighbour (false)
	 *  or some interpolator (true).
	 *  @param doInterpolation True if transformation should perform some local interpolation.
	 */
	public void setInterpolation(boolean doInterpolation)
	{
		this.doInterpolation = doInterpolation; 
	}

	/** Converts the given (<code>lng</code>, <code>lat</code>) coordinates into a location referenced
	 *  using the Lambert conformal conic projection.
	 *  Latitude/longitude coordinates should be in decimal degrees with positive North and positive East. 
	 *  @param p Longitude/latitude point. Longitude varies within +-180 degrees, latitude within +-90 degrees.
	 *  @return Location (units in metres) using Lambert conformal conic coordinate system.
	 */ 
	public PVector latLongToLambert(PVector p)
	{
		double phi     = p.y*DEG2RAD;
		double lambda  = p.x*DEG2RAD;
		double sinPhi  = Math.sin(phi);

		double t  = Math.tan(Math.PI/4 - phi/2)/Math.pow((1-e*sinPhi)/(1+e*sinPhi),e/2);
		double rho = a*F*Math.pow(t, n);
		double theta = n*(lambda-lngCentre);

		return new PVector((float)(rho*Math.sin(theta) + falseEast),
				           (float)(rho0 - rho * Math.cos(theta) + falseNorth));
	}

	/** Converts the given coordinates that use the Lambert conformal conic projection
	 * into a (longitude,latitude) location. 
	 * Longitude and latitude will be in decimal degrees with positive North and positive East. 
	 * @param p Location of point as an easting and northing in the Lambert projection (metres from local origin).
	 * @return location in geographical coordinates (longitude/latitude decimal degrees).
	 */ 
	public PVector LambertToLatLong(PVector p)
	{
		double phi,lambda;
		double theta,rho,t;
		double easting = p.x-falseEast;
		double northing = p.y-falseNorth;

		if (n < 0)
		{
			theta = Math.atan(-easting/(northing-rho0));
		}
		else
		{
			theta = Math.atan(easting/(rho0-northing));
		}

		lambda = theta/n + lngCentre;
		rho = Math.sqrt(easting*easting+(rho0-northing)*(rho0-northing));
		if (n < 0)
		{
			rho *= -1;
		}

		t = Math.pow(rho/(a*F), 1/n);

		// Converging iteration:
		phi=0;
		double phiPrev = PI_OVER_2 - 2*Math.atan(t);
		double delta = Float.MAX_VALUE;

		while (delta > 0.001)
		{
			double sinPhi = Math.sin(phiPrev);
			phi = PI_OVER_2 - 2*Math.atan(t*Math.pow((1-e*sinPhi)/(1+e*sinPhi),e/2));
			delta = Math.abs(phi-phiPrev);
			phiPrev = phi;
		}

		return new PVector((float)(lambda*RAD2DEG),(float)(phi*RAD2DEG));
	}

	/** Reports the ellipsoid used in the transformation.
	 *  @return Ellipsoid used in the UTM transformation.
	 */
	public Ellipsoid getEllipsoid()
	{
		return ellipsoid; 
	}
}