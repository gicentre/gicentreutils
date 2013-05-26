package org.gicentre.utils.io;

import javax.xml.parsers.*;     // For Document builder factory.
import org.xml.sax.*;           // For SAX Exception handling.
import org.w3c.dom.*;           // For document object model (DOM).
import java.io.*;               // For file handling.
import java.util.*;             // For vector structure.

//  ****************************************************************************************
/** Handles DOM processing allowing the reading and writing of hierarchical structures as
 *  XML files. Uses the Document Object Model (DOM) to store the tree of nodes, therefore
 *  not suitable for very large structures. For reading very large structures represented as
 *  XML, use SAX processing instead.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.3, 1st August, 2011.
 */ 
// *****************************************************************************************

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
  
public class DOMProcessor
{
    // ----------------------- Object Variables -----------------------

    private int indent;             // Indent level.  
    private Document dom;           // Document object model.
    private PrintWriter out;        // Output stream.
    private Vector<Node> matches;   // List of matching elements.
   
    // ------------------------- Constructors -------------------------
   
    /** Creates a new empty DOM ready for processing.
      */
    public DOMProcessor()
    {  
        // Do nothing.
    }
        
    /** Wraps the given DOM in this processor allowing it to be written
      * as an XML file, or appended with new nodes.
      * @param dom Document Object Model to use in processor.
      */
    public DOMProcessor(Document dom)
    {  
        this.dom = dom;
    }

    /** Reads and the given XML file and constructs a DOM from it.
      * @param fileName Name of XML file to read.
      */
    public DOMProcessor(String fileName)
    {   
        readXML(fileName);        
    }

    /** Reads XML from the given input stream and constructs a DOM from it.
      * @param inStream Stream from which to read XML.
      */
    public DOMProcessor(InputStream inStream)
    {   
        readXML(inStream);        
    }
    
    // ------------------------- Methods ---------------------------    
    
    /** Reports whether we have an empty DOM.
      * @return True if DOM is empty. 
      */
    public boolean isEmpty()
    {
        if (dom == null)
        {
            return true;
        }
        
        return false;
    }
    
    /** Adds a new element to the root of the DOM.
      * @param name Name of the new element
      * @return New element in the DOM.
      */
    public Node addElement(String name)
    {  
        if (dom == null)
        {
            // Create a DocumentBuilder using the DocumentBuilderFactory.
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            indent = 0;

            try 
            {
                db = dbf.newDocumentBuilder();
            }
            catch (ParserConfigurationException e) 
            {
                System.err.println("Problem finding an XML parser:\n"+e);
                return null;
            }
           
            dom = db.getDOMImplementation().createDocument(null,name,null);
            dom.setStrictErrorChecking(false); 
            return dom.getDocumentElement();
        }
        
        return addElement(name,null,dom.getDocumentElement());
    }
    
    /** Adds a new element to the given one within the DOM.
      * @param name Name of the new element
      * @param existingElement Element onto which the new element should be attached.
      * @return New element in the DOM.
      */
    public Node addElement(String name, Node existingElement)
    {  
        return addElement(name,null,existingElement);
    }
    
    /** Adds a new element to the given one within the DOM.
      * @param name Name of the new element
      * @param text Text to attach to element or null if none required.
      * @param existingElement Element onto which the new element should be attached.
      * @return New element in the DOM.
      */
    public Node addElement(String name, String text, Node existingElement)
    {  
        // Create the new element node and attach it to existing node.
        Node newNode = dom.createElement(name);
        existingElement.appendChild(newNode);
        
        // Add text if given.
        if (text != null)
        {
            Node textNode = dom.createTextNode(text);
            newNode.appendChild(textNode);
        }
        return newNode;
    }
    
    /** Renames the given element with the given new name.
      * @param existingElement Element to rename.
      * @param newName New name to give element.
      */
    public void renameElement(Node existingElement, String newName)
    {
        // Create an element with the new name
        Node newElement = dom.createElement(newName);
        
        // Copy the attributes to the new element
        NamedNodeMap attrs = existingElement.getAttributes();
        for (int i=0; i<attrs.getLength(); i++)
        {
            Attr attr2 = (Attr)dom.importNode(attrs.item(i), true);
            newElement.getAttributes().setNamedItem(attr2);
        }
        
        // Move all the children
        while (existingElement.hasChildNodes()) 
        {
            newElement.appendChild(existingElement.getFirstChild());
        }
        
        // Replace the old node with the new node
        existingElement.getParentNode().replaceChild(newElement, existingElement);
    }
    
