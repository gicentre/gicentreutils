package org.gicentre.utils.colour;

// *****************************************************************************************
/** Interface for any class that wishes to be informed when a colour has been chosen from 
 *  the colour picker.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.1, 18th February, 2011. 
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

public interface PickerListener
{
    /** Should respond to a colour being chosen by the colour picker.
     */
    public abstract void colourChosen(); 
}
