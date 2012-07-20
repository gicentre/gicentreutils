package org.gicentre.tests;

import java.awt.Rectangle;
import java.util.List;

import org.gicentre.utils.text.WordWrapper;
import org.gicentre.utils.text.WrappedToken;

import processing.core.PApplet;


public class WrapWrapTest extends PApplet{
	
	public void setup(){
	}

	public void draw(){
		background(255);

		//Set font - these font measurement are used 
		textSize(20);
		textAlign(LEFT,TOP);
		textLeading(22);

		noStroke();

		int wrapWidth=width;

		float x=0;
		float y=0;
		fill(150);
		String s ="{other}The {adjective}quick{other} {adjective}brown{other} fox jumped over the {adjective}lazy{other} dog";
		List<WrappedToken> wrappedTokens = WordWrapper.wordWrapAndTokenise(s, x,y,wrapWidth, this.g);
		boolean cursorOverRedText=false;
		for (WrappedToken wrappedToken:wrappedTokens){
			if (wrappedToken.id.equals("adjective")){
				if (wrappedToken.getBounds(this).contains(mouseX,mouseY)){
					cursorOverRedText=true;
					Rectangle r=wrappedToken.getBounds(this);
					pushStyle();
					noFill();
					stroke(100);
					rect(r.x,r.y,r.width,r.height);
					popStyle();
				}
				fill(255,0,0);
			}
			else
				fill(150);
			text(wrappedToken.text,wrappedToken.x,wrappedToken.y);
		}

		if (cursorOverRedText)
			cursor(HAND);
		else
			cursor(ARROW);
	}
}