    /** Adds the given attribute to the given node.
      * @param name Attribute name.
      * @param value Attribute value.
      * @param node Element to attach attribute.
      */
    public void addAttribute(String name, String value, Node node)
    {
        if (node.getNodeType() == Node.ELEMENT_NODE)
        {
            Element element = (Element)node;
            element.setAttribute(name,value);
        }
    }

    /** Adds the given comment to the root of the DOM. Note that this method should only 
      * be called once a root node has been created in the DOM. 
      * @param comment Comment text.
      */
    public void addComment(String comment)
    {
        addComment(comment,dom.getDocumentElement());
    }
    
    /** Adds the given comment to the given node.
      * @param comment Comment text.
      * @param node Element to attach comment.
      */
    public void addComment(String comment, Node node)
    {
        node.getParentNode().insertBefore(dom.createComment(comment),node);
    }
    
    /** Adds text as the child of the given node.
      * @param text Text to add to node.
      * @param node Element to attach text.
      */
    public void addText(String text, Node node)
    {  
        node.appendChild(dom.createTextNode(text));
    }

    /** Searches the entire DOM for a given element and returns text associated 
      * with it. If more than one element with the given name exists, multiple
      * text values are returned.
      * @param elementName Element to search for.
      * @return Array of strings associated with all occurrences of 
      * the given element. Array will be 0 length if none found.
      */
    public String[] getText(String elementName)
    {
        return getText(elementName,dom);
    }

    /** Returns any text associated found in the given node or its children.
      * This is equivalent to calling <code>getText(null,node)</code>. 
      * If more than one element containing text exists, multiple text values 
      * are returned.
      * @param node Node from which to start search.
      * @return Array of strings associated with all occurrences of 
      * text in the node or its children. Array will be 0 length if none found.
      */
    public String[] getText(Node node)
    {
        return getText(null,node);    
    }
        
    /** Searches for a given element and returns text associated with it. If more than one
      * element with the given name exists, multiple text values are returned.
      * @param elementName Element to search for. If elementName is null, search will be 
      *                    for all text contained within the given node.
      * @param node Node from which to start search.
      * @return Array of strings associated with all occurrences of the given element. 
      *         Array will be 0 length if none found.
      */
    public String[] getText(String elementName, Node node)
    {
        matches = new Vector<Node>();
        searchText(elementName,node);
        
        // Convert match vector into an array;
        String[] matchArray = new String[matches.size()];
        int i=0;
        for (Node matchedNode : matches)
        {
            matchArray[i++] = matchedNode.getNodeValue();
        }
        matches = null;
        return matchArray; 
    }
    
    /** Searches for a given node and returns text associated with
      * it. This version does not recurse to the node's children.
      * @param node Node to search.
      * @return Text associated with the node, or null if none found.
      */
    public String getNodeText(Node node)
    {
        // Look for text in child (text stored in its own node).
        NodeList children = node.getChildNodes();
        
        for (int i=0; i<children.getLength(); i++)
        {
            Node child = children.item(i);
            
            if ((child.getNodeType() == Node.CDATA_SECTION_NODE) ||
                (child.getNodeType() == Node.TEXT_NODE))
            {
                return(child.getNodeValue());
            }
        }
        
        // If we get this far, no text was found.
        return null;
    }

    /** Searches the entire DOM for a given attribute and returns the value associated with it.
      * If there is more than one occurrence of the attribute, multiple text values are returned.
      * @param attributeName Attribute to search for.
      * @return Array of strings associated with all occurrences of the given attribute.
      *         Array will be 0 length if none found.
      */
    public String[] getAttributes(String attributeName)
    {
        return getAttributes(attributeName,dom);
    }
    
    /** Searches the given node and its children for a given attribute and returns the value
      * associated with it. If there is more than one occurrence of the attribute, multiple
      * text values are returned.
      * @param attributeName Attribute to search for.
      * @param node Node from which to start search.
      * @return Array of strings associated with all occurrences of the given attribute.
      *         Array will be 0 length if none found.
      */
    public String[] getAttributes(String attributeName, Node node)
    {
        matches = new Vector<Node>();
        searchAttributes(attributeName,node);
                
        // Convert match vector into an array;
        String[] matchArray = new String[matches.size()];
        int i=0;
        for (Node matchedNode : matches)
        {
            matchArray[i++] = matchedNode.getNodeValue();
        }
        matches = null;
        return matchArray; 
    }
    
