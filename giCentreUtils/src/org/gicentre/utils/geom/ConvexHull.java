package org.gicentre.utils.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import processing.core.PVector;

//  ****************************************************************************************
/** Class for representing and building a convex hull around a set of point values. 
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.2, 1st August, 2011. 
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

public class ConvexHull
{    
    // ----------------------------- Object variables ------------------------------
    
    private Collection<PVector>points;
    private ArrayList<PVector> hull;
    private boolean needsNewCalculation;
    
    // ------------------------------- Constructors --------------------------------
    
    /** Creates a convex hull from the given collection of point values.
     *  @param points Points around which the convex hull is created.
     */
    public ConvexHull(Collection<PVector>points)
    {
        this.points = points;
        hull = calcHull(points);
        needsNewCalculation = false;
    }

    // ---------------------------------- Methods ----------------------------------
    
    /** Calculates the convex hull from the given collection of point values.
     *  @param points Points around which the convex hull is created.
     *  @return Ordered list of points representing the convex hull.
     */
    public static ArrayList<PVector> getHull(Collection<PVector> points)
    {
        return calcHull(points);
    }
    
    /** Provides the convex hull that surrounds the collection of points stored in this object.
     *  @return Ordered list of points representing the convex hull.
     */
    public ArrayList<PVector> getHull()
    {
        if (needsNewCalculation)
        {
            hull = calcHull(points);
            needsNewCalculation = false;
        }
        return hull;
    }
    
    /** Adds a given point to the collection from which the hull is to be constructed.
     *  @param point Point to add.
     */
    public void addPoint(PVector point)
    {
        points.add(point);
        needsNewCalculation = true;
    }
    
    /** Removes a given point from the collection from which the hull is to be constructed.
     *  @param point Point to remove.
     *  @return True if the point was found and removed.
     */
    public boolean removePoint(PVector point)
    {
        boolean success = points.remove(point);
        if (success)
        {
            needsNewCalculation = true;
        }
        return success;
    }

    // ------------------------------ Private methods ------------------------------
     
    /** Calculates the convex hull using the Andrew's variant of the Graham scan.
     *  This gives an O(n.Log n) time solution. See <a href="http://marknelson.us/2007/08/22/convex/">
     *  marknelson.us/2007/08/22/convex/</a> for details.
     *  @param points Points around which hull is constructed.
     */
    private static ArrayList<PVector>calcHull(Collection<PVector> points)
    {        
        // Sort the points in x-order.
        ArrayList<PVector> sortedPoints = new ArrayList<PVector>(points);
        Collections.sort(sortedPoints,new XSort());
        
        // If we have fewer than 4 points, no need to calculate the hull points.
        if (sortedPoints.size() <4)
        {
            ArrayList<PVector> hullPoints = new ArrayList<PVector>();
            for (PVector point : sortedPoints)
            {
                hullPoints.add(point);
            }
            return hullPoints;
        }

        // Partition the points into those above and below the line joining left and right most points.
        PVector left  = sortedPoints.remove(0);
        PVector right = sortedPoints.remove(sortedPoints.size()-1);
        ArrayList<PVector> upperPoints = new ArrayList<PVector>();
        ArrayList<PVector> lowerPoints = new ArrayList<PVector>();
        ArrayList<PVector> upperHull = new ArrayList<PVector>();
        ArrayList<PVector> lowerHull = new ArrayList<PVector>();
        
        for (PVector p : sortedPoints)
        {
            if (findDirection(left,right,p) < 0)
            {
                upperPoints.add(p);
            }
            else
            {
                lowerPoints.add(p);
            }
        }
        
        // Complete lower part of hull.
        lowerPoints.add(right);
        lowerHull.add(left);
        
        while (lowerPoints.size() > 0)
        {
            lowerHull.add(lowerPoints.remove(0));
            while (lowerHull.size() >=3)
            {
                int end = lowerHull.size()-1;
                if (findDirection(lowerHull.get(end-2), lowerHull.get(end), lowerHull.get(end-1)) <=0)
                {
                    lowerHull.remove(end-1);
                }
                else
                {
                    break;
                }
            }
        }
        
        // Complete upper part of hull.
        upperPoints.add(right);
        upperHull.add(left);
        
        while (upperPoints.size() > 0)
        {
            upperHull.add(upperPoints.remove(0));
            while (upperHull.size() >=3)
            {
                int end = upperHull.size()-1;
                if (findDirection(upperHull.get(end-2), upperHull.get(end), upperHull.get(end-1)) >=0)
                {
                    upperHull.remove(end-1);
                }
                else
                {
                    break;
                }
            }
        }
        
        // Combine the upper and lower hulls.
        ArrayList<PVector> hullPoints = new ArrayList<PVector>(lowerHull);
        for (int i=upperHull.size()-2; i>0; i--)
        {
            hullPoints.add(upperHull.get(i));
        }
       
        return hullPoints;
    }
    
    /** For points p0,p1,p2, this method will return -1 if p2 is to the right of
     *  the line from p0 to p1, 1 if it is to the left, or 0 if all three are co-linear.
     *  @param p1 First point in the triplet.
     *  @param p2 Second point in the triplet.
     *  @param p3 Third point in the triplet.
     *  @return -1 for a right turning line, 1 for a left turning line or 0 for straight line.
     */
    private static int findDirection(PVector p1, PVector p2, PVector p3)
    {
        // Find cross product after translating p2 to the origin.
        float crossProduct = (p1.x-p2.x)*(p3.y-p2.y) - (p3.x-p2.x)*(p1.y-p2.y);
        if (crossProduct < 0)
        {
            return -1;
        }
        if (crossProduct > 0)
        {
            return 1;
        }
        return 0;
    }
    
    
    // ------------------------------ Nested classes -------------------------------
    
    /** Allows points to be sorted by x value.
     */
    private static class XSort implements Comparator<PVector>
    {
        public XSort() 
        {
			// Does nothing but prevents synthetic accessor method from having to be created.
		}

		public int compare(PVector p1, PVector p2)
        {
            int order = Float.compare(p1.x,p2.x);
            
            if (order != 0)
            {
                return order;
            }
            
            // If the x values are equal, sort by y order.
            order = Float.compare(p1.y,p2.y);
            if (order != 0)
            {
                return order;
            }
            
            // If the x and y values are equal, sort by z order.
            order = Float.compare(p1.z,p2.z);
            if (order != 0)
            {
                return order;
            }
            
            // x, y and z values are identical, so compare hash codes to see if they are the same object.
            return Float.compare(p1.hashCode(), p2.hashCode());
        }
    }
    

    // ------------------------ Starter / test application -------------------------
    
    /*
    public static void main(String[] args)
    {
        Vector<PVector> ps = new Vector<PVector>();
        ps.add(new PVector(97,90));
        ps.add(new PVector(27,10));
        ps.add(new PVector(59,8));
        ps.add(new PVector(58,19));
        ps.add(new PVector(85,90));
        ps.add(new PVector(62,91));
        ps.add(new PVector(94,42));
        ps.add(new PVector(84,68));
        
        ps.add(new PVector(16,21));
        ps.add(new PVector(49,14));
        ps.add(new PVector(31,84));
        ps.add(new PVector(40,25));
        ps.add(new PVector(59,95));
        ps.add(new PVector(55,89));
        ps.add(new PVector(81,95));
        ps.add(new PVector(22,46));
        
        ps.add(new PVector(27,80));
        ps.add(new PVector(18,90));
        ps.add(new PVector(59,37));
        ps.add(new PVector(38,45));
            
        ArrayList<PVector> hull = ConvexHull.getHull(ps);
        
        for (PVector p : hull)
        {
            System.out.println("("+p.x+","+p.y+")");
        }
    }
    */
    
}