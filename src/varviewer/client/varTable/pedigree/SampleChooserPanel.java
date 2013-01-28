package varviewer.client.varTable.pedigree;

import java.util.Arrays;

import varviewer.client.HighlightButton;
import varviewer.client.IGVInterface;
import varviewer.client.varTable.pedigree.PedigreeSample.OperationType;
import varviewer.client.varTable.pedigree.PedigreeSample.ZygType;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.FlowPanel;
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
	String[] zygOptions = new String[]{"All", "Hets", "Homs."};
	
	public SampleChooserPanel(String title) {
		data.addDataDisplay(table);
		Label titleLab = new Label(title);
		this.setStylePrimaryName("pedsamplechooser");
		this.add(titleLab);
		titleLab.setStylePrimaryName("pedsamplechoosertitle");
		ScrollPanel tableSP = new ScrollPanel(table);
		this.add(tableSP);
		tableSP.setHeight("150px");
		Image addImage = new Image("images/addSampleIcon.png");
		HighlightButton addSampleButton = new HighlightButton( addImage );
		addSampleButton.getElement().getStyle().setMarginLeft(100, Unit.PX);
		addSampleButton.setWidth("34px");
		addSampleButton.setHeight("34px");
		this.add(addSampleButton);
		
		table.addColumn(new TextColumn<PedigreeSample>() {

			@Override
			public String getValue(PedigreeSample o) {
				return o.relId;
			}
		});
		
		table.addColumn(new TextColumn<PedigreeSample>() {

			@Override
			public String getValue(PedigreeSample o) {
				return "some col";
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



	
	
	static class RemoveCell extends ButtonCell {
		
		public void render(Cell.Context context, String value, SafeHtmlBuilder sb) {
			sb.appendHtmlConstant("<img src=\"images/removeIcon16.png\"/>");
		}
	}
}
