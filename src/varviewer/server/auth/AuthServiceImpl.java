package varviewer.server.auth;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import varviewer.client.services.AuthService;
import varviewer.server.appContext.SpringContext;
import varviewer.shared.AuthToken;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;



/**
 * Implementation of authentication. Obatains authManager from spring configuration
 * and uses it to attempt authentication. On success calls
 *   SecurityContextHolder.getContext().setAuthentication(auth)
 * to set the authentication context. 
 *  
 * @author brendan
 *
 */
public class AuthServiceImpl extends RemoteServiceServlet implements AuthService {

	@Override
	public AuthToken authenticate(String username, String password) {
	
		HttpServletRequest req = getThreadLocalRequest();
		Logger.getLogger(AuthServiceImpl.class).info("Authentication attempt for " + username + " from addr:" + req.getRemoteAddr() + " host:" + req.getRemoteHost());

		
		try {
			ApplicationContext context = SpringContext.getContext(); 
			AuthenticationManager manager = (AuthenticationManager) context.getBean("authManager");
	        Authentication request = new UsernamePasswordAuthenticationToken(username, password);
	        Authentication result = manager.authenticate(request);
	        SecurityContextHolder.getContext().setAuthentication(result);
	        
	      } catch(AuthenticationException e) {
	        Logger.getLogger(AuthServiceImpl.class).info("Authentication failed for user " + username + " cause: " + e.getLocalizedMessage());
	        return null;
	      }
		
		
		if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
			Logger.getLogger(AuthServiceImpl.class).info("User " + username + " authenticated successfully");
			AuthToken token = new AuthToken();
			token.setUsername(username);
			token.setStartTime(System.currentTimeMillis());	
			boolean success = ActiveUsers.getActiveUsers().logInUser(token);
			if (! success) {
				Logger.getLogger(AuthServiceImpl.class).warn("User " + username + " is already logged in, not adding user to active users list?");
			}
			return token;
		}
				
		
		Logger.getLogger(AuthServiceImpl.class).info("User " + username + " access denied");
		return null;
	}

}
