package org.gicentre.tests;

import junit.framework.TestCase;
import org.gicentre.utils.spatial.Postcode;

//  ****************************************************************************************
/** Set of unit tests for postcode parsing.
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

public class PostcodeTest extends TestCase
{

    private String[] validPostcodes = 
    {
        "LE1 7RH", "Le1   7RH","   le1\t7rh","le17rh", "s10 2Tn", "ec1v 0HB"," e c 1 v 0 h b ",
        "S1", "S     1", "S1 0", "S10", "s10hb", "s1 0hb", "s1 05hb",
        "SW", "s w1", "s w 1 7", "sw 1 7 3", "sw100", "sw1 0 0", "s100", "S 1 0", "S 1 0 3"
    };
    
    private String[] invalidPostcodes = 
    {
            "le1_7rh", "sw 1 7 3 a", null, "", "E% 5HT", "E7 3LRa", "EH24a 7RB", "510 2LR", "S10 a",
            "MK1 7r", "MK17 3r", "MKR 3", "MK  1  73a "
    };
    
    // ---------------------------------- Methods ----------------------------------
    
    /** Called before every test case method.
     */
    protected void setUp()
    {
        // Do nothing for the moment.
    }

    /** Called after every test case method.
     */
    protected void tearDown()
    {
        // Do nothing for the moment.
    }

    // --------------------------------- Test methods -------------------------------
   
    /** Checks that valid postcode text can be parsed correctly. Note that a visual check 
     *  of output should also be made to ensure that apparently valid parsing has resulted
     *  in the correct postcode being generated.
     */
    public void testValidPostcodes()
    {
        System.out.println("Valid postcodes:");
        System.out.println("================");
        
        for (String postcodeText : validPostcodes)
        {
             Postcode pc = new Postcode(postcodeText);
             assertTrue(pc.isValid());
             System.out.println("'"+postcodeText+"' -> '"+pc.toString()+"'");   
        }
        System.out.println();
    }
    
    /** Checks that invalid postcode text is not incorrectly parsed and assumed to be valid.
     *  Examine text output to check that parsing error is correctly identified.
     */
    public void testInvalidPostcodes()
    {
        System.out.println("Invalid postcodes:");
        System.out.println("==================");
        
        for (String postcodeText : invalidPostcodes)
        {
             Postcode pc = new Postcode(postcodeText);
             assertFalse(pc.isValid());
             System.out.println("'"+postcodeText+"' : "+pc.getErrorMessage());   
        }
        System.out.println();
    }
}