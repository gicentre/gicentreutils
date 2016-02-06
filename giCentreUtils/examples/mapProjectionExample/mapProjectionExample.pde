import org.gicentre.utils.spatial.*;    // For map projections.

// Displays a GPS track and map using the WebMercator projection.
// Version 1.1, 6th February, 2016.
// Author Jo Wood, giCentre, City University London.

// --------------------- Sketch-wide variables ----------------------

ArrayList<PVector>coords;    // Projected GPS coordinates.
PImage backgroundMap;        // OpenStreetMap.
PVector tlCorner,brCorner;   // Corners of map in WebMercator coordinates.

// ------------------------ Initialisation --------------------------

void setup()
{ 
  size(800,600);
  readData();
}

// ------------------------ Processing draw -------------------------

void draw()
{
  // Background map.
  image(backgroundMap,0,0,width,height);
  
  // Projected GPS coordinates
  noFill();
  stroke(150,50,50,150);
  strokeWeight(6);
  
  beginShape();
  for (PVector coord : coords)
  {
    PVector scrCoord = geoToScreen(coord);
    vertex(scrCoord.x,scrCoord.y);  
  }
  endShape();
  
  noLoop();
}

// ---------------------------- Methods -----------------------------

void readData()
{
  // Read the GPS data and background map.
  String[] geoCoords = loadStrings("gpsTrack.txt");
  backgroundMap = loadImage("background.png");
  
  WebMercator proj = new WebMercator();
  
  // Convert the GPS coordinates from lat/long to WebMercator
  coords = new ArrayList<PVector>();  
  for (String line: geoCoords)
  {
    String[] geoCoord = split(line.trim()," ");
    float lng = float(geoCoord[0]);
    float lat = float(geoCoord[1]);
    coords.add(proj.transformCoords(new PVector(lng,lat)));
  } 
  
  // Store the WebMercator coordinates of the corner of the map.
  // The lat/long of the corners was provided by OpenStreetMap
  // when exporting the map tile.
  tlCorner = proj.transformCoords(new PVector(-0.07,52.28));
  brCorner = proj.transformCoords(new PVector( 1.64,51.54));
}

// Convert from WebMercator coordinates to screen coordinates.
PVector geoToScreen(PVector geo)
{
  return new PVector(map(geo.x,tlCorner.x,brCorner.x,0,width),
                     map(geo.y,tlCorner.y,brCorner.y,0,height));
}