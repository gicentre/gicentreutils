package org.gicentre.tests;

import org.gicentre.utils.network.Edge;
import org.gicentre.utils.network.Node;
import org.gicentre.utils.network.ParticleViewer;

import processing.core.PApplet;

//  *****************************************************************************************
/** Tests particle viewer works with single particle.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.4, 5th February 2016. 
 */ 
//  *****************************************************************************************

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

public class ParticleSoloTest extends PApplet
{
	// ----------------------------- Object variables ------------------------------

	private ParticleViewer<Node, Edge> viewer;
	
	// ------------------------------ Starter method -------------------------------

	/** Starts the test as an application.
	 *  @param args Command line arguments (ignored)
	 */
	public static void main(String[] args)
	{
		PApplet.main(new String[] {"org.gicentre.tests.ParticleSoloTest"});
	}

	// ------------------------- Processing initialisation -------------------------

	/** Sets the size and of the sketch and its maximum pixel density.
      */
	public void settings()
	{
		size(800,600);
		pixelDensity(displayDensity());
	}
	
	/** Sets up the sketch ready to display.
	 */
	public void setup()
	{
		viewer = new ParticleViewer<Node,Edge>(this, width, height);
		viewer.addNode(new Node(10,0));
	}

	/** Draw the particle.
	 */
	public void draw()
	{
		background(255);
		viewer.draw();
	}
}
