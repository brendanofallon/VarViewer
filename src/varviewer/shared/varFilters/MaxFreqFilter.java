package varviewer.shared.varFilters;

import java.io.Serializable;

import varviewer.shared.variant.AnnotationIndex;
import varviewer.shared.variant.Variant;
import varviewer.shared.variant.VariantFilter;

/**
 * A variant filter that passes variants with a numeric value associated with the given
 * key that is LESS than the given value. For instance, if the key is "pop.freq", and 
 * the max value is 0.01, this will pass all variants with pop.freq less than 0.01
 * @author brendan
 *
 */
public class MaxFreqFilter implements VariantFilter, Serializable {

	//Maximum allowable pop frequency
	double maxFreq = Double.MAX_VALUE;
	
	//Max arup value
	int arupMax = 100;
	
	//VarBin min
	int varBinMin = 3;
	
	private AnnotationIndex annoIndex = null;
	private int popFreqIndex = -1;
	private int arupIndex = -1;
	private int varbinIndex = -1;
	
	public MaxFreqFilter() {
		//Required no-arg constructor
	}
	
	public void setAnnotationIndex(AnnotationIndex index) {
		this.annoIndex = index;
		this.popFreqIndex = index.getIndexForKey("pop.freq");
		this.arupIndex = index.getIndexForKey("ARUP.freq");
		this.varbinIndex = index.getIndexForKey("varbin.bin");
	}
	
	public int getVarBinMin() {
		return varBinMin;
	}



	public void setVarBinMin(int varBinMin) {
		this.varBinMin = varBinMin;
	}



	/**
	 * Set the maximum frequency allowed by this filter
	 * @param freq
	 */
	public void setMaxFreq(double freq) {
		this.maxFreq = freq;
	}
	
	public double getMaxFreq() {
		return maxFreq;
	}
	
	
	
	public int getArupMax() {
		return arupMax;
	}

	public void setArupMax(int arupMax) {
		this.arupMax = arupMax;
	}

	@Override
	public boolean variantPasses(Variant var) {
		Double freq = var.getAnnotationDouble(popFreqIndex);
		String arupStr = var.getAnnotationStr(arupIndex);
		Double varbin = var.getAnnotationDouble(varbinIndex);
		
		
		if (freq == null)
			freq = 0.0;
		if (varbin == null) {
			varbin = 0.0;
		}
		if (arupStr == null) {
			arupStr = "0 total";
		}
		
		
		if (freq > maxFreq) {
			return false;
		}

		int arupTot = 0;
		try {

			int idx = arupStr.indexOf(" tot");
			if (idx > 0) {
				arupTot = Integer.parseInt( arupStr.substring(0, idx));
				if (arupTot > arupMax) {
					return false;
				}
			}
		}
		catch(NumberFormatException nfe) {

		}

		if (varbin > varBinMin) {
			return false;
		}
		
		return true;
	}

	@Override
	public String getUserDescription() {
		return "Excluding variants with population frequency (from 1000 Genomes) greater than " + maxFreq + ", ARUP count greater than " + arupMax + ", and varbin bin# greater than " + varBinMin; 
	}

}
