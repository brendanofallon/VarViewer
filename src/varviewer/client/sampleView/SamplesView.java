package varviewer.client.sampleView;

import varviewer.shared.SampleInfo;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;

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
		//sampleDetailsPanel = new SampleDetailView(displayListener);
		detailsContainer = new SimplePanel();
		detailsContainer.add(new HTML("<h4> Select a sample to view </h4>"));
		mainArea.add(detailsContainer);
	}
	
	/**
	 * Called when a new sample has been selected in the list. We 
	 * update the details panel to show the new sample here. 
	 * @param selectedSample
	 */
	public void updateSelectedSample(SampleInfo selectedSample) {
	
		SampleDetailDisplay sampleDisplay = DetailViewFactory.getDetailDisplay(selectedSample, displayListener);
		sampleDisplay.displayDetailsForSample(selectedSample);
		detailsContainer.clear();
		detailsContainer.add(sampleDisplay);
	}

	
	private SampleChooserList listPanel;
	private SampleDetailView sampleDetailsPanel;
	private SimplePanel detailsContainer;
	
}
