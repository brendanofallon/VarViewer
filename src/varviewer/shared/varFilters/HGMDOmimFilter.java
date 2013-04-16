package varviewer.shared.varFilters;

import java.io.Serializable;

import varviewer.shared.variant.AnnotationIndex;
import varviewer.shared.variant.Variant;
import varviewer.shared.variant.VariantFilter;

/**
 * Filters out variants not in HGMD, OMIM, etc. 
 * @author brendanofallon
 *
 */
public class HGMDOmimFilter implements VariantFilter, Serializable {

	private boolean excludeNonExactHits = false;
	private boolean excludeNonGeneHits = false;
	
	private AnnotationIndex annoIndex = null;
	private int hgmdInfoIndex = -1;
	private int hgmdExactIndex = -1;
	private int omimIndex = -1;
	
	
	
	public HGMDOmimFilter() {
		//Required no-arg constructor
	}

	public void setAnnotationIndex(AnnotationIndex index) {
		this.annoIndex = index;
		hgmdInfoIndex = index.getIndexForKey("hgmd.info");
		omimIndex = index.getIndexForKey("omim.disease");
		hgmdExactIndex = index.getIndexForKey("hgmd.hit");
	}
	
	public boolean isExcludeNonExactHits() {
		return excludeNonExactHits;
	}

	public void setExcludeNonExactHits(boolean excludeNonExactHits) {
		this.excludeNonExactHits = excludeNonExactHits;
	}



	public boolean isExcludeNonGeneHits() {
		return excludeNonGeneHits;
	}



	public void setExcludeNonGeneHits(boolean excludeNonGeneHits) {
		this.excludeNonGeneHits = excludeNonGeneHits;
	}

	@Override
	public boolean variantPasses(Variant var) {
		String hgmdExact = var.getAnnotationStr(hgmdExactIndex);
		String hgmdInfo = var.getAnnotationStr(hgmdInfoIndex);
		String omim = var.getAnnotationStr(omimIndex);
		
		if (excludeNonExactHits) {
			if (hgmdExact == null || hgmdExact.length()<2) {
				return false;
			}
		}
		if (excludeNonGeneHits) {
			if ( (hgmdInfo == null || hgmdInfo.length()<2) && (omim == null || omim.length()<2)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getUserDescription() {
		if (excludeNonExactHits==false && excludeNonGeneHits == false) {
			return "No filtering based on HGMD and OMIM data was performed.";
		}
		if (excludeNonExactHits) {
			return "Variants not exactly matching a mutation documented in HGMD were excluded."; 
		}
		if (excludeNonGeneHits) {
			return "Variants not in genes associated with disease in HGMD or OMIM were excluded.";
		}
		return "?";
	}

}
