package varviewer.shared.varFilters;

import java.io.Serializable;

import varviewer.shared.variant.AnnotationIndex;
import varviewer.shared.variant.Variant;
import varviewer.shared.variant.VariantFilter;

public class MinFreqFilter implements VariantFilter, Serializable {

	//Maximum allowable value
	private double maxVal = Double.MAX_VALUE;
	
	//Annotation key for value to filter on
	private int annotationIndex = -1;
	
	private boolean missingDataPasses = true;
	
	public MinFreqFilter() {
		//Required no-arg constructor
	}
	
	public MinFreqFilter(int index, double maxVal) {
		this.annotationIndex = index;
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
		Double freq = var.getAnnotationDouble(annotationIndex);
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

	@Override
	public void setAnnotationIndex(AnnotationIndex index) {
		//don't believe this is actually used
	}


}
