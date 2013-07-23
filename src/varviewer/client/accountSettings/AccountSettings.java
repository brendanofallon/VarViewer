package varviewer.client.accountSettings;

import java.util.List;

import varviewer.client.AuthManager;
import varviewer.client.services.AccountDetailsService;
import varviewer.client.services.AccountDetailsServiceAsync;
import varviewer.shared.AccountDetails;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AccountSettings extends VerticalPanel {

	UsernamePanel usernamePanel;
	RolesPanel rolesPanel;
	Button passwordButton; 
	
	public AccountSettings() {
		this.setStylePrimaryName("accountsettingspanel");
		usernamePanel = new UsernamePanel();
		usernamePanel.setStylePrimaryName("accountsettingsitem");
		rolesPanel = new RolesPanel();
		rolesPanel.setStylePrimaryName("accountsettingsitem");
		passwordButton = new Button("Change password");
		passwordButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showPasswordDialog();
			}
			
		});
		passwordButton.getElement().getStyle().setMarginTop(40.0, Unit.PX);
		this.add(usernamePanel);
		this.add(rolesPanel);
		this.add(passwordButton);
	}
	
	protected void showPasswordDialog() {
		PasswordChangeDialog pwDialog = new PasswordChangeDialog();
		pwDialog.show();
	}

	public void loadSettings() {
		final String username = AuthManager.getAuthManager().getLoggedInUsername();
		if (username != null) {
			accountService.getAccountDetails(username, new AsyncCallback<AccountDetails>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Error gathering account info for user: " + username + ", " + caught.getLocalizedMessage());
				}

				@Override
				public void onSuccess(AccountDetails result) {
					populateFields(result);
				}
				
			});
		}
	}
	
	
	protected void populateFields(AccountDetails result) {
		usernamePanel.setUsername(result.getUserName());
		rolesPanel.setRoles(result.getRoles());
	}


	static class UsernamePanel extends HorizontalPanel {
		
		private HTML header;
		private HTML name;
		
		public UsernamePanel() {
			header = new HTML("<b>Account name </b>: ");
			header.setStylePrimaryName("textlabel14");
			name = new HTML("");
			name.setStylePrimaryName("textlabel14-light");
			name.getElement().getStyle().setMarginLeft(10.0, Unit.PX);
			this.add(header);
			this.add(name);
		}
		
		public void setUsername(String username) {
			name.setHTML(username);
		}
	}
	
	static class RolesPanel extends HorizontalPanel {
		
		private HTML header;
		private HTML roles;
		
		public RolesPanel() {
			header = new HTML("<b>Roles </b>: ");
			header.setStylePrimaryName("textlabel14");
			roles = new HTML("");
			roles.setStylePrimaryName("textlabel14-light");
			roles.getElement().getStyle().setMarginLeft(10.0, Unit.PX);
			this.add(header);
			this.add(roles);
		}
		
		public void setRoles(List<String> roleList) {
			String roleStr = "";
			for(String role : roleList) {
				roleStr = roleStr + ", " + role;
			}
			roleStr = roleStr.substring(2);
			roles.setHTML(roleStr);
		}
	}

	AccountDetailsServiceAsync accountService = (AccountDetailsServiceAsync) GWT.create(AccountDetailsService.class);

}
