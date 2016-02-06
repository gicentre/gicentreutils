import org.gicentre.utils.colour.*;    // For colour tables.
import java.util.Random;

// Sketch to show how a colour picker can be used to select a colour 
// from within a sketch. Press C to toggle the colour picker.
// Version 1.2, 6th February, 2016.
// Author Jo Wood, giCentre

// ------------------ Sketch-wide variables --------------------

private ColourPicker colourPicker;
private ColourListener colourListener;
private int lineColour;

// ---------------------- Initialisation -----------------------

// Sets up the sketch by initialising the colour picker.
void setup()
{
  size(800,600);
  
  surface.setResizable(true);
  strokeWeight(2);
  lineColour = color(180,0,0);
    
  // The colour picker needs to know the sketch (this) in which to draw.
  // The last two parameters define the width and height of the border around 
  // the colour picker relative to the size of this sketch.
  colourPicker = new ColourPicker(this,50,80);
  
  // The optional listener can be used to make changes as soon as a new colour is selected.
  colourListener = new ColourListener();
  colourPicker.addPickerListener(colourListener);
}

// ------------------ Processing draw --------------------

// Draws a simple sketch with the option of displaying the colour picker.
void draw()
{
  if (brightness(lineColour) < 220)
  {
    background(220);
  }
  else
  {
    background(40);
  }
  
  // Draw some random stuff to represent a sketch.
  stroke(lineColour);
  Random rand = new Random(5432);
  
  for (int i=0; i<100; i++)
  {
    line(rand.nextFloat()*width,rand.nextFloat()*height, 
         rand.nextFloat()*width,rand.nextFloat()*height);
  }
  
  // Add this to display the colour picker.
  colourPicker.draw();
}

// ------------------ Keyboard handling --------------------

// Turns the colour picker on or off with the 'c' key.
void keyPressed()
{
  if ((key == 'c') || (key == 'C'))
  {
    colourPicker.setIsActive(!colourPicker.getIsActive());
  }
}



// -------------------- Nested classes ----------------------

// This class listens out for a colour selection using the colour picker and updates
// the sketch's lineColour accordingly.
private class ColourListener implements PickerListener
{
  
  // Responds to a colour being chosen by the colour picker.
  public void colourChosen()
  {
    int pickedColour = colourPicker.getLastColour();
    if (pickedColour != Integer.MAX_VALUE)
    {
      lineColour = pickedColour;
    }
  }
}