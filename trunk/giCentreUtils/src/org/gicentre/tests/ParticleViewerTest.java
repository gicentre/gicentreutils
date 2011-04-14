package org.gicentre.tests;

import processing.core.PApplet;
import org.gicentre.utils.network.*;

//*****************************************************************************************
/** Tests particle viewer by creating a simple node-edge graph and displaying it. 
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.1, 30th November, 2010. 
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

@SuppressWarnings("serial")
public class ParticleViewerTest extends PApplet
{
	// ----------------------------- Object variables ------------------------------

	private ParticleViewer viewer;

	// ------------------------------ Starter method -------------------------------

	/** Starts the test as an application.
	 *  @param args Command line arguments (ignored)
	 */
	public static void main(String[] args)
	{
		PApplet.main(new String[] {"org.gicentre.tests.ParticleViewerTest"});
	}

	// ------------------------- Processing initialisation -------------------------

	/** Sets up the sketch ready to display.
	 */
	public void setup()
	{
		size(800,600);
		smooth();
		strokeWeight(1);		// For thickness of edge lines.
		
		viewer = new ParticleViewer(this, width, height);

		Node[] nodes = new Node[100];
		for (int i=0; i<nodes.length; i++)
		{
			nodes[i] = new Node((float)Math.random()*1000,(float)Math.random()*1000);
			viewer.addNode(nodes[i]);
		}
		
		// Add some random edges.
		for (int i=0; i<nodes.length; i++)
		{
			int index2 = (int)(Math.random()*nodes.length/10);
			viewer.addEdge(new Edge(nodes[i],nodes[index2]));
		}
	}

	/** Draws the particle viewer
	 */
	public void draw()
	{
		background(255);
		viewer.draw();
	}
	
	/** Responds to a mouse pressed event by selecting the node nearest to the mouse position.
	 */
	public void mousePressed()
	{
		viewer.selectNearestWithMouse();
	}
	
	/** Responds to a mouse released event by releasing any selected node.
	 */
	public void mouseReleased()
	{
		viewer.dropSelected();
	}
	
	/** Allows the zoomed view to be reset.
	 */
	public void keyPressed()
	{
		if ((key=='R') || (key=='r'))
		{
			viewer.resetView();
		}
	}
}
