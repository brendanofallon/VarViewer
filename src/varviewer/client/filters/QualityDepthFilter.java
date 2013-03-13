package varviewer.client.filters;

import java.io.Serializable;

import varviewer.shared.Variant;
import varviewer.shared.VariantFilter;

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
	
	@Override
	public boolean variantPasses(Variant var) {
		Double qual = var.getAnnotationDouble("quality");
		Double depth = var.getAnnotationDouble("depth");
		Double varDepth = var.getAnnotationDouble("var.depth");
		
		
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
