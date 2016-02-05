package org.gicentre.utils.geom;

import java.util.ArrayList;
import java.util.List;

import processing.core.PVector;

//  ****************************************************************************************
/** Class for representing an ellipse. Unlike Processing's own ellipse command this can 
 *  represent ellipses with axes aligned at any angle. See also 
 *  <a href="http://www.spaceroots.org/documents/ellipse" target="_blank">www.spaceroots.org/documents/ellipse</a>
 *  for some of the mathematical derivations used in this class. 
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.4, 5th February, 2016.
 */ 
//  *****************************************************************************************

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

public class Ellipse 
{
	// ------------------------- Object and class variables ------------------------

	private double cx, cy;                         	// Centre of ellipse
	private double a, b;                           	// Major and minor semi-axes.
	//private double theta;                          	// Orientation of ellipse.
	private double cosTheta, sinTheta;             	// Precomputed constants.
	private PVector f1,f2;							// The ellipse's two foci.

	private static final double EPSILON = 0.00001;	// Rounding error constant.
	private static final double HALF_PI = Math.PI/2.0;
	private static final double TWO_PI  = Math.PI*2;

	// ------------------------------- Constructors --------------------------------

	
	/** Creates and ellipse with axes aligned to coordinate axes.
	 *  @param cx x-coordinate of the centre of the ellipse.
	 *  @param cy y-coordinate of the centre of the ellipse.
	 *  @param eWidth Width of the ellipse.
	 *  @param eHeight Height of the ellipse.
	 */
	public Ellipse(double cx, double cy, double eWidth, double eHeight)
	{
		this(cx,cy,eWidth,eHeight,0);
	}
	
	/** Creates and ellipse major axis orientated at the given angle.
	 *  @param cx x-coordinate of the centre of the ellipse.
	 *  @param cy y-coordinate of the centre of the ellipse.
	 *  @param eWidth Width of the ellipse.
	 *  @param eHeight Height of the ellipse.
	 *  @param theta Orientation of the major axis (in radians).
	 */
	public Ellipse(double cx, double cy, double eWidth, double eHeight, double theta)
	{
		this.cx = cx;
		this.cy = cy;
		this.a  = Math.max(eWidth/2, eHeight/2);
		this.b  = Math.min(eWidth/2, eHeight/2);
		//this.theta = theta;

		if (eHeight > eWidth)
		{
			//this.theta += HALF_PI;
			cosTheta = Math.cos(theta+HALF_PI);
			sinTheta = Math.sin(theta+HALF_PI);
		}
		else
		{
			cosTheta = Math.cos(theta);
			sinTheta = Math.sin(theta);
		}
		
		f1 = new PVector((float)(cx - Math.sqrt(a*a - b*b)*cosTheta),
						 (float)(cy - Math.sqrt(a*a - b*b)*sinTheta));
		f2 = new PVector((float)(cx + Math.sqrt(a*a - b*b)*cosTheta),
				         (float)(cy + Math.sqrt(a*a - b*b)*sinTheta));
	}

	// ---------------------------------- Methods ----------------------------------

	/** Reports the elliptical position for any given angle lambda. 
	 * @param lamda Angle in radians from which elliptical position is to be calculated.
	 * @return Position on circumference of ellipse corresponding to the given angle.
	 */
	public PVector getPosition(double lamda)
	{
		double eta = getEta(lamda);
		double cosNeta = Math.cos(eta);
		double sinNeta = Math.sin(eta);

		return new PVector((float)(cx + a*cosTheta*cosNeta - b*sinTheta*sinNeta), 
				           (float)(cy + a*sinTheta*cosNeta + b*cosTheta*sinNeta));
	}

	/** Provides the cubic Bezier anchor (p) and control points (q) that approximate
	 *  the elliptical arc between the two given angles. Note that accuracy will depend
	 *  on the eccentricity of the ellipse and the curvature of the given angular segment.
	 *  Where more accurate representations, especially for larger angular segments, 
	 *  consider using getBezierVertices() instead.
	 *  @param startAngle First angle in arc (radians).
	 *  @param endAngle Second angle in arc (radians).
	 *  @return Array containing vertex points and control points in the order [p1,q1,q2,p2]
	 *          which matches the order required by Processing's own bezier() method.
	 */
	public PVector[] getBezier(double startAngle, double endAngle)
	{
		// Degenerate case of identical start and end points.
		if (startAngle == endAngle)
		{
			PVector p = getPosition(startAngle);
			return new PVector[] {p,p,p,p};
		}

		double eta1 = getEta(startAngle);
		double eta2 = getEta(endAngle);
		PVector p1 = getPosition(startAngle);
		PVector p2 = getPosition(endAngle);
		double tanSq = Math.tan((eta2-eta1)/2);
		tanSq = tanSq*tanSq;
		double alpha = Math.sin(eta2-eta1) * ((Math.sqrt(4+3*tanSq)-1)/3);

		PVector q1 = getEDash(startAngle);
		PVector q2 = getEDash(endAngle);
		q1.x = (float)(p1.x + alpha*q1.x);
		q1.y = (float)(p1.y + alpha*q1.y);

		q2.x = (float)(p2.x - alpha*q2.x);
		q2.y = (float)(p2.y - alpha*q2.y);

		return new PVector[] {p1, q1, q2, p2};
	}
	
