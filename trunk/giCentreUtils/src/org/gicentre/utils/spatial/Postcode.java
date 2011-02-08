package org.gicentre.utils.spatial;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//  ****************************************************************************************
/** Class for parsing postcodes. Allows areas, districts, sectors and units to be
 *  identified individually. Will parse partial postcodes as well as those containing
 *  arbitrary whitespace.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.1, 8th February, 2011. 
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

public class Postcode
{    
    // ----------------------------- Object variables ------------------------------
    
    private String pcArea, pcDistrict, pcSector, pcUnit;
    private boolean isValid;
    private String errorMessage;
    private int pos,endPos;      // Keeps track of character position when parsing postcode.
    private static final Pattern NON_ALPHA_NUM = Pattern.compile( "[^0-9A-Z ]" );
    
    // -------------------------------- Constructor --------------------------------
    
    /** Stores the given postcode and validates it.
     *  @param postcode Postcode to validate.  
     */    
    public Postcode(String postcode)
    {
        isValid = validate(postcode);
    }

    // ---------------------------------- Methods ----------------------------------

    /** Reports whether the stored postcode is valid. If it is, a separate area, 
     *  district, sector and unit can be extracted.
     *  @return True if postcode is valid.
     */
    public boolean isValid()
    {
        return isValid;
    }
    
    /** Reports the postcode area or null if the postcode is invalid. 
     *  For postcode LE1 7RH, the postcode area is LE.
     *  @return Postcode area, or null if postcode is invalid.
     */
    public String getArea()
    {
        if (isValid)
        {
            return pcArea;
        }
        return null;
    }
    
    /** Reports the part of the postcode that represents the postcode area or null if the postcode is invalid. 
     *  For postcode LE1 7RH, the postcode area code is LE.
     *  @return Postcode area code, or null if postcode is invalid.
     */
    public String getAreaCode()
    {
        return getArea();
    }
    
    /** Reports the postcode district or null if the postcode is invalid. If the postcode 
     *  is valid, but no district has been defined, this returns as much of the postcode as is defined.
     *  For postcode LE1 7RH, the postcode district is LE1.
     *  @return Postcode district, or empty string if not defined or null if postcode is invalid.
     */
    public String getDistrict()
    {
        if (isValid)
        {
            return pcArea+pcDistrict;
        }
        return null;
    }
    
    /** Reports the part of the postcode that represents the postcode district or null if the postcode is invalid. 
     *  For postcode LE1 7RH, the postcode district code is 1.
     *  @return Postcode district code, or null if postcode is invalid.
     */
    public String getDistrictCode()
    {
        if (isValid)
        {
            return pcDistrict;
        }
        return null;
    }
    
    /** Reports the postcode sector or null if the postcode is invalid. If the postcode 
     *  is valid, but no sector has been defined, this returns an empty string.
     *  For postcode LE1 7RH, the postcode sector is LE1 7.
     *  @return Postcode sector, or empty string if not defined or null if postcode is invalid.
     */
    public String getSector()
    {
        if (isValid)
        {
            return pcArea+pcDistrict+" "+pcSector;
        }
        return null;
    }
    
    /** Reports the part of the postcode that represents the postcode sector or null if the postcode is invalid. 
     *  For postcode LE1 7RH, the postcode sector code is 7.
     *  @return Postcode sector code, or null if postcode is invalid.
     */
    public String getSectorCode()
    {
        if (isValid)
        {
            return pcSector;
        }
        return null;
    }
    
    /** Reports the postcode unit or null if the postcode is invalid. If the postcode 
     *  is valid, but no unit has been defined, this returns an empty string.
     *  For postcode LE1 7RH, the postcode unit is LE1 7RH.
     *  @return Postcode unit, or empty string if not defined or null if postcode is invalid.
     */
    public String getUnit()
    {
        if (isValid)
        {
            if (pcSector.length() > 0)
            {
                return pcArea+pcDistrict+" "+pcSector+pcUnit;
            }
            return pcArea+pcDistrict;
        }
        return null;
    }
    
    /** Reports a seven digit unit postcode  or null if the postcode is invalid. If there are
     *  fewer than seven digits, spaces are inserted between the district and sector. This
     *  can be used for comparison with CodePoint seven digit postcode records.
     *  For postcode LE1 7RH, the seven digit unit postcode LE1 7RH, for S1 2TN the seven digit
     *  unit postcode is S1  2TN, for EC1V 0HB the seven digit unit postcode is EC1V0HB.
     *  @return Seven digit unit postcode, or null if postcode is invalid.
     */
    public String getUnit7()
    {
        if (isValid)
        {
            if (pcSector.length() > 0)
            {
                int numSpaces = 7-(pcArea.length()+pcDistrict.length()+pcSector.length()+pcUnit.length());
                String spaces = "       ";
                return pcArea+pcDistrict+spaces.substring(0,numSpaces)+pcSector+pcUnit;
            }
            return pcArea+pcDistrict;
        }
        return null;
    }
    
    /** Reports the part of the postcode that represents the postcode unit or null if the postcode is invalid. 
     *  For postcode LE1 7RH, the postcode unit code is RH.
     *  @return Postcode unit code, or null if postcode is invalid.
     */
    public String getUnitCode()
    {
        if (isValid)
        {
            return pcUnit;
        }
        return null;
    }
        
    /** Reports any error messages associated with an attempt to parse the postcode text. If the
     *  postcode is valid, this will always be an empty string.
     *  @return Error message associated with parsing the postcode or empty string if postcode is valid.
     */
    public String getErrorMessage()
    {
        return errorMessage.toString();
    }
    
    /** Provides a string representation of the postcode. If valid, this will be all uppercase
     *  with a single space separating the incode (e.g. LE1) from the outcode (7RH). Note the
     *  postcode need not be at the unit level, so, for example S, S10 and S10 3 are all valid.
     *  If invalid an empty string is returned.
     *  @return Text representing the postcode or empty string if no valid postcode represented.
     */
    public String toString()
    {
        if (isValid)
        {
            return getUnit();
        }
        return "";
    }
    
    // ------------------------------ Private methods ------------------------------
    
    /** Attempts to validate the postcode stored in this object. If successful, separate
     *  area, district, sector and unit can be extracted.
     *  @param postcode Postcode text to validate.
     */
    private boolean validate(String postcode)
    {
        errorMessage = "";
        pcDistrict = "";
        pcSector = "";
        pcUnit = "";
        pos = 0;
                          
        if (postcode == null)
        {
            errorMessage = "No postcode provided.";
            return false;
        }
        
        String pcUpperCase = postcode.trim().toUpperCase();
        if (pcUpperCase.length() == 0)
        {
            errorMessage = "Empty postcode text provided.";
            return false;
        }
        
        // Remove any duplicate whitespace characters and standardise with single space.
        pcUpperCase = pcUpperCase.replaceAll("\\s+", " ");
        String pcNoSpaces = pcUpperCase.replaceAll(" ", "");
        
        endPos = pcUpperCase.length()-1;
        
        // Check we have only valid characters (A-Z, 0-9 and space)
        Matcher matcher = NON_ALPHA_NUM.matcher(pcUpperCase);
        
        if (matcher.find())
        {
            errorMessage = "Postcode contains unknown character '"+pcUpperCase.substring(matcher.start(),matcher.start()+1)+"'.";
            return false;
        }
        
        // If we have more than 8 characters, postcode cannot be valid
        if (pcNoSpaces.length() > 7)
        {
            errorMessage = "Postcode contains too many characters.";
            return false;
        }
                
        // Possible full postcode formats are as follows where L=letter, D=digit:
        // LD DLL, LDD DLL, LDL DLL, LLD DLL, LLDD DLL, LLDL DLL
      
        // Grab the unit first if it exists.
        if (pcNoSpaces.length() >= 5)
        {
            char unit2 = getLastCharacter(pcUpperCase);
            char unit1 = getLastCharacter(pcUpperCase);
            
            if ((isLetter(unit1)) && (isLetter(unit2)))
            {
                // We have a unit postcode and therefore should have the sector.
                pcUnit = Character.toString(unit1)+Character.toString(unit2);
                
                char sector = getLastCharacter(pcUpperCase);
                if (isNumber(sector))
                {
                    pcSector = Character.toString(sector);
                }
                else
                {
                    errorMessage = "Postcode sector appears to be a letter '"+sector+"'.";
                    return false;
                }
                
                // We can strip the last three characters from the remains of the string to process
                pcUpperCase = pcUpperCase.substring(0,endPos+1).trim();
            }
        }
        
        // Area must start with either 1 letter or 2 letters.: LD, LDD, LDL, LLD, LLDD, LLDL
       
        // First character (Area) must be a letter to be valid.
        char nextChar = getNextCharacter(pcUpperCase); 
        if (isLetter(nextChar))
        {
            pcArea = Character.toString(nextChar);
        }
        else
        {
            errorMessage = "Postcode area does not start with a letter.";
            return false;
        }
        
        // Second character could be empty, a letter or a number.
        nextChar = getNextCharacter(pcUpperCase);
        if (nextChar == Character.MIN_VALUE)
        {
            // postcode is a single area letter with nothing else defined.
            return true;
        }
        
        if (isLetter(nextChar))
        {
            // Must be second and final letter of area
            pcArea = pcArea + Character.toString(nextChar);
        }
        else if (isNumber(nextChar))
        {
            // Must be first digit of district.
            pcDistrict = Character.toString(nextChar);
        }
        
        // If we have a space between the second and third characters, and we already have a 
        // district value, the third must be the sector if it is the final character and numeric,
        // or the second digit of the district if it is a letter
        if ((pcDistrict.length() > 0) && (pcUpperCase.length() > 3) && (pcUpperCase.charAt(pos)==' ') && (pcSector.length()==0))
        {
            nextChar = getNextCharacter(pcUpperCase);
            char finalChar = getNextCharacter(pcUpperCase);
            
            if (finalChar == Character.MIN_VALUE)
            {
                if (isNumber(nextChar))
                {
                    pcSector = Character.toString(nextChar);
                    return true;
                }
                
                // Must be the second character in the district (non-numeric)
                //pcDistrict = pcDistrict + Character.toString(nextChar);
                
                //errorMessage = "Postcode sector contains unexpected letter '"+nextChar+"'.";
                //return false;
            }
            
            if (isNumber(nextChar))
            {
            	pcDistrict = pcDistrict+Character.toString(nextChar);
            }
            else
            {
            	errorMessage = "Last part of postcode district or postcode sector contains unexpected letter '"+nextChar+"'.";
                return false;
            }
            
            if (isNumber(finalChar))
            {
                pcSector = Character.toString(finalChar);
                
                // Check we haven't got a single final character
                finalChar = getNextCharacter(pcUpperCase);
                if (finalChar != Character.MIN_VALUE)
                {
                    errorMessage = "Postcode unit contains only one character '"+finalChar+"'.";
                    return false; 
                }
                return true;
            }
        }
        
        // Third character could be a number or letter, either first (number) or second (number or letter) of district.
        nextChar = getNextCharacter(pcUpperCase);
        if (nextChar == Character.MIN_VALUE)
        {
            // Nothing else defined in postcode
            return true;
        }
               
        if ((isLetter(nextChar)) && (pcDistrict.length()==0))
        {
        	 errorMessage = "Postcode district should start with a number but starts with unexpected character '"+nextChar+"'.";
             return false;
        }            
       
        pcDistrict = pcDistrict+Character.toString(nextChar);
        
        // If we have a space between the third and fourth characters, the fourth must be the sector if it is
        // the final character, or the second digit of the district if it is not the final one.
        if ((pcUpperCase.length() > 4) && (pcUpperCase.charAt(pos)==' ') && (pcSector.length()==0))
        {
            nextChar = getNextCharacter(pcUpperCase);
            char finalChar = getNextCharacter(pcUpperCase);
            
            if (finalChar == Character.MIN_VALUE)
            {
                if (isNumber(nextChar))
                {
                    pcSector = Character.toString(nextChar);
                    
                    // Check we haven't got a single final character
                    finalChar = getNextCharacter(pcUpperCase);
                    if (finalChar != Character.MIN_VALUE)
                    {
                        errorMessage = "Postcode unit contains only one character '"+finalChar+"'.";
                        return false; 
                    }
                    
                    return true;
                }
            
                errorMessage = "Postcode sector contains unexpected letter '"+nextChar+"'.";
                return false;
            }
            
            pcDistrict = pcDistrict+Character.toString(nextChar);
            if (isNumber(finalChar))
            {
                pcSector = Character.toString(finalChar);
                
                // Check we haven't got a single final character
                finalChar = getNextCharacter(pcUpperCase);
                if (finalChar != Character.MIN_VALUE)
                {
                    errorMessage = "Postcode unit contains only one character '"+finalChar+"'.";
                    return false; 
                }
                return true;
            }
        
            errorMessage = "Postcode sector contains unexpected letter '"+finalChar+"'.";
            return false;
        }
        
        // If there is a fourth character, it must be the second character of the district or the sector.
        nextChar = getNextCharacter(pcUpperCase);
        if (nextChar == Character.MIN_VALUE)
        {
            // Nothing else defined in postcode
            return true;
        }
        if (pcDistrict.length() == 2)
        {
            if (isNumber(nextChar))
            {
                pcSector = Character.toString(nextChar);
                return true;
            }
            
            errorMessage = "Postcode sector contains unexpected letter '"+nextChar+"'.";
            return false;
        }
        
        pcDistrict = pcDistrict+Character.toString(nextChar);
        
        // If we have anything left, it can only be the sector.
        nextChar = getNextCharacter(pcUpperCase);
        if (nextChar != Character.MIN_VALUE)
        {
            if (isNumber(nextChar))
            {
                pcSector = Character.toString(nextChar);
                
                // Check we haven't got a single final character
                char finalChar = getNextCharacter(pcUpperCase);
                if (finalChar != Character.MIN_VALUE)
                {
                    errorMessage = "Postcode unit contains only one character '"+finalChar+"'.";
                    return false; 
                }
                return true;
            }
        
            errorMessage = "Postcode sector contains unexpected letter '"+nextChar+"'.";
            return false;
        }
        
        return true;
    }
    
    /** Gets the next character in the given text, skipping spaces if they exist.
     *  Assumes text has already been processed so that only single internal spaces can exist.
     *  @param text Text to parse.
     *  @return Next character or Character.MIN_VALUE if no more characters.
     */
    private char getNextCharacter(String text)
    {
        if (pos >= text.length())
        {
            return Character.MIN_VALUE;
        }
        
        if (text.charAt(pos) == ' ')
        {
            pos++;
        }
        
        return text.charAt(pos++);
    }
    
   /** Gets the last character in the given text, skipping spaces if they exist.
    *  Assumes text has already been processed so that only single internal spaces can exist.
    *  @param text Text to parse.
    *  @return Last character or Character.MIN_VALUE if we have reached the start.
    */
   private char getLastCharacter(String text)
   {
       if (endPos < 0)
       {
           return Character.MIN_VALUE;
       }
       
       if (text.charAt(endPos) == ' ')
       {
           endPos--;
       }
       
       return text.charAt(endPos--);
   }
    
    /** Reports whether the given character is a letter or not. Assumes text will be uppercase.
     *  @param character Character to test.
     *  @return True if character is a letter.
     */
    private boolean isLetter(char character)
    {
        if ((character >= 'A') && (character <='Z'))
        {
            return true;
        }
        return false;
    }
    
    /** Reports whether the given character is a letter or not. Assumes text will be uppercase.
     *  @param character Character to test.
     *  @return True if character is a number.
     */
    private boolean isNumber(char character)
    {
        if ((character >= '0') && (character <='9'))
        {
            return true;
        }
        return false;
    }
}