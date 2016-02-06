import org.gicentre.utils.geom.*;   // For hash grid and Locatable interface
import org.gicentre.utils.FrameTimer;

// Sketch to animate a set of coloured balls bouncing off each other in a box.
// Demonstrates the use of a HashGrid to improve the efficiency of collision 
// detection. Also shows how a frame timer can be used to report frame rate
// during drawing.
// Version 1.4, 6th February, 2016.
// Author Jo Wood, giCentre.

// ------------------ Sketch-wide variables --------------------

HashGrid<Ball> balls;              // Stores collection of locatable balls.
FrameTimer timer;                  // Used to display frame rate.
static final int RADIUS = 10;      // Size of each ball.
static final int NUM_BALLS = 1000; // Number of balls to animate.

// ----------------------- Initialisation ---------------------------

// Initialises the sketch with a set of bouncing balls.
void setup()
{
  size(800,500);
  noStroke();
  timer = new FrameTimer(50);  // Frame rate to be displayed every 50 frames.
    
  // Add lots of balls to the hash grid.
  balls = new HashGrid(width,height,RADIUS+1);
  for (int i=0; i<NUM_BALLS; i++)
  {
    balls.add(new Ball());
  }
}

// ----------------------- Processing draw --------------------------

// Draws the balls and bounces them off each other.
void draw()
{
  timer.displayFrameRate();  // Report frame rate.
  
  fill(255,80);        // Transparent backgorund leaves 'trails' with movement.
  rect(0,0,width,height);
    
  // Draw the balls and check for imminent collisions.
  for (Ball ball : balls)
  {
    fill(ball.getColour());
    ellipse(ball.getLocation().x,ball.getLocation().y,RADIUS,RADIUS);
       
    // Get the hash grid to provide all the balls close to the current ball.
    for (Ball otherBall : balls.get(ball.getLocation()))
    {
      // See if the neighbouring balls are about to bounce off each other.
      ball.checkBounce(otherBall);
    }
  }
  
  // Move all the balls to their new position.
  for (Ball ball : balls)
  {
    ball.move();
  }
   
  // Update the hash grid with new position of all the balls. 
  balls.updateAll();
}