package varviewer.client.varTable.cisTrans;

import varviewer.client.varTable.VariantDisplay;
import varviewer.client.varTable.pedigree.SampleChooserPanel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class CisTransPopup extends PopupPanel {

	private FlowPanel mainPanel = new FlowPanel();
	private HorizontalPanel centerPanel = new HorizontalPanel();
	private HorizontalPanel bottomPanel = new HorizontalPanel();
	private SampleChooserPanel includesPanel = new SampleChooserPanel("Intersect affected cases");
	private SampleChooserPanel excludesPanel = new SampleChooserPanel("Subtract healthy controls");
	private VariantDisplay varDisplay;
	 
	public CisTransPopup(VariantDisplay display) {
		super(false);
		this.varDisplay = display;
		initComponents();
		setWidth("700px");
		setHeight("350px");
	}

	private void initComponents() {
		this.add(mainPanel);
		this.setStylePrimaryName("genericpopup");
		Label header = new Label("Cis / trans inference");
		header.setStylePrimaryName("pedpopuptitle");
		mainPanel.add(header);
		
		
		
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
				done();
			}
		});
		bottomPanel.getElement().getStyle().setMarginTop(10, Unit.PX);
		bottomPanel.add(cancelButton);
		cancelButton.getElement().getStyle().setMarginLeft(10, Unit.PX);
		bottomPanel.add(doneButton);
		doneButton.getElement().getStyle().setMarginLeft(550, Unit.PX);
		mainPanel.add(bottomPanel);
	}

	protected void done() {
		hidePopup();
	}

	protected void hidePopup() {
		this.hide();
	}
}
