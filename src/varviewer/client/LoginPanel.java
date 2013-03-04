package varviewer.client;

import varviewer.client.services.AuthService;
import varviewer.client.services.AuthServiceAsync;
import varviewer.shared.AuthToken;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginPanel extends VerticalPanel {

	private TextBox usernameField;
	private PasswordTextBox passwordField;
	private VarViewer mainView;
	
	public LoginPanel(VarViewer mainView) {
		this.mainView = mainView;
		initComponents();
	}
	
	private void initComponents() {
		this.setStylePrimaryName("loginpanel");
		
		usernameField = new TextBox();
		usernameField.setStylePrimaryName("usernamefield");
		this.add(usernameField);
		
		passwordField = new PasswordTextBox();
		passwordField.setStylePrimaryName("passwordfield");
		this.add(passwordField);
		passwordField.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					tryLogin(usernameField.getText(), passwordField.getText());
				}
			}
		});
		
		Button goButton = new Button("Log in");
		goButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				tryLogin(usernameField.getText(), passwordField.getText());
			}
		});
		goButton.setFocus(true);
		goButton.setStylePrimaryName("loginbutton");
		
		this.add(goButton);
		
		this.setHeight("500px");
	}

	protected void tryLogin(final String username, final String password) {
		
		authService.authenticate(username, password, new AsyncCallback<AuthToken>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Server failure when authenticating user: " + username);	
			}

			@Override
			public void onSuccess(AuthToken token) {
				if (token != null) {
					mainView.checkTokenShowViewer(token);
					
				}
				else {
					showAccessDeniedLabel();
				}
			}
			
		});
	}
	
	
	protected void showAccessDeniedLabel() {
		Window.alert("Invalid username or password, please try again");
	}

	//private HTML accessDeniedLabel = new HTML("<b>Incorrect username / password, please try again</b>");
	private final AuthServiceAsync authService = GWT.create(AuthService.class);
}
