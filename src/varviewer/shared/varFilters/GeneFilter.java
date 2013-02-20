package varviewer.shared.varFilters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import varviewer.shared.Variant;
import varviewer.shared.VariantFilter;

/**
 * Filters variants based on a set of genes
 * @author brendanofallon
 *
 */
public class GeneFilter implements VariantFilter, Serializable {

	Set<String> geneNames = new HashSet<String>();
	
	public void setGeneNames(Collection<String> geneNames) {
		this.geneNames.clear();
		this.geneNames.addAll(geneNames);
	}
	
	@Override
	public boolean variantPasses(Variant var) {
		if (geneNames.size()==0) {
			return true;
		}
		String name = var.getAnnotationStr("gene");
		if (name != null && geneNames.contains(name)) {
			return true;
		}
		return false;
	}

	/**
	 * Return all filtered gene names in a list
	 * @return
	 */
	public List<String> getGeneNames() {
		List<String> names = new ArrayList<String>();
		names.addAll(geneNames);
		return names;
	}

}
