package varviewer.client.varTable;


import varviewer.client.HighlightButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Header of a VarTable with some controls, right now just a pager, export button, and 
 * column config button.
 * @author brendan
 *
 */
public class VarTableHeader extends HorizontalPanel {

	SimplePager pager = new SimplePager();
	Label sampleLabel = new Label("Unknown sample");
	final HighlightButton exportButton; 
	final HighlightButton colMenuButton;
	
	public VarTableHeader(final VarTable tableParent) {
		this.setStylePrimaryName("vartableheader");
		this.add(sampleLabel);
		sampleLabel.setStylePrimaryName("samplelabel");
		
		Image exportImage = new Image("images/export-icon.png");
		exportButton = new HighlightButton(exportImage, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				tableParent.exportData();
			}
			
		});
		
		this.add(exportButton);
		exportButton.setWidth("24px");
		exportButton.setHeight("24px");
		
		
		final ColPickerPopup popup = new ColPickerPopup(tableParent.colModel);
		popup.hide();
		
		Image colMenuImage = new Image("images/config-icon.png");
		colMenuButton = new HighlightButton(colMenuImage, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				popup.showRelativeTo(colMenuButton);
			}
			
		});

		this.add(colMenuButton);
		colMenuButton.setWidth("24px");
		colMenuButton.setHeight("24px");
		
		this.add(pager);
		
		pager.setDisplay(tableParent.getVarPage());
	}

	/**
	 * Set the text of the sample label at the top-left of the header
	 * @param sample
	 */
	public void setSampleName(String sample) {
		this.sampleLabel.setText(sample);
	}
	
}
