package varviewer.client.services;

import varviewer.shared.AccountDetails;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AccountDetailsServiceAsync {

	void getAccountDetails(String username, AsyncCallback<AccountDetails> result);
	
}
