package varviewer.client.varTable.pedigree;

import java.util.List;

import varviewer.shared.PedigreeSample;
import varviewer.shared.PedigreeSample.OperationType;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

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
		setWidth("700px");
		setHeight("350px");
	}

	private void initComponents() {
		this.add(mainPanel);
		this.setStylePrimaryName("genericpopup");
		Label header = new Label("Choose additional samples to intersect and subtract");
		header.setStylePrimaryName("pedpopuptitle");
		mainPanel.add(header);
		
		centerPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		FlowPanel includesContainer = new FlowPanel();
		FlowPanel excludesContainer = new FlowPanel();
		includesContainer.add(includesPanel);
		Label includeLabel = new Label("Show only variants present in ALL of these samples at the given zygosity");
		includesContainer.add(includeLabel);
		excludesContainer.add(excludesPanel);
		Label excludesLabel = new Label("Remove all variants present at the given zygosity in ANY of these samples");
		includeLabel.setStylePrimaryName("interiortext");
		excludesLabel.setStylePrimaryName("interiortext");
		includeLabel.getElement().getStyle().setTextAlign(Style.TextAlign.LEFT);
		excludesLabel.getElement().getStyle().setTextAlign(Style.TextAlign.LEFT);
		includeLabel.getElement().getStyle().setMarginLeft(10, Unit.PX);
		includeLabel.getElement().getStyle().setMarginRight(10, Unit.PX);
		excludesLabel.getElement().getStyle().setMarginRight(10, Unit.PX);
		excludesLabel.getElement().getStyle().setMarginLeft(10, Unit.PX);
		excludesContainer.add(excludesLabel);
		centerPanel.add(includesContainer);
		centerPanel.add(excludesContainer);
		
		includesPanel.setDefaultOp(PedigreeSample.OperationType.INTERSECT);
		
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
		List<PedigreeSample> includeSamples = includesPanel.getSampleSettings();
		List<PedigreeSample> excludeSamples = excludesPanel.getSampleSettings();
		hidePopup();
	}

	protected void hidePopup() {
		this.hide();
	}
}
