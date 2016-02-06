import org.gicentre.utils.move.Ease;

// Sketch to demonstrate the use of easing methods to control non-linear animation sequences.
// Animates a sequence of discs between the bottom and top of the widnow each using a different easing function.
// Shows a graph of time against distance from the bottom of the window. 
// Use the left/right arrow keys to select a different easing function and up/down arrow keys to control animation speed.
// Space bar pauses or unpauses the action.
// Each static method in the Ease class takes a value between 0-1 and returns a new value also between
// 0-1 that can be provided to the lerp() method for non-linear interpolation.
// Version 1.5, 6th February, 2016.
// Author Jo Wood, giCentre.

// ------------------ Sketch-wide variables --------------------

static final float RADIUS=15;      // Size of animated discs in pixels.
static final int graphSize = 200;  // Width and height of graph in pixels.

static final int LINEAR = 1;

static final int SIN_IN = 2;
static final int CUBIC_IN = 3;
static final int QUARTIC_IN = 4;
static final int QUINTIC_IN = 5;

static final int SIN_OUT = 6;
static final int CUBIC_OUT = 7;
static final int QUARTIC_OUT = 8;
static final int QUINTIC_OUT = 9;

static final int SIN_BOTH = 10;
static final int CUBIC_BOTH = 11;
static final int QUARTIC_BOTH = 12;
static final int QUINTIC_BOTH = 13;

static final int BOUNCE_OUT = 14;
static final int ELASTIC_IN = 15;

float t,tInc;      // t represents time scaled between 0-1. tInc is the change in time in each animation frame.
int easeStyle;     // Stores which form of easing is currently highlighted.
boolean isPaused;  // Pauses or unpauses the animation.

// ------------------- Initialisation ---------------------

// Initialises the window in which animated discs and graph are shown.
void setup()
{
  size(900,300);
  noStroke();
  textFont(loadFont("Crimson-Italic-24.vlw"));
  textAlign(RIGHT,TOP);
    
  t=0;
  tInc = 0.008;
  easeStyle = LINEAR;
  isPaused = false;
}

// ------------------ Processing draw --------------------

