package varviewer.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AuthServiceConstants implements Serializable, IsSerializable {
	
	public static final Integer TOKEN_OK = 87623;
	public static final Integer TOKEN_EXPIRED = 251562;
	public static final Integer TOKEN_INVALID = 8728919;

	public AuthServiceConstants() {
		//required no-arg constructor
	}
}
