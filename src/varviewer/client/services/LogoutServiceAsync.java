package varviewer.client.services;

import varviewer.shared.AuthToken;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LogoutServiceAsync {
	void logout(AuthToken tok, AsyncCallback<Void> cb);
}
