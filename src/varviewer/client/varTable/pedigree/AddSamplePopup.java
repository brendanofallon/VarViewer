package varviewer.client.varTable.pedigree;

import varviewer.client.sampleView.SampleChooserList;
import varviewer.client.sampleView.SampleSelectionListener;
import varviewer.shared.SampleInfo;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Popup that appears when user wants to add a new sample to the pedigree include / exclude chart
 * @author brendan
 *
 */
public class AddSamplePopup extends PopupPanel implements SampleSelectionListener {

	private SampleInfo selectedSample = null;
	private SampleChooserPanel chooserPanel;
	
	public AddSamplePopup(SampleChooserPanel chooserPanel) {
		super(false);
		this.chooserPanel = chooserPanel;
		initComponents();
		sampleChooser.refreshSampleList();
	}

	private void initComponents() {
		this.setStylePrimaryName("genericpopup");
		mainPanel = new FlowPanel();
		this.add(mainPanel);
		Label topLabel = new Label("Select a sample to add");
		topLabel.setStylePrimaryName("textlabel12");
		topLabel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		topLabel.getElement().getStyle().setMarginBottom(5, Unit.PX);
		
		mainPanel.add(topLabel);
		mainPanel.add(new HTML("<hr />"));
		
		sampleChooser = new SampleChooserList(this);
		sampleChooser.getScrollPanel().setHeight("300px");
	
		mainPanel.add(sampleChooser);
		mainPanel.add(new HTML("<hr />"));
		
		HorizontalPanel bottomPanel = new HorizontalPanel();
		mainPanel.add(bottomPanel);
		Button cancelButton = new Button("Cancel");
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				cancel();
			}
			
		});
		bottomPanel.add(cancelButton);
	
		addSampleButton = new Button("Add sample");
		addSampleButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				addSample();
			}
		});
		addSampleButton.setEnabled(false);
		bottomPanel.add(addSampleButton);
		bottomPanel.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_LEFT);
		bottomPanel.setCellHorizontalAlignment(addSampleButton, HasHorizontalAlignment.ALIGN_RIGHT);
		cancelButton.getElement().getStyle().setMarginLeft(10.0, Unit.PX);
		addSampleButton.getElement().getStyle().setMarginLeft(120.0, Unit.PX);
	}

	protected void addSample() {
		if (selectedSample != null)
			chooserPanel.addSampleInfo(selectedSample);
		this.hide();
	}

	protected void cancel() {
		this.hide();
	}

	@Override
	public void updateSelectedSample(SampleInfo selectedInfo) {
		selectedSample = selectedInfo;
		addSampleButton.setEnabled(selectedSample != null);
	}
	
	Button addSampleButton;
	private FlowPanel mainPanel;
	private SampleChooserList sampleChooser;
}
