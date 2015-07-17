package varviewer.shared.varFilters;

import java.io.Serializable;

import varviewer.shared.variant.AnnotationIndex;
import varviewer.shared.variant.Variant;
import varviewer.shared.variant.VariantFilter;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A variant filter that passes variants with a numeric value associated with the given
 * key that is LESS than the given value. For instance, if the key is "pop.freq", and 
 * the max value is 0.01, this will pass all variants with pop.freq less than 0.01
 * @author brendan
 *
 */
public class MaxFreqFilter implements VariantFilter, Serializable, IsSerializable {

	//Maximum allowable pop frequency
	double maxFreq = Double.MAX_VALUE;
	
	//Max arup value
	double arupMax = 1.0;
	
	double exomesMax = 1.0;
	
	double exomesHomMax = 1.0;
	double exacHomCountMax = 100.0; 
	
	//VarBin min
	//int varBinMin = 3;


	private AnnotationIndex annoIndex = null;
	private int popFreqIndex = -1;
	private int exomesIndex = -1;
	private int exomesHomIndex = -1;
	private int arupOverallFreqIndex = -1;
	private int exacHomCountIndex = -1;
	
	public MaxFreqFilter() {
		//Required no-arg constructor
	}
	
	public void setAnnotationIndex(AnnotationIndex index) {
		this.annoIndex = index;
		this.popFreqIndex = index.getIndexForKey("pop.freq");
		this.exomesIndex = index.getIndexForKey("exomes6500.frequency");
		this.exomesHomIndex = index.getIndexForKey("exomes6500.homalt.frequency");
		
//		this.arupIndex = index.getIndexForKey("ARUP.freq");
		this.arupOverallFreqIndex = index.getIndexForKey("ARUP.overall.freq");
		this.exacHomCountIndex = index.getIndexForKey("exomes63K.al.count.hom");
//		this.varbinIndex = index.getIndexForKey("varbin.bin");
	}
	
//	public int getVarBinMin() {
//		return varBinMin;
//	}
//
//
//
//	public void setVarBinMin(int varBinMin) {
//		this.varBinMin = varBinMin;
//	}



	/**
	 * Set the maximum population (1000 Genomes) frequency allowed by this filter
	 * @param freq
	 */
	public void setMaxFreq(double freq) {
		this.maxFreq = freq;
	}
	
	public double getMaxFreq() {
		return maxFreq;
	}
	
	
	
	public double getArupMax() {
		return arupMax;
	}

	
	
	public double getExacHomCountMax() {
		return exacHomCountMax;
	}

	public int getExacHomCountIndex() {
		return exacHomCountIndex;
	}
	
	/**
	 * Set the maximum ARUP frequency allowed by this filter
	 * @param freq
	 */
	public void setArupMax(double arupMax) {
		this.arupMax = arupMax;
	}

	public double getExomesMax() {
		return exomesMax;
	}

	public void setExomesMax(double exomesMax) {
		this.exomesMax = exomesMax;
	}

	public double getExomesHomMax() {
		return exomesHomMax;
	}

	public void setExomesHomMax(double exomesHomMax) {
		this.exomesHomMax = exomesHomMax;
	}
	
	public void setExACHomCount(Double exacHomCount) {
		this.exacHomCountMax = exacHomCount;
	}
	
	@Override
	public boolean variantPasses(Variant var) {
		Double freq = var.getAnnotationDouble(popFreqIndex);
		Double exomesFreq = var.getAnnotationDouble(exomesIndex);
		Double exomesHomFreq = var.getAnnotationDouble(exomesHomIndex);
		//String arupStr = var.getAnnotationStr(arupIndex);
		Double arupFreq = var.getAnnotationDouble(arupOverallFreqIndex);
		//Double varbin = var.getAnnotationDouble(varbinIndex);
		Double exacHomCount = var.getAnnotationDouble(exacHomCountIndex);
		
		if (freq == null)
			freq = 0.0;
		if (arupFreq == null)
			arupFreq = 0.0;
		if (exomesFreq == null) {
			exomesFreq = 0.0;
		}
		if (exomesHomFreq == null) {
			exomesHomFreq = 0.0;
		}
		if (exacHomCount == null) {
			exacHomCount = 0.0;
		}
		
		if (freq > maxFreq) {
			return false;
		}
		
		if (exomesFreq > exomesMax) {
			return false;
		}
		
		if (exomesHomFreq > exomesHomMax) {
			return false;
		}

		if (arupFreq > arupMax) {
			return false;
		}

		if (exacHomCount > exacHomCountMax) {
			return false;
		}
		
//		if (varbin > varBinMin) {
//			return false;
//		}
		
		return true;
	}

	@Override
	public String getUserDescription() {
		return "Excluding variants with population frequency (from 1000 Genomes) > " + maxFreq + ", ESP Freq > " + exomesMax + " ESP Homs > " + exomesHomMax + ", and ARUP frequency greater than " + arupMax; 
	}

	

}
