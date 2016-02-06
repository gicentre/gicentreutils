import org.gicentre.utils.colour.*;    // For CIELuv colour conversion.
import java.awt.Color;                 // For Java's own Color class.

// Sketch to demonstrate the use of CIELuv colour conversion. Use up and down
// arrows to change the hue value of the CIELCh slice. 'r' resets the view to use a
// hue value of 0 (red).
// Version 1.4, 6th February, 2016
// Author Jo Wood, giCentre.

// ---------------- Sketch-wide variables -----------------

CIELuv converter;       // Does the colour conversion work.
int h;                  // Current h (hue) value for CIELCh slice
int L;
boolean useNearest;

// ------------------- Initialisation ---------------------

// Initialises the display and sets up the CIELuv converter.
void setup()
{
  size(500,500);
  converter = new CIELuv();
  h = 0;
  L = 50;
  useNearest = false;
  textFont(loadFont("Helvetica-12.vlw"));
  textAlign(RIGHT,TOP);
}

// ------------------ Processing draw --------------------

// Draws a slice through CIELuv space holding hue constant.
void draw()
{
  background(240);
  noStroke();
       
  // Transform from colour space to screen space
  pushMatrix();
  translate(20,height-20);
  scale((width-20)/200.0,-(height-20)/105.0);
  
  float inc = 0.5;
  for (float L=0.01; L<=100; L+=inc)
  {
    for (float C=-0; C<=200; C+=inc)
    {
      Color colour = converter.getColourFromLCh(L,C,h,useNearest);
      if (colour != null)
      {
        fill(colour.getRGB());
        rect(C,L,inc+0.5,inc+0.5);
      }
    }
  }
    
  // Draw axes through CIECh origin and show current hue value.
  stroke(0,80);
  strokeWeight(50.0/width);
  line(0,0,180,0);
  popMatrix();
  
  fill(120);
  text("h="+(int)h,width-40,20);
  text("L",15,20);
  text("C",width-40,height-16);
  
  noLoop();
}

// ------------------ Processing keyboard handling --------------------

// Allows L value to be changed with up and down arrows, reset with the 'r' key
// and nearest neighbour extrapolation to be switched on or off with the 'n' key.
void keyPressed()
{
  if (key == 'r')
  {
    h = 0;
    loop();
  }
  else if (key == 'n')
  {
    useNearest = !useNearest;
    loop();
  }
  
  if (key==CODED)
  {
    if (keyCode==UP)
    { 
      h = (h+2)%360;
      L += 2;
      loop();
    }
    else if (keyCode==DOWN)
    {
      h -=2;
      L -=2;
      if (h < 0)
      {
        h += 360;
      }
      loop();
    }    
  }
}