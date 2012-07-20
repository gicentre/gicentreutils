package org.gicentre.utils.network;

import java.util.HashSet;

import processing.core.PApplet;
import processing.core.PVector;


// *****************************************************************************************
/** Represents a node in a network graph structure. This class has a default drawing 
 *  behaviour, but by inheriting it and overriding its draw() method, visual appearance of
 *  the node can be customised.
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

public class Node 
{
	// ----------------------------- Object variables ------------------------------
    
	private PVector location;			// Cartesian location represented by this node.
	private HashSet<Edge> inEdges;		// Incoming edges attached to this node.
	private HashSet<Edge> outEdges;		// Outgoing edges attached to this node.
		                          
    // ------------------------------- Constructors --------------------------------
	
	/** Creates a node with the given location.
	 *  @param x x-coordinate of the node's location.
	 *  @param y y-coordinate of the node's location.
	 */
	public Node(float x, float y)
	{
		location = new PVector(x,y);
		inEdges  = new HashSet<Edge>();
		outEdges = new HashSet<Edge>();
	}

	// ---------------------------------- Methods ----------------------------------
	
	/** Draws the node in the given Processing applet. Note that the given position 
	 *  coordinates might not reflect the node's own locational coordinates if, for example,
	 *  drawing is part of a force-directed animation.
	 *  @param applet Processing sketch in which to draw this node.
	 *  @param px x-coordinate of this node's graphical position.
	 *  @param py y-coordinate of this node's graphical position.
	 */
	public void draw(PApplet applet, float px, float py)
	{
		applet.ellipse(px, py, 12,12);
	}
	
	/** Reports the node's location.
	 *  @return Location of this node.
	 */
	public PVector getLocation()
	{
		return location;
	}
	
	/** Reports a list of all incoming edges attached to this node.
	 *  If this node is part of an undirected graph, this will contain the same 
	 *  set as the outgoing edges.
	 */
	public HashSet<Edge>getInEdges()
	{
		return inEdges;
	}
	
	/** Reports a list of all outgoing edges attached to this node.
	 *  If this node is part of an undirected graph, this will contain the same 
	 *  set as the incoming edges.
	 */
	public HashSet<Edge>getOutEdges()
	{
		return outEdges;
	}
	
	/** Adds an incoming edge to those arriving at this node.
	 * @param edge Incoming edge to add.
	 */
	void addInEdge(Edge edge)
	{
		// Note this is package wide in scope because it should only be needed by the Node class.
		inEdges.add(edge);
	}
	
	/** Adds an outgoing edge to those leaving this node.
	 * @param edge Outgoing edge to add.
	 */
	void addOutEdge(Edge edge)
	{
		// Note this is package wide in scope because it should only be needed by the Node class.
		outEdges.add(edge);
	}
}
