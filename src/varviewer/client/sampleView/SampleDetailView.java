package varviewer.client.sampleView;

import varviewer.client.VarListManager;
import varviewer.shared.SampleInfo;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SampleDetailView extends FlowPanel {

	private SampleInfo currentSample = null;
	private FlowPanel labelsPanel = new FlowPanel();
	private Grid buttonGrid = new Grid(2,2);
	private SamplesView sampleViewParent;
	
	public SampleDetailView(SamplesView sampleViewParent) {
		this.sampleViewParent = sampleViewParent;
		this.setStylePrimaryName("sampledetailspanel");
		header = new Label("No data yet");
		header.setStylePrimaryName("sampledetailsheader");
		this.add(header);
		this.add(labelsPanel);
		

		Image varsImage = new Image("images/mimeIcon3-64.png");
		buttonGrid.setWidget(0, 0, makeButtonPanel("Show Variants", varsImage, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doShowVariants();
			}
		}));
		
		buttonGrid.setStylePrimaryName("buttongrid");
		Image qcImage = new Image("images/qcIcon64.png");
		buttonGrid.setWidget(0, 1, makeButtonPanel("Quality Metrics", qcImage, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doQualityMetrics();
			}
		}));
		
		Image bamImage = new Image("images/bam2-64.png");
		buttonGrid.setWidget(1, 0, makeButtonPanel("Download BAM file", bamImage, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doDownloadBAM();
			}
		}));
		
		Image vcfImage = new Image("images/vcfIcon64.png");
		buttonGrid.setWidget(1, 1, makeButtonPanel("Download VCF file", vcfImage, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doDownloadVCF();
			}
		}));
		

		
	}
	
	protected void doShowVariants() {
		if (currentSample != null) {
			VarListManager.getManager().setSample( currentSample.getSampleID() );
			VarListManager.getManager().reloadIfRequired();
			sampleViewParent.getVarViewer().showVariantDisplay();
		}
	}

	protected void doQualityMetrics() {
		// TODO Auto-generated method stub
		
	}

	protected void doDownloadVCF() {
		
	}

	protected void doDownloadBAM() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Makes a 'bigbutton' and returns it as a widget
	 * @param label
	 * @param image
	 * @param handler
	 * @return
	 */
	private Widget makeButtonPanel(String label, Image image, ClickHandler handler) {
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
		
		if (handler != null) 
			wrapper.addClickHandler(handler);
		wrapper.add(panel);
		return wrapper;
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
			
			labelsPanel.add(analysisTypePanel);
			labelsPanel.add(datePanel);
			labelsPanel.add(submitterPanel);
			this.add(buttonGrid);
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
