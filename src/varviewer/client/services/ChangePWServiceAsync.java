package varviewer.client.services;

import varviewer.shared.ChangePWResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ChangePWServiceAsync {

	void changePassword(String username, String oldPassword, String newPassword, AsyncCallback<ChangePWResult> res);
	
}
