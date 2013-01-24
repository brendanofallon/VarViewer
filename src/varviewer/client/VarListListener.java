package varviewer.client;

import java.util.List;

import varviewer.shared.Variant;

/**
 * Anything that listens for changes to a list of variants should implement this
 * @author brendan
 *
 */
public interface VarListListener {

	/**
	 * Called when a variant list has been updated
	 * @param newVars
	 */
	public void variantListUpdated(List<Variant> newVars);
	
	/**
	 * Called when a variant list update has been requested (but not necessarily finished)
	 * @param newVars
	 */
	public void variantListUpdateBeginning();
	
	/**
	 * Called when a variant list update request has encountered an error
	 * @param newVars
	 */
	public void variantListUpdateError();
	
}
