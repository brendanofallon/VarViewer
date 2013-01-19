package varviewer.shared;

public interface VariantFilter {

	/**
	 * Returns true if the given variant passes this filter, false if it doesn't pass
	 * and, typically, should be excluded from further analysis
	 * @param var
	 * @return
	 */
	public boolean variantPasses(Variant var);
}
