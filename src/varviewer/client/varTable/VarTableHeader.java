package varviewer.client.varTable;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Header of a VarTable with some controls, right now just a pager, export button, and 
 * column config button
 * @author brendan
 *
 */
public class VarTableHeader extends HorizontalPanel {

	SimplePager pager = new SimplePager();
	Label sampleLabel = new Label("Unknown sample");
	Button exportButton = new Button("Export");
	
	
	public VarTableHeader(final VarTable tableParent) {
		this.setStylePrimaryName("vartableheader");
		
		this.setHorizontalAlignment(ALIGN_LEFT);
		this.add(sampleLabel);
		sampleLabel.setStylePrimaryName("samplelabel");
		
		this.setHorizontalAlignment(ALIGN_RIGHT);
		
		exportButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				tableParent.exportData();
			}
		});
		this.add(exportButton);
		exportButton.setStylePrimaryName("exportbutton");
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
