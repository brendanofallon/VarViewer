package varviewer.server.auth;

import varviewer.client.services.CheckAuthTokenService;
import varviewer.shared.AuthToken;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CheckAuthTokenServiceImpl extends RemoteServiceServlet implements CheckAuthTokenService {

	public static final Integer TOKEN_OK = 0;
	public static final Integer TOKEN_EXPIRED = 1;
	public static final Integer TOKEN_INVALID = 2;
	
	@Override
	public Integer checkToken(AuthToken token) {
		return TOKEN_OK;
	}

}
