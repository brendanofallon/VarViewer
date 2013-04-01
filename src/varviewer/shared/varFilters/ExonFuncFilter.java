package varviewer.shared.varFilters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import varviewer.shared.variant.AnnotationIndex;
import varviewer.shared.variant.Variant;
import varviewer.shared.variant.VariantFilter;

/**
 * A special-case filter used to filter by exon-function type
 * @author brendan
 *
 */
public class ExonFuncFilter implements VariantFilter, Serializable {

	private boolean excludeIntergenic = true;
	private boolean excludeIntronic = true;
	private boolean excludeSynonymous = true;
	private boolean excludeNonsynonymous = false;
	private boolean excludeNonFrameshift = false;
	private boolean excludeStopGainsLosses = false;
	private boolean excludeFrameshift = false;
	private boolean excludeSplicing = false;
	private boolean excludeUTR = true;
	private boolean excludeNCRNA = true;
	private AnnotationIndex index = null;
	//Pre-computed indices for the annotations we use. These get set when the annotationIndex is set. 
	private int varTypeIndex = -1;
	private int exonFuncIndex = -1;
	private int geneIndex = -1;
	
	private boolean missingDataPasses = true;
	
	public ExonFuncFilter() {
		//Required no-arg constructor
	}
	
	public void setAnnotationIndex(AnnotationIndex index) {
		this.index = index;
		if (index != null) {
			varTypeIndex = index.getIndexForKey("variant.type");
			exonFuncIndex = index.getIndexForKey("exon.function");
			geneIndex = index.getIndexForKey("gene");
		}
	}
	
	@Override
	public boolean variantPasses(Variant var) {
		String varType = var.getAnnotationStr(varTypeIndex);
		String exonFunc = var.getAnnotationStr(exonFuncIndex);
		
		if (varType == null)
			return missingDataPasses;
		
		if (excludeIntergenic 
				&& (varType.contains("intergenic") 
						|| varType.contains("upstream") 
						|| varType.contains("downstream"))
						|| (var.getAnnotationStr(geneIndex) != null && var.getAnnotationStr(geneIndex).length() < 2)) {
			return false;
		}
		
		if (excludeIntronic && varType.contains("intronic")) {
			return false;
		}
		
		if (excludeUTR && varType.contains("UTR")) {
			return false;
		}
		
		if (excludeNCRNA && varType.contains("ncRNA")) {
			return false;
		}
		
		if (excludeSplicing && varType.contains("splic")) {
			return false;
		}
		
		
		// ORDER CRUCIAL! Don't test exonFunc for nullness until after all varType tests!
		if (exonFunc == null) {
			return missingDataPasses;
		}
		
		if (excludeSynonymous && exonFunc.startsWith("synonymous")) {
			return false;
		}
		
		if (excludeNonsynonymous && exonFunc.contains("nonsynonymous")) {
			return false;
		}
		
		if (excludeNonFrameshift && exonFunc.contains("nonframeshift")) {
			return false;
		}
		
		if (excludeStopGainsLosses && exonFunc.contains("stop")) {
			return false;
		}

		
		if (excludeFrameshift && exonFunc.startsWith("frameshift")) {
			return false;
		}
		

		return true;
	}

	public boolean isExcludeIntergenic() {
		return excludeIntergenic;
	}

	public void setExcludeIntergenic(boolean excludeIntergenic) {
		this.excludeIntergenic = excludeIntergenic;
	}

	public boolean isExcludeIntronic() {
		return excludeIntronic;
	}

	public void setExcludeIntronic(boolean excludeIntronic) {
		this.excludeIntronic = excludeIntronic;
	}

	public boolean isExcludeSynonymous() {
		return excludeSynonymous;
	}

	public void setExcludeSynonymous(boolean excludeSynonymous) {
		this.excludeSynonymous = excludeSynonymous;
	}

	public boolean isExcludeNonsynonymous() {
		return excludeNonsynonymous;
	}

	public void setExcludeNonsynonymous(boolean excludeNonsynonymous) {
		this.excludeNonsynonymous = excludeNonsynonymous;
	}

	public boolean isExcludeNonFrameshift() {
		return excludeNonFrameshift;
	}

	public void setExcludeNonFrameshift(boolean excludeNonFrameshift) {
		this.excludeNonFrameshift = excludeNonFrameshift;
	}

	public boolean isExcludeFrameshift() {
		return excludeFrameshift;
	}

	public void setExcludeFrameshift(boolean excludeFrameshift) {
		this.excludeFrameshift = excludeFrameshift;
	}

	public boolean isExcludeSplicing() {
		return excludeSplicing;
	}

	public void setExcludeSplicing(boolean excludeSplicing) {
		this.excludeSplicing = excludeSplicing;
	}

	public boolean isExcludeStopGainsLosses() {
		return excludeStopGainsLosses;
	}

	public void setExcludeStopGainsLosses(boolean excludeStopGainsLosses) {
		this.excludeStopGainsLosses = excludeStopGainsLosses;
	}

	public boolean isExcludeUTR() {
		return excludeUTR;
	}

	public void setExcludeUTR(boolean excludeUTR) {
		this.excludeUTR = excludeUTR;
	}

	public boolean isMissingDataPasses() {
		return missingDataPasses;
	}

	public void setMissingDataPasses(boolean missingDataPasses) {
		this.missingDataPasses = missingDataPasses;
	}

	public boolean isExcludeNCRNA() {
		return excludeNCRNA;
	}

	public void setExcludeNCRNA(boolean excludeNCRNA) {
		this.excludeNCRNA = excludeNCRNA;
	}

	@Override
	public String getUserDescription() {
		StringBuilder str = new StringBuilder("Variants of the following types were excluded: ");
		List<String> strs = new ArrayList<String>();
		if (isExcludeIntergenic()) {
			strs.add("intergenic");
		}
		if (isExcludeIntronic()) {
			strs.add("intronic");
		}
		if (isExcludeNCRNA()) {
			strs.add("non-coding RNA");
		}
		if (isExcludeNonFrameshift()) {
			strs.add("non-frameshifting");
		}
		if (isExcludeFrameshift()) {
			strs.add("frameshifting");
		}
		if (isExcludeSplicing()) {
			strs.add("splicing");
		}
		if (isExcludeStopGainsLosses()) {
			strs.add("stop gains and losses");
		}
		if (isExcludeSynonymous()) {
			strs.add("synonymous SNPs");
		}
		if (isExcludeNonsynonymous()) {
			strs.add("nonsynonymous SNPs");
		}
		if (isExcludeUTR()) {
			strs.add("UTR");
		}
		
		if (strs.size()==0) {
			return "No filtering based on variant type was performed.";
		}
		str.append(strs.get(0));
		for(int i=0; i<strs.size(); i++) {
			str.append(", " + strs.get(i));
		}
		
		return str.toString();
	}

	
	
	
}
