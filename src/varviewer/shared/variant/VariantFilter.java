package varviewer.shared.variant;

import com.google.gwt.user.client.rpc.IsSerializable;


public interface VariantFilter extends IsSerializable {

	/**
	 * Returns true if the given variant passes this filter, false if it doesn't pass
	 * and, typically, should be excluded from further analysis
	 * @param var
	 * @return
	 */
	public boolean variantPasses(Variant var);
	
	/**
	 * Set pre-computed table of which annotation keys are associated with which indices
	 * for faster filtering. 
	 * @param index
	 */
	public void setAnnotationIndex(AnnotationIndex index);
	
	/**
	 * A user-readable description of the current settings of this filter. This is required
	 * for report generation, which needs automated & plainly written text describing all
	 * filtering settings. 
	 * @return
	 */
	public String getUserDescription();
}
