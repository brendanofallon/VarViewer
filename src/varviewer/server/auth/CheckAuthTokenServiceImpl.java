package varviewer.server.auth;

import varviewer.client.services.CheckAuthTokenService;
import varviewer.shared.AuthServiceConstants;
import varviewer.shared.AuthToken;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CheckAuthTokenServiceImpl extends RemoteServiceServlet implements CheckAuthTokenService {
	
	@Override
	public Integer checkToken(AuthToken token) {
		if (ActiveUsers.getActiveUsers().isUserLoggedIn(token.getUsername())) {
			return AuthServiceConstants.TOKEN_OK;
		}
		return AuthServiceConstants.TOKEN_EXPIRED;
	}

}
