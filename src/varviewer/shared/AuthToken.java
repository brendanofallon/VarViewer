package varviewer.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * These are returned as the result of an authentication attempt and are stored on the client in the AuthManager
 * so we can remember who's logged in. 
 * @author brendan
 *
 */
public class AuthToken implements Serializable, IsSerializable {
	
	private String username = null;
	private Long startTime = null;
	
	public AuthToken() {
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		if (this.username != null)
			throw new IllegalArgumentException("Username already set for this token");
		this.username = username;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		if (this.startTime != null)
			throw new IllegalArgumentException("Start time already set for this token");
		this.startTime = startTime;
	}


}