    /** Searches the given node for a given attribute and returns the value associated with it.
      * This version does not recurse to children of the given node.
      * @param attributeName Attribute to search for.
      * @param node Node from which to start search.
      * @return Value associated with the attribute, or null if not found.  
      */
    public String getNodeAttribute(String attributeName, Node node)
    {
        // Only consider document or element nodes.
        if ((node.getNodeType() != Node.DOCUMENT_NODE) &&
            (node.getNodeType() != Node.ELEMENT_NODE))
        {
            return null;
        }
            
        // Search attributes associated with the node.
        NamedNodeMap attributes = node.getAttributes();
        for (int i=0; i<attributes.getLength(); i++)
        {
            Node attribute = attributes.item(i);
            if (attribute.getNodeName().equalsIgnoreCase(attributeName))
            {
                return attribute.getNodeValue();
            }
        }
        
        // If we get this far, the attribute has not been found.
        return null;
    }
    
    /** Returns a list of the DOM elements with the given name. This can be
      * used to provide the base of sub-trees for searches within nested 
      * elements. 
      * @param name Element name to search for.
      * @return Array of elements with the given name. Array will be 0 length if none found.
      */
    public Node[] getElements(String name)
    {
        return getElements(name,dom);
    }
        
    /** Returns a list of the DOM elements with the given name that are 
      * nested within the given node. This can be used to provide the 
      * base of sub-trees for searches within nested elements. The order of matched elements
      * is depth-first. For breadth-first searches, use <code>getNodeElements</code> and
      * recursively search for children of returned nodes.
      * @param name Element name to search for.
      * @param node Node from which to start search.
      * @return Array of elements with the given name. Array will be 0 length if none found.
      */
    public Node[] getElements(String name, Node node)
    {
        matches = new Vector<Node>();
        searchNode(name,node);
        
        // Convert match vector into an array;
        Node[] matchArray = new Node[matches.size()];
        matches.toArray(matchArray);
        matches = null;
        
        return matchArray; 
    }
    
    /** Returns a DOM element with the given name that is the child of the 
      * given node. This is a non-recursive method that only looks for immediate
      * children. Note that unlike <code>getNodeElements()</code> this method only
      * returns the first matched child of the given node.
      * @param name Element name to search for.
      * @param node Node from which to examine children.
      * @return Child node or null if none found.
      */
    public Node getNodeElement(String name, Node node)
    {
       // Only consider document or element nodes.
        if ((node.getNodeType() != Node.DOCUMENT_NODE) &&
            (node.getNodeType() != Node.ELEMENT_NODE))
        {
            return null;
        }
            
        NodeList children = node.getChildNodes();
        
        for (int i=0; i<children.getLength(); i++)
        {
            Node child = children.item(i);
            
            // Only consider element child nodes.
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                if (child.getNodeName().equalsIgnoreCase(name))
                {
                    return child;
                }
            }
        }
       
