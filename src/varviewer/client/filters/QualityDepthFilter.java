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
		String qual = var.getAnnotation("quality");
		String depth = var.getAnnotation("depth");
		String varDepth = var.getAnnotation("var.depth");
		if (qual == null || depth == null || varDepth == null) {
			return missingDataPasses;
		}
		
		Double dQual = Double.parseDouble(qual);
		
		if (dQual < minQuality) {
			return false;
		}
		
		Double dDepth = Double.parseDouble(depth);
		if (dDepth < minDepth) {
			return false;
		}
		
		Double dvd = Double.parseDouble(varDepth);
		double varFreq = dvd / dDepth;
		if (varFreq < minVarFreq) {
			return false;
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

	
	
}
