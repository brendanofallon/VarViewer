package varviewer.client.sampleView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import varviewer.client.services.SampleListService;
import varviewer.client.services.SampleListServiceAsync;
import varviewer.shared.SampleInfo;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Allows user to pick a sample from those provided by the server (via the SampleListService)
 * These appear in both the main sample-choosing page as well as the pedigree-selecting popup
 * @author brendan
 *
 */
public class SampleChooserList extends FlowPanel {

	private Comparator<SampleInfo> sampleSorter = new SampleDateComparator();
	private SearchBox searchBox;
	private List<SampleInfo> allSamples = null;
	private CellTable<SampleInfo> sampleList;
	private SampleSelectionListener listener = null;
	
	public SampleChooserList(SampleSelectionListener listener) {
		this.listener = listener;
		initComponents();
	}
	
	/**
	 * Set the text used to search the samples list. Any sample with an id, analysis type, or 
	 * submitter that 'contains' this text will be listed. Setting the text to null disables
	 * all filtering.
	 * @param filterText
	 */
	public void setFilterText(String filterText) {
		if (allSamples != null) {
			if (filterText == null) {
				displaySamples(allSamples);
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
			displaySamples(samplesToDisplay);
		}
	}
	
	private void initComponents() {
		searchBox = new SearchBox(this);
		this.add(searchBox);
		SampleCell sampleCell = new SampleCell();
		sampleList = new CellTable<SampleInfo>();
		sampleList.addColumn(new Column<SampleInfo, SampleInfo>(sampleCell) {
			@Override
			public SampleInfo getValue(SampleInfo object) {
				return object;
			}
		});
		sampleList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		final SingleSelectionModel<SampleInfo> selectionModel = new SingleSelectionModel<SampleInfo>();
		sampleList.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new Handler() {

			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				if (listener != null)
					listener.updateSelectedSample(selectionModel.getSelectedObject());
			}
		});
		
		ScrollPanel sampleSP = new ScrollPanel(sampleList);
		sampleSP.setStylePrimaryName("samplescrollpane");	
		this.add(sampleSP);
	}

	/**
	 * Re-load list of samples from the server
	 */
	public void refreshSampleList() {
		  sampleListService.getSampleList(new AsyncCallback<List<SampleInfo>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error retrieving sample list : "+ caught.getMessage());
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<SampleInfo> result) {
				setSampleList(result);
			}
			  
		  });
	}
	
	/**
	 * Cause the list to display the given samples, ensuring that they are sorted 
	 * according to the proper comparator
	 * @param samples
	 */
	private void displaySamples(List<SampleInfo> samples) {
		if (sampleSorter != null) {
			Collections.sort(samples, sampleSorter);
		}
		sampleList.setRowData(samples);
	}
	
	/**
	 * Set the list of all samples that may be displayed in this widget. The currently
	 * displayed samples may be affected by the current filter, if there is one. 
	 * @param allSamples
	 */
	public void setSampleList(List<SampleInfo> allSamples) {
		this.allSamples = new ArrayList<SampleInfo>();
		this.allSamples.addAll(allSamples);
		setFilterText(null); //reset filter, force re-display of all samples
	}
	/**
	 * Renders a single cell in the sample list table. 
	 * @author brendanofallon
	 *
	 */
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
			String idStr = "unknown";
			if (value.getSampleID() != null) 
				idStr = value.getSampleID();
		    sb.appendEscaped(idStr);
		    sb.appendHtmlConstant("</td></tr><tr><td class=\"sampletabletextA\">");
		    String analysisTypeStr = "unknown";
		    if (value.getAnalysisType() != null)
		    	analysisTypeStr = value.getAnalysisType();
		    sb.appendEscaped(analysisTypeStr);
		    
		    sb.appendHtmlConstant("</td></tr></table>");
			
		}
		
	}
	
	static class SampleDateComparator implements Comparator<SampleInfo> {

		@Override
		public int compare(SampleInfo s1, SampleInfo s2) {
			return (int)(s2.getAnalysisDate().getTime() - s1.getAnalysisDate().getTime());
			
		}
		
	}
	
	
	SampleListServiceAsync sampleListService = (SampleListServiceAsync) GWT.create(SampleListService.class);
}
