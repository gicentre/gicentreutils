import org.gicentre.utils.*;    // For version class

// Displays the current version of the giCentre Utilities library
// Version 1.0, 6th February, 2016.
// Author Jo Wood, giCentre

// Black centred text.
void setup()
{
  size(600,400);
  fill(0);
  textSize(22);
  textAlign(CENTER,CENTER);
}

// Displays the current version number on screen and in the console.
void draw()
{
  background(255);
  text(Version.getText(),width*0.5,height*0.4);
  println("giCentreUtils V."+Version.getVersion());
  noLoop();
}