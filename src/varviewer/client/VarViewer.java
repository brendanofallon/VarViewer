package varviewer.client;

import varviewer.client.serviceUI.SampleViewUI;
import varviewer.shared.AuthToken;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Main entry point for application. This contains the main root UI element (mainPanel), and 
 * can display either a VarDisplay or a SampleView element in it. 
 */
public class VarViewer implements EntryPoint, LoginListener {
	
	public VarViewer() {
		//nothing to do...
	}

	public void onModuleLoad() {
		initComponents();
		showLoginPanel();
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
		Label footer = new Label("VariantViewer, ARUP Labs, version 1.3.1 August 23, 2013");
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
	
	
	@Override
	public void onSuccessfulLogin(AuthToken tok) {
		if (statusPanel == null) {
			statusPanel = new AccountStatus();
			topBar.add(statusPanel);
			statusPanel.setStatus(tok);
		}
		
		centerPanel.clear();
		if (sampleView == null) {
			sampleView = new SampleViewUI();
			sampleView.initialize();
		}
		centerPanel.add(sampleView.getWidget());
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
	SampleViewUI sampleView = null;

}

