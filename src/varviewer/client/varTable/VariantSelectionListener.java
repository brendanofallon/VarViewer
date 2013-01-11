package varviewer.client.varTable;

import varviewer.shared.Variant;

/**
 * These objects are notified when a new variant is selected in the main variant table
 * @author brendan
 *
 */
public interface VariantSelectionListener {

	/**
	 * Called when a new variant has been selected, may be null if no variant
	 * is selected
	 * @param selectedVar
	 */
	public void variantSelected(Variant selectedVar);
	
}
