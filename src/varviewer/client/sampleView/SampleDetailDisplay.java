package varviewer.client.sampleView;

import varviewer.shared.SampleInfo;

import com.google.gwt.user.client.ui.FlowPanel;

public abstract class SampleDetailDisplay extends FlowPanel {

	public abstract void displayDetailsForSample(SampleInfo sampleInfo);
	
}
