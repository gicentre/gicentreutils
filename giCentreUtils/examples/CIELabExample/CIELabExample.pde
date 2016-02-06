import org.gicentre.utils.colour.*;    // For CIELab colour conversion.
import java.awt.Color;                 // For Java's own Color class.

// Sketch to demonstrate the use of CIELab colour conversion. Use up and down
// arrows to change the 'L' value of the CIELab slice. 'r' resets the view to use an
// L value of 50. 'n' toggles nearest neighbour extrapolation of out-of-gamut colours.
// Version 1.3, 6th February, 2016
// Author Jo Wood, giCentre.

// ------------------ Sketch-wide variables --------------------

CIELab converter;       // Does the colour conversion work.
float L;                // Current L value for CIELab slice
boolean showNearest;    // Determines if out-of-gamut colours are shown.

// --------------------- Initialisation ------------------------

// Initialises the display and sets up the CIELab converter.
void setup()
{
  size(600,600);
  converter = new CIELab();
  L = 50;
  showNearest = false;
  textFont(loadFont("Helvetica-12.vlw"));
  textAlign(RIGHT,TOP);
}

// ------------------ Processing draw --------------------

// Draws a slice through CIELab space using the current L value.
void draw()
{
  background(255);
  noStroke();
      
  // Transform from colour space to screen space
  pushMatrix();
  scale(width/200.0,-height/200.0);
  translate(100,-100);

  float inc = 1.0;
  for (float a=-100; a <=100; a+=inc)
  {
    for (float b=-100; b<=100; b+=inc)
    {
      Color colour = converter.getColour(L,a,b,showNearest);
      if (colour != null)
      {
        fill(colour.getRGB());
        rect(a,b,inc,inc);
      }
    }
  }
  
  // Draw axes through CIELab origin and show current L value.
  stroke(0,80);
  strokeWeight(200.0/width);
  line(0,100,0,-100);
  line(-100,0,100,0);
  
  popMatrix();
  
  if (showNearest)
  {
    fill(40);
  }
  else
  {
    fill(120);
  }
  text("L="+(int)L,40,0);
  text("a",width,height/2);
  text("b",width/2,0);
  
  noLoop();
}

// ------------------ Processing keyboard handling --------------------

// Allows L value to be changed with up and down arrows, reset with the 'r' key
// and nearest neighbour extrapolation to be switched on or off with the 'n' key.
void keyPressed()
{
  if (key == 'n')
  {
    showNearest = !showNearest;
    loop();
  }
  else if (key == 'r')
  {
    L = 50;
    loop();
  }
  
  if (key==CODED)
  {
    if ((keyCode==UP) && (L<100))
    { 
      L++;
      loop();
    }
    else if ((keyCode==DOWN) && (L>0))
    {
      L--;
      loop();
    }    
  }
}