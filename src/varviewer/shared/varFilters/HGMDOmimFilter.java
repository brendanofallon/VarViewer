package varviewer.shared.varFilters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	
	private boolean includeClinvarPathogenic = true;
	private boolean includeClinvarLikelyPathogenic = true;
	private boolean includeClinvarVUS = true;
	private boolean includeClinvarLikelyBenign = true;
	private boolean includeClinvarBenign = true;
		
	private AnnotationIndex annoIndex = null;
	private int hgmdInfoIndex = -1;
	private int hgmdExactIndex = -1;
	private int omimIndex = -1;
	private int clinvarSigIndex = -1;
	
	
	
	public HGMDOmimFilter() {
		//Required no-arg constructor
	}

	public void setAnnotationIndex(AnnotationIndex index) {
		this.annoIndex = index;
		hgmdInfoIndex = index.getIndexForKey("hgmd.info");
		omimIndex = index.getIndexForKey("omim.disease");
		hgmdExactIndex = index.getIndexForKey("hgmd.hit");
		clinvarSigIndex = index.getIndexForKey("clinvar.clnsig");
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

	public boolean isIncludeClinvarPathogenic() {
		return includeClinvarPathogenic;
	}

	public boolean isIncludeClinvarLikelyPathogenic() {
		return includeClinvarLikelyPathogenic;
	}

	public boolean isIncludeClinvarVUS() {
		return includeClinvarVUS;
	}

	public boolean isIncludeClinvarLikelyBenign() {
		return includeClinvarLikelyBenign;
	}

	public boolean isIncludeClinvarBenign() {
		return includeClinvarBenign;
	}


	@Override
	public boolean variantPasses(Variant var) {
		String hgmdExact = var.getAnnotationStr(hgmdExactIndex);
		String hgmdInfo = var.getAnnotationStr(hgmdInfoIndex);
		String omim = var.getAnnotationStr(omimIndex);
		String clinSig = var.getAnnotationStr(clinvarSigIndex);

		
		if (clinSig != null && (!clinSig.equals("-"))) {
			if (includeClinvarPathogenic && clinSig.equals("5")) {
				return true;
			}
			if (includeClinvarLikelyPathogenic && clinSig.equals("4")) {
				return true;
			}
			if (includeClinvarVUS && (clinSig.equals("0") || clinSig.equals("1") || clinSig.equals("6") || clinSig.equals("7") || clinSig.equals("255"))) {
				return true;
			}
			if (includeClinvarLikelyBenign && clinSig.equals("3")) {
				return true;
			}
			if (includeClinvarBenign && clinSig.equals("2")) {
				return true;
			}
		}
		
		if ((!excludeNonExactHits) && hgmdExact != null && hgmdExact.length()>1) {
			return true;
		}
		
		
		if ((!excludeNonGeneHits) && ((hgmdInfo != null && hgmdInfo.length()>1) || (omim != null && omim.length()>1))) {
			return true;
		}
		
		return false;
	}

	@Override
	public String getUserDescription() {
		List<String> excludes = new ArrayList<String>();
		if (! includeClinvarPathogenic) {
			excludes.add("Pathogenic");
		}
		if (! includeClinvarLikelyPathogenic) {
			excludes.add("Likely pathogenic");
		}
		if (! includeClinvarVUS) {
			excludes.add("VUS & other");
		}
		if (! includeClinvarLikelyBenign) {
			excludes.add("Likely benign");
		}
		if (! includeClinvarBenign) {
			excludes.add("Benign");
		}
		
		
		if (excludeNonExactHits) {
			excludes.add("HGMD exact matches"); 
		}
		if (excludeNonGeneHits) {
			excludes.add("HGMD & OMIM gene matches");
		}
		
		if (excludes.size()==0) {
			return "No ClinVar, OMIM, or HGMD disease filtering was performed";
		}
		
		return "Variants in the following disease classes were excluded: " + join(excludes, ", ");
	}

	private static String join(List<String> strs, String joiner) {
		if (strs.size() == 0) {
			return "";
		}
		
		StringBuilder strb = new StringBuilder();
		for(int i=0; i< strs.size()-1; i++) {
			strb.append(strs.get(i));
			strb.append(joiner);
		}
		strb.append(strs.get(strs.size()-1));
		
		return strb.toString();
	}
	

	public void setIncludeClinvarPathogenic(boolean includeClinvarPathogenic) {
		this.includeClinvarPathogenic = includeClinvarPathogenic;
	}


	public void setIncludeClinvarLikelyPathogenic(
			boolean includeClinvarLikelyPathogenic) {
		this.includeClinvarLikelyPathogenic = includeClinvarLikelyPathogenic;
	}


	public void setIncludeClinvarVUS(boolean includeClinvarVUS) {
		this.includeClinvarVUS = includeClinvarVUS;
	}


	public void setIncludeClinvarLikelyBenign(boolean includeClinvarLikelyBenign) {
		this.includeClinvarLikelyBenign = includeClinvarLikelyBenign;
	}


	public void setIncludeClinvarBenign(boolean includeClinvarBenign) {
		this.includeClinvarBenign = includeClinvarBenign;
	}
}
