package varviewer.client.services;

import varviewer.shared.services.ServiceListResult;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("listservices")
public interface ListServicesService extends RemoteService {
	
	ServiceListResult listServicesForUser(String username);
	
}
