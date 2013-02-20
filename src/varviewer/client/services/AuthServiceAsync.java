package varviewer.client.services;

import varviewer.shared.AuthToken;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AuthServiceAsync {
	
	void authenticate(String username, String password, AsyncCallback<AuthToken> callback);

}
