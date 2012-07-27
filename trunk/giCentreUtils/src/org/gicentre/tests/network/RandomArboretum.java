package org.gicentre.tests.network;

import org.gicentre.utils.network.traer.physics.Particle;
import org.gicentre.utils.network.traer.physics.ParticleSystem;
import org.gicentre.utils.network.traer.physics.Spring;

import processing.core.PApplet;

// ****************************************************************************************
/** Random arboretum example using the giCentre version of the traer physics library. 
 *  @author Jeffrey Traer Bernstein with minor modifications by Jo Wood.
 *  @version 1.1, 27th July, 2012.
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
public class RandomArboretum extends PApplet 
{

	// ------------------------------ Starter method ------------------------------- 

	/** Creates a simple application to test the chart drawing utilities.
	 *  @param args Command line arguments (ignored). 
	 */
	public static void main(String[] args)
	{   
		PApplet.main(new String[] {"org.gicentre.tests.network.RandomArboretum"});
	}

	// ----------------------------- Object variables ------------------------------

	private final float NODE_SIZE = 10;
	private final float EDGE_LENGTH = 20;
	private final float EDGE_STRENGTH = 0.2f;
	private final float SPACER_STRENGTH = 1000;

	private ParticleSystem physics;
	private float scale = 1;
	private float centroidX = 0;
	private float centroidY = 0;


	// ----------------------------- Processing Methods -----------------------------

	public void setup()
	{
		size(400, 400);
		smooth();
		strokeWeight(2);
		ellipseMode(CENTER);

		physics = new ParticleSystem(0, 0.1f);

		// Runge-Kutta, the default integrator is stable and snappy,
		// but slows down quickly as you add particles.
		// 500 particles = 7 fps on my machine

		// Try this to see how Euler is faster, but borderline unstable.
		// 500 particles = 24 fps on my machine
		//physics.setIntegrator( ParticleSystem.MODIFIED_EULER ); 

		// Now try this to see make it more damped, but stable.
		//physics.setDrag( 0.2 );

		textFont(loadFont("BonvenoCF-Light-10.vlw"));
		initialize();
	}

	public void draw()
	{
		physics.tick(); 
		if (physics.getNumParticles() > 1)
		{
			updateCentroid();
		}
		background(255);
		fill(0);
		text("" + physics.getNumParticles() + " PARTICLES\n" + (int)frameRate + " FPS", 10, 20);
		translate(width/2 , height/2);
		scale(scale);
		translate(-centroidX, -centroidY);

		drawNetwork();  
	}

	public void mousePressed()
	{
		addNode();
	}

	public void mouseDragged()
	{
		addNode();
	}

	public void keyPressed()
	{
		if (key == 'c')
		{
			initialize();
		}
		else if (key == ' ')
		{
			addNode();
		}
	}

	// -------------------------------- Private Methods --------------------------------

	private void updateCentroid()
	{
		float 
		xMax = Float.NEGATIVE_INFINITY, 
		xMin = Float.POSITIVE_INFINITY, 
		yMin = Float.POSITIVE_INFINITY, 
		yMax = Float.NEGATIVE_INFINITY;

		for (int i=0; i<physics.getNumParticles(); ++i)
		{
			Particle p = physics.getParticle(i);
			xMax = max(xMax, p.position().getX());
			xMin = min(xMin, p.position().getX());
			yMin = min(yMin, p.position().getY());
			yMax = max(yMax, p.position().getY());
		}
		float deltaX = xMax-xMin;
		float deltaY = yMax-yMin;

		centroidX = xMin + 0.5f*deltaX;
		centroidY = yMin +0.5f*deltaY;

		if (deltaY > deltaX)
		{
			scale = height/(deltaY+50);
		}
		else
		{
			scale = width/(deltaX+50);
		}
	}

	private void addSpacersToNode(Particle p, Particle r)
	{
		for (int i=0; i<physics.getNumParticles(); ++i)
		{
			Particle q = physics.getParticle(i);
			if ((p != q) && (p != r))
			{
				physics.makeAttraction(p, q, -SPACER_STRENGTH, 20);
			}
		}
	}

	private void makeEdgeBetween( Particle a, Particle b )
	{
		physics.makeSpring(a, b, EDGE_STRENGTH, EDGE_STRENGTH, EDGE_LENGTH);
	}

	private void initialize()
	{
		physics.clear();
		physics.makeParticle();
	}

	private void addNode()
	{ 
		Particle p = physics.makeParticle();
		Particle q = physics.getParticle((int)random(0, physics.getNumParticles()-1));
		while (q == p)
		{
			q = physics.getParticle( (int)random(0, physics.getNumParticles()-1));
		}
		addSpacersToNode(p, q);
		makeEdgeBetween(p, q);
		p.position().set(q.position().getX() + random(-1, 1), q.position().getY() + random(-1, 1), 0);
	}

	private void drawNetwork()
	{      
		// draw vertices
		fill(160);
		noStroke();
		for (int i = 0; i < physics.getNumParticles(); ++i)
		{
			Particle v = physics.getParticle(i);
			ellipse(v.position().getX(), v.position().getY(), NODE_SIZE, NODE_SIZE);
		}

		// draw edges 
		stroke(0);
		beginShape(LINES);
		for (int i=0; i<physics.getNumSprings(); ++i)
		{
			Spring e = physics.getSpring(i);
			Particle a = e.getOneEnd();
			Particle b = e.getTheOtherEnd();
			vertex(a.position().getX(), a.position().getY());
			vertex(b.position().getX(), b.position().getY());
		}
		endShape();
	}
}
