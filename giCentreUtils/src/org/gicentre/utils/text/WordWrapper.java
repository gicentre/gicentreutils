package org.gicentre.utils.text;
import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

//****************************************************************************************
/** This class provides a some static methods for wrapping text giving more control to
 * how it is displayed that Processing's built-in functions
*  
* @author Aidan Slingsby, giCentre, City University London.
* @version 1.0, August 2011 
*/ 
//*****************************************************************************************

/* This file is part of giCentre utilities library. gicentre.utils is free software: you can 
* redistribute it and/or modify it under the terms of the GNU Lesser General Public License
* as published by the Free Software Foundation, either version 3 of the License, or (at your
* option) any later version.
* 
* gicentre.utils is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
* See the GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License along with this
* source code (see COPYING.LESSER included with this source code). If not, see 
* http://www.gnu.org/licenses/.
*/

public class WordWrapper {

	
	/** Works out where a string of text needs to wrap to fit into a given width
	 *  in pixels and returns as a list of string, each of which will not exceed
	 *  the given width. The advantage over using Processing's draw(text,x,y,w,h)
	 *  is that the number of lines that result is known.
	 * <br />
	 * \t characters are converted to single spaces
	 * \n are honoured
	 * <br />
	 * Uses the sketch's current textFont and textSize. Wraps on ' ' and '-'.
	 * @param text Text to wrap
	 * @param width Width to wrap text to
	 * @param sketch The sketch (uses current font)
	 * @return List of lines that will not exceed width
	 */
	public static List<String> wordWrap(String text, int width, PApplet sketch) {
		return wordWrap(text, width, sketch.g);
	}

	
	/** Works out where a string of text needs to wrap to fit into a given width
	 * in pixels and returns as a list of string, each of which will not exceed
	 * the given width. The advantage over using Processing's draw(text,x,y,w,h) is that 
	 * the number of lines that result is known.
	 * <br />
	 * \t characters are converted to single spaces
	 * \n are honoured
	 * <br />
	 * Uses pGraphics' current textFont and textSize. Wraps on ' ' and '-'.
	 * @param textToWrap Text to wrap.
	 * @param width Width to wrap text to.
	 * @param pGraphics The graphic context doing the text drawing.
	 * @return List of lines that will not exceed width.
	 */
	public static List<String> wordWrap(String textToWrap, int width, PGraphics pGraphics) {

		//Add a new line char to the end
		String text = textToWrap +'\n';
		
		ArrayList<String> wrappedLines=new ArrayList<String>();
		int idxInOriginalString=0;
		int lengthOriginalString=text.length();
		StringBuffer currentLine=new StringBuffer(); //to hold the current line
		StringBuffer currentWord=new StringBuffer(); //to hold the current word
		float cumWCurLine=0; //cumulative width of this line so far
		
		//keep going, char by char, until whole string done
		while(idxInOriginalString<lengthOriginalString){
			char ch=text.charAt(idxInOriginalString);
			boolean wrapIfNeeded=false;
			boolean forceNewLine=false;
			
			//convert tab to space
			if (ch=='\t'){
				ch=' ';
			}
			
			if (ch=='\n'){
				wrapIfNeeded=true;
				forceNewLine=true;
			}
			else if (ch!=' '){
				//append the char, but not if its a space (yet)
				currentWord.append(ch);
			}

			//flag to check whether this word must be wrapped
			if (ch==' '|| ch=='-'){
				wrapIfNeeded=true;
			}

			if (wrapIfNeeded){
				float w=pGraphics.textWidth(currentWord.toString());
				//check if this word would make the line too long
				if (currentLine.length()>0 && cumWCurLine+w>width){
					//if so, add what we have already to our completed lines and reset current line
					wrappedLines.add(currentLine.toString().trim());
					currentLine=new StringBuffer();
					cumWCurLine=0;
				}
				//add word to current line
				currentLine.append(currentWord);
				cumWCurLine+=w;
				currentWord=new StringBuffer();
			}
			//add the space now
			if (ch==' '){ //add this now we've check the length
				currentLine.append(ch);
				cumWCurLine+=pGraphics.textWidth(' ');
			}
			
			//wrap now if we flagged to
			if (forceNewLine){
				currentLine.append(currentWord);
				currentWord=new StringBuffer();
				wrappedLines.add(currentLine.toString());
				currentLine=new StringBuffer();
				cumWCurLine=0;
			}

			idxInOriginalString++;
		}
		return wrappedLines;
	}
	
