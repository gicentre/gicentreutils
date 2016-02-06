import org.gicentre.utils.gui.HelpScreen;
import org.gicentre.utils.gui.Tooltip;          
import org.gicentre.utils.spatial.Direction;    // For tooltip anchor direction.

// Sketch to demonstrate the use of the HelpScreen and Tooltip classes
// to provide interactive help in a sketch.
// Version 2.3, 6th February, 2016
// Author Jo Wood, giCentre.

// ------------------ Sketch-wide variables --------------------

HelpScreen helpScreen;
Tooltip tip;
boolean showTip;
PFont largeFont, smallFont;

// --------------------- Initialisation ------------------------

// Initialises the display and sets up the help screen and tooltip.
void setup()
{
  size(500,500);
  
  largeFont = loadFont("Colaborate-Thin-24.vlw");
  smallFont = loadFont("Colaborate-Thin-12.vlw");
  
  int verticalSpace = 30;      // Space between title and body text of help screen in pixels.
  int headerSize = 24;         // Font size of title.
  
  helpScreen = new HelpScreen(this,largeFont);
  helpScreen.setHeader("Help screen", verticalSpace, headerSize);
  
  helpScreen.putEntry("H", "toggle help screen on/off");
  helpScreen.putEntry("T", "toggle tooltip on/off");
  helpScreen.addSpacer();                          // Can include blank lines.
  helpScreen.putEntry("Esc","Quits the sketch");
  
  helpScreen.setTextSize(18);                      // If you want to change size from default. 
  helpScreen.setBackgroundColour(color(255,245));  // Slightly transparent.
  
  helpScreen.setFooter("This message was brought to you by the giCentre", 20, 10);
  
  int tipWidth = 120;
  showTip = false;
  tip = new Tooltip(this,smallFont,12,tipWidth);
  tip.setText("This is an example of a tooltip that should fit the text inside its user-defined width");
  tip.setAnchor(Direction.SOUTH_WEST);
  tip.setIsCurved(true);
  tip.showPointer(true);
}

// ------------------ Processing draw --------------------

// Draws a slice through CIELab space using the current L value.
void draw()
{
  background(240,200,200);
  textFont(largeFont);
  
  textAlign(CENTER,CENTER);
  fill(90);
  text("Normal sketch code here",width*0.5,height*0.45);
  text("Press 'H' to toggle help on and off",width*0.5,height*0.55);
  
  // Draw tooltip at current mouse location if active.
  if (showTip)
  {
    tip.draw(mouseX,mouseY);
  }
  
  // Add the help screen drawing at the end of the draw() method so it appears 'on top'.
  if (helpScreen.getIsActive())
  {
    helpScreen.draw();
  }
}

// ------------------ Processing keyboard handling --------------------

// Toggles the help screen on and off with the 'H' key and the tooltip on 
// and off with the 'T' key.
void keyPressed()
{
  if ((key == 'h') || (key == 'H'))
  {
    helpScreen.setIsActive(!helpScreen.getIsActive());  
  } 
  else if ((key == 't') || (key == 'T'))
  {
    showTip = !showTip;
  } 
}