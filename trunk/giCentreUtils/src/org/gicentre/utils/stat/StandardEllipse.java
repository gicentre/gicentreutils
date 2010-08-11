package org.gicentre.utils.stat;

import java.util.Collection;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

//********************************************************************************************
/** Class to create and store a standard ellipse representing the spread of a set of points.
 *  The long axis of the ellipse can represent twice the standard deviation of the spread of 
 *  points in the direction of maximum point dispersion. The short axis represents twice the 
 *   standard deviation of the spread of points in the direction of least dispersion. Points
 *  can be weighted by setting the z value of the <code>PVector</code>s used to store point
 *  coordinates. The ellipse parameters can be retrieved with the relevant accessor (getter)
 *  methods, or the class can draw the ellipse directly with the <code>draw()</code> method.
 *  For convenience, the scale of the ellipse can be controlled with <code>setScale()</code> 
 *  and the coordinates of the endpoints of the axes can also be retrieved. Weighted or 
 *  unweighted points can be used by selecting <code>setIsWeighted()</code>.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.0, 10th August, 2010. 
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

public class StandardEllipse 
{
    // ---------------------------- Object variables -----------------------------

    private PVector centre, wCentre;     // Centre of the ellipse (and weighted centre).
    private float majorAxis, minorAxis;  // Length of the ellipse's axes.
    private float wMajorAxis, wMinorAxis;
    private PVector a1,a2,b1,b2;         // End points of the major and minor axes.
    private PVector wa1,wa2,wb1,wb2;
    private float rotation,wRotation;    // Rotation of the major axis clockwise in radians.
    private float axisScale;             // Scaling factor to apply to the axes.
    private boolean useWeights;          // Determines if weighted points are used.
  
    // ------------------------------ Constructors -------------------------------
  
    /** Creates an ellipse with the given location, dimensions and rotation.
     *  @param centre Centre of the ellipse.
     *  @param major Length of the major axis of the ellipse.
     *  @param minor Length of the minor axis of the ellipse.
     *  @param rotation Clockwise rotation of the axes (can be negative for anti-clockwise)  
     */
    public StandardEllipse(PVector centre, float major, float minor, float rotation)
    {
        this.centre = centre;
        this.wCentre = centre;
        this.majorAxis = major;
        this.wMajorAxis = major;
        this.minorAxis = minor;
        this.wMinorAxis = minor;
        this.rotation = rotation;
        this.wRotation = rotation;
        axisScale = 1;
        useWeights = false;
        calcEndpoints();
    }
  
    /** Creates a standard ellipse from the given set of 2D point values. The angle of the two axes
     *  of the ellipse will represent the directions of greatest and least point dispersion. Their
     *  lengths will be 2*standard deviation of the spread in those directions. See Ebdon, p.139. 
     *  If the z component of each point is not zero, its value is used to calculate the weighted
     *  mean centre and dispersion as well as the unweighted version. The collection should store 
     *  objects of type <code>PVector</code>.
     *  @param points Collection of 2d point values.
     */
    public StandardEllipse(Collection<PVector> points)
    {  
        axisScale = 1;
        useWeights = false;
     
        // Check we have some points to process.
        if ((points == null) || (points.size() < 1))
        {
            centre = new PVector(0,0);
            wCentre = new PVector(0,0);
            majorAxis = 0;
            wMajorAxis = 0;
            minorAxis = 0;
            wMinorAxis = 0;
            rotation = 0;
            wRotation = 0;
            calcEndpoints();
            return;
        }
   
        float xTotal = 0, yTotal=0, weightTotal=0;
            
        // Calculate the mean centre.  
        for (PVector p : points)
        {
            xTotal += p.x;
            yTotal += p.y;
        }
        centre = new PVector(xTotal/points.size(),yTotal/points.size());
        
        // Calculate the weighted mean centre.  
        weightTotal = 0;
        xTotal = 0;
        yTotal = 0;
        for (PVector p : points)
        {
            float weight = 1;
            if (p.z > 0)
            {
                weight = p.z;
            }
      
            xTotal += p.x*weight;
            yTotal += p.y*weight;
     
            weightTotal+= weight;
        }
        wCentre = new PVector(xTotal/weightTotal,yTotal/weightTotal);
        
        // Calculate the unweighted rotation of the standard distance ellipse. Formula from Ebdon, 1985, pp.134-141.
        double sumXSq =0, sumYSq=0, sumXY=0;
        for (PVector p : points)
        {
            sumXSq += (p.x - centre.x)*(p.x - centre.x);
            sumYSq += (p.y - centre.y)*(p.y - centre.y);
            sumXY  += (p.x - centre.x)*(p.y - centre.y);
        }
       
        double tanAlpha = ((sumXSq-sumYSq) + Math.sqrt((sumXSq-sumYSq)*(sumXSq-sumYSq) + 4*sumXY*sumXY))/(2*sumXY);
        rotation = (float)Math.atan(tanAlpha);
        if (Double.isNaN(rotation))
        {
            rotation = 0;
        }
    
        // Calculate the major and minor axes.
        double sinAlpha = Math.sin(rotation),
               cosAlpha = Math.cos(rotation);
                 
        majorAxis = 2*(float)Math.sqrt((sumXSq*cosAlpha*cosAlpha - 2*sumXY*sinAlpha*cosAlpha + sumYSq*sinAlpha*sinAlpha)/points.size());
       
        if (Double.isNaN(majorAxis))
        {
            majorAxis = 0;
        }
     
        minorAxis = 2*(float)Math.sqrt((sumXSq*sinAlpha*sinAlpha + 2*sumXY*sinAlpha*cosAlpha + sumYSq*cosAlpha*cosAlpha)/points.size());
        if (Double.isNaN(minorAxis))
        {
            minorAxis = 0;
        } 
    
        // Make sure that the major axis is the longer one.
        if (majorAxis < minorAxis)
        {
            float temp = majorAxis;
            majorAxis = minorAxis;
            minorAxis = temp;
            rotation = (rotation+PConstants.HALF_PI)%PConstants.TWO_PI;
        }
        
        // ----------------------------------------------------------------
        // Calculate the weighted rotation of the standard distance ellipse.
        sumXSq =0;
        sumYSq=0;
        sumXY=0;
        for (PVector p : points)
        {
            float weight = 1;
            if (p.z > 0)
            {
                weight = p.z;
            }
            
            sumXSq += weight*(p.x - wCentre.x)*(p.x - wCentre.x);
            sumYSq += weight*(p.y - wCentre.y)*(p.y - wCentre.y);
            sumXY  += weight*(p.x - wCentre.x)*(p.y - wCentre.y);
        }
       
        tanAlpha = ((sumXSq-sumYSq) + Math.sqrt((sumXSq-sumYSq)*(sumXSq-sumYSq) + 4*sumXY*sumXY))/(2*sumXY);
        wRotation = (float)Math.atan(tanAlpha);
        if (Double.isNaN(wRotation))
        {
            wRotation = 0;
        }
    
        // Calculate the major and minor axes.
        sinAlpha = Math.sin(wRotation);
        cosAlpha = Math.cos(wRotation);
                 
        wMajorAxis = 2*(float)Math.sqrt((sumXSq*cosAlpha*cosAlpha - 2*sumXY*sinAlpha*cosAlpha + sumYSq*sinAlpha*sinAlpha)/weightTotal);
       
        if (Double.isNaN(wMajorAxis))
        {
            wMajorAxis = 0;
        }
     
        wMinorAxis = 2*(float)Math.sqrt((sumXSq*sinAlpha*sinAlpha + 2*sumXY*sinAlpha*cosAlpha + sumYSq*cosAlpha*cosAlpha)/weightTotal);
        if (Double.isNaN(wMinorAxis))
        {
            wMinorAxis = 0;
        } 
    
        // Make sure that the major axis is the longer one.
        if (wMajorAxis < wMinorAxis)
        {
            float temp = wMajorAxis;
            wMajorAxis = wMinorAxis;
            wMinorAxis = temp;
            wRotation = (wRotation+PConstants.HALF_PI)%PConstants.TWO_PI;
        }
    
        calcEndpoints(); 
    }
    
    // -------------------------------- Methods ----------------------------------
    
    /** Determines if the weighted or unweighted points are used to calculate the ellipse.
     *  Weights are stored in the 'z' component of each point.
     *  @param weighted Uses weighted points if true.
     */
    public void setIsWeighted(boolean weighted)
    {
        useWeights = weighted;
    }
    
    /** Reports whether or not weights are used in the calculation of the ellipse. Weights are stored
     *  in the 'z' component of each point.
     *  @return True if weighted points are used to calculate the ellipse.
     */
    public boolean isWeighted()
    {
        return useWeights;
    }
    
    /** Reports the centre of the ellipse. This will be the weighted centre if <code>isWeighted()</code> 
     *  is <code>true</code>, otherwise it reports the unweighted mean centre.
     *  @return Mean centre of the ellipse.
     */
    public PVector getCentre()
    {
        if (useWeights)
        {
            return wCentre;
        }
        return centre;
    }
  
    /** Reports the length of the major axis of the ellipse. This will represent the weighted dispersion
     *  if <code>isWeighted()</code> is true.
     *  @return Major axis of the ellipse.
     */
    public float getMajorAxis()
    {
        if (useWeights)
        {
            return wMajorAxis;
        }
        return majorAxis;
    }
  
    /** Reports the length of the minor axis of the ellipse. This will represent the weighted dispersion
     *  if <code>isWeighted()</code> is true.
     *  @return Minor axis of the ellipse.
     */
    public float getMinorAxis()
    {
        if (useWeights)
        {
            return wMinorAxis;
        }
        return minorAxis;
    }
  
    /** Reports the rotation of the ellipse. This will be the based on the weighted dispersion if 
     *  <code>isWeighted()</code> is true.
     *  @return Clockwise rotation of the ellipse in radians.
     */
    public float getRotation()
    {
        if (useWeights)
        {
            return wRotation;
        }
        return rotation;
    }
  
    /** Reports the coordinates of one of the endpoints of the major axis. This will always
     *  be the opposite end of that reported by <code>getMajorEndpoint2()</code>. This will be  
     *  based on the weighted dispersion if <code>isWeighted()</code> is true.
     *  @return Endpoint of the major axis.
     */
    public PVector getMajorEndpoint1()
    {
        if (useWeights)
        {
            return wa1;
        }
        return a1;
    }
  
    /** Reports the coordinates of one of the endpoints of the major axis. This will always
     *  be the opposite end of that reported by <code>getMajorEndpoint1()</code>. This will be  
     *  based on the weighted dispersion if <code>isWeighted()</code> is true.
     *  @return Endpoint of the major axis.
     */
    public PVector getMajorEndpoint2()
    {
        if (useWeights)
        {
            return wa2;
        }
        return a2;
    }
  
    /** Reports the coordinates of one of the endpoints of the minor axis. This will always
     *  be the opposite end of that reported by <code>getMinorEndpoint2()</code>. This will be  
     *  based on the weighted dispersion if <code>isWeighted()</code> is true.
     *  @return Endpoint of the minor axis.
     */
    public PVector getMinorEndpoint1()
    {
        if (useWeights)
        {
            return wb1;
        }
        return b1;
    }
  
    /** Reports the coordinates of one of the endpoints of the minor axis. This will always
     *  be the opposite end of that reported by <code>getMinorEndpoint1()</code>. This will be  
     *  based on the weighted dispersion if <code>isWeighted()</code> is true.
     *  @return Endpoint of the minor axis.
     */
    public PVector getMinorEndpoint2()
    {
        if (useWeights)
        {
            return wb2;
        }
        return b2;
    }
  
    /** Reports the scaling of the ellipse. A scaling factor of 1 means the radius of the major
     *  and minor axes represent 1 standard deviation of the point spread.
     *  @return Scaling factor of the ellipse.
     */
    public float getScale()
    {
        return axisScale;
    }
  
    /** Sets the scaling of the ellipse. A scaling factor of 1 means the radius of the major
     *  and minor axes represent 1 standard deviation of the point spread.
     *  @param axisScale New scaling factor of the ellipse.
     */
    public void setScale(float axisScale)
    {
        this.axisScale = axisScale;
        calcEndpoints();
    }
  
    /** Draws the ellipse. Uses the currently defined stroke weight, stroke and fill colours.
     *  @param sketch in which to draw the ellipse.
     */
    public void draw(PApplet sketch)
    {
        // Sequential transformation steps should be specified in opposite order in which
        // they will be applied. Rotation is of the coordinate space, so to rotate the ellipse
        // the coordinate space is rotated in the opposite direction. 
        sketch.pushMatrix();
        
        if (useWeights)
        {
            sketch.translate(wCentre.x,wCentre.y);
            sketch.rotate(-wRotation);
            sketch.ellipse(0,0,wMajorAxis*axisScale,wMinorAxis*axisScale);
        }
        else
        {
            sketch.translate(centre.x,centre.y);
            sketch.rotate(-rotation);
            sketch.ellipse(0,0,majorAxis*axisScale,minorAxis*axisScale);
        }
        sketch.popMatrix();
    }
  
    /** Draws the axes of the ellipse. Uses the currently defined stroke weight and colour.
     *  @param sketch in which to draw the axes.
     */
    public void drawAxes(PApplet sketch)
    {
        if (useWeights)
        {
            sketch.line(wa1.x,wa1.y,wa2.x,wa2.y);
            sketch.line(wb1.x,wb1.y,wb2.x,wb2.y);
        }
        else
        {
            sketch.line(a1.x,a1.y,a2.x,a2.y);
            sketch.line(b1.x,b1.y,b2.x,b2.y);
        }
    }
  
    // ---------------------------- Private methods ------------------------------
  
    /** Given the centre, scaling and rotation of the axes, this method calculates the 
     *  coordinates of the axes' endpoints.
     */
    private void calcEndpoints()
    {
        // Major axis.
        float x = (float)(axisScale*majorAxis*Math.cos(-rotation)/2.0);
        float y = (float)(axisScale*majorAxis*Math.sin(-rotation)/2.0);
        a1 = new PVector(x,y);
        a2 = new PVector(-x,-y);
        a1.add(centre);
        a2.add(centre);
    
        // Minor axis
        x = (float)(axisScale*minorAxis*Math.sin(rotation)/2.0);
        y = (float)(axisScale*minorAxis*Math.cos(rotation)/2.0);
        b1 = new PVector(x,y);
        b2 = new PVector(-x,-y);
        b1.add(centre);
        b2.add(centre);
        
        // Weighted major axis.
        x = (float)(axisScale*wMajorAxis*Math.cos(-wRotation)/2.0);
        y = (float)(axisScale*wMajorAxis*Math.sin(-wRotation)/2.0);
        wa1 = new PVector(x,y);
        wa2 = new PVector(-x,-y);
        wa1.add(wCentre);
        wa2.add(wCentre);
    
        // Weighted minor axis
        x = (float)(axisScale*wMinorAxis*Math.sin(wRotation)/2.0);
        y = (float)(axisScale*wMinorAxis*Math.cos(wRotation)/2.0);
        wb1 = new PVector(x,y);
        wb2 = new PVector(-x,-y);
        wb1.add(wCentre);
        wb2.add(wCentre);
    }
    
    /*
    public static void main(String[] args)
    {
        ArrayList<PVector>points = new ArrayList<PVector>();
        int width = 600;
        int height = 600;
          
        for (int i=0; i<200; i++)
        {  
            if (Math.random() < 0.5)
            {
              // Add a highly weighted point somewhere above-left of centre.
              points.add(new PVector(width/6 + (float)Math.random()*width/6,height/3 + (float)Math.random()*height/5-height/10,10));
            }
            else
            {
              // Add a unit weighted point anywhere in the central belt of the region.
              points.add(new PVector(width/2 + (float)Math.random()*width/1.5f-width/3,height/2+(float)Math.random()*height/3-height/6));
            }
        }
        
        StandardEllipse standardEllipse = new StandardEllipse(points);
        //standardEllipse.setIsWeighted(true);
        
        System.out.println("Weighting is "+standardEllipse.isWeighted());
        System.out.println("Centre at "+standardEllipse.getCentre());
        System.out.println("Major axis is "+standardEllipse.getMajorAxis());
        System.out.println("Minor axis is "+standardEllipse.getMinorAxis());
        System.out.println("Rotation is "+standardEllipse.getRotation());
    }
    */
}