	/** Provides a collection of Bezier anchor (p) and control (q) points representing 
	 *  the elliptical arc between the given angles. Unlike <code>getBezier()</code>, this 
	 *  method can be used to define any arc length from 0 to 2PI radians and should be 
	 *  visibly accurate.
	 *  @param startAngle First angle in arc (radians).
	 *  @param endAngle Second angle in arc (radians).
	 *  @return Array containing vertex points and control points in the order [p1,q1,q2,p2,q3,q4,p3...etc.]
	 *          which matches the order required when calling Processing's own <code>bezierVertex()</code>
	 *          methods from between <code>beginShape()</code> and <code>endShape()</code>. Note that when
	 *          doing this, the first anchor point (p1) should be fixed with a call to <code>vertex()</code>.
	 */
	public PVector[] getBezierVertices(double startAngle, double endAngle)
	{
		double arcAngle = clockwiseAngleBetween(startAngle, endAngle);
		List<PVector> vertices = new ArrayList<PVector>();

		// First anchor point.
		vertices.add(getPosition(startAngle));

		if (arcAngle > EPSILON)  // Account for rounding error of arc with 0 length.
		{
			// Store the required number of 90 degree arcs
			double a1 = startAngle;
			double a2;
			double angleProg = 0;

			while (angleProg < arcAngle-EPSILON)
			{
				double angleInc = HALF_PI -(((a1%TWO_PI)+TWO_PI)%HALF_PI);
				if (angleInc < EPSILON)
				{  
					angleInc = HALF_PI;    // Account for possible rounding errors.
				}

				if (angleProg+angleInc >= arcAngle)
				{
					a2 = endAngle;
					angleProg = arcAngle;
				}
				else
				{
					angleProg += angleInc;
					a2 = a1+angleInc;
				}

				PVector[] arc = getBezier(a1,a2);
				vertices.add(arc[1]);
				vertices.add(arc[2]);
				vertices.add(arc[3]);
				a1 = a2;
			}
		}

		// Convert to array
		PVector[] aVertices = new PVector[vertices.size()];
		for (int i=0; i<vertices.size(); i++)
		{
			aVertices[i] = vertices.get(i);
		}
		return aVertices;
	}

	
	/** Calculates the clockwise angle between the given first and second angles.
	 *  @param startAngle First angle in arc (radians).
	 *  @param endAngle Second angle in arc (radians).
	 *  @return Clockwise angle between the given angles (can be more than 2PI radians).
	 */
	public static double clockwiseAngleBetween(double startAngle, double endAngle)
	{
		PVector v1 = PVector.fromAngle((float)startAngle);
		PVector v2 = PVector.fromAngle((float)endAngle);
		PVector cp = v1.cross(v2);

		if (cp.z < 0)
		{
			// angle >180 degrees.
			return TWO_PI - PVector.angleBetween(v1, v2);
		}

		// Angle <= 180 degrees.
		return PVector.angleBetween(v1, v2);

	}
	
	/** Reports the centre position of the ellipse.
	 *  @return Centre position of the ellipse.
	 */
	public PVector getCentre()
	{
		return new PVector((float)cx,(float)cy);
	}
	
	/** Reports the semi-major axis of the ellipse (its longest radius from the centre to circumference).
	 *  @return semi-major axis.
	 */
	public double getMajor()
	{
		return a;
	}
	
	/** Reports the semi-minor axis of the ellipse (its shortest radius from the centre to circumference).
	 *  @return semi-minor axis.
	 */
	public double getMinor()
	{
		return b;
	}
	
	/** Reports the first focus of the ellipse.
	 *  @return First focus of the ellipse.
	 */
	public PVector getFocus1()
	{
		return f1;
	}
	
	/** Reports the second focus of the ellipse.
	 *  @return Second focus of the ellipse.
	 */
	public PVector getFocus2()
	{
		return f2;
	}

	// ------------------------------- Private methods ------------------------------

	/** Reports the virtual angle eta for any given elliptical angle lambda.
	 *  @param lambda Elliptical angle (in radians).
	 *  @return Angle eta equivalent to the given angle.
	 */
	private double getEta(double lambda)
	{
		return Math.atan2(Math.sin(lambda)/b, Math.cos(lambda)/a);
	}

	/* * Reports the elliptical angle eta for any given virtua angle eta.
	 *  @param eta Virtual angle (in radians) equivalent to the given elliptical angle.
	 *  @return Elliptical angle (in radians).
	 * /
	private double getLambda(double eta)
	{
		return Math.atan2(b*Math.sin(eta), a*Math.cos(eta));
	}
	*/
	
	/** Reports elliptical derivative for any given angle lambda.
	 * @param lambda Elliptical angle.
	 * @return Derivative of the parametric function at angle lambda that describes this ellipse.
	 */
	private PVector getEDash(double lambda)
	{
		double eta = getEta(lambda);
		double cosEta = Math.cos(eta);
		double sinEta = Math.sin(eta); 
		return new PVector((float)(-a*cosTheta*sinEta - b*sinTheta*cosEta), 
						   (float)(-a*sinTheta*sinEta + b*cosTheta*cosEta));
	}

}
