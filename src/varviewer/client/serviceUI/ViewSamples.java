package varviewer.client.serviceUI;

import varviewer.client.IGVInterface;
import varviewer.client.sampleView.DisplayVariantsListener;
import varviewer.client.sampleView.SamplesView;
import varviewer.client.varTable.VariantDisplay;
import varviewer.shared.SampleInfo;

import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ViewSamples implements ServiceUI, DisplayVariantsListener {

	private FlowPanel mainWidget;
	private VariantDisplay varDisplay = null;
	private SamplesView sampleView = null;
	
	@Override
	public Image getIcon() {
		Image image = new Image("images/qcIcon64.png");
		return image;
	}

	@Override
	public void initialize() {
		mainWidget = new FlowPanel();
		varDisplay = new VariantDisplay(this);
		sampleView = new SamplesView(this);
		sampleView.refreshSampleList();
		mainWidget.add(sampleView);
	}

	@Override
	public void close() {
		varDisplay = null;
		sampleView = null;
	}
	
	public VariantDisplay getVariantDisplay() {
		return varDisplay;
	}
	
	/**
	 * Clear main panel and show the VariantDisplay in it. 
	 */
	public void showSampleChooser() {
		mainWidget.clear();
		mainWidget.add(sampleView);
	}
	
	@Override
	public Widget getWidget() {
		return mainWidget;
	}

	@Override
	public void showVariantsForSample(SampleInfo chosenSample) {
		if (varDisplay == null) {
			varDisplay = new VariantDisplay(this);
		}
		
		
		mainWidget.clear();
		varDisplay.setBamLink( IGVInterface.baseURL + "load?file=http://" + Location.getHostName() + "/" + chosenSample.getBamLink() );
		varDisplay.setSample(chosenSample.getSampleID());
		mainWidget.add(varDisplay);
	}



}
