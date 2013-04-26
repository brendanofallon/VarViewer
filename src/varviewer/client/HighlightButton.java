package varviewer.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Button that draws a background and border on mouseover
 * @author brendan
 *
 */
public class HighlightButton extends FocusPanel {

	private HorizontalPanel interiorPanel = new HorizontalPanel();
	private Label lab; 
	private boolean enabled = true;
	
	public HighlightButton(Image image, ClickHandler handler) {
		initialize();
		interiorPanel.add(image);
		addClickHandler(handler);
	}
	
	public HighlightButton(String text, Image image) {
		initialize();
		lab = new Label(text);
		lab.setStylePrimaryName("textlabel");
		interiorPanel.add(lab);
		SimplePanel spacer = new SimplePanel();
		spacer.setWidth("4px");
		interiorPanel.add(spacer);
		interiorPanel.add(image);
	}
	
	public HighlightButton(Image image, String text,  ClickHandler handler) {
		initialize();
		lab = new Label(text);
		lab.setStylePrimaryName("textlabel");
		SimplePanel spacer = new SimplePanel();
		spacer.setWidth("4px");
		interiorPanel.add(image);
		interiorPanel.add(spacer);
		interiorPanel.add(lab);
		addClickHandler(handler);
	}
	
	public HighlightButton(Image image) {
		initialize();
		interiorPanel.add(image);
	}
	
	
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	private void initialize() {
		this.add(interiorPanel);
		setStylePrimaryName("highlightbutton");
		addMouseOverHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				if (enabled)
					setStylePrimaryName("highlightbutton-hover");
			}
			
		});
		addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				setStylePrimaryName("highlightbutton");
			}
			
		});		
	}
}
