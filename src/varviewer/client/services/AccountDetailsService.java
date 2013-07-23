package varviewer.client.services;

import varviewer.shared.AccountDetails;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("accountdetails")
public interface AccountDetailsService extends RemoteService {

	AccountDetails getAccountDetails(String username);
}
