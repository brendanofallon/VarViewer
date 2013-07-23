package varviewer.client.serviceUI;

import varviewer.client.accountSettings.AccountSettings;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * A tool for users to view info about their account and change their password
 * @author brendan
 *
 */
public class AccountSettingsUI implements ServiceUI {
	
	AccountSettings settingsPanel = null;
	
	@Override
	public void initialize() {
		settingsPanel = new AccountSettings();
		settingsPanel.loadSettings();	
	}

	@Override
	public Widget getWidget() {
		return settingsPanel;
	}

	@Override
	public void close() {
		settingsPanel = null;
	}

	@Override
	public Image getIcon() {
		return image;
	}
	
	private static Image image = new Image("images/accountSettings64.png");

}
