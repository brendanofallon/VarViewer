package varviewer.client;

import varviewer.shared.AuthToken;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * A panel with a logout button and username display
 * @author brendan
 *
 */
public class AccountStatus extends FocusPanel {

	private MenuBar mainMenu;
	private MenuBar menu;
	private HorizontalPanel internalPanel;
	
	public AccountStatus() {
		initComponents();
	}

	public void setStatus(AuthToken tok) {
		String username = tok.getUsername();
		
		if (menu == null) {
			menu = new MenuBar();
			menu.setAutoOpen(true);
			menu.setStylePrimaryName("accountmenu");
			mainMenu.addItem(new MenuItem("Settings", new Command() {

				@Override
				public void execute() {
					//At some point this will link to account settings screen
				}

			}));
			mainMenu.addSeparator();
			MenuItem item = new MenuItem("Log out", new Command() {

				@Override
				public void execute() {
					AuthManager.getAuthManager().doLogout();
				}

			});
			mainMenu.addItem(item);
			menu.addItem(username, mainMenu);

			internalPanel.add(menu);
		}
	}
	
	private void initComponents() {
		this.setStylePrimaryName("accountstatus");
		internalPanel = new HorizontalPanel();
		this.add(internalPanel);
		mainMenu = new MenuBar(true);
	}
}
