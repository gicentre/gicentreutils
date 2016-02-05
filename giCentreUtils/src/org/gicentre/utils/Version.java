package org.gicentre.utils;

// *****************************************************************************************
/** Stores version information about the giCentre utilities package.
  * @author Jo Wood, giCentre, City University London.
  * @version 3.4, 5th February, 2014.
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

public class Version 
{
    private static final float  VERSION = 3.4f;
    private static final String VERSION_TEXT = "giCentre utils V3.4, 5th February, 2016";

    /** Reports the current version of the giCentre utilities package.
      * @return Text describing the current version of this package.
      */
    public static String getText()
    {
        return VERSION_TEXT;
    }
    
    /** Reports the numeric version of the giCentre utilities package.
     *  @return Number representing the current version of this package.
     */
    public static float getVersion()
    {
        return VERSION;
    }
}