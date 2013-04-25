package varviewer.client.serviceUI;

import java.util.List;

import varviewer.client.Home;
import varviewer.shared.services.ServiceDescription;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Displays a list of services in graphical format
 * @author brendan
 *
 */
public class ServicesPanel extends FlowPanel {

	private ServiceUIRepo repo = new ServiceUIRepo();
	private Home homePanel;
	
	public ServicesPanel(Home homePanel) {
		this.homePanel = homePanel;
	}
	
	public void setServices(List<ServiceDescription> services) {
		for(ServiceDescription desc : services) {
			if (desc.getClassName().equals("varviewer.client.serviceUI.ViewSamples")) {
				addService(desc.getServiceUserText(), repo.getService(desc.getServiceID()));
				
			}
		}
	}

	private void addService(String label, final ServiceUI serviceUI) {
		Image image = serviceUI.getIcon();
		final FocusPanel wrapper = new FocusPanel();
		FlowPanel panel = new FlowPanel();
		panel.add(image);
		panel.add(new Label(label));
		wrapper.setStylePrimaryName("bigbutton");
		wrapper.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				wrapper.setStylePrimaryName("bigbutton-hover");
			}
		});
		wrapper.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				wrapper.setStylePrimaryName("bigbutton");
			}
		});
		
		wrapper.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				homePanel.loadService(serviceUI);
			} 
			
		});
		wrapper.add(panel);		
		this.add(wrapper);
	}
	
}
