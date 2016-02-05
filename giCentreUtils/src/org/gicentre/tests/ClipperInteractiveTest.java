package org.gicentre.tests;

import org.gicentre.utils.gui.Tooltip;
import org.gicentre.utils.move.Clipper;
import org.gicentre.utils.network.*;

import processing.core.PApplet;
import processing.core.PFont;

//  ****************************************************************************************
/** Tests interactive clipping using two clipping regions in the same sketch. This test is
 *  for backward compatibility only as Processing now provides its own clip() / noClip()
 *  methods.
 *  @author Alexander Kachkaev and Jo Wood, giCentre, City University London.
 *  @version 3.4, 5th February, 2016.
 */ 
//  ****************************************************************************************

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
@SuppressWarnings("deprecation")
public class ClipperInteractiveTest extends PApplet
{
	// ------------------------------ Starter method ------------------------------- 

	/** Creates a simple application to test multiple clipping regions in a single sketch.
	 *  @param args Command line arguments (ignored). 
	 */
	public static void main(String[] args)
	{   
		PApplet.main(new String[] {"org.gicentre.tests.ClipperInteractiveTest"});
	}

	// ----------------------------- Object variables ------------------------------	

	private PFont font;
	private ParticleViewer<Node,Edge> pViewer;
	private Clipper clipper1, clipper2;
	private Tooltip tooltip;
	private boolean showTooltip;

	// ---------------------------- Processing methods -----------------------------

	/** Sets the size and of the sketch and its maximum pixel density.
     */
	public void settings()
	{
		size(850,400);
		pixelDensity(displayDensity());
	}
	
    /** Sets up the sketch.
     */
	public void setup()
	{
		textAlign(CENTER,CENTER);
		font = createFont("Helvetica", 12);

		// Creating clippers
		float crSize = 300;
		clipper1 = new Clipper(this, (height - crSize)/2, (height - crSize)/2, crSize, crSize);
		clipper2 = new Clipper(this, 500, 150, 200, 100);

		// Creating a new particle viewer based on sketch dimensions
		pViewer = new ParticleViewer<Node,Edge>(this, height, height);
		generateData();

		// Creating a tooltip
		tooltip = new Tooltip(this, font, 12, 100);
	}

	/** Draws the sketch.
	 */
	public void draw()
	{
		background(255);

		// Drawing the boundary of the first clipping rectangle
		java.awt.geom.Rectangle2D.Float clippingRect = (java.awt.geom.Rectangle2D.Float) clipper1.getClippingRect();
		noFill();
		stroke(clipper1.isEnabled()?0:200,  100);
		rect(clippingRect.x, clippingRect.y, clippingRect.width, clippingRect.height);

		// Drawing the network in clipper1
		clipper1.startClipping();
		pViewer.draw();
		clipper1.stopClipping();

		// Drawing some random stuff in clipper2
		clipper2.startClipping();
		drawRandomStuffInClipper2();
		clipper2.stopClipping();

		drawText();

		drawTooltip();
	}

	// Allow nodes to be moved interactively
	public void mousePressed()
	{
		if (clipper1.contains(mouseX, mouseY) || !clipper1.isEnabled())
			pViewer.selectNearestWithMouse();
	}

	public void mouseDragged()
	{
		if (!clipper1.contains(mouseX, mouseY) && clipper1.isEnabled())
			pViewer.dropSelected();
	}

	public void mouseReleased()
	{
		pViewer.dropSelected();
	}

