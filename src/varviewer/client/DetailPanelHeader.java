package varviewer.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class DetailPanelHeader extends FlowPanel {
	
	DetailsPanel parentPanel;
	Label label = new Label("Gene details");
	
	public DetailPanelHeader(DetailsPanel parentPanel) {
		this.parentPanel = parentPanel;
		this.setStylePrimaryName("detailspanelheader");
		
		this.add(label);
	}

	public void updateLabel() {
		label.setText(parentPanel.getCurrentGene());
	}
}
