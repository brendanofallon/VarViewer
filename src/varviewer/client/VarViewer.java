package varviewer.client;

import varviewer.client.sampleView.SamplesView;
import varviewer.client.varTable.VariantDisplay;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Main entry point for application. This contains the main root UI element (mainPanel), and 
 * can display either a VarDisplay or a SampleView element in it. 
 */
public class VarViewer implements EntryPoint {

	//Static reference to VarViewer object 
	static private VarViewer viewer = null;

	//private AuthToken token = null; //This gets set when a user logs in. 
	
	public VarViewer() {
		viewer = this;
	}

	public void onModuleLoad() {
		initComponents();
	}

	/**
	 * Convenient static access to some VarViewer instance
	 * @return
	 */
	public static VarViewer getViewer() {
		return viewer;
	}

	private void initComponents() {
		mainPanel = new FlowPanel();
		mainPanel.setStylePrimaryName("maincontainer");
		Label topLabel = new Label("ARUP NGS Variant Viewer");
		topLabel.setStylePrimaryName("topbar");
		mainPanel.add(topLabel);
		centerPanel = new FlowPanel();
		mainPanel.add(centerPanel);
		Label footer = new Label("VariantViewer, ARUP Labs, Winter 2013");
		footer.setStylePrimaryName("footer");
		mainPanel.add(footer);
		showSampleViewer();
		RootPanel.get().add(mainPanel);		  
	}

	/**
	 * A reference to the VariantDisplay object used to display all variants
	 * @return
	 */
	public VariantDisplay getVarDisplay() {
		return varDisplay;
	}

	/**
	 * Remove all widgets from main panel and add the varDisplay widget to it
	 */
	public void showVariantDisplay() {
		centerPanel.clear();
		centerPanel.add(varDisplay);
	}

	public void showSampleViewer() {
		centerPanel.clear();
		sampleView.refreshSampleList();
		centerPanel.add(sampleView);
	}

//	public void setAuthToken(final AuthToken tok) {
//		//Check to see if the token is valid, then set it
//		
//	}

	FlowPanel mainPanel; //Root container for all UI elements
	FlowPanel centerPanel; //Central container, does not include topbar or footer
	VariantDisplay varDisplay = new VariantDisplay(); //Displays variant table and related panels
	SamplesView sampleView = new SamplesView(this); //UI element that displays sample list	  
	VarListManager varManager = VarListManager.getManager(); //client-side store of current variant data
	//private final CheckAuthTokenServiceAsync authCheckService = GWT.create(CheckAuthTokenService.class);
}

