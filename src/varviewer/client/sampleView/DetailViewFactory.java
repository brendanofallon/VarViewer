package varviewer.client.sampleView;

import varviewer.shared.SampleInfo;

/**
 * Factory for creating SampleDetailDisplays used in the SamplesView for individual sample types
 * @author brendan
 *
 */
public class DetailViewFactory {

	//Create the right type of Sample detail display for the given sample info
	//This defaults to SampleDetailView, but 
	public static SampleDetailDisplay getDetailDisplay(SampleInfo info, DisplayVariantsListener displayListener) {
		
		if (info.getAnalysisType().contains("BCR-ABL")) {
			return new BCRABLDetailView(displayListener);
		}
		else {
			return new SampleDetailView(displayListener);
		}
	}
}
