package org.gicentre.tests;

import processing.core.PApplet;
import org.gicentre.utils.network.*;

//*****************************************************************************************
/** Tests particle viewer by creating a simple node-edge graph and displaying it. 
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

@SuppressWarnings("serial")
public class ParticleViewerTest extends PApplet
{
	// ----------------------------- Object variables ------------------------------

	private ParticleViewer<MyNode,Edge> viewer;
	private MyNode selectedNode;

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
		selectedNode = null;
		
		viewer = new ParticleViewer<MyNode,Edge>(this, width, height);

		MyNode[] nodes = new MyNode[100];
		for (int i=0; i<nodes.length; i++)
		{
			nodes[i] = new MyNode((float)Math.random()*1000,(float)Math.random()*1000);
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
		
		selectedNode = viewer.getSelectedNode();
		
		if (selectedNode != null)
		{
			selectedNode.setHighlight(true);	
		}
	}
	
	/** Responds to a mouse released event by releasing any selected node.
	 */
	public void mouseReleased()
	{
		viewer.dropSelected();
		
		if (selectedNode != null)
		{
			selectedNode.setHighlight(false);
			selectedNode = null;
		}
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

	// --------------------------- Nested classes ---------------------------
	
	private class MyNode extends Node
	{
		private boolean isHighlighted;

		public MyNode(float x, float y)
		{
			super(x,y);
			isHighlighted = false;

		}

		@Override
		public void draw(PApplet sketch, float x, float y)
		{
			if (isHighlighted)
			{
				fill(255,0,0);
				stroke(0,200);
				ellipse(x, y, 30, 20);
			}
			else
			{
				fill(43, 100, 107, 200);
				noStroke();
				ellipse(x, y, 15, 10);
			}
			
		}

		public void setHighlight(boolean isHighlighted)
		{
			this.isHighlighted = isHighlighted;
		}

	}
}
