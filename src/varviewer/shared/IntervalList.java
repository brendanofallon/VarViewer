package varviewer.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A list of genomic intervals with a few utilities for merging, comparing etc. 
 * @author brendan
 *
 */
public class IntervalList implements Serializable {

	protected Map<String, List<Interval>> intervals = new HashMap<String, List<Interval>>();
	
	/**
	 * Create a single new list that contains all intervals
	 * @return
	 */
	public List<Interval> asList() {
		List<Interval> list = new ArrayList<Interval>();
		for(String contig : getContigs() ) {
			for(Interval interval : getIntervalsInContig(contig)) {
				list.add(interval);
			}
		}
		return list;
	}
	
	/**
	 * Returns biggest interval in list
	 * @return
	 */
	public Interval biggestInterval() {
		Interval biggest = null;
		for(String contig : getContigs()) {
			for(Interval inter : getIntervalsInContig(contig)) {
				if (biggest == null || inter.getSize() > biggest.getSize()) {
					biggest = inter;
				}
			}
		}
		return biggest;
	}
	
	/**
	 * Remove the given interval from this list
	 * @param intToRemove
	 * @return
	 */
	public boolean removeInterval(Interval intToRemove) {
		for(String contig : getContigs()) {
			List<Interval> list = getIntervalsInContig(contig);
			if (list.contains(intToRemove)) {
				list.remove(intToRemove);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the contig that contains the given interval, or null
	 * if the given interval is not in the contig
	 * @param inter
	 * @return
	 */
	public String contigOfInterval(Interval inter) {
		for(String contig : getContigs()) {
			List<Interval> list = getIntervalsInContig(contig);
			if (list.contains(inter)) {
				return contig;
			}
		}
		return null;
	}
	
	/**
	 * Sort all intervals and merge all mergeable intervals in all contigs
	 */
	public void sortAllIntervals() {
		if (intervals != null) {
			for(String contig : intervals.keySet()) {
				Collections.sort( intervals.get(contig) );
				mergeIntervals(intervals.get(contig) );
			}
		}
	}
	
	/**
	 * Merges all mergeable intervals in the given list
	 * @param inters
	 */
	private void mergeIntervals(List<Interval> inters) {
		List<Interval> merged = new ArrayList<Interval>();
		if (inters.size() == 0)
			return;
		
		merged.add( inters.get(0));
		inters.remove(0);
		
		for(Interval inter : inters) {
			Interval last = merged.get( merged.size()-1);
			if (inter.overlaps(last)) {
				Interval newLast = inter.merge(last);
				merged.remove(last);
				merged.add(newLast);
			}
			else {
				merged.add(inter);
			}
			
		}
		
		inters.clear();
		inters.addAll(merged);
	}
	
	
	public String toString() {
		if (this.getIntervalCount()==1) {
			return this.asList().get(0).toString();
		}
		
		StringBuilder strB = new StringBuilder();
		strB.append("extent: " + this.getExtent() + " count: " + this.getIntervalCount() + "  " );
		int count = 0;
		boolean ellipses = false;
		for(String contig : getContigs()) {
			for(Interval interval : getIntervalsInContig(contig)) {
				if (count < 3 || count>( this.getIntervalCount()-2))
					strB.append( interval.toString() +", ");
				else if (!ellipses) {
					strB.append("....");
					ellipses = true;
				}
				
				count++;
			}
		}
		return strB.toString();
	}
	
	/**
	 * Add the given interval to the list 
	 * @param contig
	 * @param interval
	 */
	public void addInterval(String contig, Interval interval) {
		List<Interval> cInts = intervals.get(contig);
		if (cInts == null) {
			cInts = new ArrayList<Interval>(256);
			intervals.put(contig, cInts);
		}
		cInts.add(interval);
	}
	
	/**
	 * Add a new interval spanning the given region to the list
	 * @param contig
	 * @param start
	 * @param end
	 */
	public void addInterval(String contig, int start, int end) {
		List<Interval> cInts = intervals.get(contig);
		if (cInts == null) {
			cInts = new ArrayList<Interval>(256);
			intervals.put(contig, cInts);
		}
		cInts.add(new Interval(start, end));
	}
	
	/**
	 * Returns the number of bases covered by all of the intervals
	 * @return
	 */
	public long getExtent() {
		long size = 0;
		if (intervals == null) {
			return 0;
		}
		
		for(String contig : getContigs()) {
			List<Interval> intList = getIntervalsInContig(contig);
			for(Interval interval : intList) {
				size += interval.getSize();
			}
		}
		return size;
	}
	
	/**
	 * Obtain a collection containing the names of all contigs (aka chromosomes, aka sequences)
	 * in this set of intervals
	 * @return
	 */
	public Collection<String> getContigs() {
		return intervals.keySet();
	}
	
	/**
	 * Obtain a list of all intervals in the given contig
	 * @param contig
	 * @return
	 */
	public List<Interval> getIntervalsInContig(String contig) {
		return intervals.get(contig);
	}
	
	/**
	 * Returns the number of intervals in this interval collections
	 * @return
	 */
	public int getIntervalCount() {
		
		if (intervals == null) {
			return 0;
		}
		
		int size = 0;
		for(String contig : intervals.keySet()) {
			List<Interval> intList = intervals.get(contig);
			size += intList.size();
		}
		return size;
	}
	
	public boolean contains(String contig, int pos) {
		List<Interval> cInts = intervals.get(contig);
		Interval qInterval = new Interval(pos, pos);
		if (cInts == null) {
			return false;
		}
		else {
			int index = Collections.binarySearch(cInts, qInterval);
			if (index >= 0) {
				//System.out.println("Interval " + cInts.get(index) + " contains the position " + pos);
				//An interval starts with the query position so we do contain the given pos
				return true;
			}
			else {
				//No interval starts with the query pos, but we 
				int keyIndex = -index-1 -1;
				if (keyIndex < 0) {
					return false;
				}
				Interval cInterval = cInts.get(keyIndex);
				if (pos >= cInterval.getFirstPos() && pos < cInterval.getLastPos()) {
					return true;
				}
				else {
					return false;
				}
			}
		}
	}
	
}
