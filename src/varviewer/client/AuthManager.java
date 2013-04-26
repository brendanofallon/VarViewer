package varviewer.client;

import java.util.ArrayList;
import java.util.List;

import varviewer.client.services.AuthService;
import varviewer.client.services.AuthServiceAsync;
import varviewer.client.services.LogoutService;
import varviewer.client.services.LogoutServiceAsync;
import varviewer.shared.AuthToken;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Singleton class that keeps track of who's logged in and manages calls to the authService 
 * and logoutService. LoginListeners can be added / removed and are informed on login / logout events. 
 * @author brendan
 *
 */
public class AuthManager {

	private final LogoutServiceAsync logoutService = GWT.create(LogoutService.class);
	private final AuthServiceAsync authService = GWT.create(AuthService.class);
	private AuthToken token = null;
	private static AuthManager manager = null; //Created on first call to getAuthManager()
	private List<LoginListener> listeners = new ArrayList<LoginListener>();
	
	private AuthManager() {
		//private constructor, use getAuthManer()
	}
	
	public static AuthManager getAuthManager() {
		if (manager == null) {
			manager = new AuthManager();
		}
		
		return manager;
	}
	
	public AuthToken getToken() {
		return token;
	}
	
	/**
	 * Null if there is no one logged in, otherwise returns username
	 * @return
	 */
	public String getLoggedInUsername() {
		if (token == null) {
			return null;
		}
		else {
			return token.getUsername();
		}
	}
	
	/**
	 * Issue RPC call to the server to try to log in a user with given name and password.
	 * On success .onSuccessfulLogin() will be called on all listeners, otherwise onLoginFailed()
	 * will be called 
	 * @param username
	 * @param password
	 */
	public void tryLogin(final String username, String password) {
		if (token != null) {
			if (! token.getUsername().equals(username)) {
				
			}
		}
		
		authService.authenticate(username, password, new AsyncCallback<AuthToken>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Server failure when authenticating user: " + username);	
			}

			@Override
			public void onSuccess(AuthToken token) {
				if (token != null) {
					onLoginSuccess(token);
				}
				else {
					onLoginFailed();
				}
			}

		});
	}
	
	/**
	 * Issue RPC call to server to logout current user. Does nothing if no one is logged in. 
	 * If logout is successful onLogout() is called on all listeners. 
	 */
	public void doLogout() {
		//No one logged in, do nothing
		if (token == null) {
			return;
		}
		
		logoutService.logout(token, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not log out user: " + token.getUsername() + ", " + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Void result) {
				onLogoutSuccess();
			}
			
		});
	}
	
	public void addListener(LoginListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(LoginListener listener) {
		listeners.remove(listener);
	}
	
	public void fireLoginSuccess() {
		for(LoginListener listener : listeners) {
			listener.onSuccessfulLogin(token);
		}
	}
	
	public void fireLoginFailed() {
		for(LoginListener listener : listeners) {
			listener.onFailedLogin(token);
		}
	}
	
	public void fireLogout(AuthToken tok) {
		for(LoginListener listener : listeners) {
			listener.onLogout(tok);
		}
	}
	
	private void onLoginSuccess(AuthToken tok) {
		this.token = tok;
		fireLoginSuccess();
	}
	
	private void onLoginFailed() {
		this.token = null;
		fireLoginFailed();
	}
	
	private void onLogoutSuccess() {
		AuthToken tok = this.token;
		token = null;
		fireLogout(tok);
	}
}
