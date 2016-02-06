import org.gicentre.utils.move.*;    // For the zoomer.
import java.text.*;                  // For number formatting.

// Simple sketch to demonstrate the ZoomPan class for interactively zooming and
// panning a sketch's display.
// Version 1.6, 6th February, 2016.
// Author Jo Wood, giCentre.

// ------------------ Sketch-wide variables --------------------

ZoomPan zoomer;    // This should be declared outside any methods.
PVector mousePos;  // Stores the mouse position.
                   // For pretty formatting of mouse coordinates.
NumberFormat formatter = new DecimalFormat("#.0");

// --------------------- Initialisation ------------------------

void setup()
{
  size(600,300);
  textFont(createFont("serif",12))
  ;  
  zoomer = new ZoomPan(this);  // Initialise the zoomer.
  zoomer.setMouseMask(SHIFT);  // Only zoom if the shift key is down.
                           
                               // Monitor end of zoom/pan events.
  zoomer.addZoomPanListener(new MyListener());
}

// ------------------ Processing draw --------------------

void draw()
{
  background(221,221,204); 
  
  pushMatrix();    // Store a copy of the unzoomed screen transformation.
  
  zoomer.transform(); // Enable the zooming/panning.
  
  // Do some drawing that can be zoomed and panned.  
  fill(170,128,128);
  stroke(50);
  ellipse(width/2,height/2,50,50);
  noStroke();
  fill(255);
  textAlign(CENTER,CENTER);
  
  text("Zoom",width/2,height/2);
  
  //  Get the mouse position taking into account any zooming and panning.
  mousePos = zoomer.getMouseCoord();
  
  // Do some drawing that will not be zoomed or panned.
  popMatrix();    // Restore the unzoomed screen transformation.
  
  fill(255,255,255,200);
  rect(0,height-21,width-1,20);
  fill(80);
  textAlign(LEFT,BOTTOM);
  text("Hold shift down and drag mouse to zoom and pan. Press 'R' to reset view. "+
       "Mouse at "+ formatter.format(mousePos.x)+" , "+formatter.format(mousePos.y),10,height);
}

// ------------------ Processing keyboard handling --------------------

// Reset the zoomer to its untransformed view if the 'R' key is pressed.
void keyPressed()
{
  if (key=='r')
  {
    zoomer.reset();
  }
}

// -------------------------- Nested classes --------------------------

// Simple class to show how the end of a zoom or pan event can be monitored.
// This probably isn't necessary for most sketches but is illustrated here
// for those who need it.
class MyListener implements ZoomPanListener
{
  void panEnded()
  {
    println("Panning stopped");
  }
  
  void zoomEnded()
  {
    println("Zooming stopped");
  }
}