package varviewer.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class FiltersPanel extends FlowPanel {
	
	public FiltersPanel() {
		this.setStylePrimaryName("filterspanel");
		
		Label filter1 = new Label("Filter #1");
		filter1.setStylePrimaryName("filterbox");
		filter1.setHeight("50px");
		this.add(filter1);
		
		Label filter2 = new Label("Filter #2");
		filter2.setStylePrimaryName("filterbox");
		filter2.setHeight("50px");
		this.add(filter2);
	}

}
