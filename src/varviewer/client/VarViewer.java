package varviewer.client;

import varviewer.client.sampleView.SamplesView;
import varviewer.client.services.CheckAuthTokenService;
import varviewer.client.services.CheckAuthTokenServiceAsync;
import varviewer.client.services.LogoutService;
import varviewer.client.services.LogoutServiceAsync;
import varviewer.client.varTable.VariantDisplay;
import varviewer.shared.AuthServiceConstants;
import varviewer.shared.AuthToken;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Main entry point for application. This contains the main root UI element (mainPanel), and 
 * can display either a VarDisplay or a SampleView element in it. 
 */
public class VarViewer implements EntryPoint {

	//Static reference to VarViewer object 
	static private VarViewer viewer = null;
	private AuthToken token = null; //This gets set when a user logs in. 
	
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
		Label footer = new Label("VariantViewer, ARUP Labs, Winter 2013");
		footer.setStylePrimaryName("footer");
		mainPanel.add(footer);
		showLoginPanel();
		RootPanel.get().add(mainPanel);
		RootPanel.getBodyElement().setId("maincontainer");
	}

	private void showLoginPanel() {
		centerPanel.clear();
		LoginPanel loginPanel = new LoginPanel(this);
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
		if (token != null) {
			statusPanel.setStatus(token);
			centerPanel.clear();
			centerPanel.add(varDisplay);
		}
	}

	public void showSampleViewer() {
		if (token != null) {
			statusPanel.setStatus(token);
			centerPanel.clear();
			sampleView.refreshSampleList();
			centerPanel.add(sampleView);	
		}
	}
	
	public void checkTokenShowViewer(final AuthToken tok) {
		if (tok != null) {
			//Check to see if the token is valid. If so, show the sample list panel
			authCheckService.checkToken(tok, new AsyncCallback<Integer>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Error checking authentication token: " + caught.getLocalizedMessage());
				}

				@Override
				public void onSuccess(Integer result) {
					if (result.equals(AuthServiceConstants.TOKEN_OK)) {
						setToken(tok);
						centerPanel.clear();
						sampleView.refreshSampleList();
						centerPanel.add(sampleView);
					}
					if (result.equals(AuthServiceConstants.TOKEN_EXPIRED)) {
						Window.alert("Session has expired, please log in again");
					}
					if (result.equals(AuthServiceConstants.TOKEN_INVALID)) {
						Window.alert("Invalid authentication token");
					}
				}
			});
						
		}
	}

	public void logoutCurrentUser() {
		if (token != null) {
			logoutService.logout(token, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Could not log out user: " + token.getUsername() + ", " + caught.getLocalizedMessage());
				}

				@Override
				public void onSuccess(Void result) {
					showLoginPanel();
				}
				
			});
		}
	}
	
	/**
	 * Check the validity of the given AuthToken using the CheckAuthTokenService, if
	 * the result is TOKEN_OK then set the AuthToken for this VarViewer to the given token 
	 * @param tok
	 */
	public void checkSetAuthToken(final AuthToken tok) {
		if (tok == null) {
			Window.alert("Error checking authentication token: Token is null");
			return;
		}
		
		//Check to see if the token is valid, then set it
		authCheckService.checkToken(tok, new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error checking authentication token: " + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Integer result) {
				if (result == AuthServiceConstants.TOKEN_OK) {
					setToken(tok);
				}
				if (result == AuthServiceConstants.TOKEN_EXPIRED) {
					Window.alert("Session has expired, please log in again");
				}
				if (result == AuthServiceConstants.TOKEN_INVALID) {
					Window.alert("Invalid authentication token");
				}
			}
		});
	}

	
	
	protected final void setToken(AuthToken tok) {
		this.token = tok;
		statusPanel = new AccountStatus(this);
		statusPanel.setStatus(tok);
		topBar.add(statusPanel);
		//topBar.setCellHorizontalAlignment(statusPanel, HorizontalAlignmentConstant.endOf(Direction.LTR));
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

