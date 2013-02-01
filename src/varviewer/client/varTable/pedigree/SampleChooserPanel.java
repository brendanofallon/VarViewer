package varviewer.client.varTable.pedigree;

import java.util.Arrays;

import varviewer.client.HighlightButton;
import varviewer.client.varTable.pedigree.PedigreeSample.OperationType;
import varviewer.client.varTable.pedigree.PedigreeSample.ZygType;
import varviewer.shared.SampleInfo;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;

/**
 * The panel in a PedigreePopup that allows users to add, remove, and select
 * 'samples' to intersect or subtract from a proband
 * @author brendanofallon
 *
 */
public class SampleChooserPanel extends FlowPanel {
 
	CellTable<PedigreeSample> table = new CellTable<PedigreeSample>();
	ListDataProvider<PedigreeSample> data = new ListDataProvider<PedigreeSample>();
	OperationType defaultOp = OperationType.NONE;
	ZygType defaultZygType = ZygType.HETS;
	String[] zygOptions = new String[]{"All", "Hets", "Homs"};
	
	public SampleChooserPanel(String title) {
		data.addDataDisplay(table);
		HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.add(titlePanel);
		Label titleLab = new Label(title);
		this.setStylePrimaryName("pedsamplechooser");
		titlePanel.add(titleLab);
		titlePanel.setStylePrimaryName("pedsamplechoosertitle");
		ScrollPanel tableSP = new ScrollPanel(table);
		this.add(tableSP);
		tableSP.setHeight("150px");
		Image addImage = new Image("images/addSampleIcon2.png");
		HighlightButton addSampleButton = new HighlightButton( addImage );
		addSampleButton.setTitle("Add a new sample");
		addSampleButton.getElement().getStyle().setMarginLeft(50, Unit.PX);
		addSampleButton.getElement().getStyle().setVerticalAlign(VerticalAlign.BOTTOM);
		addSampleButton.setWidth("24px");
		addSampleButton.setHeight("24px");
		addSampleButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showAddSamplePopup();
			}
		});
		titlePanel.add(addSampleButton);
		titlePanel.setCellHorizontalAlignment(addSampleButton, HasHorizontalAlignment.ALIGN_RIGHT);
		table.addColumn(new TextColumn<PedigreeSample>() {

			@Override
			public String getValue(PedigreeSample o) {
				return o.relId;
			}
		});
		
		
		
		SelectionCell zygCell = new SelectionCell(Arrays.asList(zygOptions));
		Column<PedigreeSample, String> zygColumn = new Column<PedigreeSample, String>(zygCell) {

			@Override
			public String getValue(PedigreeSample pedSample) {
				return PedigreeSample.getUserString( pedSample.zType );
			}
		}; 
		zygColumn.setFieldUpdater(new FieldUpdater<PedigreeSample, String>() {

			@Override
			public void update(int index, PedigreeSample pedSample, String value) {
				pedSample.zType = PedigreeSample.getZygTypeForString(value);
			}
		});
		table.addColumn(zygColumn);
		
		RemoveCell removeCell = new RemoveCell();
		Column<PedigreeSample, String> remCol = new Column<PedigreeSample, String>(removeCell) {
			@Override
			public String getValue(PedigreeSample object) {
				return object.relId;
			}
		}; 
		remCol.setFieldUpdater(new FieldUpdater<PedigreeSample, String>() {

			@Override
			public void update(int index, PedigreeSample object, String value) {
				System.out.println("Attempting to remove sample with name: " + object.relId);
				removeSample(object);
			}
		});
		table.addColumn(remCol);
		
		addSample("Some sample #1");
		addSample("Some sample #2");
		addSample("Some sample #3");
	}
	
	
	
	protected void showAddSamplePopup() {
		AddSamplePopup popup = new AddSamplePopup(this);
		popup.setPopupPosition(500, 120);
		popup.show();
	}



	protected void removeSample(PedigreeSample pedSample) {
		data.getList().remove(pedSample);
	}


	public void addSample(String sampleId) {
		PedigreeSample pedSample = new PedigreeSample();
		pedSample.relId = sampleId;
		pedSample.zType = defaultZygType;
		pedSample.oType = defaultOp;
		data.getList().add(pedSample);
	}

	public void setDefaultOp(OperationType defaultOp) {
		this.defaultOp = defaultOp;
	}


	/**
	 * Adds the given sampleInfo to the list of samples displayed 
	 * @param sample
	 */
	public void addSampleInfo(SampleInfo sample) {
		this.addSample(sample.getSampleID());
	}
	
	
	static class RemoveCell extends ButtonCell {
		
		public void render(Cell.Context context, String value, SafeHtmlBuilder sb) {
			sb.appendHtmlConstant("<img src=\"images/removeIcon16.png\"/>");
		}
	}






}
