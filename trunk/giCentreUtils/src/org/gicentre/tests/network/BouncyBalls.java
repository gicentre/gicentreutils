package org.gicentre.tests.network;

import org.gicentre.utils.network.traer.physics.Particle;
import org.gicentre.utils.network.traer.physics.ParticleSystem;

import processing.core.PApplet;

// ****************************************************************************************
/** Bouncy balls demo using the giCentre version of the traer physics library. 
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
public class BouncyBalls extends PApplet 
{
	// ------------------------------ Starter method ------------------------------- 

	/** Creates a simple application to test the chart drawing utilities.
	 *  @param args Command line arguments (ignored). 
	 */
	public static void main(String[] args)
	{   
		PApplet.main(new String[] {"org.gicentre.tests.network.BouncyBalls"});
	}

	// ----------------------------- Object variables ------------------------------

	private Particle mouse, b, c;
	private ParticleSystem physics;

	// ----------------------------- Processing Methods -----------------------------

	public void setup()
	{
		size(400, 400);
		frameRate(24);
		smooth();
		ellipseMode(CENTER);
		noStroke();
		noCursor();

		physics = new ParticleSystem();
		mouse = physics.makeParticle();
		mouse.makeFixed();
		b = physics.makeParticle(1.0f, random(0, width), random(0, height), 0);
		c = physics.makeParticle(1.0f, random(0, width), random(0, height), 0);

		physics.makeAttraction(mouse, b, 10000, 10);
		physics.makeAttraction(mouse, c, 10000, 10);
		physics.makeAttraction(b, c, -10000, 5);
	}

	public void draw()
	{
		mouse.position().set(mouseX, mouseY, 0);
		handleBoundaryCollisions(b);
		handleBoundaryCollisions(c);
		physics.tick();

		background(255);

		fill(150,180);
		ellipse(mouse.position().getX(), mouse.position().getY(), 35, 35);

		fill(200,50,50,180);
		ellipse(b.position().getX(), b.position().getY(), 35, 35);

		fill(50,50,200,180);
		ellipse(c.position().getX(), c.position().getY(), 35, 35);
	}

	/** Really basic collision strategy: sides of the window are walls
	  * if it hits a wall pull it outside the wall and flip the direction of the velocity
	  * the collisions aren't perfect so we take them down a notch too.
	  */
	private void handleBoundaryCollisions( Particle p )
	{
		if (p.position().getX() < 0 || p.position().getX() > width)
		{
			p.velocity().set(-0.9f*p.velocity().getX(), p.velocity().getY(), 0);
		}
		if (p.position().getY() < 0 || p.position().getY() > height)
		{
			p.velocity().set(p.velocity().getX(), -0.9f*p.velocity().getY(), 0);
		}
		p.position().set(constrain(p.position().getX(), 0, width), constrain(p.position().getY(), 0, height), 0); 
	}
}
