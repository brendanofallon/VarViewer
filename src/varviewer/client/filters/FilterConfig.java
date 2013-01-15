package varviewer.client.filters;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * An individual config tool for a variant filter box
 * @author brendan
 *
 */
public abstract class FilterConfig extends PopupPanel {

	final protected FilterBox parentBox;
	final protected DockLayoutPanel interiorPanel = new DockLayoutPanel(Unit.PX);
	
	public FilterConfig(FilterBox parent) {
		parentBox = parent;
		initComponents();
	}

	protected void cancel() {
		this.hide();
	}
	
	protected void done() {
		boolean ok = validateAndUpdateFilter();
		if (ok) {
			parentBox.getFiltersPanel().fireFiltersChanged();
			this.hide();
		}
	}
	
	/**
	 * Check to see if the user input is OK, if so update the filter with the new settings
	 * and return true. If not, return false and do nothing. 
	 * @return
	 */
	protected abstract boolean validateAndUpdateFilter();
	
	/**
	 * Create UI components
	 */
	private void initComponents() {
		this.setStylePrimaryName("filterconfig");
		add(interiorPanel);
		Label lab = new Label("Configure filter " + parentBox.getName());
		lab.setStylePrimaryName("textlabel");
		interiorPanel.addNorth(lab, 20.0);
		
		
		HorizontalPanel bottomPanel = new HorizontalPanel();
		bottomPanel.setStylePrimaryName("topborder");
		interiorPanel.addSouth(bottomPanel, 28.0);
		
		Button cancelButton = new Button("Cancel");
		bottomPanel.add(cancelButton);
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				cancel();
			}
		});
		
		SimplePanel spacer = new SimplePanel();
		spacer.setWidth("160px");
		bottomPanel.add(spacer);
		
		Button doneButton = new Button("Done");
		doneButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				done();
			}
		});
		bottomPanel.add(doneButton);
		cancelButton.getElement().getStyle().setPadding(3.0, Unit.PX);
		doneButton.getElement().getStyle().setPadding(3.0, Unit.PX);
		
	}
	
}
