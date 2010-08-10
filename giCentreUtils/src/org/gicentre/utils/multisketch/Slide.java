package org.gicentre.utils.multisketch;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Vector;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PImage;

//******************************************************************************************
/** Class to represent a presentation slide for use in a slide show.
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

public class Slide 
{
    // -------------------------- Object variables --------------------------
    
    private Vector<SlideObject> slideObjects;
    private float totalContentHeight;
    private PFont defaultFont;
    private int defaultColour;
    private int defaultSize;
    private int defaultHorizAlignment,vertAlignment;
    
    private int borderLeft,borderRight, borderTop, borderBottom;
    private int leading;
            
    // ---------------------------- Constructors ----------------------------
    
    /** Creates a slide that uses the given default font. Size is assumed to be
      * 36 pixels high, colour assumed to be grey and alignment centred on the page. 
      * @param font Default font used to display text.
      */
    public Slide(PFont font)
    {
        this(font, 36, Color.gray.getRGB(), PConstants.CENTER);
    }
  
    /** Creates a slide that uses the given default values for font, size and colour.
      * Alignment is assumed to be centred on page. 
      * @param font Default font used to display text.
      * @param size Default font size used to display text.
      * @param colour Default colour used to display text.
      */
    public Slide(PFont font, int size, int colour)
    {
        this(font,size,colour,PConstants.CENTER);
    }
    
    /** Creates a slide that uses the given default values for font, size, colour and
      * horizontal alignment. Vertical alignment is assumed to be centred on page. 
      * @param font Default font used to display text.
      * @param size Default font size used to display text.
      * @param colour Default colour used to display text.
      * @param hAlignment Default horizontal alignment to use to display text. Assumes Processing's 
      *                   <code>LEFT</code>, <code>CENTER</code> and <code>RIGHT</code> constants.
      */
    public Slide(PFont font, int size, int colour, int hAlignment)
    {
        this(font,size,colour,hAlignment,PConstants.CENTER);
    }
        
    /** Creates a slide that uses the given default values for font, size, colour and alignment.
      * @param font Default font used to display text.
      * @param size Default font size used to display text.
      * @param colour Default colour used to display text.
      * @param hAlignment Default horizontal alignment to use to display text. Assumes Processing's 
      *                   <code>LEFT</code>, <code>CENTER</code> and <code>RIGHT</code> constants.
      * @param vAlignment Vertical alignment to use to display text. Assumes Processing's 
      *                   <code>TOP</code>, <code>CENTER</code> and <code>BOTTOM</code> constants.
      */
    public Slide(PFont font, int size, int colour, int hAlignment, int vAlignment)
    {
        slideObjects = new Vector<SlideObject>();
    
        totalContentHeight = 0;
        defaultFont = font;
        defaultColour = colour;
        defaultSize = size;
        defaultHorizAlignment = hAlignment;
        vertAlignment  = vAlignment;
        borderLeft = 5;
        borderRight = 5;
        borderTop = 5;
        borderBottom = 5;
        leading = 0;
    }
    
    // -------------------------- Processing Methods --------------------------
  
    /** Draws the contents of the slide.
      * @param aContext Applet context in which to draw. 
      */
    public void draw(PApplet aContext)
    {
        aContext.background(255);
        float vPos;
        
        if (vertAlignment == PConstants.TOP)
        {
            vPos = borderTop;
        }
        else if (vertAlignment == PConstants.BOTTOM)
        {
            vPos = (aContext.height-borderBottom) - totalContentHeight;
        }
        else
        {
            vPos = (aContext.height-totalContentHeight)/2f;
        }
    
        for (Object slideObject : slideObjects)
        {
            if (slideObject instanceof TextLine)
            {
                TextLine line = (TextLine)slideObject;
            
                aContext.textFont(line.getFont(),line.getFontSize());
                aContext.fill(line.getColour());
            
                float textWidth = aContext.textWidth(line.getText());
                float hPos;
                if (line.getHorizontalAlignment() == PConstants.LEFT)
                {
                    hPos = borderLeft;
                }
                else if (line.getHorizontalAlignment() == PConstants.RIGHT)
                {
                    hPos = (aContext.width-borderRight)-textWidth;
                }
                else
                {
                    hPos = (aContext.width-textWidth)/2f;
                }
                
                aContext.text(line.getText(), hPos, vPos);
                vPos+= line.calcBounds(aContext).height;
            }
            else if (slideObject instanceof SlideImage)
            {
                SlideImage image = (SlideImage)slideObject;
                
                float hPos;
                if (image.getHorizontalAlignment() == PConstants.LEFT)
                {
                    hPos = borderLeft;
                }
                else if (image.getHorizontalAlignment() == PConstants.RIGHT)
                {
                    hPos = (aContext.width-borderRight)-image.getWidth();
                }
                else
                {
                    hPos = (aContext.width-image.getWidth())/2f;
                }
                aContext.image(image.getImage(), hPos, vPos, image.getWidth(), image.getHeight());
                vPos+= image.getHeight();
            }
            
            // Add any leading defined for this slide.
            vPos += leading;
        }
    }
    
    // ------------------------------- Methods --------------------------------
    
    /** Sets the borders around the slide.
      * @param top Top border in pixels.
      * @param bottom bottom border in pixels.
      * @param left Left hand border in pixels.
      * @param right Right hand border in pixels.
      */
    public void setBorder(int top, int bottom, int left, int right)
    {
        this.borderTop = top;
        this.borderBottom = bottom;
        this.borderLeft = left;
        this.borderRight = right;
    }
    
    /** Sets the leading (vertical gap) between items on the slide.
      * @param leading Leading height in pixels.  
      */
    public void setLeading(int leading)
    {
        this.leading = leading;
    }
    
    /** Adds a line of text to the slide to be displayed using the default font, size,
      * colour and alignment.
      * @param text The text to add to the slide. 
      */
    public void addLine(String text)
    {
        addLine(text, defaultFont);
    }
    
    /** Adds a line of text to the slide using the given font. Text will be displayed in 
      * the default colour at the default size and with the default alignment.
      * @param font Font used to display this line of text.
      * @param text The text to add to the slide. 
      */
    public void addLine(String text, PFont font)
    {
        addLine(text, font, defaultSize);
    }
    
    /** Adds a line of text to the slide using the given font and size.
      * Text will be displayed in the default colour and alignment.
      * @param font Font used to display this line of text.
      * @param text The text to add to the slide. 
      * @param fontSize Size of font in pixels.
      */
    public void addLine(String text, PFont font, int fontSize)
    {
        addLine(text, font, fontSize, defaultColour);
    }
    
    /** Adds a line of text to the slide using the given font, size and colour.
      * Text will be displayed using the default alignment. 
      * @param font Font used to display this line of text.
      * @param text The text to add to the slide. 
      * @param fontSize Size of font in pixels.
      * @param colour Colour in which to display text.
      */
    public void addLine(String text, PFont font, int fontSize, int colour)
    {
        addLine(text, font, fontSize, colour, defaultHorizAlignment);
    }
    
    /** Adds a line of text to the slide using the given font, size, colour and 
      * horizontal alignment.
      * @param font Font used to display this line of text.
      * @param text The text to add to the slide. 
      * @param fontSize Size of font in pixels.
      * @param colour Colour in which to display text.
      * @param hAlign Horizontal text alignment to use. Assumes Processing's <code>LEFT</code>, 
      *              <code>CENTER</code> and <code>RIGHT</code> constants.
      */
    public void addLine(String text, PFont font, int fontSize, int colour, int hAlign)
    {
        slideObjects.add(new TextLine(text, font, fontSize, colour, hAlign));
    }
    
    /** Adds an image to the slide with the default horizontal alignment. The image's natural dimensions
      * are used for the display size. 
      * @param image Image to display in slide.
      */
    public void addImage(PImage image)
    {
        addImage(image,0,0,defaultHorizAlignment);
    }
    
    /** Adds an image with the given dimensions to the slide with the default horizontal alignment.
      * If a width and height are both zero, the image's natural dimensions will be used for display.
      * If only one of width and height is zero, the other dimension is used and the image's aspect
      * ratio is preserved. If both values are non-zero, the image will be displayed using those
      * values.
      * @param image Image to display in slide.
      * @param width Width at which to display image.
      * @param height Height at which to display image.
      */
    public void addImage(PImage image, int width, int height)
    {
        addImage(image,width,height,defaultHorizAlignment);
    }
    
    /** Adds an image with the given dimensions to the slide with the given horizontal alignment.
      * If a width and height are both zero, the image's natural dimensions will be used for display.
      * If only one of width and height is zero, the other dimension is used and the image's aspect
      * ratio is preserved. If both values are non-zero, the image will be displayed using those
      * values.
      * @param image Image to display in slide.
      * @param width Width at which to display image.
      * @param height Height at which to display image.
      * @param hAlign Horizontal image alignment to use. Assumes Processing's <code>LEFT</code>, 
      *              <code>CENTER</code> and <code>RIGHT</code> constants.
      */
    public void addImage(PImage image, int width, int height,int hAlign)
    {
        if (image != null)
        {
            slideObjects.add(new SlideImage(image,width,height, hAlign));
        }
    }
    
    /** Provides a textual description of the contents of this slide.
      * @return Text to be shown in slide. 
      */
    public String toString()
    {
        StringBuffer buf = new StringBuffer("Slide:\n");
        for (Object slideObject : slideObjects)
        {
            buf.append(slideObject+"\n");
        }
        return buf.toString();
    }
      
    // ---------------------------- Nested classes ----------------------------
    
    /** Defines the core functionality associated with any embedded slide object.
     */
    private interface SlideObject
    {
        /** Should provide a text representation of the slide object. */
        public String toString();
    }
  
    /** Class for representing the appearance of a single line of text on a slide.
      */
    private class TextLine implements SlideObject
    {
        private String text;
        private PFont font;
        private int fontSize;
        private int colour;
        private int hAlign;
    
        // ----------------- Constructor ---------------
        
        /** Creates a single line of text for the slide.
          * @param text Text to display.
          * @param font Font to use for text display.
          * @param fontSize Size of font to use.
          * @param colour Colour used to display line of text. 
          * @param hAlign Horizontal text alignment to use. Assumes Processing's <code>LEFT</code>, 
          *              <code>CENTER</code> and <code>RIGHT</code> constants.
          */
        public TextLine(String text, PFont font, int fontSize, int colour, int hAlign)
        {
            this.text = text;
            this.font = font;
            this.fontSize = fontSize;
            this.colour = colour;
            this.hAlign = hAlign;
            totalContentHeight += fontSize;
        }
    
        // ------------------- Methods -----------------
        
        /** Reports the font used by this line of text.
          * @return Font used for display. 
          */
        public PFont getFont()
        {
            return font;
        }
        
        /** Reports size of font used for display.
          * @return Font size.
          */
        public int getFontSize()
        {
            return fontSize;
        }
    
        /** Reports the text to display.
          * @return Text to display.
          */
        public String getText()
        {
            return text;
        } 
        
        /** Reports the colour of the text to display.
          * @return Colour of the text to display.
          */
        public int getColour()
        {
            return colour;
        } 
        
        /** Reports the horizontal alignment of the text to display.
          * @return Horizontal alignment of the text to display. Uses Processing's <code>LEFT</code>, 
          *         <code>CENTER</code> and <code>RIGHT</code> constants.
          */
        public int getHorizontalAlignment()
        {
            return hAlign;
        } 
        
        /** Reports the size of the rectangle occupied by the text line.
          * This can be used to align the text in some direction. 
          * @param aContext Sketch context used to calculate text sizes.
          * @return Width and height of the bounding box surrounding the line of text.
          */
        public Dimension calcBounds(PApplet aContext)
        {
            aContext.textFont(font,fontSize);
            float textWidth = aContext.textWidth(text);
            return new Dimension((int)textWidth,fontSize);
        }
        
        /** Provides a textual description of this line in the slide.
          * @return Line of text to be shown in slide. 
          */
        public String toString()
        {
            return getText();
        }
    } 
        
    /** Class for representing the appearance of a single line of text on a slide.
      */
    private class SlideImage implements SlideObject
    {
        private PImage image;
        private int imgWidth, imgHeight;
        private int hAlign;
   
        // ----------------- Constructor ---------------
        
        /** Stores the given image with the given dimensions. If both width and height are 0,
          * the image's natural dimensions are used. If only one of the values is zero, the
          * non-zero dimension is used and the image's aspect ratio is preserved.
          * @param image Image to store.
          * @param width Width of the displayed image or 0 if to be found from image.
          * @param height Height of the displayed image or 0 if to be found from image. 
          * @param hAlign Horizontal alignment of image.
         */
        public SlideImage(PImage image, int width, int height, int hAlign)
        {
            this.image = image;
            this.hAlign = hAlign;
            
            if ((width<=0) && (height <=0))
            {
                // Use images natural dimensions
                this.imgWidth = image.width;
                this.imgHeight = image.height;
            }
            else if (width <=0)
            {
                // Use the given height and preserve the image's aspect ratio
                this.imgHeight = height;
                float aspectRatio = image.width/(float)image.height;
                this.imgWidth = Math.round(height*aspectRatio);
            }
            else if (height <=0)
            {
                // Use the given width and preserve the image's aspect ratio
                this.imgWidth = width;
                float aspectRatio = image.width/(float)image.height;
                this.imgHeight = Math.round(width/aspectRatio);
            }
            else
            {
                this.imgWidth = width;
                this.imgHeight = height;
            }
            totalContentHeight += imgHeight;
        }
        
        /** Reports the stored image.
          * @return Stored image. 
          */
        public PImage getImage()
        {
            return image;
        }
        
        /** Reports the displayed width of the image.
          * @return Displayed width of image.
          */ 
        public int getWidth()
        {
            return imgWidth;
        }
        
        /** Reports the displayed height of the image.
          * @return Displayed height of image.
          */ 
        public int getHeight()
        {
            return imgHeight;
        }
        
        /** Reports the horizontal alignment of the image to display.
          * @return Horizontal alignment of the image to display. Uses Processing's <code>LEFT</code>, 
          *         <code>CENTER</code> and <code>RIGHT</code> constants.
          */
        public int getHorizontalAlignment()
        {
            return hAlign;
        } 
        
        /** Provides a textual description of this image.
          * @return text describing image to be shown in slide. 
          */
        public String toString()
        {
            return new String("[image width="+imgWidth+" height="+imgHeight+"]");
        }
    }
}