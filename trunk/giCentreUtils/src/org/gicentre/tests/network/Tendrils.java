package org.gicentre.tests.network;

import java.util.Vector;

import traer.physics.*;
//import org.gicentre.utils.network.traer.physics.*;

import processing.core.PApplet;

// ****************************************************************************************
/** Tendrils demo using the giCentre version of the traer physics library. 
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
public class Tendrils extends PApplet 
{
	// ------------------------------ Starter method ------------------------------- 

	/** Creates a simple application to test the chart drawing utilities.
	 *  @param args Command line arguments (ignored). 
	 */
	public static void main(String[] args)
	{   
		PApplet.main(new String[] {"org.gicentre.tests.network.Tendrils"});
	}

	// ----------------------------- Object variables ------------------------------

	private Vector<T3ndril> tendrils;
	private ParticleSystem physics;
	private Particle mouse;
	private int greyer;
	private boolean drawing;

	// ----------------------------- Processing Methods -----------------------------

	public void setup()
	{
		size(400, 400);
		smooth();
		stroke(0);
		background(255);
		cursor(CROSS);	  

		physics = new ParticleSystem(0.0f, 0.05f);

		mouse = physics.makeParticle();
		mouse.makeFixed();

		tendrils = new Vector<Tendrils.T3ndril>();
		drawing = false;
		greyer = 255;
	}

	public void draw()
	{
		mouse.position().set( mouseX, mouseY, 0 );

		if (!drawing)
		{
			physics.tick();
			if (greyer < 255)
			{
				greyer *= 1.11111;
			}
			if (greyer > 255)
			{
				greyer = 255;
			}
		}
		else
		{
			if (greyer >= 64)
			{
				greyer *= 0.9;
			}
		}

		background(255);
		drawOldGrey();
	}

	public void mousePressed()
	{
		drawing = true;
		tendrils.add(new T3ndril( physics, new Vector3D(mouseX, mouseY, 0), mouse));
	}

	public void mouseDragged()
	{
		tendrils.lastElement().addPoint(new Vector3D(mouseX, mouseY, 0));
	}

	public void mouseReleased()
	{
		drawing = false;
	}

	public void keyPressed()
	{
		if ( key == ' ' )
		{
			tendrils.clear();
			physics.clear();
		}
	}

	// -------------------------------- Private Methods --------------------------------
	
	private void drawOldGrey()
	{
		stroke(255 - greyer);
		for (int i=0; i<tendrils.size()-1; ++i)
		{
			T3ndril t = tendrils.get(i);
			drawElastic(t);
		}

		stroke(0);
		if (tendrils.size()-1 >= 0)
		{
			drawElastic(tendrils.lastElement());
		}
	}

	private void drawElastic(T3ndril t)
	{
		float lastStretch = 1;
		for (int i=0; i<t.particles.size()-1; ++i)
		{
			Vector3D firstPoint = t.particles.get(i).position();
			Vector3D firstAnchor =  i < 1 ? firstPoint : t.particles.get(i-1).position();
			Vector3D secondPoint = i+1 < t.particles.size() ? t.particles.get(i+1).position() : firstPoint;
			Vector3D secondAnchor = i+2 < t.particles.size() ? t.particles.get(i+2).position() : secondPoint;

			//float springStretch = 2.5f/((Spring)t.springs.get( i )).stretch();
			Spring s = t.springs.get(i);
			float springStretch = 2.5f*s.restLength()/s.currentLength();

			strokeWeight((springStretch + lastStretch)/2.0f);	// smooth out the changes in stroke width with filter
			lastStretch = springStretch;

			curve( firstAnchor.x(), firstAnchor.y(),
					firstPoint.x(), firstPoint.y(),
					secondPoint.x(), secondPoint.y(),
					secondAnchor.x(), secondAnchor.y() );
		}
	}
	
	// --------------------------------- Nested classes --------------------------------
	
	class T3ndril
	{
		public Vector<Particle> particles;
		public Vector<Spring> springs;
		ParticleSystem pSystem;
		
		public T3ndril( ParticleSystem p, Vector3D firstPoint, Particle followPoint )
		{
			particles = new Vector<Particle>();
			springs = new Vector<Spring>();
			
			pSystem = p;
			
			Particle firstParticle = p.makeParticle( 1.0f, firstPoint.x(), firstPoint.y(), firstPoint.z() );
			particles.add( firstParticle );
			pSystem.makeSpring( followPoint, firstParticle, 0.1f, 0.1f, 5 );
		}

		public void addPoint(Vector3D p)
		{
			Particle thisParticle = pSystem.makeParticle( 1.0f, p.x(), p.y(), p.z() );
			springs.add(pSystem.makeSpring((particles.lastElement()), thisParticle,1f,1f,particles.lastElement().position().distanceTo( thisParticle.position())));
			particles.add(thisParticle);
		}
	}
}
