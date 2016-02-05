package org.gicentre.utils.network;

import processing.core.PApplet;

//  *****************************************************************************************
/** Represents an edge between two nodes in a network graph structure. This class has a 
 *  default drawing behaviour, but by inheriting it and overriding its draw() method, visual
 *  appearance of the edge can be customised.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.4, 5th February, 2016.
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

public class Edge 
{
	// ----------------------------- Object variables ------------------------------
    
	private Node n1, n2;		// The two nodes that are connected by this edge.
	private boolean isDirected;	// If true, edge is only from n1 to n2, otherwise it goes both ways.
		                         
    // ------------------------------- Constructors --------------------------------
	
	/** Creates an undirected edge between the given nodes.
	 *  @param n1 First node in the edge.
	 *  @param n2 Second node in the edge.
	 */
	public Edge(Node n1, Node n2)
	{
		this(n1,n2,false);
	}
	
	/** Creates a possibly directed edge between the given nodes.
	 *  @param n1 First node in the edge.
	 *  @param n2 Second node in the edge.
	 *  @param isDirected If true the edge runs from n1 to n2 but not the other way. If false,
	 *                    the edge represents an undirected connection between the two nodes.
	 */
	public Edge(Node n1, Node n2, boolean isDirected)
	{
		this.n1 = n1;
		this.n2 = n2;
		this.isDirected = isDirected;
		
		n1.addOutEdge(this);
		n2.addInEdge(this);
		
		if (isDirected == false)
		{
			n1.addInEdge(this);
			n2.addOutEdge(this);
		}
	}

	// ---------------------------------- Methods ----------------------------------
	
	/** Allows the edge to be drawn in the given Processing applet.
	 *  @param applet Processing sketch in which to draw this edge.
	 *  @param p1x x-coordinate of the first node in the edge.
	 *  @param p1y y-coordinate of the first node in the edge.
	 *  @param p2x x-coordinate of the second node in the edge.
	 *  @param p2y y-coordinate of the second node in the edge.
	 */
	@SuppressWarnings("static-method")
	public void draw(PApplet applet, float p1x, float p1y, float p2x, float p2y)
	{
		applet.line(p1x, p1y, p2x, p2y);
	}
	
	/** Reports the first node connected to this edge
	 *  @return First node connected to this edge.
	 */
	public Node getNode1()
	{
		return n1;
	}
	
	/** Reports the second node connected to this edge
	 *  @return Second node connected to this edge.
	 */
	public Node getNode2()
	{
		return n2;
	}
	
	/** Reports whether or not this edge is directed.
	 *  @return True if this edge is directed (from node1 to node2).
	 */
	public boolean isDirected()
	{
		return isDirected;
	}
}