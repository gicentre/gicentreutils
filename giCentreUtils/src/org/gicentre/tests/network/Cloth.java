package org.gicentre.tests.network;

import org.gicentre.utils.network.traer.physics.Particle;
import org.gicentre.utils.network.traer.physics.ParticleSystem;

import processing.core.PApplet;

// ****************************************************************************************
/** Simple cloth simulation using the giCentre version of the traer physics library. 
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
public class Cloth extends PApplet 
{

	// ------------------------------ Starter method ------------------------------- 

	/** Creates a simple application to test the chart drawing utilities.
	 *  @param args Command line arguments (ignored). 
	 */
	public static void main(String[] args)
	{   
		PApplet.main(new String[] {"org.gicentre.tests.network.Cloth"});
	}

	// ----------------------------- Object variables ------------------------------

	private ParticleSystem physics;
	private Particle[][] particles;
	private int gridSize = 10;

	private float SPRING_STRENGTH = 0.2f;
	private float SPRING_DAMPING = 0.1f;

	// ----------------------------- Processing Methods -----------------------------

	public void setup()
	{
		size(400, 400);
		smooth();
		fill(0);

		physics = new ParticleSystem(0.1f, 0.01f);

		particles = new Particle[gridSize][gridSize];

		float gridStepX = (width / 2f) / gridSize;
		float gridStepY = (height / 2f) / gridSize;

		for (int i = 0; i < gridSize; i++)
		{
			for (int j = 0; j < gridSize; j++)
			{
				particles[i][j] = physics.makeParticle(0.2f, j * gridStepX + (width / 4), i * gridStepY + 20, 0.0f);
				if (j > 0)
				{
					physics.makeSpring(particles[i][j - 1], particles[i][j], SPRING_STRENGTH, SPRING_DAMPING, gridStepX);
				}
			}
		}

		for (int j = 0; j < gridSize; j++)
		{
			for (int i = 1; i < gridSize; i++)
			{
				physics.makeSpring(particles[i - 1][j], particles[i][j], SPRING_STRENGTH, SPRING_DAMPING, gridStepY);
			}
		}

		particles[0][0].makeFixed();
		particles[0][gridSize - 1].makeFixed();
	}

	public void draw()
	{
		physics.tick();

		if (mousePressed)
		{
			particles[0][gridSize - 1].position().set(mouseX, mouseY, 0);
			particles[0][gridSize - 1].velocity().clear();
		}

		noFill();
		background(255);

		for (int i = 0; i < gridSize; i++)
		{
			beginShape();			
			curveVertex(particles[i][0].position().getX(), particles[i][0].position().getY());
			for (int j = 0; j < gridSize; j++)
			{
				curveVertex(particles[i][j].position().getX(), particles[i][j].position().getY());
			}
			curveVertex(particles[i][gridSize - 1].position().getX(), particles[i][gridSize - 1].position().getY());
			endShape();
		}
		for (int j = 0; j < gridSize; j++)
		{
			beginShape();
			curveVertex(particles[0][j].position().getX(), particles[0][j].position().getY());
			for (int i = 0; i < gridSize; i++)
			{
				curveVertex(particles[i][j].position().getX(), particles[i][j].position().getY());
			}
			curveVertex(particles[gridSize - 1][j].position().getX(), particles[gridSize - 1][j].position().getY());
			endShape();
		}
	}

	public void mouseReleased()
	{
		particles[0][gridSize - 1].velocity().set( (mouseX - pmouseX), (mouseY - pmouseY), 0 );
	}
}
