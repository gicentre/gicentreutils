package org.gicentre.utils.geom;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import processing.core.PVector;

// *****************************************************************************************
/** Class to allow the division of a set of 2d spatial objects into a coarser grid for 
 *  efficient spatial indexing. The collection is a set, so will contain a  unique set of 
 *  objects. Efficient retrieval is achieved by using the <code>get()</code> method that 
 *  will return only those items that are within a fixed distance of a given location. That
 *  fixed distance is determined by the <i>radius</i> of the hash grid. The radius should be
 *  set to the maximum search distance that an application is likely to need when performing
 *  a spatial query. Generally, the smaller the radius the quicker the retrieval.
 *  @param <E> Type of locatable objects stored in the hash grid.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.0, 10th August, 2010. 
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

public class HashGrid<E extends Locatable> implements Set<E>
{
    // ---------------------------- Object variables -----------------------------

    private int numRows,numCols;
    private float minX,minY,maxX,maxY;
    private HashMap<Integer, Collection<E>> hashMap;
    private Set<E> set;
    private float radius;
    
    // ------------------------------- Constructors ------------------------------- 
    
    /** Creates a hash grid capable of storing items located  within a rectangle between (0,0) and
     *  (<code>maxX</code>,<code>maxY</code>) with a grid resolution determined by the <code>radius</code>.
     *  @param maxX Largest x coordinate of locatable objects.
     *  @param maxY Largest y coordinate of locatable objects.
     *  @param radius Size of 1 grid cell in the same units as the preceding min and max parameters. If 0 or negative
     *                the HashGrid defaults to a radius of one tenth of the width of the grid. For optimum performance, 
     *                the radius should be set to the maximum search distance of any spatial object detection. If it 
     *                is set to be less than this, some adjacent neighbours may be missed when searching. If greater
     *                than this, searching will return more neighbours than necessary and impede performance for 
     *                dense collections of locatable objects.
     */
    public HashGrid(float maxX, float maxY, float radius)
    {
        this(0,0,maxX,maxY,radius);
    }
    
    /** Creates a hash grid capable of storing items located within a rectangle between (<code>minX</code>,<code>minY</code>)
     *  and (<code>maxX</code>,<code>maxY</code>) with a grid resolution determined by the <code>radius</code>.
     *  @param minX Smallest x coordinate of locatable objects.
     *  @param minY Smallest y coordinate of locatable objects.
     *  @param maxX Largest x coordinate of locatable objects.
     *  @param maxY Largest y coordinate of locatable objects.
     *  @param radius Size of 1 grid cell in the same units as the preceding min and max parameters. If 0 or negative
     *                the HashGrid defaults to a radius of one tenth of the width of the grid. For optimum performance, 
     *                the radius should be set to the maximum search distance of any spatial object detection. If it 
     *                is set to be less than this, some adjacent neighbours may be missed when searching. If greater
     *                than this, searching will return more neighbours than necessary and impede performance for 
     *                dense collections of locatable objects.
     */
    public HashGrid(float minX, float minY, float maxX, float maxY, float radius)
    {
        if (maxX-minX <= 0)
        {
            throw new IllegalArgumentException("Minimum x value must be smaller than maximum x value when creating a HashGrid.");
        }
        
        if (maxY-minY <= 0)
        {
            throw new IllegalArgumentException("Minimum y value must be smaller than maximum x value when creating a HashGrid.");
        }
        
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;  
        
        if (radius > 0)
        {
            this.radius = radius;
        }
        else
        {
            this.radius = (maxX-minX)/10f;
        }

        this.numCols = (int)((maxX-minX)/(radius*2));
        this.numRows = (int)((maxY-minY)/(radius*2));
        hashMap = new HashMap<Integer,Collection<E>>();
        set = new HashSet<E>();
    }

    // --------------------------------- Methods ---------------------------------
    
    /** Returns a collection of the locatable objects that are within the <code>radius</code> of
     *  the given location. <code>radius</code> is that defined in the constructor or by the most
     *  recent call to <code>updateAll(radius)</code>.
     *  @param location Location to query.
     *  @return Collection of the locatable objects that are within the radius of the given coordinates.
     *          If no objects found, an empty collection is returned.
     */
    @SuppressWarnings("boxing")
    public Set<E> get(PVector location)
    {
        int coordHash = getCoordHash(location);
        Collection<E> origCollection = hashMap.get(coordHash);
        Set<E> newCollection = new HashSet<E>();
    
        if (origCollection == null)
        {
            // No objects found in the given hash grid.
            return newCollection;
        }
        
        double maxDistSq = radius*radius;
        
        for (E obj : origCollection)
        {
            double distSq = (location.x-obj.getLocation().x)*(location.x-obj.getLocation().x) +
                            (location.y-obj.getLocation().y)*(location.y-obj.getLocation().y);
            if (distSq <= maxDistSq)
            {
                newCollection.add(obj);
            }
        }
        return newCollection;
    }
        
    /** Reports whether the hash grid contains the given locatable object within the <i>radius</i> of the
     *  given location.
     *  @param obj Object to search for.
     *  @param location Location to query.
     *  @return True if the hash grid contains the given object within the radius of the given location. 
     */
    public boolean contains(E obj, PVector location) 
    {
        return get(location).contains(obj);
    }
        
    /** Returns a set of all objects stored in the hash grid. There is no guarantee of  the order of 
     *  items returned within this set.
      * @return Set of unique objects stored by the hash grid.
      */
    public Set<E> getAll()
    {
        return set;
    }
    
    /** Updates the positions of all items in the hash grid so that their gridded location
     *  reflects the location stored inside the <code>Locatable</code> objects. This method
     *  is useful if all or many of the objects have changed location since being added to 
     *  the hash grid. If only one object has changed consider calling <code>update(E obj)</code>.
     */
    public void updateAll()
    {
        updateAll(0);
    }
    
    /** Updates the positions of all items in the hash grid so that their gridded location
     *  reflects the location stored inside the <code>Locatable</code> objects. This version
     *  also sets a new grid resolution implied by the <code>newRadius</code> value. This should
     *  represent the maximum search distance used when calling <code>get(PVector)</code>. 
     *  @param newRadius New grid cell radius corresponding to maximum search distance. No change
     *                   to the radius is made if the value is 0 or negative.
     */
    public void updateAll(float newRadius)
    {
        if (newRadius > 0)
        {
            this.radius = newRadius;
            this.numCols = (int)((maxX-minX)/(radius*2));
            this.numRows = (int)((maxY-minY)/(radius*2));
        }
    
        float halfGridX = (maxX-minX)/(numCols*2);
        float halfGridY = (maxY-minY)/(numRows*2);
        hashMap.clear();
        
        for (E obj : set)
        {
            addToGrid(obj,obj.getLocation());
            
            // Put the same object in the centre and up to 3 of the 8 adjacent grid cells to
            // avoid boundary problems.
            addToGrid(obj,new PVector(obj.getLocation().x-halfGridX,obj.getLocation().y-halfGridY));
            addToGrid(obj,new PVector(obj.getLocation().x,          obj.getLocation().y-halfGridY));
            addToGrid(obj,new PVector(obj.getLocation().x+halfGridX,obj.getLocation().y-halfGridY));
            
            addToGrid(obj,new PVector(obj.getLocation().x-halfGridX,obj.getLocation().y));
            addToGrid(obj,new PVector(obj.getLocation().x+halfGridX,obj.getLocation().y));
            
            addToGrid(obj,new PVector(obj.getLocation().x-halfGridX,obj.getLocation().y+halfGridY));
            addToGrid(obj,new PVector(obj.getLocation().x,          obj.getLocation().y+halfGridY));
            addToGrid(obj,new PVector(obj.getLocation().x+halfGridX,obj.getLocation().y+halfGridY));
        }
    }
    
    /** Updates the positions of the given item in the hash grid so that its gridded location
     *  reflects its new location. This method is useful if a <code>Locatable</code> object has
     *  changed its position, but most of the other objects in the hash grid have not.
     *  @param obj Locatable object to be updated.
     */
    public void update(E obj)
    {
        // TODO: Consider a more efficient update since remove() can be expensive.
        remove(obj);
        add(obj);
    }
    
    // ----------------------------- Implemented methods ------------------------------
    
    
    /** Adds a locatable object to the grid. Note that the object being added must
     *  implement the <code>Locatable</code> interface, otherwise a <code>ClassCastException</code>
     *  will be thrown.
     *  @param obj Locatable object to add to the hash grid.
     *  @return True if the collection has changed as a result of the object being added.
     */
    public boolean add(E obj)
    {
        return add(obj,obj);
    }
    
    
    /** Adds a locatable object to the grid. Unlike <code>add(E)</code> this allows a locatable
     *  object to be added at a location other than it's 'natural' one. Note that the object being 
     *  added must implement the <code>Locatable</code> interface, otherwise a <code>ClassCastException</code>
     *  will be thrown.
     *  @param obj Locatable object to add to the hash grid.
     *  @param loc Location of the object to add to the grid.
     *  @return True if the collection has changed as a result of the object being added.
     */
    public boolean add(E obj, Locatable loc)
    {
        // Put the object in the grid cell in which it lies (no duplications in neighbouring cells)
        boolean isItemAdded = addToGrid(obj,loc.getLocation());
   
        // Put the same object in the centre and up to 3 of the 8 adjacent grid cells to
        // avoid boundary problems.
        float halfGridX = (maxX-minX)/(numCols*2);
        float halfGridY = (maxY-minY)/(numRows*2);
 
        addToGrid(obj,new PVector(loc.getLocation().x-halfGridX,loc.getLocation().y-halfGridY));
        addToGrid(obj,new PVector(loc.getLocation().x,          loc.getLocation().y-halfGridY));
        addToGrid(obj,new PVector(loc.getLocation().x+halfGridX,loc.getLocation().y-halfGridY));
        
        addToGrid(obj,new PVector(loc.getLocation().x-halfGridX,loc.getLocation().y));
        addToGrid(obj,new PVector(loc.getLocation().x+halfGridX,loc.getLocation().y));
        
        addToGrid(obj,new PVector(loc.getLocation().x-halfGridX,loc.getLocation().y+halfGridY));
        addToGrid(obj,new PVector(loc.getLocation().x,          loc.getLocation().y+halfGridY));
        addToGrid(obj,new PVector(loc.getLocation().x+halfGridX,loc.getLocation().y+halfGridY));
        
        // Also add the same object to the set to allow 1-dimensional retrieval of all added objects.
        if (isItemAdded)
        {
            set.add(obj);
        }
        
        return isItemAdded;
    }
    
    /** Adds a collection of locatable objects to the hash grid.
      * @param collection Collection of objects to add to the collection.
      * @return Reports whether collection has been changed by the operation.
      */
    public boolean addAll(Collection<? extends E> collection) 
    {
        boolean hasChanged = false;
        
        for (E obj : collection)
        {
            if (add(obj) == true)
            {
                hasChanged = true;
            }
        }
        return hasChanged;
    }
    
    /** Clears out the contents of the grid.
      */
    public void clear()
    {
        hashMap.clear();
        set.clear();
    }

    /** Reports whether the hash grid contains the given object. While this will return results as expected,
      * for more efficient query consider using <code>contains(obj,location)</code> instead to limit search to
      * within the search radius. 
      * @param obj Object to search for.
      * @return True if collection contains the given object. 
     */
    public boolean contains(Object obj) 
    {
        return set.contains(obj);
    }

    /** Reports whether the hash grid contains all of the objects contained in the given collection. Note
      * that this operation ignores the grid cells of the objects. To limit a search to a particular spatial 
      * region call <code>get(location)</code> and search the returned collection.
      * @param collection Collection of objects to search for. 
      * @return True if the hash grid contains all of the items in the given collection. 
     */
    public boolean containsAll(Collection<?> collection) 
    {
        return set.containsAll(collection);
    }

    /** Reports whether this collection is equal to the given object. If the given object is another <code>HashGrid</code>
      * the comparison is performed on the <code>Set</code>s contained in the hash grids. In other words, it is independent
      * of the spatial locations of the objects stored in the hash grid. The only other type of object that could possibly
      * return <code>true</code> would be a <code>Set</code>.
      * @param obj Object to compare with this hash grid.
      * @return True if the given object contains a set that is equal to the set stored in this hash grid.
     */
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj)
    {       
        if (obj instanceof HashGrid)
        {
            return set.equals(((HashGrid)obj).getAll());
        }
        if (obj instanceof Set)
        {
            return set.equals(obj);
        }
        return false;
    }
    
    /** Reports the hash code of the entire collection.
      * @return Hash code of the set of objects stored in this hash grid. 
      */
    public int hashCode()
    {
        return set.hashCode();
    }
    
    /** Reports whether or not this hash grid contains any elements. 
      * @return True if this hash grid is empty. 
      */
    public boolean isEmpty() 
    {
        return set.isEmpty();
    }

    /** Provides an iterator to iterate through all the items stored in this hash grid. Note that this will not
      * guarantee iterating in any spatial order. To perform a spatial iteration, make calls to <code>get(location)</code>
      * in some spatial order and then iterate though the resulting collections.
      * @return Iterator for the objects stored in this hash grid.
      */
    public Iterator<E> iterator() 
    {
        return set.iterator();
    }

    /** Removes the given object from the hash grid.
      * @param obj Object to remove. 
      */
    public boolean remove(Object obj)
    {
        // Check to see if any instance of the object exists.
        boolean isChanged = set.remove(obj);
        
        if (isChanged == false)
        {
            return false;
        }
        
        // If object exists remove all instances of it from the hash grid lookup.
        Vector<Integer> hashesToRemove = new Vector<Integer>();
        for (Integer hashVal : hashMap.keySet())
        {
            Collection<E> collection = hashMap.get(hashVal); 
            if (collection.remove(obj))
            {
                // If we have removed the last object from a collection, remove the entire lookup.
                if (collection.size() == 0)
                {
                    hashesToRemove.add(hashVal);
                }
            }
        }
        
        for (Integer hashVal : hashesToRemove)
        {
            hashMap.remove(hashVal);
        }
        return true;
    }

    /** Removes the objects in the given collection from the hash grid.
      * @param collection Collection of objects to remove. 
      */
    public boolean removeAll(Collection<?> collection)
    {
        boolean isChanged = false;
        for (Object obj : collection)
        {
            if (remove(obj) == true)
            {
                isChanged = true;
            }
        }
        return isChanged;
    }

    /** Would strip all items from the hash grid apart from those contained in the given collection but
     *  is currently not supported by the hash grid. To limit the operation to certain grid
     *  cells, call <code>get(location)</code> and perform the <code>retainsAll()</code> on the returned collection.
     */
    public boolean retainAll(Collection<?> collection) 
    {
        throw (new UnsupportedOperationException("Cannot perform a retainsAll() operation on a hash grid."));
    }

    /** Reports the number of unique items stored in this hash grid.
     */
    public int size() 
    {
        return set.size();
    }

    /** Provides an array representation of the items in this hash grid. Note that this does not guarantee
     *  any form of spatial ordering of items.
     *  @return Array of objects.
     */
    public Object[] toArray() 
    {
        return set.toArray();
    }

    /** Provides an array representation of the items in this hash grid. Note that this does not double count
      * any items that may have been repeated in more than one grid cell and does not guarantee any form of
      * spatial ordering of items. The runtime type of the returned array is that of the given array. If the
      * collection fits in the given array, it is returned therein. Otherwise, a new array is allocated with
      * the runtime type of the given array and the size of this collection. If this collection fits in the 
      * given array with room to spare (i.e. the array has more elements than this collection), the element
      * in the array immediately following the end of the collection is set to null. (This is useful in 
      * determining the length of this collection only if the caller knows that this collection does not 
      * contain any null elements.)
      * @return Array of objects.
      */
    public <T> T[] toArray(T[] a) 
    {
        return set.toArray(a);
    }   
    
 
    // ----------------------------- Private methods ------------------------------
    
    /** Private version of the add() method that does not add object to neighbouring grid cells.
      * @param obj Object to add to the grid.
      * @param location Location to query.
      * @return True if object was successfully added to the hash grid. 
      */
    @SuppressWarnings("boxing")
    private boolean addToGrid(E obj, PVector location)
    {
        if ((location.x < 0) || (location.y < 0) || (location.x>maxX) || (location.y > maxY))
        {
            // Do nothing when out of bounds.
            return false;
        }

        int coordHash = getCoordHash(location);
        Collection<E> objects = hashMap.get(coordHash); 
        if (objects == null)
        {
            // Nothing currently stored in this grid cell.
            objects = new HashSet<E>();
        }
        // Update list of objects stored in this grid cell.
        objects.add(obj);
        hashMap.put(coordHash,objects); 
        return true;
    }

    /** Calculates the hash of the grid cell containing the given location
      * @param location Location to query.
      * @return Hash representing the given location. 
      */
    private int getCoordHash(PVector location)
    {
        // Bin coordinates into coarse (col,row) grid.
        int col = (int)(location.x*numCols/(maxX+1));
        int row = (int)(location.y*numRows/(maxY+1));

        // Convert (col,row) coordinate into single unique hash number.
        return row*numCols + col;
    }
}
