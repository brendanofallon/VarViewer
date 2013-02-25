package varviewer.client.services;

import varviewer.shared.AuthToken;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Service to force-expire an authentication token
 * @author brendan
 *
 */
@RemoteServiceRelativePath("logout")
public interface LogoutService extends RemoteService {
	void logout(AuthToken token);
}
