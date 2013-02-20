package varviewer.server.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import varviewer.shared.AuthToken;

/**
 * A static singleton that keeps track of all logged in users (and when they logged in)
 * @author brendan
 *
 */
public class ActiveUsers {
	
	private Map<String, AuthToken> users = new HashMap<String, AuthToken>();
	private static ActiveUsers activeUsers = null;
	private long timeOutSeconds = 3600l;
	
	private ActiveUsers() {
		//Private constructor to enforce singleton status
		Logger.getLogger(ActiveUsers.class).info("Initializing active users object");
	}
	
	public static ActiveUsers getActiveUsers() {
		if (activeUsers == null) {
			activeUsers = new ActiveUsers();
		}
		
		return activeUsers;
	}
	
	public boolean isUserLoggedIn(String username) {
		removeExpiredUsers();
		return users.get(username) != null;
	}
	
	/**
	 * Static access to query if a user is logged in
	 * @param username
	 * @return
	 */
	public static boolean isLoggedIn(String username) {
		return getActiveUsers().isUserLoggedIn(username);
	}
	
	/**
	 * Scan users map and remove from it all users who have expired tokens
	 */
	private void removeExpiredUsers() {
		List<String> userList = new ArrayList<String>();
		userList.addAll( users.keySet() );
		for(String user : userList) {
			AuthToken tok = users.get(user);
			if (tok == null) {
				users.remove(user);
				break;
			}
			Long loginTime = tok.getStartTime();
			if (loginTime == null) {
				users.remove(user);
				break;
			}
			
			long secondsSinceLogin = (System.currentTimeMillis() - loginTime)/1000;
			if (secondsSinceLogin > timeOutSeconds) {
				users.remove(user);
				break;
			}
		}
	}
}
