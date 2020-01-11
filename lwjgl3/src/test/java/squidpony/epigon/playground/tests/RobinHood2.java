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
public class RobinHood2<T> {
	
	private Object[] table; // The hash table.
	private int maxDisp; // The largest displacement of any element.
	private int size; // The size of the table.
	private int mask, shift;
	private int threshold;
	private final float f;

	// Initializes new hash table using Robin Hood.
	public RobinHood2() {
		table = new Object[1024];
		maxDisp = 0;
		size = 0;
		mask = 1023;
		shift = 54;
		f = 0.5f;
		threshold = 512;
	}
	// Initializes new hash table using Robin Hood.
	public RobinHood2(int initialSize) {
		this(initialSize, 0.5f);
	}
	public RobinHood2(int initialSize, float loadFactor) {
		int msb = Integer.highestOneBit(initialSize);
		if(initialSize != msb) msb <<= 1;
		table = new Object[msb];
		maxDisp = 0;
		size = 0;
		mask = msb - 1;
		shift = Long.numberOfLeadingZeros(mask);
		f = loadFactor;
		threshold = (int) (msb * loadFactor);
	}

	/**
	 * Adds the specified element to this set if it is not already present.
	 * 
	 * @param x the element to be added
	 * @return true if successful insertion, false otherwise
	 */
	public boolean add(T x) {
		if (contains(x)) {
			return false;
		}
		if (size >= threshold) {
			resize();
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
		final int loc = hash(x);
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
		threshold = (int)(table.length * f);
		shift = Long.numberOfLeadingZeros(mask);
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
	private int displacement(final T x, final int loc) {
		return loc - hash(x) & mask;
	}
	
	/**
	 * Returns the hash of x.
	 *
	 * @param x the element to hash
	 * @return the hash of x
	 */
	private int hash(final T x) {
		return (int) (x.hashCode() * 0x9E3779B97F4A7C15L >>> shift);
	}
//	private int hash(T x) {
//		int h = x.hashCode() * 0x9E375;
//		return (h ^ h >>> 16) & mask;
//	}
	
//	private int hash(T x) {
//		int h = x.hashCode();
//		h ^= (h >>> 20) ^ (h >>> 12);
//		return (h ^ (h >>> 7) ^ (h >>> 4)) & mask;
//	}

	/**
	 * Calculate distinct elements in an array
	 * @param arr: Array of Integers which may or may not have duplicates.
	 * @return: returns the count of distinct elements in the provided array.
	 */
	public static<T> RobinHood2<T> distinct(T[] arr){
		final RobinHood2<T> dist = new RobinHood2<>(arr.length);
		
		for (T e : arr) { dist.add(e); }
		return dist;
	}
	
	public final int capacity()
	{
		return mask + 1;
	}
}
