import org.gicentre.utils.stat.StandardEllipse;

// Sketch to demonstrate the use of the StandardEllipse class. Draws some 
// randomly jittering points then their weighted and unweighted standard ellipse.
// Version 1.5, 6th February 2016.
// Author Jo Wood, giCentre.

// ---------------------- Sketch-wide variables ---------------------

ArrayList<PVector> points;
static final int NUM_POINTS = 150;
static final int HIGH_WEIGHT = 10;

// ------------------------ Initialisation --------------------------

// Creates a set of points from which the standard ellipses will be calculated.
void setup()
{
  size(700,300);
 
  points = new ArrayList();
  
  for (int i=0; i<NUM_POINTS; i++)
  {  
    if (random(1) < 0.5)
    {
      // Add a highly weighted point somewhere above-left of centre.
      points.add(new PVector(width/6 + random(width/6),
                            height/3+random(-height/10,height/10),
                            HIGH_WEIGHT));
    }
    else
    {
      // Add a unit weighted point anywhere in the central belt of the region.
      points.add(new PVector(width/2 + random(-width/3,width/3),
                             height/2+random(-height/6,height/6)));
    }
  }
}

// ------------------ Processing draw --------------------

// Draws the points, randomly perturbs them and draws their standard ellipse.
void draw()
{
  background(255);
  strokeWeight(3);
    
  // Plot the original points and make them move about a bit.
  for (PVector p : points)
  {
    p.x += random(-2,2);
    p.y += random(-2,2);
    
    if (p.z == 0)
    {
      stroke(128,80,80);
    }
    else
    {
      stroke(183,80,80);
    }    
    point(p.x,p.y);
  }
  
  strokeWeight(1);
  
  // Calculate the standard ellipse of the points.
  StandardEllipse standardEllipse = new StandardEllipse(points);
  
  // Draw the unweighted version of the ellipse.
  stroke(128,128,183);
  fill(128,128,183,50);
  standardEllipse.draw(this);
  
  // Draw the ellipse axes.
  stroke(80,80,80,100);
  standardEllipse.drawAxes(this);
  
   // Draw the weighted version of the ellipse.
  standardEllipse.setIsWeighted(true);
  stroke(193,128,128);
  fill(183,128,128,50);
  standardEllipse.draw(this);
  
  // Draw the ellipse axes.
  stroke(80,80,80,100);
  standardEllipse.drawAxes(this);
}