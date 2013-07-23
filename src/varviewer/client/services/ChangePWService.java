package varviewer.client.services;

import varviewer.shared.ChangePWResult;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("authenticate")
public interface ChangePWService extends RemoteService {

	ChangePWResult changePassword(String username, String oldpassword, String newpassword);
	
	
}
