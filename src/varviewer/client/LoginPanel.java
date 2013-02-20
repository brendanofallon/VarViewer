package varviewer.client;

import varviewer.client.services.AuthService;
import varviewer.client.services.AuthServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

public class LoginPanel extends FlowPanel {

	private TextBox usernameField;
	private PasswordTextBox passwordField;
	private VarViewer mainView;
	
	public LoginPanel(VarViewer mainView) {
		this.mainView = mainView;
		initComponents();
	}
	
	private void initComponents() {
		this.add(new HTML("<h2>Please log in</h2>"));
		
		HorizontalPanel usernamePanel = new HorizontalPanel();
		usernameField = new TextBox();
		usernameField.setHeight("14px");
		HTML usernameText = new HTML("<b>User name:<b>"); 
		usernameText.addStyleName("vertaligncenter");
		usernameText.setWidth("100px");
		usernamePanel.add(usernameText);
		usernamePanel.add(usernameField);
		this.add(usernamePanel);
		
		passwordField = new PasswordTextBox();
		passwordField.setHeight("14px");
		HorizontalPanel passwdPanel = new HorizontalPanel();
		HTML passwdText = new HTML("<b>Password:</b>");
		passwdText.addStyleName("vertaligncenter");
		passwdText.setWidth("100px");
		passwdPanel.add(passwdText);
		passwdPanel.add(passwordField);
		
		this.add(passwdPanel);
		
		Button goButton = new Button("Log in");
		goButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				tryLogin(usernameField.getText(), passwordField.getText());
			}
		});
		goButton.setFocus(true);
		goButton.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				
			}
		});
		this.add(goButton);
	}

	protected void tryLogin(final String username, final String password) {
		
//		authService.authenticate(username, password, new AsyncCallback<AuthToken>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				Window.alert("Server failure when authenticating user: " + username);	
//			}
//
//			@Override
//			public void onSuccess(AuthToken token) {
//				if (token != null) {
//					mainView.setAuthToken(token);
//					mainView.showSampleViewer();
//				}
//				else {
//					//this.add(accessDeniedLabel);
//				}
//			}
//			
//		});
	}
	
	
	private HTML accessDeniedLabel = new HTML("<b>Incorrect username / password, please try again</b>");
	private final AuthServiceAsync authService = GWT.create(AuthService.class);
}
