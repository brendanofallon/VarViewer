package varviewer.client;

import varviewer.client.serviceUI.ServiceUI;
import varviewer.client.serviceUI.ServicesPanel;
import varviewer.client.services.ListServicesService;
import varviewer.client.services.ListServicesServiceAsync;
import varviewer.shared.AuthToken;
import varviewer.shared.services.ServiceDescription;
import varviewer.shared.services.ServiceListResult;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Home implements EntryPoint, LoginListener {

	//Topmost UI element that defines basic layout
	private DockLayoutPanel mainPanel;
	private HighlightButton homeButton;
	private HorizontalPanel topBar;
	private Panel centerPanel;
	private LoginPanel loginPanel;
	private ServiceUI activeService = null;
	private HandlerRegistration homeButtonReg; //Used to add/ remove click handler from home button
	
	@Override
	public void onModuleLoad() {
		initComponents();
		showLoginPanel();
	}

	private void initComponents() {
		topBar = new HorizontalPanel();
		Label topLabel = new Label("ARUP NGS Variant Viewer");
		topBar.setStylePrimaryName("topbar");
		
		homeButton = new HighlightButton(new Image("images/homeIcon.png"));
		homeButton.setTitle("Return to home screen");
		homeButton.setWidth("31px");
		homeButton.setHeight("32px");
		homeButton.setEnabled(false);
		topBar.add(homeButton);
		
		topLabel.setStylePrimaryName("topbarlabel");
		topBar.add(topLabel);
		
		mainPanel = new DockLayoutPanel(Unit.PX);
		mainPanel.addNorth(topBar, 50.0);
		//mainPanel.addWest(new Label("Sidebar"), 60.0);
		centerPanel = new FlowPanel();
		centerPanel.add(new HTML("<b> CENTER!! </b> "));
		mainPanel.add(centerPanel);
		RootLayoutPanel.get().add(mainPanel);
		
		//Register this object to listen to login/ out events
		AuthManager.getAuthManager().addListener(this);
	}


	private void showLoginPanel() {
		clearCenterPanel();
		loginPanel = new LoginPanel();
		centerPanel.add(loginPanel);
	}
	
	/**
	 * Remove all widgets from center panel
	 */
	private void clearCenterPanel() {
		centerPanel.clear();
	}
	
	@Override
	public void onSuccessfulLogin(AuthToken tok) {
		showAvailableServices();
		homeButton.setEnabled(true);
		homeButtonReg = homeButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showAvailableServices();
			}
		});
	}

	protected void showAvailableServices() {
		unloadService();
		final String username = AuthManager.getAuthManager().getLoggedInUsername();
		listServicesService.listServicesForUser(username, new AsyncCallback<ServiceListResult>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failure! " + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(ServiceListResult result) {
				System.out.println("Services OK for user : " + username);
				for(ServiceDescription desc : result.getServices()) {
					System.out.println(desc.getServiceID() + " :" + desc.getServiceUserText() + "\t" + desc.getClassName());
				}
				showServicesPanel(result);
			}
			
		});		
	}
	
	protected void showServicesPanel(ServiceListResult result) {
		clearCenterPanel();
		ServicesPanel servicesPanel = new ServicesPanel(this);
		centerPanel.add(servicesPanel);
		servicesPanel.setServices(result.getServices());	
	}
	
	public void loadService(ServiceUI service) {
		clearCenterPanel();
		service.initialize();
		Widget w = service.getWidget();
		centerPanel.add(w);
		activeService = service;
	}
	
	/**
	 * 
	 */
	public void unloadService() {
		if (activeService != null) {
			activeService.close();
			activeService = null;
			clearCenterPanel();
		}
	}

	@Override
	public void onFailedLogin(AuthToken tok) {
		//Nothing to do
	}
	
	@Override
	public void onLogout(AuthToken tok) {
		showLoginPanel();
		homeButton.setEnabled(false);
		homeButtonReg.removeHandler();
	}

	
	private final ListServicesServiceAsync listServicesService = GWT.create(ListServicesService.class);

}
