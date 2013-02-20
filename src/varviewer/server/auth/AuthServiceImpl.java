package varviewer.server.auth;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import varviewer.client.services.AuthService;
import varviewer.shared.AuthToken;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


/**
 * Implementation of authentication, check username and passwd against db 
 * @author brendan
 *
 */
public class AuthServiceImpl extends RemoteServiceServlet implements AuthService {

	@Override
	public AuthToken authenticate(String username, String password) {
	
		HttpServletRequest req = getThreadLocalRequest();
		Logger.getLogger(PasswordStore.class).info("Authentication attempt for " + username + " from addr:" + req.getRemoteAddr() + " host:" + req.getRemoteHost());

		boolean authOK = PasswordStore.checkPassword(username, password);
		
		if (authOK) {
			Logger.getLogger(PasswordStore.class).info("User " + username + " authenticated successfully");
			AuthToken token = new AuthToken();
			token.setUsername(username);
			token.setStartTime(System.currentTimeMillis());	
			return token;
		}
		
		Logger.getLogger(PasswordStore.class).info("User " + username + " access denied");
		return null;
	}

}
