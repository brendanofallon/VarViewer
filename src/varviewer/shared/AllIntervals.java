package varviewer.shared;

import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Dummy class to signify all possible variants in all intervals
 * @author brendan
 *
 */
public class AllIntervals extends IntervalList implements IsSerializable {
	
	/**
	 * Create a single new list that contains all intervals
	 * @return
	 */
	public List<Interval> asList() {
		return null;
	}
	
	/**
	 * Returns biggest interval in list
	 * @return
	 */
	public Interval biggestInterval() {
		return null;
	}
	
	/**
	 * Remove the given interval from this list
	 * @param intToRemove
	 * @return
	 */
	public boolean removeInterval(Interval intToRemove) {
		throw new IllegalArgumentException("Cannot remove intervals from All Intervals object");
	}
	
	/**
	 * Returns the contig that contains the given interval, or null
	 * if the given interval is not in the contig
	 * @param inter
	 * @return
	 */
	public String contigOfInterval(Interval inter) {
		return null;
	}
	
	/**
	 * Sort all intervals and merge all mergeable intervals in all contigs
	 */
	public void sortAllIntervals() {
		//nothing to sort
	}
	

	public String toString() {
		return "All intervals";
	}
	
	/**
	 * Add the given interval to the list 
	 * @param contig
	 * @param interval
	 */
	public void addInterval(String contig, Interval interval) {
		//do nothing, all intervals already included
	}
	
	/**
	 * Add a new interval spanning the given region to the list
	 * @param contig
	 * @param start
	 * @param end
	 */
	public void addInterval(String contig, int start, int end) {
		//do nothing, all intervals already included
	}
	
	/**
	 * Returns the number of bases covered by all of the intervals
	 * @return
	 */
	public long getExtent() {
		return -1l;
	}
	
	/**
	 * Obtain a collection containing the names of all contigs (aka chromosomes, aka sequences)
	 * in this set of intervals
	 * @return
	 */
	public Collection<String> getContigs() {
		return null;
	}
	
	/**
	 * Obtain a list of all intervals in the given contig
	 * @param contig
	 * @return
	 */
	public List<Interval> getIntervalsInContig(String contig) {
		return null;
	}
	
	/**
	 * Returns the number of intervals in this interval collections
	 * @return
	 */
	public int getIntervalCount() {
		return -1;
	}
	
	public boolean contains(String contig, int pos) {
		return true;
	}
}