// Animates the discs and shows the graph of the selected easing function.
void draw()
{
  background(255);
  
  // Increment t (time) to oscillate between 0 and 1.
  if (t<=0)
  {
    tInc = abs(tInc);
  }
  else if (t >=1)
  {
    tInc = -abs(tInc);
  }
  
  t+=tInc; 
 
  // The lerp() method is used to animate between the top and bottom of the window.
  // The Ease methods are used to modify t to give a non-linear value between 0 and 1.
  // Animation oscillates between the top and bottom of the window, so the tInc value
  // which will be positive when moving up and negative when moving down, is used to
  // control the direction of the asymmetric easing functions.
  if (easeStyle == LINEAR)  fill(220,160,160); else fill(240);
  ellipse(RADIUS,lerp(height-RADIUS,RADIUS,t),RADIUS*2,RADIUS*2);
  
  if (easeStyle == SIN_IN)  fill(220,160,160); else fill(240);
  ellipse(4*RADIUS,lerp(height-RADIUS,RADIUS,Ease.sinIn(t,tInc)),RADIUS*2,RADIUS*2);
  if (easeStyle == CUBIC_IN)  fill(220,160,160); else fill(240);
  ellipse(7*RADIUS,lerp(height-RADIUS,RADIUS,Ease.cubicIn(t,tInc)),RADIUS*2,RADIUS*2);
  if (easeStyle == QUARTIC_IN)  fill(220,160,160); else fill(240);
  ellipse(10*RADIUS,lerp(height-RADIUS,RADIUS,Ease.quarticIn(t,tInc)),RADIUS*2,RADIUS*2);
  if (easeStyle == QUINTIC_IN)  fill(220,160,160); else fill(240);
  ellipse(13*RADIUS,lerp(height-RADIUS,RADIUS,Ease.quinticIn(t,tInc)),RADIUS*2,RADIUS*2);

  if (easeStyle == SIN_OUT)  fill(220,160,160); else fill(240);
  ellipse(16*RADIUS,lerp(height-RADIUS,RADIUS,Ease.sinOut(t,tInc)),RADIUS*2,RADIUS*2);
  if (easeStyle == CUBIC_OUT)  fill(220,160,160); else fill(240);
  ellipse(19*RADIUS,lerp(height-RADIUS,RADIUS,Ease.cubicOut(t,tInc)),RADIUS*2,RADIUS*2);
  if (easeStyle == QUARTIC_OUT)  fill(220,160,160); else fill(240);
  ellipse(22*RADIUS,lerp(height-RADIUS,RADIUS,Ease.quarticOut(t,tInc)),RADIUS*2,RADIUS*2);
  if (easeStyle == QUINTIC_OUT)  fill(220,160,160); else fill(240);
  ellipse(25*RADIUS,lerp(height-RADIUS,RADIUS,Ease.quinticOut(t,tInc)),RADIUS*2,RADIUS*2);

  if (easeStyle == SIN_BOTH)  fill(220,160,160); else fill(240);
  ellipse(28*RADIUS,lerp(height-RADIUS,RADIUS,Ease.sinBoth(t)),RADIUS*2,RADIUS*2);
  if (easeStyle == CUBIC_BOTH)  fill(220,160,160); else fill(240);
  ellipse(31*RADIUS,lerp(height-RADIUS,RADIUS,Ease.cubicBoth(t)),RADIUS*2,RADIUS*2);
  if (easeStyle == QUARTIC_BOTH)  fill(220,160,160); else fill(240);
  ellipse(34*RADIUS,lerp(height-RADIUS,RADIUS,Ease.quarticBoth(t)),RADIUS*2,RADIUS*2);
  if (easeStyle == QUINTIC_BOTH)  fill(220,160,160); else fill(240);
  ellipse(37*RADIUS,lerp(height-RADIUS,RADIUS,Ease.quinticBoth(t)),RADIUS*2,RADIUS*2);
  
  if (easeStyle == BOUNCE_OUT)  fill(220,160,160); else fill(240);
  ellipse(40*RADIUS,lerp(height-RADIUS,RADIUS,Ease.bounceOut(t, tInc)),RADIUS*2,RADIUS*2);
  if (easeStyle == ELASTIC_IN)  fill(220,160,160); else fill(240);
  ellipse(43*RADIUS,lerp(height-RADIUS,RADIUS,Ease.elasticIn(t)),RADIUS*2,RADIUS*2);
  
  // Draws a graph of the currently selected easing function.  
  drawGraph();
}


// ------------------ Processing key handling --------------------

// Responds to key presses. Left and right arrows control which easing function is to
// be highlighted and graphed. The up and down arrows control the speed of animation.
// The space bar pauses or unpauses the animation.
void keyPressed()
{
  if (key == ' ')
  {
    isPaused = !isPaused;
    if (isPaused)
    {
      noLoop();
    }
    else
    {
      loop();
    }
    return;
  }
  
  if (key == CODED) 
  {
    // Up and down arrows control the speed of animation.
    if (keyCode == UP)
    { 
      tInc*= 1.1;
    }
    else if (keyCode == DOWN)
    {
      tInc *= 0.9;
    }
    
    // Left and right arrows highlight a particular easing style
    else if (keyCode == RIGHT)
    {
      easeStyle++;
      if (easeStyle > ELASTIC_IN)
      {
        easeStyle = LINEAR;
      }
    }
    else if (keyCode == LEFT)
    {
      easeStyle--;
      if (easeStyle < LINEAR)
      {
        easeStyle = ELASTIC_IN;
      }
    }
  }
}

// ------------------ Private methods--------------------

