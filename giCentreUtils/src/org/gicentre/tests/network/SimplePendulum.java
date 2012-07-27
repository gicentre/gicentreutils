package org.gicentre.tests.network;

//import traer.physics.*;
import org.gicentre.utils.network.traer.physics.Particle;
import org.gicentre.utils.network.traer.physics.ParticleSystem;

import processing.core.PApplet;

// ****************************************************************************************
/** Simple pendulum using the giCentre version of the traer physics library. 
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
public class SimplePendulum extends PApplet 
{

	// ------------------------------ Starter method ------------------------------- 

	/** Creates a simple application to test the chart drawing utilities.
	 *  @param args Command line arguments (ignored). 
	 */
	public static void main(String[] args)
	{   
		PApplet.main(new String[] {"org.gicentre.tests.network.SimplePendulum"});
	}

	// ----------------------------- Object variables ------------------------------

	private ParticleSystem physics;

	private Particle p;
	private Particle anchor;


	// ----------------------------- Processing Methods -----------------------------

	public void setup()
	{
		size(400, 400);
		smooth();
		fill(0,100);
		ellipseMode(CENTER);

		physics = new ParticleSystem(1, 0.05f);

		p = physics.makeParticle(1.0f, width/2, height/2, 0);
		anchor = physics.makeParticle(1.0f, width/2, height/2, 0);
		anchor.makeFixed(); 
		physics.makeSpring(p, anchor, 0.5f, 0.1f, 75);
	}

	public void draw()
	{
		physics.tick();
		background(255);

		line(p.position().getX(), p.position().getY(), anchor.position().getX(), anchor.position().getY());
		ellipse(anchor.position().getX(), anchor.position().getY(), 5, 5);
		ellipse(p.position().getX(), p.position().getY(), 20, 20);
	}

	public void mousePressed()
	{
		p.makeFixed(); 
		p.position().set(mouseX, mouseY, 0);
	}

	public void mouseDragged()
	{
		p.position().set(mouseX, mouseY, 0);
	}

	public void mouseReleased()
	{
		p.makeFree(); 
	}
}
