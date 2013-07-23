package varviewer.client.sampleView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import varviewer.client.services.SampleListService;
import varviewer.client.services.SampleListServiceAsync;
import varviewer.shared.SampleInfo;
import varviewer.shared.SampleListResult;
import varviewer.shared.SampleTreeNode;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

/**
 * Allows user to pick a sample from those provided by the server (via the SampleListService)
 * These appear in both the main sample-choosing page as well as the pedigree-selecting popup
 * @author brendan
 *
 */
public class SampleChooserList extends FlowPanel {

	private Comparator<SampleInfo> sampleSorter = new SampleDateComparator();
	//private SearchBox searchBox;
	private List<SampleInfo> allSamples = null;
	final private SampleTreeNode rootNode = new SampleTreeNode();
	private SampleViewModel model = null;
	private CellBrowser sampleBrowser;
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
		//searchBox = new SearchBox(this);
		//this.add(searchBox);

		model = new SampleViewModel(rootNode);
		rootNode.setChildren("root", new ArrayList<SampleTreeNode>());
		sampleBrowser = new CellBrowser(model, rootNode);
		sampleBrowser.setStylePrimaryName("samplebrowser");
		selectionModel.addSelectionChangeHandler(new Handler()  {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				fireSelectionChange();
			}
		});
		this.add(sampleBrowser);
	}
	
	protected void fireSelectionChange() {
		SampleTreeNode selectedNode = selectionModel.getSelectedObject();
		if (selectedNode != null && selectedNode.getSampleInfo() != null)
			listener.updateSelectedSample( selectedNode.getSampleInfo() );
	}

	/**
	 * Re-load list of samples from the server
	 */
	public void refreshSampleList() {
		  sampleListService.getSampleList(new AsyncCallback<SampleListResult>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error retrieving sample list : "+ caught.getMessage());
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(SampleListResult result) {
				setSampleList(result);
			}
			  
		  });
	}
	
	private List<SampleInfo> treeToList(SampleListResult result) {
		List<SampleInfo> infoList = new ArrayList<SampleInfo>();
		SampleTreeNode root = result.getRootNode();
		Stack<SampleTreeNode> stack = new Stack<SampleTreeNode>();
		stack.push(root);
		while(! stack.isEmpty()) {
			SampleTreeNode node = stack.pop();
			if (node.isLeaf()) {
				infoList.add(node.getSampleInfo());
			}
			else {
				for(SampleTreeNode child : node.getChildren()) {
					stack.push(child);
				}
			}
		}
		return infoList;
	}
	
	/**
	 * Cause the list to display the given samples, ensuring that they are sorted 
	 * by the proper comparator
	 * @param samples
	 */
	private void displaySamples(List<SampleInfo> samples) {
		if (sampleSorter != null) {
			Collections.sort(samples, sampleSorter);
		}
	}
	
	/**
	 * Set the list of all samples that may be displayed in this widget. The currently
	 * displayed samples may be affected by the current filter, if there is one. 
	 * @param allSamples
	 */
	protected void setSampleList(SampleListResult result) {
		this.remove(sampleBrowser);
		this.allSamples = treeToList(result);
		model = new SampleViewModel(rootNode);
		
		rootNode.setChildren("root", result.getRootNode().getChildren());
		
		sampleBrowser = new CellBrowser(model, rootNode);
		sampleBrowser.setAnimationEnabled(true);
		sampleBrowser.setStylePrimaryName("samplebrowser");
		sampleBrowser.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		this.add(sampleBrowser);
		//setFilterText(null); //reset filter, force re-display of all samples
	}
	/**
	 * Renders a single cell in the sample list table. 
	 * @author brendanofallon
	 *
	 */
	static class SampleCell extends AbstractCell<SampleTreeNode> {

		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				SampleTreeNode value, SafeHtmlBuilder sb) {
			
			sb.appendHtmlConstant("<table class=\"sampletableitem\">");
			sb.appendHtmlConstant("<tr><td rowspan='3'>");
			if (value.getSampleInfo() != null) {
				sb.appendHtmlConstant("<img src=\"images/newSampleIcon32.png\" />");	
			}
			else {
				sb.appendHtmlConstant("<img src=\"images/folder32.png\" />");
			}
			
		    sb.appendHtmlConstant("</td>");
			sb.appendHtmlConstant("<td class=\"sampletabletext\">");
			String idStr = "unknown";
			if (value == null) {
				idStr = "root";
			}
			else {
				if (value.getTitle() != null) 
					idStr = value.getTitle();
			}
		    sb.appendEscaped(idStr);
		    sb.appendHtmlConstant("</td></tr><tr><td class=\"sampletabletextA\">");
		    String analysisTypeStr = "unknown";
		    if (value != null && value.isLeaf()) {
		    	SampleInfo info = value.getSampleInfo();
		    	if (info.getAnalysisType() != null)
		    		analysisTypeStr = info.getAnalysisType();
		    	sb.appendEscaped(analysisTypeStr);
		    }
		    else {
		    	sb.appendEscaped("");
		    }
		    sb.appendHtmlConstant("</td></tr></table>");
			
		}
		
	}
	
	static class SampleDateComparator implements Comparator<SampleInfo> {

		@Override
		public int compare(SampleInfo s1, SampleInfo s2) {
			return (int)(s2.getAnalysisDate().getTime() - s1.getAnalysisDate().getTime());
			
		}
		
	}
	
	static class SampleViewModel implements TreeViewModel {
		
		
		
		SampleCell cell = new SampleCell();
		final SampleTreeNode root;
		
		public SampleViewModel(SampleTreeNode rootNode) {
			this.root = rootNode;
		}
		
		
		@Override
		public <T> NodeInfo<?> getNodeInfo(T value) {
			ListDataProvider<SampleTreeNode> sampleList = new ListDataProvider<SampleTreeNode>();
			if (value instanceof SampleTreeNode) {
				SampleTreeNode node = (SampleTreeNode)value;
				if (!node.isLeaf() && node.getChildren() != null) {
					sampleList.getList().addAll(node.getChildren());
				}
			}
			return new DefaultNodeInfo<SampleTreeNode>(sampleList, cell, selectionModel, null);
		}

		@Override
		public boolean isLeaf(Object value) {
			if (value instanceof SampleTreeNode) {
				return ((SampleTreeNode)value).isLeaf();
			}
			return true; //This would be weird
		}
		
		
	}
	
	
	final static SingleSelectionModel<SampleTreeNode> selectionModel = new SingleSelectionModel<SampleTreeNode>();
	SampleListServiceAsync sampleListService = (SampleListServiceAsync) GWT.create(SampleListService.class);
	
}
