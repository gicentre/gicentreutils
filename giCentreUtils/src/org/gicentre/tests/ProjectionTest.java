package org.gicentre.tests;

import java.util.ArrayList;
import org.gicentre.utils.spatial.*;			// For Map projections.

import processing.core.PVector;
import junit.framework.TestCase;

//  ****************************************************************************************
/** Set of unit tests for projection conversion.
 *  @author Jo Wood, giCentre, City University London.
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

public class ProjectionTest extends TestCase
{    

	private PVector[] oubGeoCoords;		// Out of bounds geographic coordinates.

	// ---------------------------------- Methods ----------------------------------

	/** Called before every test case method.
	 */
	protected void setUp()
	{
		oubGeoCoords = new PVector[] { new PVector(0,-91), new PVector(0,91), new PVector(-180.001f,0), new PVector(180.00001f,0)};
	}

	/** Called after every test case method.
	 */
	protected void tearDown()
	{
		// Do nothing for the moment.
	}

	// --------------------------------- Test methods -------------------------------

	/** Checks that forward and inverse transformations for the various Albers projection
	 *  round trips (start and end coordinates are equal).
	 */
	public void testAlbers()
	{
		PVector[] geoCoords = new PVector[] {new PVector(-165,65),		// Western Alaska.
											 new PVector(-180,52),		// Bering Straits.
											 new PVector(-127,59),		// Northern BC.
											 new PVector(-157,20),		// Hawaii
											 new PVector(-98,26),		// Southern Texas
											 new PVector(-68,45),		// NE US
											 new PVector(-99,39)};		// Kansas

		ArrayList<MapProjection> projs = new ArrayList<MapProjection>();
		projs.add(new AlbersBC());
		projs.add(new AlbersUS());
		projs.add(new AlbersUSCont());

		for (MapProjection proj : projs)
		{
			System.out.println("\n"+proj.getDescription());
			for (PVector geo : geoCoords)
			{
				roundTrip(proj, geo);
			}
		}
	}
	
	/** Checks that forward and inverse transformations for the various Lambert conformal conic projection
	 *  round trips (start and end coordinates are equal).
	 */
	public void testLambertConformalConic()
	{
		PVector[] geoCoords = new PVector[] {new PVector(-75,35),		// See Snyder (1987) p.296
											 new PVector(-165,65),		// Western Alaska.
											 new PVector(-180,52),		// Bering Straits.
											 new PVector(-127,59),		// Northern BC.
											 new PVector(-157,20),		// Hawaii
											 new PVector(-98,26),		// Southern Texas
											 new PVector(-68,45),		// NE US
											 new PVector(-99,39)};		// Kansas
		
		ArrayList<MapProjection> projs = new ArrayList<MapProjection>();
		projs.add(new LambertConformalConic(new Ellipsoid(Ellipsoid.CLARKE_1866),33,45,-96,23,0,0));
		projs.add(new LambertConformalConic(25,-133.459,12.19));
		
		for (MapProjection proj : projs)
		{
			System.out.println("\n"+proj.getDescription());
			for (PVector geo : geoCoords)
			{
				roundTrip(proj, geo);
			}
		}
	}

	/** Checks that forward and inverse transformations for European projections
	 *  complete a round trip (start and end coordinates are equal).
	 */
	public void testEuropean()
	{
		PVector[] geoCoords = new PVector[] {new PVector(2.5f,51), 			// Northern France
											 new PVector(-4.8f,48.3f), 		// NW France
											 new PVector(8,49), 			// Eastern France
											 new PVector(2.9f,42),			// Southern France
											 new PVector(8.5f,47.4f),		// Zurich
											 new PVector(6,46.1f),			// Geneva
											 new PVector(10.5f,46.9f)};		// Eastern Switzerland

		ArrayList<MapProjection> projs = new ArrayList<MapProjection>();
		projs.add(new FrenchNTF());
		projs.add(new OSGB());
		projs.add(new Swiss());
		projs.add(new LambertConformalConic(new Ellipsoid(Ellipsoid.INTERNATIONAL),43,62,10,30,0,0));

		for (MapProjection proj : projs)
		{
			System.out.println("\n"+proj.getDescription());
			for (PVector geo : geoCoords)
			{
				roundTrip(proj, geo);
			}
		}
	}
	
	/** Checks that forward and inverse transformations for OSGB projections
	 *  complete a round trip (start and end coordinates are equal).
	 */
	public void testOSGB()
	{
		PVector[] geoCoords = new PVector[] {new PVector(2.5f,51), 			// Northern France
											 new PVector(-4.8f,48.3f), 		// NW France
											 new PVector(8,49), 			// Eastern France
											 new PVector(2.9f,42),			// Southern France
											 new PVector(-5.3f,50),			// Cornwall
											 new PVector(1.7f,52.7f),		// Norfolk
											 new PVector(-4.8f,53.4f),		// Anglesey
											 new PVector(-7.5f,57.6f),		// Western Isles
											 new PVector(-0.9f,60.8f),		// Shetland
											 new PVector(8.5f,47.4f),		// Zurich
											 new PVector(6,46.1f),			// Geneva
											 new PVector(10.5f,46.9f)};		// Eastern Switzerland

		ArrayList<MapProjection> projs = new ArrayList<MapProjection>();
		projs.add(new OSGB());
	
		for (MapProjection proj : projs)
		{
			System.out.println("\n"+proj.getDescription());
			for (PVector geo : geoCoords)
			{
				roundTrip(proj, geo);
			}
		}
	}

	/** Checks that forward and inverse transformations for the Web mercator projection
	 *  complete a round trip (start and end coordinates are equal).
	 */
	public void testWebMercator()
	{
		MapProjection proj = new WebMercator();
		System.out.println("\n"+proj.getDescription());

		PVector[] geoCoords = new PVector[] {new PVector(0,0),     new PVector(0,52),                    new PVector(170,80), 
											 new PVector(-170,80), new PVector(-170,-80),				 new PVector(170,-80),
											 new PVector(-180,80), new PVector(-0.0377655f, 51.528625f), new PVector(-100.33333f, 24.381787f)};
		for (PVector geo : geoCoords)
		{
			roundTrip(proj, geo);
		}
	}

	/** Checks that out of bounds coordinates are caught.
	 */
	public void testOutOfBounds()
	{
		WebMercator webMerc = new WebMercator();
		for (PVector geo : oubGeoCoords)
		{
			assertNull(webMerc.transformCoords(geo));
		}
	}

	/** Performs a round-trip test to check that an inverse transformation applied to 
	 *  its forward equivalent takes the location back to the start.
	 *  @param proj Projection to test.
	 *  @param geo Lat/long coordinate to test.
	 */
	private void roundTrip(MapProjection proj, PVector geo)
	{
		PVector scr = proj.transformCoords(geo);
		PVector end = proj.invTransformCoords(scr);
		float diffX = Math.abs(geo.x-end.x);
		float diffY = Math.abs(geo.y-end.y);

		System.out.println("    lng="+geo.x+" lat="+geo.y+ "\t x="+(int)scr.x+" y="+(int)scr.y+"\t [inv: lng="+end.x+" lat="+end.y+"]");

		assertTrue(diffX < 0.001);
		assertTrue(diffY < 0.001);
	}
}