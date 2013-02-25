package varviewer.client;

import varviewer.shared.AuthToken;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * A panel with a logout button and username display
 * @author brendan
 *
 */
public class AccountStatus extends HorizontalPanel {

	private HighlightButton logoutButton;
	private Label usernameLabel;
	private final VarViewer mainView;
	
	public AccountStatus(VarViewer view) {
		this.mainView = view;
		initComponents();
	}

	public void setStatus(AuthToken tok) {
		String username = tok.getUsername();
		usernameLabel.setText("User: " + username);
	}
	
	private void initComponents() {
		Image logoutImage = new Image("images/logoutIcon24.png");
		logoutButton = new HighlightButton(logoutImage);
		logoutButton.addClickHandler( new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mainView.logoutCurrentUser();
			}
			
		});
		logoutButton.setTitle("Log out");
		logoutButton.setWidth("24px");
		logoutButton.setHeight("24px");
		
		usernameLabel = new Label("User: no one!");
		usernameLabel.setStylePrimaryName("statuslabel");
		this.add(usernameLabel);
		this.add(logoutButton);
	}
}
