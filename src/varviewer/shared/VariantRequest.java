package varviewer.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * These objects are passed to a VariantServer to retrieve a list of Variants for display.
 * @author brendan
 *
 */
public class VariantRequest implements Serializable {
	
	private List<String> sampleIDs = new ArrayList<String>();
	private IntervalList intervals = new AllIntervals();
	private List<VariantFilter> filters = new ArrayList<VariantFilter>();
	private List<String> annotations = new ArrayList<String>();
	
	public VariantRequest() {
		//blank on purpose, must have a no-arg constructor
	}
	
	/**
	 * Perform semi-deep copy of this request, all lists are new but the objects in them are 
	 * the same (by reference) as that in this object. 
	 */
	public VariantRequest clone() {
		VariantRequest newReq = new VariantRequest();
		for(String id : sampleIDs) {
			newReq.addSample(id);
		}
		newReq.setIntervals(intervals);
		for(VariantFilter filter : filters) {
			newReq.addFilter(filter);
		}
		newReq.setAnnotations(annotations);
		
		return newReq;
	}
	
	/**
	 * Empties the list of sampleIDs. Until new sampleIDs are added no variants
	 * will be returned by this request
	 */
	public void clearSamples() {
		sampleIDs.clear();
	}
	/**
	 * Append an additional sample for which to obtain variants
	 * @param sampleID
	 */
	public void addSample(String sampleID) {
		this.sampleIDs.add(sampleID);
	}
	
	/**
	 * Add a new interval to the list of intervals over which to request variants
	 * @param contig
	 * @param interval
	 */
	public void addInterval(String contig, Interval interval) {
		intervals.addInterval(contig, interval);
	}
	
	/**
	 * Clear the current intervals list and set (by reference) the intervals to the given 
	 * object 
	 * @param intervals
	 */
	public void setIntervals(IntervalList intervals) {
		this.intervals = intervals;
	}
	
	
	
	public List<String> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<String> annotations) {
		this.annotations = annotations;
	}

	public void addFilter(VariantFilter filter) {
		if (filters == null)
			filters = new ArrayList<VariantFilter>();
		filters.add(filter);
	}
	
	/**
	 * Remove all variant filters. 
	 */
	public void clearFilters() {
		filters.clear();
	}
	
	public List<VariantFilter> getFilters() {
		return filters;
	}
	
	/**
	 * Obtain the series of intervals for request
	 * @return
	 */
	public IntervalList getIntervals() {
		return intervals;
	}

	/**
	 * Obtain the list of sampleIDs to fetch variants for
	 * @return
	 */
	public List<String> getSampleIDs() {
		return sampleIDs;
	}
	
}
