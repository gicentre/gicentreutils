// Represents a single ball. A ball has a location, a velocity vector and a colour.
// Version 1.4, 6th February, 2016.
// Author Jo Wood.

class Ball implements Locatable
{
  // ------------------ Object variables --------------------
  
  private PVector location,movement;
  private int colour;
 
  // -------------------- Constructor -----------------------
 
  // Creates a ball random location, colour and trajectory.   
  Ball()
  {
    location = new PVector(random(RADIUS,width-RADIUS), random(RADIUS,height-RADIUS));
    movement = new PVector(random(-2,2),  random(-2,2));
    colour   = color(random(50,250),random(50,150), random(50,150));
  }
 
  // ---------------------- Methods -------------------------  
  
  // Moves the ball to a new location. Will bounce off the sides of the sketch
  // if it is about to go out of bounds.
  void move()
  {    
    float newX = location.x + movement.x;
    float newY = location.y + movement.y;
    
    if ((newX < RADIUS) || (newX > width-RADIUS))
    {
      movement.x = -movement.x;
      newX = location.x + movement.x;
    }
  
    if ((newY < RADIUS) || (newY > height-RADIUS))
    {
      movement.y = -movement.y;
      newY = location.y + movement.y;
    }
    
    location.x += movement.x;
    location.y += movement.y;
  }
  
  // Checks to see if this ball is about to collide with another ball. If it is,
  // the two balls will bounce off each other with a perfect elastic collision.
  // @param otherBall Ball that may or may not collide with this one.
  // @return True if the two balls have collided and have bounced off each other.
  boolean checkBounce(Ball otherBall)
  {
    if (otherBall != this)
    {  
      float newX = location.x + movement.x;
      float newY = location.y + movement.y;
      float otherNewX = otherBall.location.x + otherBall.movement.x;
      float otherNewY = otherBall.location.y + otherBall.movement.y;
      
      float dx = otherNewX - newX; 
      float dy = otherNewY - newY;
      float distSq = dx*dx + dy*dy; 
   
      if (distSq <= RADIUS*RADIUS)
      {
        // The two balls are within a radius of each other so they are about to bounce.
        float collisionAngle = atan2(dy, dx); 
        float collisionX = cos(collisionAngle);
        float collisionY = sin(collisionAngle);
        float collisionXTangent = cos(collisionAngle+HALF_PI);
        float collisionYTangent = sin(collisionAngle+HALF_PI);
        
        float v1 = sqrt(movement.x*movement.x+movement.y*movement.y);
        float v2 = sqrt(otherBall.movement.x*otherBall.movement.x+otherBall.movement.y*otherBall.movement.y);
        
        float d1 = atan2(movement.y, movement.x);
        float d2 = atan2(otherBall.movement.y, otherBall.movement.x);
        
        float v1x = v1*cos(d1-collisionAngle);
        float v1y = v1*sin(d1-collisionAngle);
        
        float v2x = v2*cos(d2-collisionAngle);
        float v2y = v2*sin(d2-collisionAngle);
         
        movement.x = collisionX*v2x + collisionXTangent*v1y;
        movement.y = collisionY*v2x + collisionYTangent*v1y;
        
        otherBall.movement.x = collisionX*v1x + collisionXTangent*v2y;
        otherBall.movement.y = collisionY*v1x + collisionYTangent*v2y;
        
        return true;
      } 
    }
    return false;    // No bounce.
  }    
  
  // Reports the colour of the ball.
  // @return Colour of the ball.
  int getColour()
  {
    return colour;
  }
  
  // Reports the location of this ball. This method is necessary because Ball
  // implements the Locatable interface.
  //  @return Location of this ball.
  public PVector getLocation()
  {
    return location;
  } 
}