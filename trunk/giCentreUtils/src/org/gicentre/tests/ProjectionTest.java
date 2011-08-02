package org.gicentre.tests;

import org.gicentre.utils.spatial.WebMercator;

import processing.core.PVector;

import junit.framework.TestCase;

//  ****************************************************************************************
/** Set of unit tests for projection conversion.
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

public class ProjectionTest extends TestCase
{    

	private PVector[] geoCoords;
	
	private PVector[] oubGeoCoords;		// Out of bounds geographic coordinates.

	// ---------------------------------- Methods ----------------------------------

	/** Called before every test case method.
	 */
	protected void setUp()
	{
		geoCoords = new PVector[] {new PVector(0,0),     new PVector(0,52),                    new PVector(180,80), 
				                   new PVector(-180,80), new PVector(-0.0377655f, 51.528625f), new PVector(-100.33333f, 24.381787f)};
		
		oubGeoCoords = new PVector[] { new PVector(0,-91), new PVector(0,91), new PVector(-180.001f,0), new PVector(180.00001f,0)};
	}

	/** Called after every test case method.
	 */
	protected void tearDown()
	{
		// Do nothing for the moment.
	}

	// --------------------------------- Test methods -------------------------------

	/** Checks that forward and inverse transformations of the Web Mercator conversion
	 *  complete a round trip (start and end coordinates are equal).
	 */
	public void testWebMercator()
	{
		WebMercator webMerc = new WebMercator();
		for (PVector geo : geoCoords)
		{
			PVector scr = webMerc.transformCoords(geo);
			PVector end = webMerc.invTransformCoords(scr);
			System.out.println("lng="+geo.x+" lat="+geo.y+ "\t x="+(int)scr.x+" y="+(int)scr.y);
			assertEquals(geo,end);
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
}