import org.gicentre.utils.colour.*;    // For colour tables.

// Sketch to demonstrate the use of ColorBrewer colour schemes using the ColourTable
// class to generate preset colour schemes. 
// Version 1.4, 6th February, 2016.
// Author Jo Wood.

// ------------------ Sketch-wide variables --------------------

ColourTable[] coloursCont, coloursDiv, coloursCat;      // Colour tables to use.
PFont largeFont,smallFont;

// ---------------------- Initialisation -----------------------

// Sets up the sketch by initialising a set of ColorBrewer colour schemes.
void setup()
{
  size(900,500);
  
  largeFont = loadFont("BonvenoCF-Light-18.vlw"); 
  smallFont = loadFont("BonvenoCF-Light-10.vlw"); 
  textAlign(CENTER,TOP);

  coloursCont = new ColourTable[18];
  coloursCont[0]  = ColourTable.getPresetColourTable(ColourTable.YL_GN,0,1);
  coloursCont[1]  = ColourTable.getPresetColourTable(ColourTable.YL_GN_BU,0,1);
  coloursCont[2]  = ColourTable.getPresetColourTable(ColourTable.GN_BU,0,1);
  coloursCont[3]  = ColourTable.getPresetColourTable(ColourTable.BU_GN,0,1);
  coloursCont[4]  = ColourTable.getPresetColourTable(ColourTable.PU_BU_GN,0,1);
  coloursCont[5]  = ColourTable.getPresetColourTable(ColourTable.PU_BU,0,1);
  coloursCont[6]  = ColourTable.getPresetColourTable(ColourTable.BU_PU,0,1);
  coloursCont[7]  = ColourTable.getPresetColourTable(ColourTable.RD_PU,0,1);
  coloursCont[8]  = ColourTable.getPresetColourTable(ColourTable.PU_RD,0,1);
  coloursCont[9]  = ColourTable.getPresetColourTable(ColourTable.OR_RD,0,1);
  coloursCont[10] = ColourTable.getPresetColourTable(ColourTable.YL_OR_RD,0,1);
  coloursCont[11] = ColourTable.getPresetColourTable(ColourTable.YL_OR_BR,0,1);
  coloursCont[12] = ColourTable.getPresetColourTable(ColourTable.PURPLES,0,1);
  coloursCont[13] = ColourTable.getPresetColourTable(ColourTable.BLUES,0,1);
  coloursCont[14] = ColourTable.getPresetColourTable(ColourTable.GREENS,0,1);
  coloursCont[15] = ColourTable.getPresetColourTable(ColourTable.ORANGES,0,1);
  coloursCont[16] = ColourTable.getPresetColourTable(ColourTable.REDS,0,1);
  coloursCont[17] = ColourTable.getPresetColourTable(ColourTable.GREYS,0,1);
  
  coloursDiv = new ColourTable[9];
  coloursDiv[0]  = ColourTable.getPresetColourTable(ColourTable.PU_OR,0,1);
  coloursDiv[1]  = ColourTable.getPresetColourTable(ColourTable.BR_B_G,0,1);
  coloursDiv[2]  = ColourTable.getPresetColourTable(ColourTable.P_R_GN,0,1);
  coloursDiv[3]  = ColourTable.getPresetColourTable(ColourTable.PI_Y_G,0,1);
  coloursDiv[4]  = ColourTable.getPresetColourTable(ColourTable.RD_BU,0,1);
  coloursDiv[5]  = ColourTable.getPresetColourTable(ColourTable.RD_GY,0,1);
  coloursDiv[6]  = ColourTable.getPresetColourTable(ColourTable.RD_YL_BU,0,1);
  coloursDiv[7]  = ColourTable.getPresetColourTable(ColourTable.SPECTRAL,0,1);
  coloursDiv[8]  = ColourTable.getPresetColourTable(ColourTable.RD_YL_GN,0,1);
  
  coloursCat = new ColourTable[8];
  coloursCat[0]  = ColourTable.getPresetColourTable(ColourTable.SET1_9);
  coloursCat[1]  = ColourTable.getPresetColourTable(ColourTable.SET2_8);
  coloursCat[2]  = ColourTable.getPresetColourTable(ColourTable.SET3_12);
  coloursCat[3]  = ColourTable.getPresetColourTable(ColourTable.PASTEL1_9);
  coloursCat[4]  = ColourTable.getPresetColourTable(ColourTable.PASTEL2_8);
  coloursCat[5]  = ColourTable.getPresetColourTable(ColourTable.DARK2_8);
  coloursCat[6]  = ColourTable.getPresetColourTable(ColourTable.PAIRED_12);
  coloursCat[7]  = ColourTable.getPresetColourTable(ColourTable.ACCENT_8);
}

