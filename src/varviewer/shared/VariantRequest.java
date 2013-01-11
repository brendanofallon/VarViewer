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
	private IntervalList intervals = new IntervalList();
	private List<String> annotationKeys = new ArrayList<String>();
	
	
	public VariantRequest() {
		//blank on purpose, must have a no-arg constructor
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
	 * Add a new item to the list of annotations requested for the variants. 
	 * @param key
	 */
	public void addAnnotationKey(String key) {
		annotationKeys.add(key);
	}
	
	/**
	 * Return a list of all annotation keys in this request
	 * @return
	 */
	public List<String> getAnnotationKeys() {
		return annotationKeys;
	}
	
	/**
	 * Obtain the series of intervals for request
	 * @return
	 */
	public IntervalList getIntervals() {
		return intervals;
	}

	
}
