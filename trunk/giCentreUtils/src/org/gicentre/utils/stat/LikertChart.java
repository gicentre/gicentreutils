package org.gicentre.utils.stat;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;

import org.gicentre.utils.colour.ColourTable;
import org.gicentre.utils.move.Ease;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

//  ****************************************************************************************
/** Class for storing and drawing Likert scaled data. That is, a set of frequencies in an 
 *  ordered dataset. Typically, these are questionnaire responses in the form 'strongly 
 *  disagree', 'disagree', 'neutral', 'agree' and 'strongly agree'. Likert scales can be 
 *  over any range, but are typically between 3 and 7 and usually odd. 
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.2, 1st August, 2011.
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

public class LikertChart
{
    
    // TODO: Convert this class to use the AbstractChart.
    
    // ----------------------------- Object variables ------------------------------

    private LikertChart chart2;
    private float[] frequencies, targetFrequencies;
    private float maxHeight,minHeight, numDontKnow, numMissing, numNA;
    private float targetMaxHeight, targetMinHeight, targetNumDontKnow, targetNumMissing, targetNumNA;
    private float currentMean,currentConsensus;
    private String title;
    private String longTitle;
    private int midBar,numBars; 
    private ColourTable cTable;
    private int secondaryColour,textColour;
    private float secondaryLineWidth;
    private float widthScale, heightScale;   // 0-1 representing proportion of bar space used.
    private boolean showTitle;
    private boolean showSecondary;
    private boolean animateToBars,animateFromBars;
    private boolean scaleByPrimary;
    private Rectangle2D lastBounds;         // Last location at which bars were drawn.
    private int highlightBar;               // Index of an optionally highlighted bar.
    
    private float mean,consensus, targetMean,targetConsensus;
    private float order;                    // For setting a custom ordering of charts.
    private float interp;
    
    private float animSpeed;                // Animation speed (1/numFrames to complete)
    private float textSize;                 // Size of text or <0 if calculated automatically.
    private float textPadding;              // Extra text padding between title and chart.

    private static final double LOG2 = Math.log(2);

    // ------------------------------- Constructors --------------------------------

    /** Creates a Likert chart of the given set of frequencies. Chart will be scaled
     *  to the maximum frequency provided.
     *  @param frequencies An ordered set of frequencies.
     */
    public LikertChart(float[] frequencies)
    {
        this(frequencies,0,0,0,null);
    }

    /** Creates a Likert chart of the given set of frequencies scaled to the given 
     *  maximum and minimum value. Can be negative (for example to represent deviation
     *  from an expected frequency).
     *  @param frequencies An ordered set of frequencies.
     *  @param maxHeight Maximum value by which the chart bars are scaled.
     *  @param minHeight Minimum value by which the chart bars are scaled.
     */
    public LikertChart(float[] frequencies, float maxHeight, float minHeight)
    {
        this(frequencies,0,0,0,null);
        this.maxHeight = maxHeight;
        this.minHeight = minHeight;
    }

    /** Creates a Likert chart of the given set of frequencies and non-responses and
     *  the given short title.
     *  @param frequencies An ordered set of frequencies.
     *  @param numDontKnow Number of 'Don't know' responses.
     *  @param numNA Number of 'not applicable' responses.
     *  @param numMissing Number of missing responses. This value can be used to provide
     *                    a fixed vertical scaling of several Likert charts.
     *  @param title Short title of chart or null if no title is to be shown.
     */
    public LikertChart(float[] frequencies, float numDontKnow, float numNA, float numMissing, String title)
    {
        this(frequencies, numDontKnow, numNA, numMissing,title,null);
    }
    
    /** Creates a Likert chart of the given set of frequencies and non-responses and
     *  the given titles.
     *  @param frequencies An ordered set of frequencies.
     *  @param numDontKnow Number of 'Don't know' responses.
     *  @param numNA Number of 'not applicable' responses.
     *  @param numMissing Number of missing responses. This value can be used to provide
     *                    a fixed vertical scaling of several Likert charts.
     *  @param title Short title of chart or null if no title is to be shown.
     *  @param longTitle Long title of chart or null if no long title is to be shown.
     */
    public LikertChart(float[] frequencies, float numDontKnow, float numNA, float numMissing, String title, String longTitle)
    {
        this.frequencies = frequencies;
        this.numDontKnow = numDontKnow;
        this.numNA = numNA;
        this.numMissing = numMissing;
        this.title = title;
        this.longTitle = longTitle;
                
        showTitle = false;
        widthScale = 0.5f;
        heightScale = 1;
        animateToBars = false;
        animateFromBars = false;
        scaleByPrimary = true;
        showSecondary = true;
        order = 0;
        interp = 1;
        animSpeed = 1f/25;
        textSize = -1;
        textPadding = 0;
        
        numBars = frequencies.length;
        midBar = (numBars-1)/2;
        cTable = ColourTable.getPresetColourTable(ColourTable.PU_OR,-1,1);
        secondaryColour = new Color(100,0,30,150).getRGB();
        secondaryLineWidth = 2;
        textColour = new Color(150,150,150).getRGB();
        highlightBar = -1;
        
        // Find the range of values in the distribution.
        maxHeight = -Float.MAX_VALUE;
        minHeight = 0;
        for (int i=0; i<numBars; i++)
        {
            if (maxHeight < frequencies[i])
            {
                maxHeight = frequencies[i];
            }
            if (minHeight > frequencies[i])
            {
                minHeight = frequencies[i];
            }
        }
        
        // Add the don't know,NA and missing values to the middle bar and check if it is now the max value.
        if (numBars%2 == 1)
        {
            maxHeight = Math.max(maxHeight, frequencies[(numBars-1)/2]+numDontKnow+numNA+numMissing);
        }

        // Calculate the mean and consensus values (see Tastle and Wierman, 2006)
        mean = 0;
        consensus = 0;
        
        if (minHeight >=0)
        {
            float total = 0;
            int dx = numBars-1;

            for (int i=0; i<numBars; i++)
            {
                total += frequencies[i];
                mean  += frequencies[i]*(i+1);
            }
            mean = mean / total;

            for (int i=0; i<numBars; i++)
            {
                consensus += ((frequencies[i]/total)*Math.log(1 - Math.abs((i+1)-mean)/dx)/LOG2);
            }
            consensus += 1;
        }
    }

    // ---------------------------------- Methods ----------------------------------
    
    /** Draws the Likert chart within the given rectangle but without any text labelling.
     *  @param parent Processing applet in which to draw the chart.
     *  @param bounds Rectangle within which to scale the chart. 
     */
    public void draw(PApplet parent, Rectangle2D bounds)
    {
        draw(parent, bounds,null);
    }
    
    /** Draws the Likert chart within the given rectangle with text labelling in the given font.
     *  @param parent Processing applet in which to draw the chart.
     *  @param bounds Rectangle within which to scale the chart. 
     *  @param font Font used to display text, or null if no text to be displayed.
     */
    public void draw(PApplet parent, Rectangle2D bounds, PFont font)
    {
        this.lastBounds = bounds;
    
        parent.pushStyle();     // Preserve any style settings in applet.
        parent.strokeWeight(0.2f); 
        parent.ellipseMode(PConstants.CENTER);
     
        float padding  = (float)bounds.getWidth()/20; 
        float barSpace = (float)(bounds.getWidth()-2*padding)/numBars;
        float originX  = (float)bounds.getX()+padding;
        float easedHeightScale = 0;
        float yScale = 0;
        
        // Store the values to plot and interpolate if we are transitioning.
        float currentMaxHeight,currentMinHeight;
        if ((scaleByPrimary) || (chart2==null))
        {
            currentMaxHeight = maxHeight;
            currentMinHeight = minHeight;
        }
        else
        {
            currentMaxHeight = chart2.maxHeight;
            currentMinHeight = chart2.minHeight;
        }
        
        float currentNumDontKnow = numDontKnow, currentNumDontKnow2=0;
        float[] currentFrequencies = new float[numBars], currentFrequencies2=null;
        
        System.arraycopy(frequencies,0,currentFrequencies,0,numBars);    
        currentMean = mean;
        currentConsensus = consensus;
        
        // Initialise the secondary values.
        if (chart2 != null)
        {
            currentNumDontKnow2 = chart2.numDontKnow;
            currentFrequencies2 = new float[chart2.numBars];
            System.arraycopy(chart2.frequencies,0,currentFrequencies2,0,chart2.numBars);
            interp = Math.min(interp, chart2.interp);   // Bind the two interpolators together.
            chart2.currentMean = chart2.mean;
            chart2.currentConsensus = chart2.consensus;
        }
        
        if (interp < 1)
        {
            interp += animSpeed;
            
            if (chart2 != null)
            {
                chart2.interp = interp;
            }
           
            if (targetFrequencies != null)
            {
                // Scale values as if still at the source scale.
                float toSourceScale = (maxHeight-minHeight)/(targetMaxHeight-targetMinHeight);     
                currentNumDontKnow = PApplet.lerp(numDontKnow,toSourceScale*targetNumDontKnow,interp);
                                 
                for (int i=0; i<numBars; i++)
                {
                    currentFrequencies[i] = PApplet.lerp(currentFrequencies[i],toSourceScale*targetFrequencies[i],interp);
                }
                currentMean = PApplet.lerp(mean,targetMean,interp);
                currentConsensus = PApplet.lerp(consensus,targetConsensus,interp);
            }
            
            // Scale the secondary values.
            if ((chart2 != null) && (currentFrequencies2 != null))
            {        
                if (chart2.targetFrequencies == null)
                {
                    // Scale values as if still at the source scale.
                    float toSourceScale;
                    if (scaleByPrimary)
                    {
                        // We are morphing the secondary values to be scaled by the current primary scaling.
                        toSourceScale = (currentMaxHeight-currentMinHeight)/(targetMaxHeight-targetMinHeight);
                        //toSourceScale = 1;
                    }
                    else
                    {
                        // We are morphing the secondary values to be scaled by the current secondary scaling.
                        //toSourceScale = 1;
                        toSourceScale = (currentMaxHeight-currentMinHeight)/(chart2.maxHeight-chart2.minHeight);
                    }
                    currentNumDontKnow2 = PApplet.lerp(chart2.numDontKnow,toSourceScale*chart2.numDontKnow,interp);
                    
                    for (int i=0; i<numBars; i++)
                    {
                        currentFrequencies2[i] = PApplet.lerp(currentFrequencies2[i],toSourceScale*currentFrequencies2[i],interp);
                    }
                }  
                else
                {
                    // We are morphing the secondary values to a new set of data items.
                    // Scale values as if still at the source scale.
                    float toSourceScale;
                    if (scaleByPrimary)
                    {
                        toSourceScale = 1;
                    }
                    else
                    {
                        toSourceScale = (currentMaxHeight-currentMinHeight)/(chart2.targetMaxHeight-chart2.targetMinHeight);
                    }
                    currentNumDontKnow2 = PApplet.lerp(chart2.numDontKnow,toSourceScale*chart2.targetNumDontKnow,interp); 

                    for (int i=0; i<chart2.numBars; i++)
                    {
                        currentFrequencies2[i] = PApplet.lerp(currentFrequencies2[i],toSourceScale*chart2.targetFrequencies[i],interp);
                    }
                    chart2.currentMean = PApplet.lerp(chart2.mean,chart2.targetMean,interp);
                    chart2.currentConsensus = PApplet.lerp(chart2.consensus,chart2.targetConsensus,interp);
                }
            }
             
            // We should use the target values as the base values now.
            if (interp >=1)
            {
                if (targetFrequencies != null)
                {
                    interp      = 1;
                    maxHeight   = targetMaxHeight;
                    minHeight   = targetMinHeight;
                    numDontKnow = targetNumDontKnow;
                    numMissing  = targetNumMissing;
                    numNA = targetNumNA;
                    System.arraycopy(targetFrequencies,0,frequencies,0,numBars);    
                    mean        = targetMean;
                    consensus   = targetConsensus;
                    resetTarget();
                }
                
                if ((chart2 != null) && (chart2.targetFrequencies != null))
                {
                    chart2.interp      = 1;
                    chart2.maxHeight   = chart2.targetMaxHeight;
                    chart2.minHeight   = chart2.targetMinHeight;
                    chart2.numDontKnow = chart2.targetNumDontKnow;
                    chart2.numMissing  = chart2.targetNumMissing;
                    chart2.numNA       = chart2.targetNumNA;
                    System.arraycopy(chart2.targetFrequencies,0,chart2.frequencies,0,chart2.numBars);    
                    chart2.mean        = chart2.targetMean;
                    chart2.consensus   = chart2.targetConsensus; 
                    chart2.resetTarget();
                }
            }
        }
        
        float negScale = (float)(bounds.getHeight()-2*padding)/(currentMaxHeight-currentMinHeight);
        float originY  = (float)(bounds.getY()+bounds.getHeight()+(currentMinHeight*negScale)-padding);
              
        // For testing
        //parent.stroke(0); parent.strokeWeight(1); parent.noFill(); 
        //parent.rect((float)bounds.getX(), (float)bounds.getY(), (float)bounds.getWidth(), (float)bounds.getHeight());
        
        if (animateToBars)
        {
            heightScale += animSpeed;
            if (heightScale >=1)
            {
                heightScale = 1;
                animateToBars = false;
            }
        }
        else if (animateFromBars)
        {
            heightScale -= animSpeed;
            if (heightScale <= 0)
            {
                heightScale = 0;
                animateFromBars = false;
            } 
        }
        
        float ticHeight = 10;
        
        if (heightScale > 0)
        {
            // Draw frequency bars.
            parent.noStroke();
            easedHeightScale = Ease.sinBoth(heightScale);
            ticHeight *= (1-easedHeightScale);
                      
            yScale = (float)(bounds.getHeight()-2*padding)*easedHeightScale/(currentMaxHeight-currentMinHeight);
            float barGap = barSpace*(1-widthScale);
            float x = originX-0.5f*barGap;
            float spaceScaling1 = barSpace*numBars/(barSpace*numBars - barGap);

            for (int bar=0; bar<numBars; bar++)
            {
                int colour = cTable.findColour((bar-(numBars-1)/2f)/(numBars-1));
                parent.fill(colour);
                
                // Highlight a bar if requested.
                if (bar == highlightBar)
                {
                    parent.stroke(secondaryColour);
                    parent.strokeWeight(2);
                }
                else
                {
                    parent.stroke(colour);
                    parent.strokeWeight(0.2f);
                }

                float barTop = originY - currentFrequencies[bar]*yScale;
                
                if (barTop < (bounds.getY()+padding-1)) 
                {
                    float barLeft = x+barGap*0.5f;
                    float barRight = barLeft + (barSpace-barGap)*spaceScaling1;
                    
                    // Bar is greater than space available.
                    parent.beginShape();
                     parent.vertex(barLeft,originY);
                     parent.vertex(barLeft,(float)bounds.getY()+padding);
                     parent.vertex((barLeft+barRight)/2,(float)bounds.getY());
                     parent.vertex(barRight,(float)bounds.getY()+padding);
                     parent.vertex(barRight,originY);
                    parent.endShape(PConstants.CLOSE);
                }
                else
                {
                    parent.rect(x+barGap*0.5f, originY - currentFrequencies[bar]*yScale, (barSpace-barGap)*spaceScaling1, currentFrequencies[bar]*yScale);
                }
                x += barSpace*spaceScaling1;
            }

            // Add the don't knows to the midpoint bar if we have an odd number of bars.
            if (numBars%2 == 1)
            {
                float barTop = originY - (currentFrequencies[midBar]+currentNumDontKnow)*yScale;
                
                if (barTop >= (bounds.getY()+padding)) 
                {
                 // Highlight a bar if requested.
                    if (midBar == highlightBar)
                    {
                        parent.stroke(secondaryColour);
                        parent.strokeWeight(2);
                    }
                    else
                    {
                        parent.stroke(0);
                        parent.strokeWeight(0.2f);
                    }
                    
                    parent.noFill();
                    parent.rect(originX + barSpace*midBar+0.5f*barGap, originY - (currentFrequencies[midBar]+currentNumDontKnow)*yScale,
                                barSpace-barGap, (currentFrequencies[midBar]+currentNumDontKnow)*yScale);
                }
            }
            
            // Draw the secondary bars if they exist and have been requested.
            if ((chart2 != null) && (showSecondary) && (currentFrequencies2 != null))
            {
                parent.fill(secondaryColour);
                parent.noStroke();
                
                float secondaryPadding = 5;
                x = originX-0.5f*barGap;
                float barSpace2 = (float)(bounds.getWidth()-2*padding)/chart2.numBars;
                float spaceScaling2 = barSpace2*chart2.numBars/(barSpace2*chart2.numBars - barGap);

                for (int bar=0; bar<chart2.numBars; bar++)
                {
                    float barTop = originY - currentFrequencies2[bar]*yScale;
                     
                    if (barTop < (bounds.getY()+padding-1)) 
                    {
                        float barLeft = x+barGap*0.5f+secondaryPadding;
                        float barRight = barLeft + (barSpace2-barGap)*spaceScaling2-2*secondaryPadding;
                        
                        // Bar is greater than space available.
                        parent.beginShape();
                         parent.vertex(barLeft,originY);
                         parent.vertex(barLeft,(float)bounds.getY()+padding);
                         parent.vertex((barLeft+barRight)/2,(float)bounds.getY());
                         parent.vertex(barRight,(float)bounds.getY()+padding);
                         parent.vertex(barRight,originY);
                        parent.endShape(PConstants.CLOSE);
                    }
                    else
                    {
                        // Bar fits within graphic space.
                        parent.rect(x+barGap*0.5f+secondaryPadding, barTop, (barSpace2-barGap)*spaceScaling2-2*secondaryPadding, currentFrequencies2[bar]*yScale);
                    }
                    
                    x += barSpace*spaceScaling2;
                }

                // Add the don't knows to the midpoint bar if we have an odd number of bars.
                if (chart2.numBars%2 == 1)
                {
                    float barTop = originY - currentFrequencies2[chart2.midBar]*yScale;
                    
                    if (barTop >= (bounds.getY()+padding)) 
                    {
                        parent.stroke(secondaryColour);
                        parent.noFill();
                        parent.rect(originX + barSpace2*chart2.midBar+0.5f*barGap +secondaryPadding, originY - (currentFrequencies2[chart2.midBar]+currentNumDontKnow2)*yScale,
                                    barSpace2-barGap-2*secondaryPadding, (currentFrequencies2[chart2.midBar]+currentNumDontKnow2)*yScale);
                    }
                }
            }
            
            // Draw y-axis at midpoint to emphasise dispersion
            parent.stroke(0);
            parent.strokeWeight(0.2f);
            float midpointX = originX+barSpace*numBars/2f;
            
            float axisHeight = easedHeightScale*currentMaxHeight*yScale;
            parent.line(midpointX, originY, midpointX, originY-axisHeight);
            axisHeight = easedHeightScale*currentMinHeight*yScale;
            parent.line(midpointX, originY, midpointX, originY-axisHeight);
        }
        
        if (heightScale < 1)
        {
            // The non-bar summary shows mid and endpoints.
            parent.stroke(0);
            float midpointX = originX+barSpace*numBars/2f;
            float rightPointX = originX+numBars*barSpace;
            parent.line(midpointX, originY-ticHeight, midpointX, originY+ticHeight);
            parent.line(originX, originY-ticHeight/5f, originX, originY+ticHeight/5f);
            parent.line(rightPointX, originY-ticHeight/5f, rightPointX, originY+ticHeight/5f);
        }

        // Draw x-axis.
        parent.line(originX,originY,(float)(originX+bounds.getWidth()-2*padding), originY);

        // Draw mean and consensus.
        if (currentMinHeight >= 0) // Do not apply to Chi distributions.
        {
            parent.strokeWeight(3);
            parent.stroke(150);
            parent.fill(cTable.findColour(((currentMean-1)-(numBars-1)/2f)/(numBars-1)));

            float barGap = barSpace*(1-widthScale);
            float spaceScaling = barSpace*numBars/(barSpace*numBars - barGap);

            float meanX = originX - spaceScaling*(0.5f*barGap - barSpace*(currentMean-0.5f));
            float dissentWidth = (float)((1-currentConsensus)*(bounds.getWidth()-2*padding)/2.0);
            float meanSize = (float)bounds.getWidth()/15;
            parent.line(meanX-dissentWidth, originY, meanX+dissentWidth, originY);
            parent.ellipse(meanX,originY,meanSize,meanSize); 
        }
        
        // Draw secondary mean and consensus if requested and available.
        if ((chart2 !=null) && (showSecondary))
        {
            if (chart2.minHeight >= 0) // Do not apply to Chi distributions.
            {
                parent.strokeWeight(secondaryLineWidth);
                parent.stroke(secondaryColour);
                parent.fill(parent.color(255,100));
               
                float barSpace2 = (float)(bounds.getWidth()-2*padding)/chart2.numBars;
                float barGap = barSpace2*(1-widthScale);
                float spaceScaling = barSpace2*chart2.numBars/(barSpace2*chart2.numBars - barGap);

                float meanX = originX - spaceScaling*(0.5f*barGap - barSpace2*(chart2.currentMean-0.5f));
                float dissentWidth = (float)((1-chart2.currentConsensus)*(bounds.getWidth()-2*padding)/2.0);
                float meanSize = (float)bounds.getWidth()/30;
                parent.ellipse(meanX,originY,meanSize,meanSize); 
                parent.line(meanX-dissentWidth, originY, meanX+dissentWidth, originY);
            }
        }
        
        // Draw text label if font provided.
        if ((showTitle) && (font != null))
        {
            parent.fill(textColour);
            
            float textHeight = textSize;
            if (textHeight < 0)
            {
                // Calculate text height automatically.
                textHeight = (float)Math.max(6, Math.min(bounds.getHeight()/5,bounds.getWidth()/20));
            }
            parent.textFont(font, textHeight);
            
            String displayedTitle = title.trim();
            float textWidth = parent.textWidth(displayedTitle);
            if (textWidth > bounds.getWidth()-padding)
            {
                // Need to truncate the title and add an ellipsis.
                float ellipsisWidth = parent.textWidth("...");
                while (textWidth > bounds.getWidth()-padding-ellipsisWidth)
                {
                    displayedTitle = displayedTitle.substring(0, displayedTitle.length()-2);
                    textWidth = parent.textWidth(displayedTitle);
                }
                displayedTitle = displayedTitle+"...";
            }
            parent.text(displayedTitle, originX, originY-textHeight/2-easedHeightScale*(currentMaxHeight*yScale - textHeight/2) - textPadding);
        }
        
        parent.popStyle();     // Restore original applet style settings.
    }
    
    /** Sets the colour table to be used to show the Likert chart. The colour table 
     *  should be scaled between +-1, probably using a diverging colour scheme. For
     *  a default uniform colour, the <code>cTable</code> value should be null.
     *  @param cTable Colour table to use for showing Likert chart.
     */
    public void setColourTable(ColourTable cTable)
    {
        this.cTable = cTable;
    }
    
    /** Sets the proportion of the width allocated to each bar that contains a coloured bar.
     *  This allows the gaps between adjacent bars to be controlled.
     *  @param widthScale Proportion of the width allocated to each bar that is drawn.
     *                    Should be scaled between 0 and 1.
     */
    public void setWidthScale(float widthScale)
    {
        this.widthScale = widthScale;
    }
    
    
    /** Determines if histogram bars should be shown.
     *  @param showBars Bars are shown if true.
     */
    public void setShowBars(boolean showBars)
    {
        if (showBars)
        {
            heightScale = 1;
        }
        else
        {
            heightScale = 0;
        }
        this.animateToBars = false;
        this.animateFromBars = false;
    }
    
    /** Add a secondary chart to the Likert chart. If not null, both sets of data from this class
     *  and the given secondary chart can be shown together. To remove the secondary chart permanently,
     *  set the given value in this method to null. To temporarily stop the display of the secondary chart
     *  consider using <code>setSecondaryDisplay(false)</code>.
     *  @param secondaryChart Second set of data to display in chart, or null if no secondary data to be displayed.
     */
    public void setSecondaryChart(LikertChart secondaryChart)
    {
        this.chart2 = secondaryChart;
    }
    
    /** Determines whether or not the secondary chart can be displayed. If false, the secondary data are never
     *  displayed although scaling of charts can still be applied to secondary data if they exist.
     *  @param showSecondary Secondary data displayed if true and they exist, otherwise no secondary data displayed.
     */
    public void setSecondaryDisplay(boolean showSecondary)
    {
        this.showSecondary = showSecondary;
    }
    
    /** Sets the colour to be used for the secondary data in the chart. Colour is represented using a 32 bit integer.
     *  @param colour Colour to be used for secondary data (using Processing's integer colour format).
     */
    public void setSecondaryColour(int colour)
    {
        this.secondaryColour = colour;
    }
    
    /** Sets the width of the secondary line symbolisation.
     *  @param width Width of lines used to draw Likert summary symbols (can be fractions of a pixel).
     */
    public void setSecondaryLineWidth(float width)
    {
        this.secondaryLineWidth = width;
    }
    
    /** Reports the colour to be used for the secondary data in the chart. Colour is represented using 
     *  a 32 bit integer.
     *  @return Colour to be used for secondary data (using Processing's integer colour format).
     */
    public int getSecondaryColour()
    {
        return secondaryColour;
    }
    
    /** Sets the colour to be used for displaying the title of the chart. Colour is represented using 
     *  a 32 bit integer.
     *  @param colour Colour to be used for chart title and any other text (using Processing's integer colour format).
     */
    public void setTextColour(int colour)
    {
        this.textColour = colour;
    }
    
    /** Reports the colour to be used for the title of the chart and any other text. Colour is represented 
     *  using a 32 bit integer.
     *  @return Colour to be used for chart title and other text (using Processing's integer colour format).
     */
    public int getTextColour()
    {
        return textColour;
    }
    
    /** Allows a bar to be highlighted or highlight removed. The given value should be the index of the bar
     *  to highlight (0 being the first, 1 the second etc.). The bar is highlighted in the current secondary
     *  colour. Highlighting can be turned off by providing a negative value to this method.
     *  @param barIndex Index of the bar to highlight or -1 if no bar is to be highlighted.
     */
    public void setHighlightBar(int barIndex)
    {
        this.highlightBar = barIndex;
    }
    
    /** Reports the index of the currently highlighted bar or -1 if no bar highlighted.
     *  @return Index of the highlighted bar or -1 if no bar is highlighted.
     */
    public int getHighlightBar()
    {
        return highlightBar;
    }
    
    /** Reports the bar at the given (x,y) location. If there is no bar at this location a -1 is
     *  returned. If chart is in the collapsed, non-bar state, this will always return -1.
     *  @param xCoord x screen coordinate of point to query.
     *  @param yCoord y screen coordinate of point to query.
     *  @return Index of bar found (0 is the first bar, 1 is the second etc.) or -1 if no bar at given location.
     */
    public int getBarAt(float xCoord, float yCoord)
    {   
        // If bars are collapsed, return -1.
        if (heightScale <= 0)
        {
            return -1;
        }
        
        // If we have yet to draw the chart, return -1.
        if (lastBounds == null)
        {
            return -1;
        }
        
        // If clicked outside the bounding box, return -1.
        if (!lastBounds.contains(xCoord, yCoord))
        {
            return -1;
        }
 
        // If clicked above the maximum bar or below the origin, return -1
        float currentMaxHeight,currentMinHeight;
        
        if ((scaleByPrimary) || (chart2==null))
        {
            currentMaxHeight = maxHeight;
            currentMinHeight = minHeight;
        }
        else
        {
            currentMaxHeight = chart2.maxHeight;
            currentMinHeight = chart2.minHeight;
        }
        
        float padding  = (float)lastBounds.getWidth()/20;
        float yScale = (float)(lastBounds.getHeight()-2*padding)/(currentMaxHeight-currentMinHeight);
        
        float originY  = (float)(lastBounds.getY()+lastBounds.getHeight()+(currentMinHeight*yScale)-padding);
        if ((yCoord <= (lastBounds.getY()+padding)) || (yCoord > originY))
        {
            return -1;
        }
        
        // Finally check each bar in turn.
        float barSpace = (float)(lastBounds.getWidth()-2*padding)/numBars;
        float barGap = barSpace*(1-widthScale);
        float originX  = (float)lastBounds.getX()+padding;
        float x = originX-0.5f*barGap;
        float spaceScaling = barSpace*numBars/(barSpace*numBars - barGap);
  
        for (int bar=0; bar <numBars; bar++)
        {
            float barTop = originY - frequencies[bar]*yScale;
            float barLeft = x+barGap*0.5f;
            float barRight = barLeft + (barSpace-barGap)*spaceScaling;
            
            if ((xCoord >= barLeft) && (xCoord <=barRight) && (yCoord >=barTop))
            {
                return bar;
            }
            x += barSpace*spaceScaling;
        }
  
        // Not within any bar.
        return -1;
    }
    
    /** Triggers an animation to a histogram representation from the summary state.
     */
    public void animateToBars()
    {
        this.animateToBars = true;
        this.animateFromBars = false;
    }
    
    /** Triggers an animation away from a histogram representation to the summary state.
     */
    public void animateFromBars()
    {
        this.animateFromBars = true;
        this.animateToBars = false;
    }
    
    /** Provides an animated transition to the given set of values.
     *  @param newFrequencies Frequency distribution of the new values to represent in the chart.
     *  @param newNumDontKnow Number of 'don't know' answers in the target distribution.
     *  @param newNumNA Number of Not Applicable answers in the target distribution.
     *  @param newNumMissing Number of missing values in the target distribution.
     */
    public void animateToNewValues(float[] newFrequencies, float newNumDontKnow, float newNumNA, float newNumMissing)
    {
        resetTarget();
        this.targetFrequencies = newFrequencies;
        interp =0;  // Signals that a transition will be required.
        
        // Find the range of values in the distribution.
        for (int i=0; i<numBars; i++)
        {
            if (targetMaxHeight < newFrequencies[i])
            {
                targetMaxHeight = newFrequencies[i];
            }
            if (targetMinHeight > newFrequencies[i])
            {
                targetMinHeight = newFrequencies[i];
            }
        }

        // Add the don't know, NA and missing values to the middle bar and check if it is now the max value.
        if (numBars%2 == 1)
        {
            targetMaxHeight = Math.max(targetMaxHeight,
            		                   newFrequencies[(numBars-1)/2]+newNumDontKnow+newNumNA+newNumMissing);
        }
        
        // Calculate the mean and consensus values (see Tastle and Wierman, 2006)
        if (targetMinHeight >=0)
        {
            float total = 0;
            int dx = numBars-1;

            for (int i=0; i<numBars; i++)
            {
                total += newFrequencies[i];
                targetMean  += newFrequencies[i]*(i+1);
            }
            targetMean = targetMean / total;

            for (int i=0; i<numBars; i++)
            {
                targetConsensus += ((newFrequencies[i]/total)*Math.log(1 - Math.abs((i+1)-targetMean)/dx)/LOG2);
            }
            targetConsensus += 1;
        }
    }
     
    /** Reports the proportion of the width allocated to each bar that contains a coloured bar.
     *  This allows the gaps between adjacent bars to be controlled.
     *  @return Proportion of the width allocated to each bar that is drawn, scaled between 0 and 1.
     */
    public float getWidthScale()
    {
        return widthScale;
    }
    
    /** Reports the frequency values that are used to produce the Likert chart.
     *  @return Array of frequency values, one for each bar in the chart.
     */
    public float[] getFrequencies()
    {
        return frequencies;
    }
    
    /** Reports the seconday data that may be drawn in this chart. Can be null if no secondary data
     *  are stored. The data are stored in a LikertChart object from which all relevant values can be
     *  extracted.
     *  @return Secondary data or null if none exist. 
     */
    public LikertChart getSecondaryData()
    {
        return chart2;
    }
    
    /** Determines whether bars are scaled to the primary data values or the secondary data values
     *  (if they exist).
     *  @param usePrimary Bars are scaled to primary values if true or secondary values if false and 
     *                    there are secondary values to show.
     */
    public void setScaleToPrimary(boolean usePrimary)
    {
        this.scaleByPrimary = usePrimary;
    }
    
    /** Determines whether or not the title is displayed on the chart.
     *  @param showTitle Title displayed if true.
     */
    public void setShowTitle(boolean showTitle)
    {
        this.showTitle = showTitle;
    }
    
    /** Reports the title of this Likert chart.
     *  @return Title of this Likert chart or null if no title defined.
     */
    public String getTitle()
    {
        return title;
    }
    
    /** Reports the long title of this Likert chart.
     *  @return Long title of this Likert chart or null if no long title defined.
     */
    public String getLongTitle()
    {
        return longTitle;
    }

    /** Reports the mean score of this Likert distribution. The mean will be scaled
     *  between 1 and the number of Likert categories. Assumes a uniform interval
     *  between adjacent ordered categories.
     *  @return Mean score for this categorical distribution.
     */
    public float getMean()
    {
        return mean;
    }
    
    /** Reports the consensus score of this Likert distribution. Consensus is scaled
     *  between 0 and 1 where 0 is strongest disagreement (half the values at one extreme
     *  of the Likert scale, the other half at the opposite extreme) and 1 is complete 
     *  agreement (all values in the same Likert category). 
     *  @return Mean score for this categorical distribution.
     */
    public float getConsensus()
    {
        return consensus;
    }
    
    /** Sets the order value used for custom ordering of a collection of Likert charts.
     *  Ordering is from low to high. 
     *  @param order Order value used when providing a custom sort of charts.
     */
    public void setOrder(float order)
    {
        this.order = order;
    }
    
    /** Sets the animation speed for all transitions (e.g. animation to new data).
     *  @param numFrames Number of frames to complete a transition.
     */
    public void setAnimationSpeed(float numFrames)
    {
        if (numFrames <=0)
        {
            this.animSpeed = 1;
        }
        else
        {
            this.animSpeed = 1/numFrames;
        }
    }

    /** Reports the animation speed for all transitions (e.g. animation to new data).
     *  @return Number of frames to complete a transition.
     */
    public float getAnimationSpeed()
    {
        return (1/animSpeed);
    }
    
    /** Sets the size of the title text in pixels or -1 if text size is to be calculated automatically.
     *  @param size Title text size in pixels.
     */
    public void setTextSize(float size)
    {
        this.textSize = size;
    }
    
    /** Reports the size of the title text in pixels or -1 if text size is calculated automatically.
     *  @return Title text size in pixel units.
     */
    public float getTextSize()
    {
        return textSize;
    }
    
    /** Sets the extra padding between title and chart.
     *  @param padding Number of extra pixels between the title and the chart.
     */
    public void setTextPadding(float padding)
    {
        this.textPadding = padding;
    }
    
    /** Reports the extra padding between title and chart.
     *  @return Extra padding between title and chart in pixel units.
     */
    public float getTextPadding()
    {
        return textPadding;
    }

    /** Reports the order value used for custom ordering of a collection of Likert charts.
     *  Ordering is from low to high.
     *  @return Order value used when constructing an custom sort.
     */
    public float getOrder()
    {
        return order;
    }
    
    /** Resets the target values after a transition has completed.
     */
    private void resetTarget()
    {
        targetFrequencies = null;
        targetNumDontKnow = numDontKnow;
        targetNumNA = numNA;
        targetNumMissing = numMissing;
        targetMaxHeight = -Float.MAX_VALUE;
        targetMinHeight = 0;  
        targetMean = 0;
        targetConsensus = 0;
    }
    
    // --------------------------- Static sorting classes --------------------------
    
    /** Provides a custom comparator that can be used for sorting Likert charts in the
     *  order implied by the values provided to <code>setOrder()</code>. If two Likert
     *  charts have the same order value, they are sorted into a consistent but
     *  platform dependent order.
     *  @return Comparator able to compare the order values of two Likert charts.
     */
    public static Comparator<LikertChart> getCustomComparator()
    {
        return new CustomComparator();
    }
    
    /** Provides a comparator that can be used for sorting Likert charts by their mean
     *  scores. If two Likert charts have the same mean, they are sorted by order value.
     *  @return Comparator able to compare the means of two Likert distributions.
     */
    public static Comparator<LikertChart> getMeanComparator()
    {
        return new MeanComparator();
    }
    
    /** Provides a comparator that can be used for sorting Likert charts by their consensus
     *  scores. If two Likert charts have the same consensus, they are sorted by mean score.
     *  @return Comparator able to compare the consensus of two Likert distributions.
     */
    public static Comparator<LikertChart> getConsensusComparator()
    {
        return new ConsensusComparator();
    }
    
    /** Allows a custom comparison of two Likert charts based on their order values.
     */
    private static class CustomComparator implements Comparator<LikertChart>
    {
    	/** Creates a new custom comparator for ordering Likert charts.
    	 */
        public CustomComparator() 
        {
			// Do nothing for the moment.
		}

		/** Compares two Likert 'order' values. If the first is less than the second, 
         *  -1 is returned. If it is greater 1 is returned. If they are identical, their
         *  hashcodes are compared.
         * @param chart1 First chart to compare.
         * @param chart2 Second chart to compare.
         * @return Negative if the first chart's order value is smaller, positive if greater.
         */    
        public int compare(LikertChart chart1, LikertChart chart2) 
        { 
            if (chart1.getOrder() < chart2.getOrder())
            {
                return -1;
            }

            if (chart1.getOrder() > chart2.getOrder())
            {
                return 1;
            }

            // If the two charts have the same order value, sort by hashcode.
            if (chart1.hashCode() < chart2.hashCode())
            {
                return -1;
            }

            if (chart1.hashCode() > chart2.hashCode())
            {
                return 1;
            }
            
            // Must be references to the same object.
            return 0;
        }
    }
    
    /** Allows a comparison of two Likert distributions based on their mean scores. A standard sort
     *  will order charts from highest to lowest mean.
     */
    private static class MeanComparator implements Comparator<LikertChart>
    {
    	/** Creates a new comparator for ordering Likert charts by their mean value.
    	 */
        public MeanComparator() 
        {
			// Do nothing for the moment.
		}

		/** Compares two Likert mean scores. If the first is greater than the second, 
         *  -1 is returned. If it is less, 1 is returned. If they are identical, their
         *  order values are compared.
         * @param chart1 First chart to compare.
         * @param chart2 Second chart to compare.
         * @return Negative if the first Likert mean score is smaller, positive if greater.
         */    
        public int compare(LikertChart chart1, LikertChart chart2) 
        { 
            if (chart1.getMean() > chart2.getMean())
            {
                return -1;
            }

            if (chart1.getMean() < chart2.getMean())
            {
                return 1;
            }

            // If the two charts have the same mean, sort by order value.
            if (chart1.getOrder() < chart2.getOrder())
            {
                return -1;
            }

            if (chart1.getOrder() > chart2.getOrder())
            {
                return 1;
            }

            // If the two charts have the same order value, sort by hashcode.
            if (chart1.hashCode() < chart2.hashCode())
            {
                return -1;
            }

            if (chart1.hashCode() > chart2.hashCode())
            {
                return 1;
            }
            
            // Must be references to the same object.
            return 0;
        }
    }
    
    /** Allows a comparison of two Likert distributions based on their consensus scores.
     */
    private static class ConsensusComparator implements Comparator<LikertChart>
    {
    	/** Creates a new comparitor for ordering by consensus value.
    	 */
        public ConsensusComparator() 
        {
			// Do nothing for the moment.
		}

		/** Compares two Likert consensus scores. If the first is less than the second, 
         *  -1 is returned. If it is greater 1 is returned. If they are identical, their
         *  mean values are compared.
         * @param chart1 First chart to compare.
         * @param chart2 Second chart to compare.
         * @return Negative if the first Likert consensus score is smaller, positive if greater.
         */    
        public int compare(LikertChart chart1, LikertChart chart2) 
        { 
            if (chart1.getConsensus() > chart2.getConsensus())
            {
                return -1;
            }

            if (chart1.getConsensus() < chart2.getConsensus())
            {
                return 1;
            }

            // If the two charts have the same consensus, sort by mean score.
            if (chart1.getMean() < chart2.getMean())
            {
                return -1;
            }

            if (chart1.getMean() > chart2.getMean())
            {
                return 1;
            }

            // If the two charts have the same mean, sort by order value.
            if (chart1.getOrder() < chart2.getOrder())
            {
                return -1;
            }

            if (chart1.getOrder() > chart2.getOrder())
            {
                return 1;
            }

            // If the two charts have the same order value, sort by hashcode.
            if (chart1.hashCode() < chart2.hashCode())
            {
                return -1;
            }

            if (chart1.hashCode() > chart2.hashCode())
            {
                return 1;
            }
            
            // Must be references to the same object.
            return 0;
        }
    }
}