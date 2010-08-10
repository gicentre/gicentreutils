package org.gicentre.utils.colour; 

import java.io.*;       // To make class serializable.

//  ****************************************************************************************
/** Colour rule class for storing  a single colour rule. The colour rule consists of two 
 *  indices and associated ARGB colour values. Colours are interpolated for any values that
 *  fall between the lower and upper indices.
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
 
public class ColourRule implements Serializable
{
    // ----------------- Object and class variables -------------------

    // Used to ensure consistency when serializing and deserializing.
    static final long serialVersionUID = -4430404671758693062L;
          
            /** Rule represents part of a continuous colour table.*/
    public static final int CONTINUOUS   = 1;
            /** Rule represents part of a discrete colour table.*/
    public static final int DISCRETE     = 2;

    private int   lColour,uColour;  // 32 bit RGBA encoded integers.
    private float lIndex,uIndex;    // Value associated with colour.
    private int   type;             // Either continuous or discrete.
    
                    // Colour masks.
    private static final int ALF = 255 << 24;
    private static final int RED = 255 << 16;
    private static final int GRN = 255 <<  8;
    private static final int BLU = 255;
    
    // -------------------- Constructors -------------------
    
    /** Creates a categorical colour rule.
      * RGB only (assumes opaque transparency).
      * @param i Index value associated with rule.
      * @param r Red component of colour associated with index.
      * @param g Green component of colour associated with index.
      * @param b Blue component of colour associated with index.
      */
    public ColourRule(float i, int r, int g, int b)
    {
        this(i,(255 << 24) | (r << 16) | (g << 8) | (b)); 
    }

    /** Creates a categorical colour rule.
      * RGBA version.
      * @param i Index value associated with rule.
      * @param r Red component of colour associated with index.
      * @param g Green component of colour associated with index.
      * @param b Blue component of colour associated with index.
      * @param a Alpha (transparency) component associated with index.
      */            
    public ColourRule(float i, int r, int g, int b, int a)
    {
        this(i,(a << 24) | (r << 16) | (g << 8) | (b)); 
    }
    
     /** Creates a categorical colour rule. Combined ARGB version.
      * @param i index value associated with rule.
      * @param c Combined ARGB colour associated with index.
      */
    public ColourRule(float i, int c)
    {   
        lIndex  = i;
        lColour = c;
        uIndex  = i;
        uColour = c;
        type    = DISCRETE;
    }
    
    /** Creates a continuous colour rule. RGB only (assumes opaque transparency).
      * @param i1 Lower index value associated with rule.
      * @param r1 Red component of colour associated with lower index.
      * @param g1 Green component of colour associated with lower index.
      * @param b1 Blue component of colour associated with lower index.
      * @param i2 Upper index value associated with rule.
      * @param r2 Red component of colour associated with upper index.
      * @param g2 Green component of colour associated with upper index.
      * @param b2 Blue component of colour associated with upper index.
      */        
    public ColourRule(float i1, int r1, int g1, int b1,
                  float i2, int r2, int g2, int b2)
    {
        this(i1, (255 << 24) | (r1 << 16) | (g1 << 8) | (b1), 
             i2, (255 << 24) | (r2 << 16) | (g2 << 8) | (b2) ); 
 
    }    
    
    /** Creates a continuous colour rule. RGBA version.
      * @param i1 Lower index value associated with rule.
      * @param r1 Red component of colour associated with lower index.
      * @param g1 Green component of colour associated with lower index.
      * @param b1 Blue component of colour associated with lower index.
      * @param a1 Alpha (transparency) component associated with lower index.
      * @param i2 Upper index value associated with rule.
      * @param r2 Red component of colour associated with upper index.
      * @param g2 Green component of colour associated with upper index.
      * @param b2 Blue component of colour associated with upper index.
      * @param a2 Alpha (transparency) component associated with upper index.
      */
    public ColourRule(float i1, int r1, int g1, int b1, int a1,
                  float i2, int r2, int g2, int b2, int a2)
    {
        this(i1, (a1 << 24) | (r1 << 16) | (g1 << 8) | (b1), 
             i2, (a2 << 24) | (r2 << 16) | (g2 << 8) | (b2) ); 
    }
    
    /** Creates a continuous colour rule. Combined ARGB version.
      * @param i1 Lower index value associated with rule.
      * @param c1 Combined RGBA colour associated with lower index.
      * @param i2 Upper index value associated with rule.
      * @param c2 Combined RGBA colour associated with upper index.
      */  
    public ColourRule(float i1, int c1, float i2, int c2)
    {   
        lIndex  = i1;
        lColour = c1;
        uIndex  = i2;
        uColour = c2;
        type    = CONTINUOUS;
    }
     
    // --------------------- Methods ---------------------
    
    /** Interpolates the colour associated with the given index.
      * @param index Index to associate colour with.
      * @return Interpolated colour as a combined ARGB value.
      */
    public int getColour(float index)
    {
        int   red,green,blue,alpha,
              lred,lgrn,lblu,lalf,
              ured,ugrn,ublu,ualf;
        
        float position;         // Weighted distance between colour rules.
        
        if (index <= lIndex)    // Check for exact matches first.
        {
            return lColour;
        }
        if (index >= uIndex)
        {
            return uColour;
        }
            
        position  = (index - lIndex) / (uIndex - lIndex);
        
        lalf = (lColour & ALF) >> 24;  
        ualf = (uColour & ALF) >> 24;
        
        if (lalf <0)
        {
            lalf += 256;
        }
        if (ualf <0)
        {
            ualf += 256; 
        }
        
        lred = (lColour & RED) >> 16;
        ured = (uColour & RED) >> 16;
  
        lgrn = (lColour & GRN) >>  8;
        ugrn = (uColour & GRN) >>  8;
        
        lblu = lColour & BLU;
        ublu = uColour & BLU;   
                
        alpha = (int)(position*(ualf-lalf) + lalf);
        red   = (int)(position*(ured-lred) + lred);
        green = (int)(position*(ugrn-lgrn) + lgrn);
        blue  = (int)(position*(ublu-lblu) + lblu);

        return ((alpha << 24) | (red << 16) | (green << 8) | blue);
    }
      
    /** Finds the lower index associated with colour rule.
      * @return Lower index value.
      */
    public float getlIndex()
    {
        return lIndex;
    }
    
    /** Sets the index associated with the lower colour.
      * @param lIndex New lower index for colour rule.
      */
    public void setlIndex(float lIndex)
    {
        this.lIndex = lIndex;
    }
    
    /** Finds the upper index associated with colour rule.
      * @return Upper index value.
      */
    public float getuIndex()
    {
        return uIndex;
    }
    
    /** Sets the index associated with the upper colour.
      * @param uIndex New upper index for colour rule.
      */
    public void setuIndex(float uIndex)
    {
        this.uIndex = uIndex;
    }
        
    /** Reports the type of colour rule. Discrete rules take precedence
      *  over continuous rules.
      * @return Type of colour rule. Either DISCRETE or CONTINUOUS. 
      */
    public int getType()
    {
        return type;
    }
    
    /** Sets the rule type (either DISCRETE or CONTINUOUS).
      * @param type Type of colour rule. Either DISCRETE or CONTINUOUS.
      */
    public void setType(int type)
    {
        this.type = type;
    }
    
    /** Finds the colour associated with the lower index.
      * @return Lower colour value (combined ARGB).
      */
    public int getlColour()
    {
        return lColour;
    }
    
    /** Sets the colour associated with the lower index of the rule.
      * @param lColour New colour for lower index (combined ARGB).
      */
    public void setlColour(int lColour)
    {
        this.lColour = lColour;
    }
    
    /** Sets the colour associated with the upper index of the rule.
      * @param uColour New colour for upper index (combined ARGB).
      */
    public void setuColour(int uColour)
    {
        this.uColour = uColour;
    }  
    
    /** Finds the colour associated with the upper index.
      * @return Upper colour value (combined ARGB).
      */
    public int getuColour()
    {
        return uColour;
    } 
    
    /** Reports the current colour rule.
      * @return String containing current colour table.
      */
    public String toString()
    {
        StringBuffer output = new StringBuffer();
                 
        output.append(lIndex + ": " + toString(lColour)+ " -> ");
        output.append(uIndex + ": " + toString(uColour));
    
        if (type == ColourRule.DISCRETE)
        {
            output.append(" (D)\n");
        }
        else
        {
            output.append("\n");
        }
    
        return output.toString();
    } 
    
    /** Converts a given colour value from integer to R,G,B,A string
      * for display and storage.
      * @param intColour Colour represented as integer.
      * @return String representation of the colour.
      */
    public static String toString(int intColour)
    {
        int alpha = (intColour & ALF) >> 24;
        int red   = (intColour & RED) >> 16;
        int green = (intColour & GRN) >>  8;
        int blue  =  intColour & BLU; 
        
        // Account for signed integers.
        if (alpha < 0)
        {
            alpha += 256;
        }
        
        return new String(red + "," + green + ","+ blue + "," + alpha);       
    }
}