package varviewer.shared.varFilters;

import java.io.Serializable;

import varviewer.shared.Variant;
import varviewer.shared.VariantFilter;

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
	private boolean excludeFrameshift = false;
	private boolean excludeSplicing = false;
	private boolean excludeUTR = true;
	private boolean excludeNCRNA = true;
	
	private boolean missingDataPasses = true;
	
	public ExonFuncFilter() {
		//Required no-arg constructor
	}
	
	@Override
	public boolean variantPasses(Variant var) {
		String varType = var.getAnnotation("variant.type");
		String exonFunc = var.getAnnotation("exon.function");
		
		if (varType == null)
			return missingDataPasses;
		
		if (excludeIntergenic && varType.contains("intergenic")) {
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

	
	
	
}
