package varviewer.shared.varFilters;

import java.io.Serializable;

import varviewer.shared.variant.AnnotationIndex;
import varviewer.shared.variant.Variant;
import varviewer.shared.variant.VariantFilter;

/**
 * A filter that filters by quality, depth, and variant frequency
 * @author brendan
 *
 */
public class QualityDepthFilter implements VariantFilter, Serializable {

	private double minQuality = 20.0;
	private int minDepth = 4;
	private double minVarFreq = 0.10;
	
	private boolean missingDataPasses = true;
	private AnnotationIndex annoIndex = null;

	private int qualityIndex = -1;
	private int depthIndex = -1;
	private int variantDepthIndex = -1;
	
	public void setAnnotationIndex(AnnotationIndex index) {
		this.annoIndex = index;
		qualityIndex = annoIndex.getIndexForKey("quality");
		depthIndex = annoIndex.getIndexForKey("depth");
		variantDepthIndex = annoIndex.getIndexForKey("var.depth");
	}
	
	@Override
	public boolean variantPasses(Variant var) {
		Double qual = var.getAnnotationDouble(qualityIndex);
		Double depth = var.getAnnotationDouble(depthIndex);
		Double varDepth = var.getAnnotationDouble(variantDepthIndex);
		
		if (qual != null && qual < minQuality) {
			return false;
		}
		
		if (depth != null && depth < minDepth) {
			return false;
		}
		
		if (depth != null && varDepth != null) {
			double varFreq =  varDepth / depth;
			if (varFreq < minVarFreq) {
				return false;
			}
		}
		return true;
	}

	public double getMinQuality() {
		return minQuality;
	}

	public void setMinQuality(double minQuality) {
		this.minQuality = minQuality;
	}

	public int getMinDepth() {
		return minDepth;
	}

	public void setMinDepth(int minDepth) {
		this.minDepth = minDepth;
	}

	public double getMinVarFreq() {
		return minVarFreq;
	}

	public void setMinVarFreq(double minVarFreq) {
		this.minVarFreq = minVarFreq;
	}

	public boolean isMissingDataPasses() {
		return missingDataPasses;
	}

	public void setMissingDataPasses(boolean missingDataPasses) {
		this.missingDataPasses = missingDataPasses;
	}

	@Override
	public String getUserDescription() {
		return "Variants with quality less than " + minQuality + ", read depth (coverage) less than " + minDepth + ", or allele balance less than " + minVarFreq + " were excluded.";
	}

	
	
}
