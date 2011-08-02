//import fullscreen.*;                       // Requires Hansi Raber's 'fullscreen' libary.
import org.gicentre.utils.multisketch.*;   // For embedded sketches and slide classes.
import java.awt.GridLayout;                // For Java's Grid layout.

// Sketch to show how to construct a slide show containg text, images and sketches.
// Can use the full screen but only if Hansi Raber's 'fullscreen' library is installed.
// Version 1.4, 10th August, 2010.
// Author Jo Wood, giCentre.

// ------------------ Sketch-wide variables --------------------

SlideShow slideShow;   // Needs to be object wide if sketches are to show the timer.
PFont font;            // Needs to be object wide if sketch and slide timers use same font.

// ---------------------- Initialisation -----------------------

void setup()
{
  // For a fullscren application uncomment the line below.
  //createFullScreen();
  
  // For a windowed application comment the line above and use line below instead.
  size(1024,768);
  
  // This slide show sketch should not loop and does not use the draw() method.
  noLoop();
  
  // For efficiency it is best to create the fonts for the slide show once at the start.
  font = loadFont("BonvenoCF-Light-48.vlw");
  
  // The slide show is added to the centre of the sketch area.
  setLayout(new GridLayout(1,1));
	    
  // Create the slide show object and add an optional timer.
  slideShow = new SlideShow(this);
  slideShow.addCountdownTimer(120,font);
  
  // You can optionally redefine the default advance and retreat keys
  // This example uses the up and down arrows instead of page up and page down
  slideShow.setAdvanceKey(DOWN);
  slideShow.setRetreatKey(UP);
	  		  
  // Each slide has a default font that must be provided in the constructor.
  // Text is added using addLine(), images added using addImage().
  Slide slide = new Slide(font);	  
  slide.addLine("This is slide one");
  slide.addImage(loadImage("portrait.jpg"));
  slide.addLine("with an image caption",font,18);
  slideShow.addSlide(slide);
  
  // In this example, a sketch is created that will be added to more than one slide in the show.
  ASketch aSketch = new ASketch();
  slideShow.addSketch(aSketch);
	  
  // Each slide can have its own default font, font size, colour and alignment.
  // You can also set the leading (vertical gap) between lines of text.
  slide = new Slide(font,58, color(90,0,0),LEFT);
  slide.setLeading(30);
  slide.addLine("This is slide three");
  slide.addLine("with default left aligned text");
  slide.addLine("and leading of 30 pixels.");
  slide.addImage(loadImage("landscape.jpg"),0,300,RIGHT);
  slide.addLine("A right aligned resized image.    ",font,24,color(90,0,0),RIGHT);
  slideShow.addSlide(slide);
	  		  
  // All objects in a slide can be vertically aligned to the top, centre or bottom.
  // You can also set the spacing borders around a slide.
  slide = new Slide(font,30,color(0,100),CENTER,BOTTOM);
  slide.setBorder(0,80,0,0);      // The 4 parameters are top,bottom,left, right all measured in pixels.
  slide.addLine("This is slide four with a caption near the bottom");
  slideShow.addSlide(slide);
	  
  // This inserts the previously created sketch at a second point in the slide show.
  slideShow.addSketch(aSketch);
	  
  // This slide shows how the slide default values can be overridden of individual lines of text.
  slide = new Slide(font);
  slide.addLine("Slide Six");
  slide.addLine("with a supplementary right-aligned line",font,18,color(50,90,50),RIGHT);
  slide.addLine("a left-aligned one",font,24,color(50,10,120),LEFT);
  slide.addLine("and a fourth line that is centred.", font, 28);
  slideShow.addSlide(slide);

  // A second sketch is added here. This one does not display the timer.
  slideShow.addSketch(new AnotherSketch());
          
  // A final simple slide.
  slide = new Slide(font);
  slide.addLine("The end");
  slideShow.addSlide(slide);
	  
  // The slide show must be added to this master sketch in order for it to be visible.
  add(slideShow);
  
  // This starts the slide show off.
  slideShow.startShow();
}


/** Method to set up a full screen set to the current resolution.
  * Note this will only work if you have hansi raber's FullScreen 
  * library for Processing installed. See
  * http://www.superduper.org/processing/fullscreen_api
  */
  
/* UNCOMMENT THIS METHOD IF YOU HAVE FULLSCREEN INSTALLED
void createFullScreen()
{
  // Set size to maximum resolution
  Dimension[] resolutions = FullScreen.getResolutions(0);
  size(resolutions[0].width, resolutions[0].height);
  
  // Create the fullscreen object and enter full screen mode.
  FullScreen fs = new FullScreen(this); 
  fs.enter(); 
}
*/
