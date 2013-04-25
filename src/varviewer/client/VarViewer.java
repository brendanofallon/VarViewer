package varviewer.client;

import varviewer.client.sampleView.SamplesView;
import varviewer.client.services.CheckAuthTokenService;
import varviewer.client.services.CheckAuthTokenServiceAsync;
import varviewer.client.services.LogoutService;
import varviewer.client.services.LogoutServiceAsync;
import varviewer.client.varTable.VariantDisplay;
import varviewer.shared.AuthToken;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Main entry point for application. This contains the main root UI element (mainPanel), and 
 * can display either a VarDisplay or a SampleView element in it. 
 */
public class VarViewer implements EntryPoint, LoginListener {

	//Static reference to VarViewer object 
	static private VarViewer viewer = null;
	
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
		topBar = new HorizontalPanel();
		Label topLabel = new Label("ARUP NGS Variant Viewer");
		topBar.setStylePrimaryName("topbar");
		topLabel.setStylePrimaryName("topbarlabel");
		topBar.add(topLabel);
				
		mainPanel.add(topBar);
		centerPanel = new FlowPanel();
		mainPanel.add(centerPanel);
		Label footer = new Label("VariantViewer, ARUP Labs, version 1.2 April 23 2013");
		footer.setStylePrimaryName("footer");
		mainPanel.add(footer);
		showLoginPanel();
		RootPanel.get().add(mainPanel);
		RootPanel.getBodyElement().setId("maincontainer");
		
		//We want to be notified of login events
		AuthManager.getAuthManager().addListener(this);
	}

	private void showLoginPanel() {
		centerPanel.clear();
		LoginPanel loginPanel = new LoginPanel();
		centerPanel.add(loginPanel);
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
		statusPanel.setStatus(AuthManager.getAuthManager().getToken());
		centerPanel.clear();
		centerPanel.add(varDisplay);
	}

	public void showSampleViewer() {
		statusPanel.setStatus(AuthManager.getAuthManager().getToken());
		centerPanel.clear();
		sampleView.refreshSampleList();
		centerPanel.add(sampleView);	
	}
	
	@Override
	public void onSuccessfulLogin(AuthToken tok) {
		if (statusPanel == null) {
			statusPanel = new AccountStatus();
			topBar.add(statusPanel);
			statusPanel.setStatus(tok);
		}
		
		centerPanel.clear();
		sampleView.refreshSampleList();
		centerPanel.add(sampleView);
	}

	@Override
	public void onFailedLogin(AuthToken tok) {
		//nothing to do, LoginPanel handles auth failed messages
	}
	
	@Override
	public void onLogout(AuthToken tok) {
		if (statusPanel != null) {
			topBar.remove(statusPanel);
		}
		showLoginPanel();
	}
	
	HorizontalPanel topBar; // Gradient bar across top showing title and account status label
	AccountStatus statusPanel;
	FlowPanel mainPanel; //Root container for all UI elements
	FlowPanel centerPanel; //Central container, does not include topbar or footer
	VariantDisplay varDisplay = new VariantDisplay(); //Displays variant table and related panels
	SamplesView sampleView = new SamplesView(this); //UI element that displays sample list	  
	private final LogoutServiceAsync logoutService = GWT.create(LogoutService.class);
	private final CheckAuthTokenServiceAsync authCheckService = GWT.create(CheckAuthTokenService.class);




}