	/** Tokenises the input string and return a list of these tokens and where they should be
	 * drawn. Wraps text to the given width and tokenises the string according to the token
	 * information embedded and so that tokens do not run over multiple lines. 
	 * <br />
	 * Designed for:
	 *   - colouring different words/phrases in a paragraph in different colours
	 *   - facilitating mouse interaction with words/phrase inline in a paragraph
	 *  <br /> 
	 * Embedded token information takes the form of a string in curly brackets inline in the
	 * supplied text. The string identifies the token in the return WrappedTokens.
	 * <br />
	 * For example:
	 * <br />
	 * <code>       "{other}The {adjective}quick{other} {adjective}brown{other} fox jumped over the
	 *         {adjective}lazy{other} dog";</code>
	 * <br /> 
     * results in a series of tokens that do not straddle lines and are identified as "other"
     * or "adjective". Any strings can be used.
     * <br />
     * The WrappedTokens returned contain the required information to draw them in the
     * correct place on the screen.
     * <br />
	 * Wraps on ' ' and '-'.
     * Uses the sketch's current textFont, textSize, lextLeading (line spacing) and textAlign
     * The token positions returned can be used directly in the PApplet's text(text,x,y) method.
	 * @param text The text to tokenise.
	 * @param x The x position of the text block placement.
	 * @param y The y position of the text block placement.
	 * @param width The width of the area within which to wrap text.
	 * @param sketch The sketch doing the text drawing.
	 * @return List of WrappedTokens that contain the information required to display these on screen.
	 */
	public static List<WrappedToken> wordWrapAndTokenise(String text, float x, float y, float width, PApplet sketch) {
		return wordWrapAndTokenise(text, x, y, width, sketch.g);
	}
	
