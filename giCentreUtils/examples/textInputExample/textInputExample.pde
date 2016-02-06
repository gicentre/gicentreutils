import org.gicentre.utils.gui.TextInput; 

// Sketch to demonstrate the use of the TextInput class for gathering typed text.
// Version 1.3, 6th February, 2016.
// Author Jo Wood, giCentre.

// --------------------- Sketch-wide variables ----------------------

TextInput textInput;
boolean isFinished;

// ------------------------ Initialisation --------------------------

// Initialises the text to be used for display and input.
void setup()
{
  size(640,90);
  
  PFont font = createFont("serif",24);
  textInput = new TextInput(this,font,24);
  isFinished = false;
}

// ------------------------ Processing draw -------------------------

// Draws prompting text and waits for text input.
void draw()
{
  background(255);
        
  fill(120,60,60);
  text("Type in some text and press return when finished.",30,30);
        
  if (isFinished == false)
  {
    textInput.draw(30,50);
  }
  else
  {
    text("You just said '"+textInput.getText()+"'. Press return to edit text.", 30, 60);
  }
   noLoop();
}


// ----------------------- Keyboard handling ------------------------

// Intercepts the return key and passes all other key presses to the 
// text input object. Since this particular sketch uses noLoop(), we
// also have to tell the sketch to redraw itself when a key is pressed.
void keyPressed()
{
  if ((key == RETURN) || (key == ENTER))
  {
    isFinished = !isFinished;
  }
  else
  {
    if (!isFinished) textInput.keyPressed();
  }
  
  loop();
}