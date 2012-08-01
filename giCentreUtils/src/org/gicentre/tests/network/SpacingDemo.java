package org.gicentre.tests.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gicentre.utils.geom.HashGrid;
import org.gicentre.utils.network.traer.physics.Attraction;
import org.gicentre.utils.network.traer.physics.Particle;
import org.gicentre.utils.network.traer.physics.ParticleSystem;
import org.gicentre.utils.network.traer.physics.Vector3D;

import processing.core.PApplet;

// ****************************************************************************************
/** Demonstrates the use of a custom force for efficient spacing of particles in space.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 1.0, 27th July, 2012.
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
public class SpacingDemo extends PApplet 
{

	// ------------------------------ Starter method ------------------------------- 

	/** Creates a simple application to test the chart drawing utilities.
	 *  @param args Command line arguments (ignored). 
	 */
	public static void main(String[] args)
	{   
		PApplet.main(new String[] {"org.gicentre.tests.network.SpacingDemo"});
	}

	// ----------------------------- Object variables ------------------------------

	private ParticleSystem physics;
	private ArrayList<Particle>particles;
	private HashGrid<Particle>hashGrid;

	private final static int NUM_PARTICLES = 2000;
	private final static int GRID_RADIUS =20;
	private final static int MAX_NUM_FORCES_PER_PARTICLE = 100;
	private final static int MAX_NUM_PARTICLES_WITH_FORCES = 200;

	// ----------------------------- Processing Methods -----------------------------

	public void setup()
	{
		size(600, 400);
		smooth();
		fill(0,100);
		ellipseMode(CENTER);

		physics = new ParticleSystem(0,0.05f);
		particles = new ArrayList<Particle>();
		hashGrid = new HashGrid<Particle>(width, height, GRID_RADIUS);

		for (int i=0; i<NUM_PARTICLES; i++)
		{
			//Particle p = physics.makeParticle(1, random(0,width-1), random(0,height-1), 0);
			Particle p = physics.makeParticle(1, width/2, height/2, 0);
			particles.add(p);
			hashGrid.add(p);
		}
	}

	public void draw()
	{
		background(255);
		
		// Draw hash grid bounds
		stroke(0);
		strokeWeight(0.5f);
		for (int x=0; x<width; x+=(2*GRID_RADIUS))
		{
			line(x,0,x,height);
		}
		for (int y=0; y<height; y+=(2*GRID_RADIUS))
		{
			line(0,y,width,y);
		}
		
		
		noStroke();
		
		

		// Space particles that share the same hashgrid cell.
		int numParticlesWithForces = MAX_NUM_PARTICLES_WITH_FORCES;
		
		physics.clearAllForces();
		List<Particle> candiateParticles = new ArrayList<Particle>(hashGrid.getAll());
		Collections.shuffle(candiateParticles);
		for (Particle p1 : candiateParticles)
		{
			// Get the hash grid to provide all the particles close to the current one.
			List<Particle> neighbours = new ArrayList<Particle>(hashGrid.get(p1.getLocation()));
			Collections.shuffle(neighbours);
			int numForcesPerParticle = MAX_NUM_FORCES_PER_PARTICLE;
			for (Particle p2 : neighbours)
			{
				// See if the neighbouring particles need to be adjusted.
				if (p1.distanceTo(p2) < 5)
				{
					physics.addCustomForce(new SpacingForce(p1, p2));
					numForcesPerParticle--;
				}
				if (numForcesPerParticle==0)
				{
					break;
				}
			}
			numParticlesWithForces--;
			
			if (numParticlesWithForces==0)
			{
				break;
			}
		}

		physics.tick();
		hashGrid.updateAll();

		for (Particle p : particles)
		{
			if (p.getForce().isZero())
			{
				fill(0,100);
			}
			else
			{
				System.err.println("Force found");
				fill(255,0,0,200);
			}
			ellipse(p.position().getX(), p.position().getY(), 8, 8);
		}
	}

	// ----------------------------------------- Nested Classes -----------------------------------------

	class SpacingForce extends Attraction
	{
		private ForcePair noForce;

		public SpacingForce(Particle oneEnd, Particle theOtherEnd)
		{
			super(oneEnd,theOtherEnd,-0.01f,1);	
			noForce   = equalAndOpposite(new Vector3D(0,0,0));
		}


		/** Implements the Attraction force calculation.
		 *  @return the ForcePair to act on oneEnd and theOtherEnd
		 */
		@Override 
		protected ForcePair forcePair() 
		{
			float d = getOneEnd().distanceTo(getTheOtherEnd());

			if (d < GRID_RADIUS)
			{
				// If the two particles are at the same location, space them randomly
				if (d <=1)
				{
					//getOneEnd().position().add(random(-0.1f,-0.1f), random(-0.1f,0.1f), 0);
					return equalAndOpposite(new Vector3D(random(-0.5f,0.5f), random(-0.5f,0.5f), 0));
				}

				Vector3D fromTheOtherEndtoOneEnd = Vector3D.subtract(getOneEnd().position(), getTheOtherEnd().position());
				fromTheOtherEndtoOneEnd.length(0.2f*getOneEnd().mass()*getTheOtherEnd().mass()/fromTheOtherEndtoOneEnd.lengthSquared());
				return equalAndOpposite(fromTheOtherEndtoOneEnd);
			}

			return noForce;

		}
	}
	/*
	class SpacingForce extends TwoBodyForce
	{
		private ForcePair repulsion, noForce;


		protected SpacingForce(Particle oneEnd, Particle theOtherEnd) throws NullPointerException 
		{
			super(oneEnd, theOtherEnd);
			repulsion = equalAndOpposite(new Vector3D(random(-0.5f,-0.1f), random(0.1f,0.5f), 0));
			noForce   = equalAndOpposite(new Vector3D(0,0,0));
		}

		@Override
		protected ForcePair forcePair() 
		{	
			// If the two particles are close together, apply a repulsive force, otherwise no force acts on the particles.
			if (getOneEnd().distanceTo(getTheOtherEnd()) < 1)
			{
				return repulsion;
			}

			return noForce;
		}	
	}
	 */
}
