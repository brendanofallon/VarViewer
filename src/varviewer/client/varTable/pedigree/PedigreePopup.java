package varviewer.client.varTable.pedigree;

import varviewer.client.varTable.pedigree.PedigreeSample.OperationType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Allows users to select additional samples to intersect and subtract from 
 * the current sample
 * @author brendanofallon
 *
 */
public class PedigreePopup extends PopupPanel {

	
	FlowPanel mainPanel = new FlowPanel();
	HorizontalPanel centerPanel = new HorizontalPanel();
	HorizontalPanel bottomPanel = new HorizontalPanel();
	SampleChooserPanel includesPanel = new SampleChooserPanel("Intersect");
	SampleChooserPanel excludesPanel = new SampleChooserPanel("Subtract");
	
	 
	public PedigreePopup() {
		super(false);
		initComponents();
		setWidth("650px");
		setHeight("350px");
	}

	private void initComponents() {
		this.add(mainPanel);
		Label header = new Label("Choose additional samples to intersect and subtract");
		header.setStylePrimaryName("pedpopuptitle");
		mainPanel.add(header);
		
		centerPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		centerPanel.add(includesPanel);
		includesPanel.setDefaultOp(OperationType.INTERSECT);
		centerPanel.add(excludesPanel);
		excludesPanel.setDefaultOp(OperationType.EXCLUDE);
		mainPanel.add(centerPanel);
		
		Button cancelButton = new Button("Cancel");
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hidePopup();
			}
		});
		Button doneButton = new Button("Done");
		doneButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hidePopup();
			}
		});
		bottomPanel.add(cancelButton);
		SimplePanel spacer = new SimplePanel();
		spacer.setWidth("200px");
		bottomPanel.add(spacer);
		bottomPanel.add(doneButton);
		mainPanel.add(bottomPanel);
	}

	protected void hidePopup() {
		this.hide();
	}
}
