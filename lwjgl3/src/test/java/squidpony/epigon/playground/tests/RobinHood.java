//MIT License
//
//Copyright (c) 2019 Rahul Nalawade
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in all
//copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//SOFTWARE.

package squidpony.epigon.playground.tests;

/**
 * CS 5V81.001: Implementation of Data Structures and Algorithms
 * Short Project SP07: Robin Hood Hashing Implementation
 * @author Rahul Nalawade (rsn170330)
 * 
 * Date: January 04, 2018
 * Code is from <a href="https://github.com/rahul1947/SP07-Comparison-of-Hashing-Implementations">this GitHub repo</a>.
 */
public class RobinHood<T> {
	
	private Object[] table; // The hash table.
	private int maxDisp; // The largest displacement of any element.
	private int size; // The size of the table.
	private int mask;

	// Initializes new hash table using Robin Hood.
	public RobinHood() {
		table = new Object[1024];
		maxDisp = 0;
		size = 0;
		mask = 1023;
	}
	// Initializes new hash table using Robin Hood.
	public RobinHood(int initialSize) {
		int msb = Integer.highestOneBit(initialSize);
		if(initialSize != msb) msb <<= 1;
		table = new Object[msb];
		maxDisp = 0;
		size = 0;
		mask = msb - 1;
	}

	/**
	 * Adds the specified element to this set if it is not already present.
	 * 
	 * @param x the element to be added
	 * @return true if successful insertion, false otherwise
	 */
	public boolean add(T x) {
		if (size >= (table.length >> 1)) {
			resize();
		}
		if (contains(x)) {
			return false;
		}
		int loc = hash(x);
		int d = 0;
		
		while (true) {
			if (table[loc] == null) {
				table[loc] = x;
				size++;
				return true;
			} 
			else if (displacement((T) table[loc], loc) >= d) {
				d++;
				loc = (loc + 1) & mask;
				maxDisp = Math.max(d, maxDisp);
			} 
			else {
				T temp = x;
				x = (T) table[loc];
				table[loc] = temp;
				loc = (loc + 1) & mask;
				d = displacement(x, loc);
				maxDisp = Math.max(d, maxDisp);
			}
		}
	}

	/**
	 * If x is there is the Collection.
	 * @param x the input element
	 * @return true if present, false otherwise
	 */
	public boolean contains(T x) {
		int loc = hash(x);
		for (int d = 0; d <= maxDisp; d++) {
			int index = (loc + d) & mask;
			if (x.equals(table[index])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes the specified element from this set if it is present.
	 * @param x the element to be removed
	 * @return true, if successfully removed, false otherwise
	 */
	public boolean remove(T x) {
		int loc = hash(x);
		
		for (int d = 0; d <= maxDisp; d++) {
			
			int index = (loc + d) & mask;
			
			if (x.equals(table[index])) {
				table[index] = null;
				size--;
				return true;
			}
		}
		return false;
	}

	// Returns the number of elements in the table.
	public int size() {
		return size;
	}

	// Resizes the table to double the size.
	private void resize() {
		Object[] oldTable = table;
		table = new Object[table.length << 1];
		mask = table.length - 1;
		size = 0;
		maxDisp = 0;
		
		for (Object x : oldTable) {
			if (x != null) {
				add((T) x);
			}
		}
	}

	/**
	 * Returns the displacement of element x at location loc.
	 *
	 * @param x the element to calculate displacement of
	 * @param loc the location of x in the table
	 * @return the displacement of element x at location loc
	 */
	private int displacement(T x, int loc) {
		int h = hash(x);
		return (loc >= h) ? (loc - h) : (table.length + loc - h);
	}

	/**
	 * Returns the hash of x.
	 *
	 * @param x the element to hash
	 * @return the hash of x
	 */
	private int hash(T x) {
		int h = x.hashCode() * 0x9E375;
		return (h ^ h >>> 16) & mask;
//		if (h < 0) {
//			return (x.hashCode() + 1 & mask) + table.length - 1;
//		}

//		int h = x.hashCode();
//		h ^= (h >>> 20) ^ (h >>> 12);
//		return (h ^ (h >>> 7) ^ (h >>> 4)) & mask;
	}

	/**
	 * Calculate distinct elements in an array
	 * @param arr: Array of Integers which may or may not have duplicates.
	 * @return: returns the count of distinct elements in the provided array.
	 */
	public static<T> int distinctElements(T[] arr){
		RobinHood<T> dist = new RobinHood<>();
		
		for (T e : arr) { dist.add(e); }
		return dist.size();
	}
	
	public final int capacity()
	{
		return mask + 1;
	}
}
