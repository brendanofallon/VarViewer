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
import com.google.gwt.user.client.ui.VerticalPanel;

public class BCRABLDetailView extends SampleDetailDisplay {

	private SampleInfo currentSample = null;
	private HorizontalPanel labelsWrapper = new HorizontalPanel();
	private FlowPanel labelsPanel = new FlowPanel();
	private HorizontalPanel buttonGrid = new HorizontalPanel();
	private VerticalPanel reportPanel = new VerticalPanel();
	private DisplayVariantsListener sampleViewParent;
	
	public BCRABLDetailView(DisplayVariantsListener sampleViewParent) {
		this.sampleViewParent = sampleViewParent;
		this.setStylePrimaryName("sampledetailspanel");
		
		header = new Label("No data yet");
		header.setStylePrimaryName("sampledetailsheader");
		this.add(header);
		labelsWrapper.add(labelsPanel);
		this.add(labelsWrapper);
		this.add(buttonGrid);
		this.add(reportPanel);
		reportPanel.setStylePrimaryName("bcrabl-reportpanel");

		Button showVarsButton = new Button("View variants", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doShowVariants();
			}
			
		});
		
		labelsWrapper.add(showVarsButton);
		showVarsButton.getElement().getStyle().setMargin(30.0, Unit.PX);
		
	}
	
	protected void doShowVariants() {
		if (currentSample != null) {
			sampleViewParent.showVariantsForSample(currentSample);
		}
	}

	protected void doGenerateReport() {
		if (currentSample == null) {
			return;
		}
		
		HTML waitLabel = new HTML("<h4>Generating report, please wait...</h4>");
		reportPanel.add(waitLabel);
		
		reportService.generateBCRABLReport(currentSample, new AsyncCallback<BCRABLReport>() {

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
		reportPanel.clear();
		
//		if (! result.isPassedQualityCheck()) {
//			reportPanel.add(new HTML("<b> Warning: Sample may be of insufficient quality </b> <br>"));
//			reportPanel.add(new HTML("<b> " + result.getQualityMessage() + "</b>"));
//		}
//		else {
//			reportPanel.add(new Label("Sample passed internal quality assessment."));
//		}	
//		reportPanel.add(new HTML("<hr>"));
		
		reportPanel.add(new Label(result.getMessage()));
		for(String text : result.getReportText()) {
			reportPanel.add(new Label(text));	
		}
		
	}

	
	
	
	@Override
	public void displayDetailsForSample(SampleInfo sampleInfo) {
		this.currentSample = sampleInfo;
		updateInfo();		
		doGenerateReport();
	}
	
	private void updateInfo() {
		if (currentSample == null) {
			header.setText("No data");
			return;
		}
		else {
			header.setText(currentSample.getSampleID());
		}
		
		if (analysisTypePanel == null) {
			//Do null checks..
			String analysisType = "unknown";
			if (currentSample.getAnalysisType() != null)
				analysisType = currentSample.getAnalysisType();
			analysisTypePanel =  new AlignedPanel("Analysis type :", analysisType);
			
			String analysisDate = "unknown";
			if (currentSample.getAnalysisDate() != null) 
				analysisDate = currentSample.getAnalysisDate().toString();
			datePanel =  new AlignedPanel("Analysis date :", analysisDate);
			
			String submitterStr = "unknown";
			if (currentSample.getSubmitter() != null)
				submitterStr = currentSample.getSubmitter();
			submitterPanel = new AlignedPanel("Submitter :", submitterStr);
			
			labelsPanel.add(analysisTypePanel);
			labelsPanel.add(datePanel);
			labelsPanel.add(submitterPanel);
			
		}
		else {
			
			String analysisType = "unknown";
			if (currentSample.getAnalysisType() != null)
				analysisType = currentSample.getAnalysisType();
			analysisTypePanel.setLabelText(analysisType);
			
			String analysisDate = "unknown";
			if (currentSample.getAnalysisDate() != null) 
				analysisDate = currentSample.getAnalysisDate().toString();
			datePanel.setLabelText(analysisDate);
			
			String submitterStr = "unknown";
			if (currentSample.getSubmitter() != null)
				submitterStr = currentSample.getSubmitter();
			submitterPanel.setLabelText(submitterStr);
		}
	}
	
	class AlignedPanel extends HorizontalPanel {
		
		Label labA = new Label("");
		Label labB = new Label("");
		
		public AlignedPanel(String labelA, String labelB) {
			this.setStylePrimaryName("alignpanel");
			
			labA.setStylePrimaryName("alignA");
			labA.setText(labelA);
			this.add(labA);
			labB.setText(labelB);
			
			labB.setStylePrimaryName("alignB");
			this.add(labB);
		}
		
		public void setLabelText(String text) {
			labB.setText(text);
		}
	}
	
	private Label header;
	private AlignedPanel analysisTypePanel = null;
	private AlignedPanel submitterPanel  = null;
	private AlignedPanel datePanel  = null;

	BCRABLReportServiceAsync reportService = (BCRABLReportServiceAsync) GWT.create(BCRABLReportService.class);

}
