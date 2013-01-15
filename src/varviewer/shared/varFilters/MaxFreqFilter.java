package varviewer.shared.varFilters;

import java.io.Serializable;

import varviewer.shared.Variant;
import varviewer.shared.VariantFilter;

/**
 * A variant filter that passes variants with a numeric value associated with the given
 * key that is LESS than the given value. For instance, if the key is "pop.freq", and 
 * the max value is 0.01, this will pass all variants with pop.freq less than 0.01
 * @author brendan
 *
 */
public class MaxFreqFilter implements VariantFilter, Serializable {

	//Maximum allowable value
	double maxVal = Double.MAX_VALUE;
	
	//Annotation key for value to filter on
	String annotation = null;
	
	private boolean missingDataPasses = true;
	
	public MaxFreqFilter() {
		//Required no-arg constructor
	}
	
	public MaxFreqFilter(String annotation, double maxVal) {
		this.annotation = annotation;
		this.maxVal = maxVal;
	}
	
	/**
	 * True if variant pass when there is no annotation value associated with the given key
	 * @param passes
	 */
	public void setMissingDataPasses(boolean passes) {
		this.missingDataPasses = passes;
	}
	
	/**
	 * Set the maximum frequency allowed by this filter
	 * @param freq
	 */
	public void setMaxValue(double freq) {
		this.maxVal = freq;
	}
	
	public double getMaxValue() {
		return maxVal;
	}
	
	@Override
	public boolean variantPasses(Variant var) {
		String freq = var.getAnnotation(annotation);
		if (freq == null)
			return missingDataPasses;
		try {
			Double val = Double.parseDouble(freq);
			if (val <= maxVal) {
				return true;
			}
			else {
				return false;
			}
		}
		catch(NumberFormatException nfe) {
			
		}
		
		return missingDataPasses;
	}

}
