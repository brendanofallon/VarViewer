package varviewer.client;

import varviewer.shared.AuthToken;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * A panel with a logout button and username display
 * @author brendan
 *
 */
public class AccountStatus extends FocusPanel {

	private HighlightButton logoutButton;
	private Label usernameLabel;
	private final VarViewer mainView;
	
	public AccountStatus(VarViewer view) {
		this.mainView = view;
		initComponents();
	}

	public void setStatus(AuthToken tok) {
		String username = tok.getUsername();
		usernameLabel.setText(username);
	}
	
	private void initComponents() {
		this.setStylePrimaryName("accountstatus");
		final HorizontalPanel internalPanel = new HorizontalPanel();
		this.add(internalPanel);
		
		this.addMouseOverHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				internalPanel.add(logoutButton);
				internalPanel.setCellHorizontalAlignment(logoutButton, HorizontalAlignmentConstant.endOf(Direction.LTR));
			}	
		});
		
		this.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				internalPanel.remove(logoutButton);
			}
		});
		

		
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
		internalPanel.add(usernameLabel);
		internalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	}
}
