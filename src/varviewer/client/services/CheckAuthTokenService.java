package varviewer.client.services;

import varviewer.shared.AuthToken;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("checktoken")
public interface CheckAuthTokenService  extends RemoteService {
	
	Integer checkToken(AuthToken token);


}