	/** Tokenises the input string and return a list of these tokens and where they should be
	 * drawn. Wraps text to the given width and tokenises the string according to the token
	 * information embedded and so that tokens do not run over multiple lines. 
	 * <br />
	 * Designed for:
	 *   - colouring different words/phrases in a paragraph in different colours
	 *   - facilitating mouse interaction with words/phrase inline in a paragraph
	 * <br />
	 * Embedded token information takes the form of a string in curly brackets inline in the
	 * supplied text. The string identifies the token in the return WrappedTokens.
	 * <br />
	 * For example:
	 * <br />
	 * <code>       "{other}The {adjective}quick{other} {adjective}brown{other} fox jumped over the
	 *         {adjective}lazy{other} dog";</code>
	 * <br />
     * results in a series of tokens that do not straddle lines and are identified as "other"
     * or "adjective". Any strings can be used.
     * <br />
     * The WrappedTokens returned contain the required information to draw them in the
     * correct place on the screen.
     * <br />
  	 * Wraps on ' ' and '-'.
     * Uses the pGraphics's current textFont, textSize, textLeading (line spacing) and textAlign
     * The token positions returned can be used directly in the PApplet's text(text,x,y) method.
	 * @param textToTokenize The text to tokenise.
	 * @param x x position of the text placement.
	 * @param y y position of the text placement.
	 * @param width Width within to wrap text.
	 * @param pGraphics Graphics context doing the text drawing.
	 * @return List of WrappedTokens that contain the information required to display these on screen
	 */
	public static List<WrappedToken> wordWrapAndTokenise(String textToTokenize, float x, float y, float width, PGraphics pGraphics) {
		float maxX=x+width;
		
		String text = textToTokenize+'\n';		//add new line char to end
		ArrayList<WrappedToken> wrappedTokens=new ArrayList<WrappedToken>();
		int idxInOriginalString=0;
		int lengthOriginalString=text.length();
		StringBuffer currentLine=new StringBuffer();
		StringBuffer currentWord=new StringBuffer();
		float curX=x;
		float curY=y;
		float cumWCurToken=0;
		
		//ASSUMES textAign(LEFT) at first. Adjusts at the end
		
		String curStringID="";
		String nextStringID="";
		
		//keep going, char by char, until whole string done
		while(idxInOriginalString<lengthOriginalString){
			char ch=text.charAt(idxInOriginalString);
			boolean wrapIfNeeded=false;
			boolean forceNewLine=false;

			//get string id if here
			if (ch=='{'){
				nextStringID="";
				idxInOriginalString++;
				ch=text.charAt(idxInOriginalString);
				while (ch!='}' && idxInOriginalString<lengthOriginalString){
					nextStringID+=ch;
					idxInOriginalString++;
					if (idxInOriginalString<lengthOriginalString)
						ch=text.charAt(idxInOriginalString);
				}
				idxInOriginalString++;
				if (idxInOriginalString<lengthOriginalString)
					ch=text.charAt(idxInOriginalString);

				//Create new token, using previous id
				//But first check whether the most recent word needs to wrap
				float w=pGraphics.textWidth(currentWord.toString());
				//check if this word would make the line too long
				if (curX+cumWCurToken+w>maxX){
					//if so, make new token (not including current word)
					WrappedToken wrappedToken=new WrappedToken();
					wrappedToken.id=curStringID;
					wrappedToken.text=currentLine.toString();
					wrappedToken.x=curX;
					wrappedToken.y=curY;
					wrappedToken.bounds.x=(int)curX;
					wrappedToken.bounds.y=(int)curY;
					wrappedToken.bounds.width=(int)pGraphics.textWidth(currentLine.toString());
					wrappedToken.bounds.height=(int)pGraphics.textLeading;
					wrappedTokens.add(wrappedToken);
					//start new line
					curX=x;
					curY+=pGraphics.textLeading;
					cumWCurToken=0;
					currentLine=new StringBuffer();
				}
				//Do current word (and rest of line if it didn't wrap (above))
				cumWCurToken+=pGraphics.textWidth(currentWord.toString());
				currentLine.append(currentWord);
				if (currentLine.length()>0){
					WrappedToken wrappedToken=new WrappedToken();
					wrappedToken.id=curStringID;
					wrappedToken.text=currentLine.toString();
					wrappedToken.x=curX;
					wrappedToken.y=curY;
					wrappedToken.bounds.x=(int)curX;
					wrappedToken.bounds.y=(int)curY;
					wrappedToken.bounds.width=(int)pGraphics.textWidth(currentLine.toString());
					wrappedToken.bounds.height=(int)pGraphics.textLeading;
					wrappedTokens.add(wrappedToken);
				}
				curStringID=nextStringID;
				nextStringID="";
				curX+=cumWCurToken;
				cumWCurToken=0;
				currentLine=new StringBuffer();
				currentWord=new StringBuffer();									
			}
			
			//convert tab to space
			if (ch=='\t'){
				ch=' ';
			}
			
			if (ch=='\n'){
				wrapIfNeeded=true;
				forceNewLine=true;
			}
			else if (ch!=' '){
				//append the char, but not if its a space (yet)
				currentWord.append(ch);
			}

			//flag to check whether this word must be wrapped
			if (ch==' '|| ch=='-'){
				wrapIfNeeded=true;
			}

			if (wrapIfNeeded){
//				System.out.println("wrap if needed: "+currentWord+"; ch="+ch);
				float w=pGraphics.textWidth(currentWord.toString());
				//check if this word would make the line too long, but not if the current line has no chars in it
				if (curX+cumWCurToken+w>maxX){
					//if so, add what we have already to our completed lines and reset current line
					WrappedToken wrappedToken=new WrappedToken();
					wrappedToken.id=curStringID;
					//trim space at end
					if (currentLine.length()>0 && currentLine.charAt(currentLine.length()-1)==' '){
						currentLine.deleteCharAt(currentLine.length()-1);
					}
					wrappedToken.text=currentLine.toString();
					wrappedToken.x=curX;
					wrappedToken.y=curY;
					wrappedToken.bounds.x=(int)curX;
					wrappedToken.bounds.y=(int)curY;
					wrappedToken.bounds.width=(int)pGraphics.textWidth(currentLine.toString());
					wrappedToken.bounds.height=(int)pGraphics.textLeading;
					wrappedTokens.add(wrappedToken);
					curX=x;
					curY+=pGraphics.textLeading;
					cumWCurToken=0;
					currentLine=new StringBuffer();
				}
				//add word to current line
				currentLine.append(currentWord);
				cumWCurToken+=w;
				currentWord=new StringBuffer();
			}
			//add the space now
			if (ch==' '){ //add this now we've check the length
				currentLine.append(ch);
				cumWCurToken+=pGraphics.textWidth(' ');
			}
			
			//wrap now if we flagged to
			if (forceNewLine){
				currentLine.append(currentWord);
				currentWord=new StringBuffer();
				WrappedToken wrappedToken=new WrappedToken();
				wrappedToken.id=curStringID;
				wrappedToken.text=currentLine.toString();
				wrappedToken.x=curX;
				wrappedToken.y=curY;
				wrappedToken.bounds.x=(int)curX;
				wrappedToken.bounds.y=(int)curY;
				wrappedToken.bounds.width=(int)pGraphics.textWidth(currentLine.toString());
				wrappedToken.bounds.height=(int)pGraphics.textLeading;
				wrappedTokens.add(wrappedToken);
				curX=x;
				curY+=pGraphics.textLeading;
				cumWCurToken=0;
				currentLine=new StringBuffer();
			}

			idxInOriginalString++;
		}
		pGraphics.textFont.getSize();

		if (!wrappedTokens.isEmpty() 
				&& (pGraphics.textAlign==PConstants.CENTER || pGraphics.textAlign==PConstants.RIGHT)){
			float lineY=wrappedTokens.get(0).y;
			List<WrappedToken> wrappedTokensOnSameLine=new ArrayList<WrappedToken>();
			for (WrappedToken wrappedToken:wrappedTokens){
				if (lineY!=wrappedToken.y){
					//correct alignment for last tokens on same line
					correctForAlignment(wrappedTokensOnSameLine,maxX,pGraphics);
					wrappedTokensOnSameLine.clear();
				}
				wrappedTokensOnSameLine.add(wrappedToken);
				lineY=wrappedToken.y;
			}
			//correct alignment for last line
			correctForAlignment(wrappedTokensOnSameLine,maxX,pGraphics);
		}		
		return wrappedTokens;
	}
	
