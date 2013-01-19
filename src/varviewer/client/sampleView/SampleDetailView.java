package varviewer.client.sampleView;

import varviewer.shared.SampleInfo;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class SampleDetailView extends FlowPanel {

	private SampleInfo currentSample = null;
	
	public SampleDetailView() {
		header = new Label("No data yet");
		this.setStylePrimaryName("sampledetailspanel");
		this.add(header);
		header.setStylePrimaryName("sampledetailsheader");
	}
	
	public void setSample(SampleInfo info) {
		this.currentSample = info;
		updateInfo();
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
			
			this.add(analysisTypePanel);
			this.add(datePanel);
			this.add(submitterPanel);
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
	
	
}
