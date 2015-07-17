package varviewer.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * A range of sites, not bound to a particular chromosome.
 * These are meant to be HALF-OPEN, they include the firstPos but not the lastPos
 * @author brendan
 *
 */
public class Interval implements Comparable<Interval>, Serializable, IsSerializable {
		
	private int firstPos;
	private int lastPos;
	
	public Interval() {
		//Required no-arg constructor for serialization
	}
	
		public Interval(int first, int last) {
			this.firstPos = first;
			this.lastPos = last;
		}
		
		public int getFirstPos() {
			return firstPos;
		}
		
		public int getLastPos() {
			return lastPos;
		}
		
		public int getSize() {
			return lastPos - firstPos + 1;
		}
		
		public String toString() {
			return firstPos + "-" + lastPos;
		}
		
		public int compareTo(Interval inter) {
			return this.firstPos - inter.firstPos;
		}
		
		/**
		 * Returns true if any site falls into both this and the other interval
		 * @param other
		 * @return
		 */
		public boolean overlaps(Interval other) {
			if (other.lastPos <= firstPos ||
					other.firstPos >= lastPos)
				return false;
			else
				return true;
		}
		
		/**
		 * Merge two overlapping intervals into a single interval that includes all sites in both
		 * @param other
		 * @return
		 */
		public Interval merge(Interval other) {
			if (! this.overlaps(other)) {
				throw new IllegalArgumentException("Intervals must overlap to merge");
			}
			
			return new Interval(Math.min(firstPos, other.firstPos), Math.max(lastPos, other.lastPos));
		}
}

