package org.gicentre.tests;

import java.awt.Color;

import org.gicentre.utils.colour.CIELuv;
import org.gicentre.utils.colour.ColourTable;

import processing.core.PVector;

//  ****************************************************************************************
/** Tests the conversion to and from CIELuv colour space.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.3, 1st August, 2011. 
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

public class CIELuvTest
{

    /** Starts the CIELuv test as an application.
     *  @param args Command line arguments (ignored).
     */
    public static void main(String[] args)
    {
        CIELuv converter = new CIELuv();
        
        double hue=0;
        double hue2 = 90;
        //Color col = converter.getColourFromLCh(80, 200, hue,true);
        
        double[] msc = converter.getMostSaturatedColour(280);
        System.err.println("Most saturated colour for h=280 is "+msc[0]+","+msc[1]+","+msc[2]);
        //if (true) return;

        //System.err.println("Hue of "+hue+" goes to "+col);
        
        //ColourTable cTable = converter.getSequential(hue, 9);
        
        System.err.println("Initial hue is "+hue);
        ColourTable cTable = converter.getSequential(hue, hue2,0.6,0.75, 0);
        //ColourTable cTable = converter.getSequential(60, 340,0.6,0.75, 0);
        ColourTable.writeFile(cTable,"temp.ctb");
        
        // Try round trip conversion to check both forward and inverse conversions are consistent.
        
        for (int i=0; i<100; i++)
        {
            Color colour1 = new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
            PVector colour1Luv = converter.getLuv(colour1);
            Color colour2 = converter.getColour(colour1Luv.z, colour1Luv.x, colour1Luv.y,false);
        
            if (colour1.equals(colour2))
            {
                System.err.println(colour1.getRed()+","+colour1.getGreen()+","+colour1.getBlue()+" converted to and from "+colour1Luv.z+","+colour1Luv.x+","+colour1Luv.y+" without problems");
            }
            else
            {
                System.err.println(colour1+ " goes to "+colour1Luv+" which comes back as "+colour2);
            }
        }
        
        
        /* for building the MSC lookups
        for (int i=0; i<360; i++)
        {
            double[] rgb = converter.getMostSaturatedColour(i);
            System.out.println(rgb[0]+","+rgb[1]+","+rgb[2]);
        }
        */
        
        //double[] rgb = converter.getMostSaturatedColour(180);
        //System.out.println(rgb[0]+","+rgb[1]+","+rgb[2]);
        
    }   
}
