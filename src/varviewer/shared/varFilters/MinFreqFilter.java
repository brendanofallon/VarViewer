package varviewer.shared.varFilters;

import java.io.Serializable;

import varviewer.shared.Variant;
import varviewer.shared.VariantFilter;

public class MinFreqFilter implements VariantFilter, Serializable {

	//Maximum allowable value
	private double maxVal = Double.MAX_VALUE;
	
	//Annotation key for value to filter on
	private String annotation = null;
	
	private boolean missingDataPasses = true;
	
	public MinFreqFilter() {
		//Required no-arg constructor
	}
	
	public MinFreqFilter(String annotation, double maxVal) {
		this.annotation = annotation;
		this.maxVal = maxVal;
	}
	
	/**
	 * Set the maximum frequency allowed by this filter
	 * @param freq
	 */
	public void setMaxValue(double freq) {
		this.maxVal = freq;
	}
	
	@Override
	public boolean variantPasses(Variant var) {
		Double freq = var.getAnnotationDouble(annotation);
		if (freq == null)
			return missingDataPasses;

		if (freq >= maxVal) {
			return true;
		}
		else {
			return false;
		}

	}

	@Override
	public String getUserDescription() {
		// TODO Auto-generated method stub
		return null;
	}


}