// Draws a graph of the currently selected easing function.
void drawGraph()
{
  pushStyle();    // Store previously used drawing styles.
  stroke(150);
  fill(150);
  strokeWeight(3);
  
  PVector origin = new PVector(width-graphSize-RADIUS,height-30);
  
  // Draw labelled axes.
  line(origin.x,origin.y,origin.x+graphSize,origin.y);
  line(origin.x,origin.y,origin.x,origin.y-graphSize);
  text("time",origin.x+graphSize,origin.y+5);
  pushMatrix();
   translate(origin.x-24,origin.y-graphSize);
   rotate(-HALF_PI);
   text("distance",0,0);
  popMatrix();
  
  // Draw transformation function. 
  float oldX = 0;
  float oldY = 0;
  stroke(150);
  strokeWeight(0.5);
 
  float y=0;
  float px=t, py=0;
  
  if (easeStyle == LINEAR)
  {
    text("Linear (no easing)",width-RADIUS,RADIUS);
    for (float x=0.01; x<=1; x+= 0.01)
    {  
      y = x;
      line(origin.x +oldX*graphSize, origin.y -oldY*graphSize, origin.x+x*graphSize, origin.y-y*graphSize);
      oldX = x;
      oldY = y;
    }
    py = px;
  }
  else if (easeStyle == SIN_IN)
  {
    text("sinIn()",width-RADIUS,RADIUS);
    for (float x=0.01; x<=1; x+= 0.01)
    {  
      y = Ease.sinIn(x,tInc);
      line(origin.x +oldX*graphSize, origin.y -oldY*graphSize, origin.x+x*graphSize, origin.y-y*graphSize);
      oldX = x;
      oldY = y;
    }
    py = Ease.sinIn(px,tInc);
  }
  else if (easeStyle == CUBIC_IN)
  {
    text("cubicIn()",width-RADIUS,RADIUS);
    for (float x=0.01; x<=1; x+= 0.01)
    {  
      y = Ease.cubicIn(x,tInc);
      line(origin.x +oldX*graphSize, origin.y -oldY*graphSize, origin.x+x*graphSize, origin.y-y*graphSize);
      oldX = x;
      oldY = y;
    }
    py = Ease.cubicIn(px,tInc);
  }
  else if (easeStyle == QUARTIC_IN)
  {
    text("quarticIn()",width-RADIUS,RADIUS);
    for (float x=0.01; x<=1; x+= 0.01)
    {  
      y = Ease.quarticIn(x,tInc);
      line(origin.x +oldX*graphSize, origin.y -oldY*graphSize, origin.x+x*graphSize, origin.y-y*graphSize);
      oldX = x;
      oldY = y;
    }
    py = Ease.quarticIn(px,tInc);
  }
  else if (easeStyle == QUINTIC_IN)
  {
    text("quinticIn()",width-RADIUS,RADIUS);
    for (float x=0.01; x<=1; x+= 0.01)
    {  
      y = Ease.quinticIn(x,tInc);
      line(origin.x +oldX*graphSize, origin.y -oldY*graphSize, origin.x+x*graphSize, origin.y-y*graphSize);
      oldX = x;
      oldY = y;
    }
    py = Ease.quinticIn(px,tInc);
  }
  
  else if (easeStyle == SIN_OUT)
  {
    text("sinOut()",width-RADIUS,RADIUS);
    for (float x=0.01; x<=1; x+= 0.01)
    {  
      y = Ease.sinOut(x,tInc);
      line(origin.x +oldX*graphSize, origin.y -oldY*graphSize, origin.x+x*graphSize, origin.y-y*graphSize);
      oldX = x;
      oldY = y;
    }
    py = Ease.sinOut(px,tInc);
  }
  else if (easeStyle == CUBIC_OUT)
  {
    text("cubicOut()",width-RADIUS,RADIUS);
    for (float x=0.01; x<=1; x+= 0.01)
    {  
      y = Ease.cubicOut(x,tInc);
      line(origin.x +oldX*graphSize, origin.y -oldY*graphSize, origin.x+x*graphSize, origin.y-y*graphSize);
      oldX = x;
      oldY = y;
    }
    py = Ease.cubicOut(px,tInc);
  }
  else if (easeStyle == QUARTIC_OUT)
  {
    text("quarticOut()",width-RADIUS,RADIUS);
    for (float x=0.01; x<=1; x+= 0.01)
    {  
      y = Ease.quarticOut(x,tInc);
      line(origin.x +oldX*graphSize, origin.y -oldY*graphSize, origin.x+x*graphSize, origin.y-y*graphSize);
      oldX = x;
      oldY = y;
    }
    py = Ease.quarticOut(px,tInc);
  }
  else if (easeStyle == QUINTIC_OUT)
  {
    text("quinticOut()",width-RADIUS,RADIUS);
    for (float x=0.01; x<=1; x+= 0.01)
    {  
      y = Ease.quinticOut(x,tInc);
      line(origin.x +oldX*graphSize, origin.y -oldY*graphSize, origin.x+x*graphSize, origin.y-y*graphSize);
      oldX = x;
      oldY = y;
    }
    py = Ease.quinticOut(px,tInc);
  }
  
  else if (easeStyle == SIN_BOTH)
  {
    text("sinBoth()",width-RADIUS,RADIUS);
    for (float x=0.01; x<=1; x+= 0.01)
    {  
      y = Ease.sinBoth(x);
      line(origin.x +oldX*graphSize, origin.y -oldY*graphSize, origin.x+x*graphSize, origin.y-y*graphSize);
      oldX = x;
      oldY = y;
    }
    py = Ease.sinBoth(px);
  }
  else if (easeStyle == CUBIC_BOTH)
  {
    text("cubicBoth()",width-RADIUS,RADIUS);
    for (float x=0.01; x<=1; x+= 0.01)
    {  
      y = Ease.cubicBoth(x);
      line(origin.x +oldX*graphSize, origin.y -oldY*graphSize, origin.x+x*graphSize, origin.y-y*graphSize);
      oldX = x;
      oldY = y;
    }
    py = Ease.cubicBoth(px);
  }
  else if (easeStyle == QUARTIC_BOTH)
  {
    text("quarticBoth()",width-RADIUS,RADIUS);
    for (float x=0.01; x<=1; x+= 0.01)
    {  
      y = Ease.quarticBoth(x);
      line(origin.x +oldX*graphSize, origin.y -oldY*graphSize, origin.x+x*graphSize, origin.y-y*graphSize);
      oldX = x;
      oldY = y;
    }
    py = Ease.quarticBoth(px);
  }
  else if (easeStyle == QUINTIC_BOTH)
  {
    text("quinticBoth()",width-RADIUS,RADIUS);
    for (float x=0.01; x<=1; x+= 0.01)
    {  
      y = Ease.quinticBoth(x);
      line(origin.x +oldX*graphSize, origin.y -oldY*graphSize, origin.x+x*graphSize, origin.y-y*graphSize);
      oldX = x;
      oldY = y;
    }
    py = Ease.quinticBoth(px);
  }
  
  else if (easeStyle == BOUNCE_OUT)
  {
    text("bounceOut()",width-RADIUS,RADIUS);
    for (float x=0.01; x<=1; x+= 0.01)
    {  
      y = Ease.bounceOut(x,tInc);
      line(origin.x +oldX*graphSize, origin.y -oldY*graphSize, origin.x+x*graphSize, origin.y-y*graphSize);
      oldX = x;
      oldY = y;
    }
    py = Ease.bounceOut(px,tInc);
  }
  else if (easeStyle == ELASTIC_IN)
  {
    text("elasticIn()",width-RADIUS,RADIUS);
    for (float x=0.01; x<=1; x+= 0.01)
    {  
      y = Ease.elasticIn(x);
      line(origin.x +oldX*graphSize, origin.y -oldY*graphSize, origin.x+x*graphSize, origin.y-y*graphSize);
      oldX = x;
      oldY = y;
    }
    py = Ease.elasticIn(px);
  }
 
  // Draw the disc's current position on the graph.
  noStroke();
  fill(220,160,160);
  ellipse(origin.x + px*graphSize, origin.y - py*graphSize,10,10);
  stroke(220,160,160);
  line(origin.x + px*graphSize, origin.y - py*graphSize, origin.x + px*graphSize, origin.y);
  line(origin.x + px*graphSize, origin.y - py*graphSize, origin.x, origin.y-py*graphSize);
  
  popStyle();    // Restores previously used drawing styles.
}