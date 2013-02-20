package varviewer.client.services;

import varviewer.shared.AuthToken;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("authenticate")
public interface AuthService extends RemoteService {
	
	AuthToken authenticate(String username, String password);

}
