package varviewer.client;

import varviewer.shared.AuthToken;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginPanel extends VerticalPanel implements LoginListener {

	private TextBox usernameField;
	private PasswordTextBox passwordField;
	
	public LoginPanel() {
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
		
		AuthManager.getAuthManager().addListener(this);
	}

	protected void tryLogin(final String username, final String password) {
		AuthManager.getAuthManager().tryLogin(username, password);
	}
	
	
	protected void showAccessDeniedLabel() {
		Window.alert("Invalid username or password, please try again");
	}

	@Override
	public void onSuccessfulLogin(AuthToken tok) {
		//Do nothing
	}

	@Override
	public void onFailedLogin(AuthToken tok) {
		Window.alert("Username / password not recognized, please try again.");
	}

	@Override
	public void onLogout(AuthToken tok) {
		
	}

	//private HTML accessDeniedLabel = new HTML("<b>Incorrect username / password, please try again</b>");
	
}
