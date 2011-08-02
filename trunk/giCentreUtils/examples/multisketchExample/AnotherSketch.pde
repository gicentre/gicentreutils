// Simple embedded sketch that can be placed in its own window.
// Version 1.3, 10th August, 2010.
// Author Jo Wood, giCentre.

class AnotherSketch extends EmbeddedSketch
{
  // -------------------- Sketch-wide variables -----------------------
  
  float textScale;
  
  // ----------------------- Initialisation ---------------------------
  
  // Initialises the sketch ready to display some animated text.
  void setup()
  {
    size(300,300);
    PFont font = createFont("SansSerif",24);
    textFont(font, 24);
    smooth();
    textAlign(CENTER,CENTER);
    fill(20,120,20);
    textScale = 0;
  }

  // ----------------------- Processing draw --------------------------
  
  // Displays some text and animates a change in size.
  void draw()
  {
    super.draw();   // Should be the first line of draw().
    background(200,255,200);
      
    pushMatrix();
     translate(width/2,height/2);
     scale(0.1+sin(textScale),1);
     text("Hello again",0,0);
    popMatrix();
    
    textScale += 0.02;
  }
}
