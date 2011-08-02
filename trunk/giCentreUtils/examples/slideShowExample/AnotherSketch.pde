// Simple embedded sketch that appears in its own window or panel.
// VersionVersion 1.4, 11th January, 2011.
// Author Jo Wood, giCentre.

class AnotherSketch extends EmbeddedSketch
{  
  // ------------------ Sketch-wide variables --------------------
  
  float textScale;
  
  // --------------------- Initialisation ------------------------
  
  void setup()
  {
    size(300,300);
    smooth();
    textAlign(CENTER,CENTER);
    fill(20,120,20);
    textScale = 0;
  }

  // -------------------- Processing draw -----------------------
  
  void draw()
  {
    super.draw();
    background(200,255,200);
      
    pushMatrix();
     translate(width/2,height/2);
     scale(0.1+sin(textScale),1);
     text("Another animated sketch but without timer",0,0);
    popMatrix();
    
    textScale += 0.02;
  }
}
