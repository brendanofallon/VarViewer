package varviewer.client.sampleView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import varviewer.shared.SampleInfo;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Displays a list of samples in fancy graphical format. 
 * @author brendan
 *
 */
public class SamplesView extends HorizontalPanel {

	private String filterText = null;
	
	public SamplesView() {
		initComponents();
		
		//practice samples...
//		List<SampleInfo> samples = new ArrayList<SampleInfo>();
//
//		samples.add( new SampleInfo("somesample", "some analysis", new Date(), "brendan"));
//		samples.add( new SampleInfo("somesampleA", "some analysis1", new Date(), "brendan"));
//		samples.add( new SampleInfo("blahablah", "some analysis2", new Date(), "brendan"));
//		samples.add( new SampleInfo("somesampleC", "bogus", new Date(), "brendan"));
//		samples.add( new SampleInfo("crazy", "some analysis4", new Date(), "brendan"));
//		samples.add( new SampleInfo("somesampleE", "text142", new Date(), "brendan"));
//		setSampleList(samples);		
				
	}
	
	/**
	 * Set the text used to search the samples list. Any sample with an id, analysis type, or 
	 * submitter that 'contains' this text will be listed. Setting the text to null disables
	 * all filtering.
	 * @param filterText
	 */
	public void setFilterText(String filterText) {
		this.filterText = filterText;
		
		if (allSamples != null) {
			if (filterText == null) {
				sampleList.setRowData(allSamples);
				return;
			}
			
			List<SampleInfo> samplesToDisplay = new ArrayList<SampleInfo>();
			for(SampleInfo info : allSamples) {
				if (info.getSampleID().contains(filterText)
						|| info.getAnalysisType().contains(filterText)
						|| info.getSubmitter().contains(filterText)) {
					samplesToDisplay.add(info);
				}
			}
			sampleList.setRowData(samplesToDisplay);
		}
		
	}
	
	/**
	 * Set the list of all samples that may be displayed in this widget. The currently
	 * displayed samples may be affected by the current filter, if there is one. 
	 * @param allSamples
	 */
	public void setSampleList(List<SampleInfo> allSamples) {
		this.allSamples = new ArrayList<SampleInfo>();
		this.allSamples.addAll(allSamples);
		sampleList.setRowData(allSamples);
	}
	
	private void initComponents() {
		this.setStylePrimaryName("samplesview");
		leftPanel = new FlowPanel();
		searchBox = new SearchBox(this);
		leftPanel.add(searchBox);
		
		sampleImage = new Image("images/sampleIcon48.png");
		
		SampleCell sampleCell = new SampleCell();
		sampleList = new CellList<SampleInfo>( sampleCell );
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
		sampleSP.setWidth("200px");
		sampleSP.setHeight("500px");
		sampleSP.setStylePrimaryName("samplescrollpane");
		
		leftPanel.add(sampleSP);
		this.add(leftPanel);
		
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
			
			sb.appendHtmlConstant("<table class=\"sampletableitem\">");
			sb.appendHtmlConstant("<tr><td rowspan='3'>");
			sb.appendHtmlConstant("<img src=\"images/sampleIcon48.png\" />");
		    sb.appendHtmlConstant("</td>");
			sb.appendHtmlConstant("<td class=\"sampletabletext\">");
		    sb.appendEscaped(value.getSampleID());
		    sb.appendHtmlConstant("</td></tr><tr><td class=\"sampletabletextA\">");
		    sb.appendEscaped(value.getAnalysisType());
		    sb.appendHtmlConstant("</td></tr></table>");
			
		}
		
	}
	
	private List<SampleInfo> allSamples = null;
	private SearchBox searchBox;
	static private Image sampleImage;
	private FlowPanel leftPanel;
	private SampleDetailView sampleDetailsPanel;
	private CellList<SampleInfo> sampleList;
	
}
