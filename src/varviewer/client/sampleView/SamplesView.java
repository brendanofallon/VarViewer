package varviewer.client.sampleView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import varviewer.shared.SampleInfo;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Displays a list of samples in fancy graphical format
 * @author brendan
 *
 */
public class SamplesView extends HorizontalPanel {

	public SamplesView() {
		initComponents();
	}

	
	
	private void initComponents() {
			
		SampleCell sampleCell = new SampleCell();
		sampleList = new CellList<SampleInfo>( sampleCell );
		
		//practice samples...
		List<SampleInfo> samples = new ArrayList<SampleInfo>();
		
		samples.add( new SampleInfo("somesample", "some analysis", new Date(), "brendan"));
		samples.add( new SampleInfo("somesampleA", "some analysis1", new Date(), "brendan"));
		samples.add( new SampleInfo("somesampleB", "some analysis2", new Date(), "brendan"));
		samples.add( new SampleInfo("somesampleC", "some analysis3", new Date(), "brendan"));
		samples.add( new SampleInfo("somesampleD", "some analysis4", new Date(), "brendan"));
		samples.add( new SampleInfo("somesampleE", "some analysis5", new Date(), "brendan"));
		
		
		sampleList.setRowData(samples);
		sampleList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		final SingleSelectionModel<SampleInfo> selectionModel = new SingleSelectionModel<SampleInfo>();
		sampleList.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new Handler() {

			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				updateSelectedSample(selectionModel.getSelectedObject());
			}
		});
		
		ScrollPanel sampleSP = new ScrollPanel(sampleList);
		sampleSP.setWidth("300px");
		sampleSP.setHeight("500px");
		sampleSP.setStylePrimaryName("samplescrollpane");
		this.add(sampleSP);
		
		
		
		//Create sample details panel...
		sampleDetailsPanel = new SampleDetailView();
		this.add(sampleDetailsPanel);
	}


	/**
	 * Called when a new sample has been selected in the list. We 
	 * update the details panel to show the new sample here. 
	 * @param selectedSample
	 */
	protected void updateSelectedSample(SampleInfo selectedSample) {
		sampleDetailsPanel.setSample(selectedSample);
	}





	static class SampleCell extends AbstractCell<SampleInfo> {

		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				SampleInfo value, SafeHtmlBuilder sb) {
	
			if (value == null)
				return;
			
			sb.appendEscaped(value.getSampleID() + " : " + value.getAnalysisType());
		}
		
	}
	
	private SampleDetailView sampleDetailsPanel;
	private CellList<SampleInfo> sampleList;
	
}
