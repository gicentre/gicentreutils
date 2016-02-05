package org.gicentre.utils.move;

import java.awt.geom.Rectangle2D;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

// ********************************************************************************
/**
 * Class for limiting drawing to a fixed rectangular area.
 * 
 * Simple usage example:<br>
 * <pre>
 * import org.gicentre.utils.gui.Clipper;
 * Clipper clipper;
 * 
 * void setup() {
 *     //...
 *     size(600, 400);
 *     clipper = new Clipper(this, 100, 100, 300, 200); // x, y, width, height
 *     //...
 * }
 * 
 * void draw() {
 *     drawSomeStuffAsUsual();
 *     
 *     clipper.startClipping();
 *     drawStuffYouWantToClip();
 *     clipper.stopClipping();
 *     
 *     drawSomeOtherStuffAsUsual();
 * }
 * </pre>
 * 
 * @deprecated This class should no longer be needed. Use Processing's <code>clip()</code> and  
 *             <code>noClip()</code> methods instead.
 * @author Alexander Kachkaev with minor modifications by Jo Wood.
 * @version 3.4, 5th February, 2016.
 *
 */
// ********************************************************************************

@Deprecated
public class Clipper
{
	// ----------------------------- Object variables ------------------------------
	
	protected PApplet applet;
	private boolean isEnabled,isClipping;
	private float x,y,width,height;			// Dimensions of clipping rectangle.
	
	// ------------------------------- Constructors --------------------------------
	
	/** Creates a new clipper set to the bounds of the given sketch.
	 *  @param applet Sketch in which to enable clipping.
	 */
	public Clipper(PApplet applet)
	{
		this(applet,0,0,applet.width,applet.height);
	}

	/** Creates a new Clipper instance capable of limiting all drawing to within the given rectangular bounds.
	 *  Clipping is enabled by default, but not active.
	 *  @param applet Sketch in which to enable clipping.
	 *  @param x x coordinate of the top-left of the clipping rectangle.
	 *  @param y y coordinate of the top-left of the clipping rectangle.
	 *  @param width Width of the clipping rectangle.
	 *  @param height Height of the clipping rectangle.
	 *  @see #startClipping()
	 *  @see #stopClipping()
	 *  @see #setEnabled(boolean)
	 */
	public Clipper(PApplet applet, float x, float y, float width, float height) 
	{
		this.isEnabled = true;
		this.isClipping = false;
		this.applet = applet;
		setClippingRect(x, y, width, height);
	}

	/** Creates a new Clipper instance capable of limiting all drawing to within the given rectangular bounds.
	 *  Clipping is enabled by default, but not active.
	 *  @param applet Sketch in which to enable clipping.
	 *  @param clippingRect Bounds of the rectangle within which clipping is to be applied.
	 *  @see #startClipping()
	 *  @see #stopClipping()
	 *  @see #setEnabled(boolean)
	 */
	public Clipper(PApplet applet, Rectangle2D clippingRect) 
	{
		this(applet, (float)clippingRect.getX(), (float)clippingRect.getY(), (float)clippingRect.getWidth(), (float)clippingRect.getHeight());
	}
	
	// ----------------------------------- Methods ------------------------------------

	/** Starts clipping all drawn content to the screen bounds of the current clip area.
	 *  If startClipping for the current clipper is called before stopClipping somewhere else,
	 *  an exception is thrown.
	 */
	public void startClipping() 
	{
		if (isEnabled)
		{
			applet.pushStyle();
			applet.imageMode(PConstants.CORNER);
			applet.clip(x, y, width, height);
			applet.popStyle();
			isClipping = true;
		}
	}

	/** Stops any active clipping.
	 */
	public void stopClipping() 
	{
		applet.noClip();
		isClipping = false;
	}

	/** Sets the clipping rectangle and applies it if currently clipping
	 *  @param x x coordinate of the top-left of the clipping rectangle.
	 *  @param y y coordinate of the top-left of the clipping rectangle.
	 *  @param width Width of the clipping rectangle.
	 *  @param height Height of the clipping rectangle.
	 */
	public void setClippingRect(float x, float y, float width, float height) 
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		if (isClipping())
		{
			startClipping();
		}
	}

	/** Sets the clipping rectangle and applies it if currently clipping
	 *  @param clippingRect The clipping rectangle to apply.
	 */
	public void setClippingRect(Rectangle2D clippingRect) 
	{
		if (clippingRect == null)
		{
			throw new IllegalArgumentException("ClippingRect should not be null.");
		}
		setClippingRect((float)clippingRect.getX(), (float)clippingRect.getY(), (float)clippingRect.getWidth(), (float)clippingRect.getHeight());
	}
	
	/** Reports the clipping rectangle, whether or not is is currently active.
	 *  @return Bounds of the current clipping rectangle.
	 */
	public Rectangle2D getClippingRect() 
	{
		return new Rectangle2D.Float(x, y, width, height);
	}

	/** Reports whether or not the given point lies within the clipping rectangle. The result is independent of
	 *  whether or not clipping is currently active.
	 *  @param px x coordinate of the point to test.
	 *  @param py y coordinate of the point to test.
	 *  @return True if the given point is within the clipping rectangle, false otherwise.
	 */
	public boolean contains(float px, float py) 
	{
		if ((px < this.x) || (px > this.x+this.width) || (py<this.y) || (py > this.y+this.height))
		{
			return false;
		}
		return true;
	}

	/** Reports whether or not the given point lies within the clipping rectangle. The result is independent of
	 *  whether or not clipping is currently active.
	 *  @param p Point to test.
	 *  @return True if the given point is within the clipping rectangle, false otherwise.
	 */
	public boolean contains(PVector p) 
	{
		return contains(p.x, p.y);
	}

	/** Reports whether or not clipping mode is currently active. Note that the result is only true between 
	 *  <code>startClipping()</code> and <code>stopClipping()</code> method calls.
	 *  @return True if called while clipping is currently active, otherwise false.
	 *  @see #isEnabled()
	 */
	public boolean isClipping() 
	{
		return isClipping;
	}

	/** Reports whether or not clipping is enabled. Unlike <code>isClipping()</code>, this returns true even before 
	 *  <code>startClipping()</code> and after <code>stopClipping()</code>.
	 *  @return True if clipping is enabled.
	 *  @see #isClipping()
	 *  @see #setEnabled(boolean)
	 */
	public boolean isEnabled() 
	{
		return this.isEnabled;
	}

	/** Determines whether or not clipping is enabled.
	 *  If enabled is false, no clipping happens between startClipping() and stopClipping() (startClipping is ignored).
	 *  If called with enabled=false during clipping, clipping is stopped.
	 *  @param isEnabled Clipping will be enabled if true.
	 */
	public void setEnabled(boolean isEnabled) 
	{
		this.isEnabled = isEnabled;
		if (!isEnabled)
		{
			stopClipping();
		}
	}
}
