import org.gicentre.utils.gui.TextPopup;
import java.util.Random;

// Sketch to show how a text popup window can be added to a sketch
// Version 1.3, 6th February, 2016.
// Author Jo Wood, giCentre.

// ------------------ Sketch-wide variables --------------------

private TextPopup textPopup;

// --------------------- Initialisation ------------------------

// Sets up the sketch by initialising the text popup.
void setup()
{
  size(800,600);

  surface.setResizable(true);
  strokeWeight(2);  
  PFont font = createFont("serif",24);
    
  // The text popup needs to know the sketch (this) in which to draw.
  // The optional font parameter allows you to specify the font for display.
  // The last two parameters define the width and height of the border around 
  // the popup relative to the size of this sketch.
  textPopup = new TextPopup(this,font,50,80);
  textPopup.setTextSize(24);
  textPopup.setInternalMargin(18,9);
  textPopup.addText("An example title",28);
  textPopup.addText("");    // Blank line
  textPopup.addText("H - Toggle help screen on and off");
  textPopup.addText("Up/down - Increase/decrease size of internal margin");
  textPopup.addText("Left/right - Decrease/increase size of external margin");
  textPopup.addText("<Esc> - Quit the sketch");
  textPopup.addText("\n\n"); // Two blank lines
  textPopup.addText("You can change the size of the fonts displayed in this window. If the text "+
                    "is too large to fit on a single line, it will get wrapped onto the next line, "+
                    "so a large block of text can be displayed easily in this window.", 20);
}

// ------------------ Processing draw --------------------

// Draws a simple sketch with the text popup over the top.
void draw()
{
  background(255);
  
  // Draw some random stuff to represent a sketch.
  stroke(180,0,0);
  Random rand = new Random(5432);
  
  for (int i=0; i<100; i++)
  {
    line(rand.nextFloat()*width,rand.nextFloat()*height, 
         rand.nextFloat()*width,rand.nextFloat()*height);
  }
  
  // Add this to display text popup.
  textPopup.draw();
  noLoop();
}

// ------------------ Keyboard handlinng --------------------

// Allow appearance to be controlled via keyboard shortcuts.
void keyPressed()
{ 
  if ((key == 'h') || (key == 'H'))
  {
    textPopup.setIsActive(!textPopup.getIsActive());
  }
  
  if (textPopup.getIsActive())
  {
    if (key== CODED)
    {
      int margin = textPopup.getInternalMargin().width;
      int border = textPopup.getExternalMargin().width;
    
      if (keyCode == UP)
      {
        margin++;
      }
      else if (keyCode == DOWN)
      {
        margin--;
      }
      textPopup.setInternalMargin(margin,margin/2);  
    
      if (keyCode == LEFT)
      {
        border-=4;
      }
      else if (keyCode == RIGHT)
      {
        border+=4;
      }
      textPopup.setExternalMargin(border,border);  
    }
  }
  loop();
}