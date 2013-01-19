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
}
