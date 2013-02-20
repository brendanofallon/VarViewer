package varviewer.shared.varFilters;

import java.io.Serializable;

import varviewer.shared.Variant;
import varviewer.shared.VariantFilter;

/**
 * Filters out variants not in HGMD, OMIM, etc. 
 * @author brendanofallon
 *
 */
public class HGMDOmimFilter implements VariantFilter, Serializable {

	private boolean excludeNonExactHits = false;
	private boolean excludeNonGeneHits = false;
	
	public HGMDOmimFilter() {
		//Required no-arg constructor
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
		String hgmdExact = var.getAnnotationStr("hgmd.hit");
		String hgmdInfo = var.getAnnotationStr("hgmd.info");
		String omim = var.getAnnotationStr("omim.disease");
		
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

}