// ------------------ Processing draw --------------------

// Draws the colour schemes as horizontal colour bars.
void draw()
{
  background(255);
 
  int border = 8;
  int colOffset=border; 
  int textSpace = 46; 
  float barWidth = (width - (border*6 + textSpace*2)) / 4;
  int rowOffset = 24 + border;
  
  fill(120);
  textFont(largeFont);
  text("Sequential",border+barWidth+textSpace/2,border);
  text("Diverging", 2*border + 3*(border+barWidth) + 1.5*textSpace,border);
  text("Categorical",2*border + 3*(border+barWidth) + 1.5*textSpace, border + (coloursDiv.length+1)*25);
  textFont(smallFont);
   
  // Brewer continuous colours 
  for (int bar=0; bar<coloursCont.length; bar++)
  {
    // Draw a continuous colour bar.
    float inc = 0.01;
    colOffset = border;
    
    for (float i=0; i<1-inc; i+=inc)
    {
      fill(coloursCont[bar].findColour(i));
      stroke(coloursCont[bar].findColour(i));
      rect(colOffset + barWidth*i, rowOffset + bar*25 ,barWidth*inc+1,20);
    }
    
    // Draw rectangle around continuous colour bar.
    stroke(0,100);
    noFill();
    rect(colOffset,rowOffset + bar*25, barWidth,20);
    
    // Label colour scheme.
    fill(80);
    textAlign(CENTER,TOP);
    text(coloursCont[bar].getName(),colOffset + barWidth+(border+textSpace)/2, rowOffset +bar*25 + 5);
    
    // Draw the discrete version of the continuous Brewer colour scheme.
    colOffset += barWidth+border + textSpace;
    stroke(0,100);
    inc = 1/9.0;
    
    for (float i=0; i<1; i+=inc)
    {
      fill(coloursCont[bar].findColour(i + 0.5*inc));
      rect(colOffset + barWidth*i, rowOffset + bar*25, barWidth*inc,20);
    }
  }
  
  // Brewer diverging colour schemes.
  for (int bar=0; bar<coloursDiv.length; bar++)
  {
    // Draw a continuous colour bar.
    float inc = 0.01;
    colOffset = (int)(barWidth+border*2)*2 + textSpace;
    
    for (float i=0; i<1-inc; i+=inc)
    {
      fill(coloursDiv[bar].findColour(i));
      stroke(coloursDiv[bar].findColour(i));
      rect(colOffset + barWidth*i, rowOffset + bar*25 ,barWidth*inc+1,20);
    }
    // Draw rectangle around continuous colour bar.
    stroke(0,100);
    noFill();
    rect(colOffset,rowOffset + bar*25, barWidth,20);
    
    // Label colour scheme.
    fill(80);
    textAlign(CENTER,TOP);
    text(coloursDiv[bar].getName(),colOffset + barWidth+(border+textSpace)/2, rowOffset +bar*25 + 5);
    
    // Draw the discrete version of the continuous Brewer colour scheme.
    colOffset += barWidth+border + textSpace;
    stroke(0,100);
    inc = 1/9.0;
    
    for (float i=0; i<1; i+=inc)
    {
      fill(coloursDiv[bar].findColour(i + 0.5*inc));
      rect(colOffset + barWidth*i, rowOffset + bar*25, barWidth*inc,20);
    }
  }
  
  // Brewer qualitative colour schemes.
  colOffset = (int)((barWidth+border*2)*2.5 + textSpace*1.5);
  for (int bar=0; bar<coloursCat.length; bar++)
  {
    // Draw the Brewer colour scheme as a discrete colour bar.
    rowOffset = 24 + border + (coloursDiv.length+1)*25;
    stroke(0,100);
    
    // Find out how many discrete colours have been defined to set appropriate number of boxes in bar.
    int numColours = coloursCat[bar].getColourRules().size()-1;
    for (float i=0; i<numColours; i++)
    {
      fill(coloursCat[bar].findColour(i+1));
      rect(colOffset + barWidth*i/numColours, rowOffset + bar*25, barWidth/numColours,20);
    }
    
    // Label colour scheme.
    textAlign(RIGHT,TOP);
    fill(80);
    text(coloursCat[bar].getName(),colOffset-border, rowOffset +bar*25 + 5);
  } 
  
  noLoop();
}