	// Allow zoom panning to be reset.
	public void keyPressed()
	{
		if (key == 'r')
		{
			pViewer.resetView();
		}
		else if (key == 't')
		{
			showTooltip = !showTooltip;
		}
		else if (key == '1')
		{
			clipper1.setEnabled(!clipper1.isEnabled());
		}
		else if (key == '2')
		{
			clipper2.setEnabled(!clipper2.isEnabled());
		}

		// Moving / scaling clipper1
		java.awt.geom.Rectangle2D.Float clippingRect = (java.awt.geom.Rectangle2D.Float) clipper1.getClippingRect();
		int dScale = 20;
		int dMove = 20;
		// Scaling the clipping rectangle up or down (adding or removing dScale value to/from width and height)
		if ((key == '+' || key == '=') && clippingRect.height <= height - dScale)
{
			clippingRect.width  += dScale;
			clippingRect.height += dScale;
			clippingRect.x  -= dScale/2;
			clippingRect.y -= dScale/2;
		}
		else if ((key == '-' || key == '_') && clippingRect.height >= 100) 
		{
			clippingRect.width  -= dScale;
			clippingRect.height -= dScale;
			clippingRect.x  += dScale/2;
			clippingRect.y += dScale/2;

			// Moving the clipping rectangle
		}
		else if (keyCode == UP) 
		{
			clippingRect.y -= dMove;
		} 
		else if (keyCode == RIGHT)
		{
			clippingRect.x += dMove;
		} 
		else if (keyCode == DOWN) 
		{
			clippingRect.y += dMove;
		}
		else if (keyCode == LEFT)
		{
			clippingRect.x -= dMove;
		}

		// Fiiting the clipper into height×height
		if (clippingRect.x < 0) 
		{
			clippingRect.x = 0;
		} 
		else if (clippingRect.x > height - clippingRect.width) 
		{
			clippingRect.x = height - clippingRect.width;
		}

		if (clippingRect.y < 0) 
		{
			clippingRect.y = 0;
		} 
		else if (clippingRect.y > height - clippingRect.height)
		{
			clippingRect.y = height - clippingRect.height;
		}

		// Applying new clippingRect if it was changed
		if (clipper1.getClippingRect() != clippingRect)
		{
			clipper1.setClippingRect(clippingRect);
		}
	}

	/** Randomly generates network data. 
	 */
	private void generateData()
	{
		int nodeCount = 10;
		int xMax = 300;
		int yMax = 200;

		Node[] nodes = new Node[nodeCount];
		for (int i = 0; i< nodeCount; i++)
		{
			nodes[i] = new Node(random(0, xMax), random(0, yMax));
			pViewer.addNode(nodes[i]);
		}

		for (int i = 0; i < nodeCount; i++)
		{
			for (int j = i+1; j < nodeCount; j++)
			{
				if (random(0, 3) != 0)
				{
					pViewer.addEdge(new Edge(nodes[i], nodes[j]));
				}
			}
		}

		// Ensure non-connected nodes are spaced apart from each other.
		pViewer.spaceNodes();
	}

	/** Performs some simple random circle drawing.
	 */
	private void drawRandomStuffInClipper2()
	{
		int rectMaring = -30;
		java.awt.geom.Rectangle2D.Float clippingRect = (java.awt.geom.Rectangle2D.Float) clipper2.getClippingRect();
		fill (150, 40);
		for (int i = 0; i < 30; i++) 
		{
			ellipse(clippingRect.x + rectMaring + random(clippingRect.width - rectMaring*2), clippingRect.y + rectMaring + random(clippingRect.height - rectMaring*2), 50, 50);
		}
	}

	/** Displays the text of the sketch.
	 */
	private void drawText()
	{
		fill(128);
		float textX = height + 50;
		textAlign(LEFT, BASELINE);
		textLeading(20);
		text("Clipper allows limiting drawing area when needed.\nYou can change dimensions of a box and toggle\nclipping. It is also possible to use multiple independent\nclippers in one sketch.", textX, 60);

		text("±↔↕", textX, 310);
		text("resize/move left clipper", textX + 50, 310);
		text("1 2", textX, 330);
		text("toggle left and right clippers", textX + 50, 330);
		text("t", textX, 350);
		text("show/hide mouse tooltip", textX + 50, 350);
	}

	/** Draws the tooltips if requested.
	 */
	private void drawTooltip() 
	{
		if (!showTooltip)
		{
			return;
		}

		String txt = "Mouse is outside clippers";
		if (clipper1.contains(mouseX, mouseY)) 
		{
			txt = "Mouse is over clipper 1" + (!clipper1.isEnabled()?", which is disabled":"");
		} 
		else if (clipper2.contains(mouseX, mouseY))
		{
			txt = "Mouse is over clipper 2" + (!clipper2.isEnabled()?", which is disabled":"");
		}
		tooltip.setText(txt);
		tooltip.draw(mouseX, mouseY);
	}
}