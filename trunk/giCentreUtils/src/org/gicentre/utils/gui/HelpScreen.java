package org.gicentre.utils.gui;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

// *****************************************************************************************
/** Class to produce a 'help screen' with the keys and instructions aligned on their 
 *  separating colons. Calculates the size required to contain all the instructions 
 *  (regardless of sketch size), then centres this.
 *  @author Aidan Slingsby and Jo Wood, giCentre, City University London.
 *  @version 3.2, 20th May, 2011. 
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

public class HelpScreen {
    
    // ----------------------------------- Object variables ------------------------------------

    private PApplet applet;
    private PFont font;
    private LinkedHashMap<String,String> helpEntries;
    private boolean isActive;           // Flag that can be used to indicate if help window is to be drawn.
    private int numSpacers;
    private static final String SPACER = "spacer_key";

    private int backgroundColour;
    private int borderColour;
    private int textColour;
    private float headerTextSize=0, footerTextSize=0;
    private String headerText=null, footerText=null;
    
    // TODO: We may chose to expose some more of these properties via getter and setter methods.
    private int outerPadding=10;
    private float textSize;
    private int spaceBetweenEntries=3;
    private int spaceBetweenLines=1;
    private int spaceBetweenColon=8;
    private int headerVerticalSpace, footerVerticalSpace;
    private int headerTextColour, footerTextColour;
    
    // -------------------------------------- Constructor --------------------------------------
    
    /** Creates a help screen object with default text size.
     * @param applet Parent sketch to which this help screen is to be attached.
     * @param font Font used for text in help screen.
     */
    public HelpScreen(PApplet applet,PFont font){
        this(applet,font,12);
    }
    
    /** Creates a help screen object.
     * @param applet Parent sketch to which this help screen is to be attached.
     * @param font Font used for text in help screen.
     * @param textSize Size of text to use in help screen (pixels).
     */
    public HelpScreen(PApplet applet,PFont font, int textSize){
        this.applet=applet;
        this.font=font;
        this.textSize = textSize;
        
        helpEntries      = new LinkedHashMap<String, String>();
        borderColour     = new Color(100,100,100,100).getRGB();
        backgroundColour = new Color(255,248,147,200).getRGB();
        textColour       = new Color(50,50,50).getRGB();
        footerTextColour = 100;
        isActive         = false;
        numSpacers       = 0;
        
        //textSize=font.size;       // font.size no longer supported in Processing.
    }
    
    // ---------------------------------------- Methods ----------------------------------------
    
    /** Adds a help entry to the help screen. Items will be displayed in order of addition.
     *  Can also be used to modify an entry (e.g. if a mode has changed - keyed to the "action").
     *  Can use \n in either (no automatic wrapping) to indicate new line.
     *  @param action The user interaction (e.g. key press) to be documented.
     *  @param instruction Description of the action.
     */
    public void putEntry(String action, String instruction){
        helpEntries.put(action,instruction);
    }
    
    /** Adds a vertical space (of the current font height) to the help screen entries.
     
     */
    public void addSpacer()
    {
        numSpacers++;
        helpEntries.put(SPACER+numSpacers," ");
    }

    /** Provides the optional text to appear at the top of the help screen. Could be title or
     *  other introductory text.
     *  Wraps at top of help screen, centred horizontally. Text size should be set explicitly.
     * @param text The text to appear in the header.
     * @param verticalSpace The space between the header and the text in the help window.
     * @param textSize Size of text in pixels.
     */
    public void setHeader(String text,int verticalSpace, int textSize){
        this.headerText=text;
        this.headerVerticalSpace=verticalSpace;
        this.headerTextSize=textSize;
    }
    
    /** Provides the optional text to appear at the foot of the help screen. Could be version, author etc
     *  Wraps at bottom of help screen, right-aligned. Text size should be set explicitly.
     * @param text The text to appear in the footer.
     * @param verticalSpace The space between the footer and the text in the help window.
     * @param textSize Size of text in pixels.
     */
    public void setFooter(String text,int verticalSpace, int textSize){
        this.footerText=text;
        this.footerVerticalSpace=verticalSpace;
        this.footerTextSize=textSize;
    }
    
    /** Reports whether or not the help window is currently active. This has no direct effect on 
     *  drawing, which has to be called explicitly, but provides a useful way of storing the
     *  state of the window visibility.
     *  @return True if the help window is active.
     */
    public boolean getIsActive()
    {
        return isActive;
    }

    /** Determines whether or not the help screen should be currently active. This has no direct 
     *  effect on drawing, which has to be called explicitly, but provides a useful way of storing
     *  the state of the window visibility.
     *  @param isActive If true, the help window is made active.
     */
    public void setIsActive(boolean isActive)
    {
        this.isActive = isActive;
    }
    
    /** Sets the current text size in pixels.
     *  @param textSize New text size in pixels.
     */
    public void setTextSize(float textSize)
    {
        this.textSize = textSize;
    }
    
    /** Sets the background colour of the help window.
     *  @param bgColour Background colour expressed as a Processing integer colour.
     */
    public void setBackgroundColour(int bgColour)
    {
        this.backgroundColour = bgColour;
    }
    
    /** Sets the foreground colour of the help window. This is the colour of the text displayed
     *  and the border around the window.
     *  @param fgColour Foreground colour expressed as a Processing integer colour.
     */
    public void setForegroundColour(int fgColour)
    {
        this.textColour   = fgColour;
        this.borderColour = fgColour;
    }

    /** Draws the help screen centred on the sketch. The parent sketch is responsible for calling this
     *  method so its visibility can be controlled, possibly by using <code>getIsActive()</code> / 
     *  <code>setIsActive()</code>.
     */
    public void draw(){
        //calculate positions/sizes;
        int widthLeftColumn=0;
        int widthRightColumn=0;
        int colonX;
        int y;
        int height=0;
        
        if (headerText!=null){
            height+=headerTextSize+spaceBetweenLines+headerVerticalSpace;
        }
        
        Iterator<Entry<String, String>> it = helpEntries.entrySet().iterator();
        while(it.hasNext()){
            Entry<String,String> entry=it.next();
            float entryHeight;
            applet.textFont(font);
            applet.textSize(textSize);

            String[] toks1 = entry.getKey().split("\n");
            String[] toks2 = entry.getValue().split("\n");
            
            // Heights
            entryHeight=PApplet.max(toks1.length,toks2.length)*(textSize+spaceBetweenLines);
            height+=entryHeight+spaceBetweenEntries;

            if (!entry.getKey().startsWith(SPACER))
            {
                // Widths
                for (int i=0;i<toks1.length;i++){
                    widthLeftColumn=(int)PApplet.max(widthLeftColumn,applet.textWidth(toks1[i]));
                }
                for (int i=0;i<toks2.length;i++){
                    widthRightColumn=(int)PApplet.max(widthRightColumn,applet.textWidth(toks2[i]));
                }
            }
        }
    
        if (footerText!=null){
            height+=footerVerticalSpace;
        }
        colonX=(applet.width/2)-((widthLeftColumn+widthRightColumn)/2)+widthLeftColumn;
        int top=(applet.height/2)-(height/2)-outerPadding;
        y=top+outerPadding;
        
        applet.fill(backgroundColour);
        applet.stroke(borderColour);
        applet.strokeWeight(0.8f);
        applet.rect((applet.width/2)-((widthLeftColumn+widthRightColumn)/2)-2-spaceBetweenColon-outerPadding,y-outerPadding,widthLeftColumn+widthRightColumn+2*(outerPadding+spaceBetweenColon+2),height+2*outerPadding);
        applet.fill(textColour);
        
        // Display header text if requested.
        if (headerText!=null){
            applet.textSize(headerTextSize);
            applet.textLeading(headerTextSize);
            applet.fill(headerTextColour);
            applet.textAlign(PConstants.CENTER,PConstants.TOP);
            applet.text(headerText,(applet.width/2)-((widthLeftColumn+widthRightColumn)/2)-2-spaceBetweenColon,y,widthLeftColumn+widthRightColumn+2*(spaceBetweenColon+2),PApplet.max(headerVerticalSpace,headerTextSize+headerTextSize/2));//added half the text size again, because textsize is too small for the height of a paragraphy box
            y+=headerTextSize+headerVerticalSpace;
            applet.textSize(textSize);
        }
        
        // Display the main entries in the help screen.
        it = helpEntries.entrySet().iterator();
        while(it.hasNext()){
            Entry<String, String> entry=it.next();
            
            if (!entry.getKey().startsWith(SPACER))
            {
                String[] toks1 = entry.getKey().split("\n");
                String[] toks2 = entry.getValue().split("\n");

                for (int i=0;i<PApplet.max(toks1.length,toks2.length);i++){
                    if (toks1.length>i){
                        applet.textAlign(PConstants.RIGHT,PConstants.TOP);
                        applet.text(toks1[i],colonX-spaceBetweenColon,y);
                    }
                    if (i==0){
                        applet.textAlign(PConstants.CENTER,PConstants.TOP);
                        applet.text(":",colonX,y);
                    }
                    if (toks2.length>i){
                        applet.textAlign(PConstants.LEFT,PConstants.TOP);
                        applet.text(toks2[i],colonX+spaceBetweenColon,y);
                    }
                    y+=textSize+spaceBetweenLines;
                }
            }
            else
            {
                y+=textSize;
            }
            y+=spaceBetweenEntries;
        }
        
        // Display the footer text if requested.
        if (footerText!=null){
            applet.textSize(footerTextSize);
            applet.textLeading(footerTextSize);
            applet.fill(footerTextColour);
            applet.textAlign(PConstants.RIGHT,PConstants.BOTTOM);
            int heightOfFooterParagraphBox=(int)PApplet.max(footerVerticalSpace,footerTextSize+footerTextSize/2);//added half the text size again, because textsize is too small for the height of a paragraphy box  
            applet.text(footerText,(applet.width/2)-((widthLeftColumn+widthRightColumn)/2)-2-spaceBetweenColon,top+height+outerPadding-heightOfFooterParagraphBox,widthLeftColumn+widthRightColumn+2*(spaceBetweenColon+2),heightOfFooterParagraphBox); 
        }
    }
}