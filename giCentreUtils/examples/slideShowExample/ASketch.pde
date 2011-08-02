// Simple embedded sketch that appears in its own window or panel.
// Version 1.4, 11th January, 2011.
// Author Jo Wood, giCentre.

class ASketch extends EmbeddedSketch
{  
  // ------------------ Sketch-wide variables --------------------
  
  float textScale;
  float rotationAngle;
  
  // ---------------------- Initialisation -----------------------
  
  void setup()
  {
    size(300,300);
    smooth();
    textAlign(CENTER,CENTER);
    fill(120,20,20);
    rotationAngle = 0;
  }

  // --------------------- Processing draw -----------------------
  
  void draw()
  {
    super.draw();
    background(255,200,200);
  
    pushMatrix();
     translate(width/2,height/2);
     rotate(rotationAngle);
     text("An animated sketch showing timer",0,0);
    popMatrix();
  
    rotationAngle += 0.01;
    
    // This optional line allows the timer to be displayed over a sketch.
    // Makes use of the slideShow and font objects defined in the main sketch.
    slideShow.displayTime(this, font);
  }
}
