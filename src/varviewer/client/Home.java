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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class Home implements EntryPoint, LoginListener {

	//Topmost UI element that defines basic layout
	private DockLayoutPanel mainPanel;
	private Panel centerPanel;
	private LoginPanel loginPanel;
	
	@Override
	public void onModuleLoad() {
		initComponents();
		showLoginPanel();
	}

	private void initComponents() {
		mainPanel = new DockLayoutPanel(Unit.PX);
		mainPanel.addNorth(new Label("North panel"), 50.0);
		mainPanel.addWest(new Label("Sidebar"), 60.0);
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
		final String username = tok.getUsername();
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
		
	}

	@Override
	public void onFailedLogin(AuthToken tok) {
		
	}
	
	@Override
	public void onLogout(AuthToken tok) {
		// TODO Auto-generated method stub
		
	}

	private final ListServicesServiceAsync listServicesService = GWT.create(ListServicesService.class);



}
