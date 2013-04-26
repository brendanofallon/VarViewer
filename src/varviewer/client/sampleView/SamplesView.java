package varviewer.client.sampleView;

import varviewer.shared.SampleInfo;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Displays a list of samples in fancy graphical format. Mostly just a wrapper for a
 * SampleChooserList (at left) and a SampleDetailsPanel (center)
 * @author brendan
 *
 */
public class SamplesView extends FlowPanel implements SampleSelectionListener {

	private DisplayVariantsListener displayListener;
	
	public SamplesView(DisplayVariantsListener displayListener) {
		this.displayListener = displayListener;
		initComponents();
	}

	/**
	 * Re-load list of samples from server. Delegates to SampleChooserList
	 */
	public void refreshSampleList() {
		listPanel.refreshSampleList();
	}
	
	private void initComponents() {
		this.setStylePrimaryName("samplesview");
		
		FlowPanel topPanel = new FlowPanel();
		HTML topLabel = new HTML("<h3>Select a sample to view </h3>");
		topPanel.add(topLabel);
		this.add(topPanel);
		
		HorizontalPanel mainArea = new HorizontalPanel();
		this.add(mainArea);
		listPanel = new SampleChooserList(this);	
		mainArea.add(listPanel);
		
		//Create sample details panel...
		sampleDetailsPanel = new SampleDetailView(displayListener);
		mainArea.add(sampleDetailsPanel);
	}
	
	/**
	 * Called when a new sample has been selected in the list. We 
	 * update the details panel to show the new sample here. 
	 * @param selectedSample
	 */
	public void updateSelectedSample(SampleInfo selectedSample) {
		sampleDetailsPanel.setSample(selectedSample);
	}

	
	private SampleChooserList listPanel;
	private SampleDetailView sampleDetailsPanel;

	
}
