import org.gicentre.utils.multisketch.*; 

// Simple example to show how two sketches can be created in separate windows.
// Version 1.3, 10th August, 2010.
// Author Jo Wood, giCentre.

// ----------------------- Sketch-wide variables -------------------------

float rotationAngle;

// -------------------------- Initialisation -----------------------------

// Sets up this sketch and adds another sketch in a separate window.
void setup()
{
  size(300,300);
  PFont font = createFont("Serif",32);
  textFont(font, 32);
  smooth();
  textAlign(CENTER,CENTER);
  fill(120,20,20);
  rotationAngle = 0;
    
  PopupWindow win = new PopupWindow(this,new AnotherSketch());
  win.setVisible(true);
}

// ----------------------- Processing draw --------------------------

// Displays some text and animates its rotation.
void draw()
{
  background(255,200,200);
  
  pushMatrix();
   translate(width/2,height/2);
   rotate(rotationAngle);
   text("Hello world",0,0);
  popMatrix();
  
  rotationAngle += 0.01;
}
