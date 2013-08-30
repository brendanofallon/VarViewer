package varviewer.client.varTable.pedigree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import varviewer.client.HighlightButton;
import varviewer.shared.PedigreeSample;
import varviewer.shared.PedigreeSample.OperationType;
import varviewer.shared.PedigreeSample.ZygType;
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
		titleLab.getElement().getStyle().setPaddingTop(5, Unit.PX);
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
				return o.getRelSample().getSampleID();
			}
		});
		
		
		
		SelectionCell zygCell = new SelectionCell(Arrays.asList(zygOptions));
		Column<PedigreeSample, String> zygColumn = new Column<PedigreeSample, String>(zygCell) {

			@Override
			public String getValue(PedigreeSample pedSample) {
				return PedigreeSample.getUserString( pedSample.getzType() );
			}
		}; 
		zygColumn.setFieldUpdater(new FieldUpdater<PedigreeSample, String>() {

			@Override
			public void update(int index, PedigreeSample pedSample, String value) {
				pedSample.setzType(PedigreeSample.getZygTypeForString(value));
			}
		});
		table.addColumn(zygColumn);
		
		RemoveCell removeCell = new RemoveCell();
		Column<PedigreeSample, String> remCol = new Column<PedigreeSample, String>(removeCell) {
			@Override
			public String getValue(PedigreeSample object) {
				return object.getRelSample().getSampleID();
			}
		}; 
		remCol.setFieldUpdater(new FieldUpdater<PedigreeSample, String>() {

			@Override
			public void update(int index, PedigreeSample object, String value) {
				System.out.println("Attempting to remove sample with name: " + object.getRelSample());
				removeSample(object);
			}
		});
		table.addColumn(remCol);
		
		table.setEmptyTableWidget(new Label("No samples added"));
		table.getElement().getStyle().setWidth(100, Unit.PCT);
	}
	
	
	public List<PedigreeSample> getSampleSettings() {
		List<PedigreeSample> samples = new ArrayList<PedigreeSample>();
		samples.addAll(data.getList());
		return samples;
	}
	
	protected void showAddSamplePopup() {
		AddSamplePopup popup = new AddSamplePopup(this);
		popup.setPopupPosition(500, 120);
		popup.show();
	}



	protected void removeSample(PedigreeSample pedSample) {
		data.getList().remove(pedSample);
	}


	public void addSample(SampleInfo sample) {
		PedigreeSample pedSample = new PedigreeSample();
		pedSample.setRelSample(sample);
		pedSample.setzType(defaultZygType);
		pedSample.setoType(defaultOp);
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
		this.addSample(sample);
	}
	
	
	static class RemoveCell extends ButtonCell {
		
		public void render(Cell.Context context, String value, SafeHtmlBuilder sb) {
			sb.appendHtmlConstant("<img src=\"images/removeIcon16.png\"/>");
		}
	}






}
