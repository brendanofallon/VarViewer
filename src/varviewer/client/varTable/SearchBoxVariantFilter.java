package varviewer.client.varTable;

import java.util.ArrayList;
import java.util.List;

import varviewer.shared.Variant;
import varviewer.shared.VariantFilter;


/**
 * A VariantFilter associated with the 'search box' in the var table header. Text changes in that box
 * are reflected as filters here 
 * @author brendan
 *
 */
public class SearchBoxVariantFilter implements VariantFilter {

	List<VariantFilter> filters = new ArrayList<VariantFilter>();
	
	public void setTerms(String terms) {
		filters.clear();
		String[] termArray = terms.split(",");
		for(int i=0; i<termArray.length; i++) {
			filters.add(filterForTerm(termArray[i].trim()));
		}
	}
	
	/**
	 * Remove all sub-filters.
	 */
	public void clearFilters() {
		filters.clear();
	}
	
	/**
	 * Clear current filters and add one new filter that only passes variants
	 * with gene names in the given list
	 * @param geneNames
	 */
	//Unused currently. Maybe useful at some point. 
//	public void setMultiGeneNameFilter(List<String> geneNames) {
//		clearFilters();
//		filters.add(new MultiGeneFilter(geneNames));
//	}
	
	/**
	 * Convert a single term (e.g. "ENG" or "chrX:5-17") into a variant filter
	 * @param term
	 * @return
	 */
	private VariantFilter filterForTerm(String term) {
		if (term.startsWith("chr")) {
			if (! term.contains(":") && term.length()<6)
				return new RegionFilter(term.replace("chr", ""));
			if (term.contains(":")) {
				String[] toks = term.split(":");
				if (toks.length==0) {
					return new RegionFilter(toks[0].replace("chr", ""));
				}
				else {
					String posStr = toks[1];
					String[] posses = posStr.split("-");
					
					try {
						int posA = Integer.parseInt(posses[0].trim());
						int posB = posA;
						if (posses.length>1)
							posB = Integer.parseInt(posses[1].trim());
						return new RegionFilter(toks[0].replace("chr", ""), posA, posB);
						
					}
					catch (NumberFormatException nfe) {
						
					}
					
				}
			}
		}
		else {
			//Term doesn't start with chr, assume it's a gene
			return new GeneFilter(term.trim());
		}
		
		return null;
	}

	@Override
	public boolean variantPasses(Variant var) {
		//True if ANY of the filters passes the variant..
		for(VariantFilter filter : filters) {
			if (filter.variantPasses(var)) {
				return true;
			}
		}
		
		//
		return false;
	}
	
	public int getFilterCount() {
		return filters.size();
	}

//	class MultiGeneFilter implements VariantFilter {
//
//		final List<String> genes = new ArrayList<String>();
//		
//		public MultiGeneFilter(List<String> genes) {
//			this.genes.addAll(genes);
//		}
//		
//		@Override
//		public boolean variantPasses(Variant var) {
//			String varGene = var.getAnnotationStr("gene");
//			if (varGene != null && genes.contains(varGene)) {
//				return true;
//			}
//			return false;
//		}
//
//		@Override
//		public String getUserDescription() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//		
//	}
	/**
	 * Sub-filter for gene names
	 * @author brendan
	 *
	 */
	class GeneFilter implements VariantFilter {
		String geneName;
		
		GeneFilter(String gene) {
			this.geneName = gene.toUpperCase();
		}
		
		@Override
		public boolean variantPasses(Variant var) {
			String varGene = var.getAnnotationStr("gene");
			if (varGene != null && varGene.startsWith(geneName)) {
				return true;
			}
			return false;
		}

		/**
		 * Not ever used to generate user text, so nothing required here
		 */
		public String getUserDescription() {
			return null;
		}
		
	}
	
	
	class RegionFilter implements VariantFilter {
		String chr;
		int startPos;
		int endPos;
		
		public RegionFilter(String chr) {
			this.chr = chr;
			this.startPos = 0;
			this.endPos = Integer.MAX_VALUE;
		}
		
		public RegionFilter(String chr, int start, int end) {
			this.chr = chr;
			this.startPos = start;
			this.endPos = end;
		}
		
		@Override
		public boolean variantPasses(Variant var) {
			return var.getChrom().equals(chr) && var.getPos() >= startPos && var.getPos() <= endPos; 
			
		}

		@Override
		/**
		 * Not ever used to generate user text, so nothing required here
		 */
		public String getUserDescription() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}


	@Override
	/**
	 * Not ever used to generate user text, so nothing required here
	 */
	public String getUserDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}
