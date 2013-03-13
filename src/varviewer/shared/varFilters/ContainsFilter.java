package varviewer.shared.varFilters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import varviewer.shared.Variant;
import varviewer.shared.VariantFilter;

/**
 * This VariantFilter passes any variants whose annotation *contains* at least one of the terms
 * supplied, and excludes all others. For instance, to create a filter that passes 
 * nonsynonymous or frameshifting indels, create one of these with annotation = "exon.function"
 * and terms containing "nonsynonymous" or "frameshift"  
 * @author brendan
 *
 */
public class ContainsFilter implements VariantFilter, Serializable {

	private List<String> terms = null;
	private String annotation = null;
	private boolean missingDataPasses = true;
	
	public ContainsFilter() {
		//required no-arg constructor
	}
	
	public ContainsFilter(String annoKey, String term) {
		this.annotation = annoKey;
		setTerm(term);
	}
	
	public ContainsFilter(String annoKey, List<String> termList) {
		this.annotation = annoKey;
		setTerms(termList);
	}
	
	/**
	 * Clear the current list of terms and add all terms from the given list
	 * @param terms
	 */
	public void setTerms(List<String> terms) {
		this.terms = terms;
	}
	
	/**
	 * Clear all current terms, add single given term
	 * @param term
	 */
	public void setTerm(String term) {
		terms = new ArrayList<String>();
		terms.add(term);
	}
	
	/**
	 * Add new term to list of terms
	 * @param newTerm
	 */
	public void addTerm(String newTerm) {
		if (terms == null) {
			terms = new ArrayList<String>();
		}
		terms.add(newTerm);
	}
	
	/**
	 * True if missing data should pass the filter, false otw
	 * @param passes
	 */
	public void setMissingDataPasses(boolean passes) {
		this.missingDataPasses = passes;
	}
	
	@Override
	public boolean variantPasses(Variant var) {
		String val = var.getAnnotationStr(annotation);
		if (val == null)
			return missingDataPasses;
		
		for(String term : terms) {
			if (val.contains(term)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getUserDescription() {
		String termStr = terms.get(0);
		for(int i=1; i<terms.size(); i++) {
			termStr = termStr + ", " + terms.get(i);
		}
		return "All variants whose annotation " + annotation + " does not include any of the following terms " + termStr;
	}

}