	private static void correctForAlignment(List<WrappedToken> wrappedTokensOnSameLine,float maxX, PGraphics pGraphics){
		float lineW=0;
		for (WrappedToken wrappedToken2:wrappedTokensOnSameLine){
			lineW+=wrappedToken2.bounds.width;
		}

		if (pGraphics.textAlign==PConstants.CENTER){
			float lineOffset=(maxX-lineW)/2f;
			for (WrappedToken wrappedToken2:wrappedTokensOnSameLine){
				float wordOffset=wrappedToken2.bounds.width/2f;
				wrappedToken2.x+=lineOffset+wordOffset;
				wrappedToken2.bounds.x=(int)(wrappedToken2.x-wordOffset);
			}
		}
		else if (pGraphics.textAlign==PConstants.RIGHT){
			float lineOffset=(maxX-lineW);
			for (WrappedToken wrappedToken2:wrappedTokensOnSameLine){
				float wordOffset=pGraphics.textWidth(wrappedToken2.text);
				wrappedToken2.x+=lineOffset+wordOffset;
				wrappedToken2.bounds.x=(int)(wrappedToken2.x-wordOffset);
			}
		}

		if (pGraphics.textAlignY==PConstants.CENTER){
			for (WrappedToken wrappedToken2:wrappedTokensOnSameLine){
				wrappedToken2.bounds.y-=pGraphics.textLeading/2-pGraphics.textDescent();
			}
		}

	}
}
