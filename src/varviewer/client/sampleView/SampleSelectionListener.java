package varviewer.client.sampleView;

import varviewer.shared.SampleInfo;

/**
 * Interface for objects that want to know when the user has selected a new sample in the SampleChooserList
 * @author brendan
 *
 */
public interface SampleSelectionListener {

	/**
	 * Called when a new sample has been selected in a SampleChooserList
	 * @param selectedInfo
	 */
	public void updateSelectedSample(SampleInfo selectedInfo);
	
}
