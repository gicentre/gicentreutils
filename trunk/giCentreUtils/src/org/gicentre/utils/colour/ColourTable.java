package org.gicentre.utils.colour;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import org.gicentre.utils.io.DOMProcessor;

//  ****************************************************************************************
/** Colour table class for storing colour rules associated with a spatial  object. A colour 
 *  table can be a set of rules, a 'raw' colour table where each attribute value is a 32 bit
 *  colour code (mostly used used  for image files), or an 'attribute' table where each 
 *  attribute is stored in the form of a colour. Most common are colour rules where each
 *  rule consists of two indices and associated RGBA colour values. Colours are interpolated
 *  for any values that fall between the lower and upper indices. Also contains static 
 *  methods for creating preset colour tables such as ColorBrewer colour schemes and Imhof
 *  relief colour schemes.
 *  <br /><br />
 *  ColorBrewer specifications and designs developed by Cynthia Brewer 
 *  (<a href="http://colorbrewer.org/" target="_blank">colorbrewer.org</a>).
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.1.1, 23rd March, 2011.
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

public class ColourTable implements Serializable
{
    // ---------------------------- Class and object Variables ----------------------------
    
                    /** Indicates the value of the spatial model is undefined. */
    public static final float NO_VALUE = Float.MIN_VALUE;
    
                    /** Undefined feature.      */
    public static final int UNDEFINED = 0;
    
                    /** Pit feature.            */
    public static final int PIT     = 1;
                    /** Channel feature.        */
    public static final int CHANNEL = 2;
                    /** Pass (saddle) feature.  */
    public static final int PASS    = 3;
                    /** Ridge feature.          */
    public static final int RIDGE   = 4;
                    /** Peak feature.           */
    public static final int PEAK    = 5;
                    /** Planar (no) feature.    */
    public static final int PLANAR  = 6;
                    /** 'Universal pit' (e.g. sea).*/
    public static final int UPIT    = 11;
                    /** 'Universal peak'        */
    public static final int UPEAK    = 15;
    
    // If the values of these constants are changed, there may be
    // some incompatibilities with older versions of LandSerf.
    // In which case, the  methods of ConvertOldLandSerf should be changed.
    
            /** Colour table uses colour rules. */
    public static final int COLOUR_RULES = 1;
            /** Colour table uses raster values.*/
    public static final int COLOUR_RAW   = 2;
            /** Uses integer coding of the object's (floating point) attribute. 
              * Not for display, this table allows graphical buffers to preserve
              * attributes of any object displayed using this colour scheme. */
    public static final int COLOUR_ATTRIB = 3;
        
    // Preset colour table IDs.
    
            /** Indicates a default (green-purple-white) colour table. */
    public static final int DEFAULT = 100;
    
            /** Indicates a feature class (black, blue, green, yellow, red ) colour table. */
    public static final int FEATURES = 101;
            /** Indicates a feature class (black, blue, green, yellow, red ) colour table. */
    public static final int MSN_FEATURES = 102;
            /** Indicates a slope (white - yellow - black) colour table. */
    public static final int SLOPE = 103;
            /** Indicates a (red-blue) aspect colour table. */
    public static final int ASPECT = 104; 
    
            /** Diverging blue to red colour table. Used for ProfC surfaces. */
    public static final int DIVERGING_BLURED = 111;
            /** Diverging blue to red colour table. Used for ProfC surfaces. */
    public static final int DIVERGING_GRNYEL = 112;
            /** Diverging blue through yellow to to red table. Based on Color Brewer's 'RdYlBu' diverging colour scheme. */
    public static final int DIVERGING_BLUYELRED = 113;
    
            /** Greyscale (black to white) colour table. */
    public static final int GREYSCALE = 121;
            /** Inverted greyscale (white to black) colour table. */
    public static final int INV_GREYSCALE = 122;
            
            /** Imhof land surface colour scheme (green to beige). */
    public static final int IMHOF_L1 = 131;
            /** Imhof land surface colour scheme (green to brown). */
    public static final int IMHOF_L2 = 132;
            /** Imhof land surface colour scheme (green to orange). */
    public static final int IMHOF_L3 = 133;
            /** Imhof land surface colour scheme (green to white). */
    public static final int IMHOF_L4 = 134;
            /** Imhof sea colour scheme (blue to white). */
    public static final int IMHOF_S1 = 135;
            /** Imhof sea colour scheme (dark blue to white). */
    public static final int IMHOF_S2 = 136;
            /** Imhof sea and land colour scheme (dark blue to brown). */
    public static final int IMHOF_SL = 137;
    
            /** Exponential orange to red colour table. Based on Color Brewer's 'OrRd' nine-sequence colour scheme. */
    public static final int EXP_ORRED = 141;
    
            /** Random colour scheme for nominal data. */
    public static final int RANDOM = 151;
    
            /** ColorBrewer sequential yellow-green scheme. */          
    public static final int YL_GN = 201;
            /** ColorBrewer sequential yellow-green-blue scheme. */     
    public static final int YL_GN_BU = 202;
            /** ColorBrewer sequential green-blue scheme. */            
    public static final int GN_BU = 203;
            /** ColorBrewer sequential blue-green scheme. */            
    public static final int BU_GN = 204;
            /** ColorBrewer sequential purple-blue-green scheme. */     
    public static final int PU_BU_GN = 205;
            /** ColorBrewer sequential purple-blue scheme. */           
    public static final int PU_BU = 206;
            /** ColorBrewer sequential blue-purple scheme. */           
    public static final int BU_PU = 207;
            /** ColorBrewer sequential red-purple scheme. */            
    public static final int RD_PU = 208;
            /** ColorBrewer sequential purple-red scheme. */            
    public static final int PU_RD = 209;
            /** ColorBrewer sequential orange-red scheme. */            
    public static final int OR_RD = 210;
            /** ColorBrewer sequential yellow-orange-red scheme. */     
    public static final int YL_OR_RD = 211;
            /** ColorBrewer sequential yellow-orange-brown scheme. */   
    public static final int YL_OR_BR = 212;
            /** ColorBrewer sequential monochrome purple scheme. */     
    public static final int PURPLES = 213;
            /** ColorBrewer sequential monochrome blue scheme. */       
    public static final int BLUES = 214;
            /** ColorBrewer sequential monochrome green scheme. */      
    public static final int GREENS = 215;
            /** ColorBrewer sequential monochrome orange scheme. */     
    public static final int ORANGES = 216;
            /** ColorBrewer sequential monochrome red scheme. */        
    public static final int REDS = 217;
            /** ColorBrewer sequential monochrome grey scheme. */       
    public static final int GREYS = 218;
            /** ColorBrewer diverging purple-orange scheme. */          
    public static final int PU_OR = 219;
            /** ColorBrewer diverging brown-blue-green scheme. */       
    public static final int BR_B_G = 220;
            /** ColorBrewer diverging purple-red-green scheme. */       
    public static final int P_R_GN = 221;
            /** ColorBrewer diverging pink-yellow-green scheme. */      
    public static final int PI_Y_G = 222;
            /** ColorBrewer diverging red-blue scheme. */               
    public static final int RD_BU = 223;
            /** ColorBrewer diverging red-grey scheme. */               
    public static final int RD_GY = 224;
            /** ColorBrewer diverging red-yellow-blue scheme. */        
    public static final int RD_YL_BU = 225;
            /** ColorBrewer diverging spectral scheme. */               
    public static final int SPECTRAL = 226;
            /** ColorBrewer diverging red-yellow-green scheme. */       
    public static final int RD_YL_GN = 227;
            /** ColorBrewer qualitative categorical scheme with 3 elements. */          
    public static final int SET1_3 = 230;
            /** ColorBrewer qualitative categorical scheme with 4 elements. */          
    public static final int SET1_4 = 231;
            /** ColorBrewer qualitative categorical scheme with 5 elements. */          
    public static final int SET1_5 = 232;
            /** ColorBrewer qualitative categorical scheme with 6 elements. */          
    public static final int SET1_6 = 233;
            /** ColorBrewer qualitative categorical scheme with 7 elements. */          
    public static final int SET1_7 = 234;
            /** ColorBrewer qualitative categorical scheme with 8 elements. */          
    public static final int SET1_8 = 235;
            /** ColorBrewer qualitative categorical scheme with 9 elements. */          
    public static final int SET1_9 = 236;
            /** ColorBrewer qualitative categorical scheme with 3 elements. */          
    public static final int SET2_3 = 237;
            /** ColorBrewer qualitative categorical scheme with 4 elements. */          
    public static final int SET2_4 = 238;
            /** ColorBrewer qualitative categorical scheme with 5 elements. */          
    public static final int SET2_5 = 239;
            /** ColorBrewer qualitative categorical scheme with 6 elements. */          
    public static final int SET2_6 = 240;
            /** ColorBrewer qualitative categorical scheme with 7 elements. */          
    public static final int SET2_7 = 241;
            /** ColorBrewer qualitative categorical scheme with 8 elements. */          
    public static final int SET2_8 = 242;
            /** ColorBrewer qualitative categorical scheme with 3 elements. */          
    public static final int SET3_3 = 243;
            /** ColorBrewer qualitative categorical scheme with 4 elements. */          
    public static final int SET3_4 = 244;
            /** ColorBrewer qualitative categorical scheme with 5 elements. */          
    public static final int SET3_5 = 245;
            /** ColorBrewer qualitative categorical scheme with 6 elements. */          
    public static final int SET3_6 = 246;
            /** ColorBrewer qualitative categorical scheme with 7 elements. */          
    public static final int SET3_7 = 247;
            /** ColorBrewer qualitative categorical scheme with 8 elements. */          
    public static final int SET3_8 = 248;
            /** ColorBrewer qualitative categorical scheme with 9 elements. */          
    public static final int SET3_9 = 249;
            /** ColorBrewer qualitative categorical scheme with 10 elements. */         
    public static final int SET3_10 = 250;
            /** ColorBrewer qualitative categorical scheme with 11 elements. */         
    public static final int SET3_11 = 251;
            /** ColorBrewer qualitative categorical scheme with 12 elements. */         
    public static final int SET3_12 = 252;
            /** ColorBrewer qualitative categorical pastel scheme with 3 elements. */   
    public static final int PASTEL1_3 = 253;
            /** ColorBrewer qualitative categorical pastel scheme with 4 elements. */   
    public static final int PASTEL1_4 = 254;
            /** ColorBrewer qualitative categorical pastel scheme with 5 elements. */   
    public static final int PASTEL1_5 = 255;
            /** ColorBrewer qualitative categorical pastel scheme with 6 elements. */
    public static final int PASTEL1_6 = 256;
            /** ColorBrewer qualitative categorical pastel scheme with 7 elements. */
    public static final int PASTEL1_7 = 257;
            /** ColorBrewer qualitative categorical pastel scheme with 8 elements. */
    public static final int PASTEL1_8 = 258;
            /** ColorBrewer qualitative categorical pastel scheme with 9 elements. */   
    public static final int PASTEL1_9 = 259;
            /** ColorBrewer qualitative categorical pastel scheme with 3 elements. */   
    public static final int PASTEL2_3 = 260;
            /** ColorBrewer qualitative categorical pastel scheme with 4 elements. */   
    public static final int PASTEL2_4 = 261;
            /** ColorBrewer qualitative categorical pastel scheme with 5 elements. */   
    public static final int PASTEL2_5 = 262;
            /** ColorBrewer qualitative categorical pastel scheme with 6 elements. */
    public static final int PASTEL2_6 = 263;
            /** ColorBrewer qualitative categorical pastel scheme with 7 elements. */   
    public static final int PASTEL2_7 = 264;
            /** ColorBrewer qualitative categorical pastel scheme with 8 elements. */
    public static final int PASTEL2_8 = 265;
            /** ColorBrewer qualitative categorical dark scheme with 3 elements. */
    public static final int DARK2_3 = 266;
            /** ColorBrewer qualitative categorical dark scheme with 4 elements. */     
    public static final int DARK2_4 = 267;
            /** ColorBrewer qualitative categorical dark scheme with 5 elements. */     
    public static final int DARK2_5 = 268;
            /** ColorBrewer qualitative categorical dark scheme with 6 elements. */     
    public static final int DARK2_6 = 269;
            /** ColorBrewer qualitative categorical dark scheme with 7 elements. */     
    public static final int DARK2_7 = 270;
            /** ColorBrewer qualitative categorical dark scheme with 8 elements. */     
    public static final int DARK2_8 = 271;
            /** ColorBrewer qualitative categorical paired scheme with 3 elements. */   
    public static final int PAIRED_3 = 272;
            /** ColorBrewer qualitative categorical paired scheme with 4 elements. */   
    public static final int PAIRED_4 = 273;
            /** ColorBrewer qualitative categorical paired scheme with 5 elements. */
    public static final int PAIRED_5 = 274;
            /** ColorBrewer qualitative categorical paired scheme with 6 elements. */
    public static final int PAIRED_6 = 275;
            /** ColorBrewer qualitative categorical paired scheme with 7 elements. */
    public static final int PAIRED_7 = 276;
            /** ColorBrewer qualitative categorical paired scheme with 8 elements. */   
    public static final int PAIRED_8 = 277;
            /** ColorBrewer qualitative categorical paired scheme with 9 elements. */
    public static final int PAIRED_9 = 278;
            /** ColorBrewer qualitative categorical paired scheme with 10 elements. */
    public static final int PAIRED_10 = 279;
            /** ColorBrewer qualitative categorical paired scheme with 11 elements. */
    public static final int PAIRED_11 = 280;
            /** ColorBrewer qualitative categorical paired scheme with 12 elements. */
    public static final int PAIRED_12 = 281;
            /** ColorBrewer qualitative categorical accented scheme with 3 elements. */
    public static final int ACCENT_3 = 282;
            /** ColorBrewer qualitative categorical accented scheme with 4 elements. */
    public static final int ACCENT_4 = 283;
            /** ColorBrewer qualitative categorical accented scheme with 5 elements. */
    public static final int ACCENT_5 = 284;
            /** ColorBrewer qualitative categorical accented scheme with 6 elements. */
    public static final int ACCENT_6 = 285;
            /** ColorBrewer qualitative categorical accented scheme with 7 elements. */
    public static final int ACCENT_7 = 286;
            /** ColorBrewer qualitative categorical accented scheme with 8 elements. */
    public static final int ACCENT_8 = 287;
    
            /** Single black colour */
    public static final int BLACK = 999;         
  
    // Used to ensure consistency when serializing and deserializing.        
    static final long serialVersionUID = 969046130790808050L;

    private Vector<ColourRule> cTable;  // Vector of colour rule objects.
    private int cTableType;             // Type of colour table ( COLOUR_RULES, COLOUR_RAW or COLOUR_ATTRIB).
    private String name;                // Name given to colour table (used for presets only).
    private boolean isDiscrete;         // Used to indicate colour table consists of discrete values only.
    
                             // Colour masks.
    private static final int ALF = 255 << 24;
    private static final int RED = 255 << 16;
    private static final int GRN = 255 <<  8;
    private static final int BLU = 255;
        
    // ------------------- Constructors ---------------------
    
    /** Creates an initial (black) colour table.
      */
    public ColourTable()
    {
        cTable = new Vector<ColourRule>();
        cTableType = COLOUR_RULES;      // Uses colour rules by default.
        name = "User";
        isDiscrete = false;
        
        cTable.add(new ColourRule(-Float.MAX_VALUE,255,255,255,0)); // Default background is transparent.
    }       
    
    /** Creates a colour table based on a copy of a colour table.
      * @param oldColourTable Old colour table to copy.
      */
    public ColourTable(ColourTable oldColourTable)
    {
        cTableType = oldColourTable.getColourTableType();
        name = oldColourTable.name;
        isDiscrete = oldColourTable.isDiscrete;
                
        cTable = new Vector<ColourRule>();
        cTable.add(new ColourRule(-Float.MAX_VALUE,255,255,255,0));
        
        // If we are using attributes as colours or colours as attributes,
        // we don't need to copy rules.
        if ((cTableType == COLOUR_RAW) || (cTableType == COLOUR_ATTRIB))
        {
            return;
        }
        
        ColourRule rule=null;
        
        for (int i=1; i<oldColourTable.getColourRules().size(); i++)
        {
            rule = oldColourTable.getColourRules().get(i);
            
            if (rule.getType() == ColourRule.DISCRETE)
            {
                addDiscreteColourRule(rule.getlIndex(), rule.getColour(rule.getlIndex()));
            }
            else
            {
                addContinuousColourRule(rule.getlIndex(), rule.getColour(rule.getlIndex())); 
            }
        }
        
        // Check for upper limit (old version of colour table).
        if (rule != null)
        {
            if (rule.getlIndex() != rule.getuIndex())
            {
                addContinuousColourRule(rule.getuIndex(), rule.getColour(rule.getuIndex())); 
            }
        }     
    }
        
    // --------------------- Methods -------------------------
   
    /** Adds a discrete colour rule to the colour table.
      * @param index Index value associated with rule.
      * @param r Red colour component (scaled between 0-255) associated with index.
      * @param g Green colour component (scaled between 0-255) associated with index.
      * @param b Blue colour component (scaled between 0-255) associated with index.
      */
    public void addDiscreteColourRule(float index, int r, int g, int b)
    {
        addDiscreteColourRule(index, (255 << 24) | (r << 16) | (g << 8) | (b)); 
    }
   
    /** Adds a discrete colour rule to the colour table.
      * @param index Index value associated with rule.
      * @param r Red colour component (scaled between 0-255) associated with index.
      * @param g Green colour component (scaled between 0-255) associated with index.
      * @param b Blue colour component (scaled between 0-255) associated with index.
      * @param a Alpha (opacity) component (scaled between 0-255) associated with index.
      */
    public void addDiscreteColourRule(float index, int r, int g, int b, int a)
    {
        addDiscreteColourRule(index, (a << 24) | (r << 16) | (g << 8) | (b)); 
    }
      
    /** Adds a discrete colour rule to the colour table. Combined RGBA version.
      * @param index Index value associated with rule.
      * @param colour Colour value associated with index.
      */
    public void addDiscreteColourRule(float index, int colour)
    {
        int i=1;
        ColourRule rule;
        
        while(i<cTable.size())
        {
            rule = cTable.get(i);
            
            // Insert new colour rule.
            if (rule.getlIndex() >= index)
            {
                break;
            }
                
            i++;
        }
        cTable.add(i, new ColourRule(index, colour));  
    }
   
    /** Adds a continuous colour rule to the colour table.
      * @param index Index value associated with rule.
      * @param r Red colour component (scaled between 0-255) associated with index.
      * @param g Green colour component (scaled between 0-255) associated with index.
      * @param b Blue colour component (scaled between 0-255) associated with index.
      */
    public void addContinuousColourRule(float index, int r, int g, int b)
    {
        addContinuousColourRule(index, (255 << 24) | (r << 16) | (g << 8) | (b)); 
    }
    
    /** Adds a continuous colour rule to the colour table.
      * @param index Index value associated with rule.
      * @param r Red colour component (scaled between 0-255) associated with index.
      * @param g Green colour component (scaled between 0-255) associated with index.
      * @param b Blue colour component (scaled between 0-255) associated with index.
      * @param a Alpha (opacity) component (scaled between 0-255) associated with index.
      */
    public void addContinuousColourRule(float index, int r, int g, int b, int a)
    {
        addContinuousColourRule(index, (a << 24) | (r << 16) | (g << 8) | (b)); 
    }

    /** Adds a continuous colour rule to the colour table. Combined RGBA version.
      * @param index Index value associated with rule.
      * @param colour Colour value associated with index.
      */
    public void addContinuousColourRule(float index, int colour)
    {
        ColourRule prevContinuousRule=null, nextRule=null;
                        
        // Insert rule in numerical order of lower index. 
        for (int i=1; i<cTable.size(); i++)
        {
            nextRule = cTable.get(i);
            
            if (nextRule.getlIndex() == index)
            {
                // Replace existing rule.
                ColourRule rule = nextRule;
                
                rule.setlColour(colour);
                rule.setType(ColourRule.CONTINUOUS);           
                
                // Look for next continuous colour rule in table.
                // We need this because we may have converted a discrete rule 
                // to a continuous one.
                for (int j=i+1; j<cTable.size(); j++)
                {
                    nextRule = cTable.get(j);
                    
                    if (nextRule.getType() == ColourRule.CONTINUOUS)
                    {
                        // Interpolate to next continuous rule.
                        rule.setuColour(nextRule.getlColour());
                        rule.setuIndex(nextRule.getlIndex());
                        break;
                    }  
                }
                
                // Update previous continuous colour.
                if (prevContinuousRule != null)
                {
                    prevContinuousRule.setuColour(colour);
                }
                         
                return; 
            }
            
            if (nextRule.getlIndex() > index)
            {
                // Look for next continuous colour rule in table.
                boolean foundRule = false;
                
                for (int j=i; j<cTable.size(); j++)
                {
                    nextRule = cTable.get(j);
                    
                    if (nextRule.getType() == ColourRule.CONTINUOUS)
                    {
                        // Interpolate to next continuous rule.
                        cTable.add(i,new ColourRule(index,colour,nextRule.getlIndex(),nextRule.getlColour()));
                        foundRule = true;
                        break;
                    }  
                }
                
                if (foundRule == false)
                {
                    // No continuous rule found further along table.
                    cTable.add(i, new ColourRule(index,colour,index,colour));  
                }
                 
                // Update upper limit of previous continuous rule if it exists.
                if (prevContinuousRule != null)
                {  
                    prevContinuousRule.setuIndex(index);
                    prevContinuousRule.setuColour(colour); 
                }
  
                return;
            }
            
            if (nextRule.getType() == ColourRule.CONTINUOUS)
            {
                prevContinuousRule = nextRule;      
            }
        }
        
        // If we get this far, new rule must be inserted at end of list.
        cTable.add(new ColourRule(index,colour,index,colour));
        if (prevContinuousRule != null)
        {
            prevContinuousRule.setuIndex(index);
            prevContinuousRule.setuColour(colour); 
        }
    }
    
    /** Removes the rule with the given lower index.
      * @param index Lower index identifying rule to remove. 
      * @return True if rule with given lower index found and removed.
      */
   public boolean removeColourRule(float index)
   {
       ColourRule prevContinuousRule=null, 
                  ruleToRemove=null, 
                  nextContinuousRule=null,
                  rule = null;
                
       // Find the rule with the given index. 
       for (int i=1; i<cTable.size(); i++)
       {
           rule = cTable.get(i);
    
           if (rule.getlIndex() == index)
           {
               ruleToRemove = rule;
           }
           else
           {
               if ((ruleToRemove == null) && (rule.getType() == ColourRule.CONTINUOUS))
               {
                   prevContinuousRule = rule;
               }
                   
               if ((ruleToRemove != null) && (rule.getType() == ColourRule.CONTINUOUS))
               {
                   nextContinuousRule = rule;
                   break;
               }
           }
       }
       if (ruleToRemove == null)
       {
           return false;
       }
               
       // Update previous continuous rule.
       if (prevContinuousRule != null)
       {
           if (nextContinuousRule != null)
           {
               prevContinuousRule.setuColour(nextContinuousRule.getlColour());
               prevContinuousRule.setuIndex(nextContinuousRule.getlIndex());
           }
           else
           {
               prevContinuousRule.setuColour(prevContinuousRule.getlColour());
               prevContinuousRule.setuIndex(prevContinuousRule.getlIndex());  
           }
       }

       // Delete selected rule.
       return cTable.remove(ruleToRemove);
   }
    
    /** Modifies the rule with the existing index to use the given index and colour.
      * @param existingIndex Index value associated with rule.
      * @param newIndex New index value associated with rule.
      * @param newColour Colour value associated with index.
      * @param newType New type of rule (either DISCRETE or CONTINUOUS).
      * @return True if rule with given index found.
      */
    public boolean modifyColourRule(float existingIndex, float newIndex, int newColour, int newType)
    {
        ColourRule prevContinuousRule=null, nextRule=null;
                        
        // Find the rule with the given index. 
        for (int i=1; i<cTable.size(); i++)
        {
            nextRule = cTable.get(i);
            
            if (nextRule.getlIndex() == existingIndex)
            {
                // Set lower index/colour.
                ColourRule rule = nextRule;
                rule.setlColour(newColour); 
                rule.setlIndex(newIndex);
                rule.setType(newType);
                
                // Set upper index/colour.
                if (newType == ColourRule.DISCRETE)
                {
                    rule.setuColour(newColour); 
                    rule.setuIndex(newIndex);
                }
                else
                {
                    // Look for next continuous colour rule in table.
                    for (int j=i+1; j<cTable.size(); j++)
                    {
                        nextRule = cTable.get(j);
                        
                        if (nextRule.getType() == ColourRule.CONTINUOUS)
                        {
                            // Interpolate to next continuous rule.
                            rule.setuColour(nextRule.getlColour());
                            rule.setuIndex(nextRule.getlIndex());
                            break;
                        }  
                    }
                }
                
                // Update previous continuous colour.
                if (prevContinuousRule != null)
                {
                    // Set upper index/colour.
                    if (newType == ColourRule.DISCRETE)
                    {
                        // Link previous upper rule with next continuous lower rule.
                        for (int j=i+1; j<cTable.size(); j++)
                        {
                            nextRule = cTable.get(j);
 
                            if (nextRule.getType() == ColourRule.CONTINUOUS)
                            {
                                // Interpolate to next continuous rule.
                                prevContinuousRule.setuColour(nextRule.getlColour());
                                prevContinuousRule.setuIndex(nextRule.getlIndex());
                                break;
                            }  
                        }
                    }
                    else
                    {
                        prevContinuousRule.setuColour(newColour);
                        prevContinuousRule.setuIndex(newIndex);
                    }
                }     
                return true;
            }
            if (nextRule.getType() == ColourRule.CONTINUOUS)
            {
                prevContinuousRule = nextRule;     
            }
        }
        
        // No match found.
        return false; 
    }
    
           
    /** Interpolates a given index colour from colour rules.
      * @param index Index to associated interpolated colour with.
      * @return Interpolated colour.
      */
    public int findColour(float index)
    {    
        int        i=1,     // Colour table entry counter.
                   nearest=0;
                   
        float               // Closest colour match.
                   minDistance= Float.MAX_VALUE;
        
        ColourRule rule;    // Single colour table rule.
        
        // If we are using raster values as colours, return them directly.
        if (cTableType == COLOUR_RAW)
        {
            return (int)index;
        }
            
        // If we are using colours to represent attributes, return the integer
        // coding of the attribute directly.
        if (cTableType == COLOUR_ATTRIB)
        {
            return Float.floatToRawIntBits(index);
        }
            
        // If index is null, return transparent colour.
        if (index == NO_VALUE)
        {
            return 0;
        }
         
        // If no extra colour rules added, return default colour.   
        if (cTable.size() == 1)
        {
            rule = cTable.firstElement();
            return rule.getlColour();
        }
             
        rule = cTable.get(1);
             
        // Return first colour if index less than first rule.
        if (index <= rule.getlIndex())
        {
            return rule.getlColour();
        }
            
        // Check for discrete colours first.
        while(i<cTable.size())
        {
            rule =  cTable.get(i);
            
            if (rule.getType() == ColourRule.DISCRETE)
            {
                if (index == rule.getlIndex())
                {
                    return rule.getlColour();
                }
            }
            i++;
        }
        
        i=1;
                        
        // Find closest rule that matches index.
        while(i<cTable.size())
        {
            rule = cTable.get(i);
            
            if ((index >= rule.getlIndex()) && (index <= rule.getuIndex()))
            {
                return (rule.getColour(index));
            }
            
            if (Math.abs(index-rule.getlIndex()) < minDistance)
            {
                nearest= rule.getColour(rule.getlIndex());
                minDistance = Math.abs(index-rule.getlIndex());
            }
            i++;
        }
        
        if (index >= rule.getuIndex())
        {
            nearest= rule.getuColour();
        }
        
        // Return nearest value if no other match found.
        return nearest;    
    }
    
    /** Finds the lowest index value in a set of colour rules.
     *  @return Minimum index value.
     */
    public float getMinIndex()
    {
        if (cTable.size() <= 1)
        {
            return 0;
        }
            
        ColourRule rule = cTable.elementAt(1);
        return rule.getlIndex();
    }
    
    /** Finds the highest index value in a set of colour rules.
     *  @return Maximum index value.
     */
    public float getMaxIndex()
    {
        if (cTable.size() <= 1)
        {
            return 0;
        }
            
        ColourRule rule = cTable.lastElement();
        return rule.getuIndex();
    }
    
    /** Reports whether this colour table can be treated as consisting of discrete values only.
     *  Note that for user defined colour tables, this value needs to be set manually. Preset
     *  colour tables will set the correct value, but it can be overridden with <code>setIsDiscrete()</code>.
     *  The default value for all non-preset colour tables is false.
     *  @return True if colour table can be considered discrete.
     */
    public boolean getIsDiscrete()
    {
        return isDiscrete;
    }
    
    /** Sets whether this colour table should be treated as consisting of discrete values only or not.
     *  @param isDiscrete Determines whether or not this colour table should be considered discrete.
     */
    public void setIsDiscrete(boolean isDiscrete)
    {
        this.isDiscrete = isDiscrete;
    }
    
    /** Reports the name attached to the colour table. By default this is 'User' for all
     *  colour tables created by adding rules, although it can be changed by calling
     *  <code>setName()</code>. All preset colour tables should provide a name that uniquely
     *  identifies the colour scheme.
     *  @return Name of the colour table.
     */
    public String getName()
    {
        return name;
    }
    
    /** Sets a new name to be associated with the colour table.
     *  @param name Name to give to the colour table.
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /** Reports the current colour table rules.
      * @return String containing current colour table.
      */
    public String toString()
    {
        StringBuffer output;     // Output string.     
        ColourRule   rule=null;  // Single colour table rule.
        
        if (cTable.size() <=1)
        {
            return ("Empty colour table");
        }   
        output = new StringBuffer();
        
        Iterator<ColourRule> i = cTable.iterator();
        i.next();   // Ignore first rule.
        
        while (i.hasNext())
        {
            rule = i.next();
            output.append(rule.toString());
        }
          
        // Check for old format rules where last colour is defined by upper rule only.
        if (rule.getlIndex() != rule.getuIndex())
        {
            int alpha = ((rule.getColour(rule.getuIndex())) & ALF) >> 24;
            int red   = ((rule.getColour(rule.getuIndex())) & RED) >> 16;
            int green = ((rule.getColour(rule.getuIndex())) & GRN) >>  8;
            int blue  =   rule.getColour(rule.getuIndex())  & BLU;

            // Account for signed integers.
            if (alpha < 0)
            {
                alpha += 256;
            }
                 
            output.append(rule.getlIndex() + ": " + red + "," + green + ","+ blue + "," + alpha + "\n");
        }
                  
        return output.toString();
    }
    
    /** Reports the current colour table rules.
      * @return String containing current colour table.
      */
    public String[] toStringArray()
    {
        String[] output=null;       // Output strings.     
        ColourRule   rule=null;     // Single colour table rule.
    
        if (cTable.size() <=1)
        {
            output = new String[1];
            output[0] = new String("Empty colour table");
            return output;
        }
        
        output = new String[cTable.size()-1];
    
        for (int i=1; i<cTable.size(); i++)
        {
            rule = cTable.get(i);
            output[i-1] = new String(rule.toString());
        }
           
        return output;
    }
    
    /** Converts the colour rules into a ListModel suitable for display by Swing <code>JList</code>s
     *  or <code>JTable</code>s. If the colour table is not empty, each element is a <code>ColourRule</code>
     *  and so can perform detailed query of its colours and indices. If it is empty, the list model will
     *  contain a single string indicating table is empty.
     * @return ListModel representation of the colour rules.
     */              
    public ListModel toListModel()
    {
        DefaultListModel listModel = new DefaultListModel();
        
        if (cTable.size() <=1)
        {
            listModel.addElement(new String("Empty colour table"));
        }
        else
        {
            for (int i=1; i<cTable.size(); i++)
            {
                ColourRule rule = cTable.get(i);
                listModel.addElement(rule);
            }
        }
        return listModel; 
    }

    /** Reports type of colour table (<code>COLOUR_RULES</code>, <code>COLOUR_ATTRIB</code> or <code>COLOUR_RAW</code>).
      * @return Type of colour table.
      */   
    public int getColourTableType()
    {
        return cTableType;
    }
    
    /** Sets the type of colour table (<code>COLOUR_RULES</code>, <code>COLOUR_ATTRIB</code> or <code>COLOUR_RAW</code>).
      * @param cTableType Type of colour table.
      */
    public void setColourTableType(int cTableType)
    {
        // We need this to avoid inadvertently changing a manually set colour table name.
        if (cTableType == this.cTableType)
        {
            return;
        }
  
        this.cTableType = cTableType;
        if (cTableType == COLOUR_RAW)
        {
            name = "raw";
        }
        else if (cTableType == COLOUR_ATTRIB)
        {
            name = "attrib";
        }
        else if (cTableType == COLOUR_RULES)
        {
            name = "user";
        }
    }
    
    /** Returns the rules associated with the colour table
      * @return Ordered list of colour table rules.
      */
    public Vector<ColourRule> getColourRules()
    {
        return cTable;
    }
   
    // ---------------------- Static Methods ----------------------
    
    /** Converts an RGBA into an ARGB colour.
      * @param rgba RGBA format colour.
      * @return ARGB format colour.
      */
    public static int rgbaToArgb(int rgba)
    {
        int alpha  = rgba << 24;
        return (rgba >> 8) | alpha;  
    }
     
    /** Converts an ARGB into an RGBA colour.
      * @param argb ARGB format colour.
      * @return RGBA format colour.
      */
    public static int argbToRgba(int argb)
    {
        int alpha  = argb >> 24; 
        return (argb << 8) | alpha;  
    }   
    
    /** Converts a given colour into its HTML-like hex string in the form <code>#rrggbb</code> or
     *  <code>rrggbbaa</code> if the colour has a non-opaque alpha value.
     *  @param colour Colour to convert.
     *  @return HTML hex string representing colour.
     */  
    public static String getHexString(int colour)
    {
        int red   = (colour & RED) >> 16;
        int green = (colour & GRN) >>  8;
        int blue  = (colour & BLU);
        
        int alf   = (colour & ALF) >> 24;
        // Convert to unsigned integer.
        if (alf < 0) 
        {
            alf +=256;
        }
        
        // The #rrggbbaa version.
        if (alf <255)
        {
        	return new String("#"+paddedHex (red) + paddedHex (green) + paddedHex (blue)+ paddedHex(alf));
        }
        
        // The #rrggbb version.
        return new String("#"+paddedHex (red) + paddedHex (green) + paddedHex (blue));            
    }
    
    /** Converts a given colour into its hex string in the form <code>aabbggrr</code>.
     *  @param colour Colour to convert.
     *  @return Hex string representing colour.
     */  
    public static String getHexStringABGR(int colour)
    {
        int alf   = (colour & ALF) >> 24;
        // Convert to unsigned integer.
        if (alf < 0) 
        {
            alf +=256;
        }
        int red   = (colour & RED) >> 16;
        int green = (colour & GRN) >>  8;
        int blue  = (colour & BLU);
  
        return new String(paddedHex(alf) + paddedHex (blue) + paddedHex (green) + paddedHex (red));            
    }
   
    /** Reports the alpha value of the given colour.
      * @param colour Colour to query.
      * @return Alpha value of the colour (scaled from 0 (transparent) to 255 (opaque).
      */  
    public static int getAlpha(int colour)
    {
        int alf = (colour & ALF) >> 24;
        if (alf < 0) 
        {
            alf +=256;
        }
        return alf;           
    }
    
    /** Converts the given integer into a hex string, padded with a zero if only one digit.
      * @param i Integer to convert.
      * @return Hex version of integer.
      */ 
    private static String paddedHex(int i) 
    {
        String s = Integer.toHexString (i);
        if (s.length () == 1) 
        {
            s = "0" + s;
        }
        return s;
    }

    /** Creates a preset colour table scaled between default minimum and maximum values. Defaults are generally between
     *  0 and 1 for continuous sequential schemes, -1 and 1 for diverging schemes and 1 to <i>n</i> for categorical schemes.
     *  Categorical schemes with a fixed number of categories can be identified by a <code>_</code> suffix indicating the number
     *  of categories to be assigned colours.
     *  @param type Type of colour table to create (<code>DEFAULT</code>, <code>GREYSCALE</code>, <code>YL_OR_BR</code> etc.).
     *  @return New colour table.
     */
    public static ColourTable getPresetColourTable(int type)
    {
        return getPresetColourTable(type,Float.NaN,Float.NaN);
    }
    
    /** Provides a default minimum value depending on the type of colour scheme. The default is 0 for continuous sequential schemes,
     *  -1 for diverging schemes and 1 for categorical schemes.
     *  @param type Colour scheme to be used to determine default minimum.
     *  @return Default minimum value.
     */
    private static float getDefaultMin(int type)
    {
        switch (type)
        {
            // Diverging schemes start at -1.
            case DIVERGING_BLURED:
            case DIVERGING_GRNYEL:
            case DIVERGING_BLUYELRED:
            case IMHOF_SL:
            case PU_OR:
            case BR_B_G:
            case P_R_GN:
            case PI_Y_G:
            case RD_BU:
            case RD_GY:
            case RD_YL_BU:
            case SPECTRAL:
                return -1;
            
            // Categorical schemes start at 1.
            case RANDOM:
            case SET1_3: case SET1_4: case SET1_5: case SET1_6: case SET1_7: case SET1_8: case SET1_9:
            case SET2_3: case SET2_4: case SET2_5: case SET2_6: case SET2_7: case SET2_8:
            case SET3_3: case SET3_4: case SET3_5: case SET3_6: case SET3_7: case SET3_8: case SET3_9: case SET3_10: case SET3_11: case SET3_12:
            case PASTEL1_3: case PASTEL1_4: case PASTEL1_5: case PASTEL1_6: case PASTEL1_7: case PASTEL1_8: case PASTEL1_9:
            case PASTEL2_3: case PASTEL2_4: case PASTEL2_5: case PASTEL2_6: case PASTEL2_7: case PASTEL2_8:
            case DARK2_3: case DARK2_4: case DARK2_5: case DARK2_6: case DARK2_7: case DARK2_8:
            case PAIRED_3: case PAIRED_4: case PAIRED_5: case PAIRED_6: case PAIRED_7: case PAIRED_8: case PAIRED_9: case PAIRED_10: case PAIRED_11: case PAIRED_12:
            case ACCENT_3: case ACCENT_4: case ACCENT_5: case ACCENT_6: case ACCENT_7: case ACCENT_8:
                return 1;
            
            // All other schemes start at 0.
            default:
                return 0;
        }
    }
    
    /** Provides a default maximum value depending on the type of colour scheme and for categorical ones, the minimum value.
     *  The default is 1 for continuous sequential and diverging schemes, and <i>(min-1)+n</i> for categorical schemes where
     *  <i>n</i> is the number of categories in the scheme. The <code>RANDOM</code> colour scheme defaults to 10 random 
     *  categories.
     *  @param type Colour scheme to be used to determine default maximum.
     *  @param min Minimum value in the scheme.
     *  @return Default maximum value.
     */
    private static float getDefaultMax(int type, float min)
    {
        if (Float.isNaN(min))
        {
            min = getDefaultMin(type);
        }
        
        switch (type)
        {
            // The random scheme is the only categorical scheme that does not have a predefined number of classes.
            case RANDOM:
                return (min-1) + 10;
            
            case FEATURES:
                return UPEAK;       // Highest feature value.
            
            // Categorical schemes start at min and end according to the number of categories
            case SET1_3: case SET2_3: case SET3_3: case PASTEL1_3: case PASTEL2_3: case DARK2_3: case PAIRED_3: case ACCENT_3:
                return (min-1)+3;
                
            case SET1_4: case SET2_4: case SET3_4: case PASTEL1_4: case PASTEL2_4: case DARK2_4: case PAIRED_4: case ACCENT_4:
                return (min-1)+4;
                
            case SET1_5: case SET2_5: case SET3_5: case PASTEL1_5: case PASTEL2_5: case DARK2_5: case PAIRED_5: case ACCENT_5:
                return (min-1)+5;
                
            case SET1_6: case SET2_6: case SET3_6: case PASTEL1_6: case PASTEL2_6: case DARK2_6: case PAIRED_6: case ACCENT_6:
                return (min-1)+6;
                
            case SET1_7: case SET2_7: case SET3_7: case PASTEL1_7: case PASTEL2_7: case DARK2_7: case PAIRED_7: case ACCENT_7:
                return (min-1)+7;
            
            case SET1_8: case SET2_8: case SET3_8: case PASTEL1_8: case PASTEL2_8: case DARK2_8: case PAIRED_8: case ACCENT_8:
                return (min-1)+8;
                
            case SET1_9: case SET3_9: case PASTEL1_9: case PAIRED_9:
                return (min-1)+9;
            
            case SET3_10: case PAIRED_10:
                return (min-1)+10;
            
            case SET3_11: case PAIRED_11:
                return (min-1)+11;
                
            case SET3_12: case PAIRED_12:
                return (min-1)+12;
            
            // All other schemes end at 1.
            default:
                return 1;
        }
    }
    
    /** Creates a preset colour table between the given values. Note that the <code>max</code> value is ignored for
     *  ColorBrewer categorical schemes that specify the number of classes since this is pre-determined relative to the 
     *  <code>min</code> value using integer steps. For example, calling <code>getPresetColourTable(DARK_4,10,100)</code>
     *  would define categorical colours to the values 10, 11, 12 and 13 since only 4 colours are defined in that scheme.
     *  Categorical schemes with a fixed number of categories can be identified by a <code>_</code> suffix indicating the number
     *  of categories to be assigned colours.
     * @param type Type of colour scheme to create (<code>DEFAULT</code>, <code>GREYSCALE</code>, <code>YL_OR_BR</code> etc.).
     * @param min Minimum value. If <code>Float.NaN</code>, the scheme's default minimum is used.
     * @param max Maximum value (ignored for ColorBrewer categorical schemes). If <code>Float.NaN</code>, the scheme's default
     *            maximum is used.
     * @return Colour table scaled between given <code>min</code> and <code>max</code> values.
     */
    public static ColourTable getPresetColourTable(int type, float min, float max)
    {
        if (Float.isNaN(min))
        {
            min = getDefaultMin(type);
        }
        if (Float.isNaN(max))
        {
            max = getDefaultMax(type,min);
        }
        
        float range = max - min,
              interval;
        
        ColourTable colours =  new ColourTable();
    
        if (range <= 0)
        {
            colours.addContinuousColourRule(max,0,0,0);
            colours.addContinuousColourRule(min,0,0,0);
            return colours;
        }
            
        switch (type)
        {
            case SLOPE:
                colours.name = "slope";
                colours.addContinuousColourRule(0f ,255,255,255);
                colours.addContinuousColourRule(10f,255,255,  0);
                colours.addContinuousColourRule(30f,255,  0,  0);
                colours.addContinuousColourRule(90f,  0,  0,  0);
                break;
                
            case ASPECT:
                colours.name = "aspect";
                colours.addContinuousColourRule(  0f,255,127,127);
                colours.addContinuousColourRule( 45f,255,  0,  0);
                colours.addContinuousColourRule(135f,  0,  0,  0);
                colours.addContinuousColourRule(225f,  0,  0,255);
                colours.addContinuousColourRule(315f,255,255,255);
                colours.addContinuousColourRule(360f,255,127,127);
                break;
              
            case FEATURES:
                colours.name = "features";
                colours.addDiscreteColourRule(UNDEFINED, 50,  50,  50, 0);   // Transparent.
                colours.addDiscreteColourRule(PIT,     0,   0,   0);
                colours.addDiscreteColourRule(CHANNEL, 0,   0, 200);
                colours.addDiscreteColourRule(PASS,    0, 150,   0);
                colours.addDiscreteColourRule(RIDGE, 250, 250,   0);
                colours.addDiscreteColourRule(PEAK,  200,   0,   0);
                colours.addDiscreteColourRule(PLANAR,200, 200, 200);
                colours.addDiscreteColourRule((PLANAR+UPIT)/2, 50,  50,  50, 0);
                colours.addDiscreteColourRule(UPIT,  100, 100, 100);
                colours.addDiscreteColourRule(UPEAK, 150, 100, 100);
                colours.setIsDiscrete(true);
                break;
                
            case DIVERGING_BLURED:
                colours.name = "diverging2";
                if (min >0)
                {
                    colours.addContinuousColourRule(min,           0,  0,  0);
                    colours.addContinuousColourRule(min+range/3f,  0,  0,255);
                    colours.addContinuousColourRule((max+min)/2f,255,255,255);
                    colours.addContinuousColourRule(max-range/3f,255,  0,  0);
                    colours.addContinuousColourRule(max,           0,  0,  0);
                }
                else
                {
                    // Centre around 0.
                    float lowerInterval = 2*(0-min)/3f;
                    float upperInterval = 2*max/3f;
                    colours.addContinuousColourRule(min,           0,  0,  0);
                    colours.addContinuousColourRule(min+lowerInterval,  0,  0,255);
                    colours.addContinuousColourRule(0,255,255,255);
                    colours.addContinuousColourRule(max-upperInterval,255,  0,  0);
                    colours.addContinuousColourRule(max,           0,  0,  0);
                }
                break;
                
            case DIVERGING_GRNYEL:
                colours.name = "diverging3";
                if (min >0)
                {
                    colours.addContinuousColourRule(min,           0,  0,  0);
                    colours.addContinuousColourRule(min+range/3f,  0,255,  0);
                    colours.addContinuousColourRule((max+min)/2f,255,255,255);
                    colours.addContinuousColourRule(max-range/3f,255,255,  0);
                    colours.addContinuousColourRule(max,           0,  0,  0);
                }
                else
                {
                    // Centre around 0.
                    float lowerInterval = 2*(0-min)/3f;
                    float upperInterval = 2*max/3f;
                    colours.addContinuousColourRule(min,                0,  0,  0);
                    colours.addContinuousColourRule(min+lowerInterval,  0,255,  0);
                    colours.addContinuousColourRule(0,                255,255,255);
                    colours.addContinuousColourRule(max-upperInterval,255,255,  0);
                    colours.addContinuousColourRule(max,                0,  0,  0);
                }
                break;
                
            case DIVERGING_BLUYELRED:
                colours.name = "diverging1";
                if (min >0)
                {
                    interval = range/12f;
                    colours.addContinuousColourRule(min,             49, 54,149);
                    colours.addContinuousColourRule(min+interval,   49, 54,149);
                    colours.addContinuousColourRule(min+2*interval,  69,117,180);
                    colours.addContinuousColourRule(min+3*interval,116,173,209);
                    colours.addContinuousColourRule(min+4*interval,171,217,233);
                    colours.addContinuousColourRule(min+5*interval,224,243,248);
                    
                    colours.addContinuousColourRule(min+6*interval,     255,255,191);
                    
                    colours.addContinuousColourRule(min+7*interval,254,224,144);
                    colours.addContinuousColourRule(min+8*interval,253,174, 97);
                    colours.addContinuousColourRule(min+9*interval,244,109, 67);
                    colours.addContinuousColourRule(min+10*interval,  215, 48, 39);     
                    colours.addContinuousColourRule(min+11*interval,  165,  0, 38);
                    colours.addContinuousColourRule(max,            165,  0, 38);
                }
                else
                {
                    // Centre around 0.
                    float lowerInterval = (0-min)/6f;
                    float upperInterval = max/6f;
                    colours.addContinuousColourRule(min,             49, 54,149);
                    colours.addContinuousColourRule(min+lowerInterval,   49, 54,149);
                    colours.addContinuousColourRule(min+2*lowerInterval,  69,117,180);
                    colours.addContinuousColourRule(min+3*lowerInterval,116,173,209);
                    colours.addContinuousColourRule(min+4*lowerInterval,171,217,233);
                    colours.addContinuousColourRule(min+5*lowerInterval,224,243,248);
                    colours.addContinuousColourRule(0,     255,255,191);
                    
                    colours.addContinuousColourRule(upperInterval,254,224,144);
                    colours.addContinuousColourRule(2*upperInterval,253,174, 97);
                    colours.addContinuousColourRule(3*upperInterval,244,109, 67);
                    colours.addContinuousColourRule(4*upperInterval,  215, 48, 39);     
                    colours.addContinuousColourRule(5*upperInterval,  165,  0, 38);
                    colours.addContinuousColourRule(max,            165,  0, 38);
                }
                break;
                
            case GREYSCALE:
                colours.name = "grey1";
                colours.addContinuousColourRule(min,  0,  0,  0);
                colours.addContinuousColourRule(max,255,255,255);
                break;
            
            case INV_GREYSCALE:
                colours.name = "grey2";
                colours.addContinuousColourRule(min,255,255,255);
                colours.addContinuousColourRule(max,  0,  0,  0);
                break;
                
            case IMHOF_L1:
                colours.name = "land1";
                interval = range/6.5f;
                colours.addContinuousColourRule(min,            98,123, 92);
                colours.addContinuousColourRule(min+interval,  130,152,117);
                colours.addContinuousColourRule(min+2*interval,155,180,139);
                colours.addContinuousColourRule(min+3*interval,196,197,160);
                colours.addContinuousColourRule(min+4*interval,229,216,175);
                colours.addContinuousColourRule(min+5*interval,244,232,195);
                colours.addContinuousColourRule(min+6*interval,249,242,230);
                colours.addContinuousColourRule(max,           249,242,230);
                break;
                
            case IMHOF_L2:
                colours.name = "land2";
                interval = range/6.5f;
                colours.addContinuousColourRule(min,           161,212,179);
                colours.addContinuousColourRule(min+interval,  199,221,182);
                colours.addContinuousColourRule(min+2*interval,231,234,195);
                colours.addContinuousColourRule(min+3*interval,248,231,190);
                colours.addContinuousColourRule(min+4*interval,235,213,174);
                colours.addContinuousColourRule(min+5*interval,224,195,158);
                colours.addContinuousColourRule(min+6*interval,216,172,136);
                colours.addContinuousColourRule(max,           216,172,136);
                break;
                
            case IMHOF_L3:
                colours.name = "land3";
                interval = range/6.5f;
                colours.addContinuousColourRule(min,           141,166,141);
                colours.addContinuousColourRule(min+interval,  172,194,155);
                colours.addContinuousColourRule(min+2*interval,221,219,167);
                colours.addContinuousColourRule(min+3*interval,254,235,181);
                colours.addContinuousColourRule(min+4*interval,248,212,153);
                colours.addContinuousColourRule(min+5*interval,241,170,109);
                colours.addContinuousColourRule(min+6*interval,227,112, 72);
                colours.addContinuousColourRule(max,           227,112, 72);
                break;
                
            case IMHOF_L4:
                colours.name = "land4";
                interval = range/7f;
                colours.addContinuousColourRule(min,              153,201,171);
                colours.addContinuousColourRule(min+interval,     202,224,190);
                colours.addContinuousColourRule(min+2*interval,   252,239,201);
                colours.addContinuousColourRule(min+3*interval,   252,222,197);
                colours.addContinuousColourRule(min+4*interval,   254,207,177);
                colours.addContinuousColourRule(min+5*interval,   217,199,210);
                colours.addContinuousColourRule(min+6*interval,   242,228,227);
                colours.addContinuousColourRule(min+6.5f*interval,255,255,255);
                colours.addContinuousColourRule(max,              255,255,255);
                break;
                
            case IMHOF_S1:
                colours.name = "sea1";
                interval = range/7.8f;
                colours.addContinuousColourRule(min,              112,193,189);
                colours.addContinuousColourRule(min+3*interval,   153,210,198);
                colours.addContinuousColourRule(min+5*interval,   182,222,211);
                colours.addContinuousColourRule(min+6.5f*interval,224,232,223);
                colours.addContinuousColourRule(min+7.5f*interval,255,255,255);
                colours.addContinuousColourRule(max,              255,255,255);
                break;
                
            case IMHOF_S2:
                colours.name = "sea2";
                interval = range/8f;
                colours.addContinuousColourRule(min,               69,171,178);
                colours.addContinuousColourRule(min+3*interval,   121,200,191);
                colours.addContinuousColourRule(min+5*interval,   154,211,204);
                colours.addContinuousColourRule(min+6.5f*interval,195,227,212);
                colours.addContinuousColourRule(min+7.5f*interval,224,232,230);
                colours.addContinuousColourRule(min+7.9f*interval,255,255,255);
                colours.addContinuousColourRule(max,              255,255,255);
                break;
                
            case IMHOF_SL:
                colours.name = "seaLand";
                interval = (0-min)/6f;
                float minPos = 0.001f;
                
                if (interval >=0)
                {
                    colours.addContinuousColourRule(min,            40,153,162);
                    colours.addContinuousColourRule(min+interval,   92,181,176);
                    colours.addContinuousColourRule(min+2*interval,120,190,188);
                    colours.addContinuousColourRule(min+3*interval,169,208,203);
                    colours.addContinuousColourRule(min+4*interval,207,220,210);
                    colours.addContinuousColourRule(min+5*interval,234,228,218);
                    colours.addContinuousColourRule(0,             234,228,218);
                }
                
                interval = (max-minPos)/6.5f;
                if (interval >0)
                {
                    colours.addContinuousColourRule(minPos,           134,189,155);
                    colours.addContinuousColourRule(minPos+interval,  179,205,174);
                    colours.addContinuousColourRule(minPos+2*interval,229,216,184);
                    colours.addContinuousColourRule(minPos+3*interval,228,216,181);
                    colours.addContinuousColourRule(minPos+4*interval,217,198,166);
                    colours.addContinuousColourRule(minPos+5*interval,201,179,149);
                    colours.addContinuousColourRule(minPos+6*interval,189,152,123);
                    colours.addContinuousColourRule(max,              189,152,123);
                }
                break;
                
            case EXP_ORRED:
                colours.name = "exp1";
                colours.addContinuousColourRule(min,           255,247,236);
                colours.addContinuousColourRule(min+range/500, 254,232,200);
                colours.addContinuousColourRule(min+range/200, 253,212,158);
                colours.addContinuousColourRule(min+range/100, 253,187,132);
                colours.addContinuousColourRule(min+range/50,  252,141, 89);
                colours.addContinuousColourRule(min+range/20,  239,101, 72);
                colours.addContinuousColourRule(min+range/10,  215, 48, 31);
                colours.addContinuousColourRule(min+range/5,   179,  0,  0);
                colours.addContinuousColourRule(min+range/2,   127,  0,  0);
                colours.addContinuousColourRule(max,           64,  0,  0);
                break;
                
            case RANDOM:
                colours.name = "random";
                if ((max-min)> 2)
                {
                    interval = 1;
                }
                else
                {
                    interval = (max-min)/100f;
                }
                    
                int prevRed = -999;
                int prevGrn = -999;
                int prevBlu = -999;
                int red,grn,blu;
                int diff = 0;
                
                for (float i=min; i<max+interval; i+=interval)
                {
                    // Avoid the dark intensities and strong saturation and ensure 
                    // adjacent colours are not too similar.
                    do
                    {
                        red = (int)(Math.random()*150);
                        grn = (int)(Math.random()*150);
                        blu = (int)(Math.random()*150);
                        diff = Math.abs(red-prevRed) + Math.abs(grn-prevGrn) + Math.abs(blu-prevBlu); 
                    }
                    while (diff < 50);
                    
                    colours.addDiscreteColourRule(i, red+100,grn+100,blu+100);
                    
                    prevRed = red;
                    prevGrn = grn;
                    prevBlu = blu;
                }
                colours.setIsDiscrete(true);
                break;
                
            case BLACK:
                colours.name = "black";
                colours.addContinuousColourRule(min,0,0,0);
                colours.addContinuousColourRule(max,0,0,0);
                break;
                
            // ---- ColorBrewer colours
            case YL_GN:
                colours.name = "YlGn";
                interval = range/9f;
                colours.addContinuousColourRule(min,              255,255,229);
                colours.addContinuousColourRule(min+0.5f*interval,255,255,229);
                colours.addContinuousColourRule(min+1.5f*interval,247,252,185);
                colours.addContinuousColourRule(min+2.5f*interval,217,240,163);
                colours.addContinuousColourRule(min+3.5f*interval,173,221,142);
                colours.addContinuousColourRule(min+4.5f*interval,120,198,121);
                colours.addContinuousColourRule(min+5.5f*interval,65,171,93);
                colours.addContinuousColourRule(min+6.5f*interval,35,132,67);
                colours.addContinuousColourRule(min+7.5f*interval,0,104,55);
                colours.addContinuousColourRule(min+8.5f*interval,0,69,41);
                colours.addContinuousColourRule(max,              0,69,41);
                break;
            case YL_GN_BU:
                colours.name = "YlGnBu";
                interval = range/9f;
                colours.addContinuousColourRule(min,              255,255,217);
                colours.addContinuousColourRule(min+0.5f*interval,255,255,217);
                colours.addContinuousColourRule(min+1.5f*interval,237,248,177);
                colours.addContinuousColourRule(min+2.5f*interval,199,233,180);
                colours.addContinuousColourRule(min+3.5f*interval,127,205,187);
                colours.addContinuousColourRule(min+4.5f*interval,65,182,196);
                colours.addContinuousColourRule(min+5.5f*interval,29,145,192);
                colours.addContinuousColourRule(min+6.5f*interval,34,94,168);
                colours.addContinuousColourRule(min+7.5f*interval,37,52,148);
                colours.addContinuousColourRule(min+8.5f*interval,8,29,88);
                colours.addContinuousColourRule(max,              8,29,88);
                break;
            case GN_BU:
                colours.name = "GnBu";
                interval = range/9f;
                colours.addContinuousColourRule(min,              247,252,240);
                colours.addContinuousColourRule(min+0.5f*interval,247,252,240);
                colours.addContinuousColourRule(min+1.5f*interval,224,243,219);
                colours.addContinuousColourRule(min+2.5f*interval,204,235,197);
                colours.addContinuousColourRule(min+3.5f*interval,168,221,181);
                colours.addContinuousColourRule(min+4.5f*interval,123,204,196);
                colours.addContinuousColourRule(min+5.5f*interval,78,179,211);
                colours.addContinuousColourRule(min+6.5f*interval,43,140,190);
                colours.addContinuousColourRule(min+7.5f*interval,8,104,172);
                colours.addContinuousColourRule(min+8.5f*interval,8,64,129);
                colours.addContinuousColourRule(max,              8,64,129);
                break;
            case BU_GN:
                colours.name = "BuGn";
                interval = range/9f;
                colours.addContinuousColourRule(min,              247,252,253);
                colours.addContinuousColourRule(min+0.5f*interval,247,252,253);
                colours.addContinuousColourRule(min+1.5f*interval,229,245,249);
                colours.addContinuousColourRule(min+2.5f*interval,204,236,230);
                colours.addContinuousColourRule(min+3.5f*interval,153,216,201);
                colours.addContinuousColourRule(min+4.5f*interval,102,194,164);
                colours.addContinuousColourRule(min+5.5f*interval,65,174,118);
                colours.addContinuousColourRule(min+6.5f*interval,35,139,69);
                colours.addContinuousColourRule(min+7.5f*interval,0,109,44);
                colours.addContinuousColourRule(min+8.5f*interval,0,68,27);
                colours.addContinuousColourRule(max,              0,68,27);
                break;
            case PU_BU_GN:
                colours.name = "PuBuGn";
                interval = range/9f;
                colours.addContinuousColourRule(min,              255,247,251);
                colours.addContinuousColourRule(min+0.5f*interval,255,247,251);
                colours.addContinuousColourRule(min+1.5f*interval,236,226,240);
                colours.addContinuousColourRule(min+2.5f*interval,208,209,230);
                colours.addContinuousColourRule(min+3.5f*interval,166,189,219);
                colours.addContinuousColourRule(min+4.5f*interval,103,169,207);
                colours.addContinuousColourRule(min+5.5f*interval,54,144,192);
                colours.addContinuousColourRule(min+6.5f*interval,2,129,138);
                colours.addContinuousColourRule(min+7.5f*interval,1,108,89);
                colours.addContinuousColourRule(min+8.5f*interval,1,70,54);
                colours.addContinuousColourRule(max,              1,70,54);
                break;
            case PU_BU:
                colours.name = "PuBu";
                interval = range/9f;
                colours.addContinuousColourRule(min,              255,247,251);
                colours.addContinuousColourRule(min+0.5f*interval,255,247,251);
                colours.addContinuousColourRule(min+1.5f*interval,236,231,242);
                colours.addContinuousColourRule(min+2.5f*interval,208,209,230);
                colours.addContinuousColourRule(min+3.5f*interval,166,189,219);
                colours.addContinuousColourRule(min+4.5f*interval,116,169,207);
                colours.addContinuousColourRule(min+5.5f*interval,54,144,192);
                colours.addContinuousColourRule(min+6.5f*interval,5,112,176);
                colours.addContinuousColourRule(min+7.5f*interval,4,90,141);
                colours.addContinuousColourRule(min+8.5f*interval,2,56,88);
                colours.addContinuousColourRule(max,              2,56,88);
                break;
            case BU_PU:
                colours.name = "BuPu";
                interval = range/9f;
                colours.addContinuousColourRule(min,              247,252,253);
                colours.addContinuousColourRule(min+0.5f*interval,247,252,253);
                colours.addContinuousColourRule(min+1.5f*interval,224,236,244);
                colours.addContinuousColourRule(min+2.5f*interval,191,211,230);
                colours.addContinuousColourRule(min+3.5f*interval,158,188,218);
                colours.addContinuousColourRule(min+4.5f*interval,140,150,198);
                colours.addContinuousColourRule(min+5.5f*interval,140,107,177);
                colours.addContinuousColourRule(min+6.5f*interval,136,65,157);
                colours.addContinuousColourRule(min+7.5f*interval,129,15,124);
                colours.addContinuousColourRule(min+8.5f*interval,77,0,75);
                colours.addContinuousColourRule(max,              77,0,75);
                break;
            case RD_PU:
                colours.name = "RdPu";
                interval = range/9f;
                colours.addContinuousColourRule(min,              255,247,243);
                colours.addContinuousColourRule(min+0.5f*interval,255,247,243);
                colours.addContinuousColourRule(min+1.5f*interval,253,224,221);
                colours.addContinuousColourRule(min+2.5f*interval,252,197,192);
                colours.addContinuousColourRule(min+3.5f*interval,250,159,181);
                colours.addContinuousColourRule(min+4.5f*interval,247,104,161);
                colours.addContinuousColourRule(min+5.5f*interval,221,52,151);
                colours.addContinuousColourRule(min+6.5f*interval,174,1,126);
                colours.addContinuousColourRule(min+7.5f*interval,122,1,119);
                colours.addContinuousColourRule(min+8.5f*interval,73,0,106);
                colours.addContinuousColourRule(max,              73,0,106);
                break;
            case PU_RD:
                colours.name = "PuRd";
                interval = range/9f;
                colours.addContinuousColourRule(min,              247,244,249);
                colours.addContinuousColourRule(min+0.5f*interval,247,244,249);
                colours.addContinuousColourRule(min+1.5f*interval,231,225,239);
                colours.addContinuousColourRule(min+2.5f*interval,212,185,218);
                colours.addContinuousColourRule(min+3.5f*interval,201,148,199);
                colours.addContinuousColourRule(min+4.5f*interval,223,101,176);
                colours.addContinuousColourRule(min+5.5f*interval,231,41,138);
                colours.addContinuousColourRule(min+6.5f*interval,206,18,86);
                colours.addContinuousColourRule(min+7.5f*interval,152,0,67);
                colours.addContinuousColourRule(min+8.5f*interval,103,0,31);
                colours.addContinuousColourRule(max,              103,0,31);
                break;
            case OR_RD:
                colours.name = "OrRd";
                interval = range/9f;
                colours.addContinuousColourRule(min,              255,247,236);
                colours.addContinuousColourRule(min+0.5f*interval,255,247,236);
                colours.addContinuousColourRule(min+1.5f*interval,254,232,200);
                colours.addContinuousColourRule(min+2.5f*interval,253,212,158);
                colours.addContinuousColourRule(min+3.5f*interval,253,187,132);
                colours.addContinuousColourRule(min+4.5f*interval,252,141,89);
                colours.addContinuousColourRule(min+5.5f*interval,239,101,72);
                colours.addContinuousColourRule(min+6.5f*interval,215,48,31);
                colours.addContinuousColourRule(min+7.5f*interval,179,0,0);
                colours.addContinuousColourRule(min+8.5f*interval,127,0,0);
                colours.addContinuousColourRule(max,              127,0,0);
                break;
            case YL_OR_RD:
                colours.name = "YlOrRd";
                interval = range/9f;
                colours.addContinuousColourRule(min,              255,255,204);
                colours.addContinuousColourRule(min+0.5f*interval,255,255,204);
                colours.addContinuousColourRule(min+1.5f*interval,255,237,160);
                colours.addContinuousColourRule(min+2.5f*interval,254,217,118);
                colours.addContinuousColourRule(min+3.5f*interval,254,178,76);
                colours.addContinuousColourRule(min+4.5f*interval,253,141,60);
                colours.addContinuousColourRule(min+5.5f*interval,252,78,42);
                colours.addContinuousColourRule(min+6.5f*interval,227,26,28);
                colours.addContinuousColourRule(min+7.5f*interval,189,0,38);
                colours.addContinuousColourRule(min+8.5f*interval,128,0,38);
                colours.addContinuousColourRule(max,              128,0,38);
                break;
            case YL_OR_BR:
                colours.name = "YlOrBr";
                interval = range/9f;
                colours.addContinuousColourRule(min,              255,255,229);
                colours.addContinuousColourRule(min+0.5f*interval,255,255,229);
                colours.addContinuousColourRule(min+1.5f*interval,255,247,188);
                colours.addContinuousColourRule(min+2.5f*interval,254,227,145);
                colours.addContinuousColourRule(min+3.5f*interval,254,196,79);
                colours.addContinuousColourRule(min+4.5f*interval,254,153,41);
                colours.addContinuousColourRule(min+5.5f*interval,236,112,20);
                colours.addContinuousColourRule(min+6.5f*interval,204,76,2);
                colours.addContinuousColourRule(min+7.5f*interval,153,52,4);
                colours.addContinuousColourRule(min+8.5f*interval,102,37,6);
                colours.addContinuousColourRule(max,              102,37,6);
                break;
            case PURPLES:
                colours.name = "purples";
                interval = range/9f;
                colours.addContinuousColourRule(min,              252,251,253);
                colours.addContinuousColourRule(min+0.5f*interval,252,251,253);
                colours.addContinuousColourRule(min+1.5f*interval,239,237,245);
                colours.addContinuousColourRule(min+2.5f*interval,218,218,235);
                colours.addContinuousColourRule(min+3.5f*interval,188,189,220);
                colours.addContinuousColourRule(min+4.5f*interval,158,154,200);
                colours.addContinuousColourRule(min+5.5f*interval,128,125,186);
                colours.addContinuousColourRule(min+6.5f*interval,106,81,163);
                colours.addContinuousColourRule(min+7.5f*interval,84,39,143);
                colours.addContinuousColourRule(min+8.5f*interval,63,0,125);
                colours.addContinuousColourRule(max,              63,0,125);
                break;
            case BLUES:
                colours.name = "blues";
                interval = range/9f;
                colours.addContinuousColourRule(min,              247,251,255);
                colours.addContinuousColourRule(min+0.5f*interval,247,251,255);
                colours.addContinuousColourRule(min+1.5f*interval,222,235,247);
                colours.addContinuousColourRule(min+2.5f*interval,198,219,239);
                colours.addContinuousColourRule(min+3.5f*interval,158,202,225);
                colours.addContinuousColourRule(min+4.5f*interval,107,174,214);
                colours.addContinuousColourRule(min+5.5f*interval,66,146,198);
                colours.addContinuousColourRule(min+6.5f*interval,33,113,181);
                colours.addContinuousColourRule(min+7.5f*interval,8,81,156);
                colours.addContinuousColourRule(min+8.5f*interval,8,48,107);
                colours.addContinuousColourRule(max,              8,48,107);
                break;
            case GREENS:
                colours.name = "greens";
                interval = range/9f;
                colours.addContinuousColourRule(min,              247,252,245);
                colours.addContinuousColourRule(min+0.5f*interval,247,252,245);
                colours.addContinuousColourRule(min+1.5f*interval,229,245,224);
                colours.addContinuousColourRule(min+2.5f*interval,199,233,192);
                colours.addContinuousColourRule(min+3.5f*interval,161,217,155);
                colours.addContinuousColourRule(min+4.5f*interval,116,196,118);
                colours.addContinuousColourRule(min+5.5f*interval,65,171,93);
                colours.addContinuousColourRule(min+6.5f*interval,35,139,69);
                colours.addContinuousColourRule(min+7.5f*interval,0,109,44);
                colours.addContinuousColourRule(min+8.5f*interval,0,68,27);
                colours.addContinuousColourRule(max,              0,68,27);
                break;
            case ORANGES:
                colours.name = "oranges";
                interval = range/9f;
                colours.addContinuousColourRule(min,              255,245,235);
                colours.addContinuousColourRule(min+0.5f*interval,255,245,235);
                colours.addContinuousColourRule(min+1.5f*interval,254,230,206);
                colours.addContinuousColourRule(min+2.5f*interval,253,208,162);
                colours.addContinuousColourRule(min+3.5f*interval,253,174,107);
                colours.addContinuousColourRule(min+4.5f*interval,253,141,60);
                colours.addContinuousColourRule(min+5.5f*interval,241,105,19);
                colours.addContinuousColourRule(min+6.5f*interval,217,72,1);
                colours.addContinuousColourRule(min+7.5f*interval,166,54,3);
                colours.addContinuousColourRule(min+8.5f*interval,127,39,4);
                colours.addContinuousColourRule(max,              127,39,4);
                break;
            case REDS:
                colours.name = "reds";
                interval = range/9f;
                colours.addContinuousColourRule(min,              255,245,240);
                colours.addContinuousColourRule(min+0.5f*interval,255,245,240);
                colours.addContinuousColourRule(min+1.5f*interval,254,224,210);
                colours.addContinuousColourRule(min+2.5f*interval,252,187,161);
                colours.addContinuousColourRule(min+3.5f*interval,252,146,114);
                colours.addContinuousColourRule(min+4.5f*interval,251,106,74);
                colours.addContinuousColourRule(min+5.5f*interval,239,59,44);
                colours.addContinuousColourRule(min+6.5f*interval,203,24,29);
                colours.addContinuousColourRule(min+7.5f*interval,165,15,21);
                colours.addContinuousColourRule(min+8.5f*interval,103,0,13);
                colours.addContinuousColourRule(max,              103,0,13);
                break;
            case GREYS:
                colours.name = "greys";
                interval = range/9f;
                colours.addContinuousColourRule(min,              255,255,255);
                colours.addContinuousColourRule(min+0.5f*interval,255,255,255);
                colours.addContinuousColourRule(min+1.5f*interval,240,240,240);
                colours.addContinuousColourRule(min+2.5f*interval,217,217,217);
                colours.addContinuousColourRule(min+3.5f*interval,189,189,189);
                colours.addContinuousColourRule(min+4.5f*interval,150,150,150);
                colours.addContinuousColourRule(min+5.5f*interval,115,115,115);
                colours.addContinuousColourRule(min+6.5f*interval,82,82,82);
                colours.addContinuousColourRule(min+7.5f*interval,37,37,37);
                colours.addContinuousColourRule(min+8.5f*interval,0,0,0);
                colours.addContinuousColourRule(max,              0,0,0);
                break;
            case PU_OR:
                colours.name = "PuOr";
                interval = range/11f;
                colours.addContinuousColourRule(min,              127,59,8);
                colours.addContinuousColourRule(min+0.5f*interval,127,59,8);
                colours.addContinuousColourRule(min+1.5f*interval,179,88,6);
                colours.addContinuousColourRule(min+2.5f*interval,224,130,20);
                colours.addContinuousColourRule(min+3.5f*interval,253,184,99);
                colours.addContinuousColourRule(min+4.5f*interval,254,224,182);
                colours.addContinuousColourRule(min+5.5f*interval,247,247,247);
                colours.addContinuousColourRule(min+6.5f*interval,216,218,235);
                colours.addContinuousColourRule(min+7.5f*interval,178,171,210);
                colours.addContinuousColourRule(min+8.5f*interval,128,115,172);
                colours.addContinuousColourRule(min+9.5f*interval,84,39,136);
                colours.addContinuousColourRule(min+10.5f*interval,45,0,75);
                colours.addContinuousColourRule(max,              45,0,75);
                break;
            case BR_B_G:
                colours.name = "BrBG";
                interval = range/11f;
                colours.addContinuousColourRule(min,              84,48,5);
                colours.addContinuousColourRule(min+0.5f*interval,84,48,5);
                colours.addContinuousColourRule(min+1.5f*interval,140,81,10);
                colours.addContinuousColourRule(min+2.5f*interval,191,129,45);
                colours.addContinuousColourRule(min+3.5f*interval,223,194,125);
                colours.addContinuousColourRule(min+4.5f*interval,246,232,195);
                colours.addContinuousColourRule(min+5.5f*interval,245,245,245);
                colours.addContinuousColourRule(min+6.5f*interval,199,234,229);
                colours.addContinuousColourRule(min+7.5f*interval,128,205,193);
                colours.addContinuousColourRule(min+8.5f*interval,53,151,143);
                colours.addContinuousColourRule(min+9.5f*interval,1,102,94);
                colours.addContinuousColourRule(min+10.5f*interval,0,60,48);
                colours.addContinuousColourRule(max,              0,60,48);
                break;
            case P_R_GN:
                colours.name = "PRGn";
                interval = range/11f;
                colours.addContinuousColourRule(min,              64,0,75);
                colours.addContinuousColourRule(min+0.5f*interval,64,0,75);
                colours.addContinuousColourRule(min+1.5f*interval,118,42,131);
                colours.addContinuousColourRule(min+2.5f*interval,153,112,171);
                colours.addContinuousColourRule(min+3.5f*interval,194,165,207);
                colours.addContinuousColourRule(min+4.5f*interval,231,212,232);
                colours.addContinuousColourRule(min+5.5f*interval,247,247,247);
                colours.addContinuousColourRule(min+6.5f*interval,217,240,211);
                colours.addContinuousColourRule(min+7.5f*interval,166,219,160);
                colours.addContinuousColourRule(min+8.5f*interval,90,174,97);
                colours.addContinuousColourRule(min+9.5f*interval,27,120,55);
                colours.addContinuousColourRule(min+10.5f*interval,0,68,27);
                colours.addContinuousColourRule(max,              0,68,27);
                break;
            case PI_Y_G:
                colours.name = "PiYG";
                interval = range/11f;
                colours.addContinuousColourRule(min,              142,1,82);
                colours.addContinuousColourRule(min+0.5f*interval,142,1,82);
                colours.addContinuousColourRule(min+1.5f*interval,197,27,125);
                colours.addContinuousColourRule(min+2.5f*interval,222,119,174);
                colours.addContinuousColourRule(min+3.5f*interval,241,182,218);
                colours.addContinuousColourRule(min+4.5f*interval,253,224,239);
                colours.addContinuousColourRule(min+5.5f*interval,247,247,247);
                colours.addContinuousColourRule(min+6.5f*interval,230,245,208);
                colours.addContinuousColourRule(min+7.5f*interval,184,225,134);
                colours.addContinuousColourRule(min+8.5f*interval,127,188,65);
                colours.addContinuousColourRule(min+9.5f*interval,77,146,33);
                colours.addContinuousColourRule(min+10.5f*interval,39,100,25);
                colours.addContinuousColourRule(max,              39,100,25);
                break;
            case RD_BU:
                colours.name = "RdBu";
                interval = range/11f;
                colours.addContinuousColourRule(min,              103,0,31);
                colours.addContinuousColourRule(min+0.5f*interval,103,0,31);
                colours.addContinuousColourRule(min+1.5f*interval,178,24,43);
                colours.addContinuousColourRule(min+2.5f*interval,214,96,77);
                colours.addContinuousColourRule(min+3.5f*interval,244,165,130);
                colours.addContinuousColourRule(min+4.5f*interval,253,219,199);
                colours.addContinuousColourRule(min+5.5f*interval,247,247,247);
                colours.addContinuousColourRule(min+6.5f*interval,209,229,240);
                colours.addContinuousColourRule(min+7.5f*interval,146,197,222);
                colours.addContinuousColourRule(min+8.5f*interval,67,147,195);
                colours.addContinuousColourRule(min+9.5f*interval,33,102,172);
                colours.addContinuousColourRule(min+10.5f*interval,5,48,97);
                colours.addContinuousColourRule(max,              5,48,97);
                break;
            case RD_GY:
                colours.name = "RdGy";
                interval = range/11f;
                colours.addContinuousColourRule(min,              103,0,31);
                colours.addContinuousColourRule(min+0.5f*interval,103,0,31);
                colours.addContinuousColourRule(min+1.5f*interval,178,24,43);
                colours.addContinuousColourRule(min+2.5f*interval,214,96,77);
                colours.addContinuousColourRule(min+3.5f*interval,244,165,130);
                colours.addContinuousColourRule(min+4.5f*interval,253,219,199);
                colours.addContinuousColourRule(min+5.5f*interval,255,255,255);
                colours.addContinuousColourRule(min+6.5f*interval,224,224,224);
                colours.addContinuousColourRule(min+7.5f*interval,186,186,186);
                colours.addContinuousColourRule(min+8.5f*interval,135,135,135);
                colours.addContinuousColourRule(min+9.5f*interval,77,77,77);
                colours.addContinuousColourRule(min+10.5f*interval,26,26,26);
                colours.addContinuousColourRule(max,              26,26,26);
                break;
            case RD_YL_BU:
                colours.name = "RdYlBu";
                interval = range/11f;
                colours.addContinuousColourRule(min,              165,0,38);
                colours.addContinuousColourRule(min+0.5f*interval,165,0,38);
                colours.addContinuousColourRule(min+1.5f*interval,215,48,39);
                colours.addContinuousColourRule(min+2.5f*interval,244,109,67);
                colours.addContinuousColourRule(min+3.5f*interval,253,174,97);
                colours.addContinuousColourRule(min+4.5f*interval,254,224,144);
                colours.addContinuousColourRule(min+5.5f*interval,255,255,191);
                colours.addContinuousColourRule(min+6.5f*interval,224,243,248);
                colours.addContinuousColourRule(min+7.5f*interval,171,217,233);
                colours.addContinuousColourRule(min+8.5f*interval,116,173,209);
                colours.addContinuousColourRule(min+9.5f*interval,69,117,180);
                colours.addContinuousColourRule(min+10.5f*interval,49,54,149);
                colours.addContinuousColourRule(max,              49,54,149);
                break;
            case SPECTRAL:
                colours.name = "spectral";
                interval = range/11f;
                colours.addContinuousColourRule(min,              158,1,66);
                colours.addContinuousColourRule(min+0.5f*interval,158,1,66);
                colours.addContinuousColourRule(min+1.5f*interval,213,62,79);
                colours.addContinuousColourRule(min+2.5f*interval,244,109,67);
                colours.addContinuousColourRule(min+3.5f*interval,253,174,97);
                colours.addContinuousColourRule(min+4.5f*interval,254,224,139);
                colours.addContinuousColourRule(min+5.5f*interval,255,255,191);
                colours.addContinuousColourRule(min+6.5f*interval,230,245,152);
                colours.addContinuousColourRule(min+7.5f*interval,171,221,164);
                colours.addContinuousColourRule(min+8.5f*interval,102,194,165);
                colours.addContinuousColourRule(min+9.5f*interval,50,136,189);
                colours.addContinuousColourRule(min+10.5f*interval,94,79,162);
                colours.addContinuousColourRule(max,              94,79,162);
                break;
            case RD_YL_GN:
                colours.name = "RdYlGn";
                interval = range/11f;
                colours.addContinuousColourRule(min,              165,0,38);
                colours.addContinuousColourRule(min+0.5f*interval,165,0,38);
                colours.addContinuousColourRule(min+1.5f*interval,215,48,39);
                colours.addContinuousColourRule(min+2.5f*interval,244,109,67);
                colours.addContinuousColourRule(min+3.5f*interval,253,174,97);
                colours.addContinuousColourRule(min+4.5f*interval,254,224,139);
                colours.addContinuousColourRule(min+5.5f*interval,255,255,191);
                colours.addContinuousColourRule(min+6.5f*interval,217,239,139);
                colours.addContinuousColourRule(min+7.5f*interval,166,217,106);
                colours.addContinuousColourRule(min+8.5f*interval,102,189,99);
                colours.addContinuousColourRule(min+9.5f*interval,26,152,80);
                colours.addContinuousColourRule(min+10.5f*interval,0,104,55);
                colours.addContinuousColourRule(max,              0,104,55);
                break;
                
            case SET1_3:
                colours.name = "Set1_3";
                colours.addDiscreteColourRule(min,228,26,28);
                colours.addDiscreteColourRule(min+1,55,126,184);
                colours.addDiscreteColourRule(min+2,77,175,74);
                colours.setIsDiscrete(true);
                break;
            case SET1_4:
                colours.name = "Set1_4";
                colours.addDiscreteColourRule(min,228,26,28);
                colours.addDiscreteColourRule(min+1,55,126,184);
                colours.addDiscreteColourRule(min+2,77,175,74);
                colours.addDiscreteColourRule(min+3,152,78,163);
                colours.setIsDiscrete(true);
                break;
            case SET1_5:    
                colours.name = "Set1_5";
                colours.addDiscreteColourRule(min,228,26,28);
                colours.addDiscreteColourRule(min+1,55,126,184);
                colours.addDiscreteColourRule(min+2,77,175,74);
                colours.addDiscreteColourRule(min+3,152,78,163);
                colours.addDiscreteColourRule(min+4,255,127,0);
                colours.setIsDiscrete(true);
                break;
            case SET1_6:    
                colours.name = "Set1_6";
                colours.addDiscreteColourRule(min,228,26,28);
                colours.addDiscreteColourRule(min+1,55,126,184);
                colours.addDiscreteColourRule(min+2,77,175,74);
                colours.addDiscreteColourRule(min+3,152,78,163);
                colours.addDiscreteColourRule(min+4,255,127,0);
                colours.addDiscreteColourRule(min+5,255,255,51);
                colours.setIsDiscrete(true);
                break;
            case SET1_7:
                colours.name = "Set1_7";
                colours.addDiscreteColourRule(min,228,26,28);
                colours.addDiscreteColourRule(min+1,55,126,184);
                colours.addDiscreteColourRule(min+2,77,175,74);
                colours.addDiscreteColourRule(min+3,152,78,163);
                colours.addDiscreteColourRule(min+4,255,127,0);
                colours.addDiscreteColourRule(min+5,255,255,51);
                colours.addDiscreteColourRule(min+6,166,86,40);
                colours.setIsDiscrete(true);
                break;
            case SET1_8:    
                colours.name = "Set1_8";
                colours.addDiscreteColourRule(min,228,26,28);
                colours.addDiscreteColourRule(min+1,55,126,184);
                colours.addDiscreteColourRule(min+2,77,175,74);
                colours.addDiscreteColourRule(min+3,152,78,163);
                colours.addDiscreteColourRule(min+4,255,127,0);
                colours.addDiscreteColourRule(min+5,255,255,51);
                colours.addDiscreteColourRule(min+6,166,86,40);
                colours.addDiscreteColourRule(min+7,247,129,191);
                colours.setIsDiscrete(true);
                break;
            case SET1_9:    
                colours.name = "Set1_9";
                colours.addDiscreteColourRule(min,228,26,28);
                colours.addDiscreteColourRule(min+1,55,126,184);
                colours.addDiscreteColourRule(min+2,77,175,74);
                colours.addDiscreteColourRule(min+3,152,78,163);
                colours.addDiscreteColourRule(min+4,255,127,0);
                colours.addDiscreteColourRule(min+5,255,255,51);
                colours.addDiscreteColourRule(min+6,166,86,40);
                colours.addDiscreteColourRule(min+7,247,129,191);
                colours.addDiscreteColourRule(min+8,153,153,153);
                colours.setIsDiscrete(true);
                break;
                
            case SET2_3:
                colours.name = "Set2_3";
                colours.addDiscreteColourRule(min,102,194,165);
                colours.addDiscreteColourRule(min+1,252,141,98);
                colours.addDiscreteColourRule(min+2,141,160,203);
                colours.setIsDiscrete(true);
                break;
            case SET2_4:
                colours.name = "Set2_4";
                colours.addDiscreteColourRule(min,102,194,165);
                colours.addDiscreteColourRule(min+1,252,141,98);
                colours.addDiscreteColourRule(min+2,141,160,203);
                colours.addDiscreteColourRule(min+3,231,138,195);
                colours.setIsDiscrete(true);
                break;
            case SET2_5:
                colours.name = "Set2_5";
                colours.addDiscreteColourRule(min,102,194,165);
                colours.addDiscreteColourRule(min+1,252,141,98);
                colours.addDiscreteColourRule(min+2,141,160,203);
                colours.addDiscreteColourRule(min+3,231,138,195);
                colours.addDiscreteColourRule(min+4,166,216,84);
                colours.setIsDiscrete(true);
                break;
            case SET2_6:    
                colours.name = "Set2_6";
                colours.addDiscreteColourRule(min,102,194,165);
                colours.addDiscreteColourRule(min+1,252,141,98);
                colours.addDiscreteColourRule(min+2,141,160,203);
                colours.addDiscreteColourRule(min+3,231,138,195);
                colours.addDiscreteColourRule(min+4,166,216,84);
                colours.addDiscreteColourRule(min+5,255,217,47);
                colours.setIsDiscrete(true);
                break;
            case SET2_7:
                colours.name = "Set2_7";
                colours.addDiscreteColourRule(min,102,194,165);
                colours.addDiscreteColourRule(min+1,252,141,98);
                colours.addDiscreteColourRule(min+2,141,160,203);
                colours.addDiscreteColourRule(min+3,231,138,195);
                colours.addDiscreteColourRule(min+4,166,216,84);
                colours.addDiscreteColourRule(min+5,255,217,47);
                colours.addDiscreteColourRule(min+6,229,196,148);
                colours.setIsDiscrete(true);
                break;
            case SET2_8:
                colours.name = "Set2_8";
                colours.addDiscreteColourRule(min,102,194,165);
                colours.addDiscreteColourRule(min+1,252,141,98);
                colours.addDiscreteColourRule(min+2,141,160,203);
                colours.addDiscreteColourRule(min+3,231,138,195);
                colours.addDiscreteColourRule(min+4,166,216,84);
                colours.addDiscreteColourRule(min+5,255,217,47);
                colours.addDiscreteColourRule(min+6,229,196,148);
                colours.addDiscreteColourRule(min+7,179,179,179);
                colours.setIsDiscrete(true);
                break;
            
            case SET3_3:
                colours.name = "Set3_3";
                colours.addDiscreteColourRule(min,141,211,199);
                colours.addDiscreteColourRule(min+1,255,255,179);
                colours.addDiscreteColourRule(min+2,190,186,218);
                colours.setIsDiscrete(true);
                break;
            case SET3_4:
                colours.name = "Set3_4";
                colours.addDiscreteColourRule(min,141,211,199);
                colours.addDiscreteColourRule(min+1,255,255,179);
                colours.addDiscreteColourRule(min+2,190,186,218);
                colours.addDiscreteColourRule(min+3,251,128,114);
                colours.setIsDiscrete(true);
                break;
            case SET3_5:    
                colours.name = "Set3_5";
                colours.addDiscreteColourRule(min,141,211,199);
                colours.addDiscreteColourRule(min+1,255,255,179);
                colours.addDiscreteColourRule(min+2,190,186,218);
                colours.addDiscreteColourRule(min+3,251,128,114);
                colours.addDiscreteColourRule(min+4,128,177,211);
                colours.setIsDiscrete(true);
                break;
            case SET3_6:    
                colours.name = "Set3_6";
                colours.addDiscreteColourRule(min,141,211,199);
                colours.addDiscreteColourRule(min+1,255,255,179);
                colours.addDiscreteColourRule(min+2,190,186,218);
                colours.addDiscreteColourRule(min+3,251,128,114);
                colours.addDiscreteColourRule(min+4,128,177,211);
                colours.addDiscreteColourRule(min+5,253,180,98);
                colours.setIsDiscrete(true);
                break;
            case SET3_7:    
                colours.name = "Set3_7";
                colours.addDiscreteColourRule(min,141,211,199);
                colours.addDiscreteColourRule(min+1,255,255,179);
                colours.addDiscreteColourRule(min+2,190,186,218);
                colours.addDiscreteColourRule(min+3,251,128,114);
                colours.addDiscreteColourRule(min+4,128,177,211);
                colours.addDiscreteColourRule(min+5,253,180,98);
                colours.addDiscreteColourRule(min+6,179,222,105);
                colours.setIsDiscrete(true);
                break;
            case SET3_8:    
                colours.name = "Set3_8";
                colours.addDiscreteColourRule(min,141,211,199);
                colours.addDiscreteColourRule(min+1,255,255,179);
                colours.addDiscreteColourRule(min+2,190,186,218);
                colours.addDiscreteColourRule(min+3,251,128,114);
                colours.addDiscreteColourRule(min+4,128,177,211);
                colours.addDiscreteColourRule(min+5,253,180,98);
                colours.addDiscreteColourRule(min+6,179,222,105);
                colours.addDiscreteColourRule(min+7,252,205,229);
                colours.setIsDiscrete(true);
                break;
            case SET3_9:
                colours.name = "Set3_9";
                colours.addDiscreteColourRule(min,141,211,199);
                colours.addDiscreteColourRule(min+1,255,255,179);
                colours.addDiscreteColourRule(min+2,190,186,218);
                colours.addDiscreteColourRule(min+3,251,128,114);
                colours.addDiscreteColourRule(min+4,128,177,211);
                colours.addDiscreteColourRule(min+5,253,180,98);
                colours.addDiscreteColourRule(min+6,179,222,105);
                colours.addDiscreteColourRule(min+7,252,205,229);
                colours.addDiscreteColourRule(min+8,217,217,217);
                colours.setIsDiscrete(true);
                break;
            case SET3_10:   
                colours.name = "Set3_10";
                colours.addDiscreteColourRule(min,141,211,199);
                colours.addDiscreteColourRule(min+1,255,255,179);
                colours.addDiscreteColourRule(min+2,190,186,218);
                colours.addDiscreteColourRule(min+3,251,128,114);
                colours.addDiscreteColourRule(min+4,128,177,211);
                colours.addDiscreteColourRule(min+5,253,180,98);
                colours.addDiscreteColourRule(min+6,179,222,105);
                colours.addDiscreteColourRule(min+7,252,205,229);
                colours.addDiscreteColourRule(min+8,217,217,217);
                colours.addDiscreteColourRule(min+9,188,128,189);
                colours.setIsDiscrete(true);
                break;
            case SET3_11:   
                colours.name = "Set3_11";
                colours.addDiscreteColourRule(min,141,211,199);
                colours.addDiscreteColourRule(min+1,255,255,179);
                colours.addDiscreteColourRule(min+2,190,186,218);
                colours.addDiscreteColourRule(min+3,251,128,114);
                colours.addDiscreteColourRule(min+4,128,177,211);
                colours.addDiscreteColourRule(min+5,253,180,98);
                colours.addDiscreteColourRule(min+6,179,222,105);
                colours.addDiscreteColourRule(min+7,252,205,229);
                colours.addDiscreteColourRule(min+8,217,217,217);
                colours.addDiscreteColourRule(min+9,188,128,189);
                colours.addDiscreteColourRule(min+10,204,235,197);
                colours.setIsDiscrete(true);
                break;
            case SET3_12:
                colours.name = "Set3_12";
                colours.addDiscreteColourRule(min,141,211,199);
                colours.addDiscreteColourRule(min+1,255,255,179);
                colours.addDiscreteColourRule(min+2,190,186,218);
                colours.addDiscreteColourRule(min+3,251,128,114);
                colours.addDiscreteColourRule(min+4,128,177,211);
                colours.addDiscreteColourRule(min+5,253,180,98);
                colours.addDiscreteColourRule(min+6,179,222,105);
                colours.addDiscreteColourRule(min+7,252,205,229);
                colours.addDiscreteColourRule(min+8,217,217,217);
                colours.addDiscreteColourRule(min+9,188,128,189);
                colours.addDiscreteColourRule(min+10,204,235,197);
                colours.addDiscreteColourRule(min+11,255,237,111);
                colours.setIsDiscrete(true);
                break;
        
            case PASTEL1_3:
                colours.name = "Pastel1_3";
                colours.addDiscreteColourRule(min,251,180,174);
                colours.addDiscreteColourRule(min+1,179,205,227);
                colours.addDiscreteColourRule(min+2,204,235,197);
                colours.setIsDiscrete(true);
                break;
            case PASTEL1_4:
                colours.name = "Pastel1_4";
                colours.addDiscreteColourRule(min,251,180,174);
                colours.addDiscreteColourRule(min+1,179,205,227);
                colours.addDiscreteColourRule(min+2,204,235,197);
                colours.addDiscreteColourRule(min+3,222,203,228);
                colours.setIsDiscrete(true);
                break;
            case PASTEL1_5:
                colours.name = "Pastel1_5";
                colours.addDiscreteColourRule(min,251,180,174);
                colours.addDiscreteColourRule(min+1,179,205,227);
                colours.addDiscreteColourRule(min+2,204,235,197);
                colours.addDiscreteColourRule(min+3,222,203,228);
                colours.addDiscreteColourRule(min+4,254,217,166);
                colours.setIsDiscrete(true);
                break;
            case PASTEL1_6:
                colours.name = "Pastel1_6";
                colours.addDiscreteColourRule(min,251,180,174);
                colours.addDiscreteColourRule(min+1,179,205,227);
                colours.addDiscreteColourRule(min+2,204,235,197);
                colours.addDiscreteColourRule(min+3,222,203,228);
                colours.addDiscreteColourRule(min+4,254,217,166);
                colours.addDiscreteColourRule(min+5,255,255,204);
                colours.setIsDiscrete(true);
                break;
            case PASTEL1_7:
                colours.name = "Pastel1_7";
                colours.addDiscreteColourRule(min,251,180,174);
                colours.addDiscreteColourRule(min+1,179,205,227);
                colours.addDiscreteColourRule(min+2,204,235,197);
                colours.addDiscreteColourRule(min+3,222,203,228);
                colours.addDiscreteColourRule(min+4,254,217,166);
                colours.addDiscreteColourRule(min+5,255,255,204);
                colours.addDiscreteColourRule(min+6,229,216,189);
                colours.setIsDiscrete(true);
                break;
            case PASTEL1_8:
                colours.name = "Pastel1_8";
                colours.addDiscreteColourRule(min,251,180,174);
                colours.addDiscreteColourRule(min+1,179,205,227);
                colours.addDiscreteColourRule(min+2,204,235,197);
                colours.addDiscreteColourRule(min+3,222,203,228);
                colours.addDiscreteColourRule(min+4,254,217,166);
                colours.addDiscreteColourRule(min+5,255,255,204);
                colours.addDiscreteColourRule(min+6,229,216,189);
                colours.addDiscreteColourRule(min+7,253,218,236);
                colours.setIsDiscrete(true);
                break;
            case PASTEL1_9:
                colours.name = "Pastel1_9";
                colours.addDiscreteColourRule(min,251,180,174);
                colours.addDiscreteColourRule(min+1,179,205,227);
                colours.addDiscreteColourRule(min+2,204,235,197);
                colours.addDiscreteColourRule(min+3,222,203,228);
                colours.addDiscreteColourRule(min+4,254,217,166);
                colours.addDiscreteColourRule(min+5,255,255,204);
                colours.addDiscreteColourRule(min+6,229,216,189);
                colours.addDiscreteColourRule(min+7,253,218,236);
                colours.addDiscreteColourRule(min+8,242,242,242);
                colours.setIsDiscrete(true);
                break;
                
            case PASTEL2_3:
                colours.name = "Pastel2_3";
                colours.addDiscreteColourRule(min,179,226,205);
                colours.addDiscreteColourRule(min+1,253,205,172);
                colours.addDiscreteColourRule(min+2,203,213,232);
                colours.setIsDiscrete(true);
                break;
            case PASTEL2_4:
                colours.name = "Pastel2_4";
                colours.addDiscreteColourRule(min,179,226,205);
                colours.addDiscreteColourRule(min+1,253,205,172);
                colours.addDiscreteColourRule(min+2,203,213,232);
                colours.addDiscreteColourRule(min+3,244,202,228);
                colours.setIsDiscrete(true);
                break;
            case PASTEL2_5:
                colours.name = "Pastel2_5";
                colours.addDiscreteColourRule(min,179,226,205);
                colours.addDiscreteColourRule(min+1,253,205,172);
                colours.addDiscreteColourRule(min+2,203,213,232);
                colours.addDiscreteColourRule(min+3,244,202,228);
                colours.addDiscreteColourRule(min+4,230,245,201);
                colours.setIsDiscrete(true);
                break;
            case PASTEL2_6:
                colours.name = "Pastel2_6";
                colours.addDiscreteColourRule(min,179,226,205);
                colours.addDiscreteColourRule(min+1,253,205,172);
                colours.addDiscreteColourRule(min+2,203,213,232);
                colours.addDiscreteColourRule(min+3,244,202,228);
                colours.addDiscreteColourRule(min+4,230,245,201);
                colours.addDiscreteColourRule(min+5,255,242,174);
                colours.setIsDiscrete(true);
                break;
            case PASTEL2_7:
                colours.name = "Pastel2_7";
                colours.addDiscreteColourRule(min,179,226,205);
                colours.addDiscreteColourRule(min+1,253,205,172);
                colours.addDiscreteColourRule(min+2,203,213,232);
                colours.addDiscreteColourRule(min+3,244,202,228);
                colours.addDiscreteColourRule(min+4,230,245,201);
                colours.addDiscreteColourRule(min+5,255,242,174);
                colours.addDiscreteColourRule(min+6,241,226,204);   
                colours.setIsDiscrete(true);
                break;
            case PASTEL2_8:
                colours.name = "Pastel2_8";
                colours.addDiscreteColourRule(min,179,226,205);
                colours.addDiscreteColourRule(min+1,253,205,172);
                colours.addDiscreteColourRule(min+2,203,213,232);
                colours.addDiscreteColourRule(min+3,244,202,228);
                colours.addDiscreteColourRule(min+4,230,245,201);
                colours.addDiscreteColourRule(min+5,255,242,174);
                colours.addDiscreteColourRule(min+6,241,226,204);
                colours.addDiscreteColourRule(min+7,204,204,204);
                colours.setIsDiscrete(true);
                break;  
                
            case DARK2_3:
                colours.name = "Dark2_3";
                colours.addDiscreteColourRule(min,27,158,119);
                colours.addDiscreteColourRule(min+1,217,95,2);
                colours.addDiscreteColourRule(min+2,117,112,179);
                colours.setIsDiscrete(true);
                break;
            case DARK2_4:
                colours.name = "Dark2_4";
                colours.addDiscreteColourRule(min,27,158,119);
                colours.addDiscreteColourRule(min+1,217,95,2);
                colours.addDiscreteColourRule(min+2,117,112,179);
                colours.addDiscreteColourRule(min+3,231,41,138);
                colours.setIsDiscrete(true);
                break;
            case DARK2_5:
                colours.name = "Dark2_5";
                colours.addDiscreteColourRule(min,27,158,119);
                colours.addDiscreteColourRule(min+1,217,95,2);
                colours.addDiscreteColourRule(min+2,117,112,179);
                colours.addDiscreteColourRule(min+3,231,41,138);
                colours.addDiscreteColourRule(min+4,102,166,30);
                colours.setIsDiscrete(true);
                break;
            case DARK2_6:
                colours.name = "Dark2_6";
                colours.addDiscreteColourRule(min,27,158,119);
                colours.addDiscreteColourRule(min+1,217,95,2);
                colours.addDiscreteColourRule(min+2,117,112,179);
                colours.addDiscreteColourRule(min+3,231,41,138);
                colours.addDiscreteColourRule(min+4,102,166,30);
                colours.addDiscreteColourRule(min+5,230,171,2);
                colours.setIsDiscrete(true);
                break;
            case DARK2_7:
                colours.name = "Dark2_7";
                colours.addDiscreteColourRule(min,27,158,119);
                colours.addDiscreteColourRule(min+1,217,95,2);
                colours.addDiscreteColourRule(min+2,117,112,179);
                colours.addDiscreteColourRule(min+3,231,41,138);
                colours.addDiscreteColourRule(min+4,102,166,30);
                colours.addDiscreteColourRule(min+5,230,171,2);
                colours.addDiscreteColourRule(min+6,166,118,29);
                colours.setIsDiscrete(true);
                break;
            case DARK2_8:
                colours.name = "Dark2_8";
                colours.addDiscreteColourRule(min,27,158,119);
                colours.addDiscreteColourRule(min+1,217,95,2);
                colours.addDiscreteColourRule(min+2,117,112,179);
                colours.addDiscreteColourRule(min+3,231,41,138);
                colours.addDiscreteColourRule(min+4,102,166,30);
                colours.addDiscreteColourRule(min+5,230,171,2);
                colours.addDiscreteColourRule(min+6,166,118,29);
                colours.addDiscreteColourRule(min+7,102,102,102);
                colours.setIsDiscrete(true);
                break;
            
            case PAIRED_3:
                colours.name = "Paired_3";
                colours.addDiscreteColourRule(min,166,206,227);
                colours.addDiscreteColourRule(min+1,31,120,180);
                colours.addDiscreteColourRule(min+2,178,223,138);   
                colours.setIsDiscrete(true);
                break;
            case PAIRED_4:
                colours.name = "Paired_4";
                colours.addDiscreteColourRule(min,166,206,227);
                colours.addDiscreteColourRule(min+1,31,120,180);
                colours.addDiscreteColourRule(min+2,178,223,138);
                colours.addDiscreteColourRule(min+3,51,160,44);
                colours.setIsDiscrete(true);
                break;
            case PAIRED_5:
                colours.name = "Paired_5";
                colours.addDiscreteColourRule(min,166,206,227);
                colours.addDiscreteColourRule(min+1,31,120,180);
                colours.addDiscreteColourRule(min+2,178,223,138);
                colours.addDiscreteColourRule(min+3,51,160,44);
                colours.addDiscreteColourRule(min+4,251,154,153);
                colours.setIsDiscrete(true);
                break;
            case PAIRED_6:
                colours.name = "Paired_6";
                colours.addDiscreteColourRule(min,166,206,227);
                colours.addDiscreteColourRule(min+1,31,120,180);
                colours.addDiscreteColourRule(min+2,178,223,138);
                colours.addDiscreteColourRule(min+3,51,160,44);
                colours.addDiscreteColourRule(min+4,251,154,153);
                colours.addDiscreteColourRule(min+5,227,26,28);
                colours.setIsDiscrete(true);
                break;
            case PAIRED_7:
                colours.name = "Paired_7";
                colours.addDiscreteColourRule(min,166,206,227);
                colours.addDiscreteColourRule(min+1,31,120,180);
                colours.addDiscreteColourRule(min+2,178,223,138);
                colours.addDiscreteColourRule(min+3,51,160,44);
                colours.addDiscreteColourRule(min+4,251,154,153);
                colours.addDiscreteColourRule(min+5,227,26,28);
                colours.addDiscreteColourRule(min+6,253,191,111);
                colours.setIsDiscrete(true);
                break;
            case PAIRED_8:
                colours.name = "Paired_8";
                colours.addDiscreteColourRule(min,166,206,227);
                colours.addDiscreteColourRule(min+1,31,120,180);
                colours.addDiscreteColourRule(min+2,178,223,138);
                colours.addDiscreteColourRule(min+3,51,160,44);
                colours.addDiscreteColourRule(min+4,251,154,153);
                colours.addDiscreteColourRule(min+5,227,26,28);
                colours.addDiscreteColourRule(min+6,253,191,111);
                colours.addDiscreteColourRule(min+7,255,127,0);
                colours.setIsDiscrete(true);
                break;
            case PAIRED_9:
                colours.name = "Paired_9";
                colours.addDiscreteColourRule(min,166,206,227);
                colours.addDiscreteColourRule(min+1,31,120,180);
                colours.addDiscreteColourRule(min+2,178,223,138);
                colours.addDiscreteColourRule(min+3,51,160,44);
                colours.addDiscreteColourRule(min+4,251,154,153);
                colours.addDiscreteColourRule(min+5,227,26,28);
                colours.addDiscreteColourRule(min+6,253,191,111);
                colours.addDiscreteColourRule(min+7,255,127,0);
                colours.addDiscreteColourRule(min+8,202,178,214);
                colours.setIsDiscrete(true);
                break;
            case PAIRED_10:
                colours.name = "Paired_10";
                colours.addDiscreteColourRule(min,166,206,227);
                colours.addDiscreteColourRule(min+1,31,120,180);
                colours.addDiscreteColourRule(min+2,178,223,138);
                colours.addDiscreteColourRule(min+3,51,160,44);
                colours.addDiscreteColourRule(min+4,251,154,153);
                colours.addDiscreteColourRule(min+5,227,26,28);
                colours.addDiscreteColourRule(min+6,253,191,111);
                colours.addDiscreteColourRule(min+7,255,127,0);
                colours.addDiscreteColourRule(min+8,202,178,214);
                colours.addDiscreteColourRule(min+9,106,61,154);
                colours.setIsDiscrete(true);
                break;
            case PAIRED_11:
                colours.name = "Paired_11";
                colours.addDiscreteColourRule(min,166,206,227);
                colours.addDiscreteColourRule(min+1,31,120,180);
                colours.addDiscreteColourRule(min+2,178,223,138);
                colours.addDiscreteColourRule(min+3,51,160,44);
                colours.addDiscreteColourRule(min+4,251,154,153);
                colours.addDiscreteColourRule(min+5,227,26,28);
                colours.addDiscreteColourRule(min+6,253,191,111);
                colours.addDiscreteColourRule(min+7,255,127,0);
                colours.addDiscreteColourRule(min+8,202,178,214);
                colours.addDiscreteColourRule(min+9,106,61,154);
                colours.addDiscreteColourRule(min+10,255,255,153);
                colours.setIsDiscrete(true);
                break;
            case PAIRED_12:
                colours.name = "Paired_12";
                colours.addDiscreteColourRule(min,166,206,227);
                colours.addDiscreteColourRule(min+1,31,120,180);
                colours.addDiscreteColourRule(min+2,178,223,138);
                colours.addDiscreteColourRule(min+3,51,160,44);
                colours.addDiscreteColourRule(min+4,251,154,153);
                colours.addDiscreteColourRule(min+5,227,26,28);
                colours.addDiscreteColourRule(min+6,253,191,111);
                colours.addDiscreteColourRule(min+7,255,127,0);
                colours.addDiscreteColourRule(min+8,202,178,214);
                colours.addDiscreteColourRule(min+9,106,61,154);
                colours.addDiscreteColourRule(min+10,255,255,153);
                colours.addDiscreteColourRule(min+11,177,89,40);
                colours.setIsDiscrete(true);
                break;
                
            case ACCENT_3:
                colours.name = "Accent_3";
                colours.addDiscreteColourRule(min,127,201,127);
                colours.addDiscreteColourRule(min+1,190,174,212);
                colours.addDiscreteColourRule(min+2,253,192,134);
                colours.setIsDiscrete(true);
                break;
            case ACCENT_4:
                colours.name = "Accent_4";
                colours.addDiscreteColourRule(min,127,201,127);
                colours.addDiscreteColourRule(min+1,190,174,212);
                colours.addDiscreteColourRule(min+2,253,192,134);
                colours.addDiscreteColourRule(min+3,255,255,153);
                colours.setIsDiscrete(true);
                break;
            case ACCENT_5:
                colours.name = "Accent_5";
                colours.addDiscreteColourRule(min,127,201,127);
                colours.addDiscreteColourRule(min+1,190,174,212);
                colours.addDiscreteColourRule(min+2,253,192,134);
                colours.addDiscreteColourRule(min+3,255,255,153);
                colours.addDiscreteColourRule(min+4,56,108,176);
                colours.setIsDiscrete(true);
                break;
            case ACCENT_6:
                colours.name = "Accent_6";
                colours.addDiscreteColourRule(min,127,201,127);
                colours.addDiscreteColourRule(min+2,190,174,212);
                colours.addDiscreteColourRule(min+3,253,192,134);
                colours.addDiscreteColourRule(min+4,255,255,153);
                colours.addDiscreteColourRule(min+5,56,108,176);
                colours.addDiscreteColourRule(min+6,240,2,127);
                colours.setIsDiscrete(true);
                break;
            case ACCENT_7:
                colours.name = "Accent_7";
                colours.addDiscreteColourRule(min,127,201,127);
                colours.addDiscreteColourRule(min+1,190,174,212);
                colours.addDiscreteColourRule(min+2,253,192,134);
                colours.addDiscreteColourRule(min+3,255,255,153);
                colours.addDiscreteColourRule(min+4,56,108,176);
                colours.addDiscreteColourRule(min+5,240,2,127);
                colours.addDiscreteColourRule(min+6,191,91,23);
                colours.setIsDiscrete(true);
                break;
            case ACCENT_8:
                colours.name = "Accent_8";
                colours.addDiscreteColourRule(min,127,201,127);
                colours.addDiscreteColourRule(min+1,190,174,212);
                colours.addDiscreteColourRule(min+2,253,192,134);
                colours.addDiscreteColourRule(min+3,255,255,153);
                colours.addDiscreteColourRule(min+4,56,108,176);
                colours.addDiscreteColourRule(min+5,240,2,127);
                colours.addDiscreteColourRule(min+6,191,91,23);
                colours.addDiscreteColourRule(min+7,102,102,102);
                colours.setIsDiscrete(true);
                break;

            default:
                colours.name = "default";
                interval = range/4f;
                colours.addContinuousColourRule(min,             0, 50,  0);
                colours.addContinuousColourRule(min+interval,    0,150,  0);
                colours.addContinuousColourRule(min+2*interval,150,150,  0);
                colours.addContinuousColourRule(min+3*interval,150,  0,160);
                colours.addContinuousColourRule(max,           255,255,255);
                break;
        }
        return colours;  
    }
    
    /** Creates a colour table from the rules defined in the file with the given name. The file 
      * should be a landSerf colour table file containing XML defining a set of colour rules. 
      * This can be created using the <code>writeFile()</code> method.
      * @param fileName Name of file containing rules.
      * @return Colour table created or null if problems creating table from file.
      */
    public static ColourTable readFile(String fileName)
    {       
        // Extract colour rules from XML file.   
        DOMProcessor dom = new DOMProcessor(fileName);
        if (dom.isEmpty())
        {
            System.err.println("Could not extract XML colourtable data from '"+fileName+"'.");
            return null;
        }
        return processDom(dom);
    }
    
    
    /** Creates a colour table from the rules defined in the given input stream. The file should
      * be a landSerf colour table file containing XML defining a set of colour rules. This can
      * be created using the <code>writeFile()</code> method.
      * @param inStream Name of file containing rules.
      * @return Colour table created or null if problems creating table from file.
      */
    public static ColourTable readFile(InputStream inStream)
    {
        // Extract colour rules from XML file.   
        DOMProcessor dom = new DOMProcessor(inStream);
        if (dom.isEmpty())
        {
            System.err.println("Could not extract XML colourtable data from input stream.");
            return null;
        }
        return processDom(dom);
    }
       
    /** Writes the given colour table out as a file using the given name. The file can
      * be read back in using one of the <code>readFile()</code> methods.
      * @param colourTable Colour table to be written.
      * @param fileName Name of file to contain rules.
      * @return True if file written successfully.
      */
    public static boolean writeFile(ColourTable colourTable, String fileName)
    {
        if (colourTable == null)
        {
            System.err.println("No colour table provided to write to file.");
            return false;
        }
        
        DOMProcessor dom = new DOMProcessor();
        org.w3c.dom.Node root = dom.addElement("colourTable");
        dom.addComment("Colour table rules");
        dom.addComment("Each rule associates a colour with a value. Discrete rules apply to that value only, continuous rules are interpolated between values.");
        dom.addComment("Generated by the org.gicentre.processing.utils colour package.");
        
        dom.addElement("name", colourTable.name, root);
        
        if (colourTable.cTableType == COLOUR_RAW)
        {
            dom.addAttribute("raw","true",root); 
        }
        else
        {
            Vector<ColourRule> rules = colourTable.getColourRules();
            ColourRule colRule;
        
            for (int i=1; i<rules.size(); i++)  // Excludes first value which is transparent.
            {
                colRule = rules.get(i);
                org.w3c.dom.Node rule = dom.addElement("rule",ColourRule.toString(colRule.getlColour()),root);

                dom.addAttribute("value",Float.toString(colRule.getlIndex()),rule);
            
                if (colRule.getType() == ColourRule.DISCRETE)
                {
                    dom.addAttribute("type","discrete",rule); 
                }
            }
        }
        
        return dom.writeXML(fileName);
    }
    
    /** Writes the given colour table out as a file using the given output stream. The file can
      * be read back in using one of the <code>readFile()</code> methods.
      * @param colourTable Colour table to be written.
      * @param outStream Output stream of the file to contain rules.
      * @return True if file written successfully.
      */
    public static boolean writeFile(ColourTable colourTable, OutputStream outStream)
    {
        if (colourTable == null)
        {
            System.err.println("No colour table provided to write to file.");
            return false;
        }
        
        DOMProcessor dom = new DOMProcessor();
        org.w3c.dom.Node root = dom.addElement("colourTable");
        dom.addComment("Colour table rules");
        dom.addComment("Each rule assoociates a colour with a value. Discrete rules apply to that value only, continuous rules are interpolated between values.");
        dom.addComment("Generated by the org.gicentre.processing.utils colour package.");
        
        
        dom.addElement("name", colourTable.name, root);
        
        if (colourTable.cTableType == COLOUR_RAW)
        {
            dom.addAttribute("raw","true",root); 
        }
        else
        {
            Vector<ColourRule> rules = colourTable.getColourRules();
            ColourRule colRule;
        
            for (int i=1; i<rules.size(); i++)  // Excludes first value which is transparent.
            {
                colRule = rules.get(i);
                org.w3c.dom.Node rule = dom.addElement("rule",ColourRule.toString(colRule.getlColour()),root);

                dom.addAttribute("value",Float.toString(colRule.getlIndex()),rule);
            
                if (colRule.getType() == ColourRule.DISCRETE)
                {
                    dom.addAttribute("type","discrete",rule); 
                }
            }
        }
        return dom.writeXML(outStream);
    }
    
    // -------------------------- Private methods --------------------------
    
    /** Creates the colour table from the given DOM.
      * @param dom DOM containing colour table information.
      * @return New colour table.
      */
    private static ColourTable processDom(DOMProcessor dom)
    {
        // Check to see if we have defined a 'raw' colour table.
        org.w3c.dom.Node[] cTables = dom.getElements("colourTable");
  
        if (cTables.length > 0)
        {
            String atts[]  = dom.getAttributes("raw",cTables[0]);
    
            if (atts != null)
            {
                for (int i=0; i<atts.length; i++)
                {
                    if (atts[i].equalsIgnoreCase("true"))
                    {
                        ColourTable cTable = new ColourTable();
                        cTable.setColourTableType(COLOUR_RAW);
                        return cTable;
                    }
                }
            }
        }
        
        
        ColourTable colourTable = new ColourTable();
        
        // Store the colour table name if it exists. The name is optional in that older
        // versions of the colour table format did not specify a name.
        org.w3c.dom.Node[] nodes = dom.getElements("name");
        if (nodes.length >=1)
        {
            String names[] = dom.getText("name",nodes[0]);
            if ((names[0] != null) && (names[0].trim().length() > 0))
            {
                colourTable.name = names[0].trim();
            }   
        }
                
        nodes = dom.getElements("rule");
        
        if (nodes.length <1)
        {
            System.err.println("No colour rules found in colourtable file.");
            return null;
        }
       
        StringTokenizer sToken = null;
        int red,grn,blu,alf;
        float lVal;
        
        for (int i=0; i<nodes.length; i++)
        {
            String colours[] = dom.getText("rule",nodes[i]);
            String values[]  = dom.getAttributes("value",nodes[i]);
            String dRules[]  = dom.getAttributes("type",nodes[i]);
            
            sToken = new StringTokenizer(colours[0]," \t\n\r\f,");   
            if (sToken.countTokens() < 3)
            {
                continue;   // RGB or RGBA expected, so ignore if < 3 colour values.
            }
            if (values.length < 1)
            {
                continue;   // All rules should have 1 value attribute.
            }

            try
            {
                red = Integer.parseInt(sToken.nextToken());
                grn = Integer.parseInt(sToken.nextToken());
                blu = Integer.parseInt(sToken.nextToken());
                
                if (sToken.hasMoreTokens())
                {
                    alf = Integer.parseInt(sToken.nextToken());
                }
                else
                {
                    alf = 255; 
                }
                
                lVal = Float.parseFloat(values[0]);    
            }
            catch (NumberFormatException e)
            {
                // Ignore lines that don't contain numbers.
                continue;
            }
            
            if ((dRules.length > 0) && (dRules[0].equalsIgnoreCase("discrete")))
            {
                colourTable.addDiscreteColourRule(lVal,red,grn,blu,alf);
            }
            else
            {
                colourTable.addContinuousColourRule(lVal,red,grn,blu,alf); 
            }
        }
        return colourTable; 
    }
}