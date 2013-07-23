package varviewer.client.varTable;


import varviewer.client.HighlightButton;
import varviewer.client.services.TextFetchService;
import varviewer.client.services.TextFetchServiceAsync;
import varviewer.client.varTable.cisTrans.CisTransPopup;
import varviewer.client.varTable.pedigree.PedigreePopup;
import varviewer.shared.TextFetchResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Header of a VarTable with some controls, right now just a pager, and a few buttons.
 * @author brendan
 *
 */
public class VarTableHeader extends HorizontalPanel {

	SimplePager pager = new SimplePager();
	Label sampleLabel = new Label("Unknown sample");
	final HighlightButton pedigreeButton; 
	final HighlightButton igvButton; 
	final HighlightButton exportButton; 
	final HighlightButton incidentalsButton;
	final HighlightButton colMenuButton;
	final HighlightButton cisTransButton;
	final HeaderSearchBox searchBox;
	final PedigreePopup pedPopup;
	final CisTransPopup cisTransPopup;
	final ColPickerPopup popup;
	
	private String igvLinkText = null;
	private VarTable tableParent;
	
	public VarTableHeader(final VarTable tableParent, 
			ColumnModel colModel, 
			PedigreePopup pedPopup, 
			CisTransPopup cisTransPopup, 
			ColPickerPopup colPopup) {
		this.tableParent = tableParent;
		this.setStylePrimaryName("vartableheader");
		this.add(sampleLabel);
		sampleLabel.setStylePrimaryName("samplelabel");
		this.pedPopup = pedPopup;
		this.cisTransPopup = cisTransPopup;
		this.popup = colPopup;
		searchBox = new HeaderSearchBox(tableParent);
		this.add(searchBox);
		
		Image pedImage = new Image("images/pedigreeIcon.png");
		pedigreeButton = new HighlightButton(pedImage, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showPedigreePanel();
			}
			
		});
		pedigreeButton.setTitle("Pedigree based filtering");
		this.add(pedigreeButton);
		pedigreeButton.setWidth("24px");
		pedigreeButton.setHeight("24px");
		
		
		Image igvImage = new Image("images/igvIcon.png");
		igvButton = new HighlightButton(igvImage, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				attemptLoadIGV();
			}
		});
		igvButton.setTitle("Load alignment information in IGV");
		this.add(igvButton);
		
		igvButton.setWidth("24px");
		igvButton.setHeight("24px");
		
		Image exportImage = new Image("images/export-icon.png");
		exportButton = new HighlightButton(exportImage, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				tableParent.exportData();
			}
			
		});
		exportButton.setTitle("Download variants in Excel / csv");
		this.add(exportButton);
		exportButton.setWidth("24px");
		exportButton.setHeight("24px");
		
		
		Image incidentalImage = new Image("images/incidentalHitsImage.png");
		incidentalsButton = new HighlightButton(incidentalImage, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doIncidentalsFilter();
			}
			
		});
		incidentalsButton.setTitle("Show hits in incidental genes only");
		this.add(incidentalsButton);
		incidentalsButton.setWidth("24px");
		incidentalsButton.setHeight("24px");
		
		final ColPickerPopup popup = new ColPickerPopup(colModel);
		popup.hide();
		
		Image colMenuImage = new Image("images/config-icon.png");
		colMenuButton = new HighlightButton(colMenuImage, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				popup.showRelativeTo(colMenuButton);
			}
			
		});

		colMenuButton.setTitle("Chose columns to display");
		this.add(colMenuButton);
		colMenuButton.setWidth("24px");
		colMenuButton.setHeight("24px");
		
		
		Image cisTransImage = new Image("images/pieChart24.png");
		cisTransButton = new HighlightButton(cisTransImage, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doCisTransCompute();
			}
			
		});
		cisTransButton.setTitle("Perform cis / trans inference on selected variants");
		this.add(cisTransButton);
		cisTransButton.setWidth("24px");
		cisTransButton.setHeight("24px");
		
		
		this.add(pager);
		
		
		this.setCellHorizontalAlignment(sampleLabel, ALIGN_LEFT);
		this.setCellHorizontalAlignment(searchBox, ALIGN_CENTER);
		this.setCellHorizontalAlignment(pager, ALIGN_RIGHT);
		
		pager.setDisplay(tableParent.getVarPage());
	}

	protected void doCisTransCompute() {
		int selectedVariantCount = tableParent.getSelectedVariantCount();
		if (selectedVariantCount != 2) {
			Window.alert("Please select two variants before using this feature");
		}
		else {
			cisTransPopup.setPopupPosition(200, 100);
			cisTransPopup.show();
		}
	}

	protected void doIncidentalsFilter() {
		//Fetch list of incidentals genes using a textFetchService
		textFetchService.fetchText("incidentalsGenes.csv", new AsyncCallback<TextFetchResult>() {

			@Override
			public void onFailure(Throwable caught) {
				System.out.println("Aargh, failure!");
			}

			@Override
			public void onSuccess(TextFetchResult result) {
				StringBuilder strB = new StringBuilder();
				for(String line : result.getLinesOfText()) {
					String gene = line.trim().toUpperCase();
					if (gene.length()>0)
						strB.append(gene + ", ");
				}
				searchBox.setText(strB.toString());
				searchBox.handleTextChange();
			}
			
		});
	}

	/**
	 * Called when user clicks the 'pedigree button', which brings up a popup allowing the
	 * user to do a pedigree analysis
	 */
	protected void showPedigreePanel() {
		pedPopup.setPopupPosition(200, 100);
		pedPopup.show();
	}

	protected void attemptLoadIGV() {
		if (igvLinkText != null) {
			Window.open(igvLinkText, "_self", "");
		}
		else {
			Window.alert("No BAM information link found, cannot load IGV");
		}
	}

	/**
	 * Set the text of the sample label at the top-left of the header
	 * @param sample
	 */
	public void setSampleName(String sample) {
		this.sampleLabel.setText(sample);
	}
	
	public String getSampleLabel() {
		return this.sampleLabel.getText();
	}

	/**
	 * To use IGV we must have the link to the bam file, which is displayed 
	 * in a popup from this header
	 * @param bamLink
	 */
	public void setBAMLink(String bamLink) {
		igvLinkText = bamLink;
	}
	
	
	private final TextFetchServiceAsync textFetchService = GWT.create(TextFetchService.class);
}
