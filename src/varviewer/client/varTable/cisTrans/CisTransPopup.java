package varviewer.client.varTable.cisTrans;

import varviewer.client.services.CisTransService;
import varviewer.client.services.CisTransServiceAsync;
import varviewer.client.varTable.VariantDisplay;
import varviewer.shared.bcrabl.CisTransRequest;
import varviewer.shared.bcrabl.CisTransResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class CisTransPopup extends PopupPanel {

	private FlowPanel mainPanel = new FlowPanel();
	private HorizontalPanel centerPanel = new HorizontalPanel();
	private HorizontalPanel bottomPanel = new HorizontalPanel();
	private VariantDisplay varDisplay;
	 
	public CisTransPopup(VariantDisplay display) {
		super(false);
		this.varDisplay = display;
		initComponents();
		setWidth("700px");
		setHeight("350px");
	}

	public void refreshResults() {
		CisTransRequest req = new CisTransRequest();
		
		cisTransService.computeCisTrans(req, new AsyncCallback<CisTransResult>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failure! " + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(CisTransResult result) {
				mainPanel.add(new Label(result.getMessage()));
			}
			
		});
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
	
	CisTransServiceAsync cisTransService = (CisTransServiceAsync) GWT.create(CisTransService.class);

}
