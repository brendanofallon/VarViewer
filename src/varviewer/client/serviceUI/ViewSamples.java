package varviewer.client.serviceUI;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ViewSamples implements ServiceUI {

	
	@Override
	public Image getIcon() {
		Image image = new Image("images/qcIcon64.png");
		return image;
	}

	@Override
	public void initialize() {
		//Nothing to do
	}

	@Override
	public Widget getWidget() {
		FlowPanel samplesPanel = new FlowPanel();
		samplesPanel.add(new Label("View samples here"));
		return samplesPanel;
	}

}
