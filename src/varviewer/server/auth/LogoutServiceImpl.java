package varviewer.server.auth;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import varviewer.client.services.LogoutService;
import varviewer.shared.AuthToken;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LogoutServiceImpl extends RemoteServiceServlet implements LogoutService {

	@Override
	public void logout(AuthToken token) {
		
		HttpServletRequest req = getThreadLocalRequest();
		Logger.getLogger(LogoutServiceImpl.class).warn("Attempting to log out user " + token.getUsername() + " from addr:" + req.getRemoteAddr() + " host:" + req.getRemoteHost() );
		
		boolean loggedOut = ActiveUsers.getActiveUsers().logOutUser(token);
		if (! loggedOut) {
			Logger.getLogger(LogoutServiceImpl.class).warn("User " + token.getUsername() + " could not be logged out (user is probably not logged in)");
		}
	}

}
