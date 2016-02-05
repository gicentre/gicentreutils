package org.gicentre.tests.network;

import org.gicentre.utils.network.traer.physics.*;

import processing.core.PApplet;

// ****************************************************************************************
/** Particles in a box demo using the giCentre version of the traer physics library. 
 *  @author Jeffrey Traer Bernstein with minor modifications by Jo Wood.
 *  @version 3.4, 4th February, 2016.
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

public class Box extends PApplet 
{

	// ------------------------------ Starter method ------------------------------- 

	/** Creates a simple application to test the giCentre version of the Traer physics engine.
	 *  @param args Command line arguments (ignored). 
	 */
	public static void main(String[] args)
	{   
		PApplet.main(new String[] {"org.gicentre.tests.network.Box"});
	}

	// ----------------------------- Object variables ------------------------------

	private float bigBoxSize = 200;
	private float littleBoxSize = 8;

	private float xRotation = 0;
	private float yRotation = 0;

	private float gravityMagnitude = 1;
	private float bounceDamp = 0.5f;

	private int numberOfLittleBoxes = 400;

	private float repulsionStrength = 5;
	private float repulsionMinimum = 5;

	private ParticleSystem physics = new ParticleSystem(gravityMagnitude, 0.01f);

	// ----------------------------- Processing Methods -----------------------------

	/** Sets the size and of the sketch and its maximum pixel density.
     */
	public void settings()
	{
		size(400,400,P3D);
		pixelDensity(displayDensity());
	}
	
	public void setup()
	{
		physics.setIntegrator(Integrator.METHOD.MODEULER);

		for (int i=0; i<numberOfLittleBoxes; i++)
		{
			Particle p = physics.makeParticle(1, 
					random(-0.45f*littleBoxSize, 0.45f*littleBoxSize),
					random(-0.45f*littleBoxSize, 0.45f*littleBoxSize),
					random(-0.45f*littleBoxSize, 0.45f*littleBoxSize)); 

			for (int j=0; j<i; j++)
			{
				Particle q = physics.getParticle(j);
				physics.makeAttraction(p, q, -repulsionStrength, repulsionMinimum);
			}
		}
	}

	public void draw()
	{
		setRotation(-0.02f*(mouseY-height/2), 0.02f*(mouseX-width/2));

		physics.tick();
		bounce();

		background(255); 
		directionalLight(255, 255, 255, 0, 1, -1);
		translate(width/2, height/2);
		rotateX(xRotation);
		rotateY(yRotation);

		// Draw the big bounding box
		noFill();
		stroke( 192 );
		box( bigBoxSize );

		// Draw the smaller boxes (particles) inside the large box.
		drawLittleBoxes();
	}

	// -------------------------------- Private Methods --------------------------------

	private void setRotation(float xRotate, float yRotate)
	{
		physics.setGravity(gravityMagnitude*sin(xRotate)*sin(yRotate), gravityMagnitude*cos(xRotate), -gravityMagnitude*sin(xRotate)*cos(yRotate));
		xRotation = xRotate;
		yRotation = yRotate; 
	}

	private void drawLittleBoxes()
	{
		noStroke();
		fill(255);
		for (int i=0; i< physics.getNumParticles(); i++)
		{
			pushMatrix();
			Particle p = physics.getParticle(i);
			translate( p.position().getX(), p.position().getY(), p.position().getZ());
			box(littleBoxSize);
			popMatrix();
		}
	}

	private void bounce()
	{
		float collisionPoint = 0.5f*(bigBoxSize - littleBoxSize);
		for (int i=0; i<physics.getNumParticles(); i++)
		{
			Particle p = physics.getParticle(i);

			if (p.position().getX() < -collisionPoint)
			{
				p.position().setX(-collisionPoint);
				p.velocity().setX(-p.velocity().getX());
			}
			else
			{
				if (p.position().getX() > collisionPoint)
				{
					p.position().setX(collisionPoint);
					p.velocity().setX(-bounceDamp*p.velocity().getX());
				}
			}
			if (p.position().getY() < -collisionPoint)
			{
				p.position().setY(-collisionPoint);
				p.velocity().setY(-bounceDamp*p.velocity().getY());
			}
			else
			{
				if (p.position().getY() > collisionPoint)
				{
					p.position().setY(collisionPoint);
					p.velocity().setY(-bounceDamp*p.velocity().getY());
				}
			}
			if (p.position().getZ() < -collisionPoint)
			{
				p.position().setZ(-collisionPoint);
				p.velocity().setZ(-bounceDamp*p.velocity().getZ());
			}
			else
			{
				if (p.position().getZ() > collisionPoint)
				{
					p.position().setZ(collisionPoint);
					p.velocity().setZ(-bounceDamp*p.velocity().getZ());
				}
			}
		} 
	}
}