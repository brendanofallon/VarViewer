package varviewer.client;

import varviewer.shared.AuthToken;

/**
 * These objects listen to login events, which are fired by the AuthManager
 * @author brendan
 *
 */
public interface LoginListener {

	public void onSuccessfulLogin(AuthToken tok);
	
	public void onFailedLogin(AuthToken tok);
	
	public void onLogout(AuthToken tok);
	
}
