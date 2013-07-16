package varviewer.client.varTable.pedigree;

import java.util.ArrayList;
import java.util.List;

import varviewer.client.varTable.VariantDisplay;
import varviewer.shared.PedigreeSample;
import varviewer.shared.PedigreeSample.OperationType;
import varviewer.shared.varFilters.PedigreeFilter;

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

	
	private FlowPanel mainPanel = new FlowPanel();
	private HorizontalPanel centerPanel = new HorizontalPanel();
	private HorizontalPanel bottomPanel = new HorizontalPanel();
	private SampleChooserPanel includesPanel = new SampleChooserPanel("Intersect affected cases");
	private SampleChooserPanel excludesPanel = new SampleChooserPanel("Subtract healthy controls");
	private VariantDisplay varDisplay;
	 
	public PedigreePopup(VariantDisplay display) {
		super(false);
		this.varDisplay = display;
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
		hidePopup();
		
		List<PedigreeFilter> filters = new ArrayList<PedigreeFilter>();
		for(PedigreeSample sample : includesPanel.getSampleSettings()) {
			filters.add(new PedigreeFilter(sample));
		}
		for(PedigreeSample sample : excludesPanel.getSampleSettings()) {
			filters.add(new PedigreeFilter(sample));
		}

		//Two things need to happen here. First, we need to add the pedigreeFilters to the list of
		//filters used.

		varDisplay.getVarListManager().setPedigreeFilters(filters);

		//Second, we need to add columns to the current column model so that the zygosities of 
		//the filtered samples are displayed in the table
		//Clear existing PedigreeVarAnnotations from column model, then add the new ones
		varDisplay.getVarTable().getColumnModel().removeColumnsByClass(PedigreeVarAnnotation.class);
		for(PedigreeSample sample : includesPanel.getSampleSettings()) {
			PedigreeVarAnnotation pedAnnotation = new PedigreeVarAnnotation(sample);
			varDisplay.getVarTable().getColumnModel().addColumn(pedAnnotation);	
		}
		for(PedigreeSample sample : excludesPanel.getSampleSettings()) {
			PedigreeVarAnnotation pedAnnotation = new PedigreeVarAnnotation(sample);
			varDisplay.getVarTable().getColumnModel().addColumn(pedAnnotation);
		}
		
		varDisplay.getVarListManager().reloadIfRequired();
		
	}

	protected void hidePopup() {
		this.hide();
	}
	
	
}
