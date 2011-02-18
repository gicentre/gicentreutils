package org.gicentre.utils.spatial;

// *****************************************************************************************
/** Enumerated list of direction constants. Useful for specifying rook's-case and
 *  queen's-case neighbour relations.
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

public enum Direction
{
    /** Identifies a northerly direction. */        NORTH,
    /** Identifies a north-easterly direction. */   NORTH_EAST,
    /** Identifies a easterly direction. */         EAST,
    /** Identifies a south easterly direction. */   SOUTH_EAST,
    /** Identifies a southerly direction. */        SOUTH,
    /** Identifies a south-westerly direction. */   SOUTH_WEST,
    /** Identifies a westerly direction. */         WEST,
    /** Identifies a north-westerly direction. */   NORTH_WEST,
    /** Identifies a central position. */           CENTRE;

    // ---------------------------------- Methods ----------------------------------

    /** Provides a textual representation of the list of direction options.
     *  @return Text representing the direction type.
     */
    public String toString()
    {
        switch(this)
        {
            case NORTH:
                return "north";
            case NORTH_EAST:
                return "north-east";
            case EAST:
                return "east";
            case SOUTH_EAST:
                return "south-east";
            case SOUTH:
                return "south";
            case SOUTH_WEST:
                return "south-west";
            case WEST:
                return "west";
            case NORTH_WEST:
                return "north-west";
            case CENTRE:
                return "centre";
        }
        // We shouldn't ever get to this line.
        return super.toString();
    }
}