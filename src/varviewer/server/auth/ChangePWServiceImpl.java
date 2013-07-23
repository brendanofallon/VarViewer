package varviewer.server.auth;

import varviewer.client.services.ChangePWService;
import varviewer.shared.ChangePWResult;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ChangePWServiceImpl extends RemoteServiceServlet implements ChangePWService {

	@Override
	public ChangePWResult changePassword(String username, String oldpassword,
			String newpassword) {
		
		ChangePWResult res = new ChangePWResult();
		
		
		
		return res;
	}

}
