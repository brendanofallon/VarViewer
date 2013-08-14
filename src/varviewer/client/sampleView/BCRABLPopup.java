package varviewer.client.sampleView;

import varviewer.client.bcrablReporter.BCRABLReportService;
import varviewer.client.bcrablReporter.BCRABLReportServiceAsync;
import varviewer.shared.SampleInfo;
import varviewer.shared.bcrabl.BCRABLReport;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BCRABLPopup extends PopupPanel {
	
	private FlowPanel mainPanel = new FlowPanel();
	private VerticalPanel centerPanel = new VerticalPanel();
	private HorizontalPanel bottomPanel = new HorizontalPanel();

	 
	public BCRABLPopup() {
		super(false);
		super.setPopupPosition(100, 100);
		
		initComponents();
	}

	public void refreshResults(SampleInfo info) {
		
		
		centerPanel.clear(); //Clear previous results, otherwise may be confusing...
		HTML waitLabel = new HTML("<h4>Generating report, please wait...</h4>");
		centerPanel.add(waitLabel);
		
		reportService.generateBCRABLReport(info, new AsyncCallback<BCRABLReport>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error generating report: " + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(BCRABLReport result) {
				showReport(result);
			}
			
		});
	}
	
	protected void showReport(BCRABLReport result) {
		centerPanel.clear();
		centerPanel.add(new Label(result.getMessage()));
		for(String text : result.getReportText()) {
			centerPanel.add(new Label(text));	
		}
		
	}


	
	private void initComponents() {
		this.add(mainPanel);
		this.setStylePrimaryName("genericpopup");
		Label header = new Label("BCR-ABL Mutation Report");
		header.setStylePrimaryName("pedpopuptitle");
		mainPanel.add(header);
		
		mainPanel.add(centerPanel);
		centerPanel.setStylePrimaryName("bcrabl-centerpanel");
			

		Button doneButton = new Button("Done");
		doneButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				done();
			}
		});
		bottomPanel.getElement().getStyle().setMarginTop(10, Unit.PX);
		bottomPanel.add(doneButton);
		doneButton.setStylePrimaryName("centered");
		mainPanel.add(bottomPanel);
	}

	protected void done() {
		hidePopup();
	}

	protected void hidePopup() {
		this.hide();
	}
	
	BCRABLReportServiceAsync reportService = (BCRABLReportServiceAsync) GWT.create(BCRABLReportService.class);



}
