import org.gicentre.utils.stat.*;        // For chart classes.

// Sketch to demonstrate the use of the BarChart class to draw simple bar charts.
// Version 1.3, 6th February, 2016.
// Author Jo Wood, giCentre.

// --------------------- Sketch-wide variables ----------------------

BarChart barChart;
PFont titleFont,smallFont;

// ------------------------ Initialisation --------------------------

// Initialises the data and bar chart.
void setup()
{
  size(800,300);
  smooth();
  noLoop();
  
  titleFont = loadFont("Helvetica-22.vlw");
  smallFont = loadFont("Helvetica-12.vlw");
  textFont(smallFont);

  barChart = new BarChart(this);
  barChart.setData(new float[] {2462,2801,3280,3983, 4490, 4894, 5642, 6322, 6489,
                                6401,7657,9649,9767,12167,15154,18200,23124,28645,39471});
  barChart.setBarLabels(new String[] {"1830","1840","1850","1860","1870","1880","1890",
                                      "1900","1910","1920","1930","1940","1950","1960",
                                      "1970","1980","1990","2000","2010"});
  barChart.setBarColour(color(200,80,80,100));
  barChart.setBarGap(2); 
  barChart.setValueFormat("$###,###");
  barChart.showValueAxis(true); 
  barChart.showCategoryAxis(true); 
}

// ------------------ Processing draw --------------------

// Draws the graph in the sketch.
void draw()
{
  background(255);
  
  barChart.draw(10,10,width-20,height-20);
  fill(120);
  textFont(titleFont);
  text("Income per person, United Kingdom", 70,30);
  float textHeight = textAscent();
  textFont(smallFont);
  text("Gross domestic product measured in inflation-corrected $US", 70,30+textHeight);
}