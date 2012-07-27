package org.gicentre.tests.network;

import org.gicentre.utils.network.traer.physics.Particle;
import org.gicentre.utils.network.traer.physics.ParticleSystem;

import processing.core.PApplet;
import processing.core.PImage;

// ****************************************************************************************
/** Simple cloud simulation using the giCentre version of the traer physics library. 
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
public class Cloud extends PApplet 
{

	// ------------------------------ Starter method ------------------------------- 

	/** Creates a simple application to test the chart drawing utilities.
	 *  @param args Command line arguments (ignored). 
	 */
	public static void main(String[] args)
	{   
		PApplet.main(new String[] {"org.gicentre.tests.network.Cloud"});
	}

	// ----------------------------- Object variables ------------------------------
	
	private Particle mouse;
	private Particle[] others;
	private ParticleSystem physics;
	private PImage img;
	
	// ----------------------------- Processing Methods -----------------------------

	public void setup()
	{
		size(400, 400);
		frameRate(24);
		cursor(CROSS);

		img = loadImage("fade.png");
		imageMode(CORNER);
		tint(0, 32);

		physics = new ParticleSystem(0, 0.1f);
		mouse = physics.makeParticle();
		mouse.makeFixed();

		others = new Particle[1000];
		for (int i=0; i<others.length; i++)
		{
			others[i] = physics.makeParticle(1.0f, random(0, width), random(0, height), 0);
			physics.makeAttraction(mouse, others[i], 5000, 50); 
		}
	}

	public void draw()
	{
		mouse.position().set(mouseX, mouseY, 0);
		physics.tick();
		background(255);

		for (int i=0; i<others.length; i++)
		{
			Particle p = others[i];
			image(img,p.position().getX()-img.width/2,p.position().getY()-img.height/2);
		}
	}
}