        // If we get this far, no child node was found.
        return null;
    }
    
    /** Returns the DOM elements with the given name that are the children of the 
      * given node. This is a non-recursive method that only looks for immediate
      * children. Array will be 0 length if none found.
      * @param name Element name to search for.
      * @param node Node from which to examine children.
      * @return Child nodes or empty Node array if none found.
      */
    public Node[] getNodeElements(String name, Node node)
    {
        // Only consider document or element nodes.
        if ((node.getNodeType() != Node.DOCUMENT_NODE) &&
            (node.getNodeType() != Node.ELEMENT_NODE))
        {
            return new Node[0];
        }
        
        Vector<Node> matchedChildren = new Vector<Node>();
           
        NodeList children = node.getChildNodes();
       
        for (int i=0; i<children.getLength(); i++)
        {
            Node child = children.item(i);
           
            // Only consider element child nodes.
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                if (child.getNodeName().equalsIgnoreCase(name))
                {
                    matchedChildren.add(child);
                }
            }
        }
        
        Node[] nodes = new Node[matchedChildren.size()];
        matchedChildren.toArray(nodes);
        return nodes;
    }
     
    /** Reads the given XML file and converts it into a DOM.
      * @param fileName Name of XML file to convert.
      * @return True if converted successfully.
      */
    public boolean readXML(String fileName)
    {
        // Create a DocumentBuilder using the DocumentBuilderFactory.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        indent = 0;

        try 
        {
            db = dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) 
        {
            System.err.println("Problem finding an XML parser:\n"+e);
            return false;
        }
  
        // Try to parse the given file and store XML nodes in the DOM. 
        try 
        {
            dom = db.parse(new File(fileName));
        }
        catch (SAXException e) 
        {
            System.err.println("Problem parsing document: "+e.getMessage());
            dom = db.newDocument();
            return false;
        }
        catch (IOException e) 
        {
            System.err.println("Problem reading "+fileName);
            return false;
        }
        return true;
    }
    
    
    /** Reads the XML from the given input stream and converts it into a DOM. 
      * @param inStream Input stream containing XML to convert.
      * @return True if converted successfully.
      */
    public boolean readXML(InputStream inStream)
    {
        // Create a DocumentBuilder using the DocumentBuilderFactory.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        indent = 0;

        try 
        {
            db = dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) 
        {
            System.err.println("Problem finding an XML parser:\n"+e);
            return false;
        }
  
        // Try to parse the given file and store XML nodes in the DOM. 
        try 
        {
            dom = db.parse(inStream);
        }
        catch (SAXException e) 
        {
            System.err.println("Problem parsing document: "+e.getMessage());
            dom = db.newDocument();
            return false;
        }
        catch (IOException e) 
        {
            System.err.println("Problem reading from "+inStream);
            return false;
        }
        return true;
    }
    
    /** Displays the DOM stored within this class as an XML file with default 
      * document settings on standard output.
      * @return Always true.
      */
    public boolean writeXML()
    {
        return writeXML(null,null,null);
    }

    /** Displays the DOM stored within this class as an XML file with the given document
      * settings on standard output.
      * @param version XML version, or null if default ('1.0') is to be used.
      * @param encoding XML encoding, or null if encoding is not to be specified.
      * @param standalone XML stand-alone status of XML file or null if not to be specified.
      * @return Always true.
      */
    public boolean writeXML(String version, String encoding, Boolean standalone)
    {
        out = new PrintWriter(System.out);
        indent = 0;
        outputNodeAsXML(dom,version,encoding,standalone);

        // NOTE: Closing the writer to standard output closes stdout itself!
        //       So flush output rather than close it.
        out.flush();
       
        return true;
    }

    /** Converts the DOM stored within this class into an XML file with default document settings.
      * @param fileName Name of file to contain the XML.
      * @return true if successful XML generation.
      */
    public boolean writeXML(String fileName)
    {
        return writeXML(fileName,null,null,null);
    }
    
    /** Converts the DOM stored within this class into an XML file with default document settings.
      * @param outStream Output stream representing file to contain the XML.
      * @return true if successful XML generation.
      */
    public boolean writeXML(OutputStream outStream)
    {
        return writeXML(outStream,null,null,null);
    }
    
    /** Converts the DOM stored within this class into an XML file with the given document settings.
      * @param fileName Name of file to contain the XML.
      * @param version XML version, or null if default ('1.0') is to be used.
      * @param encoding XML encoding, or null if encoding is not to be specified.
      * @param standalone XML stand-alone status of XML file or null if not to be specified.
      * @return true if successful XML generation.
      */
    public boolean writeXML(String fileName, String version, String encoding, Boolean standalone)
    {
        if (dom == null)
        {
            System.err.println("Error: No document object model to process.");
            return false;
        }
        
        // Open file for output.
        try
        {           
            out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        }
        catch (IOException e)
        {
            System.err.println("Error: Problem creating XML file: "+fileName);
            return false;
        }
    
        // Start recursive output of the whole DOM.
        indent = 0;
        outputNodeAsXML(dom,version,encoding,standalone);
        
        // Close output and leave.
        out.close();
        return true;
    }

    /** Converts the DOM stored within this class into an XML file with the given document settings.
      * @param outStream Output stream representing file to contain the XML.
      * @param version XML version, or null if default ('1.0') is to be used.
      * @param encoding XML encoding, or null if encoding is not to be specified.
      * @param standalone XML stand-alone status of XML file or null if not to be specified.
      * @return true if successful XML generation.
      */
    public boolean writeXML(OutputStream outStream, String version, String encoding, Boolean standalone)
    {
        if (dom == null)
        {
            System.err.println("Error: No document object model to process.");
            return false;
        }
        
        // Open file for output.
        out = new PrintWriter(outStream);
           
        // Start recursive output of the whole DOM.
        indent = 0;
        outputNodeAsXML(dom,version,encoding,standalone);
        
        // Close output and leave.
        out.close();
        return true;
    }
    
    // ---------------------- Private Methods --------------------------
    
    /** Searches for a given element in the given node and updates list
      * of text within matched elements. Recursively searches for sub-nodes
      * of the given one.
      * @param element Element to search for. If null, all elements searched.
      * @param node Node to start search from.
      */
    private void searchText(String element, Node node)
    {
        // Only consider document or element nodes.
        if ((node.getNodeType() != Node.DOCUMENT_NODE) &&
            (node.getNodeType() != Node.ELEMENT_NODE))
        {
            return;
        }
            
        if ((element == null) || (node.getNodeName().equalsIgnoreCase(element)))
        {
            // Match found so look for text in children.
            NodeList children = node.getChildNodes();
            
            for (int i=0; i<children.getLength(); i++)
            {
                Node child = children.item(i);
                
                if ((child.getNodeType() == Node.CDATA_SECTION_NODE) ||
                    (child.getNodeType() == Node.TEXT_NODE))
                {
                    if (child.getNodeValue().trim().length() > 0)
                    {
                        //matches.add(child.getNodeValue());
                        matches.add(child);
                    }
                }
            }
        }
        
        if ((node.getNodeType() == Node.DOCUMENT_NODE) ||
            (node.getNodeType() == Node.ELEMENT_NODE))
        {
            // Search child nodes.
            NodeList children = node.getChildNodes();
            
            for (int i=0; i<children.getLength(); i++)
            {
                searchText(element,children.item(i));
            }
        }
    }
    
    /** Searches for a given attribute in the given node and updates list
      * of attribute values. Recursively searches for sub-nodes of the given one.
      * @param element Element to search for.
      * @param node Node to start search from.
      */
    private void searchAttributes(String element, Node node)
    {
        // Only consider document or element nodes.
        if ((node.getNodeType() != Node.DOCUMENT_NODE) &&
            (node.getNodeType() != Node.ELEMENT_NODE))
        {
            return;
        }
            
        // Search attributes associated with current node.
        NamedNodeMap attributes = node.getAttributes();
        for (int i=0; i<attributes.getLength(); i++)
        {
            Node attribute = attributes.item(i);
            if (attribute.getNodeName().equalsIgnoreCase(element))
            {
                //matches.add(attribute.getNodeValue());
                matches.add(attribute);
            }
        }

        // Search child nodes.
        NodeList children = node.getChildNodes();
            
        for (int i=0; i<children.getLength(); i++)
        {
            searchAttributes(element,children.item(i));
        }
    }

    /** Searches for a given element in the given node and updates list
      * of elements with that name. Recursively searches for sub-nodes of the given one.
      * @param element Element to search for.
      * @param node Node to start search from.
      */
    private void searchNode(String element, Node node)
    {
        // Only consider document or element nodes.
        if ((node.getNodeType() != Node.DOCUMENT_NODE) &&
            (node.getNodeType() != Node.ELEMENT_NODE))
        {
            return;
        }
          
        // Match found, so add node to list.  
        if (node.getNodeName().equalsIgnoreCase(element))
        {
            matches.add(node);
        }
            
        // Search children
        NodeList children = node.getChildNodes();
 
        for (int i=0; i<children.getLength(); i++)
        {
            searchNode(element,children.item(i));
        }
    }
    
    /** Converts the given DOM node into XML. Recursively converts
      * any child nodes.
      * @param node DOM Node to display.
      */
    private void outputNodeAsXML(Node node) 
    {
        outputNodeAsXML(node,null,null,null);   
    }
    
    /** Converts the given DOM node into XML. Recursively converts
      * any child nodes. This version allows the XML version, encoding and stand-alone
      * status to be set.
      * @param node DOM Node to display.
      * @param version XML version, or null if default ('1.0') is to be used.
      * @param encoding XML encoding, or null if encoding is not to be specified.
      * @param standalone XML stand-alone status or null if not to be specified.
      */
    private void outputNodeAsXML(Node node, String version, String encoding, Boolean standalone) 
    {
        // Store node name, type and value.
        String name  = node.getNodeName(),
               value = makeFriendly(node.getNodeValue());
        int    type  = node.getNodeType();

        // Ignore empty nodes (e.g. blank lines etc.)
        if ((value != null) && (value.trim().equals("")))
        {
            return;
        }
    
        switch (type) 
        {
            case Node.DOCUMENT_NODE:    // Start of document.
            {
                if (version == null)
                {
                	out.print("<?xml version=\"1.0\" ");
                }
                else
                {
                	out.print("<?xml version=\""+version+"\" ");
                }
                
                if (encoding != null)
                {
                    out.print("encoding=\""+encoding+"\" ");
                }
                
                if (standalone != null)
                {
                    if (standalone.booleanValue())
                    {
                        out.print("standalone=\"yes\" ");    
                    }
                    else
                    {
                        out.print("standalone=\"no\" ");       
                    }
                }
                
                out.println("?>");

                // Output the document's child nodes.
                NodeList children = node.getChildNodes();
                
                for (int i=0; i<children.getLength(); i++) 
                {
                    outputNodeAsXML(children.item(i));
                }
                break;
            }

            case Node.ELEMENT_NODE:     // Document element with attributes.
            {
                // Output opening element tag.
                indent++;
                indent();          
                out.print("<"+name);

                // Output any attributes the element might have.
                NamedNodeMap attributes = node.getAttributes();
                for (int i=0; i<attributes.getLength(); i++)
                {
                    Node attribute = attributes.item(i);
                    out.print(" "+attribute.getNodeName()+"=\""+attribute.getNodeValue()+"\"");
                }
                out.print(">");

                // Output any child nodes that exist.                    
                NodeList children = node.getChildNodes();

                for (int i=0; i<children.getLength(); i++)
                { 
                    outputNodeAsXML(children.item(i));
                }
              
                break;
            }

            case Node.CDATA_SECTION_NODE:          // Display text.
            case Node.TEXT_NODE: 
            {
                out.print(value);
                break;
            }
            
            case Node.COMMENT_NODE:                // Comment node.
            {
                indent++;
                indent(); 
                out.print("<!--"+value+"-->");
                indent--;
                break;
            }

            case Node.ENTITY_REFERENCE_NODE:       // Entity reference nodes.
            {
                indent++;
                indent(); 
                out.print("&"+name+";");
                indent--;
                break;
            }

            case Node.PROCESSING_INSTRUCTION_NODE: // Processing instruction.
            {
                indent++;
                indent();
                out.print("<?"+name); 
                if ((value != null) && (value.length() > 0)) 
                {
                    out.print(" "+value);
                }
                out.println("?>");
                indent--;
                break;
            }
        }
           
        // Finally output closing tags for each element.
        if (type == Node.ELEMENT_NODE)
        {
            out.print("</"+node.getNodeName()+">");
            indent--;
            if (node.getNextSibling() == null)
            {
                indent();  // Only throw new line if this is the last sibling.
            }
        }
    }

    /** Converts a given string into XML-friendly code by replacing 
      * quotes, triangular brackets etc. with their symbolic equivalent.
      * @param text Text to process.
      * @return Processed text with XML friendly symbols.
      */
    private static String makeFriendly(String text) 
    {
        StringBuffer newText = new StringBuffer();
        if (text == null)   
        {
            return null;
        }

        int numCharacters = text.length();
        for (int i=0; i<numCharacters; i++) 
        {
            char ch = text.charAt(i);
            switch (ch) 
            {
                case '<': 
                {
                    newText.append("&lt;");
                    break;
                }
                case '>': 
                {
                    newText.append("&gt;");
                    break;
                }
                case '&': 
                {
                    newText.append("&amp;");
                    break;
                }
                case '"': 
                {
                    newText.append("&quot;");
                    break;
                }
                default:
                {
                    newText.append(ch);
                }
            }
        }
        return newText.toString();
    } 

    /** Indents output to current tree depth.
      */
    private void indent()
    {
        out.println("");
        for (int i=1; i<indent; i++)
        {
            out.print(" ");
        }
    }
}