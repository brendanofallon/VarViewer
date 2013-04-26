package varviewer.client.varTable.filters;

import java.util.List;

import varviewer.shared.variant.VariantFilter;

/**
 * FilterListers listen for changes to a series of VariantFilters, for instance, produced
 * by the elements in the FiltersPanel. Presumably, when the filters change we'll want to
 * update the list of displayed variants
 * @author brendan
 *
 */
public interface FilterListener {

	/**
	 * Called when the list of filters has been updated. 
	 * @param newFilters
	 */
	public void filtersUpdated(List<VariantFilter> newFilters);
}
