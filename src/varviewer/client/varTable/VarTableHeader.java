package varviewer.client.varTable;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;

/**
 * Header of a VarTable with some controls, right now just a pager, export button, and 
 * column config button
 * @author brendan
 *
 */
public class VarTableHeader extends HorizontalPanel {

	SimplePager pager = new SimplePager();
	Label sampleLabel = new Label("Unknown sample");
	final PushButton exportButton; 
	PushButton colMenuButton;
	//MenuBar colMenu = new MenuBar();
	
	public VarTableHeader(final VarTable tableParent) {
		this.setStylePrimaryName("vartableheader");
		this.add(sampleLabel);
		sampleLabel.setStylePrimaryName("samplelabel");
		
		Image exportImage = new Image("images/export-icon.png");
		exportButton = new PushButton(exportImage, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				tableParent.exportData();
			}
			
		});
		
		this.add(exportButton);
		exportButton.setStylePrimaryName("exportbutton");
		exportButton.addMouseOverHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				exportButton.setStylePrimaryName("exportbutton-hover");
			}
			
		});
		exportButton.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				exportButton.setStylePrimaryName("exportbutton");
				
			}
			
		});
		
		
		final ColPickerPopup popup = new ColPickerPopup(tableParent.colModel);
		popup.hide();
		
		Image colMenuImage = new Image("images/config-icon.png");
		colMenuButton = new PushButton(colMenuImage, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				popup.showRelativeTo(colMenuButton);
			}
			
		});
		colMenuButton.setStylePrimaryName("exportbutton");
		this.add(colMenuButton);
		
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
