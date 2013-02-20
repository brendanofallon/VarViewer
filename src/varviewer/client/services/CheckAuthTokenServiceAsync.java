package varviewer.client.services;

import varviewer.shared.AuthToken;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CheckAuthTokenServiceAsync {

	void checkToken(AuthToken token, AsyncCallback<Integer> cb);
}
