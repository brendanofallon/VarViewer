package varviewer.client.sampleView;

import varviewer.shared.SampleInfo;

public interface DisplayVariantsListener {

	
	/**
	 * Called when the user has requested to view the variants for a selected sample
	 * @param selectedInfo
	 */
	public void showVariantsForSample(SampleInfo chosenSample);
	
}
