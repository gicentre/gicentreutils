import org.gicentre.utils.colour.*;    // For colour tables.

// Sketch to demonstrate the use of customised colour tables. A ColourTable 
// object consists of a set of ColourRules. Each rule maps a numeric value to a
// colour. The colour to be associated with any value can be found by calling the
// findColour() method of a colour table. If the rules are continuous, the 
// returned colour is interpolated between the two colour rules closest to the 
// given value. If rules are discrete, only exact matches are mapped to a colour.
// Version 1.3, 6th February, 2016.
// Author Jo Wood, giCentre.

// ------------------ Sketch-wide variables --------------------

ColourTable cTable1, cTable2, cTable3;      // Colour tables to use.

// ---------------------- Initialisation -----------------------

// Creates the colour tables to display and save as files.
void setup()
{
  size(500,250);
  
  // Create a continuous Brewer colour table (YlOrBr6).
  cTable1 = new ColourTable();
  cTable1.addContinuousColourRule(0.5/6, 255,255,212);
  cTable1.addContinuousColourRule(1.5/6, 254,227,145);
  cTable1.addContinuousColourRule(2.5/6, 254,196, 79);
  cTable1.addContinuousColourRule(3.5/6, 254,153, 41);
  cTable1.addContinuousColourRule(4.5/6, 217, 95, 14);
  cTable1.addContinuousColourRule(5.5/6, 153, 52,  4);
  
  // Create a preset colour table and save it as a file
  cTable2 = ColourTable.getPresetColourTable(ColourTable.SPECTRAL,0,1);
  ColourTable.writeFile(cTable2,createOutput("data/colorBrewer.ctb"));
  
  // Read in a colour table from a ctb file.  
  cTable3 = ColourTable.readFile(createInput("data/imhofLand3.ctb")); 
}


// ------------------ Processing draw --------------------

// Draws the colour tables as horizontal colour bars.
void draw()
{
  background(255);
  
  // Draw the continuous Brewer colour table.
  float inc = 0.001;
  for (float i=0; i<1; i+=inc)
  {
    fill(cTable1.findColour(i));
    stroke(cTable1.findColour(i));
    rect(width*i,10,width*inc,50);
  }

  // Draw the discrete version of the Brewer colour table.
  stroke(0);
  inc = 1/6.0;
  for (float i=0; i<1; i+=inc)
  {
    fill(cTable1.findColour(i + 0.5*inc));
    rect(width*i,70,width*inc,50);
  }
  
  // Draw the preset colour table.
  inc = 0.001;
  for (float i=0; i<1; i+=inc)
  {
    fill(cTable2.findColour(i));
    stroke(cTable2.findColour(i));
    rect(width*i,130,width*inc,50);
  }
  
  // Draw the colour table loaded from a file.
  inc = 0.001;
  for (float i=0; i<1; i+=inc)
  {
    fill(cTable3.findColour(i));
    stroke(cTable3.findColour(i));
    rect(width*i,190,width*inc,50);
  }
  
  noLoop();
}