package org.gicentre.tests;

import java.awt.Color;

import org.gicentre.utils.colour.CIELab;
import org.gicentre.utils.colour.ColourConverter.WhitePoint;

import processing.core.PVector;

//  *****************************************************************************************
/** Tests the conversion to and from CIELab colour space.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.2, 1st August, 2011.. 
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

public class CIELabTest
{

    /** Starts the test as an application.
     *  @param args Command line arguments (ignored).
     */
    public static void main(String[] args)
    {
        CIELab converter = new CIELab(WhitePoint.D50);
        System.err.println(converter.getColour(50, -100, 0,true));
        
        // Try round trip conversion to check both forward and inverse conversions are consistent.
        
        for (int i=0; i<100; i++)
        {
            Color colour1 = new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
            PVector colour1Lab = converter.getLab(colour1);
            Color colour2 = converter.getColour(colour1Lab.z, colour1Lab.x, colour1Lab.y,false);
        
            if (colour1.equals(colour2))
            {
                System.err.println(colour1.getRed()+","+colour1.getGreen()+","+colour1.getBlue()+" converted without problems");
            }
            else
            {
                System.err.println(colour1+ " goes to "+colour1Lab+" which comes back as "+colour2);
            }
        }
    }
}
